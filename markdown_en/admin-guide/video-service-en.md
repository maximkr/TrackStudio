[Home](../index.md) | [Up (Administrator's Guide)](index.md)

---

# Using TrackStudio as video service

In TrackStudio 5.5.0 version was added a capability to use TrackStudio as video service.

Now you can add you video files to tasks and comments. You can watch the video in a fully functional player with rewind, select playback speed, and volume adjustment functions.

## How to add you video to a task.

- First you need to upload your video file to any task. Video files can be stored in a task with "Container" type.
- Use the macro to insert video to comment or task description

```
Video{task_number:attach_id}
```

task_number - number of task with uploaded video file.

Also you can use Add Video option from Tools menu in editor to insert this macro

![](../images/tools.PNG)

AttachmentId you can get from the attachment link

![](../images/CopyLinkeng.PNG)

```
http://localhost:8888/TrackStudio/download/task/124/**8a80828f58f2efc20158f31b2763004e**
```

```
Video{124:8a80828f58f2efc20158f31b2763004e}
```

The video file with AttachmentId 8a80828f58f2efc20158f31b2763004e from task #124 will be inserted.

![](../images/demo.PNG)

---

[Home](../index.md) | [Up (Administrator's Guide)](index.md)
