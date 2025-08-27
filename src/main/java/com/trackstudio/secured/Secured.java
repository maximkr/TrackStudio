package com.trackstudio.secured;

import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.tools.PropertyComparable;

import net.jcip.annotations.ThreadSafe;

/**
 * Абстрактный класс, на основании которого создаются объекты вида Secured...Bean
 */
@ThreadSafe
public abstract class Secured extends PropertyComparable {
    protected volatile SessionContext sc;

    public SessionContext getSecure() {
        return sc;
    }

    public void setSecure(SessionContext sc) {
        this.sc = sc;
    }

    public abstract String getId();
    public void setId(String id) {
    }

    public abstract boolean isAllowedByACL() throws GranException;

    /**
     * Проверяет доступ к бину
     *
     * @return TRUE - если доступ есть, FALSE - если нет (на самом деле, конечно, никто бины не может редактировать, у них сеттеров нет)
     * @throws GranException при необходимости
     * @deprecated потому что правильно нужно проверять с учетом ролей и Action. Используйте метод canManage. Этот оставлен только как legacy
     */
    public boolean canUpdate() throws GranException {
        return isAllowedByACL();
    }

    /**
     * Проверяет доступ к бину. Новый улучшеный метод. Нужно проверять доступ как по ACL, так и по Roles
     *
     * @return TRUE - доступ есть, FALSE - нет
     * @throws GranException при необходимости
     */
    public abstract boolean canManage() throws GranException;

    /**
     * Проверяет доступ к бину
     *
     * @return TRUE - если доступ есть, FALSE - если нет (на самом деле, конечно, никто бины не может редактировать, у них сеттеров нет)
     * @throws GranException при необходимости
     * @deprecated потому что правильно нужно проверять с учетом ролей и Action. Используйте метод canManage. Этот оставлен только как legacy
     */
    public boolean getCanUpdate() throws GranException {
        return isAllowedByACL();
    }

    /**
     * Проверяет доступ к бину. Новый улучшеный метод. Нужно проверять доступ как по ACL, так и по Roles
     *
     * @return TRUE - доступ есть, FALSE - нет
     * @throws GranException при необходимости
     */
    public boolean getCanManage() throws GranException {
        return canManage();
    }

    public abstract boolean canView() throws GranException;
}
