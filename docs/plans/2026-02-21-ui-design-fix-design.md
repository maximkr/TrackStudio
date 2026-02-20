# UI Design Fix — Design Document

> Date: 2026-02-21
> Branch: ui-update
> Approach: B — CSS + targeted JSP changes

## Problem Statement

After Phases 1-3 and 6, the app has a modern layout (div-shell, CSS tokens, YUI replaced), but several components still look visually dated:

1. **TreeFrame sidebar headers** — dark teal (`var(--ts-primary)`) header/footer bars, legacy look
2. **img.state** — status dot is a 12×12 square (border-radius: 3px) instead of a circle
3. **Status column** — plain text "Новый", no color badge
4. **Priority column** — plain text "Нормальный", no visual indicator
5. **Task number** — #26 not visually distinct from task name
6. **Header user info** — logout link is sparse, needs better layout

## Changes

### 1. style-panel.css — TreeFrame sidebar

**Before:** `background: var(--ts-primary)` (dark teal) on `.head` and `.foot`, white text
**After:** `background: var(--ts-surface)`, `border-bottom: 1px solid var(--ts-border)`, subtle text with uppercase label, bottom tabs as pill buttons

### 2. style_components.css — Visual improvements

- `img.state` → `border-radius: 50%` (round dot), 10×10px
- `.ts-status-badge` — new class: colored pill using `color-mix()` with `--sc` CSS variable
- `.ts-priority-badge` — new class: colored pill for priority using `data-priority` attribute targeting
- `span.internal` (task number) → monospace, muted color
- Task table cell improvements

### 3. Subtasks.jsp — Status and priority cells

**Status cell:** Add `class="ts-status-badge"` and `style="--sc: ${taskLine.status.color}"` to `<span>`

**Priority cell:** Add `data-priority="${taskLine.priority.name}"` to `<td>` (for CSS targeting)

### 4. TaskHeader.jsp — User info row

Consolidate the second header row — put user name and logout button in one clean line with separator.

## Files Changed

| File | Type |
|---|---|
| `src/main/webapp/style-panel.css` | CSS |
| `src/main/webapp/style_components.css` | CSS |
| `src/main/webapp/jsp/task/subtasks/Subtasks.jsp` | JSP |
| `src/main/webapp/jsp/task/TaskHeader.jsp` | JSP |

## Risk

Low. CSS changes are additive. JSP changes are minimal (add class/style attributes to existing elements).
