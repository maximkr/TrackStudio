[Home](../index.md) | [Up (Developer's Guide)](index.md)

---

# Compiling TrackStudio from source code

For the purpose of compiling TrackStudio, you will need [Java SDK](http://www.oracle.com/technetwork/java/javase/downloads/index.html) and [Apache Ant](http://ant.apache.org/bindownload.cgi). It would be even better if you have [some IDE](http://www.jetbrains.com/idea/download/), but for minor changes even a simple **text** editor will be sufficient.

## How to compile the project without using IDE

Make sure that Ant is installed correctly. For this, in the command line or terminal you have to type

```
ant
```

The response must look like:

```
Buildfile: build.xml does not exist!Build failed
```

In case Ant is not installed properly, you need to [refer to documentation](http://ant.apache.org/manual/index.html).

1. Download the source code of TrackStudio.
2. Extract the source code of TrackStudio to any empty folder.
3. In the command line, type ‘ant war’.
4. Wait for installation to complete.

![](../images/compilation_9.png)

1. After successful compilation, file **TrackStudio.war** must appear in the folder **dist**.This file can be extracted to the directory**webapps/TrackStudio** of your **backup instance** of TrackStudio.
2. Thereafter, you can launch the backup instance and see what you have got.

## How to create and compile the project in IntelliJ IDEA.

1. Download the source code of TrackStudio.
2. Extract the source code of TrackStudio to any empty folder.
3. Run IDEA.
4. Create a new project, while indicating the folder, where you extracted the archive with source code, as its root folder.

![](../images/compilation_1.png)

![](../images/compilation_2.png)

![](../images/compilation_3.png)

![](../images/compilation_4.png)

![](../images/compilation_5.png)

1. Go to the menu item **File->Project Structure**.
2. Select the tab **Libraries**.
3. Create the library **TrackStudio**.
4. Add all the classes from folder **webapps/TrackStudio/WEB-INF/lib** to it.

![](../images/compilation_6.png)

1. Then open the tab Ant Build (generally it is located on the right side in the IDEA window)
2. Select the file build.xml from folder, where you had extracted source codes of TrackStudio.

![](../images/compilation_7.png)

1. Run the task named ‘war’ from the tasks list Ant Build
2. After successful compilation, file **TrackStudio.war** must appear in the folder **dist**. This file can be extracted to the directory**webapps/TrackStudio**** ****of your**** ****backup instance**** ****of TrackStudio.**

![](../images/compilation_8.png)

1. Thereafter, you can run the backup instance and see what you have got.

This way, in TrackStudio you can practically change anything, or complete your own packages and plugins.

## How to remove syntax error highlights

![](../images/compilation_10.png)

![](../images/compilation_11.png)

So as to make the editing of our code easy in IDEA, it needs to be integrated well with the features of our project.

As a first step, connect the library javax.servlet.jar. It is located in the folder **jetty/lib** of extracted source code.

1. In IDEA, go to the menu item **File->Project Structure**.
2. Select the item **Modules**.
3. Select the module **TrackStudio**.
4. Select the tab Dependencies.
5. Press the button Add and select Single Module Library
6. Locate javax.servelet.jar and add it to the project

![](../images/compilation_12.png)

After that the source code will look like:

![](../images/compilation_13.png)

Now it is time for libraries with tags.

We need to create Facet for our project. For this purpose:

1. In IDEA, go to the menu item **File->Project Structure**.
2. Select the item **Facets**.
3. Select **Web**.
4. Add new facet to the module TrackStudio

![](../images/compilation_15.png)

1. Indicate the path to the file **web.xml**: **webapps/TrackStudio/WEB-INF/web.xml**or**etc/webxml/web.jspc20.xml**
2. Specify the path to resources (Web Resource Directory Path): **webapps/TrackStudio/F**
3. Indicate the Relative Path as **/**

![](../images/compilation_17.png)

![](../images/compilation_18.png)

## Debugging

For debugging of code, Remote Debug can be configured. And for this purpose:

1. In IDEA, go to the menu item **Run->Edit Configuration**.
2. Create a new configuration. Select the item **Remote**.
3. Connect the files of source code from the module TrackStudio.

![](../images/compilation_19.png)

1. Copy the string into the file startJetty.vmoptions in the root of your backup instance of TrackStudio.

```
-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005
```

![](../images/compilation_20.png)

1. Run jetty

```
sh startJetty
```

1. Following line must appear in the terminal

```
Listening for transport dt_socket at address: 5005
```

1. Go to IDEA.
2. Select the earlier created configuration of TrackStudio and press the button for debugging.

![](../images/compilation_21.png)

1. Put the breakpoints at the required places.
2. Through the browser, enter TrackStudio and execute the functions, which must stop debugger at the required points.

![](../images/compilation_22.png)

1. Locate the bug.
2. Correct the error.
3. Stop the backup instance of TrackStudio.
4. Recompile your source code as per the above mentioned instruction.
5. Repeat until all errors are fixed

---

[Home](../index.md) | [Up (Developer's Guide)](index.md)
