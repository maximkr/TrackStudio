[Home](../index.md) | [Up (User's Guide)](index.md)

---

# Project Overview

Some users, executing different roles, have been created in the project and corresponding information has been mentioned in the table below.

| Administrator | Administrator | root | root |
| --- | --- | --- | --- |
| Sergey Managerov | Manager | manager | 123 |
| Ivan Analiticov | Analyst | analitik | 123 |
| Dmitry Writerov | Tech writer | writer | 123 |
| Stepan developerov | Developer | developer | 123 |
| Maxim Testrov | Tester | tester | 123 |

Similarly, group “External Customers” is created which is used by the system for the users, which get registered themselves. In the initial stages of project development, all the requirements are collected and analyzed, there is a provision for 2 categories to work with them in the project viz. ‘Requirements’ and ‘List of Requirements’. Each requirement has its own ‘Life Cycle’, which is specified by the states:

```
Analysis » Technical specification » Asserted » Development » Testing » Accepted » Resolved
```

The analysis of requirement can be temporarily paused any time, or completely deflected, i.e. they are forwarded to the state ‘Paused’ and ‘Deflected’. So as to forward the requirements for development, we need to prepare a technical specification and the system has a special field for this purpose named as ‘Technical Specification’. Besides, there are special fields, in which there is a provision to store “Version of requirement” and index to “Test suite” for its testing. TrackStudio has the provision for system of notes, which is used for changing the state of task, role of assignee, time tracking, adding the comments etc. This note system is meant for the purpose when any user wants to change the state, he adds a note. For adding the note of different types, the user or group of users must have the corresponding permissions (e.g., requirement analysis can be carried out by ‘Analytic’ only, and testing can only be carried out by ‘Tester’).

‘Test case’ (or ‘Test suite’, which includes several cases in it) is created for each requirement. Such type of tasks can be created within the category ‘List of tests’.

In the ‘Test case’, like in the requirement, there exists inherent ‘Life workflow’:

```
New » In Process » Ready » Development
```

Similarly Tests can get outdated and for this purpose there is provision for corresponding state.

For test case there is provision of standard template, which encapsulates following sections in it:

- Preliminary Requirements – here those requirements are mentioned which are to be executed before starting the test.
- Plan of action – here all the actions are mentioned in steps, which must be executed for ensuring the necessary testing.
- The Check Plan – here all the results are mentioned which are achieved after the execution of Plan of Action.
- Notes – here we can write everything suitable in other sections

Besides requirements and test suites, the project can have errors and incompletion. For the purpose of writing them, we use the type of tasks ‘Error / Bug’. This type of tasks is accessible within the category ‘List of errors/bugs’. The category ‘Bug’ also possesses its own life workflow:

```
New » In the process of correction » Fixed » Closed
```

In the same way if, e.g., priorities have changed, then the bug can be put at Pause.

After adding the task ‘Error/Bug’, manager assigns the Assignee for it. The assignee analyses the task and determines which requirement was broken (violated). In case, if the bug emerged as a result of testing, then the tester indicates the test case, during the work of which the bug appeared.

The project also consists of the type of tasks ‘Completion’, which is accessible within the category ‘List of changes’. It possesses the following workflow:

```
New » In process » Ready » Closed
```

If some hitch takes place with the implementation, then “Change” can be put to pause (Pause Change).

## Filters are used in TrackStudio for managing large number of tasks

By default, following filters are available:

- **All tasks** – shows all the tasks
- **My tasks**** **– shows only those tasks, which are assigned to the current user.
- **Open tasks** – shows the open tasks

There is option for all these filters with the signature ‘deep search’. When these filters are selected, the tasks of current folder (category) as well as the inner (deep) tasks are also processed.

We need readable briefing while working with the system of requirements, and TrackStudio uses reports for this purpose. By default, one type of reports ‘List of requirements’ has been created, which contains all the requirements of selected folder (there is also the option with all the attached requirements). There is provision for each requirement to display its name, absolute path inside the project, current state, submitter, assignee, and update date.

---

[Home](../index.md) | [Up (User's Guide)](index.md)
