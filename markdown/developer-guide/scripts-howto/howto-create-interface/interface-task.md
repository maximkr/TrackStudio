# Как вывести на страницу текущую задачу

Текущая задача хранится в переменной **task**. Вы можете выводить в шаблоне значения полей задачи, например таких:

```
${task.number} — номер задачи
${task.name} — название задачи
${task.category.name} — категория задачи
${task.status.name} — текущее состояние задачи
${task.resolution.name} — резолюция задачи
${task.priority.name} — приоритет задачи
${task.submitter.name} — имя автора задачи
${task.budgetAsString} — бюджет задачи
${task.actualBudgetAsString} — потраченное время
${DateFormatter.parse(task.submitdate)} — время создания задачи
${DateFormatter.parse(task.updatedate)} — время обновления задачи
${DateFormatter.parse(task.closedate)} — время закрытия задачи (у задачи не в финальном состоянии этого значения нет)
${DateFormatter.parse(task.deadline)} — срок, к которому задачу нужно выполнить (дедлайн)
```

Список подзадач текущей задачи, отфильтрованный фильтром "Мои задачи" можно получить так:

```
<@std.subtasks localTask=task filter="Мои задачи"; taskCollection>
...
<#list taskCollection as t>
...
</#list>
...
</@std.subtasks>
```

Внутри итератора <#list/> следует использовать те же переменные, что и при выводе значений полей одной задачи.

Сам список названий фильтров доступен в переменной **filters**

```
<#list filters as filter>
${filter}
</#list>
```
