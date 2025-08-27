package com.trackstudio.kernel.cache;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.trackstudio.app.UdfValue;
import com.trackstudio.app.udf.GenericValue;
import com.trackstudio.app.udf.ListMultiValue;
import com.trackstudio.app.udf.ListValue;
import com.trackstudio.constants.UdfConstants;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.model.Prstatus;
import com.trackstudio.tools.Intern;
import com.trackstudio.tools.Null;
import com.trackstudio.tools.Pair;
import com.trackstudio.tools.formatter.DateFormatter;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

/**
 * Класс для хренения данных о пользователе
 */
@ThreadSafe
public class UserRelatedInfo implements Comparable {
    public static final String ANONYMOUS_USER = "anonymous";
    private final ReentrantReadWriteLock udfLock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock emergencyNoticeDateLock = new ReentrantReadWriteLock();

    @GuardedBy("udfLock")
    private CopyOnWriteArrayList<UDFCacheItem> UDFs; // collection of UDFCacheItem of all udfs and thir values

    @GuardedBy("udfLock")
    private CopyOnWriteArrayList<UdfValue> UDFValues; // collection of UDFValue

    private AtomicReference<ConcurrentMap> acl;
    private final AtomicInteger childCount;
    private final String id;
    private final String login;
    private final String name;
    private final String password;
    private final String preferences;
    private final String tel;
    private final String email;
    private final boolean enabled;
    private final String locale;
    private final String timezone;
    private final String company;
    private final Integer childAllowed;
    private final Calendar expireDate;
    private final Calendar lastLogonDate;
    private final Calendar passwordChangedDate;
    private final String prstatusId;
    private final String parentId;
    private final String template;
    private final String defaultProjectId;
    private final AtomicReference<String> emergencyNotice;
    @GuardedBy("emergencyNoticeDateLock")
    private Calendar emergencyNoticeDate;
    private final ThreadLocal<DateFormatter> dateFormatter;

    /**
     * Разделитель
     */
    public static final String delimiter = ",";

    /**
     * Возвращает Форматтер даты
     *
     * @return форматтер даты
     * @throws GranException при необходимости
     * @see com.trackstudio.tools.formatter.DateFormatter
     */
    public DateFormatter getDateFormatter() throws GranException {
        if (dateFormatter.get() == null)
            dateFormatter.set(new DateFormatter(timezone, locale));
        return dateFormatter.get();
    }

    /**
     * Конструктор
     *
     * @param id ID пользователя
     */
    UserRelatedInfo(String id) {
        this(id,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null);
    }

    /**
     * Конструктор
     *
     * @param id                  ID пользователя
     * @param login               ЛОгин
     * @param password            Пароль
     * @param name                Имя
     * @param tel                 телефон
     * @param email               электронная почта
     * @param enabled             активность пользователя
     * @param locale              локаль
     * @param timezone            таймзона
     * @param company             компания
     * @param childAllowed        доступные потомки
     * @param expireDate          дата истекания пользователя
     * @param prstatusId          ID статуса
     * @param managerId           ID родительского пользователя
     * @param template            Шаблон
     * @param defaultProjectId    Проект по умолчанию
     * @param lastLogonDate       Дата последнего логина
     * @param passwordChangedDate дата последнего изменения пароля
     * @param preferences         настройки
     */
    // Intern not used here because method called from allowedByACL and too slow
    public UserRelatedInfo(String id, String login, String password, String name, String tel, String email, Integer enabled, String locale, String timezone, String company, Integer childAllowed, Calendar expireDate, String prstatusId, String managerId, String template, String defaultProjectId, Calendar lastLogonDate, Calendar passwordChangedDate, String preferences) {
        this.id = id;
        this.childCount = new AtomicInteger(0);
        this.login = login;
        this.password = password;
        this.name = name;
        this.tel = tel;
        this.email = email;
        this.enabled = enabled != null && enabled == 1;
        this.locale = locale;
        this.timezone = timezone;
        this.company = company;
        this.childAllowed = childAllowed;
        if (expireDate != null)
            this.expireDate = (Calendar)expireDate.clone();
        else
            this.expireDate = null;
        this.prstatusId = prstatusId;
        this.parentId = managerId;
        this.template = template;
        this.defaultProjectId = defaultProjectId;
        if (lastLogonDate != null)
            this.lastLogonDate = (Calendar)lastLogonDate.clone();
        else
            this.lastLogonDate = null;
        if (passwordChangedDate != null)
            this.passwordChangedDate = (Calendar)passwordChangedDate.clone();
        else
            this.passwordChangedDate = null;
        this.preferences = preferences;
        this.emergencyNotice = new AtomicReference("");
        this.emergencyNoticeDate=null;
        this.acl = new AtomicReference<ConcurrentMap>(new ConcurrentHashMap());
        this.dateFormatter = new ThreadLocal<DateFormatter>();
    }

    /**
     * Возвращает ID пользвателя
     *
     * @return ID пользователя
     */
    public String getId() {
        return id;
    }

    /**
     * Сравнивает два объекта текущего класса
     *
     * @param obj Сравниваемый объект
     * @return TRUE - объекты равны, FALSE - нет
     */
    public boolean equals(Object obj) {
        return obj instanceof UserRelatedInfo && getId().equals(((UserRelatedInfo) obj).getId());
    }

    /**
     * Возвращает строковое представление объекта
     *
     * @return строковое представление
     */
    @Override
    public String toString() {
        return "UserRelatedInfo{" +
                "id='" + id + '\'' +
                ", login='" + login + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", prstatusId='" + prstatusId + '\'' +
                '}';
    }

    /**
     * Сравнивает два объекта текущего класса
     *
     * @param o сравниваемый объект
     * @return +1, 0, -1
     */
    public int compareTo(Object o) {
        return id.compareTo(((UserRelatedInfo) o).id);
    }

    /**
     * Возвращает даты истекания срока годности пользователя
     *
     * @return дата
     */
    public Calendar getExpireDate() {
        if (expireDate==null)
            return null;
        else
            return (Calendar)expireDate.clone();
    }

    /**
     * Истек ли срок годности
     *
     * @return TRUE - истек, FALSE - нет
     */
    public boolean isExpired() {
        return expireDate!=null && expireDate.before(Calendar.getInstance());
    }

    /**
     * ВОзвращает активный пользователь или нет
     *
     * @return активный пользователь или нет
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Возвращает число дочерних пользователей
     *
     * @return число дочерних пользователей
     */
    public int getChildCount() {
        return childCount.intValue();
    }

    /**
     * Устанавливает число дочерних пользователей
     *
     * @param childCount число дочерних пользователей
     */
    public void setChildCount(int childCount) {
        this.childCount.set(childCount);
    }

    /**
     * Возвращает число доступных пользователей
     *
     * @return число доступных пользователей
     */
    public Integer getChildAllowed() {
        return childAllowed;
    }

    /**
     * Возвращает карту
     *
     * @return карта
     */
    public ConcurrentHashMap getAcl() {
        return new ConcurrentHashMap(Null.removeNullElementsFromMap(acl.get()));
    }

    /**
     * Возвращает логин
     *
     * @return логин
     */
    public String getLogin() {
        return login;
    }

    /**
     * Возвращает имя пользователя
     *
     * @return имя пользователя
     */
    public String getName() {
        return name;
    }


    /**
     * Возвращают пароль пользователя
     *
     * @return пароль пользователя
     */
    public String getPassword() {
        return password;
    }

    /**
     * Возвращает телефон
     *
     * @return телефон
     */
    public String getTel() {
        return tel;
    }


    /**
     * Возвращает электронную почту
     *
     * @return электронная почта
     */
    public String getEmail() {
        return email;
    }


    /**
     * Возвращает локаль пользователя
     *
     * @return локаль пользователя
     */
    public String getLocale() {
        return locale;
    }

    /**
     * Возвращает таймзону пользователя
     *
     * @return таймзона
     */
    public String getTimezone() {
        return timezone;
    }

    /**
     * Возвращает компанию
     *
     * @return компания
     */
    public String getCompany() {
        return company;
    }

    /**
     * Возвращает даты последнего логина пользователя
     *
     * @return дата
     */
    public Calendar getLastLogonDate() {
        if (lastLogonDate == null)
            return null;
        else
            return (Calendar)lastLogonDate.clone();
    }

    /**
     * Возвращает дату последнего изменения пароля
     *
     * @return дата
     */
    public Calendar getPasswordChangedDate() {
        if (passwordChangedDate == null)
            return null;
        else
            return (Calendar)passwordChangedDate.clone();
    }

    /**
     * Возвращает ID статуса
     *
     * @return ID статуса
     */
    public String getPrstatusId() {
        return prstatusId;
    }

    /**
     * Возвращает ID родительского пользователя
     *
     * @return ID родительского пользователя
     */

    public String getParentId() {
        return parentId;
    }


    /**
     * Возвращает шаблон
     *
     * @return шаблон
     */
    public String getTemplate() {
        return template;
    }

    /**
     * Возвращает проект по умолчанию
     *
     * @return проект по умолчанию
     */
    public String getDefaultProjectId() {
        return defaultProjectId;
    }

    /**
     * Возвращает карту правил доступа только для чтения
     *
     * @return карта правил доступа
     */
    public Map<String, TreeSet<InternalACL>> getReadOnlyAcl() {
        return Collections.unmodifiableMap(acl.get());
    }

    /**
     * Устанавливает карту правил доступа
     *
     * @param acl карта правил доступа
     */
    public void setAcl(ConcurrentMap acl) {
        this.acl.set(new ConcurrentHashMap(Null.removeNullElementsFromMap(acl)));
    }

    /**
     * Добавляет правило доступа
     *
     * @param iacl правило доступа
     */
    public void addAcl(InternalACL iacl) {
        UserRelatedManager.addAcl(acl.get(), iacl);
    }


    /**
     * Возвращает дочерних пользователей
     *
     * @return список пользователей
     * @throws GranException при необходимости
     */
    public ArrayList<String> getChildren() throws GranException {
            return UserRelatedManager.getInstance().getChildren(getId());
    }

    /**
     * Возвращает потомков
     *
     * @return список ID пользователей
     * @throws GranException при необходимости
     */
    public ArrayList<String> getDescendents() throws GranException {
            return UserRelatedManager.getInstance().getDescendents(this.getId());
    }

    /**
     * ВОзвращает общее коичество дочерних пользователей
     *
     * @return количество дочерних пользователей
     * @throws GranException при необходимости
     */
    public Integer getTotalChildrenCount() throws GranException {
            return getChildCount();
    }


    /**
     * Очищает кеш значений пользовательских полей
     *
     * @throws GranException при необходимости
     */
    public void invalidateUDFsValues() throws GranException {
        udfLock.writeLock().lock();
        try {
            UDFValues = null;
            getUDFValues();
        } finally {
            udfLock.writeLock().unlock();
        }

    }

    /**
     * Очищает кеш пользовательского поля при изменении списка значений
     *
     * @param udfId  ID поля
     * @param value  новое значение
     * @param listId ID списка
     * @throws GranException при необходимости
     */
    public void invalidateUDFWhenChangeList(String udfId, String value, String listId) throws GranException {
        udfLock.writeLock().lock();
        try {
            UdfvalCacheItem cv;
            if (!isUDFValuesInitialized())
                return; // Nothing to update
            // Update or delete an existng value
            for (UdfValue udf : UDFValues) {
                if (udfId.equals(udf.getUdfId())) {
                    if (udf.getType() == UdfValue.MULTILIST) {
                        List<Pair> pairs = ((ListMultiValue) udf.getValueContainer()).getValue(null);
                        if (pairs != null && pairs.contains(new Pair(listId, value))) {
                            cv = new UdfvalCacheItem(null, getId(), value, null, null, listId, value, null, null, null);
                            udf.setValue(cv);
                        }
                    } else {
                        Pair pair = ((ListValue) udf.getValueContainer()).getValue(null);
                        if (pair != null && pair.getKey().equals(listId)) {
                            if (value.length() > 2000) {
                                String newLongtextId = null;
                                newLongtextId = KernelManager.getLongText().createLongtext(null, value);
                                cv = new UdfvalCacheItem(null, getId(), null, null, null, listId, value, newLongtextId, null, null);
                            } else {
                                cv = new UdfvalCacheItem(null, getId(), value, null, null, listId, value, null, null, null);
                            }
                            udf.setValue(cv);
                        }
                    }

                }
            }

        } finally {
            udfLock.writeLock().unlock();
        }

    }


    /**
     * Обновляет значение пользователского поля
     *
     * @param udfId ID поля
     * @param uci   поле
     * @throws GranException при необходимости
     */
    void invalidateUDF(String udfId, UDFCacheItem uci) throws GranException {
        udfLock.writeLock().lock();
        try {
            // Remove udf, if it's already exists
            if (UDFs != null)
                for (UDFCacheItem udf : UDFs) {
                    if (udf.getId().equals(udfId)) {
                        UDFs.remove(udf);
                        break;
                    }
                }
            // Remove value
            GenericValue cv = null;
            if (isUDFValuesInitialized()) {
                for (UdfValue udf : UDFValues) {
                    if (udf.getUdfId().equals(udfId)) {
                        if (uci != null)
                            cv = udf.getValueContainer();
                        UDFValues.remove(udf);
                        break;
                    }
                }
            }
            if (uci != null) {
                if (UDFs == null)
                    UDFs = new CopyOnWriteArrayList<UDFCacheItem>();
                if (this.id.equals(uci.userId))
                    UDFs.add(uci);
                if (!isUDFValuesInitialized())
                    UDFValues = new CopyOnWriteArrayList<UdfValue>();
                UdfValue value = new UdfValue(uci);
                if (cv != null)
                    value.setValueContainer(cv);
                UDFValues.add(value);
            }
        } finally {
            udfLock.writeLock().unlock();
        }


    }

    /**
     * Возвращает список значений отфильтрованных польовательских полей
     *
     * @return список значений
     * @throws GranException при необходимости
     */
    public List<UdfValue> getFilterUDFValues() throws GranException {
            return getUDFValues();
    }

    /**
     * Возвращает список значений пользовательских полей
     *
     * @return список значений
     * @throws GranException при необходимости
     */
    public List<UdfValue> getUDFValues() throws GranException {
        udfLock.writeLock().lock();
        try {
            if (UDFValues == null) {
                UDFValues = new CopyOnWriteArrayList(Null.removeNullElementsFromList(KernelManager.getUdf().getUDFValues(getId(), UdfConstants.USER_ALL, getUDFCacheItems())));
            }
            return UDFValues;
        } finally {
            udfLock.writeLock().unlock();
        }


    }

    /**
     * Возвращает отфильтрованные пользовательские поля
     *
     * @return список полей
     * @throws GranException при необходимости
     */
    public ArrayList<UDFCacheItem> getFilterUDFs() throws GranException {
        return getUDFCacheItems();
    }

    /**
     * Возвращает список пользовательских полей
     *
     * @return список полей
     * @throws GranException при необходимости
     */
    public ArrayList<UDFCacheItem> getUDFCacheItems() throws GranException {
            ArrayList<UDFCacheItem> list = new ArrayList<UDFCacheItem>();
            UserRelatedManager userRelatedManager = UserRelatedManager.getInstance();
            for (UserRelatedInfo localId : userRelatedManager.getUserChain(null, id)) {
                list.addAll(localId.getUDFs());
            }
            return list;
    }

    /**
     * Возвращает список полей
     *
     * @return список полей
     * @throws GranException при необходимости
     */
    public List<UDFCacheItem> getUDFs() throws GranException {
        udfLock.writeLock().lock();
        try {
            if (UDFs == null) {
                UDFs = new CopyOnWriteArrayList(Null.removeNullElementsFromList(KernelManager.getUdf().getListUserUDFCacheItem(getId())));
            }
            return UDFs;
        } finally {
            udfLock.writeLock().unlock();
        }

    }

    /**
     * Устанавливает список пользовательских полей
     *
     * @param udfList список полей
     * @throws GranException при необходимости
     */
    void setUDFs(List<UDFCacheItem> udfList) throws GranException {
        udfLock.writeLock().lock();
        try {
            UDFs = new CopyOnWriteArrayList(Null.removeNullElementsFromList(udfList));
        } finally {
            udfLock.writeLock().unlock();
        }

    }

    /**
     * Возвращает инициализированы поля или нет
     *
     * @return TRUE - инициализировано, FALSE - нет
     */
    protected boolean isUserUDFInitialized() {
        udfLock.readLock().lock();
        try {
            return UDFs != null;
        } finally {
            udfLock.readLock().unlock();
        }

    }

    /**
     * Устанавливает список значений полей
     *
     * @param udfList список значений
     * @throws GranException при необходимости
     */
    void setUDFValues(ArrayList<UdfValue> udfList) throws GranException {
        udfLock.writeLock().lock();
        try {
            UDFValues = new CopyOnWriteArrayList<UdfValue>(Null.removeNullElementsFromList(udfList));
        } finally {
            udfLock.writeLock().unlock();
        }

    }

    /**
     * Возвращает инициализированы значения полей или нет
     *
     * @return TRUE - инициализировано, FALSE - нет
     */
    protected boolean isUDFValuesInitialized() {
        udfLock.readLock().lock();
        try {
            return UDFValues != null;
        } finally {
            udfLock.readLock().unlock();
        }

    }

    /**
     * Возвращает количество полей
     *
     * @return количество полей
     * @throws GranException при необходимости
     */
    public Integer getCountUDF() throws GranException {
            ArrayList l = getUDFCacheItems();
            if (l != null)
                return l.size();
            else
                return null;
    }


    /**
     * ВОзвращает список адресов почты
     *
     * @return список адресов
     */
    public ArrayList<String> getEmailList() {
        ArrayList<String> al = new ArrayList<String>();
        if (Null.isNotNull(email)) {
            StringTokenizer tk = new StringTokenizer(email, delimiter);
            while (tk.hasMoreElements()) {
                String token = tk.nextToken();
                al.add(token);
            }
        }
        return al;
    }


    /**
     * ВОзвращает настройки пользователя
     *
     * @return настройки
     */
    public String getPreferences() {
        return preferences;
    }

    public String getEmergencyNotice() {
        return emergencyNotice.get();
    }

    public void setEmergencyNotice(String emergencyNotice) {
        this.emergencyNotice.set(emergencyNotice);
    }

    public Calendar getEmergencyNoticeDate() {
        emergencyNoticeDateLock.readLock().lock();
        try {
            if (emergencyNoticeDate == null)
                return null;
            else
                return (Calendar) emergencyNoticeDate.clone();
        } finally {
            emergencyNoticeDateLock.readLock().unlock();
        }

    }

    public void setEmergencyNoticeDate(Calendar emergencyNoticeDate) {
        emergencyNoticeDateLock.writeLock().lock();
        try {
            if (emergencyNoticeDate == null)
                this.emergencyNoticeDate = null;
            else
                this.emergencyNoticeDate = (Calendar) emergencyNoticeDate.clone();
        } finally {
            emergencyNoticeDateLock.writeLock().unlock();
        }

    }

    public Prstatus getPrstatus() throws GranException {
        return KernelManager.getFind().findPrstatus(prstatusId);
    }
}
