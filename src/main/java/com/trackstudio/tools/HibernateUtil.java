package com.trackstudio.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.CacheMode;
import org.hibernate.Hibernate;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Query;
import org.hibernate.Session;

import com.trackstudio.exception.CantDeleteAclException;
import com.trackstudio.exception.CantDeleteCategoryException;
import com.trackstudio.exception.CantDeleteFilterException;
import com.trackstudio.exception.CantDeleteMailImportException;
import com.trackstudio.exception.CantDeleteMessageException;
import com.trackstudio.exception.CantDeleteMessageTypeException;
import com.trackstudio.exception.CantDeleteNotificationException;
import com.trackstudio.exception.CantDeletePriorityException;
import com.trackstudio.exception.CantDeletePrstatusException;
import com.trackstudio.exception.CantDeleteRegistrationException;
import com.trackstudio.exception.CantDeleteResolutionException;
import com.trackstudio.exception.CantDeleteStateException;
import com.trackstudio.exception.CantDeleteTemplateException;
import com.trackstudio.exception.CantDeleteTransitionException;
import com.trackstudio.exception.CantDeleteUdfException;
import com.trackstudio.exception.CantDeleteUserException;
import com.trackstudio.exception.CantDeleteWorkflowException;
import com.trackstudio.exception.GranException;
import com.trackstudio.exception.TaskNotFoundException;
import com.trackstudio.exception.UserException;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.model.Acl;
import com.trackstudio.model.Category;
import com.trackstudio.model.Cprstatus;
import com.trackstudio.model.Filter;
import com.trackstudio.model.MailImport;
import com.trackstudio.model.Message;
import com.trackstudio.model.Mstatus;
import com.trackstudio.model.Notification;
import com.trackstudio.model.Priority;
import com.trackstudio.model.Prstatus;
import com.trackstudio.model.Registration;
import com.trackstudio.model.Resolution;
import com.trackstudio.model.Status;
import com.trackstudio.model.Task;
import com.trackstudio.model.Template;
import com.trackstudio.model.Transition;
import com.trackstudio.model.Udf;
import com.trackstudio.model.Udflist;
import com.trackstudio.model.User;
import com.trackstudio.model.Workflow;

import net.jcip.annotations.Immutable;

/**
 * Класс с набором методов для доступа к базе данных через Hibernate
 */
@Immutable
public class HibernateUtil {

    private static final String className = "HibernateUtil.";
    private static final LockManager lockManager = LockManager.getInstance();

    /**
     * Pattern - questions mark.
     */
    private static final Pattern QUESTION_MARK = Pattern.compile("\\?");

    /**
     * Возвращает список объектов указанного класса
     *
     * @param query     запрос в виде HSQL
     * @return список объектов
     * @throws GranException при необходимости
     */
    public List getList(String query) throws GranException {
        return getList(query, true, new ArrayList());
    }

    /**
     * Возвращает список объектов указанного класса
     *
     * @param query     запрос в виде HSQL
     * @param cacheable надо ли кешировать средствами хибернейта
     * @return список объектов
     * @throws GranException при необходимости
     */
    public List getList(String query, boolean cacheable, Collection collection) throws GranException {
        return createQuery(query, collection, cacheable).list();
    }

    /**
     * Возвращает список объектов указанного класса
     *
     * @param query запрос в виде HSQL
     * @return список объектов
     * @throws GranException при необходимости
     */
    public List getList(String query, Object ... params) throws GranException {
        return getList(query, false, Arrays.asList(params));
    }

	// sufdclient~ zkau~ web~ 389a5975~ city~ icons~ miscellaneous~ notification_info.png~26locale~3Dru~26checksum~3Dc126c67db7b13f56f1f8802028c2731485a4876f/notification_info.png
	
    public List getListMap(String query, Map<String, String> paramsString, Map<String, Collection> paramsList) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            Session sess = lockManager.getDBSession().getSession();
            Query q = sess.createQuery(query);
            for (Map.Entry<String, String> entry : paramsString.entrySet()) {
                q.setString(entry.getKey(), entry.getValue());
            }
            for (Map.Entry<String, Collection> entry : paramsList.entrySet()) {
                q.setParameterList(entry.getKey(), entry.getValue());
            }
            q.setCacheable(true);
            return q.list();
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    public int executeDMLListMap(String query, Map<String, String> paramsString, Map<String, Collection> paramsList) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            Session sess = lockManager.getDBSession().getSession();
            Query q = sess.createQuery(query);
            for (Map.Entry<String, String> entry : paramsString.entrySet()) {
                q.setString(entry.getKey(), entry.getValue());
            }
            for (Map.Entry<String, Collection> entry : paramsList.entrySet()) {
                q.setParameterList(entry.getKey(), entry.getValue());
            }
            return q.executeUpdate();
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Создает указанный объект
     *
     * @param obj объект
     * @return ID созданного объекта
     * @throws GranException при необходимости
     */
    public String createObject(Object obj) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            Session sess = lockManager.getDBSession().getSession();
            String template;
            template = (String) sess.save(obj);
            cleanSession();
            return template;
        } catch (Exception e) {
            throw new GranException(e);
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * This method makes flush and then clear the hibernate session
     */
    public void cleanSession() {
        lockManager.getDBSession().commit();
    }

    /**
     * Создает указанный объект
     *
     * @param objs            объект
     * @return ID созданного объекта
     * @throws GranException при необходимости
     */
    public List<String> createObjects(List<Object> objs) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            Session sess = lockManager.getDBSession().getSession();
            List<String> ids = new ArrayList<String>();
                for (Object obj : objs) {
                    ids.add((String) sess.save(obj));
                }
            cleanSession();
            return ids;
        } catch (Exception e) {
            throw new GranException(e);
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Обновляет указанный объект
     *
     * @param obj            объект
     * @throws GranException при необходимости
     */
    public void updateObject(Object obj) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            Session sess = lockManager.getDBSession().getSession();
            sess.update(obj);
            cleanSession();
        } catch (Exception e) {
            throw new GranException(e);
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Удаляет объект указанного класса по его ID
     *
     * @param c              класс удаляемого объекта
     * @param id             ID удаляемого объекта
     * @throws GranException при необходимости
     */
    public void deleteObject(Class c, String id) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            Session sess = lockManager.getDBSession().getSession();
            Object obj = null;
            try {
                obj = sess.load(c, id);

                sess.delete(obj);
                cleanSession();
            } catch (Exception e) {
//                log.debug("CLOSE SESSION HU deleteObject");
//                HibernateSession.closeSession();
                if (c.equals(Acl.class))
                    throw new CantDeleteAclException(e);
                else if (c.equals(Category.class))
                    throw new CantDeleteCategoryException(e, obj != null ? ((Category) obj).getName() : id);
                else if (c.equals(Cprstatus.class))
                    throw new CantDeleteCategoryException(e);
                else if (c.equals(Notification.class))
                    throw new CantDeleteNotificationException(e);
                else if (c.equals(MailImport.class))
                    throw new CantDeleteMailImportException(e, obj != null ? ((MailImport) obj).getId() : id);
                else if (c.equals(Template.class))
                    throw new CantDeleteTemplateException(e, obj != null ? ((Template) obj).getId() : id);
                else if (c.equals(Message.class))
                    throw new CantDeleteMessageException(e, obj != null ? ((Message) obj).getTime().toString() : id);
                else if (c.equals(Prstatus.class))
                    throw new CantDeletePrstatusException(e, obj != null ? ((Prstatus) obj).getName() : id);
                else if (c.equals(Registration.class))
                    throw new CantDeleteRegistrationException(e, obj != null ? ((Registration) obj).getName() : id);
                else if (c.equals(Udflist.class))
                    throw new CantDeleteUdfException(e);
                else if (c.equals(Udf.class))
                    throw new CantDeleteUdfException(e, obj != null ? ((Udf) obj).getCaption() : id);
                else if (c.equals(User.class))
                    throw new CantDeleteUserException(obj != null ? ((User) obj).getName() : id);
                else if (c.equals(Workflow.class))
                    throw new CantDeleteWorkflowException(e, obj != null ? ((Workflow) obj).getName() : id);
                else if (c.equals(Transition.class))
                    throw new CantDeleteTransitionException(e);
                else if (c.equals(Resolution.class))
                    throw new CantDeleteResolutionException(e, obj != null ? ((Resolution) obj).getName() : id);
                else if (c.equals(Mstatus.class))
                    throw new CantDeleteMessageTypeException(e, obj != null ? ((Mstatus) obj).getName() : id);
                else if (c.equals(Status.class))
                    throw new CantDeleteStateException(e, obj != null ? ((Status) obj).getName() : id);
                else if (c.equals(Priority.class))
                    throw new CantDeletePriorityException(e, obj != null ? ((Priority) obj).getName() : id);
                else if (c.equals(Filter.class))
                    throw new CantDeleteFilterException(e, obj != null ? ((Filter) obj).getName() : id);
                else
                    throw new GranException(e);
            }
        } catch (UserException e) {
            throw e;
        } catch (Exception e) {
            throw new GranException(e);
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    private Query createQuery(String query, Collection param, boolean cacheable) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            Session sess = lockManager.getDBSession().getSession();
            Query q = sess.createQuery(convert(query));
            int count = 0;
            for (Object p : param) {
                if (p instanceof String) {
                    q.setString(count++, (String)p);
                } else if (p instanceof Integer) {
                    q.setInteger(count++, (Integer)p);
                } else if (p instanceof Date) {
                    q.setDate(count++, (Date)p);
                } else if (p instanceof Calendar) {
                    q.setCalendar(count++, (Calendar)p);
                }
            }
            q.setCacheable(cacheable);
            if (!cacheable)
                q.setCacheMode(CacheMode.IGNORE);
            return q;
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает объект указанного класса по его ID
     *
     * @param c  класс, объект которого возвращаем
     * @param id ID объекта
     * @return объект
     * @throws GranException при необходимости
     */
    public Object getObject(Class c, String id) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            Session sess = lockManager.getDBSession().getSession();
            Object object = sess.get(c, id);
            Hibernate.initialize(object);
            return object;
        } catch (ObjectNotFoundException e) {
            if (c.equals(Task.class))
                throw new TaskNotFoundException(id);
            else
                throw new UserException("ERROR_OBJECT_NOT_FOUND", new Object[]{id});
        } catch (Exception e) {
            throw new GranException(e);
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    public void executeDML(String query, Object ... params) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            createQuery(query, Arrays.asList(params), false).executeUpdate();
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }


    /**
     * Convent HQL Legacy-style to new HQL-Style
     * @param query legacy-style HQL
     * @return HQL in new style.
     */
    public String convert(String query) {
        int count = 0;
        StringBuffer sb = new StringBuffer();
        Matcher matcher = QUESTION_MARK.matcher(query);
        while (matcher.find()) {
            matcher.appendReplacement(sb, String.format("?%s", count++));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}