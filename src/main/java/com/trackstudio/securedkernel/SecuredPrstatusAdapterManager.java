package com.trackstudio.securedkernel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.AccessDeniedException;
import com.trackstudio.exception.GranException;
import com.trackstudio.exception.InvalidParameterException;
import com.trackstudio.exception.UserException;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.kernel.cache.UserRelatedManager;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.kernel.manager.SafeString;
import com.trackstudio.secured.SecuredCategoryBean;
import com.trackstudio.secured.SecuredMstatusBean;
import com.trackstudio.secured.SecuredPrstatusBean;
import com.trackstudio.secured.SecuredTaskUDFBean;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.secured.SecuredUserUDFBean;
import com.trackstudio.secured.SecuredWorkflowBean;
import com.trackstudio.startup.Config;
import com.trackstudio.tools.Pair;
import com.trackstudio.tools.ParameterValidator;
import com.trackstudio.tools.SecuredBeanUtil;

import net.jcip.annotations.Immutable;

/**
 * Класс SecuredPrstatusAdapterManager содержит методы для работы со статусами пользователей
 */
@Immutable
public class SecuredPrstatusAdapterManager {

    private static final Log log = LogFactory.getLog(SecuredPrstatusAdapterManager.class);
    private static final ParameterValidator pv = new ParameterValidator();

    /**
     * Создается статус
     *
     * @param sc     сессия пользователя
     * @param name   Название статуса
     * @param userId ID пользователя, который создает статус
     * @return ID созданного статуса
     * @throws GranException при необходимости
     */
    public String createPrstatus(SessionContext sc, String userId, String name) throws GranException {
        log.trace("createPrstatus");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "createPrstatus", "sc", sc);
        if (name == null || pv.badSmallDesc(name))
            throw new InvalidParameterException(this.getClass(), "createPrstatus", "name", sc);

        String prStatusId = new SecuredUserBean(sc.getUserId(userId), sc).getPrstatusId();
        ArrayList<SecuredCategoryBean> categories = AdapterManager.getInstance().getSecuredCategoryAdapterManager().getAllCategoryList(sc, prStatusId);
        ArrayList<SecuredWorkflowBean> workflows = AdapterManager.getInstance().getSecuredWorkflowAdapterManager().getAllWorkflowListForUser(sc, prStatusId);

        if (!sc.canAction(Action.manageRoles, sc.getUserId(userId)))
            throw new AccessDeniedException(this.getClass(), "createPrstatus", sc, "!sc.canAction(Action.createState, userId)", userId);

        String newPrStatusId = KernelManager.getPrstatus().createPrstatus(SafeString.createSafeString(name), sc.getUserId(userId));
        boolean permitAutomatically = Config.isTurnItOn("trackstudio.permit.role.automatically", "true");
        HashMap<String, List<String>> map = new HashMap<String, List<String>>();
        for (SecuredCategoryBean scb : categories) {
            map.put(scb.getId(), KernelManager.getCategory().getCategoryRuleList(prStatusId, scb.getId()));
        }
        if (permitAutomatically) {
            KernelManager.getCategory().setCategoryRuleMap(map, newPrStatusId);
        }
        List<String> mstatusList = new ArrayList<String>();
        for (SecuredWorkflowBean swb : workflows) {
            List<SecuredMstatusBean> mStatusList = AdapterManager.getInstance().getSecuredWorkflowAdapterManager().getMstatusList(sc, swb.getId());
            for (SecuredMstatusBean mStatus : mStatusList) {
                mstatusList.add(mStatus.getId());
            }
        }
        if (permitAutomatically) {
            KernelManager.getWorkflow().grantMap(newPrStatusId, mstatusList);
        }
        List<SecuredUserUDFBean> userUDFs = AdapterManager.getInstance().getSecuredUDFAdapterManager().getAllAvailableUserUdfListForStatus(sc, prStatusId);
        for (SecuredUserUDFBean suub : userUDFs) {
            List<String> types = AdapterManager.getInstance().getSecuredUDFAdapterManager().getUDFRuleList(sc, prStatusId, suub.getId());
            for (String type : types) {
                KernelManager.getUdf().setUDFRule(suub.getId(), newPrStatusId, type);
            }
        }

        List<SecuredTaskUDFBean> taskUDFs = AdapterManager.getInstance().getSecuredUDFAdapterManager().getAllAvailableTaskUdfListForStatus(sc, prStatusId);
        for (SecuredTaskUDFBean stub : taskUDFs) {
            List<String> types = AdapterManager.getInstance().getSecuredUDFAdapterManager().getUDFRuleList(sc, prStatusId, stub.getId());
            for (String type : types) {
                KernelManager.getUdf().setUDFRule(stub.getId(), newPrStatusId, type);
            }
        }

        return newPrStatusId;
    }

    /**
     * Создается копия статуса (клонируется)
     *
     * @param sc         сессия пользователя
     * @param prstatusId ID копируемого статуса
     * @param toUserId   ID пользователя
     * @return ID созданного статуса
     * @throws GranException при необходимости
     */
    public String clonePrstatus(SessionContext sc, String toUserId, String prstatusId) throws GranException {
        log.trace("clonePrstatus");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "clonePrstatus", "sc", null);
        if (prstatusId == null)
            throw new InvalidParameterException(this.getClass(), "clonePrstatus", "prstatusId", sc);
        if (!sc.canAction(Action.manageRoles, toUserId))
            throw new AccessDeniedException(this.getClass(), "clonePrstatus", sc, "!sc.canAction(Action.copyStatus, toUserId)", toUserId);
        SecuredPrstatusBean prstatus = AdapterManager.getInstance().getSecuredFindAdapterManager().findPrstatusById(sc, prstatusId);
        if (prstatusId.equals(UserRelatedManager.getInstance().find("1").getPrstatusId()))
            throw new AccessDeniedException(this.getClass(), "clonePrstatus", sc, "prstatusId.equals(UserRelatedManager.getInstance().find(\"1\").getPrstatusId()", prstatusId);
        if (!sc.canAction(Action.manageRoles, toUserId))
            throw new AccessDeniedException(this.getClass(), "clonePrstatus", sc, "!sc.canAction(Action.copyStatus, toUserId)", toUserId);
        if (!prstatus.canView())
            throw new AccessDeniedException(this.getClass(), "clonePrstatus", sc, "!prstatus.canView()", prstatusId);

        return KernelManager.getPrstatus().clonePrstatus(prstatusId, sc.getUserId(toUserId), sc.getLocale());
    }

    /**
     * Редактирует статус
     *
     * @param sc          сессия пользователя
     * @param prstatusId  ID редактируемого статуса
     * @param name        Название статуса
     * @param preferences Настройки статуса
     * @throws GranException при необходимости
     */
    public void updatePrstatus(SessionContext sc, String prstatusId, String name, String preferences) throws GranException {
        log.trace("updatePrstatus");
        log.debug("prstatusId(" + prstatusId + "), name(" + name + ')');
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "updatePrstatus", "sc", null);
        if (prstatusId == null)
            throw new InvalidParameterException(this.getClass(), "updatePrstatus", "prstatusId", sc);
        if (name == null || pv.badSmallDesc(name))
            throw new InvalidParameterException(this.getClass(), "updatePrstatus", "name", sc);
//        SecuredPrstatusBean prstatus = AdapterManager.getInstance().getSecuredFindAdapterManager().findPrstatusById(sc, prstatusId);
        //if (!(sc.canAction(Action.updateState, prstatus.getUserId()) && prstatus.canUpdate()))
        //    throw new AccessDeniedException(this.getClass(), "updatePrstatus", sc);
        if (!sc.canAction(Action.manageRoles, sc.getUserId()))
            throw new AccessDeniedException(this.getClass(), "updatePrstatus", sc, "!sc.canAction(Action.manageRoles, sc.getUserId())", sc.getUserId());
//        if (!prstatus.canManage())
//            throw new AccessDeniedException(this.getClass(), "updatePrstatus", sc, "!prstatus.canManage()");
        KernelManager.getPrstatus().updatePrstatus(prstatusId, SafeString.createSafeString(name), preferences);
    }

    /**
     * Удаляет статус по ID
     *
     * @param sc         сессия пользователя
     * @param prstatusId ID удаляемого статуса
     * @throws GranException при необходимости
     */
    public void deletePrstatus(SessionContext sc, String prstatusId) throws GranException {
        log.trace("deletePrstatus");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "deletePrstatus", "sc", sc);
        if (prstatusId == null)
            throw new InvalidParameterException(this.getClass(), "deletePrstatus", "prstatusId", sc);
        SecuredPrstatusBean prstatus = AdapterManager.getInstance().getSecuredFindAdapterManager().findPrstatusById(sc, prstatusId);
        //if (!(sc.canAction(Action.deleteState, prstatus.getUserId()) && prstatus.canUpdate() && !sc.getUser().getPrstatusId().equals(prstatus.getId())))
        //    throw new AccessDeniedException(this.getClass(), "deletePrstatus", sc);
        if (!prstatus.canManage())
            throw new AccessDeniedException(this.getClass(), "deletePrstatus", sc, "!prstatus.canManage()", prstatusId);
//        if (sc.getUser().getPrstatusId().equals(prstatus.getId()))
//            throw new AccessDeniedException(this.getClass(), "deletePrstatus", sc, "sc.getUser().getPrstatusId().equals(prstatus.getId())");
        /*if (prstatus.hasChildren())
            throw new CantDeletePrstatusException(new Exception("Children statuses exists"), prstatus.getName());
        */
        List<Pair> areUsed = UserRelatedManager.getInstance().getUsersForPrstatus(prstatusId);
        if (!areUsed.isEmpty()) {
            StringBuffer sb = new StringBuffer();
            for (Pair pair : areUsed) {
                sb.append(pair.getValueSort()).append("[").append(pair.getValue()).append("];");
            }
            throw new UserException("ERROR_CAN_NOT_DELETE_PRSTATUS", new Object[]{prstatus.getName(), sb});
        }

        if (KernelManager.getAcl().getAclForOverridePrstatusList(prstatusId).size() > 0)
            throw new UserException("ERROR_CAN_NOT_DELETE_PRSTATUS_OVERRIDE", new Object[]{prstatus.getName()});
        KernelManager.getPrstatus().deletePrstatus(prstatusId);
    }

    /**
     * Для пользователя, который создает ACL и его парентов достается
     * список статусов, созданных ими, затем достаются все подчиненные статусы
     * от собственного статуса пользователя плюс сам этот статус. Возвращается
     * сумма этих двух множеств
     *
     * @param sc     сессия пользователя
     * @param userId ID пользователя
     * @return Список статусов
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Prstatus
     */
    public ArrayList<SecuredPrstatusBean> getAvailablePrstatusList(SessionContext sc, String userId) throws GranException {
        log.trace("getAvailablePrstatusList. userId=" + userId);
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getAvailablePrstatusList", "sc", null);
        if (userId == null)
            throw new InvalidParameterException(this.getClass(), "getAvailablePrstatusList", "userId", sc);
        if (!sc.userOnSight(userId))
            throw new AccessDeniedException(this.getClass(), "getAvailablePrstatusList", sc, "!sc.userOnSight(userId)", userId);

        return SecuredBeanUtil.toArrayList(sc, KernelManager.getPrstatus().getAvailablePrstatusList(userId), SecuredBeanUtil.PRSTATUS);
    }

    /**
     * Возвращает списое статусов, которые может редактировать текущий пользователь
     *
     * @param sc сессия пользователя
     * @return список статусов
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredPrstatusBean
     */
    public ArrayList<SecuredPrstatusBean> getEditablePrstatusList(SessionContext sc) throws GranException {
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getEditablePrstatusList", "sc", null);
        ArrayList<SecuredPrstatusBean> list = getAvailablePrstatusList(sc, sc.getUserId());
        ArrayList<SecuredPrstatusBean> ret = new ArrayList<SecuredPrstatusBean>();
        for (SecuredPrstatusBean bean : list) {
            if (bean.isAllowedByACL()) {
                ret.add(bean);
            }
        }
        return ret;
    }

    /**
     * Возвращается список статусов, owner'ов которых мы можем "видеть", т.е.
     * которые находятся в одной ветке с нами
     *
     * @param sc сессия пользователя
     * @return список статусов
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredPrstatusBean
     */
    public ArrayList<SecuredPrstatusBean> getAllViewablePrstatuses(SessionContext sc) throws GranException {
        log.trace("getAllViewablePrstatuses.");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getAvailablePrstatusList", "sc", null);
        ArrayList<SecuredPrstatusBean> ret = new ArrayList<SecuredPrstatusBean>();
        ArrayList<SecuredPrstatusBean> list = SecuredBeanUtil.toArrayList(sc, KernelManager.getPrstatus().getPrstatusList(), SecuredBeanUtil.PRSTATUS);
        if (null != list) {
            for (SecuredPrstatusBean b : list) {
                if (b.canView())
                    ret.add(b);
            }
        }
        return ret;
    }

    /**
     * Возвращает список статусов, которые может создавать указанный пользователя
     *
     * @param sc     сессия пользователя
     * @param userId ID пользователя
     * @return список статусов
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Prstatus
     */
    public ArrayList<SecuredPrstatusBean> getCreatablePrstatusList(SessionContext sc, String userId) throws GranException {
        log.trace("getCreatablePrstatusList. userId=" + userId);
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getCreatablePrstatusList", "sc", null);
        if (userId == null)
            throw new InvalidParameterException(this.getClass(), "getCreatablePrstatusList", "userId", sc);
        if (!sc.userOnSight(userId))
            throw new AccessDeniedException(this.getClass(), "getCreatablePrstatusList", sc, "!sc.userOnSight(userId)", userId);

        return SecuredBeanUtil.toArrayList(sc, KernelManager.getPrstatus().getCreatablePrstatusList(userId), SecuredBeanUtil.PRSTATUS);
    }

    /**
     * Устанавливает роли для пользователя
     *
     * @param sc         сессия пользователя
     * @param prstatusId ID статуса пользователя
     * @param allowed    Устанавливаемые роли
     * @param denied     Удаляемые роли
     * @throws GranException при неободимости
     */
    public void setRoles(SessionContext sc, String prstatusId, List<String> allowed, List<String> denied) throws GranException {
        log.trace("setRoles");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "setRoles", "sc", sc);
        if (prstatusId == null)
            throw new InvalidParameterException(this.getClass(), "setRoles", "prstatusId", sc);

        SecuredPrstatusBean prstatus = AdapterManager.getInstance().getSecuredFindAdapterManager().findPrstatusById(sc, prstatusId);
        if (!prstatus.canManage())
            throw new AccessDeniedException(this.getClass(), "setRoles", sc, "sc.canAction(Action.updateState, prstatus.getUserId()) && prstatus.canUpdate() && !sc.getUser().getPrstatus().equals(prstatus)", prstatusId);

        KernelManager.getPrstatus().setRoles(prstatusId, allowed, denied);
    }

    /**
     * Проверяем, может ли один пользователь быть менеджером для другого.
     * <br>
     * Для userId и managerId достаем соответствующие prstatus и сравниваем. Если они не одинаковы, то берем parent-статус
     * от пользовательского и снова сравниваем со статусом менеджера. Так продолжаем, пока не дойдем до верха, либо не
     * найдем совпадающие статусы. Если статусы находятся в одной ветви, и статус managerId выше, чем статус userId,
     * проверяем, находятся ли managerId и владелец статуса userId в одной ветке, причем managerId должен являться parent-ом
     * по отношению к владельцу статуса. Т.е. иметь права редактирования на этот статус, фактически.
     * Если такие условия выполняются, то возвращаем true.
     *
     * @param sc        сессия пользователя
     * @param userId    Пользователь, для которого проверяем.
     * @param managerId Потенциальный менеджер
     * @return true - если может
     * @throws GranException при необходимости
     */
    public boolean isManagerAvailable(SessionContext sc, String userId, String managerId) throws GranException {
        log.trace("isManagerAvailable");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "isManagerAvailable", "sc", sc);
        if (userId == null)
            throw new InvalidParameterException(this.getClass(), "isManagerAvailable", "userId", sc);
        if (managerId == null)
            throw new InvalidParameterException(this.getClass(), "isManagerAvailable", "managerId", sc);
        return KernelManager.getPrstatus().isManagerAvailable(userId, managerId);
    }
}
