[Home](../index.md) | [Up (Administrator's Guide)](index.md)

---

# How to Track the History of Changes in Task Parameters

How to Track the History of Changes in Task Parameters

## What is Change Auditing and Why it is required

TrackStudio allows you to track all the changes incorporated in the task and you can even control the changed data. This feature is implemented through a special type of operation allowing you to enable the tracking of changes for tasks with a particular workflow and flexibly manage the access for viewing these changes with the help of standard access control system in TrackStudio.

## How to Enable Change Tracking

1. In TrackStudio menu, go to the required workflow
2. Go to the operations list
3. Add the operation with the name “*” (one symbol)
4. In this operation, go to the tab “Operation Permissions”.
5. Set the rules for viewing the operation

You don’t need to set any changes, other access rules, triggers, resolutions etc. Change audit works at the level TrackStudio core, therefore triggers will not be executed, and access control – checked.

## How to Manage the Visibility of Audit

Same access rules apply for viewing the operations for change audit which are used for common operations. It means that while setting the rules, you can mention the roles of users who can view the audit records, and who can’t.

## How it looks

In the database “Requirements Management” supplied with TrackStudio package, change audit is already enabled for the workflows “**Error**” and “**Test Case**”. By default, the audit records can only be viewed by administrators.

![](../images/audit_trail_en_1.png)

Audit records can be seen on the page task view page. For this, you need to click the button “** ****Show audit trail**” on the toolbar.

![](../images/audit_trail_en_2.png)

---

[Home](../index.md) | [Up (Administrator's Guide)](index.md)
