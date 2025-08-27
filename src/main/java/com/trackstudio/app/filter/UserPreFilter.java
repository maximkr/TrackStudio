package com.trackstudio.app.filter;

import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Projections;

import com.trackstudio.common.FieldMap;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.UserRelatedManager;
import com.trackstudio.model.User;

import net.jcip.annotations.Immutable;

/**
 * Класс для работы с предварительной фильтрацией пользователей
 */
@Immutable
public class UserPreFilter extends PreFilter {

    private static final Log log = LogFactory.getLog(UserPreFilter.class);

    /**
     * Конструктор
     *
     * @param fv            Параметры фильтрации пользователей
     * @param currentUserId ID текущего пользователя
     * @throws GranException при необзодимости
     */
    public UserPreFilter(UserFValue fv, String currentUserId) throws GranException {
        super(fv, currentUserId);
    }

    /**
     * Фильтрует пользователей
     *
     * @param sourceUsersSet Список пользователей
     * @param userId         ID пользователя
     * @return список пользователей
     * @throws GranException при необходимости
     */
    public Set<String> filterUsers(Set<String> sourceUsersSet, String userId) throws GranException {
        List<String> parent = UserRelatedManager.getInstance().getParents(sourceUsersSet);
        parent.add(userId);
        return fastRetainAll(fastRetainAll(sourceUsersSet, processUsers(parent)), processUdfs(parent, sourceUsersSet));
    }

    /**
     * Фильтрует пользователей
     *
     * @param parentObjects Список пользователей
     * @return список пользователей
     * @throws GranException при необходимости
     */
    private Set<String> processUsers(List<String> parentObjects) throws GranException {
        boolean w = lockManager.acquireConnection(UserPreFilter.class.getSimpleName());
        try {
            log.trace("*****");
            List<String> userConditions = ((UserFValue) fv).getUseForUser();
            Session sess = lockManager.getDBSession().getSession();
            Criteria cr = sess.createCriteria(User.class, "userPrj");
            cr.setProjection(Projections.projectionList().add(Projections.property("userPrj.id"), "id"));
            boolean processed = false;
            for (String c : userConditions) {
                if (c.equals(FieldMap.USER_STATUS.getFilterKey()))
                    processed = applyListCriteria(cr, FieldMap.USER_STATUS.getFilterKey(), "prstatus", "id") || processed;
                else if (c.equals(FieldMap.USER_LOCALE.getFilterKey()))
                    processed = applyListCriteria(cr, FieldMap.USER_LOCALE.getFilterKey(), null, "locale") || processed;
                else if (c.equals(FieldMap.USER_TIMEZONE.getFilterKey()))
                    processed = applyListCriteria(cr, FieldMap.USER_TIMEZONE.getFilterKey(), null, "timezone") || processed;
                else if (c.equals(FieldMap.USER_EXPIREDATE.getFilterKey()))
                    processed = applyDateCriteria(cr, FieldMap.USER_EXPIREDATE.getFilterKey(), "expireDate") || processed;

            }
            if (parentObjects.size() < MAX_PARENT) {
                cr.createAlias("manager", "manager").add(Expression.in("manager.id", parentObjects));
            }
            if (processed)
                return getResult(cr);
            else
                return null;
        } finally {
            if (w) lockManager.releaseConnection(UserPreFilter.class.getSimpleName());
        }
    }
}
