[Home](../index.md) | [Up (Developer's Guide)](index.md)

---

# How to import emails from users to tasks

You can automatically import e-mails from your users into the TrackStudio system in the form of either new tasks or notes to the existing ones.

This feature needs to be enabled in Server Manager and necessary fields must be filled: mail server, protocol, login and password. Similarly you can mention the import interval of emails and indicate what to do with unprocessed emails (whether they must be deleted or forwarded to the other address).

![](../images/gmail-pop3.png)

So that the users could send the tasks and notes to TrackStudio, they must have an account in the system and email address must be indicated in that.

Then go to the required project and create email import rules.

![](../images/create_email_import_rule.png)

Indicate the keyword (it can also be a general phrase). The system with this keyword will select the emails which need to be imported. Sequence in the rule determines the sequence in which the import rules will be applied (they may be several in number for the same project as well as for different projects). Emails from only those users will be imported who have access to the specific project.

Keyword can be mentioned either in the subject of the email, or in its body, or in the headers. You can similarly configure the domains, mails from which will be processed (e.g., for protecting the system from spam).

Specify the category, from which the task will be imported. The users must have the permission to create the tasks with this category. If you tick the check box for importing the email from unknown users, in that case the system will create the tasks from emails in your name (in the name of the submitter of import rules). There exists [another method for creating the tasks](../admin-guide/how-to-import-tasks-from-e-mai.md), in which new user accounts will be created in the system for unknown users and permissions to the task will also be generated.

If in the header the system comes across the symbol # and a few digits, it will consider that this is the task number to which the message is to be added from the email. You must specify which type of operation must be executed (e.g., “Notes”).

Attachments in the email will similarly be attached to the task. This is convenient, e.g., for uploading user logs to the tasks.

With the help of combination of import rules and notification rules, you can manage the tasks in TrackStudio completely through e-mail interface.

---

[Home](../index.md) | [Up (Developer's Guide)](index.md)
