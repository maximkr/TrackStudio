# Как вывести историю операций с задачей

Список сообщений можно получить так:

```
<#assign msgs = task.messages/>
<#if msgs?exists && (msgs?size>0)>
<#list msgs?reverse as message>
...
</#list>
</#if>
```

С помощью ?reverse список выводится в обратном порядке. Если нужно вывести список сообщений, отфильтрованный фильтром "Мои задачи":

```
<@std.messages localTask=task filter="Мои задачи"; messages>
...
<#list messages as message>
...
</#list>
...
</@std.messages>
```

Здесь в messages возвращается коллекция сообщений

Значения полей:

```
${message.id} - идентификатор операции
<@std.time date=message.time/> - дата и время выполнения операции
${message.submitter.name?html} - имя автора
${message.description?html} - описание операции (комментарий)
${message.mstatus.name} - тип операции
${message.resolution.name} - резолюция
${message.budgetAsString} - бюджет
${message.actualBudgetAsString} - потраченное время
```

Ответственный выводится немного сложнее - тут уже нужны проверки значений на null:

```
<#if message.handlerUserId?exists || msg.handlerGroupId?exists>
<#if message.handlerUserId?exists>
Ответственный: ${message.handlerUser.name}
</#if>
<#if message.handlerGroupId?exists>
Ответственные: ${message.handlerGroup.name}
</#if>
</#if>
```
