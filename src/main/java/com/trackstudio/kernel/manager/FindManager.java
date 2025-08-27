/*
 * @(#)FindManager.java
 * Copyright 2009 TrackStudio, Inc. All rights reserved.
 */
package com.trackstudio.kernel.manager;

import java.util.List;
import java.util.Locale;

import org.hibernate.Hibernate;

import com.trackstudio.exception.CantFindObjectException;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.model.Acl;
import com.trackstudio.model.Attachment;
import com.trackstudio.model.Bookmark;
import com.trackstudio.model.Category;
import com.trackstudio.model.Catrelation;
import com.trackstudio.model.Cprstatus;
import com.trackstudio.model.CurrentFilter;
import com.trackstudio.model.Filter;
import com.trackstudio.model.Fvalue;
import com.trackstudio.model.Longtext;
import com.trackstudio.model.MailImport;
import com.trackstudio.model.Mprstatus;
import com.trackstudio.model.Mstatus;
import com.trackstudio.model.Notification;
import com.trackstudio.model.Priority;
import com.trackstudio.model.Property;
import com.trackstudio.model.Prstatus;
import com.trackstudio.model.Registration;
import com.trackstudio.model.Report;
import com.trackstudio.model.Resolution;
import com.trackstudio.model.Rolestatus;
import com.trackstudio.model.Status;
import com.trackstudio.model.Subscription;
import com.trackstudio.model.Task;
import com.trackstudio.model.Template;
import com.trackstudio.model.Transition;
import com.trackstudio.model.Trigger;
import com.trackstudio.model.Udf;
import com.trackstudio.model.Udflist;
import com.trackstudio.model.Udfsource;
import com.trackstudio.model.Udfval;
import com.trackstudio.model.Umstatus;
import com.trackstudio.model.Uprstatus;
import com.trackstudio.model.User;
import com.trackstudio.model.Usersource;
import com.trackstudio.model.Workflow;

import net.jcip.annotations.Immutable;

/**
 * Класс FindManager содержит методы для поиска объектов по их ID.
 */
@Immutable
public class FindManager extends KernelManager {

    private static final String className = "FindManager.";
    private static final FindManager instance = new FindManager();
    private static final LockManager lockManager = LockManager.getInstance();
    /**
     * Конструктор по умолчанию
     */
    private FindManager() {
    }

    /**
     * Возвращает экземпляр текущего класса
     *
     * @return Экземпляр FindManager
     */
    protected static FindManager getInstance() {
        return instance;
    }

    /**
     * Ищет Property по ID
     *
     * @param id ID искомого объекта
     * @return Property
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Property
     */
    public Property findProperty(String id) throws GranException {
        if (id == null)
            return null;
        boolean r = lockManager.acquireConnection(className);
        try {
            Property obj = (Property) hu.getObject(Property.class, id);
            if (obj == null)
                throw new CantFindObjectException(new Object[]{Property.class, id});
            return obj;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Ищет Attachment по ID
     *
     * @param id ID искомого объекта
     * @return Attachment
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Attachment
     */
    public Attachment findAttachment(String id) throws CantFindObjectException, GranException {
        if (id == null)
            return null;
        boolean r = lockManager.acquireConnection(className);
        try {
            Attachment obj = (Attachment) hu.getObject(Attachment.class, id);
            if (obj == null)
                throw new CantFindObjectException(new Object[]{Attachment.class, id});
            return obj;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Ищет Bookmark по ID
     *
     * @param id ID искомого объекта
     * @return Bookmark
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Bookmark
     */
    public Bookmark findBookmark(String id) throws GranException {
        if (id == null) return null;
        boolean r = lockManager.acquireConnection(className);
        try {
            Bookmark obj = (Bookmark) hu.getObject(Bookmark.class, id);
            if (obj == null)
                throw new CantFindObjectException(new Object[]{Bookmark.class, id});
            return obj;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Ищет Category по ID
     *
     * @param id ID искомого объекта
     * @return Category
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Category
     */
    public Category findCategory(String id) throws GranException {
        if (id == null)
            return null;
        boolean r = lockManager.acquireConnection(className);
        try {
            Category cat = (Category) hu.getObject(Category.class, id);
            if (cat == null)
                throw new CantFindObjectException(new Object[]{Category.class, id});
            Hibernate.initialize(cat.getCrTrigger());
            Hibernate.initialize(cat.getUpdTrigger());
            return cat;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Ищет Trigger по ID
     *
     * @param id ID искомого объекта
     * @return Trigger
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Trigger
     */
    public Trigger findTrigger(String id) throws GranException {
        if (id == null)
            return null;
        boolean r = lockManager.acquireConnection(className);
        try {
            Trigger tr = (Trigger) hu.getObject(Trigger.class, id);
            if (tr == null)
                throw new CantFindObjectException(new Object[]{Trigger.class, id});
            return tr;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Ищет Catrelation по ID
     *
     * @param id ID искомого объекта
     * @return Catrelation
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Catrelation
     */
    public Catrelation findCatrelation(String id) throws GranException {
        if (id == null)
            return null;
        boolean r = lockManager.acquireConnection(className);
        try {
            Catrelation obj = (Catrelation) hu.getObject(Catrelation.class, id);
            if (obj == null)
                throw new CantFindObjectException(new Object[]{Catrelation.class, id});
            return obj;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Ищет Cprstatus по ID
     *
     * @param id ID искомого объекта
     * @return Cprstatus
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Cprstatus
     */
    public Cprstatus findCprstatus(String id) throws GranException {
        if (id == null)
            return null;
        boolean r = lockManager.acquireConnection(className);
        try {
            Cprstatus obj = (Cprstatus) hu.getObject(Cprstatus.class, id);
            if (obj == null)
                throw new CantFindObjectException(new Object[]{Cprstatus.class, id});
            return obj;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Ищет Template по ID
     *
     * @param id ID искомого объекта
     * @return Template
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Template
     */
    public Template findTemplate(String id) throws GranException {
        if (id == null)
            return null;
        boolean r = lockManager.acquireConnection(className);
        try {

            Template obj = (Template) hu.getObject(Template.class, id);
            if (obj == null)
                throw new CantFindObjectException(new Object[]{Template.class, id});
            return obj;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Ищет Filter по ID
     *
     * @param id ID искомого объекта
     * @return Filter
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Filter
     */
    public Filter findFilter(String id) throws GranException {
        if (id == null)
            return null;
        boolean r = lockManager.acquireConnection(className);
        try {

            Filter obj = (Filter) hu.getObject(Filter.class, id);
            if (obj == null)
                throw new CantFindObjectException(new Object[]{Filter.class, id});
            return obj;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Ищет Fvalue по ID
     *
     * @param id ID искомого объекта
     * @return Fvalue
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Fvalue
     */
    public Fvalue findFvalue(String id) throws GranException {
        if (id == null)
            return null;
        boolean r = lockManager.acquireConnection(className);
        try {

            Fvalue obj = (Fvalue) hu.getObject(Fvalue.class, id);
            if (obj == null)
                throw new CantFindObjectException(new Object[]{Fvalue.class, id});
            return obj;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Ищет Longtext по ID
     *
     * @param id ID искомого объекта
     * @return Longtext
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Longtext
     */
    public Longtext findLongtext(String id) throws GranException {
        if (id == null)
            return null;
        boolean r = lockManager.acquireConnection(className);
        try {

            Longtext obj = (Longtext) hu.getObject(Longtext.class, id);
            if (obj == null)
                throw new CantFindObjectException(new Object[]{Longtext.class, id});
            return obj;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Ищет MailImport по ID
     *
     * @param id ID искомого объекта
     * @return MailImport
     * @throws GranException при необходимости
     * @see com.trackstudio.model.MailImport
     */
    public MailImport findMailImport(String id) throws GranException {
        if (id == null)
            return null;
        boolean r = lockManager.acquireConnection(className);
        try {

            MailImport obj = (MailImport) hu.getObject(MailImport.class, id);
            if (obj == null)
                throw new CantFindObjectException(new Object[]{MailImport.class, id});
            return obj;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Ищет Mprstatus по ID
     *
     * @param id ID искомого объекта
     * @return Mprstatus
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Mprstatus
     */
    public Mprstatus findMprstatus(String id) throws GranException {
        if (id == null)
            return null;
        boolean r = lockManager.acquireConnection(className);
        try {

            Mprstatus obj = (Mprstatus) hu.getObject(Mprstatus.class, id);
            if (obj == null)
                throw new CantFindObjectException(new Object[]{Mprstatus.class, id});
            return obj;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Ищет Uprstatus по ID
     *
     * @param id ID искомого объекта
     * @return Uprstatus
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Uprstatus
     */
    public Uprstatus findUprstatus(String id) throws GranException {
        if (id == null)
            return null;
        boolean r = lockManager.acquireConnection(className);
        try {

            Uprstatus obj = (Uprstatus)hu.getObject(Uprstatus.class, id);
            if (obj == null)
                throw new CantFindObjectException(new Object[]{Uprstatus.class, id});
            return obj;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Ищет Umstatus по ID
     *
     * @param id ID искомого объекта
     * @return Umstatus
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Umstatus
     */
    public Umstatus findUmstatus(String id) throws GranException {
        if (id == null)
            return null;
        boolean r = lockManager.acquireConnection(className);
        try {

            Umstatus obj = (Umstatus) hu.getObject(Umstatus.class, id);
            if (obj == null)
                throw new CantFindObjectException(new Object[]{Umstatus.class, id});
            return obj;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Ищет Mstatus по ID
     *
     * @param id ID искомого объекта
     * @return Mstatus
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Mstatus
     */
    public Mstatus findMstatus(String id) throws GranException {
        if (id == null)
            return null;
        boolean r = lockManager.acquireConnection(className);
        try {

            Mstatus obj = (Mstatus) hu.getObject(Mstatus.class, id);
            if (obj == null)
                throw new CantFindObjectException(new Object[]{Mstatus.class, id});
            Hibernate.initialize(obj.getTrigger());
            return obj;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Ищет Notification по ID
     *
     * @param id ID искомого объекта
     * @return Notification
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Notification
     */
    public Notification findNotification(String id) throws GranException {
        if (id == null)
            return null;
        boolean r = lockManager.acquireConnection(className);
        try {

            Notification obj = (Notification) hu.getObject(Notification.class, id);
            if (obj == null)
                throw new CantFindObjectException(new Object[]{Notification.class, id});
            Hibernate.initialize(obj.getUser());
            if (obj.getUser().getUser() != null) {
                Hibernate.initialize(obj.getUser().getUser());
            } else {
                Hibernate.initialize(obj.getUser().getPrstatus());
            }
            return obj;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Ищет Priority по ID
     *
     * @param id ID искомого объекта
     * @return Priority
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Priority
     */
    public Priority findPriority(String id) throws GranException {
        if (id == null)
            return null;
        boolean r = lockManager.acquireConnection(className);
        try {

            Priority obj = (Priority) hu.getObject(Priority.class, id);
            if (obj == null)
                throw new CantFindObjectException(new Object[]{Priority.class, id});
            Hibernate.initialize(obj.getWorkflow());
            return obj;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Ищет Prstatus по ID
     *
     * @param id ID искомого объекта
     * @return Prstatus
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Prstatus
     */
    public Prstatus findPrstatus(String id) throws GranException {
        if (id == null)
            return null;
        boolean r = lockManager.acquireConnection(className);
        try {

            Prstatus obj = (Prstatus) hu.getObject(Prstatus.class, id);
            if (obj == null)
                throw new CantFindObjectException(new Object[]{Prstatus.class, id});
            return obj;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Use this method only for HibernateUtil
     * <p/>
     * Ищет Task по ID
     *
     * @param id ID искомого объекта
     * @return Task
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Task
     */
    public Task findTask(String id) throws GranException {
        if (id == null)
            return null;
        boolean r = lockManager.acquireConnection(className);
        try {

            Task obj = (Task) hu.getObject(Task.class, id);
            if (obj == null)
                throw new CantFindObjectException(new Object[]{Task.class, id});
            Hibernate.initialize(obj.getStatus());
            return obj;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Use this method only for HibernateUtil
     * <p/>
     * Ищет User по ID
     *
     * @param id ID искомого объекта
     * @return User
     * @throws GranException при необходимости
     * @see com.trackstudio.model.User
     */
    public User findUser(String id) throws GranException {
        if (id == null)
            return null;
        boolean r = lockManager.acquireConnection(className);
        try {

            User obj = (User) hu.getObject(User.class, id);
            if (obj == null)
                throw new CantFindObjectException(new Object[]{User.class, id});
            String defaultLocale = obj.getLocale() == null || obj.getLocale().equals("default") ? Locale.getDefault().getLanguage() : obj.getLocale();
            obj.setLocale(defaultLocale);
            return obj;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Ищет Acl по ID
     *
     * @param id ID искомого объекта
     * @return Acl
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Acl
     */
    public Acl findAcl(String id) throws GranException {
        if (id == null)
            return null;
        boolean r = lockManager.acquireConnection(className);
        try {
            Acl obj = (Acl) hu.getObject(Acl.class, id);
            if (obj == null)
                throw new CantFindObjectException(new Object[]{Acl.class, id});
            Hibernate.initialize(obj.getUsersource());
            if (obj.getUsersource().getUser() != null) {
                Hibernate.initialize(obj.getUsersource().getUser());
            } else {
                Hibernate.initialize(obj.getUsersource().getPrstatus());
            }
            return obj;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Ищет CurrentFilter по ID
     *
     * @param id ID искомого объекта
     * @return CurrentFilter
     * @throws GranException при необходимости
     * @see com.trackstudio.model.CurrentFilter
     */
    public CurrentFilter findCurrentFilter(String id) throws GranException {
        if (id == null)
            return null;
        boolean r = lockManager.acquireConnection(className);
        try {

            CurrentFilter obj = (CurrentFilter) hu.getObject(CurrentFilter.class, id);
            if (obj == null)
                throw new CantFindObjectException(new Object[]{CurrentFilter.class, id});
            return obj;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Ищет Report по ID
     *
     * @param id ID искомого объекта
     * @return Report
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Report
     */
    public Report findReport(String id) throws GranException {
        if (id == null)
            return null;
        boolean r = lockManager.acquireConnection(className);
        try {

            Report obj = (Report) hu.getObject(Report.class, id);
            if (obj == null)
                throw new CantFindObjectException(new Object[]{Report.class, id});
            return obj;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Ищет Resolution по ID
     *
     * @param id ID искомого объекта
     * @return Resolution
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Resolution
     */
    public Resolution findResolution(String id) throws GranException {
        if (id == null)
            return null;
        boolean r = lockManager.acquireConnection(className);
        try {

            Resolution obj = (Resolution) hu.getObject(Resolution.class, id);
            if (obj == null)
                throw new CantFindObjectException(new Object[]{Resolution.class, id});
            Hibernate.initialize(obj.getMstatus());
            Hibernate.initialize(obj.getMstatus().getWorkflow());
            return obj;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Ищет Rolestatus по ID
     *
     * @param id ID искомого объекта
     * @return Rolestatus
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Rolestatus
     */
    public Rolestatus findRolestatus(String id) throws GranException {
        if (id == null)
            return null;
        boolean r = lockManager.acquireConnection(className);
        try {

            Rolestatus obj = (Rolestatus) hu.getObject(Rolestatus.class, id);
            if (obj == null)
                throw new CantFindObjectException(new Object[]{Rolestatus.class, id});
            return obj;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Ищет Status по ID
     *
     * @param id ID искомого объекта
     * @return Status
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Status
     */
    public Status findStatus(String id) throws GranException {
        if (id == null)
            return null;
        boolean r = lockManager.acquireConnection(className);
        try {

            Status obj = (Status) hu.getObject(Status.class, id);
            if (obj == null)
                throw new CantFindObjectException(new Object[]{Status.class, id});
            Hibernate.initialize(obj.getWorkflow());
            return obj;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Ищет Subscription по ID
     *
     * @param id ID искомого объекта
     * @return Subscription
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Subscription
     */
    public Subscription findSubscription(String id) throws GranException {
        if (id == null)
            return null;
        boolean r = lockManager.acquireConnection(className);
        try {

            Subscription obj = (Subscription) hu.getObject(Subscription.class, id);
            if (obj == null)
                throw new CantFindObjectException(new Object[]{Subscription.class, id});
            Hibernate.initialize(obj.getUser());
            Hibernate.initialize(obj.getTask());
            Hibernate.initialize(obj.getTask().getChildSet());
            Hibernate.initialize(obj.getFilter());
            if (obj.getUser().getUser() != null) {
                Hibernate.initialize(obj.getUser().getUser());
            } else {
                Hibernate.initialize(obj.getUser().getPrstatus());
            }
            return obj;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Ищет Transition по ID
     *
     * @param id ID искомого объекта
     * @return Transition
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Transition
     */
    public Transition findTransition(String id) throws GranException {
        if (id == null)
            return null;
        boolean r = lockManager.acquireConnection(className);
        try {

            Transition obj = (Transition) hu.getObject(Transition.class, id);
            if (obj == null)
                throw new CantFindObjectException(new Object[]{Transition.class, id});
            return obj;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Ищет Udf по ID
     *
     * @param id ID искомого объекта
     * @return Udf
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Udf
     */
    public Udf findUdf(String id) throws GranException {
        if (id == null)
            return null;
        boolean r = lockManager.acquireConnection(className);
        try {

            Udf obj = (Udf) hu.getObject(Udf.class, id);
            if (obj == null)
                throw new CantFindObjectException(new Object[]{Udf.class, id});
            Hibernate.initialize(obj.getUdfsource());
            Hibernate.initialize(obj.getUdfsource().getWorkflow());
            return obj;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    public Udf findUdfNull(String id) throws GranException {
        if (id == null)
            return null;
        boolean r = lockManager.acquireConnection(className);
        try {

            return (Udf) hu.getObject(Udf.class, id);
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Проверяет существоваение Udf
     *
     * @param id ID искомого объекта
     * @return TRUE если есть, FALSE если нет
     * @throws GranException при необходимости
     */
    public boolean isUdfExists(String id) throws GranException {
        if (id == null)
            return false;
        boolean r = lockManager.acquireConnection(className);
        try {

            try {
                Udf obj = (Udf) hu.getObject(Udf.class, id);
                if (obj == null)
                    return false;
                //throw new CantFindObjectException(new Object[]{, id});
            } catch (Exception e) {
                return false;
            }
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
        return true;
    }

    /**
     * Ищет Udflist по ID
     *
     * @param id ID искомого объекта
     * @return Udflist
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Udflist
     */
    public Udflist findUdflist(String id) throws GranException {
        if (id == null)
            return null;
        boolean r = lockManager.acquireConnection(className);
        try {

            Udflist obj = (Udflist) hu.getObject(Udflist.class, id);
            if (obj == null)
                throw new CantFindObjectException(new Object[]{Udflist.class, id});
            return obj;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Ищет Udfsource по ID
     *
     * @param id ID искомого объекта
     * @return Udfsource
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Udfsource
     */
    public Udfsource findUdfsource(String id) throws GranException {
        if (id == null)
            return null;
        boolean r = lockManager.acquireConnection(className);
        try {

            Udfsource obj = (Udfsource) hu.getObject(Udfsource.class, id);
            if (obj == null)
                throw new CantFindObjectException(new Object[]{Udfsource.class, id});
            return obj;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Ищет Usersource по ID
     *
     * @param id ID искомого объекта
     * @return Usersource
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Usersource
     */
    public Usersource findUsersource(String id) throws GranException {
        if (id == null)
            return null;
        boolean r = lockManager.acquireConnection(className);
        try {

            Usersource obj = (Usersource) hu.getObject(Usersource.class, id);
            if (obj == null)
                throw new CantFindObjectException(new Object[]{Usersource.class, id});
            return obj;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Ищет Udfval по ID
     *
     * @param id ID искомого объекта
     * @return Udfval
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Udfval
     */
    public Udfval findUdfval(String id) throws GranException {
        if (id == null)
            return null;
        boolean r = lockManager.acquireConnection(className);
        try {

            Udfval obj = (Udfval) hu.getObject(Udfval.class, id);
            if (obj == null)
                throw new CantFindObjectException(new Object[]{Udfval.class, id});
            Hibernate.initialize(obj.getUdflist());
            return obj;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Ищет Workflow по ID
     *
     * @param id ID искомого объекта
     * @return Workflow
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Workflow
     */
    public Workflow findWorkflow(String id) throws GranException {
        if (id == null)
            return null;
        boolean r = lockManager.acquireConnection(className);
        try {

            Workflow obj = (Workflow) hu.getObject(Workflow.class, id);
            if (obj == null)
                throw new CantFindObjectException(new Object[]{Workflow.class, id});
            return obj;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Ищет Registration по ID
     *
     * @param id ID искомого объекта
     * @return Registration
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Registration
     */
    public Registration findRegistration(String id) throws GranException {
        if (id == null)
            return null;
        boolean r = lockManager.acquireConnection(className);
        try {

            Registration obj = (Registration) hu.getObject(Registration.class, id);
            if (obj == null)
                throw new CantFindObjectException(new Object[]{Registration.class, id});
            return obj;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Ищет PluginType по ID
     *
     * @param id ID искомого объекта
     * @return PluginType
     * @throws GranException при необходимости
     * @see com.trackstudio.kernel.cache.PluginType
     */
    public com.trackstudio.kernel.cache.PluginType findScriptType(String id) throws GranException {
        if (id == null)
            return null;
        boolean r = lockManager.acquireConnection(className);
        try {

            com.trackstudio.kernel.cache.PluginType obj = (com.trackstudio.kernel.cache.PluginType) hu.getObject(com.trackstudio.kernel.cache.PluginType.class, id);
            if (obj == null)
                throw new CantFindObjectException(new Object[]{com.trackstudio.kernel.cache.PluginType.class, id});
            return obj;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    public String buildName(String name, String udfId) throws GranException  {
        List list = hu.getList("select udf.caption || ' (' || workflow.name || ')' from " + Udf.class.getName() + " as udf, " + Udfsource.class.getName() + " as udfsource, " + Workflow.class.getName() + " as workflow where udf.id=? and udf.udfsource.id=udfsource.id and udfsource.workflow.id=workflow.id", udfId);
        return list.isEmpty() ? name : (String) list.get(0);
    }

    /**
     * Проверяет наличие в базе категории по ID
     *
     * @param id ID искомого объекта
     * @return isCategoryExists
     * @throws GranException при необходимости
     */
    public boolean isCategoryExists(String id) throws GranException {

        boolean r = lockManager.acquireConnection(className);
        if (id == null) return false;
        try {
            Category cat = (Category) hu.getObject(Category.class, id);
            return cat != null;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }
}