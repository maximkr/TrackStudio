package com.trackstudio.kernel.cache;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.exception.GranException;
import com.trackstudio.startup.Config;
import com.trackstudio.tools.EggBasket;
import com.trackstudio.tools.JarClassLoader;
import com.trackstudio.tools.PluginPair;
import com.trackstudio.tools.ShellClassLoader;

import net.jcip.annotations.ThreadSafe;

import javax.imageio.ImageIO;

/**
 * Класс используется для кеширования плгинов
 */
@ThreadSafe
public class PluginCacheManager extends com.trackstudio.kernel.cache.CacheManager {
    private static final int DELAY = 1000;
    private static long lastLoadTimestamp = System.currentTimeMillis();
    private static final Log log = LogFactory.getLog(PluginCacheManager.class);
    // кеш плагинов
    private static final EggBasket<PluginType, AbstractPluginCacheItem> pluginCache = new EggBasket<PluginType, AbstractPluginCacheItem>();
    private static final PluginCacheManager instance = new PluginCacheManager();

    private final Map<Long, List<String>> detailsInfo = new LinkedHashMap<Long, List<String>>();

    // loadedJar key - jar name, value - last modify
    private final ConcurrentHashMap<String, Long> loadedJar;

    /**
     * Конструктор по умолчанию. Инициализирует кеш плагинов
     *
     * @throws GranException при необходимости
     */
    private PluginCacheManager() {
        loadedJar = new ConcurrentHashMap<String, Long>();
        loadJars();
        initCache(PluginType.AFTER_ADD_MESSAGE);
        initCache(PluginType.AFTER_CREATE_TASK);
        initCache(PluginType.AFTER_EDIT_TASK);
        initCache(PluginType.BEFORE_ADD_MESSAGE);
        initCache(PluginType.BEFORE_CREATE_TASK);
        initCache(PluginType.BEFORE_EDIT_TASK);
        initCache(PluginType.BULK);
        initCache(PluginType.EMAIL);
        initCache(PluginType.ICON);
        initCache(PluginType.INSTEAD_OF_ADD_MESSAGE);
        initCache(PluginType.INSTEAD_OF_CREATE_TASK);
        initCache(PluginType.INSTEAD_OF_EDIT_TASK);
        initCache(PluginType.TASK_CUSTOM_FIELD_LOOKUP);
        initCache(PluginType.TASK_CUSTOM_FIELD_VALUE);
        initCache(PluginType.USER_CUSTOM_FIELD_LOOKUP);
        initCache(PluginType.USER_CUSTOM_FIELD_VALUE);
        initCache(PluginType.WEB);
        initCache(PluginType.XSLT);
        initCache(PluginType.TXT);
        initCache(PluginType.MULTI_BULK);
        initCache(PluginType.REPORT_ACTION);
        initCache(PluginType.SCHEDULER_JOB);
        initCache(PluginType.MACROS);
        initCache(PluginType.BEFORE_MAIL_IMPORT);
        initCache(PluginType.INSTEAD_OF_MAIL_IMPORT);
        initCache(PluginType.AFTER_MAIL_IMPORT);
    }

    /**
     * Вспомогательный класс, используемый для фильтрации директорий из списка файлов
     */
    private static class OnlyDirectories implements FileFilter {
        public boolean accept(File pathname) {
            return pathname.isDirectory();
        }
    }

    /**
     * Возвращает экземпляр текущего класса
     *
     * @return Экземпляр PluginCacheManager
     * @throws GranException при необходимости
     */
    public static PluginCacheManager getInstance() throws GranException {
        return instance;
    }

    /**
     * Возвращает плагин по его типу и имени, если он есть в кеше, то из кеша, если его там нет,
     * то создается новый объект, помещается в кеш и возвращается
     *
     * @param type тип плагина
     * @param name название плагина
     * @return плагин
     * @throws IOException            при чтении файла
     * @throws ClassNotFoundException при азгрузке класса
     * @see com.trackstudio.kernel.cache.AbstractPluginCacheItem
     */
    private AbstractPluginCacheItem loadItem(PluginType type, String name) throws IOException, ClassNotFoundException {
        File path = new File(Config.getInstance().getPluginsDir() + type.toString());
        File file = new File(path, name);
        // if (file.exists()) {
        if (type.equals(PluginType.ICON)) {
            BinaryPluginCacheItem vObj = new BinaryPluginCacheItem(type, name);
            readData(file, vObj);
            vObj.setLastModified(file.lastModified());
            pluginCache.putItem(type, vObj);
            return vObj;
        } else {
            if (!file.isDirectory()) {
                getCurrentDetails().add(name);
                if (name.endsWith(".class")) {
                    // compiled code
                    CompiledPluginCacheItem cpm = loadClassFromFile(type, name);
                    cpm.setLastModified(file.lastModified());
                    pluginCache.putItem(type, cpm);
                    return cpm;
                } else {
                    PluginCacheItem vObj = new PluginCacheItem(type, name);
                    readText(file, vObj);
                    vObj.setLastModified(file.lastModified());
                    pluginCache.putItem(type, vObj);
                    return vObj;
                }
            } else {
                PluginCacheItem vObj = new PluginCacheItem(type, name);
                vObj.setLastModified(file.lastModified());
                pluginCache.putItem(type, vObj);
                return vObj;
            }
        }
        //}
    }

    /**
     * Загружает текст плагина из файла в плагин
     *
     * @param file файл
     * @param vObj плагин
     * @throws IOException при чтении файла
     * @see com.trackstudio.kernel.cache.PluginCacheItem
     */
    private void readText(File file, PluginCacheItem vObj) throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(file), Config.getEncoding()));
        StringBuffer b = new StringBuffer();
        String s;
        do {
            s = r.readLine();
            if (s != null) {
                b.append(s);
                b.append("\n");
            } else break;
        } while (true);
        // ????? ??????????? ????????? ???????? ????????? ??? ?????? ?????
        r.close();
        String commentRegexp = "/\\*(.*\\n)*?.*\\*/";
        String text = b.toString();

        try {
            Pattern p = Pattern.compile(commentRegexp, Pattern.MULTILINE);
            Matcher m = p.matcher(text);
            if (m.find())
                vObj.setDescription(m.group());
        } catch (Exception e) {
            log.error("Regexp ", e);
        }

        vObj.setText(text);
    }

    /**
     * Загружает бинарные данные плагина из файла в плагин
     *
     * @param file файл
     * @param vObj плагин
     * @throws IOException при чтении файла
     * @see com.trackstudio.kernel.cache.PluginCacheItem
     */
    private void readData(File file, BinaryPluginCacheItem vObj) throws IOException {
        BufferedImage img = ImageIO.read(file);
        vObj.setData(img);
    }

    /**
     * Ищет плагин по названию и типу
     *
     * @param type тип плагина
     * @param name название плагина
     * @return плагин
     * @throws GranException при необходимости
     * @see com.trackstudio.kernel.cache.AbstractPluginCacheItem
     */
    public AbstractPluginCacheItem find(PluginType type, String name) {
        try {
            if (type == null || name == null) return null;
            List<AbstractPluginCacheItem> list = pluginCache.get(type);
            AbstractPluginCacheItem found = null;
            if (list != null && !list.isEmpty()) {
                for (AbstractPluginCacheItem p : list) {
                    if (p != null && p.getName().equals(name)) {
                        found = p;
                        break;
                    }
                }
            }
            String path = Config.getInstance().getPluginsDir() + type.toString() + "/" + name;
            File file = new File(path);
            if (found != null && !file.exists()) return found;
            if (found == null || (found.getLastModified() + DELAY) < System.currentTimeMillis() && file.lastModified() > found.getLastModified()) {
                return loadItem(type, name);
            } else {
                return found;
            }
        } catch (Exception e) {
            log.error("Error", e);
        }
        return null;
    }

    /**
     * Возвращает список плагинов указанного типа
     *
     * @param types тип плагина
     * @return список плагинов
     * @throws GranException при необходимости
     * @see com.trackstudio.kernel.cache.AbstractPluginCacheItem
     */
    public Map<PluginType, List<AbstractPluginCacheItem>> list(final PluginType... types) throws GranException {
        if (System.currentTimeMillis() - lastLoadTimestamp > 5L * 1000) { //reload if last reload is 5 secs or older. Required because load is quite expensive and slowdown a lot
            synchronized (this) {
                // double checking
                // required because other thread can update lastLoadTimestamp
                // while we wait in sync
                if (System.currentTimeMillis() - lastLoadTimestamp > 5L * 1000) { //reload if last reload is 5 secs or older. Required because load is quite expensive and slowdown a lot
                    lastLoadTimestamp = System.currentTimeMillis();
                    loadJars();
                    for (PluginType type : types) {
                        initCache(type);
                    }
                }
            }
        }
        Map<PluginType, List<AbstractPluginCacheItem>> result = new LinkedHashMap<PluginType, List<AbstractPluginCacheItem>>(types.length);
        for (PluginType type : types) {
            List<AbstractPluginCacheItem> items = pluginCache.get(type);
            if (items != null) {
                items.sort((o1, o2) -> {
                            String first = o1 != null ? o1.getName() : "";
                            String second = o2 != null ? o2.getName() : "";
                            return first.compareTo(second);
                        }
                );
            }
            result.put(type, items);
        }
        return result;
    }

    public List<AbstractPluginCacheItem> getOnlyFtlTemplateForEmail() throws GranException {
        List<AbstractPluginCacheItem> list = list(PluginType.EMAIL).get(PluginType.EMAIL);
        List<AbstractPluginCacheItem> onlyFtl = new ArrayList<AbstractPluginCacheItem>();
        for (AbstractPluginCacheItem item : list) {
            if (item != null) {
                if (item.getName().endsWith(".ftl")) {
                    onlyFtl.add(item);
                }
            }
        }
        return onlyFtl;
    }

    /**
     * Возвращает список импортов для импортируемых скриптов
     *
     * @return список импортов
     */
    private StringBuffer getImports() {
        StringBuffer a = new StringBuffer();
        a.append("import com.trackstudio.app.adapter.AdapterManager;\n");
        a.append("import com.trackstudio.app.csv.CSVImport;\n");
        a.append("import com.trackstudio.app.session.SessionContext;\n");
        a.append("import com.trackstudio.exception.*;\n");
        a.append("import com.trackstudio.kernel.cache.Action;\n");
        a.append("import com.trackstudio.kernel.manager.AclManager;\n");
        a.append("import com.trackstudio.kernel.manager.KernelManager;\n");
        a.append("import com.trackstudio.secured.*;\n");
        a.append("import com.trackstudio.securedkernel.*;\n");
        a.append("import com.trackstudio.startup.Config;\n");
        a.append("import com.trackstudio.tools.formatter.DateFormatter;\n");
        a.append("import com.trackstudio.tools.formatter.HourFormatter;\n");
        a.append("import com.trackstudio.tools.textfilter.HTMLEncoder;\n");
        a.append("import java.lang.Boolean;\n");
        a.append("import java.lang.Byte;\n");
        a.append("import java.lang.Character;\n");
        a.append("import java.lang.Class;\n");
        a.append("import java.lang.Comparable;\n");
        a.append("import java.lang.Double;\n");
        a.append("import java.lang.Exception;\n");
        a.append("import java.lang.Float;\n");
        a.append("import java.lang.Integer;\n");
        a.append("import java.lang.Long;\n");
        a.append("import java.lang.Math;\n");
        a.append("import java.lang.Number;\n");
        a.append("import java.lang.Object;\n");
        a.append("import java.lang.Short;\n");
        a.append("import java.lang.StrictMath;\n");
        a.append("import java.lang.String;\n");
        a.append("import java.lang.StringBuffer;\n");
        a.append("import java.sql.*;\n");
        a.append("import java.text.*;\n");
        a.append("import java.util.*;\n");
        a.append("import org.apache.commons.logging.Log;\n");
        a.append("import org.apache.commons.logging.LogFactory;\n");
        return a;
    }

    /**
     * Создает скрипт
     *
     * @param name    название скрипта
     * @param type    тип скрипта
     * @param formula фармула скрипта
     * @return ID созданного скрипта
     * @throws GranException при необходимости
     */
    public String createScript(String name, com.trackstudio.kernel.cache.PluginType type, String formula) throws GranException {
        try {
            String path = Config.getInstance().getPluginsDir() + type.toString() + "/";
            String fileName = name + ".bsh";
            File f = new File(path + fileName);
            if (f.exists()) {
                int tr = 0;
                do {
                    tr++;
                    fileName = name + tr + ".bsh";
                    f = new File(path + fileName);
                } while (f.exists());
            }
            f.getParentFile().mkdirs();
            f.createNewFile();

            PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(f), Config.getEncoding()));
            pw.println(getImports() + formula);
            pw.flush();
            pw.close();

            return fileName;
        } catch (IOException e) {
            throw new GranException(e, name);
        }
    }

    /**
     * Рекурсивно обходит вглубь все вложенные директории, начиная с указанной и собирает название файлов-плагинов вместе с их пакетами
     *
     * @param f           начальная директория
     * @param names       список названий файлов-плагинов вместе с их пакетами
     * @param packageName имя пакета плагинов
     */
    private void visitDirectories(File f, List<String> names, String packageName) {
        for (File n : f.listFiles()) {
            if (n.isDirectory()) {
                String newPackage = packageName + n.getName() + ".";
                visitDirectories(n, names, newPackage);
            } else {
                names.add(packageName + n.getName());
            }
        }
    }

    /**
     * Инициализирует кеш плагинов конкретного типа
     *
     * @param type тип плагина
     * @throws GranException при необходимости
     */
    private void initCache(PluginType type) {
        List<String> l = getPluginList(type);
        if (l != null) {
            log.debug(" load plugin : " + type);
            for (String s : l) {
                log.debug(" plugin : " + s);
                pluginCache.putItem(type, find(type, s));
            }
        }
    }


    /**
     * Возвращает список плагинов указанного типа
     *
     * @param type тип плагина
     * @return список плагинов
     * @throws GranException при необходимости
     */
    private List<String> getPluginList(PluginType type) {
        String path = Config.getInstance().getPluginsDir() + type.toString() + "/";
        File dir = new File(path);
        File[] list = null;
        if (dir.exists()) {
            if (type.equals(PluginType.WEB)) {
                list = dir.listFiles(new OnlyDirectories());
                List<String> dirList = new ArrayList<String>();
                for (File lst : list) {
                    dirList.add(lst.getName());
                }
                return dirList;
            } else {
                list = dir.listFiles();
            }
        }
        if (list != null) {
            List<String> names = new ArrayList<String>();
            for (File f : list) {
                String name = f.getName();
                if (name.equals("plugins") || name.startsWith(".") || name.indexOf(".java") != -1)
                    continue;
                if (f.isDirectory()) {
                    visitDirectories(f, names, name + ".");
                } else {
                    names.add(name);
                }
            }
            return names;
        }

        return null;
    }

    /**
     * Возаращает карту названий скриптов
     *
     * @param scripts список названий скриптоа
     * @return карту названий скриптов
     */
    public static HashMap<String, String> getScriptNames(List<String> scripts) {
        if (scripts == null)
            return null;
        HashMap<String, String> ret = new HashMap<String, String>();
        for (String script : scripts) {
            ret.put(script, script.replace(".bsh", ""));
        }
        return ret;
    }

    /**
     * Метод инициализирует компилированные скрипты, которое лежат в scripts
     */
    private void loadJars() {
        try {
            String path = Config.getInstance().getPluginsDir() + "scripts";
            File dir = new File(path);
            File[] files = dir.listFiles();
            if (files == null)
                return; // no scripts directory

            for (File file : files) {
                if (file.getName().contains(".jar")) {
                    Long lastModify = loadedJar.get(file.getName());
                    if (lastModify == null || !lastModify.equals(file.lastModified())) {
                        List<PluginPair> names = getClassNameFromJar(file.getPath());
                        loadJar(file.getPath(), names);
                        loadedJar.put(file.getName(), file.lastModified());
                    }
                }
            }
        } catch (Exception ioe) {
            ioe.printStackTrace();
        }
    }

    private CompiledPluginCacheItem loadClassFromFile(PluginType type, String name) throws ClassNotFoundException {
        CompiledPluginCacheItem cpm = new CompiledPluginCacheItem(type, name);
        String nameFile = type.toString().replaceAll("/", ".") + "." + name.substring(0, name.lastIndexOf(".class"));
        ClassLoader loader = new ShellClassLoader(new File(Config.getInstance().getPluginsDir()));
        Class c = loadFile(nameFile, loader);
        if (c == null) {
            c = loadFile(nameFile, PluginCacheManager.class.getClassLoader());
        }
        if (c != null) cpm.setCompiled(c);
        return cpm;
    }

    private Class loadFile(String nameFile, ClassLoader loader) {
        Class c = null;
        try {
            c = Class.forName(nameFile, true, loader);
        } catch (Exception e) {
            log.error("Error", e);
        }
        return c;
    }

    public void loadJar(String filePath, List<PluginPair> names) throws MalformedURLException {
        log.debug("Loading jar : " + filePath);
        JarClassLoader jarClassLoader = new JarClassLoader(new URL[]{}, this.getClass().getClassLoader());
        jarClassLoader.addFile(filePath);
        for (PluginPair pair : names) {
            try {
                String scriptName = pair.getKey().substring(0, pair.getKey().lastIndexOf(".class"));
                String name = scriptName.replaceAll("\\.", "/").substring(scriptName.lastIndexOf("/") + 1, scriptName.length()) + ".class";
                name = name.substring(pair.getType().toString().length() + 1).replace("/", ".");
                CompiledPluginCacheItem cpm = new CompiledPluginCacheItem(pair.getType(), name);
                Class clazz = Class.forName(scriptName, true, jarClassLoader);
                if (clazz != null) cpm.setCompiled(clazz);
                cpm.setLastModified(pair.getTime());
                pluginCache.putItem(pair.getType(), cpm);
            } catch (ClassNotFoundException e) {
                log.error("Loading jar : " + filePath, e);
            }
        }
    }

    public List<PluginPair> getClassNameFromJar(String path) throws IOException {
        List<PluginPair> name = new ArrayList<PluginPair>();
        JarFile jarFile = new JarFile(path);
        Enumeration entries = jarFile.entries();
        List<String> details = this.getCurrentDetails();
        while (entries.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) entries.nextElement();
            if (entry.getName().contains(".class")) {
                PluginType type = getType(entry.getName());
                type = type != null ? type : PluginType.EMPTY;
                String className = entry.getName().replaceAll("/", ".");
                details.add(className);
                name.add(new PluginPair(className, type, entry.getTime()));
            }
        }
        jarFile.close();
        return name;
    }

    public PluginType getType(String name) {
        String[] packages = name.split("/");
        //if (packages.length == 3) {
        String nameType = packages[0] + "/" + packages[1];
        if (nameType.equals(PluginType.AFTER_ADD_MESSAGE.toString())) {
            return PluginType.AFTER_ADD_MESSAGE;
        }
        if (nameType.equals(PluginType.AFTER_CREATE_TASK.toString())) {
            return PluginType.AFTER_CREATE_TASK;
        }
        if (nameType.equals(PluginType.AFTER_EDIT_TASK.toString())) {
            return PluginType.AFTER_EDIT_TASK;
        }
        if (nameType.equals(PluginType.BEFORE_ADD_MESSAGE.toString())) {
            return PluginType.BEFORE_ADD_MESSAGE;
        }
        if (nameType.equals(PluginType.BEFORE_CREATE_TASK.toString())) {
            return PluginType.BEFORE_CREATE_TASK;
        }
        if (nameType.equals(PluginType.BEFORE_EDIT_TASK.toString())) {
            return PluginType.BEFORE_EDIT_TASK;
        }
        if (nameType.equals(PluginType.BULK.toString())) {
            return PluginType.BULK;
        }
        if (nameType.equals(PluginType.INSTEAD_OF_ADD_MESSAGE.toString())) {
            return PluginType.INSTEAD_OF_ADD_MESSAGE;
        }
        if (nameType.equals(PluginType.INSTEAD_OF_CREATE_TASK.toString())) {
            return PluginType.INSTEAD_OF_CREATE_TASK;
        }
        if (nameType.equals(PluginType.INSTEAD_OF_EDIT_TASK.toString())) {
            return PluginType.INSTEAD_OF_EDIT_TASK;
        }
        if (nameType.equals(PluginType.TASK_CUSTOM_FIELD_LOOKUP.toString())) {
            return PluginType.TASK_CUSTOM_FIELD_LOOKUP;
        }
        if (nameType.equals(PluginType.TASK_CUSTOM_FIELD_VALUE.toString())) {
            return PluginType.TASK_CUSTOM_FIELD_VALUE;
        }
        if (nameType.equals(PluginType.USER_CUSTOM_FIELD_LOOKUP.toString())) {
            return PluginType.USER_CUSTOM_FIELD_LOOKUP;
        }
        if (nameType.equals(PluginType.USER_CUSTOM_FIELD_VALUE.toString())) {
            return PluginType.USER_CUSTOM_FIELD_VALUE;
        }
        if (nameType.equals(PluginType.MULTI_BULK.toString())) {
            return PluginType.MULTI_BULK;
        }
        if (nameType.equals(PluginType.REPORT_ACTION.toString())) {
            return PluginType.REPORT_ACTION;
        }
        if (nameType.equals(PluginType.SCHEDULER_JOB.toString())) {
            return PluginType.SCHEDULER_JOB;
        }
        if (nameType.equals(PluginType.MACROS.toString())) {
            return PluginType.MACROS;
        }
        if (nameType.equals(PluginType.AFTER_MAIL_IMPORT.toString())) {
            return PluginType.AFTER_MAIL_IMPORT;
        }
        if (nameType.equals(PluginType.BEFORE_MAIL_IMPORT.toString())) {
            return PluginType.BEFORE_MAIL_IMPORT;
        }
        if (nameType.equals(PluginType.INSTEAD_OF_MAIL_IMPORT.toString())) {
            return PluginType.INSTEAD_OF_MAIL_IMPORT;
        }
        return null;
    }

    public String getText(PluginType type, String name) throws GranException {
        String template = "";
        AbstractPluginCacheItem item = PluginCacheManager.getInstance().find(type, name);
        if (item != null) {
            template = ((PluginCacheItem) item).getText();
        }
        return template;
    }

    /**
     * This method loads the classes from properties
     *
     * @param name property name
     * @param impl checkable interface
     * @param type plugins type
     * @param <T>  class type
     * @return list of classes
     */
    public static <T> List<Class<T>> loadPlugins(String name, Class<T> impl, PluginType type) {
        List<Class<T>> classes = new CopyOnWriteArrayList<Class<T>>();
        String actions = Config.getProperty(name);
        if (actions != null) {
            for (String action : actions.split(";")) {
                try {
                    Class<?> compiledClass = loadDirectly(action);
                    if (compiledClass == null) {
                        AbstractPluginCacheItem item = PluginCacheManager.getInstance().find(type, action);
                        if (item != null) {
                            compiledClass = ((CompiledPluginCacheItem) item).getCompiled();
                        }
                    }
                    if (compiledClass != null) {
                        if (impl != null) {
                            if (Arrays.asList(compiledClass.getInterfaces()).contains(impl)) {
                                classes.add((Class<T>) compiledClass);
                            }
                        } else {
                            classes.add((Class<T>) compiledClass);
                        }
                    }
                } catch (Exception e) {
                    log.error("Could not load the class : " + action);
                }
            }
        }
        return classes;
    }

    /**
     * This method tries to load a particular class from  a default loader.
     *
     * @param className String - class's name
     * @return Class or null
     */
    private static Class<?> loadDirectly(String className) {
        Class<?> cl;
        try {
            cl = Class.forName(className);
        } catch (Exception e) {
            cl = null;
        }
        return cl;
    }

    private List<String> getCurrentDetails() {
        if (!this.detailsInfo.containsKey(lastLoadTimestamp)) {
            this.detailsInfo.put(lastLoadTimestamp, new ArrayList<String>());
        }
        return this.detailsInfo.get(lastLoadTimestamp);
    }

    public Map<Long, List<String>> getScriptLoadLog() {
        return this.detailsInfo;
    }
}