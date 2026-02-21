[Home](../index.md)

---

# Developer's Guide

[How to build project from maven template](how-to-build-project-from-mave.md)

[How to write and debug scripts in IntelliJ IDEA](how-to-write-and-debug-scripts/index.md)

[How to use SOAP integration in TrackStudio](how-to-write-and-debug-scripts/how-to-use-soap-integration-in.md)

[Compiling TrackStudio from source code](compiling-trackstudio-from-sou.md)

[How to Make Dynamic List of Possible Values of Custom Field](how-to-make-dynamic-list-of-po.md)

[How to Make the Value of Custom Field Computable](how-to-make-the-value-of-custo/index.md)

[Integer](how-to-make-the-value-of-custo/integer/index.md)

[Number of days since task update](how-to-make-the-value-of-custo/integer/number-of-days-since-task-upda.md)

[How to Perform Automatic Task Escalation](how-to-perform-automatic-task-.md)

[How to Save the History of Changes in Task Parameters](how-to-save-the-history-of-cha.md)

[How to check data before saving the task (Before Task Edit Trigger)](how-to-check-data-before-savin.md)

[How to copy access control rules into other project using single button](how-to-copy-access-control-rul.md)

[How to import emails from users to tasks](how-to-import-emails-from-user.md)

[How to perform actions on multiple tasks](how-to-perform-actions-on-mult.md)

[How to use REST API](how-to-use-rest-api.md)

[List of Similar Tasks](list-of-similar-tasks.md)

TrackStudio has a wide scope of applicability. The basic principles of this system are its universality and scalability. No matter how complex and difficult your business-process is, there is always a way out for executing it in TrackStudio. May be, the obtained result is not as elegant as in specially designed systems for the required task (if it does exist at all), and more or less, this is a good working tool.

Additional functionality in TrackStudio can be implemented with the help of scripts and triggers. Scripts generally correspond to different types of computable custom fields. Triggers correspond to the events, taking place with the tasks, particularly: creation of tasks, their editing and executing other operations with the tasks. Scripts are executed every time while viewing the task, when value of the field is to be displayed, and triggers are executed when associated actions are executed.

Scripts and triggers can be of two types: interpretable and complied. Interpretable scripts are the java-code, processed by the interpreter Beanshell. It is a bit easier to write these types of scripts, as they do not require datatype matching and presence of JDK is also not required for compilation.

Compiled scripts have many advantages:

- first of all, they are executed faster as there is no need to load them and initialize the interpreter
- secondly, if the script is compiled then it is sure that there is no syntax error in it
- and thirdly, you can use the entire capabilities of Java in the compiled scripts. For example, inheritance and static variables

## How to write Scripts and Triggers?

For writing and compiling the scripts and triggers, you will require:

JDK (java development kit), which can be downloaded from the website [http://java.com](http://java.com/) (see that it is JDK and not JRE)

You will need the file trackstudio.jar (located in /webapps/TrackStudio/WEB-INF/lib/trackstudio.jar), which is available in any version of TrackStudio package. It would be better, if you use some IDE, e.g. IDEA, Eclipse or Netbeans

All the scripts and triggers must implement one of the following triggers:

```
com.trackstudio.external.OperationTrigger - for the triggers, executed during operations with tasks
com.trackstudio.external.TaskBulkProcessor - for triggers, executed on the tasks selected from the list
com.trackstudio.external.TaskTrigger - for the triggers, executed during the creation or editing of tasks
com.trackstudio.external.TaskUDFLookupScript - for the sets of possible values of tasks custom fields
com.trackstudio.external.TaskUDFValueScript - for the values of tasks custom fields
com.trackstudio.external.UserUDFLookupScript - for the sets of possible values of user custom fields
com.trackstudio.external.UserUDFValueScript - for the values of user custom fields
```

Besides, complied scripts must remain in the packages in conformity with the structure of folders inside plugin in TrackStudio. For example, for the script, implementing the trigger Before Add Message the path must be

```
./etc/plugins/scripts/before_add_message/
```

And correspondingly the package **scripts.before_add_message**

package scripts.before_add_message;import com.trackstudio.exception.GranException;import com.trackstudio.exception.UserException;import com.trackstudio.external.OperationTrigger;import com.trackstudio.secured.SecuredMessageTriggerBean;import com.trackstudio.tools.textfilter.HTMLEncoder;public class CheckSpentTimeScript implements OperationTrigger { public static final int TIME_LIMIT = 1; /** time limit in milliseconds */ public static final int K = 20; /** conversion factors */ @Override public SecuredMessageTriggerBean execute(SecuredMessageTriggerBean message) throws GranException { String desc = HTMLEncoder.stripHtmlTags(message.getDescription()); if (message.getHrs() != null && message.getHrs() > TIME_LIMIT && desc.length()/message.getHrs() < K){ throw new UserException("Please "+ message.getSubmitter().getName()+", describe what you did at that time!"); } return message; }}

This script can be compiled by the command

```
javac -cp path/trackstudio.jar CheckSpentTimeScript.java
```

But it is better to use some IDE.

Further, either save the obtained **class** in the folder **./etc/plugins/scripts/before_add_message/**, or create jar

```
cd ../../jar cf myscripts.jar scripts
```

And save in the folder **./etc/plugins/scripts**.

---

[Home](../index.md)
