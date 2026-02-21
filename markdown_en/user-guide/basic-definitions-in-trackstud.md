[Home](../index.md) | [Up (User's Guide)](index.md)

---

# Basic Definitions in TrackStudio

One of the most important features of TrackStudio is the hierarchical organization of tasks as well as users. The parameters (workflows, categories, and permissions) for upper layers of hierarchy are inherited by the lower layers of hierarchy. Besides, child objects can have additional parameters, not inherited from parents.

**Workflows**** **are used for determining the life cycle of tasks in TrackStudio. They enable the setting of user permissions for changing the state of task and contain the set of properties: priorities, types of user notes, steps between the states and custom fields.

**Operations**** **are intended for changing the state of task, assigning the assignee, time tracking and adding the comments. Every executed operation has a submitter, create (execution) time and type. Depending upon the type of operation, the task is transferred from one state to another, or it remains in the same state. For executing the operation, the user must have the corresponding permission.

**Categories**** **determine the types of tasks in TrackStudio, and also the relations between them. While creating categories, it is necessary to select the workflow, which will reflect its policy, list of permitted sub-categories for it and permission to view, delete and edit the tasks for different groups of users. In other words, the categories, in contrast to workflows, define not the life cycle of tasks, but they define the limitations and rights of users for their usage.

**Roles**** **of users are used for distributing the responsibilities of users, taking part in the project. One user may have several roles in one as well as in different projects.

**Filters**** **are used for selecting and sorting the tasks and users, corresponding to particular categories. Like other objects in TrackStudio, filters are also inherited from higher layers to the lower layers thereby saving a lot of time for configuring the projects. For example, one may create a filter which will show the list of tasks solved during the week. If this filter is executed for the root project, we get the list of tasks solved during the week by all the users in all the sub-projects during that week (considering that the function of deep search has been included in the filter). If the same filter is executed for a sub-project, we get the list of solved tasks in that particular sub-project. Filters can be private and public. Private filters are accessible only to that user, who created them

**E-mail Notifications**** **are used for the purpose of informing the users about the changes in the tasks which concern them. For example, the reader may get an email notification when the task concerning him has been solved.

**Filter Subscriptions**** **are intended for periodically informing the users about the state of their concerned tasks.

**Scripts**** **are used for computing the custom fields and for defining the triggers (trigger – is a special script, which gets automatically actuated before, in place of or after the change of task). Java-like language (Beanshell) is used for writing the scripts, which enables to solve the tasks of different complexities: from simple mathematical operations with task fields to implementing the rigorous algorithms for processing the data using cycles and branching. You can also write scripts directly in Java, then compile them independently and use them in TrackStudio in the compiled form.

---

[Home](../index.md) | [Up (User's Guide)](index.md)
