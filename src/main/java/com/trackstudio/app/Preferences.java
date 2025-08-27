package com.trackstudio.app;

import com.trackstudio.tools.Null;

import net.jcip.annotations.Immutable;

/**
 * Класс для хранения настроек
 */
@Immutable
public class Preferences {
    public enum TreeCategory {
        OPEN, CHECK_STATUS, HIDE
    }
    /**
     * Настройки
     */
    protected final StringBuffer preferences;
    /**
     * Показывать на панеле
     */
    public static final String SHOW_IN_TOOLBAR = "T";
    /**
     * Скрывать категорию или нет
     */
    public static final String HIDE_CATEGORY = "H";
    /**
     * Просматривать ли настройки задачи
     */
    public static final String VIEW_TASK_PROPERTIES = "V";
    /**
     * Порядок сортировки сообщений
     */
    public static final String MESSAGE_ASC_ORDER = "A";


    /**
     * Порядок сортировки категорий в дереве S - в прорядке обновления во всех остальных по алфавиту
     */
    public static final String SORT_UPDATE_ORDER_IN_TREE = "S";


    /**
     * Отображение категории в дереве. N - не показывать
     */
    public static final String HIDDEN_IN_TREE = "N";

    /**
     * Отображение категории в дереве. E - показывать всегда, если символа нет значит показывать и скрывать в конечном состоянии
     */
    public static final String SHOW_ALWAYS_IN_TREE = "E";

    public static final String HIDE_CLOSE_IN_TREE = "F";

    public static final String USE_GOOGLE_CALENDAR = "G";

    public static final String USE_YANDEX_CALENDAR = "Z";


    /**
     * Отображение категории как задачи или как документ. по умолчанию как задача.
     */
    public static final String VIEW_CATEGORY_AS_TASK_AS_DOCUMENT = "D";
    public static final String VIEW_CATEGORY_AS_TASK_AS_FILE_CONTAINER = "B";

    public static final String VIEW_AUTID_TRAIL = "U";

    public static final String HANDLER_ONLY_ROLE = "R";

    /**
     * MStatus is observed by scheduler
     */
    public static final String SCHEDULER = "C";

    /**
     * MStatus is observed by scheduler
     */
    public static final String UN_SCHEDULER = "I";

    /**
     * Показывать ли в панеле
     *
     * @param pref настройки
     * @return TRUE - да, FALSE = нет
     */
    public static boolean showInToolbar(String pref) {
        return pref != null && pref.indexOf(SHOW_IN_TOOLBAR) > -1;
    }

    /**
     * Показывать ли в панеле
     *
     * @param pref настройки
     * @return TRUE - да, FALSE = нет
     */
    public static boolean showInToolbar(StringBuffer pref) {
        return pref != null && pref.indexOf(SHOW_IN_TOOLBAR) > -1;
    }

    public static String scheduler(String pref) {
        if (pref != null) {
            return pref.contains(SCHEDULER) ? SCHEDULER : pref.contains(UN_SCHEDULER) ? UN_SCHEDULER : "";
        }
        return "";
    }

    /**
     * Скрывать категорию или нет
     *
     * @param pref настройки
     * @return TRUE - да, FALSE = нет
     */
    public static boolean isCategoryHidden(String pref) {
        return pref != null && pref.indexOf(HIDE_CATEGORY) > -1;
    }

    /**
     * Скрывать категорию или нет
     *
     * @param pref настройки
     * @return TRUE - да, FALSE = нет
     */
    public static boolean isCategoryHidden(StringBuffer pref) {
        return pref != null && pref.indexOf(HIDE_CATEGORY) > -1;
    }

    /**
     * Возврашает ссылку по умолчанию
     *
     * @param pref настройки
     * @return ссылка
     */
    public static String getDefaultLink(String pref) {
        if (pref != null && pref.indexOf(VIEW_TASK_PROPERTIES) > -1)
            return VIEW_TASK_PROPERTIES;
        else
            return "";
    }

    /**
     * Возврашает ссылку по умолчанию
     *
     * @param pref настройки
     * @return ссылка
     */
    public static String getDefaultLink(StringBuffer pref) {
        if (pref != null && pref.indexOf(VIEW_TASK_PROPERTIES) > -1)
            return VIEW_TASK_PROPERTIES;
        else
            return "";
    }

    /**
     * Конструктор
     *
     * @param preferences настройки
     */
    public Preferences(String preferences) {
        if (preferences == null)
            this.preferences = new StringBuffer();
        else
            this.preferences = new StringBuffer(preferences);
    }

    /**
     * Возвращает настройки
     *
     * @return настройки
     */
    public String getPreferences() {
        return preferences.toString();
    }

    /**
     * Устанавливает показ в тулбаре
     *
     * @param t да/нет
     */
    public void setShowInToolbar(boolean t) {
        if (!showInToolbar(preferences)) {
            if (t) preferences.append(SHOW_IN_TOOLBAR);
        } else if (!t) {
            preferences.deleteCharAt(preferences.indexOf(SHOW_IN_TOOLBAR));
        }
    }

    /**
     * Устанавливает надо ли скрывать категорию или нет
     *
     * @param t да/нет
     */
    public void setHideCategory(boolean t) {
        if (!isCategoryHidden(preferences)) {
            if (t) preferences.append(HIDE_CATEGORY);
        } else if (!t) {
            preferences.deleteCharAt(preferences.indexOf(HIDE_CATEGORY));
        }
    }

    /**
     * Устанавливает ссылку по умолчанию
     *
     * @param defaul ссылка
     */
    public void setDefaultLink(String defaul) {
        if (getDefaultLink(preferences).equals(VIEW_TASK_PROPERTIES)) {
            if (defaul.equals("")) preferences.deleteCharAt(preferences.indexOf(VIEW_TASK_PROPERTIES));
        } else if (defaul.equals(VIEW_TASK_PROPERTIES)) {
            preferences.append(VIEW_TASK_PROPERTIES);
        }
    }

    /**
     * Возвращает тип сортировки
     *
     * @param pref настройки
     * @return TRUE - прямая сортировка, FALSE - обратная
     */
    public static boolean isAscMessageSortOrder(String pref) {
        return pref != null && pref.indexOf(MESSAGE_ASC_ORDER) > -1;
    }

    /**
     * Возвращает тип сортировки
     *
     * @param pref настройки
     * @return TRUE - прямая сортировка, FALSE - обратная
     */
    public static boolean isAscMessageSortOrder(StringBuffer pref) {
        return pref != null && pref.indexOf(MESSAGE_ASC_ORDER) > -1;
    }

    /**
     * Устанавливает тип сортировки
     *
     * @param t TRUE - прямая сортировка, FALSE - обратная
     */
    public void setAscMessageSortOrder(boolean t) {
        if (!isAscMessageSortOrder(preferences)) {
            if (t) preferences.append(MESSAGE_ASC_ORDER);
        } else if (!t) {
            preferences.deleteCharAt(preferences.indexOf(MESSAGE_ASC_ORDER));
        }
    }

    /**
     * Метод определяет вид сортировки категорий в дереве
     * @param pref входная строка
     * @return результат
     */
    public static boolean isSortOrgerInTree(String pref) {
        return  pref != null && pref.indexOf(SORT_UPDATE_ORDER_IN_TREE) != -1;
    }

    /**
     * Метод определяет вид сортировки категорий в дереве
     * @param order в каком порядке
     */
    public void setSortOrgerInTree(boolean order) {
        if (order) {
            if (preferences.indexOf(SORT_UPDATE_ORDER_IN_TREE) == -1) {
                preferences.append(SORT_UPDATE_ORDER_IN_TREE);
            }
        } else {
            if (preferences.indexOf(SORT_UPDATE_ORDER_IN_TREE) != -1) {
                preferences.deleteCharAt(preferences.indexOf(SORT_UPDATE_ORDER_IN_TREE));
            }
        }
    }

    public static String getHiddenInTree(String pref) {
        if (pref != null) {
            if (pref.indexOf(HIDDEN_IN_TREE) != -1) {
                return HIDDEN_IN_TREE;
            }
            if (pref.indexOf(SHOW_ALWAYS_IN_TREE) != -1) {
                return SHOW_ALWAYS_IN_TREE;
            }
        }
        return HIDE_CLOSE_IN_TREE;
    }

    /**
     * Установка отображения в дереве
     * @param hidden да или нет
     */
    public void setHiddenInTree(String hidden) {
        if (hidden == null || hidden.indexOf(HIDE_CLOSE_IN_TREE) != -1 || hidden.indexOf(SHOW_ALWAYS_IN_TREE) != -1) {
            if (preferences.indexOf(HIDDEN_IN_TREE) != -1) {
                preferences.deleteCharAt(preferences.indexOf(HIDDEN_IN_TREE));
            }
            if (preferences.indexOf(SHOW_ALWAYS_IN_TREE) != -1) {
                preferences.deleteCharAt(preferences.indexOf(SHOW_ALWAYS_IN_TREE));
            }
        }
        if (hidden == null) {
            preferences.append(SHOW_ALWAYS_IN_TREE);
        } else {
            if (hidden.indexOf(HIDDEN_IN_TREE) != -1) {
                if (preferences.indexOf(HIDDEN_IN_TREE) == -1) {
                    preferences.append(HIDDEN_IN_TREE);
                }
            }
            if (hidden.indexOf(SHOW_ALWAYS_IN_TREE) != -1) {
                if (preferences.indexOf(SHOW_ALWAYS_IN_TREE) == -1) {
                    preferences.append(SHOW_ALWAYS_IN_TREE);
                }
            }
        }
    }

    public static boolean isUseGoogleCalendar(String pref) {
        return  pref != null && pref.indexOf(USE_GOOGLE_CALENDAR) != -1;
    }

    public void setUseGoogleCalendar(boolean order) {
        if (order) {
            if (preferences.indexOf(USE_GOOGLE_CALENDAR) == -1) {
                preferences.append(USE_GOOGLE_CALENDAR);
            }
        } else {
            if (preferences.indexOf(USE_GOOGLE_CALENDAR) != -1) {
                preferences.deleteCharAt(preferences.indexOf(USE_GOOGLE_CALENDAR));
            }
        }
    }

    public static boolean isUseYandexCalendar(String pref) {
        return  pref != null && pref.indexOf(USE_YANDEX_CALENDAR) != -1;
    }

    public void setUseYandexCalendar(boolean order) {
        if (order) {
            if (preferences.indexOf(USE_YANDEX_CALENDAR) == -1) {
                preferences.append(USE_YANDEX_CALENDAR);
            }
        } else {
            if (preferences.indexOf(USE_YANDEX_CALENDAR) != -1) {
                preferences.deleteCharAt(preferences.indexOf(USE_YANDEX_CALENDAR));
            }
        }
    }

    public void setViewCategory(String view) {
        if (view != null && view.indexOf(VIEW_CATEGORY_AS_TASK_AS_DOCUMENT) != -1) {
            if (preferences.indexOf(VIEW_CATEGORY_AS_TASK_AS_DOCUMENT) == -1) {
                preferences.append(VIEW_CATEGORY_AS_TASK_AS_DOCUMENT);
            }
        } else if (view != null && view.indexOf(VIEW_CATEGORY_AS_TASK_AS_FILE_CONTAINER) != -1) {
            if (preferences.indexOf(VIEW_CATEGORY_AS_TASK_AS_FILE_CONTAINER) == -1) {
                preferences.append(VIEW_CATEGORY_AS_TASK_AS_FILE_CONTAINER);
            }
        } else {
            if (preferences.indexOf(VIEW_CATEGORY_AS_TASK_AS_DOCUMENT) != -1) {
                preferences.deleteCharAt(preferences.indexOf(VIEW_CATEGORY_AS_TASK_AS_DOCUMENT));
            }
            if (preferences.indexOf(VIEW_CATEGORY_AS_TASK_AS_FILE_CONTAINER) != -1) {
                preferences.deleteCharAt(preferences.indexOf(VIEW_CATEGORY_AS_TASK_AS_FILE_CONTAINER));
            }
        }
    }

    public static String getViewCategory(String pref) {
        if (pref == null) {
            return "";
        }
        if (pref.indexOf(VIEW_CATEGORY_AS_TASK_AS_DOCUMENT) != -1) {
            return VIEW_CATEGORY_AS_TASK_AS_DOCUMENT;
        } else if (pref.indexOf(VIEW_CATEGORY_AS_TASK_AS_FILE_CONTAINER) != -1) {
            return VIEW_CATEGORY_AS_TASK_AS_FILE_CONTAINER;
        }
        return "";
    }

    public static boolean isViewAutidTrail(StringBuffer pref) {
        return pref != null && pref.indexOf(VIEW_AUTID_TRAIL) != -1;
    }

    public static boolean isViewAutidTrail(String pref) {
        return pref != null && pref.indexOf(VIEW_AUTID_TRAIL) != -1;
    }

    public void setViewAutidTrail(boolean t) {
        if (!isViewAutidTrail(preferences)) {
            if (t) preferences.append(VIEW_AUTID_TRAIL);
        } else if (!t) {
            preferences.deleteCharAt(preferences.indexOf(VIEW_AUTID_TRAIL));
        }
    }


    public static boolean isHandlerOnlyRole(String pref) {
        return pref != null && pref.toString().contains(HANDLER_ONLY_ROLE);
    }

    public void setHandlerOnlyRole(boolean t) {
        if (!isHandlerOnlyRole(preferences.toString())) {
            if (t) preferences.append(HANDLER_ONLY_ROLE);
        } else if (!t) {
            preferences.deleteCharAt(preferences.indexOf(HANDLER_ONLY_ROLE));
        }
    }

    public void setScheduler(String value) {
        if (preferences.indexOf(SCHEDULER) != -1) {
            preferences.deleteCharAt(preferences.indexOf(SCHEDULER));
        }
        if (preferences.indexOf(UN_SCHEDULER) != -1) {
            preferences.deleteCharAt(preferences.indexOf(UN_SCHEDULER));
        }
        if (Null.isNotNull(value)) {
            if (value.equals(SCHEDULER)) {
                preferences.append(SCHEDULER);
            } else {
                preferences.append(UN_SCHEDULER);
            }
        }
    }
}