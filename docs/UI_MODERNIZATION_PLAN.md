# TrackStudio UI Modernization Plan

> Последнее обновление: 15 февраля 2026  
> Автор: AI-ассистент  
> Статус: Черновик для обсуждения

---

## Содержание

1. [Текущее состояние](#1-текущее-состояние)
2. [Цели модернизации](#2-цели-модернизации)
3. [Архитектурные ограничения](#3-архитектурные-ограничения)
4. [Карта зависимостей фаз](#4-карта-зависимостей-фаз)
5. [Phase 1: CSS Foundation](#phase-1-css-foundation)
6. [Phase 2: Замена Frameset](#phase-2-замена-frameset)
7. [Phase 3: Header и навигация](#phase-3-header-и-навигация)
8. [Phase 4: Список задач](#phase-4-список-задач)
9. [Phase 5: Страница задачи](#phase-5-страница-задачи)
10. [Phase 6: Замена YUI Dialogs](#phase-6-замена-yui-dialogs)
11. [Phase 7: Обновление библиотек](#phase-7-обновление-библиотек)
12. [Phase 8: UX создания и редактирования](#phase-8-ux-создания-и-редактирования)
13. [Phase 9: Responsive Design](#phase-9-responsive-design)
14. [Phase 10: Polish и Accessibility](#phase-10-polish-и-accessibility)
15. [Сводная таблица](#сводная-таблица)
16. [Рекомендуемый порядок](#рекомендуемый-порядок)

---

## 1. Текущее состояние

### 1.1 Технологический стек

| Компонент | Версия / Технология | Статус |
|---|---|---|
| Серверный рендеринг | JSP + Apache Struts 1.3 + Tiles | Актуально |
| ORM | Hibernate 5.6 | Актуально |
| БД | PostgreSQL 17 | Актуально |
| Java | 21 | Актуально |
| jQuery | **1.11.2** (2014) | ❌ Устарело |
| jQuery UI | Вместе с jQuery 1.11 | ❌ Устарело |
| YUI (Yahoo) | Диалоги + ColorPicker | ❌ Поддержка прекращена (2014) |
| Дерево | FancyTree 2.31.0 | ⚠️ Обновить до 2.38+ |
| Редактор | TinyMCE (старая версия) | ⚠️ Обновить до 6/7 |
| Менюшки | Собственный TSMenu (641 строка) | ❌ Нужна замена |
| CSS-фреймворк | Нет (собственный CSS) | — |
| JS-фреймворк | Нет (jQuery + vanilla) | — |
| Сборка JS | Кастомная JSP-конкатенация (`<ts:js>`) | — |
| Сборка CSS | Кастомная JSP-конкатенация (`<ts:css>`) | — |
| Навигация | **HTML Frameset** | ❌ Устарело |

### 1.2 Метрики кодовой базы (UI)

| Метрика | Значение |
|---|---|
| JSP-файлов | **190** |
| JSP с `ListLayout.jsp` | **~107** |
| CSS-файлов | **~49** |
| JS-файлов | **~142** |
| `style_src.css` строк | **3665** (из них ~2957 legacy, ~708 modern refresh) |
| `style-panel.css` строк | **183** |
| `tsmenu.js` строк | **641** |
| `slidingframe.js` строк | **669** |
| `font-size: 11px` вхождений | **97** (только в style_src.css) |
| `class="general"` таблиц в JSP | **~220** вхождений в **~95** файлах |
| `class="controlPanel"` в JSP | **46** файлов |
| `self.top.frames[` в JSP | **31** вхождение в **9** файлах |
| `self.top.frames[` в JS | **58** вхождений в `slidingframe.js` |
| `self.top.frames[` в Java | **9** вхождений в **5** файлах |
| `TSMenu` вхождений | **176** в **12** файлах |
| `YAHOO.widget.Dialog` | **4** диалога в `ListLayout.jsp` |
| `confirm()` вызовов | **8** в **6** JSP-файлах |

### 1.3 Текущая оценка UI

| Критерий | Оценка (1-10) | Комментарий |
|---|---|---|
| Визуальная современность | **4** | Логин ~7, остальное ~3 |
| Интуитивность действий | **4** | Workflow-centric, а не task-centric |
| Типографика | **3** | Verdana 11px, мелко и плотно |
| Навигация | **5** | Дерево хорошее, frameset плохо |
| Формы | **4** | Функциональные, но визуально старые |
| Мобильная адаптивность | **1** | Frameset = zero mobile |
| Скорость загрузки | **6** | SSR, но много отдельных ресурсов |

---

## 2. Цели модернизации

### Визуальные цели
- Единый современный дизайн на **всех** страницах (не только логин)
- Шрифт 14px, просторный layout с достаточным breathing room
- Визуальная иерархия: primary actions выделяются цветом и размером
- Консистентные компоненты: кнопки, инпуты, таблицы, диалоги

### UX-цели
- **Создать задачу** — 1 клик до формы
- **Закрыть/изменить статус задачи** — явные кнопки, не спрятанные в workflow
- **Добавить комментарий** — inline форма, не отдельная страница
- **Приложить файл** — drag & drop
- **Фильтрация** — быстрый поиск + chips фильтров
- **Навигация** — работающие URL, кнопки назад/вперёд в браузере

### Технические цели
- Избавиться от `<frameset>` 
- Избавиться от YUI
- Обновить jQuery до 3.7+
- Responsive: работа на планшете и базовая — на телефоне

### Принципы
- **Инкрементально**: каждая фаза — самодостаточный релиз
- **CSS-first**: максимум визуальных изменений без переписывания JSP
- **Не ломать**: каждая фаза обратно совместима с предыдущей
- **Без SPA-перезаписи**: сервер-сайд рендеринг остаётся

---

## 3. Архитектурные ограничения

### 3.1 Frameset-архитектура

**Файл:** `src/main/webapp/staticframeset.html`

```html
<frameset id="fsid" cols="240,*">
    <frame id="treeFrame" src="TreeLoaderAction.do?method=init" name="treeFrame">
    <frame id="mainFrame" src="TaskAction.do" name="mainFrame">
</frameset>
```

Левый фрейм (`treeFrame`) — дерево задач/пользователей/закладок (`TreeFrame.jsp`).  
Правый фрейм (`mainFrame`) — основной контент (все ~107 страниц через `ListLayout.jsp`).

**Межфреймовая коммуникация:**

| Направление | Паттерн | Пример |
|---|---|---|
| Content → Tree | `self.top.frames[0].reloadTsTree(hint)` | Обновить дерево после CRUD |
| Content → Tree | `self.top.frames[0].expandTsTree(path)` | Раскрыть путь до задачи |
| Content → Tree | `self.top.frames[0].selectNodesTsTree(nodes)` | Выделить узлы |
| Content → Tree | `self.top.frames[0].updateBookmarks(url)` | Обновить закладки |
| Tree → Content | `self.top.frames[1].location = url` | Навигация к задаче/пользователю |
| Content → Frameset | `gInnerFrameset.cols = "240, *"` | Скрыть/показать sidebar |

**Java-код тоже генерирует frame-вызовы!** 5 Java-файлов создают JS-строки с `self.top.frames[1].location`:
- `TaskEditAction.java` (строки 594, 611)
- `MessageCreateAction.java` (строки 462, 473, 603)
- `SubtaskAction.java` (строка 938)
- `UserEditAction.java` (строки 244, 436)
- `UserListAction.java` (строка 408)
- `BookmarkServlet.java` (строки 71, 77, 97, 100)

### 3.2 Legacy slidingframe.js

`slidingframe.js` (669 строк) содержит:
- Анимацию сворачивания sidebar через `setInterval` + манипуляцию `gInnerFrameset.cols`
- Функции управления деревом через старый WebFX Tree API (`self.top.frames[0].taskTree`)
- FancyTree-совместимые функции (`reloadTsTree`, `expandTsTree`) определены в `TreeFrame.jsp`

**Важно:** `slidingframe.js` содержит ДВА набора функций:
1. **Старый WebFX Tree API** (функции `addTaskToTree`, `removeTasksFromTree`, `updateTaskInTree` и т.д.) — работает через `self.top.frames[0].taskTree` / `self.top.frames[0].WebFXLoadTreeItem`
2. **Новый FancyTree API** (функции `reloadTsTree`, `expandTsTree`, `selectNodesTsTree`) — определены в `TreeFrame.jsp`

JSP/Java код использует **новый** FancyTree API. Старый WebFX API возможно тоже ещё вызывается из Java.

### 3.3 Tiles-композиция

Страницы собираются inline через `<tiles:insert>`:

```
ListLayout.jsp (шаблон)
├── header → TaskHeader.jsp или UserHeader.jsp (+ 21 специализированный header)
├── customHeader → опциональный блок
├── tabs → вкладки навигации
└── main → контент страницы

LoginLayout.jsp (шаблон)
└── form → форма логина/регистрации
```

Нет XML-определений tiles. Все ~107 страниц подключают `ListLayout.jsp` напрямую.

### 3.4 Кастомная JS/CSS конкатенация

```jsp
<ts:js request="${request}" response="${response}">
    <ts:jsLink link="${urlHtml}/jquery/jquery-1.11.2.min.js"/>
    <ts:jsLink link="${urlHtml}/tsmenu/tsmenu.js"/>
    ...
</ts:js>
```

Java-класс `StoreCssJs.java` конкатенирует файлы и отдаёт по MD5-ключу через `/JSServlet/{key}`. Это аналог простого бандлера. **Порядок файлов важен.**

---

## 4. Карта зависимостей фаз

```
Phase 1 (CSS) ──────┬──→ Phase 2 (Frameset) ──→ Phase 3 (Header) ──→ Phase 9 (Responsive)
                     │           │
                     │           ├──→ Phase 4 (Task List)
                     │           │
                     │           └──→ Phase 5 (Task Detail) ──→ Phase 8 (Create/Edit UX)
                     │
                     └──→ Phase 6 (YUI Dialogs) ─── параллельно с любой фазой
                     
Phase 7 (Library Updates) ────────────── параллельно, до Phase 4-5

Phase 10 (Polish) ────────────────────── последний, после Phase 1-9
```

**Критический путь:** 1 → 2 → 3 → 4/5 → 8 → 9 → 10

---

## Phase 1: CSS Foundation

> **Риск:** Очень низкий  
> **Влияние:** Высокое  
> **Трудоёмкость:** 2-3 дня  
> **Зависимости:** Нет  
> **Принцип:** Только CSS. Ни один JSP, ни один JS-файл не меняется.

### 1.1 Цель

Сделать **весь** интерфейс визуально целостным и заметно более современным, изменяя только CSS-файлы.

### 1.2 Проблема: дуализм стилей

`style_src.css` содержит 3665 строк:
- **Строки 1-2957:** legacy-стили (Verdana 11px, жёсткие цвета, table-centric)
- **Строки 2958-3665:** modern refresh (CSS-переменные, Segoe UI 12px, border-radius, shadows)

Modern-блок перезаписывает legacy через каскадность, но покрывает **не все** компоненты. Пользователь видит мозаику: современные кнопки рядом с Verdana-11px-таблицами.

### 1.3 План действий

#### Шаг 1: Реструктуризация CSS-файлов

Разделить `style_src.css` на 3 файла:

| Файл | Содержание | Примерный размер |
|---|---|---|
| `style_tokens.css` | CSS-переменные, reset, базовая типографика, глобальные элементы | ~200 строк |
| `style_components.css` | Все компоненты: `TABLE.general`, `controlPanel`, `tabbedpane`, `.login`, `.logopath`, `.taskTitle`, формы, диалоги, меню, пагинация | ~800 строк |
| `style_legacy.css` | Оставшиеся legacy-стили, которые ещё нужны для корректного отображения. Каждый блок помечен комментарием `/* TODO: migrate to components */` | ~1500 строк |

**Файлы, которые нужно изменить:**
- `src/main/webapp/style_src.css` → разделить на 3 файла
- `src/main/webapp/jsp/layout/ListLayout.jsp` строки 96-110 → подключить новые CSS:
  ```jsp
  <ts:css request="${request}">
      <ts:cssLink link="style_tokens.css"/>
      <ts:cssLink link="style_legacy.css"/>
      <ts:cssLink link="style_components.css"/>
      ...
  </ts:css>
  ```
- `src/main/webapp/jsp/layout/LoginLayout.jsp` строка 21 → аналогично
- `src/main/webapp/jsp/TreeFrame.jsp` строки 231-234 → аналогично

**Порядок подключения важен:** tokens → legacy → components. Components перезаписывают legacy.

#### Шаг 2: Расширить дизайн-токены

Текущие токены (строки 2959-2980 `style_src.css`):

```css
:root {
    --ts-bg: #f4f7fb;
    --ts-surface: #ffffff;
    --ts-surface-muted: #f8fafc;
    --ts-border: #d9e4ec;
    --ts-border-strong: #bfcfdb;
    --ts-text: #1d2b36;
    --ts-text-muted: #5f7383;
    --ts-primary: #2f7396;
    --ts-primary-strong: #225975;
    --ts-primary-soft: #e6f0f6;
    --ts-success: #1e8a54;
    --ts-danger: #c23b3b;
    --ts-shadow: 0 6px 20px rgba(29, 43, 54, 0.08);
    --ts-radius-sm: 6px;
    --ts-radius-md: 10px;
    --ts-space-1: 4px;
    --ts-space-2: 8px;
    --ts-space-3: 12px;
    --ts-space-4: 16px;
    --ts-space-5: 20px;
}
```

**Добавить:**

```css
:root {
    /* === Существующие токены (без изменений) === */
    
    /* Typography — НОВОЕ */
    --ts-font-family: "Segoe UI", -apple-system, BlinkMacSystemFont, system-ui, sans-serif;
    --ts-font-mono: "Cascadia Code", "Fira Code", "JetBrains Mono", monospace;
    --ts-font-base: 14px;
    --ts-font-sm: 12px;
    --ts-font-xs: 11px;
    --ts-font-lg: 16px;
    --ts-font-xl: 20px;
    --ts-font-xxl: 24px;
    --ts-line-height: 1.5;
    --ts-line-height-tight: 1.3;
    
    /* Spacing — расширить */
    --ts-space-6: 24px;
    --ts-space-8: 32px;
    --ts-space-10: 40px;
    --ts-space-12: 48px;
    
    /* Transitions — НОВОЕ */
    --ts-transition-fast: 0.1s ease;
    --ts-transition: 0.15s ease;
    --ts-transition-slow: 0.3s ease;
    
    /* Layout sizes — НОВОЕ */
    --ts-sidebar-width: 260px;
    --ts-header-height: 48px;
    --ts-toolbar-height: 40px;
    --ts-max-content: 1400px;
    --ts-input-height: 36px;
    
    /* Z-index scale — НОВОЕ */
    --ts-z-base: 1;
    --ts-z-sidebar: 100;
    --ts-z-header: 200;
    --ts-z-dropdown: 300;
    --ts-z-modal-backdrop: 400;
    --ts-z-modal: 500;
    --ts-z-toast: 600;
    
    /* Additional colors — НОВОЕ */
    --ts-warning: #d68a00;
    --ts-info: #2f7396;
    --ts-success-soft: #e6f5ed;
    --ts-danger-soft: #fde8e8;
    --ts-warning-soft: #fff3d6;
}
```

#### Шаг 3: Глобальная типографика

**Проблема:** 97 вхождений `font-size: 11px` в legacy CSS. Плюс inline-стили в JSP.

**Решение:** Добавить в `style_tokens.css` глобальный override с высокой специфичностью:

```css
/* Global typography reset — overrides all legacy font-size: 11px declarations */
BODY, 
BODY TD, BODY TH, BODY INPUT, BODY SELECT, BODY TEXTAREA, 
BODY PRE, BODY A, BODY LABEL, BODY SPAN:not(.fancytree-title):not(.fancytree-expander) {
    font-family: var(--ts-font-family) !important;
    font-size: var(--ts-font-base);
    line-height: var(--ts-line-height);
}
```

**Исключения**, для которых нужен мелкий шрифт:
- FancyTree (`.fancytree-title` — 12px, оставить)
- Footer/copyright (11px, оставить)
- Мелкие пояснения (класс `.ts-text-sm` → 12px)

#### Шаг 4: Форменные элементы

Все `INPUT`, `SELECT`, `TEXTAREA` — единый стиль:

```css
INPUT[type="text"],
INPUT[type="password"],
INPUT[type="email"],
INPUT[type="number"],
INPUT[type="search"],
SELECT,
TEXTAREA {
    height: var(--ts-input-height);
    padding: var(--ts-space-1) var(--ts-space-2);
    border: 1px solid var(--ts-border-strong);
    border-radius: var(--ts-radius-sm);
    color: var(--ts-text);
    background-color: var(--ts-surface);
    font-family: var(--ts-font-family);
    font-size: var(--ts-font-base);
    transition: border-color var(--ts-transition), box-shadow var(--ts-transition);
    box-sizing: border-box;
}

INPUT:focus, SELECT:focus, TEXTAREA:focus {
    outline: none;
    border-color: var(--ts-primary);
    box-shadow: 0 0 0 3px rgba(47, 115, 150, 0.15);
}

TEXTAREA {
    height: auto;
    min-height: 80px;
    resize: vertical;
}
```

**Кнопки — 3 варианта:**

```css
/* Primary (главное действие) */
INPUT[type="submit"],
INPUT[type="button"],
BUTTON,
.iconized {
    height: var(--ts-input-height);
    padding: var(--ts-space-1) var(--ts-space-4);
    border: 1px solid var(--ts-primary);
    border-radius: var(--ts-radius-sm);
    background-color: var(--ts-primary);
    color: #ffffff;
    font-weight: 600;
    cursor: pointer;
    transition: all var(--ts-transition);
}

INPUT[type="submit"]:hover,
INPUT[type="button"]:hover,
BUTTON:hover,
.iconized:hover {
    background-color: var(--ts-primary-strong);
    border-color: var(--ts-primary-strong);
}

/* Secondary */
.iconized.secondary,
INPUT.secondary {
    background-color: var(--ts-surface);
    color: var(--ts-primary);
    border-color: var(--ts-border-strong);
}

.iconized.secondary:hover,
INPUT.secondary:hover {
    background-color: var(--ts-primary-soft);
    border-color: var(--ts-primary);
}

/* Danger */
.iconized.danger {
    background-color: var(--ts-danger);
    border-color: var(--ts-danger);
    color: #ffffff;
}
```

#### Шаг 5: TABLE.general — полная перестилизация

Используется в **~95 JSP-файлах** (~220 вхождений). CSS-only изменение затронет все.

```css
TABLE.general {
    width: 100%;
    border: 1px solid var(--ts-border);
    border-radius: var(--ts-radius-md);
    background-color: var(--ts-surface);
    overflow: hidden;
    box-shadow: var(--ts-shadow);
    margin-bottom: var(--ts-space-4);
    border-collapse: separate;
    border-spacing: 0;
}

TABLE.general CAPTION {
    background-color: var(--ts-surface-muted);
    border-bottom: 1px solid var(--ts-border);
    color: var(--ts-text);
    font-size: var(--ts-font-base);
    font-weight: 600;
    padding: var(--ts-space-3) var(--ts-space-4);
    text-align: left;
}

TABLE.general TH {
    background-color: var(--ts-surface-muted);
    color: var(--ts-text-muted);
    font-weight: 600;
    font-size: var(--ts-font-sm);
    padding: var(--ts-space-2) var(--ts-space-3);
    text-align: right;
    white-space: nowrap;
    border-bottom: 1px solid var(--ts-border);
}

TABLE.general TD {
    padding: var(--ts-space-2) var(--ts-space-3);
    font-size: var(--ts-font-base);
    color: var(--ts-text);
    border-bottom: 1px solid var(--ts-border);
}

TABLE.general TR:last-child TD,
TABLE.general TR:last-child TH {
    border-bottom: none;
}

/* Alternating rows — более тонкая разница */
TABLE.general TR.line0 TD { background-color: var(--ts-surface); }
TABLE.general TR.line1 TD { background-color: var(--ts-surface-muted); }

/* Hover на строках списка */
TABLE.general TR.line0:hover TD,
TABLE.general TR.line1:hover TD {
    background-color: var(--ts-primary-soft);
}
```

#### Шаг 6: Вкладки (tabbedpane)

Убрать image-based табы. CSS-only:

```css
TABLE.tabbedpane {
    margin-top: var(--ts-space-4);
    margin-bottom: 0;
}

TABLE.tabbedpane TD LI A {
    background: none !important;
    border: none;
    border-bottom: 2px solid transparent;
    border-radius: 0;
    padding: var(--ts-space-2) var(--ts-space-3);
    margin-right: var(--ts-space-1);
    color: var(--ts-text-muted);
    font-weight: 500;
    transition: all var(--ts-transition);
}

TABLE.tabbedpane TD LI A SPAN {
    background: none !important;
    float: none;
    display: inline;
    padding: 0;
}

TABLE.tabbedpane TD LI A:hover {
    color: var(--ts-primary);
    border-bottom-color: var(--ts-primary-soft);
    background: none !important;
}

TABLE.tabbedpane TD LI#selectedtab A,
TABLE.tabbedpane TD LI#selectedtab A:hover {
    color: var(--ts-primary);
    border-bottom-color: var(--ts-primary);
    font-weight: 600;
    background: none !important;
}
```

#### Шаг 7: Control Panel

```css
div.controlPanel {
    display: flex;
    align-items: center;
    flex-wrap: wrap;
    gap: var(--ts-space-1);
    border: 1px solid var(--ts-border);
    border-radius: var(--ts-radius-md);
    background-color: var(--ts-surface);
    padding: var(--ts-space-2) var(--ts-space-3);
    margin-bottom: var(--ts-space-3);
    box-shadow: var(--ts-shadow);
}

div.controlPanel A {
    display: inline-flex;
    align-items: center;
    gap: 4px;
    font-family: var(--ts-font-family);
    font-size: var(--ts-font-sm);
    font-weight: 500;
    color: var(--ts-text-muted);
    text-decoration: none;
    padding: var(--ts-space-1) var(--ts-space-2);
    border: 1px solid transparent;
    border-radius: var(--ts-radius-sm);
    transition: all var(--ts-transition);
    white-space: nowrap;
}

div.controlPanel A:hover {
    color: var(--ts-text);
    background-color: var(--ts-surface-muted);
    border-color: var(--ts-border);
}

div.controlPanel span.separator {
    display: inline-block;
    width: 1px;
    height: 20px;
    background-color: var(--ts-border);
    margin: 0 var(--ts-space-2);
}
```

### 1.4 Файлы для изменения

| Файл | Действие |
|---|---|
| `src/main/webapp/style_src.css` | Разделить на 3 файла |
| *(новый)* `src/main/webapp/style_tokens.css` | CSS-переменные, reset, типографика |
| *(новый)* `src/main/webapp/style_components.css` | Компоненты (tables, forms, toolbar, tabs, etc.) |
| *(новый)* `src/main/webapp/style_legacy.css` | Остатки legacy (помечены для миграции) |
| `src/main/webapp/style-panel.css` | Обновить переменные, убрать дубликаты |
| `src/main/webapp/jsp/layout/ListLayout.jsp` | Строки 96-110: подключить новые CSS |
| `src/main/webapp/jsp/layout/LoginLayout.jsp` | Строка 21: подключить новые CSS |
| `src/main/webapp/jsp/TreeFrame.jsp` | Строки 231-234: подключить новые CSS |

### 1.5 Тестирование

- [ ] Страница логина — визуально не регрессировала
- [ ] Страница списка задач (Subtasks) — все колонки видны, hover работает
- [ ] Страница задачи (TaskInfo) — свойства, описание, сообщения
- [ ] Форма создания задачи — все поля, кнопки
- [ ] Форма сообщения (MessageCreate) — TinyMCE, поля, кнопки
- [ ] Страница пользователя — свойства, ACL
- [ ] Настройки workflow — таблицы, формы
- [ ] Дерево (TreeFrame) — не сломалось, шрифты
- [ ] Печать (style-print_src.css) — не сломалась

---

## Phase 2: Замена Frameset

> **Риск:** Средне-высокий  
> **Влияние:** Критическое  
> **Трудоёмкость:** 5-7 дней  
> **Зависимости:** Phase 1  
> **Принцип:** div-shell + iframe (промежуточный шаг, сохраняет все JSP без изменений)

### 2.1 Цель

Заменить `<frameset>` на div-based layout. Это даёт:
- Работающие URL в браузере (shareable links)
- Кнопки назад/вперёд
- Возможность responsive в будущем
- Современный CSS layout (Grid)

### 2.2 Стратегия: div-shell + iframe

**Почему не чистые div-ы?** Все ~107 JSP-страниц рассчитаны на работу внутри frame. Если убрать iframe сразу, нужно переписать механизм навигации всех страниц. Промежуточный шаг:

```
БЫЛО:                           СТАЛО:
┌──────────────────────┐        ┌──────────────────────┐
│ <frameset>           │        │ <div class="ts-shell">│
│ ┌──────┬───────────┐ │        │ ┌──────┬───────────┐ │
│ │frame │   frame   │ │   →    │ │ div  │  iframe   │ │
│ │tree  │  content  │ │        │ │tree  │  content  │ │
│ └──────┴───────────┘ │        │ └──────┴───────────┘ │
│ </frameset>          │        │ </div>               │
└──────────────────────┘        └──────────────────────┘
```

Левый фрейм → обычный `<div>` (дерево загружается inline или через AJAX).  
Правый фрейм → `<iframe>` (все JSP работают как раньше).  
Позже iframe можно будет заменить на AJAX-загрузку контента.

### 2.3 Новые файлы

#### `src/main/webapp/app-shell.html`

```html
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>TrackStudio</title>
    <link rel="shortcut icon" href="favicon.ico" type="image/x-icon">
    <link rel="icon" href="favicon.png" type="image/png">
    <!-- CSS через <link>, не через <ts:css>, т.к. это не JSP -->
    <link rel="stylesheet" href="style_tokens.css">
    <link rel="stylesheet" href="style_shell.css">
</head>
<body>
    <div class="ts-shell" id="tsShell">
        <aside class="ts-sidebar" id="tsSidebar">
            <iframe id="tsSidebarFrame" src="TreeLoaderAction.do?method=init" 
                    class="ts-sidebar__frame"></iframe>
        </aside>
        <div class="ts-sidebar-toggle" id="tsSidebarToggle" title="Toggle sidebar">
            <span class="ts-sidebar-toggle__icon">&#x276E;</span>
        </div>
        <main class="ts-main" id="tsMain">
            <iframe id="tsContent" name="mainFrame" src="TaskAction.do"
                    class="ts-main__frame"></iframe>
        </main>
    </div>
    <script src="app-shell.js"></script>
</body>
</html>
```

#### `src/main/webapp/style_shell.css`

```css
/* App shell layout — replaces frameset */
* { box-sizing: border-box; margin: 0; padding: 0; }

body {
    font-family: var(--ts-font-family, "Segoe UI", sans-serif);
    overflow: hidden;
    height: 100vh;
}

.ts-shell {
    display: grid;
    grid-template-columns: var(--ts-sidebar-width, 260px) auto 1fr;
    height: 100vh;
    overflow: hidden;
    transition: grid-template-columns 0.2s ease;
}

.ts-shell.sidebar-collapsed {
    grid-template-columns: 0px auto 1fr;
}

.ts-sidebar {
    overflow: hidden;
    border-right: 1px solid var(--ts-border, #d9e4ec);
    background: var(--ts-surface, #ffffff);
    min-width: 0;
}

.ts-sidebar__frame {
    width: 100%;
    height: 100%;
    border: 0;
}

.ts-sidebar-toggle {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 20px;
    cursor: pointer;
    background: var(--ts-surface-muted, #f8fafc);
    border-right: 1px solid var(--ts-border, #d9e4ec);
    user-select: none;
    transition: background 0.15s ease;
}

.ts-sidebar-toggle:hover {
    background: var(--ts-primary-soft, #e6f0f6);
}

.ts-sidebar-toggle__icon {
    font-size: 12px;
    color: var(--ts-text-muted, #5f7383);
    transition: transform 0.2s ease;
}

.sidebar-collapsed .ts-sidebar-toggle__icon {
    transform: rotate(180deg);
}

.ts-main {
    overflow: hidden;
}

.ts-main__frame {
    width: 100%;
    height: 100%;
    border: 0;
}
```

#### `src/main/webapp/app-shell.js`

```javascript
(function() {
    'use strict';
    
    // === Глобальный namespace для межкомпонентной коммуникации ===
    window.TS = window.TS || {};
    
    var shell = document.getElementById('tsShell');
    var sidebar = document.getElementById('tsSidebar');
    var sidebarFrame = document.getElementById('tsSidebarFrame');
    var contentFrame = document.getElementById('tsContent');
    var toggle = document.getElementById('tsSidebarToggle');
    
    // --- Sidebar toggle ---
    var sidebarOpen = localStorage.getItem('ts-sidebar') !== 'closed';
    if (!sidebarOpen) shell.classList.add('sidebar-collapsed');
    
    toggle.addEventListener('click', function() {
        shell.classList.toggle('sidebar-collapsed');
        sidebarOpen = !shell.classList.contains('sidebar-collapsed');
        localStorage.setItem('ts-sidebar', sidebarOpen ? 'open' : 'closed');
    });
    
    window.TS.sidebar = {
        toggle: function() { toggle.click(); },
        isOpen: function() { return sidebarOpen; },
        open: function() { if (!sidebarOpen) toggle.click(); },
        close: function() { if (sidebarOpen) toggle.click(); }
    };
    
    // --- Navigation ---
    window.TS.navigate = function(url) {
        contentFrame.src = url;
    };
    
    // --- Tree API bridge ---
    // Проксирует вызовы в TreeFrame iframe
    window.TS.tree = {
        _call: function(fnName, args) {
            try {
                var win = sidebarFrame.contentWindow;
                if (win && typeof win[fnName] === 'function') {
                    return win[fnName].apply(win, args);
                }
            } catch(e) {
                console.warn('TS.tree.' + fnName + ' failed:', e);
            }
        },
        reload: function(hint) { this._call('reloadTsTree', [hint]); },
        reloadUser: function(hint, nodes) { this._call('reloadTsUserTree', [hint, nodes]); },
        expand: function(path) { this._call('expandTsTree', [path]); },
        selectNodes: function(nodes) { this._call('selectNodesTsTree', [nodes]); },
        selectUsers: function(nodes) { this._call('selectUsersTsTree', [nodes]); },
        updateBookmarks: function(url) { this._call('updateBookmarks', [url]); },
        isLoaded: function() {
            try {
                return sidebarFrame.contentWindow.TREE_LOADED === true;
            } catch(e) { return false; }
        }
    };
    
    // --- URL sync: обновляем URL браузера при навигации в iframe ---
    contentFrame.addEventListener('load', function() {
        try {
            var innerUrl = contentFrame.contentWindow.location.href;
            var path = contentFrame.contentWindow.location.pathname +
                       contentFrame.contentWindow.location.search;
            // Обновляем title
            var innerTitle = contentFrame.contentDocument.title;
            if (innerTitle) document.title = innerTitle;
            // Обновляем URL в адресной строке (без перезагрузки)
            if (path && path !== '/app-shell.html') {
                history.replaceState({path: path}, innerTitle, path);
            }
        } catch(e) {
            // cross-origin — игнорируем
        }
    });
    
    // --- Backward compatibility ---
    // Для страниц, которые ещё используют self.top.frames[0/1]
    // Создаём прокси-объект на window.frames
    // Этот трюк позволяет `self.top.frames[0]` вернуть sidebar iframe
    // и `self.top.frames[1]` вернуть content iframe
    // (в div-based layout window.frames нумерует iframes по порядку)
    // Это работает автоматически, т.к. iframes регистрируются в window.frames
    
})();
```

### 2.4 Миграция cross-frame вызовов

**Этап 1: Совместимость**

В новом `app-shell.html` два iframe идут в порядке: sidebar (index 0) + content (index 1). Это означает, что `self.top.frames[0]` и `self.top.frames[1]` **автоматически** указывают на правильные iframe. Старый код будет работать **без изменений** на этом этапе.

**Этап 2: Постепенная миграция (рекомендуется, но не блокирует)**

Заменять `self.top.frames[0].xxx()` на `window.top.TS.tree.xxx()` файл за файлом:

| Файл | Вызовы | Замена |
|---|---|---|
| `TaskHeader.jsp` | 17 × `self.top.frames[0].reloadTsTree(...)` и др. | `window.top.TS.tree.reload(...)` |
| `UserHeader.jsp` | 6 × `self.top.frames[0].reloadTsUserTree(...)` и др. | `window.top.TS.tree.reloadUser(...)` |
| `ListLayout.jsp` | 1 × `self.top.frames[0].updateBookmarks(...)` | `window.top.TS.tree.updateBookmarks(...)` |
| `TaskInfo.jsp` | 1 × `self.top.frames[0].updateBookmarks(...)` | `window.top.TS.tree.updateBookmarks(...)` |
| `FileInfo.jsp` | 1 × `self.top.frames[0].updateBookmarks(...)` | `window.top.TS.tree.updateBookmarks(...)` |
| `DocumentInfo.jsp` | 1 × `self.top.frames[0].updateBookmarks(...)` | `window.top.TS.tree.updateBookmarks(...)` |
| `UserView.jsp` | 1 × `self.top.frames[0].updateBookmarks(...)` | `window.top.TS.tree.updateBookmarks(...)` |
| `View.jsp (user/list)` | 1 × `self.top.frames[0].updateBookmarks(...)` | `window.top.TS.tree.updateBookmarks(...)` |

**Java-файлы:** заменить генерацию `self.top.frames[1].location` на `self.top.frames[1].location` (уже совместимо) или на новый `window.top.TS.navigate(url)`:

| Java-файл | Строки |
|---|---|
| `TaskEditAction.java` | 594, 611 |
| `MessageCreateAction.java` | 462, 473, 603 |
| `SubtaskAction.java` | 938 |
| `UserEditAction.java` | 244, 436 |
| `UserListAction.java` | 408 |
| `BookmarkServlet.java` | 71, 77, 97, 100 |

### 2.5 Обновление `ListLayout.jsp` — детекция

**Текущий код (строки 66-93):** Если страница открыта вне frameset — редирект в frameset.

**Замена:**

```javascript
if (parent === self) {
    // Страница открыта напрямую, не внутри app-shell
    var url = encodeURIComponent(self.location.pathname + self.location.search);
    self.location.replace('app-shell.html?url=' + url);
}
```

В `app-shell.js` добавить обработку `?url=`:
```javascript
var params = new URLSearchParams(window.location.search);
var initialUrl = params.get('url');
if (initialUrl) {
    contentFrame.src = decodeURIComponent(initialUrl);
}
```

### 2.6 Редирект со старого frameset

**Файл:** `staticframeset.html` — заменить содержимое на редирект:

```html
<!DOCTYPE html>
<html>
<head><title>Redirecting...</title></head>
<body>
<script>
    // Редирект на новый app-shell
    window.location.replace('app-shell.html' + window.location.search);
</script>
</body>
</html>
```

### 2.7 Slidingframe.js — что делать

**Вариант A (рекомендуется):** Оставить `slidingframe.js` как есть на этом этапе. Функции WebFX Tree (addTaskToTree и т.д.) возможно ещё вызываются из Java. Они будут работать через `self.top.frames[0]` = sidebar iframe.

**Вариант B (позже):** Удалить все WebFX-функции (строки 43-558), оставить только `showTree()` → заменить на `window.top.TS.sidebar.toggle()`.

### 2.8 Файлы для изменения

| Файл | Действие |
|---|---|
| *(новый)* `src/main/webapp/app-shell.html` | Новый entry point |
| *(новый)* `src/main/webapp/style_shell.css` | Layout стили |
| *(новый)* `src/main/webapp/app-shell.js` | Shell logic + TS namespace |
| `src/main/webapp/staticframeset.html` | Редирект на app-shell |
| `src/main/webapp/jsp/layout/ListLayout.jsp` | Строки 66-93: обновить детекцию |

### 2.9 Тестирование

- [ ] Открытие app-shell.html — отображается sidebar + content
- [ ] Клик по дереву задач — навигация в content iframe
- [ ] Клик по дереву пользователей — навигация в content iframe
- [ ] Создание задачи — дерево обновляется (`reloadTsTree`)
- [ ] Удаление задачи — дерево обновляется
- [ ] Toggle sidebar — скрывается/показывается, состояние сохраняется
- [ ] Открытие прямого URL (например `/task/123`) — редирект в app-shell
- [ ] Закладки — обновляются через `updateBookmarks`
- [ ] Кнопки назад/вперёд в браузере
- [ ] URL в адресной строке обновляется при навигации

---

## Phase 3: Header и навигация

> **Риск:** Средний  
> **Влияние:** Высокое  
> **Трудоёмкость:** 3-4 дня  
> **Зависимости:** Phase 2

### 3.1 Новая структура TaskHeader.jsp

Текущая структура (строки 250-303):
```html
<div class="login">
    <table width="100%" style="height:25px;">
        <tr><td>Logo</td><td>Search</td></tr>
        <tr><td colspan="2">User info + logout</td></tr>
    </table>
</div>
```

**Замена на:**
```html
<header class="ts-header">
    <div class="ts-header__brand ts-brand-inline">
        <span class="ts-brand-mark">TS</span>
        <span class="ts-brand-name">TrackStudio</span>
    </div>
    <div class="ts-header__search">
        <input type="text" class="ts-search-input" id="key" placeholder="Поиск задач... (Ctrl+K)" autocomplete="off">
    </div>
    <div class="ts-header__user">
        <span class="ts-avatar" title="${sc.user.name}">${initials}</span>
        <span class="ts-header__username">${sc.user.name}</span>
        <a href="..." class="ts-header__logout">Выход</a>
    </div>
</header>
```

### 3.2 Замена TSMenu

**Проблема:** TSMenu (641 строк) — это полностью кастомная система меню с собственным рендерингом, позиционированием, browser-sniffing для IE5/6/7/Opera.

**Стратегия:** Создать `ts-dropdown.js` — drop-in replacement с тем же API:

```javascript
// Конструкторы остаются совместимыми:
// new TSMenuItem(label, url, disabled, selected, icon, action, description)
// new TSMenuBar()
// new TSMenuBut(label, url, menu, icon)
// new TSMenuSeparator()
//
// Но внутренний рендеринг — современный.
```

**Ключевые отличия нового ts-dropdown.js:**
- Рендеринг через `<div class="ts-menu">` вместо inline HTML
- Позиционирование через CSS (`position: absolute`) + минимальный JS для flip
- Без browser-sniffing (поддержка только современных браузеров)
- Клавиатурная навигация (Escape, Arrow keys)
- Закрытие по клику вне

**Файлы для изменения:**

| Файл | Действие |
|---|---|
| `src/main/webapp/jsp/task/TaskHeader.jsp` | Строки 250-303: новая HTML-структура header. Строки 305-486: toolbar |
| `src/main/webapp/jsp/user/UserHeader.jsp` | Аналогично TaskHeader |
| *(новый)* `src/main/webapp/html/ts-dropdown.js` | Замена tsmenu.js |
| `src/main/webapp/html/tsmenu/tsmenu.js` | ~~Удалить~~ → оставить как fallback, потом удалить |
| `src/main/webapp/jsp/layout/ListLayout.jsp` | Строка 184: подключить ts-dropdown.js вместо tsmenu.js |

### 3.3 Тестирование

- [ ] Верхний бар: лого, поиск, пользователь
- [ ] Поиск: autocomplete работает
- [ ] Dropdown-меню "Управление задачами" — все пункты на месте
- [ ] Dropdown-меню "Добавить" — категории видны
- [ ] Toolbar: все ссылки/кнопки работают
- [ ] Keyboard shortcuts: Ctrl+K → фокус на поиск

---

## Phase 4: Список задач

> **Риск:** Средний  
> **Влияние:** Высокое  
> **Трудоёмкость:** 4-5 дней  
> **Зависимости:** Phase 1, Phase 2

### 4.1 Файл: `src/main/webapp/jsp/task/subtasks/Subtasks.jsp`

Это основной рабочий экран TrackStudio — список подзадач.

### 4.2 Стилизация таблицы

CSS-изменения (без изменения JSP-структуры, насколько возможно):

- Строки: hover-эффект, увеличенный padding
- **Статус:** цветной badge (pill) вместо текста
  ```css
  .ts-status-badge {
      display: inline-block;
      padding: 2px 8px;
      border-radius: 12px;
      font-size: var(--ts-font-xs);
      font-weight: 600;
  }
  ```
- **Приоритет:** цветная точка/иконка
- **Номер задачи:** моноширинный, приглушённый цвет (`var(--ts-text-muted)`)
- **Handler:** аватар с инициалами (CSS pseudo-element или маленький `<span>`)
- **Даты:** добавить JS для relative time ("2 часа назад") с title=абсолютная дата

### 4.3 Quick search над таблицей

Добавить в `Subtasks.jsp` перед таблицей:

```html
<div class="ts-quick-filter">
    <input type="text" class="ts-quick-filter__input" 
           placeholder="Быстрый поиск по списку..." id="quickFilter">
</div>
```

JS: фильтрация видимых строк таблицы по введённому тексту (client-side).

### 4.4 Пагинация

Текущий `DIV.slider` → современный пагинатор:

```css
.ts-pagination {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: var(--ts-space-2);
    padding: var(--ts-space-3) 0;
}
.ts-pagination__item { /* кнопка страницы */ }
.ts-pagination__item--active { /* текущая страница */ }
.ts-pagination__info { /* "Показано 1-25 из 143" */ }
```

### 4.5 Bulk actions

При выборе чекбоксов — плавающая панель:

```html
<div class="ts-bulk-bar" id="bulkBar" style="display:none">
    <span class="ts-bulk-bar__count">Выбрано: <span id="bulkCount">0</span></span>
    <button class="ts-btn ts-btn--secondary">Архивировать</button>
    <button class="ts-btn ts-btn--danger">Удалить</button>
</div>
```

### 4.6 Файлы для изменения

| Файл | Действие |
|---|---|
| `src/main/webapp/jsp/task/subtasks/Subtasks.jsp` | HTML-обновления |
| `style_components.css` | Стили таблицы задач, пагинации, bulk bar |
| *(новый)* `src/main/webapp/html/quick-filter.js` | Client-side фильтрация |

---

## Phase 5: Страница задачи

> **Риск:** Средний  
> **Влияние:** Высокое  
> **Трудоёмкость:** 5-6 дней  
> **Зависимости:** Phase 1, Phase 2

### 5.1 Файл: `src/main/webapp/jsp/task/viewtask/TaskInfo.jsp`

### 5.2 Двухколоночный layout

```
┌─────────────────────────────────┬──────────────────┐
│ ts-task-main (70%)              │ ts-task-sidebar   │
│                                 │ (30%)             │
│ Описание                        │                  │
│ ──────────                      │ Свойства         │
│ Текст описания задачи...        │ ──────────       │
│                                 │ Статус: [badge]  │
│ Активность                      │ Приоритет: Высок │
│ ──────────                      │ Handler: Иванов  │
│ 📝 Иванов (2ч назад)            │ Дедлайн: 20 фев │
│    Текст комментария            │ Бюджет: 8ч       │
│                                 │                  │
│ 🔄 Статус → В работе (5ч назад) │ Доп. поля        │
│                                 │ ──────────       │
│ 📎 Петров приложил файл.pdf     │ Поле 1: ...      │
│                                 │                  │
│ ┌──────────────────────────┐    │ Вложения         │
│ │ Добавить комментарий...  │    │ ──────────       │
│ └──────────────────────────┘    │ 📎 file.pdf      │
└─────────────────────────────────┴──────────────────┘
```

```css
.ts-task-layout {
    display: grid;
    grid-template-columns: 1fr 320px;
    gap: var(--ts-space-4);
    max-width: var(--ts-max-content);
}
```

### 5.3 Quick actions — ключевое UX-улучшение

**Текущая проблема:** Чтобы закрыть задачу, пользователь должен:
1. Понять, что статус меняется через "сообщения"
2. Найти нужное действие в меню сообщений
3. Открыть форму MessageCreate.jsp
4. Заполнить (опционально) комментарий
5. Нажать "Сохранить"

**Решение:** Показывать workflow-переходы как **явные кнопки** над формой комментария:

```html
<!-- Генерируется из ${messageActions} -->
<div class="ts-task-actions">
    <button class="ts-btn ts-btn--primary" data-action="take">Взять в работу</button>
    <button class="ts-btn ts-btn--success" data-action="close">Закрыть</button>
    <button class="ts-btn ts-btn--outline" data-action="reject">Отклонить</button>
</div>
```

Каждая кнопка = shortcut к соответствующему message action (то, что сейчас спрятано в TSMenu). Нажатие открывает inline-форму комментария с предвыбранным workflow-переходом.

### 5.4 Activity timeline

Файл: `src/main/webapp/jsp/task/viewtask/messages/MessagesTile.jsp`

Переработать рендеринг сообщений в стиле activity feed:

```css
.ts-activity-item {
    display: flex;
    gap: var(--ts-space-3);
    padding: var(--ts-space-3) 0;
    border-bottom: 1px solid var(--ts-border);
}
.ts-activity-item__avatar { /* круг с инициалами */ }
.ts-activity-item__content { flex: 1; }
.ts-activity-item__meta { /* автор, время */ }
.ts-activity-item__body { /* текст сообщения */ }
```

### 5.5 Файлы для изменения

| Файл | Действие |
|---|---|
| `src/main/webapp/jsp/task/viewtask/TaskInfo.jsp` | Layout: 2 колонки, quick actions |
| `src/main/webapp/jsp/task/viewtask/messages/MessagesTile.jsp` | Activity timeline |
| `src/main/webapp/jsp/task/viewtask/messages/Messages.jsp` | Контейнер сообщений |
| `style_components.css` | Стили activity feed, task sidebar |

---

## Phase 6: Замена YUI Dialogs

> **Риск:** Низкий  
> **Влияние:** Среднее  
> **Трудоёмкость:** 1-2 дня  
> **Зависимости:** Нет (можно параллельно с любой фазой)

### 6.1 Текущие YUI-диалоги

Все 4 определены в `ListLayout.jsp` (строки 394-436):

| Переменная | Назначение | Ширина |
|---|---|---|
| `YAHOO.trackstudio.bookmark.bookmark_dialog` | Сохранение закладки | 600px |
| `YAHOO.trackstudio.bookmark.view_dialog` | Просмотр | 400px |
| `YAHOO.trackstudio.bookmark.dialog_test_reg_exp` | Тестирование регулярных выражений | 400px |
| `YAHOO.trackstudio.bookmark.post_filter_save_as` | Сохранение фильтра | 400px |

### 6.2 Новый компонент: `ts-dialog.js`

```javascript
/**
 * TSDialog — замена YAHOO.widget.Dialog
 * API максимально совместим для минимальных изменений в ListLayout.jsp
 */
function TSDialog(elementId, config) {
    this.element = document.getElementById(elementId);
    this.config = config || {};
    this.overlay = null;
    this._init();
}

TSDialog.prototype._init = function() {
    // Создать backdrop overlay
    // Привязать Escape
    // Создать кнопки из config.buttons
    // Focus trap
};

TSDialog.prototype.show = function() { /* ... */ };
TSDialog.prototype.hide = function() { /* ... */ };
TSDialog.prototype.render = function() { /* no-op для совместимости */ };
```

CSS:
```css
.ts-dialog-overlay {
    position: fixed;
    inset: 0;
    background: rgba(0, 0, 0, 0.4);
    z-index: var(--ts-z-modal-backdrop);
    opacity: 0;
    transition: opacity var(--ts-transition);
}
.ts-dialog-overlay.visible { opacity: 1; }

.ts-dialog {
    position: fixed;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%) scale(0.95);
    background: var(--ts-surface);
    border-radius: var(--ts-radius-md);
    box-shadow: 0 20px 60px rgba(0,0,0,0.15);
    z-index: var(--ts-z-modal);
    opacity: 0;
    transition: all var(--ts-transition);
    max-width: 90vw;
    max-height: 90vh;
    overflow: auto;
}
.ts-dialog.visible {
    opacity: 1;
    transform: translate(-50%, -50%) scale(1);
}
```

### 6.3 Замена в ListLayout.jsp

```javascript
// БЫЛО:
YAHOO.trackstudio.bookmark.bookmark_dialog = new YAHOO.widget.Dialog("bookmark_dialog", {...});

// СТАЛО:
YAHOO.trackstudio.bookmark.bookmark_dialog = new TSDialog("bookmark_dialog", {...});
```

**API кнопок совместим:** `buttons: [{text: "OK", handler: fn, isDefault: true}]`

### 6.4 Удаление YUI зависимостей

После замены диалогов — удалить из `ListLayout.jsp` (строки 192-197):
```jsp
<ts:jsLink link="${urlHtml}/colorpicker/js/utilities.js"/>
<ts:jsLink link="${urlHtml}/colorpicker/js/container-min.js"/>
```

**Внимание:** `colorpicker` всё ещё использует YUI Slider. Его нужно заменить отдельно (можно на `<input type="color">`).

### 6.5 Замена `confirm()` вызовов

8 вызовов `confirm()` в 6 JSP-файлах. Заменить на стилизованный:

```javascript
TSDialog.confirm("Удалить задачу #123?", function(confirmed) {
    if (confirmed) { /* действие */ }
});
```

### 6.6 Файлы для изменения

| Файл | Действие |
|---|---|
| *(новый)* `src/main/webapp/html/ts-dialog.js` | Компонент диалогов |
| `src/main/webapp/jsp/layout/ListLayout.jsp` | Строки 394-449: заменить YAHOO.widget.Dialog → TSDialog |
| `src/main/webapp/jsp/task/TaskHeader.jsp` | Строки 120, 124: confirm() → TSDialog.confirm() |
| `src/main/webapp/jsp/user/UserHeader.jsp` | confirm() → TSDialog.confirm() |
| `style_components.css` | Стили диалогов |

---

## Phase 7: Обновление библиотек

> **Риск:** Средний  
> **Влияние:** Среднее  
> **Трудоёмкость:** 3-4 дня  
> **Зависимости:** Нет (можно параллельно, желательно до Phase 4-5)

### 7.1 jQuery 1.11.2 → 3.7+

**Breaking changes для аудита:**

| Deprecated/Removed | Замена | Поиск |
|---|---|---|
| `.size()` | `.length` | `rg '\.size\(\)'` |
| `.andSelf()` | `.addBack()` | `rg '\.andSelf\(\)'` |
| `.bind()` | `.on()` | `rg '\.bind\('` |
| `.unbind()` | `.off()` | `rg '\.unbind\('` |
| `.delegate()` | `.on()` | `rg '\.delegate\('` |
| `.live()` | `.on()` | `rg '\.live\('` |
| `.error()` (event) | `.on('error')` | `rg '\.error\('` |
| `$.isArray()` | `Array.isArray()` | — |
| `$.parseJSON()` | `JSON.parse()` | — |
| `$.trim()` | `str.trim()` | — |

**Стратегия:**
1. Подключить [jQuery Migrate 3.x](https://github.com/jquery/jquery-migrate) — логирует deprecated вызовы
2. Протестировать все основные сценарии
3. Исправить найденные проблемы
4. Убрать jQuery Migrate

**Файлы:**
- `src/main/webapp/html/jquery/jquery-1.11.2.min.js` → заменить на jQuery 3.7.1
- `src/main/webapp/html/jquery/jquery-ui.min.js` → обновить до совместимой версии
- `src/main/webapp/html/fancytree-2.31.0/lib/jquery.js` → обновить (дерево грузит свой jQuery!)

**Внимание:** TreeFrame.jsp грузит **отдельную копию jQuery** через FancyTree. Нужно убедиться, что обе копии обновлены.

### 7.2 FancyTree 2.31.0 → 2.38+

Минорный апдейт. Проверить:
- Lazy loading работает
- Drag & drop работает
- Context menu работает

**Файлы:**
- `src/main/webapp/html/fancytree-2.31.0/` → обновить до `fancytree-2.38.x/`
- `src/main/webapp/jsp/TreeFrame.jsp` строки 16-23 → обновить пути

### 7.3 TinyMCE

Текущая: старая версия в `src/main/webapp/html/tiny_mce/`.

Обновить до TinyMCE 6 или 7:
- Современный UI
- Улучшенная мобильная поддержка
- Новая система плагинов

**Файлы:**
- `src/main/webapp/html/tiny_mce/` → заменить на новую версию
- JSP-файлы, инициализирующие TinyMCE → обновить конфигурацию

---

## Phase 8: UX создания и редактирования

> **Риск:** Средний  
> **Влияние:** Высокое  
> **Трудоёмкость:** 4-5 дней  
> **Зависимости:** Phase 5

### 8.1 Кнопка "Новая задача"

Сейчас: категории в TSMenu dropdown. Для нового пользователя неочевидно.

**Решение:** Явная кнопка в toolbar:

```html
<div class="controlPanel">
    <!-- ... существующие ссылки ... -->
    <button class="ts-btn ts-btn--primary ts-btn--create" id="createTaskBtn">
        + Новая задача
    </button>
</div>
```

При нажатии — dropdown с категориями (если их несколько) или сразу открытие формы.

Файл: `TaskHeader.jsp` строки 408-476 (блок с категориями).

### 8.2 Форма создания задачи

**Файлы:** `CreateTaskTile.jsp`, `TaskEdit.jsp`

Сейчас двухшаговый процесс:
1. `CreateTaskTile.jsp` — название + категория → POST
2. `TaskEdit.jsp` — все поля

**Улучшение (без изменения бэкенда):**
- Progressive disclosure: сначала показать Name + Description
- "Показать больше" → открывает Handler, Priority, Deadline, Custom Fields
- Drag & drop зона для файлов

### 8.3 Терминология: "Message" → "Комментарий"

В language properties:

| Ключ | Текущее значение | Новое значение |
|---|---|---|
| `MESSAGE_CREATE` | "Create Message" | "Add Comment" |
| `MESSAGE_LIST` | "Messages" | "Comments" |
| и т.д. | | |

**Файлы:**
- `src/main/resources/language_en.properties`
- `src/main/resources/language_ru.properties`

### 8.4 Файлы для изменения

| Файл | Действие |
|---|---|
| `src/main/webapp/jsp/task/TaskHeader.jsp` | Кнопка "Новая задача" |
| `src/main/webapp/jsp/task/tiles/CreateTaskTile.jsp` | Модернизация формы |
| `src/main/webapp/jsp/task/viewtask/edit/TaskEdit.jsp` | Progressive disclosure |
| `src/main/webapp/jsp/task/viewtask/messages/MessageCreate.jsp` | Inline-форма |
| `src/main/resources/language_en.properties` | Терминология |
| `src/main/resources/language_ru.properties` | Терминология |

---

## Phase 9: Responsive Design

> **Риск:** Средний  
> **Влияние:** Среднее  
> **Трудоёмкость:** 3-4 дня  
> **Зависимости:** Phase 2 (обязательно — frameset = zero responsive)

### 9.1 Breakpoints

```css
/* Desktop (по умолчанию) */
.ts-shell {
    grid-template-columns: var(--ts-sidebar-width) auto 1fr;
}

/* Tablet: sidebar как overlay */
@media (max-width: 1199px) {
    .ts-shell {
        grid-template-columns: 0px 0px 1fr;
    }
    .ts-sidebar {
        position: fixed;
        top: 0;
        left: 0;
        bottom: 0;
        width: var(--ts-sidebar-width);
        z-index: var(--ts-z-sidebar);
        transform: translateX(-100%);
        transition: transform var(--ts-transition-slow);
    }
    .ts-shell:not(.sidebar-collapsed) .ts-sidebar {
        transform: translateX(0);
        box-shadow: 4px 0 20px rgba(0,0,0,0.15);
    }
}

/* Mobile */
@media (max-width: 767px) {
    .ts-header { flex-wrap: wrap; }
    .ts-header__search { order: 3; width: 100%; }
    .ts-task-layout { grid-template-columns: 1fr; }
    TABLE.general { font-size: var(--ts-font-sm); }
    TABLE.general TH { display: none; }
    TABLE.general TD { display: block; padding: var(--ts-space-1) var(--ts-space-2); }
}
```

### 9.2 Touch-friendly

```css
@media (pointer: coarse) {
    /* Увеличить touch targets */
    div.controlPanel A { min-height: 44px; padding: var(--ts-space-2) var(--ts-space-3); }
    INPUT[type="checkbox"] { width: 20px; height: 20px; }
    .ts-sidebar-toggle { width: 44px; }
}
```

### 9.3 Файлы для изменения

| Файл | Действие |
|---|---|
| `src/main/webapp/style_shell.css` | Media queries для shell |
| `src/main/webapp/style_components.css` | Media queries для компонентов |
| `src/main/webapp/app-shell.js` | Touch events для sidebar |

---

## Phase 10: Polish и Accessibility

> **Риск:** Низкий  
> **Влияние:** Среднее  
> **Трудоёмкость:** 3-4 дня  
> **Зависимости:** Phase 1-9

### 10.1 Micro-interactions

```css
/* Skeleton loading */
.ts-skeleton {
    background: linear-gradient(90deg, var(--ts-surface-muted) 25%, #e8eef3 50%, var(--ts-surface-muted) 75%);
    background-size: 200% 100%;
    animation: ts-skeleton-pulse 1.5s ease-in-out infinite;
    border-radius: var(--ts-radius-sm);
}

@keyframes ts-skeleton-pulse {
    0% { background-position: 200% 0; }
    100% { background-position: -200% 0; }
}

/* Toast notifications */
.ts-toast {
    position: fixed;
    bottom: var(--ts-space-4);
    right: var(--ts-space-4);
    padding: var(--ts-space-3) var(--ts-space-4);
    background: var(--ts-text);
    color: #ffffff;
    border-radius: var(--ts-radius-md);
    box-shadow: 0 10px 40px rgba(0,0,0,0.2);
    z-index: var(--ts-z-toast);
    animation: ts-toast-in 0.3s ease;
}
```

### 10.2 Accessibility

- ARIA-атрибуты: `role="navigation"`, `role="main"`, `aria-label` на кнопки
- Focus management: `tabindex`, `:focus-visible`
- Contrast: проверить все цвета на WCAG AA (4.5:1 для текста)
- Screen reader: `aria-live="polite"` для динамических обновлений

### 10.3 Dark mode

```css
@media (prefers-color-scheme: dark) {
    :root {
        --ts-bg: #0d1117;
        --ts-surface: #161b22;
        --ts-surface-muted: #1c2129;
        --ts-border: #30363d;
        --ts-border-strong: #484f58;
        --ts-text: #e6edf3;
        --ts-text-muted: #8b949e;
        --ts-primary: #58a6ff;
        --ts-primary-strong: #79c0ff;
        --ts-primary-soft: #0d2240;
        --ts-shadow: 0 6px 20px rgba(0, 0, 0, 0.3);
    }
}

/* Ручной переключатель */
[data-theme="dark"] {
    /* те же переменные */
}
```

---

## Сводная таблица

| # | Phase | Трудоёмкость | Риск | Влияние | Зависимости | Файлы JSP | Файлы CSS | Файлы JS | Файлы Java |
|---|---|---|---|---|---|---|---|---|---|
| 1 | CSS Foundation | 2-3 дня | Очень низкий | Высокое | — | 3 (подключение) | **3 новых** + 2 обновления | 0 | 0 |
| 2 | Frameset → div | 5-7 дней | Средне-высокий | Критическое | Phase 1 | 1 (ListLayout) | **1 новый** | **1 новый** + 1 обновление | 0* |
| 3 | Header & Nav | 3-4 дня | Средний | Высокое | Phase 2 | 2 (headers) | 1 обновление | **1 новый** | 0 |
| 4 | Task List | 4-5 дней | Средний | Высокое | Phase 1, 2 | 1 (Subtasks) | 1 обновление | **1 новый** | 0 |
| 5 | Task Detail | 5-6 дней | Средний | Высокое | Phase 1, 2 | 3 (TaskInfo, Messages) | 1 обновление | 0 | 0 |
| 6 | YUI → Dialogs | 1-2 дня | Низкий | Среднее | — | 3 (Layout, headers) | 1 обновление | **1 новый** | 0 |
| 7 | Libraries | 3-4 дня | Средний | Среднее | — | 1 (TreeFrame) | 0 | ~5 обновлений | 0 |
| 8 | Create/Edit UX | 4-5 дней | Средний | Высокое | Phase 5 | 5 (forms, props) | 1 обновление | 0 | 0 |
| 9 | Responsive | 3-4 дня | Средний | Среднее | Phase 2 | 0 | 2 обновления | 1 обновление | 0 |
| 10 | Polish & A11y | 3-4 дня | Низкий | Среднее | Phase 1-9 | ~10 (ARIA) | 1 обновление | **1 новый** | 0 |
| | **Итого** | **~34-44 дня** | | | | | | | |

\* Java-файлы (6 шт.) можно обновить позже — старый `self.top.frames` код работает в iframe layout.

---

## Рекомендуемый порядок

```
Неделя 1:  Phase 1 (CSS Foundation) + Phase 6 (YUI Dialogs)    ← быстрые победы
Неделя 2:  Phase 2 (Frameset → div)                             ← фундамент
Неделя 3:  Phase 7 (Library Updates) + Phase 3 (Header)         ← параллельно
Неделя 4:  Phase 4 (Task List)                                  ← главный экран
Неделя 5:  Phase 5 (Task Detail)                                ← второй экран
Неделя 6:  Phase 8 (Create/Edit UX)                             ← UX улучшения
Неделя 7:  Phase 9 (Responsive) + Phase 10 (Polish)             ← финальная полировка
```

**Рекомендация:** начать с **Phase 1** — это безрисковое CSS-only изменение с максимальным визуальным эффектом.

---

## Приложение A: Полный список файлов с `self.top.frames`

### JSP-файлы

| Файл | Строки | Вызовы |
|---|---|---|
| `jsp/task/TaskHeader.jsp` | 135, 138, 141, 144, 147, 150, 153, 156, 159, 162, 165, 168, 171, 174, 182, 184 | `reloadTsTree`, `selectNodesTsTree`, `expandTsTree`, `TREE_LOADED` |
| `jsp/user/UserHeader.jsp` | 56, 59, 62, 65, 68 | `reloadTsUserTree`, `selectUsersTsTree` |
| `jsp/layout/ListLayout.jsp` | 348 | `updateBookmarks` |
| `jsp/task/viewtask/TaskInfo.jsp` | 885 | `updateBookmarks` |
| `jsp/task/viewtask/FileInfo.jsp` | 200 | `updateBookmarks` |
| `jsp/task/viewtask/DocumentInfo.jsp` | 136 | `updateBookmarks` |
| `jsp/user/user/view/UserView.jsp` | 673 | `updateBookmarks` |
| `jsp/user/list/View.jsp` | 588 | `updateBookmarks` |
| `jsp/TreeFrame.jsp` | 56, 167 | `self.top.frames[1].location` (навигация из дерева) |

### Java-файлы

| Файл | Строки | Контекст |
|---|---|---|
| `action/task/TaskEditAction.java` | 594, 611 | `self.top.frames[1].location` в action строке для дерева |
| `action/task/MessageCreateAction.java` | 462, 473, 603 | То же |
| `action/task/SubtaskAction.java` | 938 | То же |
| `action/user/UserEditAction.java` | 244, 436 | То же |
| `action/user/UserListAction.java` | 408 | То же |
| `action/BookmarkServlet.java` | 71, 77, 97, 100 | `self.top.frames[1].location` для навигации по закладкам |

### JS-файлы

| Файл | Вхождений | Контекст |
|---|---|---|
| `html/slidingframe.js` | 58 | Операции с деревом через WebFX API + frameset manipulation |

---

## Приложение B: Полный список CSS-классов для миграции

| Legacy-класс | Кол-во в JSP | Modern-замена |
|---|---|---|
| `TABLE.general` | ~220 | Перестилизация в Phase 1 (CSS-only) |
| `div.controlPanel` | 46 файлов | Перестилизация в Phase 1 + Phase 3 |
| `DIV.slider` (пагинация) | ~5 | `.ts-pagination` в Phase 4 |
| `TABLE.tabbedpane` | ~5 | Перестилизация в Phase 1 (CSS-only) |
| `DIV.logopath` (breadcrumb) | ~2 | Перестилизация в Phase 1 (CSS-only) |
| `DIV.taskTitle` | ~2 | Перестилизация в Phase 1 (CSS-only) |
| `div.login` (header) | ~2 | `.ts-header` в Phase 3 |
| `TABLE.login` | ~3 | Перестилизация в Phase 1 (CSS-only) |
| `TABLE.error` | ~1 | `.ts-message-error` (уже есть) |
