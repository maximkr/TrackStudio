[Home](../../../index.md) | [Up (How to Manage Roles of Users)](../index.md)

---

# How to define the roles and organize the users into groups

Conventionally speaking there is no such term called group of users in TrackStudio. Generally, management of groups in incident management systems leads to the administrator creating certain group, e.g. “Developers” and stuffing it with Toms and Dicks as users ( and remaining 325 developers). Then he configures the permissions for this group in a certain project (as creating individual permissions for every user will be costly). This is the most convenient method to define the permissions when you have 2 - 3 developers and a couple of projects. Suppose, if you have 327 developers, and moreover they are divided into very complex teams (e.g., one and the same person can perform the role of developer in different teams, or may be he is developer in one team and tester in another), or when you have 2-3 projects, and 20 – 30 users for different customers. What will you do in that case?

Will you create groups like “Developers in project A”, “Developers in project B”, “Team No. 7” and so on? This is not correct. This will lead to chaos, and tasks management systems are there to simplify your management, and not make it complex.

The right method is to define the roles of users in your workflows. For example, in ITIL, particularly in [ITSM](http://ru.wikipedia.org/wiki/ITSM) for the workflow “Incident Management”, it is advised to define the following roles:

**Workflow owner**** **defines the goals and tasks of the workflow, policies of the workflow; evaluates the key performance indicators (KPI) and rationality of workflow; gives recommendations for improving the work.

**Workflow Manager**** **organizes the communication between the participants; plans and executes the measures for development of workflow; ensures the urgency of workflow documents and its organization; takes part in hierarchal escalation.

**Specialists 1 and other support channels**

Other roles for the workflow “Change Management”:

**Change Manager**:

initial assessment and filtering of RFC, initial categorization; organizing the work of CAB; authorizing the decisions taken by CAB; publishing FSC/PSA; coordinating and controlling CHG, organizing the communication with concerned parties; refreshing the CHG log; estimating deferred / delayed RFC; analysis of RFC, identification of trends (inclinations), risk analysis; closing of RFC; reporting of workflow.

**САВ Member**:

assessment of RFC received for approval (assess the effect, cost, and resources); participate in CAB and CAB/EC; ensure the accessibility for urgent approval of CHG; provide recommendations for implementation of CHG.

**Change Coordinator**

**Change Analyst**

Roles in TrackStudio define what exactly the user can do like which fields in the tasks he can views and edit, which operations he can perform and tasks of which categories he can view.

So as to allow the user of required role to do something in a particular task, he must be given access to this or higher task. Access may be given directly to the user as well as to all of **your** subordinate users with particular role.

## Access Management

Suppose we have many developers as well as projects, and each of these projects needs to have its own team of developers.

### " Flat Schema"

In “Flat schema” the users are distributed into teams and projects not because of their hierarchy, but because of their direct linkage to the projects through access control rules.

Suppose we have a list of users:

![](../../../images/user_list.png)

Let us allocate one part of users to “Project B”

![](../../../images/screenshot6.png)

And another part to “Project C”

![](../../../images/screenshot7.png)

We get two projects with different teams. At the same time, the users of neighboring teams do not see projects of others.

![](../../../images/screenshot8.png)

Such schema simply requires you to add new users to the project, and create new projects. However, if you need to add more users to the project, or in different projects there is requirement for part of users to play the role of developers, and remaining must be observers, or one team is engaged in several projects, then this schema will not be simple.

### Hierarchal Schema

In case you have established teams of developers, and the teams are not changed frequently, you can organize the teams of developers in the following manner:

![](../../../images/user_groups.png)

“Leader of Team Alpha” etc. can be real users as well as virtual ones. In the second situation, one (or many) of other users can be assigned as team leaders.

![](../../../images/manage_users_acl.png)

This means that in TrackStudio each user can manage the permissions only for subordinate users. Subordination is determined by the hierarchy of users and access control rules for the users (see the figure above).

Now, for the purpose of giving the team “Alpha” access to “Project A’, following steps must be taken:

The higher user (in this case it is **Administrator** or **Development Manager**) configures the access for “Leader of Team Alpha” to the root task, or directly to “Project A’.

![](../../../images/add_leader_to_project.png)

It is mandatory that this user in the given project had a role, allowing him to manage the access control rules to the tasks.

![](../../../images/can_manage_acl.png)

![](../../../images/screenshot3.png)

Then enter the system as this user (password of root user can be used). Allow the access to users with the role “Developer”.

![](../../../images/screenshot5.png)

Who will consequently have access to “Project A”

![](../../../images/effective_roles.png)

Similarly configure the permissions for other teams to the remaining projects.

---

[Home](../../../index.md) | [Up (How to Manage Roles of Users)](../index.md)
