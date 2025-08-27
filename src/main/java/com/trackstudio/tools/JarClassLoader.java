package com.trackstudio.tools;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class JarClassLoader extends URLClassLoader {
    public JarClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public void addFile (String path) throws MalformedURLException {
        addURL(new File(path).toURI().toURL());
    }

    public Class getClassLoad(String path, String name, ClassLoader parent) {
        try {
            URL urls [] = {};
            JarClassLoader cl = new JarClassLoader(urls, parent);
            cl.addFile(path);
            return cl.loadClass(name);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
