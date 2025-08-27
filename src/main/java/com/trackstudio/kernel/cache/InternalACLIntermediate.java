package com.trackstudio.kernel.cache;

import com.trackstudio.tools.Intern;

import net.jcip.annotations.Immutable;

/**
 * Вспомогательный класс для прав доступа
 */
@Immutable
public class InternalACLIntermediate {

    private final String objectId;
    private final String userId;
    private final String prstatusId;
    private final String aclId;
    private final String ownerId;
    private final String groupId;
    private final Integer override;

    /**
     * Конструктор по умолчанию
     *
     * @param objectId   ID задачи или польльзователя, для которых создано правило
     * @param aclId      ID правила доступа
     * @param userId     ID пользователя
     * @param groupId    ID группы
     * @param prstatusId ID статуса
     * @param ownerId    ID владельца правила
     * @param override   Нужно ли переопределять группу
     */
    public InternalACLIntermediate(String objectId, String aclId, String userId, String groupId, String prstatusId, String ownerId, Integer override) {
        this.objectId = objectId;
        this.aclId = Intern.process(aclId);
        this.userId = Intern.process(userId);
        this.groupId = Intern.process(groupId);
        this.prstatusId = Intern.process(prstatusId);
        this.ownerId = Intern.process(ownerId);
        this.override = override;
    }

    /**
     * Возвращает InternalACL
     *
     * @return InternalACL
     * @see com.trackstudio.kernel.cache.InternalACL
     */
    public InternalACL getInternalACL() {
        return new InternalACL(aclId, userId, groupId, prstatusId, ownerId, override);
    }

    /**
     * Возвращает ID хаджачи или пользователя, для которого создано правило доступа
     *
     * @return ID задачи или пользователя
     */
    public String getObjectId() {
        return this.objectId;
    }
}
