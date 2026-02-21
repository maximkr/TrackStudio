[Home](../../index.md) | [Up (Administrator's Guide)](../index.md)

---

# How to Manage Roles of Users

As a rule, there is no term like group of users in TrackStudio. Generally group management in incident management systems means that the administrator creates some group, e.g. "**Developers**" and goes on adding users like Tom and Dick (and remaining 325 developers). Thereafter he configures the permissions for this group for some project (it is reasonable enough, as configuring permissions individually for users will be quite costly). When you have 2-3 developers and a couple of projects — this is the most convenient way for setting permissions. But if you have 327 developers, moreover they are divided into teams by a very complex way (e.g. one person can be a developer in different teams, or in other team — he is a developer, and in some other — he is a tester), or when you have not 2-3 projects, but 20-30 and for different clients. What needs to be done in that situation?

It would not be a good idea to create the groups like "Developers in project A", "Developers in Project B", "Team No. 7" and so on. This surely will lead to chaos, and task management systems must facilitate the management and not make it complex.

The most accurate way is to define the roles of users in your work processes. For example, in ITIL (detailed in [ITSM](http://en.wikipedia.org/wiki/IT_service_management) following roles are proposed to be defined for the workflow "Incident Management":

**Workflow Owner** defines the goals and tasks of the workflow, policies of the workflow; evaluates the key performance indicators (KPI) and rationality of workflow; gives recommendations for improving the work.

**Workflow Manager**** **organizes the communication between the participants(members); plans and implements the measures for development of workflow; ensures the urgency of workflow documents and their preparation; takes part in hierarchal escalation.

**Specialists 1 and other support channels**

Other roles for the workflow "Change Management":

**Change Manager**:

primary estimation and RFC filtering, primary classification; organizing the work of CAB; authorizing the decisions taken by CAB; publishing FSC/PSA; coordination and control of CHG, organizing interaction with involved parties; updating the CHG log; estimating the canceled/pending RFC; analysis of RFC, identification of trends, risk analysis; RFC closing; reporting on process work.

**CAB Member**:

estimation of RFC received for approval (estimation of impact, cost, resources); participation in CAB and CAB/EC; ensuring the availability for emergency approval of CHG; providing recommendations for adoption of CHG

**Change Coordinator**

**Change Analyst**

**Roles** in TrackStudio determine what exactly the user can do, which roles in the tasks can view and edit, can perform which operations and view the tasks of which categories.

---

[Home](../../index.md) | [Up (Administrator's Guide)](../index.md)
