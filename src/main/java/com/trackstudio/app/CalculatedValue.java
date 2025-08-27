package com.trackstudio.app;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.PluginCacheItem;
import com.trackstudio.secured.Secured;
import com.trackstudio.secured.SecuredMessageBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUserBean;

import bsh.BshClassManager;
import bsh.EvalError;
import bsh.Interpreter;
import bsh.NameSpace;
import freemarker.core.Environment;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

/**
 * Класс, описывает вычисляемое пользовательское поле
 */
@ThreadSafe
public class CalculatedValue {
    /**
     * Задача
     */
    public static final String TASK = "task";
    /**
     * Пользователь
     */
    public static final String USER = "user";
    /**
     * Сообщение
     */
    public static final String MESSAGE = "message";
    /**
     * Сессия
     */
    public static final String SC = "sc";
    /**
     * Логгер
     */
    public static final String LOG = "log";

    public static final String TASKS = "tasks";


    private final NameSpace namespace = new NameSpace(new BshClassManager(), "root");
    private final static Log log = LogFactory.getLog("com.trackstudio.SCRIPT");
    // It's thread safe: http://www.beanshell.org/oldmail/msg00040.html
    private final static Interpreter interpreter = new Interpreter();

    private final String formula;

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    @GuardedBy("lock")
    private Object value;
    /**
     * Возвращает экземпляр текущего класса
     *
     * @return экземпляр CalculatedValue
     * @throws GranException при необходимости
     */
    public static CalculatedValue getInstance() throws GranException {
        return new CalculatedValue();
    }

    private CalculatedValue() {
        this.formula = null;
        try {
            interpreter.set("SECONDS", 1000L);
            interpreter.set("MINUTES", 60000L);
            interpreter.set("HOURS", 3600000L);
            interpreter.set("DAYS", 24L * 3600000L);
        } catch (Exception e) {
            log.error("Error", e);
        }
    }

    /**
     * Конструктор
     *
     * @param formula формула
     * @param tci     задача
     * @throws GranException при неободимлсти
     */
    public CalculatedValue(String formula, Secured tci) throws GranException {
        interpreter.setNameSpace(namespace);
        this.formula = formula;

        try {
            interpreter.set("SECONDS", 1000L);
            interpreter.set("MINUTES", 60000L);
            interpreter.set("HOURS", 3600000L);
            interpreter.set("DAYS", 24L * 3600000L);

            if (tci instanceof SecuredTaskBean) {
                interpreter.set(TASK, tci);
            } else if (tci instanceof SecuredUserBean) {
                interpreter.set(USER, tci);
            } else if (tci instanceof SecuredMessageBean) {
                interpreter.set(MESSAGE, tci);
            }
            interpreter.set(SC, tci.getSecure());
            interpreter.set(LOG, log);
        } catch (Exception ev) {
            throw new GranException(ev);
        }
    }

    public CalculatedValue(String formula, List<SecuredTaskBean> tasks, SessionContext sc) throws GranException {
        interpreter.setNameSpace(namespace);
        this.formula = formula;
        try {
            interpreter.set("SECONDS", 1000L);
            interpreter.set("MINUTES", 60000L);
            interpreter.set("HOURS", 3600000L);
            interpreter.set("DAYS", 24L * 3600000L);

            interpreter.set(TASKS, tasks);
            interpreter.set(SC, sc);
            interpreter.set(LOG, log);
        } catch (Exception ev) {
            throw new GranException(ev);
        }
    }


    /**
     * Возвращает значение
     *
     * @return значение
     * @throws EvalError при необзодимости
     */
    public Object getValue() throws EvalError {
        lock.writeLock().lock();
        try {
            if (value == null && formula != null) {
                value = interpreter.eval(formula, interpreter.getNameSpace());
            }
            return value;
        } finally {
            lock.writeLock().unlock();
        }

    }


    /**
     * Производит вычисления
     *
     * @param text формула
     * @return результат вычисления
     * @throws GranException при необходимости
     * @throws EvalError     при необходимости
     */
    public Object eval(String text) throws GranException, EvalError {
        return this.eval(new HashMap<String, Object>(), text);
    }

    public Object eval(Map<String, Object> parameters, String text) throws GranException, EvalError {
        try {
            for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                interpreter.set(entry.getKey(), entry.getValue());
            }
            interpreter.set(LOG, log);

            Environment currentEnvironment = Environment.getCurrentEnvironment();
            TemplateHashModel jvars = currentEnvironment.getDataModel();

            Set set = currentEnvironment.getKnownVariableNames();
            for (Object aSet : set) {
                String key = aSet.toString();
                try {
                    TemplateModel templateModel = jvars.get(key);
                    if (templateModel != null) {
                        Object o = freemarker.template.utility.DeepUnwrap.unwrap(templateModel);
                        System.out.println("define " + key + "=" + o);
                        interpreter.set(key, o);
                    }
                } catch (TemplateModelException e) {
                    log.error(e);
                }
            }
            interpreter.getNameSpace().importClass("com.trackstudio.app.csv.CSVImport");
            interpreter.getNameSpace().importClass("com.trackstudio.secured.SecuredTaskTriggerBean");
            interpreter.getNameSpace().importClass("com.trackstudio.secured.SecuredMessageTriggerBean");
            return interpreter.eval(text, interpreter.getNameSpace());
        } catch (Exception e) {
            log.error("Evaluation error", e);
            throw new GranException(e);
        }
    }

    /**
     * Возврашщает значение
     *
     * @param plugin плагин
     * @param tci    задача
     * @return Значение
     * @throws GranException при необходимости
     */
    public Object getValue(PluginCacheItem plugin, Secured tci) throws GranException {
        if (plugin != null) {
            try {
                if (tci instanceof SecuredTaskBean) {
                    interpreter.set(TASK, tci);
                } else if (tci instanceof SecuredUserBean) {
                    interpreter.set(USER, tci);
                } else if (tci instanceof SecuredMessageBean) {
                    interpreter.set(MESSAGE, tci);
                }
                interpreter.set(SC, tci.getSecure());
                interpreter.set(LOG, log);
                if (plugin.getText() != null)
                    return interpreter.eval(plugin.getText(), interpreter.getNameSpace());
            } catch (Exception e) {
                log.error("Error occured while executing script '" + plugin.getName() + "'" , e);
                throw new GranException(e);
            }
        }
        return null;
    }


}
