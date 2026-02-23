[Home](../../../index.md) | [Up (How to configure the list of operations with task)](index.md)

---

# How to configure permissions for users in operations

You can configure the rules as per which the users with particular roles can or can’t view the operations, execute them and become assignee for the task at particular stages of its workflow. To set the access control rules for operations, choose the required operation from the list and select the tab “**Operation Permissions**”.

Settings of each of the rules are given in the form of pairs of roles lists. Users with the roles from the left list can not execute the particular actions, and the users from the right one can do this.

![](../../../images/workflow_operation_permissions_en.png)

Access control rules for all the roles, to which the user creating the role has access, are displayed automatically at the time of creation of operation.

Settings of rules must not conflict with each other. For example, it is not permitted to allow the execution of operation without permitting its viewing.

For each role from the list of permitted ones, you can also specify particular modifiers. So, e.g., you can allow the execution of operations to not all the managers, but only to the **Manager**, who is the submitter or assignee of the task. For the purpose of using the modifier, select the required role (or roles) from the list and press the button of the modifier. You can use two modifiers (**Submitter** and **Assignee**), if they are accessible. Pairs of modifiers work as per the rule **OR**.

In contrast to categories, in operations all the modifiers act for all the rules in the same manner, i.e. **Submitter** — is the submitter of task, and**Assignee** — is the responsible for it at the time of checking the permissions.

---

[Home](../../../index.md) | [Up (How to configure the list of operations with task)](index.md)
