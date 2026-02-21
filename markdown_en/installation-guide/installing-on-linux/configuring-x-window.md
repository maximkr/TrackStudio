[Home](../../index.md) | [Up (Installing on Linux)](index.md)

---

# Configuring X Window

TrackStudio doesn’t contain graphics libraries for generating colors, fonts and other graphic documentation. Java uses system libraries for generating such information, in that way, for displaying graphic documentation (reports) you will require graphics subsystem XWindow.

- Install X11 Server and define the environment variable DISPLAY
- Assign the parameter **-Djava.awt.headless=true** to JVM, when you run it. In this case, you don’t need to start Xvfb, but availability of X11 packages is mandatory.

For different applications servers, this parameter is defined differently. For example, for Tomcat 5.x, you can write these options in в **catalina.bat** or **catalina.sh** in **CATALINA_OPTS**

---

[Home](../../index.md) | [Up (Installing on Linux)](index.md)
