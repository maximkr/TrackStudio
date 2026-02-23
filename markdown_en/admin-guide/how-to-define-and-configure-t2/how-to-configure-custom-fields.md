[Home](../../index.md) | [Up (How to define and configure the workflows)](index.md)

---

# How to configure custom fields for the workflow

With custom fields, in TrackStudio you can store specific information about your tasks in structural form. Custom fields can be connected to the tasks, to the users, or to the workflows. You can read in detail about custom fields in the [special chapter](http://www.trackstudio.com/how-add-custom-fields-tasks.html). And here, we are describing about configuring the access control rules for custom fields, in respect to the workflows.

So as to configure the permissions for custom fields, connected to the workflow, go to the tab "**Custom Fields**" of the required workflow. Thereafter, in the list select the required field and go to its page.

There will be two buttons in the bar: "**Permissions**" and "**Custom Fields Permissions for Operations**".

![](../../images/workflow_udf_overview_en.png)

So as to specify, who from among the users can view this custom field and who can edit it, press the button "**Permissions**". Settings for each of the rules are given in the form of pairs of role lists. Users with rules from the left list can’t execute specific operations, users from the right — can.

Settings for rules must not conflict one another. For example, it is not possible to permit someone to edit the field without permitting him to view it.

For each role from the list of permitted ones, you also can indicate specific modifiers. Thus, e.g., you can permit the change of field not to all the managers, but only to the **Manager**, who is the submitter or assignee of the task. So as to use the modifier, select the required role (or roles) from the list and press the button of the modifier. You can use two modifiers (**Submitter** and (**Assignee**), if they are accessible. Pairs of modifiers work as per **OR**.

In the given case, all the modifiers operate for all the rules equally, i.e. **Submitter** — is the author of the task, and **Assignee** — responsible for it at the time of checking the permissions.

![](../../images/workflow_udf_permissions_en.png)

So as to indicate, in which operations the users, who are allowed to execute the operations, can view or edit the value of custom field, press the button "** ****Custom Fields Permissions for Operations**".

Settings for each of the rules are presented in the form of pairs of lists of operations. In operations from the left list, can’t view or edit the value of custom field, in operations from right list — can.

Settings of the rules must not conflict with each other. For example, it is not possible to permit someone to edit the field without permitting him to view it.

![](../../images/workflow_udf_operations_permissions_en.png)

---

[Home](../../index.md) | [Up (How to define and configure the workflows)](index.md)
