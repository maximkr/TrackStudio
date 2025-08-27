package com.trackstudio.startup;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;

import com.trackstudio.app.filter.FValue;
import com.trackstudio.app.filter.TaskFValue;
import com.trackstudio.common.FieldMap;
import com.trackstudio.constants.CategoryConstants;
import com.trackstudio.constants.UdfConstants;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.kernel.cache.PluginCacheManager;
import com.trackstudio.kernel.cache.PluginType;
import com.trackstudio.kernel.cache.TaskRelatedManager;
import com.trackstudio.kernel.cache.UserRelatedManager;
import com.trackstudio.kernel.manager.AttachmentManager;
import com.trackstudio.kernel.manager.TSPropertyManager;
import com.trackstudio.model.Attachment;
import com.trackstudio.model.Cprstatus;
import com.trackstudio.model.Fvalue;
import com.trackstudio.model.Longtext;
import com.trackstudio.model.MailImport;
import com.trackstudio.model.Mstatus;
import com.trackstudio.model.Notification;
import com.trackstudio.model.Property;
import com.trackstudio.model.Prstatus;
import com.trackstudio.model.Rolestatus;
import com.trackstudio.model.Trigger;
import com.trackstudio.model.Udf;
import com.trackstudio.model.Umstatus;
import com.trackstudio.model.Uprstatus;
import com.trackstudio.model.User;
import com.trackstudio.model.Workflow;
import com.trackstudio.tools.HibernateUtil;

import net.jcip.annotations.Immutable;

import static com.trackstudio.tools.textfilter.MacrosUtil.removeLocaleCharacters;

/**
 * Класс для валидации данных в БД при переходе на более новые версии ТС
 */
@Immutable
public class DatabaseValidator {
    private static final HibernateUtil hu = new HibernateUtil();
    private static final DatabaseValidator ourInstance = new DatabaseValidator();
    private static final String dbVersion = "dbVersion";
    private static final String canBeHandlerCatPermission = "canBeHandlerCatPermission";
    private static final Log log = LogFactory.getLog(DatabaseValidator.class);

    /**
     * Возвращает экземпляр текущего класса
     *
     * @return экземпляр DatabaseValidator
     */
    public static DatabaseValidator getInstance() {
        return ourInstance;
    }

    private DatabaseValidator() {
    }

    //dnikitin меняем владельцев prstatus-ов так, юзер не использовал в качестве parent status-a статусы своих подчиненных
    /** @deprecated since 08.08.2006 ver 3.5.2
    private void validatePrstatuses() throws HibernateException, GranException {

    List prstatuses = sess.createQuery("select p from com.trackstudio.model.Prstatus p") ;
    for (Iterator it = prstatuses.iterator(); it.hasNext();) {//вытаскиваем статусы по одному
    Prstatus prst = (Prstatus) it.next();
    if (prst.getParent() != null) {
    List prstatusChain = new ArrayList();//формируем цепочку от текущего статуса до корневого
    List userChain = new ArrayList();//сюда записываем владельцев статусов цепочки prstatusChain
    Map ownerMap = new HashMap();// [владелец_статуса, цепочка из юзеров от владельца до root-a]
    while(prst.getParent() != null) {
    prstatusChain.add(prst.getId());
    userChain.add(prst.getUser().getId());
    List users = new ArrayList();
    User owner = prst.getUser();
    while(owner != null) {
    users.add(owner.getId());
    owner = owner.getManager();
    }
    ownerMap.put(prst.getUser().getId(), users);
    prst = prst.getParent();
    }

    Collections.reverse(userChain);
    Collections.reverse(prstatusChain);//инвертируем список, чтобы статусы начинались с корневого

    int i = 0;
    String previousOwner = null;
    for (Iterator iter = prstatusChain.iterator(); iter.hasNext(); i++) {//начиная с корневого статуса смотрим, не является ли владелец
    String prId = (String) iter.next();
    String ownerId = (String)userChain.get(i);//этого статуса начальником юзера владельца parent статуса.
    if(previousOwner != null) {
    List chain = (List)ownerMap.get(ownerId);
    if(!chain.contains(previousOwner)) {//Если является, то сменяем владельца у текущего статуса.
    Prstatus p = (Prstatus)sess.get(Prstatus.class, prId);
    p.setUser(new User(previousOwner));//владельцем задаем owner-а parent статуса.
    sess.save(p);
    continue;
    }
    }
    previousOwner = ownerId;

    }
    }
    }
    hu.cleanSession();

    log.debug("validatePrstatuses() done");
    }
     */
    /**
     * @throws HibernateException
     * @throws GranException
     */
    private void validateCatPermission() throws HibernateException, GranException {
        log.trace("#######");
        boolean beHandlerAlreadyAdded = TSPropertyManager.getInstance().get(canBeHandlerCatPermission) != null;
        List prstatuses = hu.getList("select p.id from com.trackstudio.model.Prstatus p");
        List categories = hu.getList("select c.id from com.trackstudio.model.Category c");
        for (Object prstatuse : prstatuses) {
            String pId = (String) prstatuse;
            for (Object category : categories) {
                String cId = (String) category;
                hu.createObject(new Cprstatus(CategoryConstants.VIEW_ALL, cId, pId));
                if (!beHandlerAlreadyAdded)//dnikitin: это мы смотрим, не были ли добавлены права be_handler в 3.1 (это делает версия 3.1.4 или 3.1.5)
                    hu.createObject(new Cprstatus(CategoryConstants.BE_HANDLER_ALL, cId, pId));
            }
        }
        hu.cleanSession();

        log.debug("validateCatPermission() done");
    }

    private void validateTaskTemplatePermission() throws HibernateException, GranException {
        log.trace("#######");
        String adminPrstatusId = ((User) hu.getObject(User.class, "1")).getPrstatus().getId();
        List templateRoles = hu.getList("from com.trackstudio.model.Rolestatus rs where rs.prstatus.id=? and rs.role='manageTaskTemplates'", adminPrstatusId);
        if (templateRoles.isEmpty()) {
            hu.createObject(new Rolestatus(adminPrstatusId, "manageTaskTemplates"));
        }
        hu.cleanSession();

        log.debug("validateTaskTemplatePermission() done");
    }

    private void validateUserUploadsPermission() throws HibernateException, GranException {
        log.trace("#######");
        String adminPrstatusId = ((User) hu.getObject(User.class, "1")).getPrstatus().getId();
        List templateRoles = hu.getList("from com.trackstudio.model.Rolestatus rs where rs.prstatus.id=? and rs.role='editUserAttachments'", adminPrstatusId);
        if (templateRoles.isEmpty()) {
            hu.createObject(new Rolestatus(adminPrstatusId, Action.viewUserAttachments.toString()));
            hu.createObject(new Rolestatus(adminPrstatusId, Action.createUserAttachments.toString()));
            hu.createObject(new Rolestatus(adminPrstatusId, Action.manageUserAttachments.toString()));
        }
        hu.cleanSession();

        log.debug("validateTaskTemplatePermission() done");
    }

    private void validateAttachmentsPermissions() throws HibernateException, GranException {
        log.trace("#######");
        Set<String> canManage = new TreeSet<String>();
        List roles = hu.getList("select r from  com.trackstudio.model.Rolestatus r where r.role='deleteUploads'");

        for (Object role : roles) {
            Rolestatus rs = (Rolestatus) role;
            canManage.add(rs.getPrstatus().getId());
            hu.deleteObject(Rolestatus.class, rs.getId());
        }
        Set<String> canCreate = new TreeSet<String>();
        List rolesCreate = hu.getList("select r from  com.trackstudio.model.Rolestatus r where r.role='createUploads' or r.role='editUploads'");
        for (Object role : rolesCreate) {
            Rolestatus rs = (Rolestatus) role;
            canCreate.add(rs.getPrstatus().getId());
            hu.deleteObject(Rolestatus.class, rs.getId());
        }
        Set<String> canView = new TreeSet<String>();
        List rolesView = hu.getList("select r from  com.trackstudio.model.Rolestatus r where r.role='viewUploads'") ;
        for (Object role : rolesView) {
            Rolestatus rs = (Rolestatus) role;
            canView.add(rs.getPrstatus().getId());
            hu.deleteObject(Rolestatus.class, rs.getId());
        }
        canView.removeAll(canCreate);
        canView.removeAll(canManage);
        canCreate.removeAll(canManage);
        for (String prstatus : canManage) {
            hu.createObject(new Rolestatus(prstatus, Action.viewTaskAttachments.toString()));
            hu.createObject(new Rolestatus(prstatus, Action.createTaskAttachments.toString()));
            hu.createObject(new Rolestatus(prstatus, Action.manageTaskAttachments.toString()));
            hu.createObject(new Rolestatus(prstatus, Action.createTaskMessageAttachments.toString()));
            hu.createObject(new Rolestatus(prstatus, Action.manageTaskMessageAttachments.toString()));

            hu.createObject(new Rolestatus(prstatus, Action.manageUserAttachments.toString()));
            hu.createObject(new Rolestatus(prstatus, Action.viewUserAttachments.toString()));
            hu.createObject(new Rolestatus(prstatus, Action.createUserAttachments.toString()));
        }
        for (String prstatus : canCreate) {
            hu.createObject(new Rolestatus(prstatus, Action.viewTaskAttachments.toString()));
            hu.createObject(new Rolestatus(prstatus, Action.createTaskAttachments.toString()));
            hu.createObject(new Rolestatus(prstatus, Action.createTaskMessageAttachments.toString()));

            hu.createObject(new Rolestatus(prstatus, Action.viewUserAttachments.toString()));
            hu.createObject(new Rolestatus(prstatus, Action.createUserAttachments.toString()));
        }
        for (String prstatus : canView) {
            hu.createObject(new Rolestatus(prstatus, Action.viewTaskAttachments.toString()));
            hu.createObject(new Rolestatus(prstatus, Action.viewUserAttachments.toString()));
        }


        hu.cleanSession();

        log.debug("validateTaskTemplatePermission() done");
    }

    private void validateTaskFilterPermissions() throws HibernateException, GranException {
        log.trace("#######");
        Set<String> canManagePublic = new TreeSet<String>();
        List rolesPublic = hu.getList("select r from  com.trackstudio.model.Rolestatus r where r.role='createPublicTaskFilter'") ;

        for (Object role : rolesPublic) {
            Rolestatus rs = (Rolestatus) role;
            canManagePublic.add(rs.getPrstatus().getId());
            hu.deleteObject(Rolestatus.class, rs.getId());
        }
        List rolesView = hu.getList("select r from  com.trackstudio.model.Rolestatus r where r.role='viewTaskFilter'") ;
        for (Object role : rolesView) {
            Rolestatus rs = (Rolestatus) role;
            rs.setRole("viewFilters");
            hu.deleteObject(Rolestatus.class, rs.getId());
        }
        Set<String> canManagePrivate = new TreeSet<String>();
        List manage = hu.getList("select r from  com.trackstudio.model.Rolestatus r where r.role='editTaskFilter' or r.role='editTaskFilterParameters' or r.role='copyTaskFilter' or r.role='createTaskFilter' or r.role='deleteTaskFilter'") ;

        for (Object role : manage) {
            Rolestatus rs = (Rolestatus) role;
            canManagePrivate.add(rs.getPrstatus().getId());
            hu.deleteObject(Rolestatus.class, rs.getId());
        }
        canManagePrivate.removeAll(canManagePublic);

        for (String prstatus : canManagePublic) {
            hu.createObject(new Rolestatus(prstatus, Action.viewFilters.toString()));
            hu.createObject(new Rolestatus(prstatus, Action.manageTaskPrivateFilters.toString()));
            hu.createObject(new Rolestatus(prstatus, Action.manageTaskPublicFilters.toString()));

        }
        for (String prstatus : canManagePrivate) {
            hu.createObject(new Rolestatus(prstatus, Action.viewFilters.toString()));
            hu.createObject(new Rolestatus(prstatus, Action.manageTaskPrivateFilters.toString()));
        }


        hu.cleanSession();

        log.debug("validateTaskFilterPermissions() done");
    }

    private void validateUserFilterPermissions() throws HibernateException, GranException {
        log.trace("#######");


        List rolesPublic = hu.getList("select r from com.trackstudio.model.Rolestatus r where r.role='createPublicUserFilter'") ;
        Set<String> canManagePublic = new TreeSet<String>();
        for (Object role : rolesPublic) {
            Rolestatus rs = (Rolestatus) role;
            canManagePublic.add(rs.getPrstatus().getId());
            hu.deleteObject(Rolestatus.class, rs.getId());
        }

        List rolesView = hu.getList("select r from com.trackstudio.model.Rolestatus r where r.role='viewUserFilter'") ;
        for (Object role : rolesView) {
            Rolestatus rs = (Rolestatus) role;
            rs.setRole("viewUserFilters");
            hu.updateObject(rs);
        }

        List manage = hu.getList("select r from com.trackstudio.model.Rolestatus r where r.role='editUserFilter' or r.role='editUserFilterParameters' or r.role='copUserFilter' or r.role='createUserFilter' or r.role='deleteUserFilter' or r.role='copyUserFilter'") ;
        Set<String> canManagePrivate = new TreeSet<String>();
        for (Object role : manage) {
            Rolestatus rs = (Rolestatus) role;
            canManagePrivate.add(rs.getPrstatus().getId());
            hu.deleteObject(Rolestatus.class, rs.getId());
        }

        canManagePrivate.removeAll(canManagePublic);

        for (String prstatus : canManagePublic) {
            hu.createObject(new Rolestatus(prstatus, Action.viewUserFilters.toString()));
            hu.createObject(new Rolestatus(prstatus, Action.manageUserPrivateFilters.toString()));
            hu.createObject(new Rolestatus(prstatus, Action.manageUserPublicFilters.toString()));

        }
        for (String prstatus : canManagePrivate) {
            hu.createObject(new Rolestatus(prstatus, Action.viewUserFilters.toString()));
            hu.createObject(new Rolestatus(prstatus, Action.manageUserPrivateFilters.toString()));

        }


        hu.cleanSession();

        log.debug("validateUserFilterPermissions() done");
    }

    private void validateWorkflowPermissions() throws HibernateException, GranException {
        log.trace("#######");


        List rolesView = hu.getList("select r from  com.trackstudio.model.Rolestatus r where r.role='viewWorkflow' or r.role='viewPriority' or r.role='viewState'  or r.role='viewTransition' or r.role='viewResolution' or r.role='viewMessageType' or r.role='viewMessageTypeTrigger' or r.role='viewWorkflowCustomization' or r.role='viewWorkflowCustomPermission' or r.role='viewMessageTypePermissions'") ;
        for (Object role : rolesView) {
            Rolestatus rs = (Rolestatus) role;
            hu.deleteObject(Rolestatus.class, rs.getId());
        }

        List manageTriggers = hu.getList("select r from  com.trackstudio.model.Rolestatus r where r.role='editMessageTypeTrigger'") ;
        Set<String> canManageTriggers = new TreeSet<String>();
        for (Object role : manageTriggers) {
            Rolestatus rs = (Rolestatus) role;
            canManageTriggers.add(rs.getPrstatus().getId());
            hu.deleteObject(Rolestatus.class, rs.getId());
        }

        List manageWorkflow = hu.getList("select r from  com.trackstudio.model.Rolestatus r where r.role='copyWorkflow' or r.role='createWorkflow' or r.role='editWorkflow' or r.role='deleteWorkflow' or r.role='editPriority' or r.role='deletePriority' or r.role='createPriority' or r.role='createState' or r.role='deleteState' or r.role='editState' or r.role='createMessageType' or r.role='editMessageType' or r.role='deleteMessageType' or r.role='editTransition' or r.role='editResolution' or r.role='createWorkflowCustomization' or r.role='editWorkflowCustomization' or r.role='editWorkflowCustomPermission' or r.role='deleteWorkflowCustomization' or r.role='editMessageTypePermissions'") ;
        Set<String> canManage = new TreeSet<String>();
        for (Object role : manageWorkflow) {
            Rolestatus rs = (Rolestatus) role;
            canManage.add(rs.getPrstatus().getId());
            hu.deleteObject(Rolestatus.class, rs.getId());
        }


        for (String prstatus : canManageTriggers) {
            hu.createObject(new Rolestatus(prstatus, Action.manageWorkflows.toString()));
        }
        for (String prstatus : canManage) {
            hu.createObject(new Rolestatus(prstatus, Action.manageWorkflows.toString()));

        }


        hu.cleanSession();

        log.debug("validateTaskFilterPermissions() done");
    }

    private void validateCategoryPermissions() throws HibernateException, GranException {
        log.trace("#######");


        List rolesView = hu.getList("select r from  com.trackstudio.model.Rolestatus r where r.role='viewCategory' or r.role='viewCategoryTrigger' or r.role='viewCategoryTemplate'  or r.role='viewCategoryPermissions'") ;
        for (Object role : rolesView) {
            Rolestatus rs = (Rolestatus) role;
            hu.deleteObject(Rolestatus.class, rs.getId());
        }

        List manageTriggers = hu.getList("select r from  com.trackstudio.model.Rolestatus r where r.role='editCategoryTrigger'") ;
        Set<String> canManageTriggers = new TreeSet<String>();
        for (Object role : manageTriggers) {
            Rolestatus rs = (Rolestatus) role;
            canManageTriggers.add(rs.getPrstatus().getId());
            hu.deleteObject(Rolestatus.class, rs.getId());
        }

        List manageWorkflow = hu.getList("select r from  com.trackstudio.model.Rolestatus r where r.role='editCategory' or r.role='deleteCategory' or r.role='createCategory' or r.role='editCategoryTemplate' or r.role='editCategoryPermissions'") ;
        Set<String> canManage = new TreeSet<String>();
        for (Object role : manageWorkflow) {
            Rolestatus rs = (Rolestatus) role;
            canManage.add(rs.getPrstatus().getId());
            hu.deleteObject(Rolestatus.class, rs.getId());
        }


        for (String prstatus : canManageTriggers) {
            hu.createObject(new Rolestatus(prstatus, Action.manageCategories.toString()));
        }
        for (String prstatus : canManage) {
            hu.createObject(new Rolestatus(prstatus, Action.manageCategories.toString()));

        }


        hu.cleanSession();

        log.debug("validateTaskFilterPermissions() done");
    }

    private void validateTaskReportPermissions() throws HibernateException, GranException {
        log.trace("#######");


        List rolesPublic = hu.getList("select r from  com.trackstudio.model.Rolestatus r where r.role='createPublicReport'") ;
        Set<String> canManagePublic = new TreeSet<String>();
        for (Object role : rolesPublic) {
            Rolestatus rs = (Rolestatus) role;
            canManagePublic.add(rs.getPrstatus().getId());
            hu.deleteObject(Rolestatus.class, rs.getId());
        }

        List rolesView = hu.getList("select r from  com.trackstudio.model.Rolestatus r where r.role='viewReport'") ;
        for (Object role : rolesView) {
            Rolestatus rs = (Rolestatus) role;
            rs.setRole("viewReports");
            hu.deleteObject(Rolestatus.class, rs.getId());
        }

        List manage = hu.getList("select r from  com.trackstudio.model.Rolestatus r where r.role='editReport' or r.role='createReport' or r.role='deleteReport'") ;
        Set<String> canManagePrivate = new TreeSet<String>();
        for (Object role : manage) {
            Rolestatus rs = (Rolestatus) role;
            canManagePrivate.add(rs.getPrstatus().getId());
            hu.deleteObject(Rolestatus.class, rs.getId());
        }

        canManagePrivate.removeAll(canManagePublic);

        for (String prstatus : canManagePublic) {
            hu.createObject(new Rolestatus(prstatus, Action.viewReports.toString()));
            hu.createObject(new Rolestatus(prstatus, Action.managePrivateReports.toString()));
            hu.createObject(new Rolestatus(prstatus, Action.managePublicReports.toString()));

        }
        for (String prstatus : canManagePrivate) {
            hu.createObject(new Rolestatus(prstatus, Action.managePrivateReports.toString()));
            hu.createObject(new Rolestatus(prstatus, Action.viewReports.toString()));
        }


        hu.cleanSession();

        log.debug("validateTaskFilterPermissions() done");
    }

    private void validateRolesPermissions() throws HibernateException, GranException {
        log.trace("#######");


        List rolesView = hu.getList("select r from  com.trackstudio.model.Rolestatus r where r.role='viewStatus'") ;
        for (Object role : rolesView) {
            Rolestatus rs = (Rolestatus) role;
            hu.deleteObject(Rolestatus.class, rs.getId());
        }

        List manage = hu.getList("select r from  com.trackstudio.model.Rolestatus r where r.role='updateState' or r.role='createState' or r.role='deleteState' or r.role='copyStatus'") ;
        Set<String> canManage = new TreeSet<String>();
        for (Object role : manage) {
            Rolestatus rs = (Rolestatus) role;
            canManage.add(rs.getPrstatus().getId());
            hu.deleteObject(Rolestatus.class, rs.getId());
        }


        for (String prstatus : canManage) {
            hu.createObject(new Rolestatus(prstatus, Action.manageRoles.toString()));
        }
        hu.cleanSession();

        log.debug("validateRolesPermissions() done");
    }

    private void validateTaskACLPermissions() throws HibernateException, GranException {
        log.trace("#######");


        List rolesView = hu.getList("select r from  com.trackstudio.model.Rolestatus r where r.role='viewAccessControl'") ;
        for (Object role : rolesView) {
            Rolestatus rs = (Rolestatus) role;
            hu.deleteObject(Rolestatus.class, rs.getId());
        }

        List manage = hu.getList("select r from  com.trackstudio.model.Rolestatus r where r.role='createAccessControl' or r.role='deleteAccessControl'") ;
        Set<String> canManage = new TreeSet<String>();
        for (Object role : manage) {
            Rolestatus rs = (Rolestatus) role;
            canManage.add(rs.getPrstatus().getId());
            hu.deleteObject(Rolestatus.class, rs.getId());
        }


        for (String prstatus : canManage) {
            hu.createObject(new Rolestatus(prstatus, Action.manageTaskACLs.toString()));
        }
        hu.cleanSession();

        log.debug("validateTaskACLPermissions() done");
    }

    private void validateViewUserPermissions() throws HibernateException, GranException {
        log.trace("#######");

        List rolesView = hu.getList("select r from  com.trackstudio.model.Rolestatus r where r.role='viewUser'") ;
        for (Object role : rolesView) {
            Rolestatus rs = (Rolestatus) role;
            hu.deleteObject(Rolestatus.class, rs.getId());
        }
        hu.cleanSession();

        log.debug("validateViewUserPermissions() done");
    }

    private void validateTaskFieldPermissions() throws HibernateException, GranException {
        log.trace("#######");


        List roles = hu.getList("select r from com.trackstudio.model.Rolestatus r where r.role='editTaskName'") ;
        for (Object role : roles) {
            Rolestatus rs = (Rolestatus) role;
            hu.deleteObject(Rolestatus.class, rs.getId());
        }
        List prstatuses = hu.getList("select r.prstatus from com.trackstudio.model.Rolestatus r where r.role='editTaskPriority'") ;
        for (Object role : prstatuses) {
            Prstatus ps = (Prstatus) role;
            hu.createObject(new Rolestatus(ps.getId(), Action.viewTaskPriority.toString()));
        }
        prstatuses = hu.getList("select r.prstatus from com.trackstudio.model.Rolestatus r where r.role='editTaskDeadline'") ;
        for (Object role : prstatuses) {
            Prstatus ps = (Prstatus) role;
            hu.createObject(new Rolestatus(ps.getId(), Action.viewTaskDeadline.toString()));
        }
        prstatuses = hu.getList("select r.prstatus from com.trackstudio.model.Rolestatus r where r.role='editTaskBudget'") ;
        for (Object role : prstatuses) {
            Prstatus ps = (Prstatus) role;
            hu.createObject(new Rolestatus(ps.getId(), Action.viewTaskBudget.toString()));
        }
        prstatuses = hu.getList("select r.prstatus from com.trackstudio.model.Rolestatus r where r.role='editTaskActualBudget'") ;
        for (Object role : prstatuses) {
            Prstatus ps = (Prstatus) role;
            hu.createObject(new Rolestatus(ps.getId(), Action.viewTaskActualBudget.toString()));
        }
        prstatuses = hu.getList("select r.prstatus from com.trackstudio.model.Rolestatus r where r.role='editTaskDescription'") ;
        for (Object role : prstatuses) {
            Prstatus ps = (Prstatus) role;
            hu.createObject(new Rolestatus(ps.getId(), Action.viewTaskDescription.toString()));
        }
        hu.cleanSession();

        log.debug("validateTaskACLPermissions() done");
    }

    private void validateUserFieldPermissions() throws HibernateException, GranException {
        log.trace("#######");


        List roles = hu.getList("select r from com.trackstudio.model.Rolestatus r where r.role='editUserName' or r.role='editUserLogin' ") ;
        for (Object role : roles) {
            Rolestatus rs = (Rolestatus) role;
            hu.deleteObject(Rolestatus.class, rs.getId());
        }
        List prstatuses = hu.getList("select r.prstatus from com.trackstudio.model.Rolestatus r where r.role='editUserCompany'") ;
        for (Object role : prstatuses) {
            Prstatus ps = (Prstatus) role;
            hu.createObject(new Rolestatus(ps.getId(), Action.viewUserCompany.toString()));
        }
        prstatuses = hu.getList("select r.prstatus from com.trackstudio.model.Rolestatus r where r.role='editUserPhone'") ;
        for (Object role : prstatuses) {
            Prstatus ps = (Prstatus) role;
            hu.createObject(new Rolestatus(ps.getId(), Action.viewUserPhone.toString()));
        }

        hu.cleanSession();

        log.debug("validateTaskACLPermissions() done");
    }

    private void validateEditUserPermissions() throws HibernateException, GranException {
        log.trace("#######");


        Set<String> canEditUserHimself = new TreeSet<String>();
        List editUserHimself = hu.getList("select r from  com.trackstudio.model.Rolestatus r where r.role='editUserHimself'") ;
        for (Object role : editUserHimself) {
            Rolestatus rs = (Rolestatus) role;
            canEditUserHimself.add(rs.getPrstatus().getId());
            hu.deleteObject(Rolestatus.class, rs.getId());
        }

        Set<String> canEditUserChildren = new TreeSet<String>();
        List editUserChildren = hu.getList("select r from  com.trackstudio.model.Rolestatus r where r.role='editUserChildren'") ;
        for (Object role : editUserChildren) {
            Rolestatus rs = (Rolestatus) role;
            canEditUserChildren.add(rs.getPrstatus().getId());
            hu.deleteObject(Rolestatus.class, rs.getId());
        }

        Set<String> canCreateUser = new TreeSet<String>();
        List createUser = hu.getList("select r from  com.trackstudio.model.Rolestatus r where r.role='createUser'") ;
        for (Object role : createUser) {
            Rolestatus rs = (Rolestatus) role;
            canCreateUser.add(rs.getPrstatus().getId());
            hu.deleteObject(Rolestatus.class, rs.getId());
        }

        Set<String> canDeleteUser = new TreeSet<String>();
        List deleteUser = hu.getList("select r from  com.trackstudio.model.Rolestatus r where r.role='deleteUser'") ;
        for (Object role : deleteUser) {
            Rolestatus rs = (Rolestatus) role;
            canDeleteUser.add(rs.getPrstatus().getId());
            hu.deleteObject(Rolestatus.class, rs.getId());
        }

        canEditUserHimself.removeAll(canEditUserChildren);
        canEditUserHimself.removeAll(canCreateUser);
        canEditUserHimself.removeAll(canDeleteUser);

        canEditUserChildren.removeAll(canCreateUser);
        canEditUserChildren.removeAll(canDeleteUser);

        canCreateUser.removeAll(canDeleteUser);

        for (String prstatus : canDeleteUser) {
            hu.createObject(new Rolestatus(prstatus, Action.deleteUser.toString()));
            hu.createObject(new Rolestatus(prstatus, Action.createUser.toString()));
            hu.createObject(new Rolestatus(prstatus, Action.editUserChildren.toString()));
            hu.createObject(new Rolestatus(prstatus, Action.editUserHimself.toString()));
        }
        for (String prstatus : canCreateUser) {
            hu.createObject(new Rolestatus(prstatus, Action.createUser.toString()));
            hu.createObject(new Rolestatus(prstatus, Action.editUserChildren.toString()));
            hu.createObject(new Rolestatus(prstatus, Action.editUserHimself.toString()));
        }
        for (String prstatus : canEditUserChildren) {
            hu.createObject(new Rolestatus(prstatus, Action.editUserChildren.toString()));
            hu.createObject(new Rolestatus(prstatus, Action.editUserHimself.toString()));
        }
        for (String prstatus : canEditUserHimself) {
            hu.createObject(new Rolestatus(prstatus, Action.editUserHimself.toString()));
        }


        Set<String> canEditUserPasswordHimself = new TreeSet<String>();
        List editUserPasswordHimself = hu.getList("select r from  com.trackstudio.model.Rolestatus r where r.role='editUserPasswordHimself'") ;
        for (Object role : editUserPasswordHimself) {
            Rolestatus rs = (Rolestatus) role;
            canEditUserPasswordHimself.add(rs.getPrstatus().getId());
            hu.deleteObject(Rolestatus.class, rs.getId());
        }

        Set<String> canEditUserChildrenPassword = new TreeSet<String>();
        List editUserChildrenPassword = hu.getList("select r from  com.trackstudio.model.Rolestatus r where r.role='editUserChildrenPassword'") ;
        for (Object role : editUserChildrenPassword) {
            Rolestatus rs = (Rolestatus) role;
            canEditUserChildrenPassword.add(rs.getPrstatus().getId());
            hu.deleteObject(Rolestatus.class, rs.getId());
        }

        canEditUserPasswordHimself.removeAll(canEditUserChildrenPassword);

        for (String prstatus : canEditUserChildrenPassword) {
            hu.createObject(new Rolestatus(prstatus, Action.editUserPasswordHimself.toString()));
            hu.createObject(new Rolestatus(prstatus, Action.editUserChildrenPassword.toString()));
        }
        for (String prstatus : canEditUserPasswordHimself) {
            hu.createObject(new Rolestatus(prstatus, Action.editUserPasswordHimself.toString()));
        }

        hu.cleanSession();

        log.debug("validateTaskACLPermissions() done");
    }

    private void validateUserACLPermissions() throws HibernateException, GranException {

        log.trace("#######");


        List rolesView = hu.getList("select r from  com.trackstudio.model.Rolestatus r where r.role='viewUserAccessControl' or r.role='viewUserAcl'") ;
        for (Object role : rolesView) {
            Rolestatus rs = (Rolestatus) role;
            hu.deleteObject(Rolestatus.class, rs.getId());
        }

        List manage = hu.getList("select r from  com.trackstudio.model.Rolestatus r where r.role='createUserAccessControl' or r.role='deleteUserAccessControl'") ;
        Set<String> canManage = new TreeSet<String>();
        for (Object role : manage) {
            Rolestatus rs = (Rolestatus) role;
            canManage.add(rs.getPrstatus().getId());
            hu.deleteObject(Rolestatus.class, rs.getId());
        }


        for (String prstatus : canManage) {
            hu.createObject(new Rolestatus(prstatus, Action.manageUserACLs.toString()));
        }
        hu.cleanSession();

        log.debug("validateUserACLPermissions() done");
    }

    private void validateImportRulesPermissions() throws HibernateException, GranException {
        log.trace("#######");


        List rolesView = hu.getList("select r from  com.trackstudio.model.Rolestatus r where r.role='viewImportTaskRule'") ;
        for (Object role : rolesView) {
            Rolestatus rs = (Rolestatus) role;
            hu.deleteObject(Rolestatus.class, rs.getId());
        }

        List manage = hu.getList("select r from  com.trackstudio.model.Rolestatus r where r.role='editImportTaskRule' or r.role='createImportTaskRule' or r.role='deleteImportTaskRule'") ;
        Set<String> canManage = new TreeSet<String>();
        for (Object role : manage) {
            Rolestatus rs = (Rolestatus) role;
            canManage.add(rs.getPrstatus().getId());
            hu.deleteObject(Rolestatus.class, rs.getId());
        }


        for (String prstatus : canManage) {
            hu.createObject(new Rolestatus(prstatus, Action.manageEmailImportRules.toString()));
        }
        hu.cleanSession();

        log.debug("validateImportRulesPermissions() done");
    }

    private void validateUserCustomizationPermissions() throws HibernateException, GranException {

        log.trace("#######");


        List rolesPermission = hu.getList("select r from  com.trackstudio.model.Rolestatus r where r.role='editUserCustomizationPermission'") ;
        Set<String> canManagePermission = new TreeSet<String>();
        for (Object role : rolesPermission) {
            Rolestatus rs = (Rolestatus) role;
            canManagePermission.add(rs.getPrstatus().getId());
            hu.deleteObject(Rolestatus.class, rs.getId());
        }

        List rolesView = hu.getList("select r from  com.trackstudio.model.Rolestatus r where r.role='viewUserCustomization' or r.role='viewUserCustomizationPermission'") ;
        for (Object role : rolesView) {
            Rolestatus rs = (Rolestatus) role;
            hu.deleteObject(Rolestatus.class, rs.getId());
        }

        List manage = hu.getList("select r from  com.trackstudio.model.Rolestatus r where r.role='editUserCustomization' or r.role='createUserCustomization' or r.role='deleteUserCustomization'") ;
        Set<String> canManage = new TreeSet<String>();
        for (Object role : manage) {
            Rolestatus rs = (Rolestatus) role;
            canManage.add(rs.getPrstatus().getId());
            hu.deleteObject(Rolestatus.class, rs.getId());
        }
        for (String prstatus : canManagePermission) {
            hu.createObject(new Rolestatus(prstatus, Action.manageUserUDFs.toString()));
        }
        for (String prstatus : canManage) {
            hu.createObject(new Rolestatus(prstatus, Action.manageUserUDFs.toString()));
        }
        hu.cleanSession();

        log.debug("validateTaskFilterPermissions() done");
    }

    private void validateTaskCustomizationPermissions() throws HibernateException, GranException {
        log.trace("#######");


        List rolesPermission = hu.getList("select r from  com.trackstudio.model.Rolestatus r where r.role='editTaskCustomizationPermission'") ;
        Set<String> canManagePermission = new TreeSet<String>();
        for (Object role : rolesPermission) {
            Rolestatus rs = (Rolestatus) role;
            canManagePermission.add(rs.getPrstatus().getId());
            hu.deleteObject(Rolestatus.class, rs.getId());
        }

        List rolesView = hu.getList("select r from  com.trackstudio.model.Rolestatus r where r.role='viewTaskCustomization' or r.role='viewTaskCustomizationPermission'") ;
        for (Object role : rolesView) {
            Rolestatus rs = (Rolestatus) role;
            hu.deleteObject(Rolestatus.class, rs.getId());
        }

        List manage = hu.getList("select r from  com.trackstudio.model.Rolestatus r where r.role='editTaskCustomization' or r.role='createTaskCustomization' or r.role='deleteTaskCustomization'") ;
        Set<String> canManage = new TreeSet<String>();
        for (Object role : manage) {
            Rolestatus rs = (Rolestatus) role;
            canManage.add(rs.getPrstatus().getId());
            hu.deleteObject(Rolestatus.class, rs.getId());
        }


        for (String prstatus : canManagePermission) {
            hu.createObject(new Rolestatus(prstatus, Action.manageTaskUDFs.toString()));


        }
        for (String prstatus : canManage) {
            hu.createObject(new Rolestatus(prstatus, Action.manageTaskUDFs.toString()));

        }


        hu.cleanSession();

        log.debug("validateTaskFilterPermissions() done");
    }

    private void validateRegistrationPermissions() throws HibernateException, GranException {
        log.trace("#######");


        List rolesView = hu.getList("select r from  com.trackstudio.model.Rolestatus r where r.role='viewRegistration'") ;
        for (Object role : rolesView) {
            Rolestatus rs = (Rolestatus) role;
            hu.deleteObject(Rolestatus.class, rs.getId());
        }

        List manage = hu.getList("select r from  com.trackstudio.model.Rolestatus r where r.role='editRegistration' or r.role='createRegistration' or r.role='deleteRegistration' or r.role='copyRegistration'") ;
        Set<String> canManage = new TreeSet<String>();
        for (Object role : manage) {
            Rolestatus rs = (Rolestatus) role;
            canManage.add(rs.getPrstatus().getId());
            hu.deleteObject(Rolestatus.class, rs.getId());
        }
        for (String prstatus : canManage) {
            hu.createObject(new Rolestatus(prstatus, Action.manageRegistrations.toString()));
        }
        hu.cleanSession();

        log.debug("validateRegistrationPermissions() done");
    }

    private void validateSubscriptionPermissions() throws HibernateException, GranException {
        log.trace("#######");

        String adminPrstatusId = ((User) hu.getObject(User.class, "1")).getPrstatus().getId();
        List templateRoles = hu.getList("from com.trackstudio.model.Rolestatus rs where rs.prstatus.id=? and rs.role='managePublicSubscriptions'", adminPrstatusId) ;
        if (templateRoles.isEmpty()) {
            hu.createObject(new Rolestatus(adminPrstatusId, Action.manageEmailSchedules.toString()));
        }
        {
            List rolesView = hu.getList("select r from  com.trackstudio.model.Rolestatus r where r.role='viewSubscription' or r.role='viewNotification' or r.role='viewUserNotification' or r.role='viewUserSubscription'") ;
            for (Object role : rolesView) {
                Rolestatus rs = (Rolestatus) role;
                hu.deleteObject(Rolestatus.class, rs.getId());
            }
        }
        Set<String> canManage = new TreeSet<String>();
        {
            List manage = hu.getList("select r from  com.trackstudio.model.Rolestatus r where r.role='editSubscription' or r.role='createSubscription' or r.role='deleteSubscription' or r.role='editNotification' or r.role='deleteNotification'") ;

            for (Object role : manage) {
                Rolestatus rs = (Rolestatus) role;
                canManage.add(rs.getPrstatus().getId());
                hu.deleteObject(Rolestatus.class, rs.getId());
            }
        }
        for (String prstatus : canManage) {
            hu.createObject(new Rolestatus(prstatus, Action.manageEmailSchedules.toString()));
        }


        hu.cleanSession();

        log.debug("validateSubscriptionPermissions() done");
    }

    private void validateCopyCutPermissions() throws HibernateException, GranException {
        log.trace("#######");

        Set<String> canCopyRec = new TreeSet<String>(); {
            List roles = hu.getList("select r from  com.trackstudio.model.Rolestatus r where r.role='recursivelyCopyTask'") ;
            for (Object role : roles) {
                Rolestatus rs = (Rolestatus) role;
                canCopyRec.add(rs.getPrstatus().getId());
                hu.deleteObject(Rolestatus.class, rs.getId());
            }
        }
        Set<String> canCopy = new TreeSet<String>();
        {
            List rolesCopy = hu.getList("select r from  com.trackstudio.model.Rolestatus r where r.role='cutCopyPasteTask'") ;

            for (Object role : rolesCopy) {
                Rolestatus rs = (Rolestatus) role;
                canCopy.add(rs.getPrstatus().getId());

            }
        }
        canCopyRec.removeAll(canCopy);

        for (String prstatus : canCopyRec) {
            hu.createObject(new Rolestatus(prstatus, Action.cutCopyPasteTask.toString()));
        }
        hu.cleanSession();

        log.debug("validateTaskTemplatePermission() done");
    }

    private void validateOldPermission() throws HibernateException, GranException {
        log.trace("#######");

        List roles = hu.getList("select r from  com.trackstudio.model.Rolestatus r where r.role='viewScript' or r.role='editScript' or r.role='deleteScript' or r.role='copyScript' or r.role='createScript' or r.role='viewTemplate' or r.role='editTemplate' or r.role='createTemplate' or r.role='deleteTemplate' or r.role='copyTemplate' or r.role='viewTask' or r.role='editTask' or r.role='viewSimilar' or r.role='editTaskUdf' or r.role='editUserUdf' or r.role='viewTaskUdf' or r.role='viewUserUdf'") ;
        for (Object role : roles) {
            Rolestatus rs = (Rolestatus) role;
            hu.deleteObject(Rolestatus.class, rs.getId());
        }
        hu.cleanSession();

        log.debug("validateTaskTemplatePermission() done");
    }

    private void validateOperationPermission() throws HibernateException, GranException {
        log.trace("#######");

        List roles = hu.getList("select r from  com.trackstudio.model.Rolestatus r where r.role='viewMessage' or r.role='createMessage'") ;
        for (Object role : roles) {
            Rolestatus rs = (Rolestatus) role;
            hu.deleteObject(Rolestatus.class, rs.getId());
        }
        List templateRoles = hu.getList("from com.trackstudio.model.Rolestatus rs where rs.role='deleteMessage'") ;
        for (Object role : templateRoles) {
            Rolestatus rs = (Rolestatus) role;
            rs.setRole(Action.deleteOperations.toString());
            hu.updateObject(rs);
        }
        hu.cleanSession();

        log.debug("validateTaskTemplatePermission() done");
    }

    private void validateBulkProcessing() throws HibernateException, GranException {
        log.trace("#######");

        String adminPrstatusId = ((User) hu.getObject(User.class, "1")).getPrstatus().getId();
        List templateRoles = hu.getList("from com.trackstudio.model.Rolestatus rs where rs.prstatus.id=? and rs.role='taskBulkProcessing'", adminPrstatusId) ;
        if (templateRoles.isEmpty()) {
            hu.createObject(new Rolestatus(adminPrstatusId, Action.bulkProcessingTask.toString()));
        }
        hu.cleanSession();

        log.debug("validateTaskTemplatePermission() done");
    }

    private void validateOldPermission2() throws HibernateException, GranException {
        log.trace("#######");

        List roles = hu.getList("select r from  com.trackstudio.model.Rolestatus r where r.role='deleteTask' or r.role='createTask' or r.role='editTask'") ;
        for (Object role : roles) {
            Rolestatus rs = (Rolestatus) role;
            hu.deleteObject(Rolestatus.class, rs.getId());
        }
        hu.cleanSession();

        log.debug("validateTaskTemplatePermission() done");
    }

    private void validateUdfPermission() throws HibernateException, GranException {
        log.trace("#######");

        List prstatuses = hu.getList("select p.id from com.trackstudio.model.Prstatus p") ;
        List udfs = hu.getList("select u from com.trackstudio.model.Udf u") ;
        for (Iterator it = prstatuses.iterator(); it.hasNext();) {
            String pId = (String) it.next();
            for (Iterator it2 = udfs.iterator(); it2.hasNext();) {
                Udf udf = (Udf) it2.next();
                hu.createObject(new Uprstatus(CategoryConstants.VIEW_ALL, udf.getId(), pId));
                hu.createObject(new Uprstatus(CategoryConstants.EDIT_ALL, udf.getId(), pId));
                if (udf.getUdfsource().getWorkflow() != null) {
                    hu.createObject(new Uprstatus(UdfConstants.STATUS_VIEW_ALL, udf.getId(), pId));
                    hu.createObject(new Uprstatus(UdfConstants.STATUS_EDIT_ALL, udf.getId(), pId));
                }
            }
        }

        log.debug("validateUdfPermission() done");
    }

    private boolean renameAttachment(String attId, File f ){
        try{
            return f.renameTo(new File(f.getParent(), attId));
        } catch (SecurityException s){
            log.error("ERROR UPDATE UPLOAD FILE : ", s);
        }
        return false;
    }

    private void validateAttachments() throws HibernateException, GranException {
        log.trace("#######");

        List attaches = hu.getList("from com.trackstudio.model.Attachment a") ;
        log.debug("CONVERT UPLOAD FILE");
        for (Iterator it = attaches.iterator(); it.hasNext();) {
            Attachment n = (Attachment) it.next();
            log.debug("name file :" + n.getName());
            createAndRenameAttachment(n);
        }
        hu.cleanSession();

        log.debug("validateNotifications() done");
    }


    private void checkedAttachments() throws HibernateException, GranException {
        log.info("#######");
        List<Attachment> attaches = hu.getList("select attach from com.trackstudio.model.Attachment as attach");
        for (Attachment attachment : attaches) {
            String dirPath = AttachmentManager.getAttachmentDirPath(attachment.getTask()!=null ? attachment.getTask().getId(): null, attachment.getUser()!=null? attachment.getUser().getId(): null, false);
            File dir =  new File(dirPath);
            if (!dir.exists()) {
                createAndRenameAttachment(attachment);
            }
            if (dir.exists()) {
                for (File file : dir.listFiles()) {
                    if (file.getName().contains(".")) {
                        for (Object o : attaches) {
                            Attachment attach = (Attachment) o;
                            if (attach.getName().equals(file.getName())) {
                                boolean result = renameAttachment(attach.getId(), file);
                                log.debug("result conver : " + result + "; attachemnt name : " + attach.getName());
                            }
                        }
                    }
                }
            }
        }
        hu.cleanSession();

        log.debug("validateNotifications() done");
    }

    private void createAndRenameAttachment(Attachment n) {
        File f = AttachmentManager.getInstance().getAttachmentFile(n.getTask()!=null ? n.getTask().getId(): null, n.getUser()!=null? n.getUser().getId(): null, removeLocaleCharacters(n.getName(), false, false), false);
        if (f != null) {
            boolean result = renameAttachment(n.getId(), f);
            log.debug("result convert : " + result);
        }
    }

    private void validatePrstatusRoles() throws HibernateException, GranException {
        log.trace("#######");


        List prstatuses = hu.getList("select p.id from com.trackstudio.model.Prstatus p, com.trackstudio.model.User u where u.prstatus=p.id and u.id='1'") ;
        if (!prstatuses.isEmpty())
            hu.createObject(new Rolestatus((String) prstatuses.get(0), "csvImport"));
        hu.cleanSession();


        prstatuses = hu.getList("select p.id from com.trackstudio.model.Prstatus p, com.trackstudio.model.Rolestatus r where r.prstatus=p.id and r.role='createAccessControl'") ;
        for (Object prstatuse1 : prstatuses) {
            String pId = (String) prstatuse1;
            hu.createObject(new Rolestatus(pId, "viewUserAccessControl"));
            hu.createObject(new Rolestatus(pId, "createUserAccessControl"));
            hu.createObject(new Rolestatus(pId, "deleteUserAccessControl"));
        }
        hu.cleanSession();

        //add viewCategoryTrigger roles
        prstatuses = hu.getList("select p.id from com.trackstudio.model.Prstatus p, com.trackstudio.model.Rolestatus r where r.prstatus=p.id and (r.role='viewCategory' or r.role='viewCategoryPermissions' or r.role='editCategoryPermissions')") ;
        for (Object prstatuse : prstatuses) {
            String pId = (String) prstatuse;
            hu.createObject(new Rolestatus(pId, "viewCategoryTrigger"));
        }
        hu.cleanSession();

        //add editCategoryTrigger roles
        prstatuses = hu.getList("select p.id from com.trackstudio.model.Prstatus p, com.trackstudio.model.Rolestatus r where r.prstatus=p.id and (r.role='editCategory' or r.role='createCategory' or r.role='deleteCategory')") ;
        for (Iterator it = prstatuses.iterator(); it.hasNext();) {
            String pId = (String) it.next();
            hu.createObject(new Rolestatus(pId, "editCategoryTrigger"));
        }
        hu.cleanSession();

        //add viewMessageTypeTrigger roles
        prstatuses = hu.getList("select p.id from com.trackstudio.model.Prstatus p, com.trackstudio.model.Rolestatus r where r.prstatus=p.id and (r.role='viewMessageType' or r.role='viewTransition' or r.role='editTransition' or r.role='viewResolution' or r.role='editResolution' or r.role='viewMessageTypePermissions' or r.role='editMessageTypePermissions')") ;
        for (Iterator it = prstatuses.iterator(); it.hasNext();) {
            String pId = (String) it.next();
            hu.createObject(new Rolestatus(pId, "viewMessageTypeTrigger"));
        }
        hu.cleanSession();

        //add editMessageTypeTrigger roles
        prstatuses = hu.getList("select p.id from com.trackstudio.model.Prstatus p, com.trackstudio.model.Rolestatus r where r.prstatus=p.id and (r.role='editMessageType' or r.role='createMessageType' or r.role='deleteMessageType')") ;
        for (Iterator it = prstatuses.iterator(); it.hasNext();) {
            String pId = (String) it.next();
            hu.createObject(new Rolestatus(pId, "editMessageTypeTrigger"));
        }
        hu.cleanSession();

        //add recursivelyCopyTask roles
        prstatuses = hu.getList("select p.id from com.trackstudio.model.Prstatus p, com.trackstudio.model.Rolestatus r where r.prstatus=p.id and (r.role='viewTaskList' or r.role='deleteTask')") ;
        for (Iterator it = prstatuses.iterator(); it.hasNext();) {
            String pId = (String) it.next();
            hu.createObject(new Rolestatus(pId, "recursivelyCopyTask"));
        }
        hu.cleanSession();

        //add view/editResolution roles
        prstatuses = hu.getList("select p.id from com.trackstudio.model.Prstatus p, com.trackstudio.model.Rolestatus r where r.prstatus=p.id and r.role='viewMessageType'") ;
        for (Iterator it = prstatuses.iterator(); it.hasNext();) {
            String pId = (String) it.next();
            hu.createObject(new Rolestatus(pId, "viewResolution"));
        }
        hu.cleanSession();

        prstatuses = hu.getList("select p.id from com.trackstudio.model.Prstatus p, com.trackstudio.model.Rolestatus r where r.prstatus=p.id and r.role='editMessageType'") ;
        for (Iterator it = prstatuses.iterator(); it.hasNext();) {
            String pId = (String) it.next();
            hu.createObject(new Rolestatus(pId, "editResolution"));
        }
        hu.cleanSession();

        //add viewXXXCustomizationPermission roles
        prstatuses = hu.getList("select p.id from com.trackstudio.model.Prstatus p, com.trackstudio.model.Rolestatus r where r.prstatus=p.id and r.role='viewWorkflowCustomization'") ;
        for (Object prstatuse3 : prstatuses) {
            String pId = (String) prstatuse3;
            hu.createObject(new Rolestatus(pId, "viewWorkflowCustomPermission"));
        }
        hu.cleanSession();

        prstatuses = hu.getList("select p.id from com.trackstudio.model.Prstatus p, com.trackstudio.model.Rolestatus r where r.prstatus=p.id and r.role='editWorkflowCustomization'") ;
        for (Object prstatuse2 : prstatuses) {
            String pId = (String) prstatuse2;
            hu.createObject(new Rolestatus(pId, "editWorkflowCustomPermission"));
        }
        hu.cleanSession();

        prstatuses = hu.getList("select p.id from com.trackstudio.model.Prstatus p, com.trackstudio.model.Rolestatus r where r.prstatus=p.id and r.role='viewTaskCustomization'") ;
        for (Iterator it = prstatuses.iterator(); it.hasNext();) {
            String pId = (String) it.next();
            hu.createObject(new Rolestatus(pId, "viewTaskCustomizationPermission"));
        }
        hu.cleanSession();

        prstatuses = hu.getList("select p.id from com.trackstudio.model.Prstatus p, com.trackstudio.model.Rolestatus r where r.prstatus=p.id and r.role='editTaskCustomization'") ;
        for (Iterator it = prstatuses.iterator(); it.hasNext();) {
            String pId = (String) it.next();
            hu.createObject(new Rolestatus(pId, "editTaskCustomizationPermission"));
        }
        hu.cleanSession();

        prstatuses = hu.getList("select p.id from com.trackstudio.model.Prstatus p, com.trackstudio.model.Rolestatus r where r.prstatus=p.id and r.role='viewUserCustomization'") ;
        for (Iterator it = prstatuses.iterator(); it.hasNext();) {
            String pId = (String) it.next();
            hu.createObject(new Rolestatus(pId, "viewUserCustomizationPermission"));
        }
        hu.cleanSession();

        prstatuses = hu.getList("select p.id from com.trackstudio.model.Prstatus p, com.trackstudio.model.Rolestatus r where r.prstatus=p.id and r.role='editUserCustomization'") ;
        for (Iterator it = prstatuses.iterator(); it.hasNext();) {
            String pId = (String) it.next();
            hu.createObject(new Rolestatus(pId, "editUserCustomizationPermission"));
        }
        hu.cleanSession();

        //remove viewXXXList
        List roles = hu.getList("select r from  com.trackstudio.model.Rolestatus r where r.role='viewUserFilterList'") ;
        for (Iterator it = roles.iterator(); it.hasNext();) {
            Rolestatus rs = (Rolestatus) it.next();
            rs.setRole("viewUserFilter");
            hu.updateObject(rs);
        }
        hu.cleanSession();

        roles = hu.getList("select r from  com.trackstudio.model.Rolestatus r where r.role='editUserManager'") ;
        for (Iterator it = roles.iterator(); it.hasNext();) {
            Rolestatus rs = (Rolestatus) it.next();
            rs.setRole("cutPasteUser");
            hu.updateObject(rs);
        }
        hu.cleanSession();

        roles = hu.getList("select r from  com.trackstudio.model.Rolestatus r where r.role='editTaskParent'") ;
        for (Iterator it = roles.iterator(); it.hasNext();) {
            Rolestatus rs = (Rolestatus) it.next();
            rs.setRole("cutCopyPasteTask");
            hu.updateObject(rs);
        }
        hu.cleanSession();

        roles = hu.getList("select r from  com.trackstudio.model.Rolestatus r where r.role='viewStatusList'") ;
        for (Iterator it = roles.iterator(); it.hasNext();) {
            Rolestatus rs = (Rolestatus) it.next();
            rs.setRole("viewStatus");
            hu.updateObject(rs);
        }
        hu.cleanSession();

        roles = hu.getList("select r from  com.trackstudio.model.Rolestatus r where r.role='viewTemplateList'") ;
        for (Iterator it = roles.iterator(); it.hasNext();) {
            Rolestatus rs = (Rolestatus) it.next();
            rs.setRole("viewTemplate");
            hu.updateObject(rs);
        }
        hu.cleanSession();

        roles = hu.getList("select r from  com.trackstudio.model.Rolestatus r where r.role='viewRegistrationList'") ;
        for (Iterator it = roles.iterator(); it.hasNext();) {
            Rolestatus rs = (Rolestatus) it.next();
            rs.setRole("viewRegistration");
            hu.updateObject(rs);
        }
        hu.cleanSession();

        roles = hu.getList("select r from  com.trackstudio.model.Rolestatus r where r.role='viewScriptList'") ;
        for (Iterator it = roles.iterator(); it.hasNext();) {
            Rolestatus rs = (Rolestatus) it.next();
            rs.setRole("viewScript");
            hu.updateObject(rs);
        }
        hu.cleanSession();

        roles = hu.getList("select r from  com.trackstudio.model.Rolestatus r where r.role='viewTaskFilterList'") ;
        for (Iterator it = roles.iterator(); it.hasNext();) {
            Rolestatus rs = (Rolestatus) it.next();
            rs.setRole("viewTaskFilter");
            hu.updateObject(rs);
        }
        hu.cleanSession();

        roles = hu.getList("select r from  com.trackstudio.model.Rolestatus r where r.role='viewReportList'") ;
        for (Iterator it = roles.iterator(); it.hasNext();) {
            Rolestatus rs = (Rolestatus) it.next();
            rs.setRole("viewReport");
            hu.updateObject(rs);
        }
        hu.cleanSession();

        roles = hu.getList("select r from  com.trackstudio.model.Rolestatus r where r.role='viewCategoryList'") ;
        for (Iterator it = roles.iterator(); it.hasNext();) {
            Rolestatus rs = (Rolestatus) it.next();
            rs.setRole("viewCategory");
            hu.updateObject(rs);
        }
        hu.cleanSession();

        roles = hu.getList("select r from  com.trackstudio.model.Rolestatus r where r.role='viewWorkflowList'") ;
        for (Iterator it = roles.iterator(); it.hasNext();) {
            Rolestatus rs = (Rolestatus) it.next();
            rs.setRole("viewWorkflow");
            hu.updateObject(rs);
        }
        hu.cleanSession();

        //update notification and subscription roles
        roles = hu.getList("select r from  com.trackstudio.model.Rolestatus r where r.role='notifyTaskFilter'") ;
        for (Iterator it = roles.iterator(); it.hasNext();) {
            Rolestatus rs = (Rolestatus) it.next();
            rs.setRole("deleteNotification");
            hu.updateObject(rs);
        }
        hu.cleanSession();

        roles = hu.getList("select r from  com.trackstudio.model.Rolestatus r where r.role='subscribeTaskFilter'") ;
        for (Iterator it = roles.iterator(); it.hasNext();) {
            Rolestatus rs = (Rolestatus) it.next();
            rs.setRole("deleteSubscription");
            hu.updateObject(rs);
        }
        hu.cleanSession();

        roles = hu.getList("select r from  com.trackstudio.model.Rolestatus r where r.role='viewTaskList'") ;
        for (Iterator it = roles.iterator(); it.hasNext();) {
            Rolestatus rs = (Rolestatus) it.next();
            List list = hu.getList("select r from  com.trackstudio.model.Rolestatus r where r.role='viewTask' and r.prstatus=?", rs.getPrstatus().getId());
            if (list.isEmpty()) {
                rs.setRole("viewTask");
                hu.updateObject(rs);
            }
        }
        hu.cleanSession();

        roles = hu.getList("select r from  com.trackstudio.model.Rolestatus r where r.role='viewUserList'") ;
        for (Iterator it = roles.iterator(); it.hasNext();) {
            Rolestatus rs = (Rolestatus) it.next();
            List list = hu.getList("select r from  com.trackstudio.model.Rolestatus r where r.role='viewUser' and r.prstatus=?", rs.getPrstatus().getId());
            if (list.isEmpty()) {
                rs.setRole("viewUser");
                hu.updateObject(rs);
            }

        }
        hu.cleanSession();

        //update importTask roles
        roles = hu.getList("select r from  com.trackstudio.model.Rolestatus r where r.role='importTask'") ;
        for (Iterator it = roles.iterator(); it.hasNext();) {
            Rolestatus rs = (Rolestatus) it.next();
            rs.setRole("deleteImportTaskRule");
            hu.updateObject(rs);
        }


        hu.cleanSession();

        log.debug("validatePrstatusPermission() done");
    }

    private void validateRoleEditLogin() throws HibernateException, GranException {
        log.debug("#######");

        List roles = hu.getList("select r from  com.trackstudio.model.Rolestatus r where r.role='editUserLogin'") ;
        if (roles == null || roles.isEmpty()) {
            List prstatuses = hu.getList("select p.id from com.trackstudio.model.Prstatus p, com.trackstudio.model.Rolestatus r where r.prstatus=p.id and r.role='editUserName'") ;
            for (Iterator it = prstatuses.iterator(); it.hasNext();) {
                String pId = (String) it.next();
                hu.createObject(new Rolestatus(pId, "editUserLogin"));
            }
        }
        hu.cleanSession();


        log.debug("validateRoleEditLogin() done");
    }

    private void validateNotifications() throws HibernateException, GranException {
        log.trace("#######");

        List notifications = hu.getList("from com.trackstudio.model.Notification p") ;
        ArrayList checksums = new ArrayList();
        for (Iterator it = notifications.iterator(); it.hasNext();) {
            Notification n = (Notification) it.next();
            String cs = n.getFilter().getId() + n.getTask().getId() + (n.getUser().getUser() != null ? n.getUser().getUser().getId() : n.getUser().getPrstatus().getId()) + (n.getTemplate() != null ? n.getTemplate() : "");
            if (checksums.contains(cs))
                hu.deleteObject(Notification.class, n.getId());
            else
                checksums.add(cs);
        }
        hu.cleanSession();

        log.debug("validateNotifications() done");
    }

    private void validateLastUpdateDate() throws HibernateException, GranException {
        log.trace("#######");

        List users = hu.getList("from com.trackstudio.model.User p") ;
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(System.currentTimeMillis());
        for (Object user : users) {
            User n = (User) user;
            n.setLastLogonDate(now);
            n.setPasswordChangedDate(now);
            hu.updateObject(n);
        }
        hu.cleanSession();

        log.debug("validateNotifications() done");
    }

    private void validateUserLocales() throws HibernateException, GranException {
        log.trace("#######");

        List users = hu.getList("from com.trackstudio.model.User p") ;
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(System.currentTimeMillis());
        for (Object user : users) {
            User n = (User) user;
            if (n.getLocale() == null || n.getLocale().equals(""))
                n.setLocale(Config.getInstance().getDefaultLocale());
            if (n.getTimezone() == null || n.getTimezone().equals(""))
                n.setTimezone(Config.getInstance().getDefaultTimezone());
        }
        hu.cleanSession();

        log.debug("validateNotifications() done");
    }

    private String getLongtext(String id) throws GranException {

        Longtext ltext = (Longtext) hu.getObject(Longtext.class, id);
        if (ltext != null) {
            String text = ltext.getValue();
            if (text.length() < 1800) // sometimes 2000 chars there doesn't work, use some threshould
                return text; // short string

            StringBuffer result = new StringBuffer(10000);
            result.append(text);
            List csv = hu.getList("select new java.lang.String(l.value) from com.trackstudio.model.Longtext l where l.reference=? order by l.order", id) ;
            for (Iterator it = csv.iterator(); it.hasNext();)
                result.append(it.next());
            return result.toString();
        } else return null;
    }

    private void validateScriptsAndTemplates() throws HibernateException, GranException {
        log.trace("#######");


        List csv = hu.getList("select p from  com.trackstudio.model.Property p, com.trackstudio.model.Longtext l where p.id=l.id") ;
        for (Object aCsv : csv) {
            Property p = (Property) aCsv;
            String scriptLongText = p.getId();
            String script = getLongtext(scriptLongText);
            String fileName = removeLocaleCharacters(p.getName(), false, true);
            String scriptType = p.getValue();
            hu.deleteObject(p.getClass(), p.getId());
            if (scriptType.equals("1") || scriptType.equals("2")) {
                fileName = PluginCacheManager.getInstance().createScript(fileName, scriptType.equals("1") ? PluginType.TASK_CUSTOM_FIELD_VALUE : PluginType.USER_CUSTOM_FIELD_VALUE, script);
                List udfScripts = hu.getList("from  com.trackstudio.model.Udf udf where udf.script=?", scriptLongText) ;
                for (Object udfScript : udfScripts) {
                    Udf udf = (Udf) udfScript;

                    udf.setScript(fileName);

                    hu.updateObject(udf);
                }


            } else if (scriptType.equals("3") || scriptType.equals("4")) {
                fileName = PluginCacheManager.getInstance().createScript(fileName, PluginType.TASK_CUSTOM_FIELD_LOOKUP, script);
                fileName = PluginCacheManager.getInstance().createScript(fileName, scriptType.equals("3") ? PluginType.TASK_CUSTOM_FIELD_LOOKUP : PluginType.USER_CUSTOM_FIELD_LOOKUP, script);
                List udfScripts = hu.getList("from  com.trackstudio.model.Udf udf where udf.lookupscript=?", scriptLongText) ;
                for (Object udfScript : udfScripts) {
                    Udf udf = (Udf) udfScript;
                    udf.setLookupscript(fileName);

                    hu.updateObject(udf);
                }
            } else if (scriptType.equals("5")) {
                fileName = PluginCacheManager.getInstance().createScript(fileName, PluginType.AFTER_ADD_MESSAGE, script);
                List triggers = hu.getList("select t from  com.trackstudio.model.Trigger t, com.trackstudio.model.Mstatus m where m.trigger=t and t.after=?", scriptLongText) ;
                for (Object trigger : triggers) {
                    Trigger t = (Trigger) trigger;
                    t.setInsteadOf(fileName);
                    hu.updateObject(t);
                }

            } else if (scriptType.equals("6")) {
                fileName = PluginCacheManager.getInstance().createScript(fileName, PluginType.BEFORE_ADD_MESSAGE, script);
                List triggers = hu.getList("select t from  com.trackstudio.model.Trigger t, com.trackstudio.model.Mstatus m where m.trigger=t and t.before=?", scriptLongText) ;
                for (Object trigger : triggers) {
                    Trigger t = (Trigger) trigger;
                    t.setBefore(fileName);
                    hu.updateObject(t);
                }

            } else if (scriptType.equals("7")) {
                fileName = PluginCacheManager.getInstance().createScript(fileName, PluginType.INSTEAD_OF_ADD_MESSAGE, script);
                List triggers = hu.getList("select t from  com.trackstudio.model.Trigger t, com.trackstudio.model.Mstatus m where m.trigger=t and t.insteadOf=?", scriptLongText) ;
                for (Object trigger : triggers) {
                    Trigger t = (Trigger) trigger;
                    t.setInsteadOf(fileName);
                    hu.updateObject(t);
                }

            } else if (scriptType.equals("8")) {
                fileName = PluginCacheManager.getInstance().createScript(fileName, PluginType.AFTER_CREATE_TASK, script);
                List triggers = hu.getList("select t from com.trackstudio.model.Trigger t,  com.trackstudio.model.Category cat where cat.crTrigger=t and t.after=?", scriptLongText) ;
                for (Object trigger : triggers) {
                    Trigger t = (Trigger) trigger;
                    t.setAfter(fileName);
                    hu.updateObject(t);
                }

            } else if (scriptType.equals("9")) {
                fileName = PluginCacheManager.getInstance().createScript(fileName, PluginType.BEFORE_CREATE_TASK, script);
                List triggers = hu.getList("select t from com.trackstudio.model.Trigger t,  com.trackstudio.model.Category cat where cat.crTrigger=t and t.before=?", scriptLongText) ;
                for (Object trigger : triggers) {
                    Trigger t = (Trigger) trigger;
                    t.setBefore(fileName);
                    hu.updateObject(t);
                }

            } else if (scriptType.equals("10")) {
                fileName = PluginCacheManager.getInstance().createScript(fileName, PluginType.INSTEAD_OF_CREATE_TASK, script);
                List triggers = hu.getList("select t from com.trackstudio.model.Trigger t,  com.trackstudio.model.Category cat where cat.crTrigger=t and t.insteadOf=?", scriptLongText) ;
                for (Object trigger : triggers) {
                    Trigger t = (Trigger) trigger;
                    t.setInsteadOf(fileName);
                    hu.updateObject(t);
                }

            } else if (scriptType.equals("11")) {
                fileName = PluginCacheManager.getInstance().createScript(fileName, PluginType.AFTER_EDIT_TASK, script);
                List triggers = hu.getList("select t from com.trackstudio.model.Trigger t,  com.trackstudio.model.Category cat where cat.updTrigger=t and t.after=?", scriptLongText) ;
                for (Object trigger : triggers) {
                    Trigger t = (Trigger) trigger;
                    t.setAfter(fileName);
                    hu.updateObject(t);
                }

            } else if (scriptType.equals("12")) {
                fileName = PluginCacheManager.getInstance().createScript(fileName, PluginType.BEFORE_EDIT_TASK, script);
                List triggers = hu.getList("select t from com.trackstudio.model.Trigger t,  com.trackstudio.model.Category cat where cat.updTrigger=t and t.before=?", scriptLongText) ;
                for (Object trigger : triggers) {
                    Trigger t = (Trigger) trigger;
                    t.setBefore(fileName);
                    hu.updateObject(t);
                }

            } else if (scriptType.equals("13")) {
                fileName = PluginCacheManager.getInstance().createScript(fileName, PluginType.INSTEAD_OF_EDIT_TASK, script);
                List triggers = hu.getList("select t from com.trackstudio.model.Trigger t,  com.trackstudio.model.Category cat where cat.updTrigger=t and t.insteadOf=?", scriptLongText) ;
                for (Object trigger : triggers) {
                    Trigger t = (Trigger) trigger;
                    t.setInsteadOf(fileName);
                    hu.updateObject(t);
                }

            } /*else if (scriptType.equals("14"))
                PluginCacheManager.getInstance().createScript(fileName, PluginType.CSV_IMPORT, script);
                */
            Longtext obj = (Longtext) hu.getObject(Longtext.class, scriptLongText);
            hu.deleteObject(Longtext.class, obj.getId());
        }
        hu.cleanSession();

        log.debug("validateNotifications() done");
    }

    private void validateReports() throws HibernateException, GranException {
        log.debug("#######");


        // Delete export status
        List roles = hu.getList("select r from  com.trackstudio.model.Rolestatus r where r.role='exportTask'") ;
        for (Object role : roles) {
            Rolestatus rs = (Rolestatus) role;
            hu.deleteObject(Rolestatus.class, rs.getId());
        }
        hu.cleanSession();


        log.debug("validateNotifications() done");
    }

    private Boolean isMsgKey(Map<String, String> msgKeysMap, String key) {
        for (String msgKey : msgKeysMap.keySet()) {
            if (key.equals(msgKey) || key.indexOf(msgKey) != -1)
                return true;
        }
        return false;
    }

    private String convertOldKeyToNew(Map<String, String> keysMap, String key) {
        String prefix = FValue.getValuePrefix(key);
        if (!prefix.equals("") && prefix.length() > 0) {
            key = key.substring(prefix.length());
        }
        String val = keysMap.get(key);
        if (val != null)
            return prefix + val;
        else
            return prefix + key;
    }

    private void validateFilters() throws HibernateException, GranException {
        log.trace("#######");

        List<String> filters = hu.getList("select f.id from  com.trackstudio.model.Filter f") ;
        if (filters.size() > 0) {

            Map<String, String> msgKeysMap = new HashMap<String, String>();
            msgKeysMap.put("messageview", FieldMap.MESSAGEVIEW.getFilterKey());
            msgKeysMap.put("submittermsg", FieldMap.MSG_SUSER_NAME.getFilterKey());
            msgKeysMap.put("datemsg", FieldMap.MSG_SUBMITDATE.getFilterKey());
            msgKeysMap.put("typemsg", TaskFValue.MSG_TYPE);
            msgKeysMap.put("handlermsg", FieldMap.MSG_HUSER_NAME.getFilterKey());
            msgKeysMap.put("resolutionmsg", FieldMap.MSG_RESOLUTION.getFilterKey());
            msgKeysMap.put("budgetmsg", FieldMap.MSG_ABUDGET.getFilterKey());
            msgKeysMap.put("textmsg", TaskFValue.MSG_TEXT);

            Map<String, String> taskKeysMap = new HashMap<String, String>();
            taskKeysMap.put("taskdescription", FieldMap.TASK_DESCRIPTION.getFilterKey());
            taskKeysMap.put("messagecount", FieldMap.TASK_MESSAGECOUNT.getFilterKey());

            for (String id : filters) {
                List<String> use = new ArrayList<String>();
                List<String> hide = new ArrayList<String>();
                Boolean messageview = false;
                String messageviewcount = "";
                TaskFValue map = new TaskFValue();
                List<Fvalue> fvalues = hu.getList("select fvalue from com.trackstudio.model.Fvalue as fvalue where fvalue.filter=?", id) ;
                for (Fvalue fv : fvalues) {
                    String key = fv.getKey();
                    String val = fv.getValue();
                    if (!key.equals(FValue.SORTORDER)) {
                        if (key.equals("use")) {
                            List<String> useTmp = FValue.parseFilterValue(val);
                            for (String u : useTmp) {
                                if (u.equals("messageview"))
                                    messageview = true;
                                if (!u.equals("bulkprocessingtool")) {
                                    if (isMsgKey(msgKeysMap, u)) {
                                        use.add(convertOldKeyToNew(msgKeysMap, u));
                                    } else {
                                        use.add(convertOldKeyToNew(taskKeysMap, u));
                                    }
                                }
                            }
                        } else if (key.equals("hide")) {
                            List<String> hideTmp = FValue.parseFilterValue(val);
                            for (String u : hideTmp) {
                                if (isMsgKey(msgKeysMap, u)) {
                                    hide.add(convertOldKeyToNew(msgKeysMap, u));
                                } else {
                                    hide.add(convertOldKeyToNew(taskKeysMap, u));
                                }
                            }
                        } else {
                            if (key.indexOf("budget") != -1) {
                                String prefix = FValue.getValuePrefix(val);
                                String budget = val;
                                if (prefix.length() > 0 && prefix.length() < val.length())
                                    budget = val.substring(prefix.length());
                                if (budget != null && budget.trim().length() > 0) {
                                    try {
                                        val = String.valueOf((long) (Double.valueOf(budget) * 3600));
                                    } catch (Exception e) {
                                        if (budget.trim().lastIndexOf("_") != -1 && !budget.trim().endsWith("_")) {
                                            val = String.valueOf((long) (Double.valueOf(budget.substring(budget.lastIndexOf("_"))) * 3600));
                                        } else {
                                            val = "";
                                        }
                                    }
                                }
                            }
                            if (key.indexOf("messageview") != -1) {
                                messageviewcount = val;
                            } else {
                                List<String> list = FValue.parseFilterValue(val);
                                if (isMsgKey(msgKeysMap, key)) {
                                    key = convertOldKeyToNew(msgKeysMap, key);
                                } else {
                                    key = convertOldKeyToNew(taskKeysMap, key);
                                }
                                if (list.isEmpty()) {
                                    map.putItem(key, val);
                                } else {
                                    map.put(key, list);
                                }
                            }
                        }
                        hu.deleteObject(fv.getClass(), fv.getId());
                    }
                }
                if (messageview) {
                    Integer count;
                    try {
                        count = Integer.valueOf(messageviewcount);
                    } catch (Exception e) {
                        count = -1;
                    }
                    if (count == 1) {
                        map.putItem(FieldMap.MESSAGEVIEW.getFilterKey(), "1");
                    } else
                    if (count > 1 && count <= 3) {
                        map.putItem(FieldMap.MESSAGEVIEW.getFilterKey(), "3");
                    } else if (count > 3 && count <= 5) {
                        map.putItem(FieldMap.MESSAGEVIEW.getFilterKey(), "5");
                    } else if (count > 5 && count <= 10) {
                        map.putItem(FieldMap.MESSAGEVIEW.getFilterKey(), "10");
                    } else if (count > 10) {
                        map.putItem(FieldMap.MESSAGEVIEW.getFilterKey(), "-1");
                    } else {
                        map.putItem(FieldMap.MESSAGEVIEW.getFilterKey(), "0");
                    }
                } else {
                    map.putItem(FieldMap.MESSAGEVIEW.getFilterKey(), "0");
                }
                use.removeAll(hide);
                map.put(FValue.DISPLAY, use);
                map.remove(FieldMap.TASK_SHORTNAME.getFilterKey());
                map.remove(FieldMap.FULLPATH.getFilterKey());
                map.remove(FieldMap.TASK_DESCRIPTION.getFilterKey());
                map.remove("messagefilter");

                for (String key : map.keySet()) {
                    if (key != null && key.trim().length() > 0) {
                        List<String> list = map.getOriginValues(key);
                        if (list != null && !list.isEmpty()) {
                            for (String s : list) {
                                if (s != null && s.trim().length() > 0) {
                                    hu.createObject(new Fvalue(id, key, s));
                                }
                            }
                        }
                    }
                }
            }
        }
        hu.cleanSession();

        log.debug("validateFilters() done");
    }

    private void validateMailimport() throws HibernateException, GranException {
        log.trace("#######");
        List<MailImport> mailimports = hu.getList("select m from  com.trackstudio.model.MailImport m") ;
        if (mailimports.size() > 0) {

            for (MailImport mi : mailimports) {
                List<Mstatus> defaults = hu.getList("select m from com.trackstudio.model.Mstatus as m, com.trackstudio.model.MailImport as mi, com.trackstudio.model.Category as c where mi.category=c.id and c.workflow=m.workflow and m.preferences='T' and mi.id=?", mi.getId()) ;
                for (Mstatus fv : defaults) {
                    mi.setMstatus(fv);
                    hu.updateObject(mi);
                }
            }
        }
        hu.cleanSession();

        log.debug("validateFilters() done");
    }

    private void validateUprstatus() throws HibernateException, GranException {
        log.trace("#######");

        List uprstatuses = hu.getList("select u from com.trackstudio.model.Uprstatus u") ;
        if (uprstatuses.size() > 0) {
            for (Object uprstatusO : uprstatuses) {
                Uprstatus uprstatus = (Uprstatus) uprstatusO;
                if (uprstatus.getType().startsWith("MSTATUS_EDIT_")) {
                    // set any can edit this UDF in this operation
                    String mstatusId = uprstatus.getType().substring("MSTATUS_EDIT_".length());
                    if (hu.getList("select m from com.trackstudio.model.Mstatus m where m.id=? ", mstatusId) .size() > 0) {
                        if (mstatusId.equals("ALL")) {
                            Workflow w = uprstatus.getUdf().getUdfsource().getWorkflow();
                            if (w != null) {
                                List ms = hu.getList("select m.id from  com.trackstudio.model.Mstatus m where m.workflow=?", w.getId()) ;
                                for (Object id : ms) {
                                    hu.createObject(new Umstatus(uprstatus.getUdf().getId(), id.toString(), "E"));
                                }
                            }
                        } else {
                            hu.createObject(new Umstatus(uprstatus.getUdf().getId(), mstatusId, "E"));
                        }
                    }
                    hu.deleteObject(uprstatus.getClass(), uprstatus.getId());
                } else if (uprstatus.getType().startsWith("MSTATUS_VIEW_")) {
                    // set any can view this UDF in this operation
                    String mstatusId = uprstatus.getType().substring("MSTATUS_VIEW_".length());
                    if (hu.getList("select m from com.trackstudio.model.Mstatus m where m.id=? ", mstatusId).size() > 0) {
                        if (mstatusId.equals("ALL")) {
                            Workflow w = uprstatus.getUdf().getUdfsource().getWorkflow();
                            if (w != null) {
                                List ms = hu.getList("select m.id from  com.trackstudio.model.Mstatus m where m.workflow=?", w.getId()) ;
                                for (Object id : ms) {
                                    hu.createObject(new Umstatus(uprstatus.getUdf().getId(), id.toString(), "V"));
                                }
                            }
                        } else {
                            hu.createObject(new Umstatus(uprstatus.getUdf().getId(), mstatusId, "V"));
                        }
                    }
                    hu.deleteObject(uprstatus.getClass(), uprstatus.getId());
                }
            }
        }
        hu.cleanSession();

        log.debug("validateFilters() done");
    }

    /**
     * Метод, который непосредственно выполняет валидацию БД
     *
     * @throws Exception при необходимости
     */
    public void validate() throws Exception {
        log.trace("#######");
        log.info("Checking database integrity");

        try {
            ////3.1->3.2////
            String dbV = TSPropertyManager.getInstance().get(dbVersion);
            if (dbV != null && dbV.equals("4.0*")) {
                validateViewUserPermissions();
                validateEditUserPermissions();
                validateTaskTemplatePermission();
                validateScriptsAndTemplates();
                validateUserUploadsPermission();
                validateSubscriptionPermissions();
                validateRegistrationPermissions();
                validateTaskFilterPermissions();
                validateUserFilterPermissions();
                validateWorkflowPermissions();
                validateCategoryPermissions();
                validateRolesPermissions();
                validateTaskReportPermissions();
                validateImportRulesPermissions();
                validateTaskCustomizationPermissions();
                validateUserCustomizationPermissions();
                validateTaskACLPermissions();
                validateUserACLPermissions();
                validateCopyCutPermissions();
                validateAttachmentsPermissions();
                validateBulkProcessing();
                validateOldPermission();
                validateTaskFieldPermissions();
                validateUserFieldPermissions();
                validateOldPermission2();
                validateUprstatus();
                validateFilters();
                validateMailimport();
                validateOperationPermission();
                TSPropertyManager.getInstance().set(dbVersion, "4.0");
            } else if (dbV != null && dbV.equals("3.2*")) {
                //validatePrstatuses();
                validateCatPermission();
                validatePrstatusRoles();
                validateUdfPermission();
                validateNotifications();
                validateLastUpdateDate();
                validateUserLocales();
                TSPropertyManager.getInstance().set(dbVersion, "3.2");
            } else {
                validateReports();
                validateRoleEditLogin();
            }

            if (TSPropertyManager.getInstance().get("UPLOADNAMES") == null){
                validateAttachments();
                TSPropertyManager.getInstance().set("UPLOADNAMES", "fixed");
            }
            checkedAttachments();
            checkUnexistedAttachments();
//            if (validatePrimaryKey())
            TSPropertyManager.getInstance().set("trackstudio.validatePrimaryKey", "true");
//            else TSPropertyManager.getInstance().set("trackstudio.validatePrimaryKey", "false");
        } catch (Exception ex) {
            log.error("Exception ", ex);
            throw new GranException(ex, "UPGRADE FAILED.");
        }
    }

    private boolean validatePrimaryKey() throws GranException {
        log.trace("#######");
        log.debug("TrackStudio checks  constrains keys in your Database, Skip this error. Start");
        try {
            hu.executeDML("update com.trackstudio.model.User user set user.id='2' where user.id=?", "1");
            hu.executeDML("update com.trackstudio.model.User user set user.id='1' where user.id=?", "2");

            return false;
        } catch (HibernateException e) {
            return true;
        } finally {
            log.debug("TrackStudio checks  constrains keys in your Database, Skip this error. Finish");
        }
    }

    private void checkFileInFileSystemByEntityInDatabase() throws GranException {
        File file = new File(Config.getProperty("trackstudio.uploadDir") + "/" + leaks);
        if (!file.exists()) {
            StringBuilder sb = new StringBuilder();
            String uploadDir = Config.getProperty("trackstudio.uploadDir");
            File mainDir = new File(uploadDir);
            if (mainDir.exists() && mainDir.isDirectory()) {
                File[] files = mainDir.listFiles();
                if (files != null) {
                    for (File preId : files) {
                        File[] taskFiles = preId.listFiles();
                        if (taskFiles != null) {
                            for (File postId : taskFiles) {
                                String entityId = preId.getName().concat(postId.getName());
                                boolean exist;
                                if (entityId.startsWith("user")) {
                                    exist = UserRelatedManager.getInstance().isUserExists(entityId.substring(4));
                                } else {
                                    exist = TaskRelatedManager.getInstance().isTaskExists(entityId);
                                }
                                if (exist) {
                                    File[] postFiles = postId.listFiles();
                                    if (postFiles != null) {
                                        for (File postFile : postFiles) {
                                            boolean attachExists = AttachmentManager.getInstance().existAttachment(postFile.getName());
                                            if (!attachExists) {
                                                sb.append("File exists but attachment does not exist in TrackStudio : " + postFile.getAbsolutePath());
                                            }
                                        }
                                    }
                                } else {
                                    sb.append("Unexisted task/user has uploaded folder : " + postId.getAbsolutePath());
                                }
                            }
                        }
                    }
                }
                try {
                    if (file.createNewFile()) {
                        saveText(sb.toString(), file);
                    }
                } catch (IOException e) {
                    throw new GranException(e);
                }
            }
        }
    }

    private final static String missed= "missed.txt";
    private final static String leaks= "leaks.txt";

    private void saveText(String text, File file) {
        try {
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file)));
            pw.print(text);
            pw.close();
        } catch (Exception e) {
            log.error("Error", e);
        }
    }

    private void checkEntityInDatabaseToFileInFileSystem() throws GranException {
        File file = new File(Config.getProperty("trackstudio.uploadDir") + "/" + missed);
        if (!file.exists()) {
            StringBuilder sb = new StringBuilder();
            List<Attachment> attaches = hu.getList("select attach from com.trackstudio.model.Attachment as attach");
            for (Attachment attachment : attaches) {
                String dirPath = AttachmentManager.getAttachmentDirPath(attachment.getTask()!=null ? attachment.getTask().getId(): null, attachment.getUser()!=null? attachment.getUser().getId(): null, false);
                File dir =  new File(dirPath);
                if (!dir.exists()) {
                    sb.append("An attachment exists in TrackStudio but folder does not exist in file system! Attachment : " + attachment + " folder : " + dir.getAbsolutePath());
                    sb.append("\n");
                } else {
                    File attachmentFile = new File(dir.getAbsolutePath() + "/" + attachment.getId());
                    if (!attachmentFile.exists()) {
                        sb.append("Attachment exists in TrackStudio but it does not have a real file in file system! Attachment : " + attachment + " folder : " + attachmentFile.getAbsolutePath());
                        sb.append("\n");
                    }
                }
            }
            try {
                if (file.createNewFile()) {
                    saveText(sb.toString(), file);
                }
            } catch (IOException e) {
                throw new GranException(e);
            }
        }
    }

    private void checkUnexistedAttachments() throws GranException {
        checkFileInFileSystemByEntityInDatabase();
        checkEntityInDatabaseToFileInFileSystem();
    }
}
