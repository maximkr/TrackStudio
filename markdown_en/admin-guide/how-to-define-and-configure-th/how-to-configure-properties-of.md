[Home](../../index.md) | [Up (How to define and configure the categories of tasks?)](index.md)

---

# How to configure properties of the task with a particular category

## How to place a button for creating the task on the top bar

Fields: **Action**, **Show in Toolbar**

So that the button for creating the task with this category appeared on the bar, option "**Show in Toolbar** needs to be checked. Text on the button is set in the field "**Action**". For example, for errors we may create a button "**Create bug**", and also we may create "**Register bug**". If value of the field "**Action** is not assigned, the text on the button for this category will contain the word "**Create**" and the name of the category. If option "Show in Toolbar" is not checked, button for creating the task will be positioned not on the bar, but in the menu "**Add another**".

There is also a provision for grouping the buttons for creation of tasks. Group is similarly set in the field "**Action**" with the help of forward slash (/). For example, "**Test/Create test case**", "**Test/Create test suite**".

![](../../images/category_group_en.png)

## Display settings

### How to change the icon for task

While editing the properties of a category, you can select the icon, with which the tasks of this category will be displayed in the tasks lists, and in the custom fields of type "**Task**, in the tree of tasks at left side of the screen and the places like that.

You can use your own set of icons. For this purpose, you’ll need to save the graphic files with images (PNG and GIF images with transparent background are the best for usage) in the folder **etc/plugins/icons/categories**. You don’t need to stop or restart TrackStudio for this purpose.

Moreover, the icon of task status is displayed near to the category icon:

- ![](../../images/startstate.png) corresponds to the initial state of task
- ![](../../images/state.png) corresponds to the intermediate state of task
- ![](../../images/finishstate.png) corresponds to the final state of task

Color of the background under this icon is assigned in the settings of the task status (in Workflows).

### How to set format for time tracking

General format of time, used in TrackStudio for tracking the time spent and limiting the budgets is — hours, minutes and seconds. For example,*6 hours 8 minutes 10 seconds*. But this format of time is not appropriate for large entities, e.g., projects or versions.

In TrackStudio, you can set your own format of time for each category of tasks separately. Moreover, if the task of this category contains sub-tasks with other categories and other formats of time, the spent time will automatically be converted to the required format during display.

You can set the input and output of spent time and budgets in years, months, weeks, days, hours, minutes and seconds. At the same time, you can use any combinations of these units.

In the file of settings **trackstudio.properties**(trackstudio.default.properties), you can set the parameters for converting one types of units to others.

```
trackstudio.hoursInDay 8 trackstudio.daysInWeek 5 trackstudio.daysInMonth 22 trackstudio.hoursInYear 2000 trackstudio.monthsInYear 12
```

These parameters are set as global for the entire instance of TrackStudio.

### Default Page

You can configure the page, which will be displayed while jumping to the task over the link from the tasks list or over the link from custom field.

While jumping to the page, so that the list of sub-tasks of selected task got displayed, specify "Go to 'Tasks List' page", and so that the page with task properties got displayed — "Go to 'Manage Task' page".

### Task Appearance Settings

Tasks in TrackStudio can be viewed as documents as well as containers for files. You can specify, how particularly the page with task properties should be displayed:

1. Task
2. Document
3. File Container

### Display setting in the tree of tasks

For each category of tasks, you can manage the display in the task tree. For example, you can hide "closed" (i.e. which are in the final state) tasks from the tree. Similarly, you can specify the sequence for sorting the tasks in the tree: alphabetical or in the order of modification.

### Managing the behavior of task

You can specify for tasks of a particular category, if the roles of users as assignee can be indicated in them. This way, some task can for example be assigned to "Developers". Similarly, you may make it compulsory to specify the assignee of the task.

### Integration with Google Calendar

So that the task of a particular category was displayed in Google Calendar, check the option "**Create the event in Google Calendar**".

![](../../images/google_calendar.png)

---

[Home](../../index.md) | [Up (How to define and configure the categories of tasks?)](index.md)
