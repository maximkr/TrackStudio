[Home](../index.md) | [Up (Developer's Guide)](index.md)

---

# How to Perform Automatic Task Escalation

Generally, escalation of a task is its transition into other state, to the other submitter or raising its priority depending upon the conditions. For example, upon the expiry of deadline, or after a particular time since last refresh. Generally the escalation is done automatically. TrackStudio doesn’t yet have the facility for automatic execution of triggers, but things can be managed without it also.

As an example we will automatically convert the tasks, which are in the state “New” or “In Process” or “Paused”, to the state “Expired” on the next day after the deadline. In this method we will create the computable field for the task of required type, in which through*SecuredMessageTriggerBean.create()* we will execute the operation, converting the task to the required state (and, for example, reassign it to the submitter of the task). The given field will be computed every time, when it is displayed to the user. So as to prevent it from occurring very frequently, we will configure the permissions for the field in such a way that it is displayed only for a particular group of users.

We will login as root in the default database ([‘Requirements Management’](http://www.trackstudio.ru/requirements-management-overview.html)). Then we go to ‘Users Management’ and create the role ‘Bot’. We will not set the permissions for it yet.

![](../images/create_role_bot.png)

---

[Home](../index.md) | [Up (Developer's Guide)](index.md)
