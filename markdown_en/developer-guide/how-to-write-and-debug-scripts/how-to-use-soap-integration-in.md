[Home](../../index.md) | [Up (How to write and debug scripts in IntelliJ IDEA)](index.md)

---

# How to use SOAP integration in TrackStudio

SOAP project is located in **etc/deveopment/soap**.

Generate resources by command

mvn generate-resources

Open project in IDE. Notice **target/generated-sources/cxf** folder as sources.

![](../../images/soapidea.PNG)

After that, you can start writing your SOAP-client or using a ready example from the project.

You can operate with services or containers in SOAP. Use services to perform different functions. Containers are used for data transfer.

Service for tasks manipulations you can get so:

public static final String url = "http://localhost:8888/TrackStudio/services/";public Task getTaskService(url) throws MalformedURLException { TaskService service = new TaskService(new URL(url + Task.class.getSimpleName() + "?wsdl"), new QName("http://task.service.soap.trackstudio.com/", "TaskService")); return service.getTaskPort(); }

Here you need to substitute your URL

For creating task you need to execute method createTask from TaskService

public String createTask(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "categoryId") String categoryId, @WebParam(name = "shortname") String shortname, @WebParam(name = "name") String name, @WebParam(name = "description") String description, @WebParam(name = "budget") long budget, @WebParam(name = "deadline") long deadline, @WebParam(name = "priorityId") String priorityId, @WebParam(name = "parentId") String parentId, @WebParam(name = "handlerUserId") String handlerUserId, @WebParam(name = "handlerGroupId") String handlerGroupId, @WebParam(name = "udfNames") String[] udfNames, @WebParam(name = "udfValues") String[] udfValues) throws Exception;

If you want to get list of tasks, which UDF equals to the specific value, use the following code:

TaskFvalueBean taskFvalueBean = new TaskFvalueBean(); taskFvalueBean.setSubtask("1"); taskFvalueBean.getUdfs().add("UDF8a80828f4a0619c0014a062a5cc00004->_eq_test"); TaskSliderBean sliderBean = task.getTaskList(sessionId, taskId, taskFvalueBean, true, 20, new ArrayList<String>());

If you want to get tasks, wich have been closed in the specific period, use the following code:

GregorianCalendar date = new GregorianCalendar(2014, 12, 1); TaskFvalueBean taskFvalueBean = new TaskFvalueBean(); taskFvalueBean.setSubtask("1"); taskFvalueBean.setCloseDate("_"+String.valueOf(date.getTimeInMillis())); /*from date*/ taskFvalueBean.setCloseDate(String.valueOf(date.getTimeInMillis())); /*to date*/ TaskSliderBean sliderBean = task.getTaskList(sessionId, taskId, taskFvalueBean, true, 20, new ArrayList<String>());

---

[Home](../../index.md) | [Up (How to write and debug scripts in IntelliJ IDEA)](index.md)
