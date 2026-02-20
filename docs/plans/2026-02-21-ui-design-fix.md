# UI Design Fix Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Fix 6 visual issues in the TrackStudio UI: sidebar headers, status badges, priority indicators, circular status dots, task number styling, and header user info layout.

**Architecture:** CSS-first changes where possible. Two JSP files get minimal targeted changes (add class/style/data attributes only). No server-side logic changes.

**Tech Stack:** CSS custom properties, `color-mix()` CSS Level 4, JSP JSTL expressions, style-panel.css (TreeFrame), style_components.css (main components), Subtasks.jsp, TaskHeader.jsp.

---

### Task 1: Modernize TreeFrame sidebar headers (style-panel.css)

**Files:**
- Modify: `src/main/webapp/style-panel.css`

**Context:**
The sidebar (TreeFrame) renders 3 panels: panel_1 (tasks), panel_2 (users), panel_3 (bookmarks).
Each panel has a `div.head` and `div.foot`. Currently:
```css
body.ts-tree-body div.head,
body.ts-tree-body div.foot {
    background: var(--ts-primary);  /* ← dark teal, ugly */
}
body.ts-tree-body label.header {
    color: #ffffff;                  /* ← white text on dark bg */
    background-color: var(--ts-primary) !important;
}
```
**Step 1: Replace dark header backgrounds with light styling**

Find the block `body.ts-tree-body div.head,` in `style-panel.css` (around line 115) and replace with:

```css
body.ts-tree-body div.head,
body.ts-tree-body div.foot {
    background: var(--ts-surface);
    border-bottom: 1px solid var(--ts-border);
}

body.ts-tree-body label.header {
    display: flex;
    align-items: center;
    gap: 6px;
    color: var(--ts-text-muted);
    background-color: var(--ts-surface) !important;
    font-family: var(--ts-font-family);
    font-size: var(--ts-font-xs);
    font-weight: 600;
    text-transform: uppercase;
    letter-spacing: 0.6px;
    padding: var(--ts-space-1) var(--ts-space-2);
    text-shadow: none;
    border: none;
    height: auto;
    cursor: pointer;
    transition: color var(--ts-transition), background-color var(--ts-transition);
}

body.ts-tree-body label.header:hover {
    color: var(--ts-primary);
    background-color: var(--ts-primary-soft) !important;
}

body.ts-tree-body label.header img {
    margin-right: 0;
    opacity: 0.5;
    width: 14px;
    height: 14px;
}
```

**Step 2: Verify visually**

Rebuild Docker and check: `docker compose up --build -d && sleep 30`
Open http://localhost:8080/ and log in as root/root.
Expected: sidebar header "Дерево задач" is now white background with small grey uppercase text.

**Step 3: Commit**

```bash
git add src/main/webapp/style-panel.css
git commit -m "style: modernize TreeFrame sidebar headers — light bg instead of dark teal"
```

---

### Task 2: Circular img.state + status badge CSS (style_components.css)

**Files:**
- Modify: `src/main/webapp/style_components.css`

**Context:**
`img.state` is the colored status dot next to task name in Subtasks.jsp:
```jsp
<html:img styleClass="state" border="0" style="background-color: ${taskLine.status.color}" .../>
```
Currently: `border-radius: 3px` (square-ish). Should be `50%` (circle).

The status *column* shows plain text. We'll add `.ts-status-badge` class (next task handles JSP).

**Step 1: Find and update img.state**

In `style_components.css` around line 937, find:
```css
img.state {
    height: 12px;
    width: 12px;
    border-radius: 3px;
    vertical-align: middle;
    margin-right: 4px;
}
```
Replace with:
```css
img.state {
    height: 10px;
    width: 10px;
    border-radius: 50%;
    vertical-align: middle;
    margin-right: 5px;
    display: inline-block;
    flex-shrink: 0;
}
```

**Step 2: Add .ts-status-badge after img.state**

After the `img.state` block, add:
```css

/* ── Status badge ─────────────────────────────────────────── */

.ts-status-badge {
    display: inline-block;
    padding: 2px 8px;
    border-radius: 12px;
    font-size: var(--ts-font-xs);
    font-weight: 600;
    white-space: nowrap;
    line-height: 1.5;
    background-color: color-mix(in srgb, var(--sc, var(--ts-primary)) 12%, white);
    color: var(--sc, var(--ts-primary));
    border: 1px solid color-mix(in srgb, var(--sc, var(--ts-primary)) 22%, transparent);
    vertical-align: middle;
}
```

**Step 3: Add .ts-priority-badge and priority data-attr CSS**

After `.ts-status-badge`, add:
```css

/* ── Priority cell ────────────────────────────────────────── */

/* Wrapper: the <td> will have data-priority="..." set by JSP */
td[data-priority]::before {
    content: "•";
    font-size: 16px;
    line-height: 1;
    vertical-align: middle;
    margin-right: 4px;
    color: var(--ts-text-muted);
}

/* Russian priority names from TrackStudio default data */
td[data-priority="Критический"]::before,
td[data-priority="Critical"]::before    { color: #c23b3b; }

td[data-priority="Высокий"]::before,
td[data-priority="High"]::before        { color: #d68a00; }

td[data-priority="Нормальный"]::before,
td[data-priority="Normal"]::before      { color: var(--ts-text-muted); }

td[data-priority="Низкий"]::before,
td[data-priority="Low"]::before         { color: #8fbccc; }
```

**Step 4: Improve task number span.internal**

Find `span.internal` usage in style_components.css and add/update:
```css

/* ── Task number in list (span.internal is #26 etc.) ─────── */

TABLE.general span.internal {
    font-family: var(--ts-font-mono);
    font-size: var(--ts-font-xs);
    color: var(--ts-text-muted);
    font-weight: 500;
    letter-spacing: 0.2px;
}
```

**Step 5: Verify**
`docker compose up --build -d && sleep 30`
Check task list — the colored dot next to task names should now be circular.

**Step 6: Commit**

```bash
git add src/main/webapp/style_components.css
git commit -m "style: circular status dot, status badge CSS, priority indicator, task number monospace"
```

---

### Task 3: Add status badge and priority data-attr to Subtasks.jsp

**Files:**
- Modify: `src/main/webapp/jsp/task/subtasks/Subtasks.jsp`

**Context:**
Status cell (around line 686-694):
```jsp
<c:if test="${headerStatus.canView}">
    <td>
        <span>
            <c:if test="${taskLine.status ne null}">
                <c:out value="${taskLine.status.name}" escapeXml="true"/>
            </c:if>
        </span>
    </td>
</c:if>
```

Priority cell (around line 702-708):
```jsp
<c:if test="${headerPriority.canView}">
    <td>
        <c:if test="${taskLine.priority ne null}">
            <c:out value="${taskLine.priority.name}" escapeXml="true"/>
        </c:if>
    </td>
</c:if>
```

**Step 1: Update status cell**

Replace the status cell block with:
```jsp
<c:if test="${headerStatus.canView}">
    <td>
        <c:if test="${taskLine.status ne null}">
            <span class="ts-status-badge" style="--sc: ${taskLine.status.color}">
                <c:out value="${taskLine.status.name}" escapeXml="true"/>
            </span>
        </c:if>
    </td>
</c:if>
```

**Step 2: Update priority cell**

Replace the priority cell block with:
```jsp
<c:if test="${headerPriority.canView}">
    <td<c:if test="${taskLine.priority ne null}"> data-priority="<c:out value="${taskLine.priority.name}" escapeXml="true"/>"</c:if>>
        <c:if test="${taskLine.priority ne null}">
            <c:out value="${taskLine.priority.name}" escapeXml="true"/>
        </c:if>
    </td>
</c:if>
```

**Step 3: Verify**
`docker compose up --build -d && sleep 30`
Check http://localhost:8080/ → task list should show colored status pills (e.g., "Новый" in a blue-tinted pill).

**Step 4: Commit**

```bash
git add src/main/webapp/jsp/task/subtasks/Subtasks.jsp
git commit -m "style: status badge and priority indicator in task list table"
```

---

### Task 4: Improve header user info layout (TaskHeader.jsp)

**Files:**
- Modify: `src/main/webapp/jsp/task/TaskHeader.jsp`
- Modify: `src/main/webapp/style_components.css`

**Context:**
Current header structure (around line 282-307 in TaskHeader.jsp):
```html
<tr style="height:5px;">
    <td colspan="2">
        <div class="link" style="float:right;">
            <c:if test="${sc.user.login != 'anonymous'}">
                <I18n:message key="LOGGED_INFO"/>
                <html:link ...><c:out value="${sc.user.name}"/></html:link>
                ...
            </c:if>
            <html:link ...>Войти/Выход</html:link>
        </div>
    </td>
</tr>
```
This is a second table row with float:right div. Needs to be replaced with a flex row that has the user name and logout button side by side cleanly.

**Step 1: Replace the second header row**

Find the second `<tr>` (around line 282) in the `div.login` table:
```html
<tr style="height:5px;">
    <td colspan="2">
        <div class="link" style="float:right;">
```
Replace the entire `<tr style="height:5px;">...</tr>` block with:
```html
<tr>
    <td colspan="2">
        <div class="ts-header-user-row">
            <c:if test="${sc.user.login != 'anonymous'}">
                <span class="ts-header-user-info">
                    <html:link href="javascript:{self.top.frames[1].location = '${contextPath}/UserViewAction.do?id=${sc.userId}'};" class="ts-header-user-link">
                        <c:out value="${sc.user.name}"/>
                    </html:link>
                    <span class="ts-header-user-sep">·</span>
                    <span class="ts-header-user-roles">${prstatuses}</span>
                </span>
            </c:if>
            <c:if test="${regRole && sc.user.login == 'anonymous'}">
                <a class="ts-header-action-link" href="${contextPath}/LoginAction.do?method=registerPage"><I18n:message key="REGISTER"/></a>
            </c:if>
            <html:link class="ts-header-action-link" href="${contextPath}/LoginAction.do?method=logoutPage">
                <c:choose>
                    <c:when test="${sc.user.login == 'anonymous'}">
                        <I18n:message key="LOG_IN"/>
                    </c:when>
                    <c:otherwise>
                        <I18n:message key="LOGOUT"/>
                    </c:otherwise>
                </c:choose>
            </html:link>
        </div>
    </td>
</tr>
```

**Step 2: Add CSS for the new header user row**

In `style_components.css`, find the `div.login A` block (around line 167) and add after it:
```css

.ts-header-user-row {
    display: flex;
    align-items: center;
    justify-content: flex-end;
    gap: var(--ts-space-2);
    padding: 0 0 var(--ts-space-1) 0;
}

.ts-header-user-info {
    display: inline-flex;
    align-items: center;
    gap: var(--ts-space-1);
    font-size: var(--ts-font-xs);
    color: var(--ts-text-muted);
}

.ts-header-user-link {
    font-size: var(--ts-font-xs) !important;
    font-weight: 600;
    color: var(--ts-text) !important;
    text-decoration: none;
}
.ts-header-user-link:hover {
    color: var(--ts-primary) !important;
}

.ts-header-user-sep {
    color: var(--ts-border-strong);
    font-size: var(--ts-font-sm);
}

.ts-header-user-roles {
    font-size: var(--ts-font-xs);
    color: var(--ts-text-muted);
    font-style: italic;
}

.ts-header-action-link {
    font-size: var(--ts-font-xs) !important;
    font-weight: 600;
    color: var(--ts-primary) !important;
    text-decoration: none;
    padding: 2px 8px;
    border: 1px solid var(--ts-border);
    border-radius: var(--ts-radius-sm);
    transition: all var(--ts-transition);
    background: var(--ts-surface);
}
.ts-header-action-link:hover {
    background: var(--ts-primary-soft);
    border-color: var(--ts-primary);
    color: var(--ts-primary-strong) !important;
}
```

**Step 3: Verify**

`docker compose up --build -d && sleep 30`
After login as root/root — header should show user name "Администратор" on the right side with a styled "Выход" button.

**Step 4: Commit**

```bash
git add src/main/webapp/jsp/task/TaskHeader.jsp src/main/webapp/style_components.css
git commit -m "style: clean user info row in header with styled logout button"
```

---

### Task 5: Final visual verification + screenshots

**Step 1: Rebuild and take screenshots**

```bash
docker compose up --build -d
sleep 30
```

Run the CDP screenshot script (saved at `/tmp/cdp-screenshot4.py`) or equivalent to capture:
- `/tmp/ts-final-tree.png` — sidebar
- `/tmp/ts-final-tasklist.png` — task list

**Step 2: Check for regressions**

Verify these pages still look correct:
- Login page: http://localhost:8080/
- Tree frame: http://localhost:8080/TreeLoaderAction.do?method=init
- Task list: http://localhost:8080/TaskAction.do
- Any form page

**Step 3: Commit design doc**

```bash
git add docs/plans/
git commit -m "docs: UI design fix — design and implementation plan"
```
