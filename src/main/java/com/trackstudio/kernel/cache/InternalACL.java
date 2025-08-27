package com.trackstudio.kernel.cache;

import com.trackstudio.tools.Intern;

import net.jcip.annotations.Immutable;

/**
 * Класс используется для кеширования ACL
 */
@Immutable
public class InternalACL implements Comparable {

    private final String userId;
    private final String groupId;
    private final String prstatusId;
    private final String aclId;
    private final String ownerId;
    private final boolean override;

    /**
     * Конструктор
     *
     * @param aclId      ID правила доступа
     * @param userId     ID пользователя
     * @param groupId    ID группы
     * @param prstatusId ID статуса
     * @param ownerId    ID владельца правила доступа
     * @param override   Указывает переопределен ли статус или нет
     */
    public InternalACL(String aclId, String userId, String groupId, String prstatusId, String ownerId, Integer override) {
        this.aclId = aclId;
        this.userId = Intern.process(userId);
        this.groupId = Intern.process(groupId);
        this.prstatusId = Intern.process(prstatusId);
        this.ownerId = Intern.process(ownerId);
        this.override = override != null && override == 1;
    }

    /**
     * Возвращает ID пользователя
     *
     * @return ID пользователя
     */
    public String getUserId() {
        return userId;
    }


    /**
     * Возвращает ID группы
     *
     * @return ID группы
     */
    public String getGroupId() {
        return groupId;
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
     * Возвращает ID владельца правила доступа
     *
     * @return ID владельца правила доступа
     */
    public String getOwnerId() {
        return ownerId;
    }


    /**
     * Возвращает ID правила доступа
     *
     * @return ID правила доступа
     */
    public String getAclId() {
        return aclId;
    }


    /**
     * Сравнивает два обхекта текущего класса
     *
     * @param obj Скравниваемый обхект
     * @return TREU если равны, FALSE если нет
     */
    public boolean equals(Object obj) {
        return obj instanceof InternalACL && ((InternalACL) obj).getAclId().equals(getAclId()); //&& ((InternalACL)o).getUserId().equals(getUserId()) && ((InternalACL)o).getPrstatusId().equals(getPrstatusId());
    }

    /**
     * Сравнивает два объекта текущего класса без учета регистра
     *
     * @param o Сравниваемый обхект
     * @return +1, 0 или -1
     */
    public int compareTo(Object o) {
        return ((InternalACL) o).getAclId().compareTo(getAclId());
    }

    /**
     * Возвращает переопределен статус или нет
     *
     * @return TRUE - переопределен, FALSE - нет
     */
    public boolean getOverride() {
        return override;
    }

}
