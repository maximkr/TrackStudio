package com.trackstudio.action.task;

/**
 * Определяет события, при которых надо обновлять JavaScript-дерево,
 * хранит необходимые параметры этих событий.
 */
public class ChangeEvent {
    public static final String EVENT_TASK_ADDED = "Task Added";
    public static final String EVENT_TASK_DELETED = "Task Deleted";
    public static final String EVENT_TASK_COPIED = "Task Copied";
    public static final String EVENT_TASK_COPIED_RECURSIVELY = "Task Copied Recursively";
    public static final String EVENT_TASK_CUT = "Task Cut";
    public static final String EVENT_TASK_PASTED_CUT = "Task Pasted Cut";
    public static final String EVENT_TASK_PASTED_COPIED = "Task Pasted Copied";
    public static final String EVENT_TASK_PASTED_COPIED_RECURSIVELY = "Task Pasted Copied Recursively";
    public static final String EVENT_TASK_UPDATED = "Task Updated";
    public static final String EVENT_USER_ADDED = "User Added";
    public static final String EVENT_USER_DELETED = "User Deleted";
    public static final String EVENT_USER_RENAMED = "User Renamed";
    public static final String EVENT_USER_CUT = "User Cut";
    public static final String EVENT_USER_PASTED_CUT = "User Pasted Cut";

    public String type,
            parentHint, parentId;

    public String[] icons,
            statusIcons,
            hints,
            names,
            actions,
            ids,
            copiedHints,
            listParentHintForCopieds;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getParentHint() {
        return "'" + parentHint + "'";
    }

    public void setParentHint(String parentHint) {
        this.parentHint = parentHint;
    }

    public String getParentId() {
        return "'" + parentId + "'";
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getIcons() {
    	if (icons == null || icons.length == 0) return "[]";
        String result = "[";
        for (String icon : icons) {
            result += "'" + icon + "',";
        }
        result = result.substring(0, result.length() - 1) + "]";
        
        return result;
    }

    public void setIcons(String[] icons) {
        this.icons = icons;
    }

    public String getStatusIcons() {
    	if (statusIcons == null || statusIcons.length == 0) return "[]";
        String result = "[";
        for (String statusIcon : statusIcons) {
            result += "'" + statusIcon + "',";
        }
        result = result.substring(0, result.length() - 1) + "]";
        
        return result;
    }

    public void setStatusIcons(String[] statusIcons) {
        this.statusIcons = statusIcons;
    }

    public String getHints() {
    	if (hints == null || hints.length == 0) return "[]";
        String result = "[";
        for (String hint : hints) {
            result += "'" + hint + "',";
        }
        result = result.substring(0, result.length() - 1) + "]";
        
        return result;
    }

    public void setHints(String[] hints) {
        this.hints = hints;
    }

    public String getNames() {
    	if (names == null || names.length == 0) return "[]";
        String result = "[";
        for (String name : names) {
            result += "'" + name + "',";
        }
        result = result.substring(0, result.length() - 1) + "]";
        
        return result;
    }

    public void setNames(String[] names) {
        this.names = names;
    }

    public String getActions() {
    	if (actions == null || actions.length == 0) return "[]";
        String result = "[";
        for (String action : actions) {
            result += "\"" + action + "\",";
        }
        result = result.substring(0, result.length() - 1) + "]";
        
        return result;
    }

    public void setActions(String[] actions) {
        this.actions = actions;
    }

    public String getIds() {
    	if (ids == null || ids.length == 0) return "[]";
        String result = "[";
        for (String id : ids) {
            result += "'" + id + "',";
        }
        result = result.substring(0, result.length() - 1) + "]";
        
        return result;
    }

    public void setIds(String[] ids) {
        this.ids = ids;
    }

    public String getCopiedHints() {
    	if (copiedHints == null || copiedHints.length == 0) return "[]";
        String result = "[";
        for (String copiedHint : copiedHints) {
            result += "'" + copiedHint + "',";
        }
        result = result.substring(0, result.length() - 1) + "]";
        
        return result;
    }

    public void setCopiedHints(String[] copiedHints) {
        this.copiedHints = copiedHints;
    }

    public void setListParentHintForCopieds(String[] listParentHintForCopieds) {
        this.listParentHintForCopieds = listParentHintForCopieds;
    }

    public String getListParentHintForCopieds() {
    	if (listParentHintForCopieds == null || listParentHintForCopieds.length == 0) return "[]";
        String result = "[";
        for (String listParentHintForCopied : listParentHintForCopieds) {
            result += "'" + listParentHintForCopied + "',";
        }
        result = result.substring(0, result.length() - 1) + "]";
        
        return result;
    }

    /**
     * @param type
     * @param parentHint
     * @param icons
     * @param statusIcons
     * @param hints       - элемент в дереве ищется по всплывающей подсказке. Для задач - это номер задачи (например, "#3"),
     *                    для юзеров - логин (например, "jsmith").
     * @param names
     * @param actions
     * @param ids
     */
    public ChangeEvent(String type, String parentHint, String[] icons, String[] statusIcons, String[] hints, String[] names, String[] actions, String[] ids, String[] copiedHints, String[] listParentHintForCopieds, String parentId) {
        this.type = type;
        this.parentHint = parentHint;
        this.icons = icons;
        this.statusIcons = statusIcons;
        this.hints = hints;
        this.names = names;
        this.actions = actions;
        this.ids = ids;
        this.copiedHints = copiedHints;
        this.listParentHintForCopieds = listParentHintForCopieds;
        this.parentId = parentId;
    }
}
