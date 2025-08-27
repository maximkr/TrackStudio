package com.trackstudio.securedkernel;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.filter.FilterSettings;
import com.trackstudio.app.filter.TaskFValue;
import com.trackstudio.app.report.handmade.HandMadeReportManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.AccessDeniedException;
import com.trackstudio.exception.GranException;
import com.trackstudio.exception.InvalidParameterException;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.kernel.manager.SafeString;
import com.trackstudio.secured.SecuredFilterBean;
import com.trackstudio.secured.SecuredReportBean;
import com.trackstudio.tools.ParameterValidator;
import com.trackstudio.tools.SecuredBeanUtil;

import net.jcip.annotations.Immutable;

/**
 * Класс SecuredReportAdapterManager содержит методы для работы с отчетами
 */
@Immutable
public class SecuredReportAdapterManager {
    public final static Log log = LogFactory.getLog(SecuredReportAdapterManager.class);
    private static final ParameterValidator pv = new ParameterValidator();
    public final static String RT_TREE_XML = "TreeXML";
    public final static String RT_CSV = "CSV";
    public final static String RT_RSS = "RSS";

    /**
     * Возвращает список отчетов для указанных задачи и текущего пользователя
     *
     * @param sc     сессия пользователя
     * @param taskId ID задачи, для которой получаются отчеты
     * @return список отчетов
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Report
     */
    public List<SecuredReportBean> getReportList(SessionContext sc, String taskId) throws GranException {
        log.trace("getReportList");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getReportList", "sc", null);
        if (taskId == null)
            throw new InvalidParameterException(this.getClass(), "getReportList", "taskId", sc);
        //if (!(sc.canAction(com.trackstudio.kernel.cache.Action.viewReport, taskId) && sc.taskOnSight(taskId)))
        //    throw new AccessDeniedException(this.getClass(), "getReportList", sc);
        if (!sc.canAction(Action.viewReports, taskId))
            throw new AccessDeniedException(this.getClass(), "getReportList", sc, "!sc.canAction(com.trackstudio.kernel.cache.Action.viewReport, taskId)", taskId);
        if (!sc.taskOnSight(taskId))
            throw new AccessDeniedException(this.getClass(), "getReportList", sc, "!sc.taskOnSight(taskId)", taskId);
        List<SecuredReportBean> ret = new ArrayList<SecuredReportBean>();
        for (Object o : SecuredBeanUtil.toArrayList(sc, KernelManager.getReport().getReportList(taskId, sc.getUserId()), SecuredBeanUtil.REPORT)) {
            SecuredReportBean srb = (SecuredReportBean) o;
            if (srb.canView())
                ret.add(srb);
        }
        return ret;
    }

    /**
     * Возвращает список всех отчетов для указанных задачи и пользователя
     *
     * @param sc     скссия пользователя
     * @param taskId ID задачи, для которой получаются отчеты
     * @return список отчетов
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Report
     */
    public ArrayList<SecuredReportBean> getAllReportList(SessionContext sc, String taskId) throws GranException {
        log.trace("getReportList");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getReportList", "sc", null);
        if (taskId == null)
            throw new InvalidParameterException(this.getClass(), "getReportList", "taskId", sc);
        //if (!(sc.canAction(com.trackstudio.kernel.cache.Action.viewReport, taskId) && sc.taskOnSight(taskId)))
        //    throw new AccessDeniedException(this.getClass(), "getReportList", sc);
        if (!sc.canAction(Action.viewReports, taskId))
            throw new AccessDeniedException(this.getClass(), "getReportList", sc, "!sc.canAction(com.trackstudio.kernel.cache.Action.viewReport, taskId)", taskId);
        if (!sc.taskOnSight(taskId))
            throw new AccessDeniedException(this.getClass(), "getReportList", sc, "!sc.taskOnSight(taskId)", taskId);
        ArrayList<SecuredReportBean> ret = new ArrayList<SecuredReportBean>();
        for (Object o : SecuredBeanUtil.toArrayList(sc, KernelManager.getReport().getAllReportList(taskId, sc.getUserId()), SecuredBeanUtil.REPORT)) {
            SecuredReportBean srb = (SecuredReportBean) o;
            if (srb.canView())
                ret.add(srb);
        }
        return ret;
    }

    /**
     * Создается отчет
     *
     * @param sc       сессия пользователя
     * @param name     Название отчета
     * @param rtypeId  Тип отчета
     * @param priv     Приватный или публичный отчет
     * @param filterId ID фильтра для отчета
     * @param taskId   ID задачи, для которой создается отчет
     * @return ID созданного отчета
     * @throws GranException при необходимости
     */
    public String createReport(SessionContext sc, String name, String rtypeId, boolean priv, String filterId,
                               String taskId) throws GranException {
        log.trace("createReport");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "createReport", "sc", null);
        if (name == null || pv.badSmallDesc(name))
            throw new InvalidParameterException(this.getClass(), "createReport", "name", sc);
        if (rtypeId == null)
            throw new InvalidParameterException(this.getClass(), "createReport", "rtypeId", sc);
        if (filterId == null)
            throw new InvalidParameterException(this.getClass(), "createReport", "filterId", sc);
        if (taskId == null)
            throw new InvalidParameterException(this.getClass(), "createReport", "taskId", sc);
        SecuredFilterBean filter = AdapterManager.getInstance().getSecuredFindAdapterManager().findFilterById(sc, filterId);
        //if (!(sc.canAction(Action.createReport, taskId) && sc.taskOnSight(taskId) && filter.canView()) || (!priv && !sc.canAction(Action.createPublicReport, taskId)))
        //    throw new AccessDeniedException(this.getClass(), "createReport", sc);
        if (!sc.canAction(Action.managePrivateReports, taskId))
            throw new AccessDeniedException(this.getClass(), "createReport", sc, "!sc.canAction(Action.createReport, taskId)", taskId);
        if (!sc.taskOnSight(taskId))
            throw new AccessDeniedException(this.getClass(), "createReport", sc, "!sc.taskOnSight(taskId)", taskId);
        if (!filter.canView())
            throw new AccessDeniedException(this.getClass(), "createReport", sc, "!filter.canView()", filterId);
        if (!priv && !sc.canAction(Action.managePublicReports, taskId))
            throw new AccessDeniedException(this.getClass(), "createReport", sc, "!priv && !sc.canAction(Action.createPublicReport, taskId)", taskId);
        String repId = KernelManager.getReport().createReport(SafeString.createSafeString(name), rtypeId, priv, filterId, taskId, sc.getUserId());
//        HibernateSession.closeSession();
        return repId;
    }

    /**
     * Редактируется отчет
     *
     * @param sc       сессия пользователя
     * @param reportId ID отчета, который редактируется
     * @param name     Название отчета
     * @param priv     Приватный или публичный отчет
     * @param rtypeId  Тип отчета
     * @param filterId ID фильтра для отчета
     * @throws GranException при необходимости
     */
    public void updateReport(SessionContext sc, String reportId, String name, boolean priv, String rtypeId, String filterId)
            throws GranException {
        log.trace("updateReport");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "updateReport", "sc", null);
        if (reportId == null)
            throw new InvalidParameterException(this.getClass(), "updateReport", "reportId", sc);
        if (name == null || pv.badSmallDesc(name))
            throw new InvalidParameterException(this.getClass(), "updateReport", "name", sc);
        if (rtypeId == null)
            throw new InvalidParameterException(this.getClass(), "updateReport", "rtypeId", sc);
        if (filterId == null)
            throw new InvalidParameterException(this.getClass(), "updateReport", "filterId", sc);
        SecuredReportBean report = AdapterManager.getInstance().getSecuredFindAdapterManager().findReportById(sc, reportId);
        SecuredFilterBean filter = AdapterManager.getInstance().getSecuredFindAdapterManager().findFilterById(sc, filterId);
        if (!sc.canAction(Action.managePrivateReports, report.getTaskId()))
            throw new AccessDeniedException(this.getClass(), "updateReport", sc, "!sc.canAction(com.trackstudio.kernel.cache.Action.editReport, report.getTaskId())", reportId);
        if (!report.canManage())
            throw new AccessDeniedException(this.getClass(), "updateReport", sc, "!report.canUpdate()", reportId);
        if (!filter.canView())
            throw new AccessDeniedException(this.getClass(), "updateReport", sc, "!filter.canView()", filterId);
        KernelManager.getReport().updateReport(reportId, SafeString.createSafeString(name), priv, rtypeId, filterId);
    }

    /**
     * Редактируется параметры для отчета
     *
     * @param sc       сессия пользователя
     * @param reportId ID отчета, параметры которого обновляются
     * @param params   параметры
     * @throws GranException при необходимости
     */
    public void updateReportParams(SessionContext sc, String reportId, String params) throws GranException {
        log.trace("updateReportParams");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "updateReportParams", "sc", null);
        if (reportId == null)
            throw new InvalidParameterException(this.getClass(), "updateReportParams", "reportId", sc);
        if (params == null || pv.badDesc(params))
            throw new InvalidParameterException(this.getClass(), "updateReportParams", "params", sc);
        SecuredReportBean report = AdapterManager.getInstance().getSecuredFindAdapterManager().findReportById(sc, reportId);
        if (!report.canView())
            throw new AccessDeniedException(this.getClass(), "updateReportParams", sc, "!report.canView()", reportId);
        KernelManager.getReport().updateReportParams(reportId, params);
    }

    /**
     * Удаляется отчет по его ID
     *
     * @param sc       сессия пользователя
     * @param reportId ID отчета, который удаляется
     * @throws GranException при необходимости
     */
    public void deleteReport(SessionContext sc, String reportId) throws GranException {
        log.trace("deleteReport");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "deleteReport", "sc", null);
        if (reportId == null)
            throw new InvalidParameterException(this.getClass(), "deleteReport", "reportId", null);
        SecuredReportBean report = AdapterManager.getInstance().getSecuredFindAdapterManager().findReportById(sc, reportId);
        //if (!(sc.canAction(com.trackstudio.kernel.cache.Action.deleteReport, report.getTaskId()) && report.canUpdate()))
        //    throw new AccessDeniedException(this.getClass(), "deleteReport", sc);
        if (!sc.canAction(Action.managePrivateReports, report.getTaskId()))
            throw new AccessDeniedException(this.getClass(), "deleteReport", sc, "!sc.canAction(com.trackstudio.kernel.cache.Action.deleteReport, report.getTaskId())", reportId);
        if (!report.canUpdate())
            throw new AccessDeniedException(this.getClass(), "deleteReport", sc, "!report.canUpdate()", reportId);
        KernelManager.getReport().deleteReport(reportId);
    }

    /**
     * Возвращает параметры для фильтра задач
     *
     * @param sc       сессия пользователя
     * @param reportId ID отчета
     * @return параметры для фильтра в виде объекта UserFValue
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredTaskFValueBean
     */
    public TaskFValue getFValue(SessionContext sc, String reportId) throws GranException {
        log.trace("getFValue");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getFValue", "sc", null);
        if (reportId == null)
            throw new InvalidParameterException(this.getClass(), "getFValue", "reportId", null);
        SecuredReportBean report = AdapterManager.getInstance().getSecuredFindAdapterManager().findReportById(sc, reportId);
        if (!report.canView())
            throw new AccessDeniedException(this.getClass(), "getFValue", sc, "!report.canView()", reportId);
        SecuredReportBean bean = AdapterManager.getInstance().getSecuredFindAdapterManager().findReportById(sc, reportId);
        FilterSettings flthm = null;
        Object filterObject = sc.getAttribute(reportId + "_reportfilter_"+report.getFilterId());
        if (filterObject != null) {
            flthm = (FilterSettings) filterObject;
            if (!flthm.getFilterId().equals(bean.getFilterId())) {
                flthm = null;
            }
        }
        TaskFValue val = AdapterManager.getInstance().getSecuredFilterAdapterManager().getTaskFValue(sc, bean.getFilterId()).getFValue();
        FilterSettings originFilterSettings = new FilterSettings(val, bean.getTaskId(), bean.getFilterId());
        if (flthm == null) {
            flthm = originFilterSettings;
        }
        if (flthm.getFieldId() == null) {
            flthm.setFieldId("default");
        }
        return (TaskFValue) flthm.getSettings();
    }

    /**
     * Создает отчет
     *
     * @param sc         сессия пользователя
     * @param reportId   ID отчета
     * @param taskId     ID задачи
     * @param format     формат отчета
     * @param delimiter  разделитель
     * @param encoding   кодировка
     * @param fvalue     параметры постфильтрации
     * @param request    запрос
     * @return сгенерированный отчет в виде байтового массива
     * @throws Exception при необходимости
     */
    public byte[] generateReport(SessionContext sc, String reportId, String taskId, String format, String delimiter, String encoding, TaskFValue fvalue, HttpServletRequest request) throws Exception {
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "generateReport", "sc", null);
        if (reportId == null)
            throw new InvalidParameterException(this.getClass(), "generateReport", "reportId", null);
        SecuredReportBean rep = AdapterManager.getInstance().getSecuredFindAdapterManager().findReportById(sc, reportId);
        if (!rep.canView())
            throw new AccessDeniedException(this.getClass(), "generateReport", sc, "!report.canView()", reportId);
        SecuredReportBean bean = AdapterManager.getInstance().getSecuredFindAdapterManager().findReportById(sc, reportId);
            if (fvalue == null)
                fvalue = AdapterManager.getInstance().getSecuredReportAdapterManager().getFValue(sc, reportId);
            String filterId = bean.getFilterId();
            String export = new HandMadeReportManager().generate(sc, taskId, filterId, fvalue, format, delimiter, encoding);
            return export.getBytes(encoding);
    }
}