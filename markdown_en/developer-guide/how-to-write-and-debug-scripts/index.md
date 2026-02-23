[Home](../../index.md) | [Up (Developer's Guide)](../index.md)

---

# How to write and debug scripts in IntelliJ IDEA

IntelliJ IDEA is a powerful commercial tool for development. There is a free common edition version, which you can use for writing scripts.

After [building project](../how-to-build-project-from-mave.md) you can open it in IDE.

- First of all open "Project Structure" (Ctrl+Shift+S) and choose **Project language level:**** **7.0 and save changes.

![](../../images/prj_structure.PNG)

- Open "Maven Projects" tab and click "Reimport All Maven Projects" button

![](../../images/reimport.PNG)

Now you can write your actions in existing files or add new classes for that.

For packaging scripts:

- Open "Maven Projects" tab
- Choose "clean", "package" items
- Click "Run" button

![](../../images/run.PNG)

After that, all scripts will be packaged into ts-scripts.jar archive and placed in etc/plugins/scripts folder of your TrackStudio copy.

## Debug scripts

- Go to "Run > Edit Configurations" (Alt + U then R)
- Click + button and choose "Remote"

![](../../images/remote.PNG)

- Copy command line arguments for running remote JVM,

![](../../images/copy.PNG)

paste it into the end of startJetty.vmoptions and save the file. (**TrackStudio should be restarted)**

Enter name for Debug configuration and save changes.

Now you can run debug configuration.

---

[Home](../../index.md) | [Up (Developer's Guide)](../index.md)
