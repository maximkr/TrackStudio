package com.trackstudio.tools;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import com.trackstudio.startup.Config;

import net.jcip.annotations.Immutable;

/**
 * Класс для контроля выполнения стороннего кода из загружаемых классов, заугружает только допустимые классы
 */
@Immutable
public class ShellClassLoader extends java.lang.ClassLoader {

    /**
     * Базовый каталог, в котором лежат загружаемые классы
     */
    protected final File base;

    /**
     * Конструктор
     *
     * @param f базовый каталог
     */
    public ShellClassLoader(File f) {
       this.base = f;
    }

    /**
     * Возвращает базовый каталог
     *
     * @return каталог
     */
    public File getBase() {
        return base;
    }

    /**
     * Загружает указанный класс
     *
     * @param classAndPackageNameWithoutExtention
     *         имя класса или пакета
     * @return загруженный класс
     * @throws ClassNotFoundException если класс не найден
     */
    public Class loadClass(String classAndPackageNameWithoutExtention) throws ClassNotFoundException {
        try {
            return ClassLoader.getSystemClassLoader().loadClass(classAndPackageNameWithoutExtention);
        } catch (ClassNotFoundException t){

            try {
                File parentFile = getBase();
                URI uri = parentFile.toURI(); // must ends with /  !!!
                URL url = uri.toURL();        // file:/c:/almanac1.4/examples/

                List<URL> urls = new ArrayList<URL>();
                urls.add(url);
                File dir = new File(Config.getInstance().getPluginsDir() + "scripts");
                for (File file : dir.listFiles()) {
                    if (file.getName().indexOf(".jar") != -1) {
                        urls.add(new URL("jar:file://" + file.getPath() + "!/"));
                    }
                }

                ClassLoader cl = new URLClassLoader(urls.toArray(new URL[urls.size()]), Thread.currentThread().getContextClassLoader());

                // Load in the class

                return cl.loadClass(classAndPackageNameWithoutExtention);

            } catch (MalformedURLException e) {
                throw new ClassNotFoundException(classAndPackageNameWithoutExtention);
            }
        }
    }
}