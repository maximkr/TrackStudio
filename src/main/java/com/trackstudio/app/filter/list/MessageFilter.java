package com.trackstudio.app.filter.list;

import java.util.ArrayList;
import java.util.List;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.csv.CSVImport;
import com.trackstudio.app.filter.AbstractFilter;
import com.trackstudio.app.filter.TaskFValue;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.common.FieldMap;
import com.trackstudio.exception.GranException;
import com.trackstudio.secured.SecuredMessageBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUserBean;

import net.jcip.annotations.Immutable;

/**
 * Класс содержит методы для фильтрации сообщений
 */
@Immutable
public class MessageFilter extends AbstractFilter {

    private final SecuredTaskBean task;

    /**
     * Конструктор
     *
     * @param task задача
     * @see com.trackstudio.secured.SecuredTaskBean
     */
    public MessageFilter(SecuredTaskBean task) {
        this.task = task;
    }

    /*! Установить условия поиска задач в зависимости от того есть ли у нас права на
     *  просмотр текущей задачи и стоит ли галка with subtasks
     */
    /**
     * Возвращает список проходящих по фильтру сообщений
     *
     * @param sc                    сессия пользователя
     * @param flt                   параметры фильтрации
     * @param forcedAllMessagesView указывает нудно ли принудительно возвращать все отфильтрованные сообщения или нет, вне зависимости от указанного в параметрах фильтрации числа
     * @return список сообщений
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredMessageBean
     */                                                                              //forcedAllMessagesView - надо для workload отчета
    public List<SecuredMessageBean> getMessageList(SessionContext sc, TaskFValue flt, Boolean forcedAllMessagesView, boolean withAudit) throws GranException {
        try {
            log.trace("##########");

            List<SecuredMessageBean> list = new ArrayList<SecuredMessageBean>();
            if ((flt.get(FieldMap.MESSAGEVIEW.getFilterKey()) != null && !flt.get(FieldMap.MESSAGEVIEW.getFilterKey()).contains("0")) || forcedAllMessagesView) {
                ArrayList<SecuredMessageBean> listMes = AdapterManager.getInstance().getSecuredMessageAdapterManager().getMessageList(sc, task.getId());
                for (SecuredMessageBean mess : listMes) {
                    if (pass(mess, flt)) {
                        if (CSVImport.LOG_MESSAGE.equals(mess.getMstatus().getName())) {
                            if (withAudit) {
                                list.add(mess);
                            }
                        } else {
                            list.add(mess);
                        }
                    }
                }
                if (!forcedAllMessagesView) {
                    String v = flt.getAsString(FieldMap.MESSAGEVIEW.getFilterKey());
                    String prefix = flt.getPrefix(FieldMap.MESSAGEVIEW.getFilterKey());
                    int msgMax = 0;
                    //todo winzard 13.12.2007
                    if (!(v == null || v.length() == 0)) {
                        msgMax = prefix.equals("_") ? -Integer.parseInt(v) : Integer.parseInt(v);
                    }
                    if (msgMax >= 0 && list.size() > msgMax) {
                        list = list.subList(list.size() - msgMax, list.size());
                    }
                }
            }

            return list;
        } catch (Exception e) {

            throw new GranException(e);
        }
    }

    /**
     * Проверяет соответствие сообщения условиям фильтрации
     *
     * @param mess сообщение
     * @param flt  параметры фильтрации
     * @return TRUE - соответствует, FALSE - нет
     * @throws GranException при необходимости
     */
    public boolean pass(SecuredMessageBean mess, TaskFValue flt) throws GranException {
        log.trace("##########");
        boolean msgNeedAdd = true;
        List<String> useForMessages = flt.getUseForMessages();
        for (String use : useForMessages) {
            if (use.equals(FieldMap.MSG_SUSER_NAME.getFilterKey()))
                msgNeedAdd = testUser(flt, FieldMap.MSG_SUSER_NAME.getFilterKey(), mess.getSubmitterId(), task.getSecure().getUserId());
            else if (use.equals(FieldMap.MSG_SUBMITDATE.getFilterKey()))
                msgNeedAdd = testTimestamp(flt, FieldMap.MSG_SUBMITDATE.getFilterKey(), mess.getTime());
            else if (use.equals(TaskFValue.MSG_TYPE))
                msgNeedAdd = testList(flt, TaskFValue.MSG_TYPE, mess.getMstatusId());
            else if (use.equals(FieldMap.MSG_HUSER_NAME.getFilterKey()))
                if (mess.getHandlerGroupId() == null)
                    msgNeedAdd = testUser(flt, FieldMap.MSG_HUSER_NAME.getFilterKey(), mess.getHandlerUserId(), task.getSecure().getUserId());
                else {
                    msgNeedAdd = testList(flt, FieldMap.MSG_HUSER_NAME.getFilterKey(), "GROUP_" + mess.getHandlerGroupId());
                    if (!msgNeedAdd) {
                        List<String> values = flt.get(FieldMap.MSG_HUSER_NAME.getFilterKey());
                        if (values != null) {
                            for (String handler : values) {
                                SecuredUserBean user = null;
                                if (handler.equals("null")) continue;
                                if (handler.equals("CurrentUserID") || handler.equals("IandSubUsers") ||
                                        handler.equals("IandManager") || handler.equals("IandManagers"))
                                    user = task.getSecure().getUser();
                                else {
                                    if (handler.startsWith("GROUP_")) {
                                        if (handler.equals("GROUP_MyActiveGroup")) {
                                            msgNeedAdd = testActiveGroupHandler(flt, FieldMap.MSG_HUSER_NAME.getFilterKey(), mess.getHandlerGroupId(), new ArrayList<String>(task.getSecure().getAllowedPrstatusesForTask(mess.getTaskId())));
                                        }
                                    } else {
                                        user = new SecuredUserBean(handler, mess.getSecure());
                                    }
                                }
                                if (user != null && user.getPrstatus().getId().equals(task.getHandlerGroupId()))
                                    msgNeedAdd = true;
                            }
                        }
                    }
                }
            else if (use.equals(FieldMap.MSG_RESOLUTION.getFilterKey()))
                msgNeedAdd = testList(flt, FieldMap.MSG_RESOLUTION.getFilterKey(), mess.getResolutionId() != null ? mess.getResolutionId() : null);
            else if (use.equals(FieldMap.MSG_ABUDGET.getFilterKey()))
                msgNeedAdd = testFloat(flt, FieldMap.MSG_ABUDGET.getFilterKey(), mess.getHrs() != null ? mess.getHrs() : 0.0);
            else if (use.equals(TaskFValue.MSG_TEXT))
                msgNeedAdd = testString(flt, TaskFValue.MSG_TEXT, mess.getTextDescription());
            if (!msgNeedAdd)
                break;
        }
        return msgNeedAdd;
    }
}
