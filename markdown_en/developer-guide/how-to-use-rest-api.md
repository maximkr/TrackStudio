[Home](../index.md) | [Up (Developer's Guide)](index.md)

---

# How to use REST API

```
TrackStudio REST API is an experimental feature now. We plan to extend and rework this interface, so requests formats and responsed data structure may be changed.
```

## Requests Structure

Generally all requests in REST API looks like:

http://host:port/TrackStudio/rest/resource-name?paramname=value

At this time you should send some required parameters in REST API requests:

login - is an account name

password - is a password

These resources are supplied

/task/filter/{parent}

Returns list of task filters for task with number {**parent**}

**Usage:**

Request:

```
http://localhost:8888/TrackStudio/rest/task/filter/95?login=root&password=root
```

Response:

```
["All tasks (including subtasks)","Requirements list (including subtasks)","Requirements list","Open tasks (including subtasks)","Open tasks","My tasks (including subtasks)","My tasks","All tasks"]
```

/task/tasks/{parent}/{filter}

Returns subtaks list for task with number {**parent**}, filtered by {**filter**}

**Usage:**

Request:

```
http://localhost:8888/TrackStudio/rest/task/tasks/96?filter=All&login=root&password=root
```

Response:

```
[{"name":"Functional requirements", "id":"4028808a1951e21b0119526f85440163", "number":"104", "attachment":null,"description":"&nbsp;\n", "abudget":0,"budget":0,"closedate":-1,"deadline":-1,"shortname":"", "submitdate":1208268850000,"updatedate":1208286872000,"childrenCount":1,"messageCount":0, "hasAttachments":false,"onSight":true,"messages":null,"udfs":null,"categoryName":"Requirements list", "statusName":"New", "resolutionName":null, "priorityName":"Normal", "submitterName":"root", "handlerUserName":"manager", "handlerGroupName":null,"parentName":"Requirements", "workflowName":"Folder", "abudgetView":"-", "budgetView":"", "closedateView":"", "deadlineView":"", "submitdateView":"15.04.08 10:14", "updatedateView":"15.04.08 15:14", "categoryLink":"http://localhost:8888/TrackStudio/icons/categories/default_folder.png", "stateLink":"http://localhost:8888/TrackStudio/cssimages/startstate.png"},{"name":"Users requirements", "id":"4028808a1951e21b0119526f56e10162", "number":"103", "attachment":null,"description":"&nbsp;\n", "abudget":0,"budget":0,"closedate":-1,"deadline":-1,"shortname":"", "submitdate":1208268838000,"updatedate":1208286853000,"childrenCount":1,"messageCount":0,"hasAttachments":false,"onSight":true,"messages":null,"udfs":null,"categoryName":"Requirements list", "statusName":"New", "resolutionName":null,"priorityName":"Normal", "submitterName":"root", "handlerUserName":"manager", "handlerGroupName":null,"parentName":"Requirements", "workflowName":"Folder", "abudgetView":"-", "budgetView":"", "closedateView":"", "deadlineView":"", "submitdateView":"15.04.08 10:13", "updatedateView":"15.04.08 15:14", "categoryLink":"http://localhost:8888/TrackStudio/icons/categories/default_folder.png", "stateLink":"http://localhost:8888/TrackStudio/cssimages/startstate.png"},{"name":"General Requirements", "id":"4028808a1951e21b0119526f2be90161", "number":"102", "attachment":null,"description":"&nbsp;\n", "abudget":0,"budget":0,"closedate":-1,"deadline":-1,"shortname":"", "submitdate":1208268827000, "updatedate":1208268827000,"childrenCount":0,"messageCount":0,"hasAttachments":false,"onSight":true,"messages":null,"udfs":null,"categoryName":"Requirements list", "statusName":"New", "resolutionName":null,"priorityName":"Normal", "submitterName":"root", "handlerUserName":"manager", "handlerGroupName":null,"parentName":"Requirements", "workflowName":"Folder", "abudgetView":"-", "budgetView":"", "closedateView":"", "deadlineView":"", "submitdateView":"15.04.08 10:13", "updatedateView":"15.04.08 10:13", "categoryLink":"http://localhost:8888/TrackStudio/icons/categories/default_folder.png", "stateLink":"http://localhost:8888/TrackStudio/cssimages/startstate.png"},{"name":"BUsiness Requirements", "id":"4028808a1951e21b0119526efcca0160", "number":"101", "attachment":null,"description":"&nbsp;\n", "abudget":0,"budget":0,"closedate":-1,"deadline":-1,"shortname":"", "submitdate":1208268815000,"updatedate":1208268815000,"childrenCount":0,"messageCount":0,"hasAttachments":false,"onSight":true,"messages":null,"udfs":null,"categoryName":"Requirements List", "statusName":"Новый", "resolutionName":null,"priorityName":"Normal", "submitterName":"root", "handlerUserName":"manager", "handlerGroupName":null,"parentName":"Requirements", "workflowName":"Folder", "abudgetView":"-", "budgetView":"", "closedateView":"", "deadlineView":"", "submitdateView":"15.04.08 10:13", "updatedateView":"15.04.08 10:13", "categoryLink":"http://localhost:8888/TrackStudio/icons/categories/default_folder.png", "stateLink":"http://localhost:8888/TrackStudio/cssimages/startstate.png"}]
```

/task/info/{number}

Returns information for task with number {**number**}

**Required parameters:**

login - is an account name

password - is a password

**Optional parameters:**

message - if 'true', comments for this task will be returned in response

attach - if 'true', attachments for this task will be returned in response

udf - if 'true', custom fields values will be returned in response

**Usage:**

Request:

```
http://localhost:8888/TrackStudio/rest/task/info/120?login=root&password=root
```

Response:

```
{"name":"Login page check","id":"4028808a1953022d0119531c0e5500c8","number":"120","attachment":null,"description":"<p><strong>INITIAL REQUIREMENTS:</strong></p>\n<p>All tasks for designers on this page should be completed.&nbsp;</p>\n<p><strong>PLAN:</strong></p><p>- Open login page<br /> - Page name - should be Login<br /> - Company logo is dislayed in left right corner&nbsp;<br /> - There a 2 fields on form - Login and Password<br /> - Login button is active<br /> - "Forgot password" link is available&nbsp;</p>\n<p>- List to registration page also needed.</p>\n","abudget":0,"budget":0,"closedate":-1,"deadline":-1,"shortname":"", "submitdate":1208280157000,"updatedate":1291304234000,"childrenCount":0,"messageCount":2,"hasAttachments":false,"onSight":true,"messages":null,"udfs":null,"categoryName":" Testing data set","statusName":"New","resolutionName":null,"priorityName":"Normal","submitterName":"manager","handlerUserName":"manager","handlerGroupName":null,"parentName":"Documentation for testing","workflowName":"Testing data","abudgetView":"-","budgetView":"","closedateView":"","deadlineView":"","submitdateView":"15.04.08 13:22","updatedateView":"02.12.10 10:37","categoryLink":"http://localhost:8888/TrackStudio/icons/categories/document.png","stateLink":"http://localhost:8888/TrackStudio/cssimages/startstate.png"}

```

---

[Home](../index.md) | [Up (Developer's Guide)](index.md)
