# Как создать свой отчет в TrackStudio.

В ТС появилась возможность создавать свои отчеты.

Рассмотрим пример реализации нового отчета типа список.

1. В папке /etc/developement/scripts/scr/main/java/scripts/report/info есть класс ListTaskInfoBuilder.java

**import** com.trackstudio.app.filter.TaskFValue;**import** com.trackstudio.app.report.birt.IBuildReport;**import** com.trackstudio.app.report.birt.Report;**import** com.trackstudio.app.session.SessionContext;**import** com.trackstudio.exception.GranException;**import** com.trackstudio.secured.SecuredReportBean;**import** com.trackstudio.secured.SecuredTaskBean;**import** java.util.HashMap;**import** java.util.List;**public** **class** ListTaskInfoBuilder **implements** IBuildReport, IBuildReport.IList {**private** Report report;**private** Parameters parameters;@Override**public** **void** init() **throws** GranException { **this**.report = **new** ListTask( parameters.getContext(), parameters.getSc(), parameters.getSrb(), parameters.getFv(), parameters.getTaskId(), parameters.getReportType());}@Override**public** **void** setParameters(Parameters parameters) {**this**.parameters = parameters;}@Override**public** Report getReport() {**return** **this**.report;}@Override**public** String getTemplate() {**return** "taskInfo.rptdesign";}@Override**public** HashMap<String, String> getReportParams() **throws** GranException {**return** **this**.report.initReportParameters(**this**.parameters);}**public** **static** **class** ListTask **extends** Report {**public** ListTask(String contextPath,SessionContext sc,SecuredReportBean report,TaskFValue fv,String taskId,String format) **throws** GranException {**super**(contextPath, sc, report, fv, taskId, format);}**public** List<SecuredTaskBean> getList() {**return** **this**.list;}}}

1. Класс должен реализовывать интерфейсы implements IBuildReport, IBuildReport.IList
2. Создаем внутренний класс ListTask extends Report - объект этого класса будет доступен в шаблоне. На основании объекта этого класса будет заполняться шаблон.
3. Класс ListTask имеет один метод getList() - возвращает список отфильтрованных задач.
4. Создаем шаблон taskInfo.rptdesign. Его нужно положить в папку webapps/TrackStudio/reports
5. Указываем что этот шаблон используется для этого отчета

@Override**public** String getTemplate() {**return** "taskInfo.rptdesign";}

1. Дизайн отчета настраивается через Eclipse.
2. Реализация заполнения производиться через скрипт шаблона.

Packages.org.eclipse.birt.report.model.api);importPackage(Packages.com.trackstudio.app.report.birt);importPackage(Packages.java.util);importPackage(Packages.com.trackstudio.app.report.birt.list);var elementFactory = reportContext.getReportRunnable().designHandle.getElementFactory();var store = reportContext.getHttpServletRequest().getAttribute("report");buildHtmlLabel(elementFactory, "This is an example of report, which build a list of tasks!")reportContext.getReportRunnable().designHandle.getBody().add(buildTableTasks(store));function buildTableTasks(store) {var data = store.getList();var rows = data.size();var dt = elementFactory.newTableItem(**null**, 3, 0, rows, 0);dt.setWidth("95%");dt.setStyleName("tableCSS");**for** (var j=0;j!=rows;++j) {var task = data.get(j);var row = dt.getDetail().get(j);buildCell(row.getCells().get(0), task.getName());buildCell(row.getCells().get(1), task.getNumber());buildCell(row.getCells().get(2), task.getCategory().getName());}**return** dt;}function buildCell(cell, value) {label = elementFactory.newTextItem(**null**);label.setContentType("html");label.setContent(value);cell.getContent().add(label);cell.setStyleName("boxCSS")**return** cell;}function buildHtmlLabel(elementFactory, text) {var labelCountTask = elementFactory.newTextItem(**null**);labelCountTask.setStyleName("paddLeft");labelCountTask.setContentType("html");labelCountTask.setContent(text);reportContext.getReportRunnable().designHandle.getBody().add(labelCountTask);}

1. Далее собираем скрипты mvn clean package - выходной ts-scritps.jar нужно положить в etc/plugins/scripts/
2. Нужно прописать новый обработчик trackstudio.adapter.properties

```
handler.report info.ListTaskInfoBuilder.class;
```

1. Создаем отчет типа список и указываем обработчик класс info.ListTaskInfoBuilder.class

![](../../images/handler-report.PNG)
