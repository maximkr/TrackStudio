# Как сделать свои шаблоны для почтовых уведомлений

В TrackStudio вы можете делать и использовать свои шаблоны для рассылки почтовых уведомлений и подписок. Мы поставляем 2 общих шаблона, текстовый и HTML варианты. Кому-то хватает и их, но многим потребуются шаблоны, соответствующие их корпоративным стандартам и требованиям.

Шаблоны в TrackStudio сделаны [на языке разметки Freemarker](http://freemarker.sourceforge.net/docs/index.html). Чтобы вам не пришлось изучать этот язык, мы покажем вам, как выводить в эти шаблоны основную информацию из TrackStudio.

## Как поменять Subject письма

В тему письма удобнее всего вставлять различные служебные отметки. Например, номер задачи и ее название

```
<#assign Subject="[#${task.number}] ${task.name}"/>
```

**Некоторые полезные переменные:**

```
${task.projectAlias} — код проекта (то, что записано у вышестоящей задачи в поле Код)
${reasonForSubject} — код события. События могут быть такими:
"Добавлена задача", "Обновлена задача", "Загружен файл", "Добавлено сообщение", "Тестирование", "Подписка".
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

## Как заменить поле "От кого (From)"

Вы можете поменять как имя пользователя, от которого придет письмо, так и его email. Для этого нужно задать переменные FromEmail и FromUser

```

<#if fromUserEmail??>
<#assign FromEmail="${fromUserEmail}"/>
</#if>
<#if fromUserName??>
<#assign FromUser="${fromUserName}"/>
</#if>
```

Здесь **${fromUserEmail}** и **${fromUserName}** берутся из свойств пользователя в TrackStudio, причем e-mail берется первым из списка, если для пользователя их задано несколько.

Вы можете заменить эти параметры. Например,

```
<#assign FromEmail="callcenter@domain.com"/>
<#assign FromUser="Call Center"/>
```

В этом случае письмо придет от "Call Center <callcenter@domain.com>"

Таким же способом можно поменять другие поля. Например ReplyTo:

```
<#assign ReplyTo="Call Center <callcenter@domain.com>"/>
```

Или CC (carbon copy)

```
<#assign CC="Sales Department <sales@domain.com>, IT Department <it@domain.com>"/>
```

Вы также можете менять остальные заголовки письма, помещая из в Headers, например для использования данных из них в специальных почтовых клиентах или обработчиках.

```
<#assign Headers={"X-Meta":"data"}/>
```

## Как выбрать формат письма

Для писем, отправляемых через систему уведомлений, **нужно** указывать тип содержимого: простой текст или html:

для **html**

```
<#assign ContentType="text/html;\n charset=\"${charset}\""/>,
```

для **простого текста**

```
<#assign ContentType="text/plain;\n charset=\"${charset}\""/>
```

${charset} - это переменная, значение которой соответствует выбранной пользователем в настройках профиля кодировке.

## Как установить кодировку письма

```
<#assign ContentType="text/plain;\n charset=\"${charset}\""/>
```

здесь **${charset}** передается из настроек TrackStudio (trackstudio.encoding), но вы можете задать нужную вам кодировку. В таком случае текст шаблона также должен быть в этой кодировке.

## Как указать ссылку на задачу

Ссылка на ваш экземпляр TrackStudio хранится в переменной **${link}**, соответственно ссылка на задачу будет

```
{link}/task/${task.number}?thisframe=true — для текстового шаблона и
<a href="${link}/task/${task.number}?thisframe">для шаблона HTML</a>
```

## Как вывести описание задачи

Если у вас шаблон в формате HTML, описание выводится просто как **${task.description}**. Для текстового формата можно использовать

```
<#if task.textDescription??>
${task.textDescription}
</#if>
```

## Как вывести значение дополнительного поля

```
${task.udfValues["Заголовок"]}
```

## Как вывести список сообщений

```
<#assign taskMessages = Util.getSortedMessages(task)/>
<#if (taskMessages?size>0)>
<#list taskMessages as msg>
${DateFormatter.parse(msg.time)}: ${msg.textDescription}
</#list>
</#if>
```

При отправке оповещений и уведомлений в переменную reason.code помещается код события. Коды существуют такие:

```
N: Добавлена новая задача
NA: Добавлена новая задача с приложенным файлом
M: Добавлено новое сообщение
MA: Добавлено новое сообщение с приложенным файлом
A: Добавлен новый приложенный файл
U: Изменена задача
S: Рассылка подписки по расписанию
T: Проверка
```

## Как добавить в CC (carbon copy) уведомления пользователей, указанных в дополнительном поле типа "Пользователь" в задаче

```
<#assign simpleTask = Util.simplify(task)/>
<#if simpleTask.udfValues["Пользователи"]??>
<#assign userlist = simpleTask.udfValues["Пользователи"]/>
<#assign CC>
<#list userlist?split(";") as u>
<#assign us= Util.findUser(u?trim)/>
<#if us??>
${us.email}<#if u_has_next>, </#if>
</#if>
</#list>
</#assign>
</#if>
```
