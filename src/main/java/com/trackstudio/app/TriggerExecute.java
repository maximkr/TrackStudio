package com.trackstudio.app;

import bsh.EvalError;
import bsh.TargetError;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.exception.TriggerException;
import com.trackstudio.exception.UserException;
import com.trackstudio.external.MailImportTrigger;
import com.trackstudio.external.OperationTrigger;
import com.trackstudio.external.TaskTrigger;
import com.trackstudio.kernel.cache.AbstractPluginCacheItem;
import com.trackstudio.kernel.cache.CompiledPluginCacheItem;
import com.trackstudio.kernel.cache.PluginCacheItem;
import com.trackstudio.model.MailImport;
import com.trackstudio.secured.Secured;
import com.trackstudio.secured.SecuredMessageTriggerBean;
import com.trackstudio.secured.SecuredTaskTriggerBean;
import net.jcip.annotations.Immutable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.mail.internet.MimeMessage;

@Immutable
public class TriggerExecute {
    private static final Log log = LogFactory.getLog(TriggerExecute.class);

    public void mail(AbstractPluginCacheItem script, MimeMessage mail, MailImport rule) throws GranException {
            try {
                Class compiledClass = ((CompiledPluginCacheItem) script).getCompiled();
                ((MailImportTrigger) compiledClass.newInstance()).execute(mail, rule);
            } catch (ClassCastException cce) {
                log.error("Error", cce);
                throw new UserException("This script " + script.getName() + " should have a type " + script.getType() + "so you need to implement com.trackstudio.external.OperationTrigger interface!");
            } catch (Exception e) {
                log.error("Error ", e);
                throw new UserException("This script " + script.getName() + " has the error : " + e.getMessage());
            }
    }

    /**
     * Выполняет триггер
     *
     * @param sc        сессия
     * @param script    скрипт
     * @param userInput входные данные - сообщение
     * @return сообщение
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredMessageTriggerBean
     */
    public SecuredMessageTriggerBean executeTrigger(SessionContext sc, AbstractPluginCacheItem script, SecuredMessageTriggerBean userInput) throws GranException {
        SecuredMessageTriggerBean ret = null;
        if (script instanceof CompiledPluginCacheItem) {
            try {
                Class compiledClass = ((CompiledPluginCacheItem) script).getCompiled();
                OperationTrigger compiled = (OperationTrigger) compiledClass.newInstance();
                ret = compiled.execute(userInput);
            } catch (ClassCastException cce) {
                log.error("Error", cce);
                throw new UserException("This script " + script.getName() + " should have a type " + script.getType() + "so you need to implement com.trackstudio.external.OperationTrigger interface!");
            } catch (NullPointerException e) {
                log.error("Error ", e);
                throw new UserException("This script " + script.getName() + " has the error : " + e.getMessage());
            } catch (InstantiationException ie) {
                throw new TriggerException(script.getName(), ie);
            } catch (IllegalAccessException e) {
                throw new TriggerException(script.getName(), e);
            }
        } else {
            Object o = executeCommonTrigger(sc, (PluginCacheItem) script, userInput);
            if (o != null) {
                ret = (SecuredMessageTriggerBean) o;
            }
        }
        if (ret != null) {
            return ret;
        } else {
            return userInput;
        }
    }

    /**
     * Выполняет триггер
     *
     * @param sc        сессия
     * @param script    скрипт
     * @param userInput входные данные - задача
     * @return задача
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredTaskTriggerBean
     */
    public SecuredTaskTriggerBean executeTrigger(SessionContext sc, AbstractPluginCacheItem script, SecuredTaskTriggerBean userInput) throws GranException {
        SecuredTaskTriggerBean ret = null;
        if (script != null && script instanceof CompiledPluginCacheItem) {
            try {
                Class compiledClass = ((CompiledPluginCacheItem) script).getCompiled();
                TaskTrigger compiled;
                try {
                    compiled = (TaskTrigger) compiledClass.newInstance();
                } catch (ClassCastException e) {
                    log.error("Error", e);
                    throw new UserException("This script " + script.getName() + " has a type " + script.getType() + " for this reason you need to implement a com.trackstudio.external.TaskTrigger interface!");
                }
                if (compiled != null) {
                    ret = compiled.execute(userInput);
                }
            } catch (UserException ue) {
                throw ue;
            } catch (Exception ie) {
                throw new TriggerException(script.getName(), ie);
            }
        } else {
            Object o = executeCommonTrigger(sc, (PluginCacheItem) script, userInput);
            if (o != null) {
                ret = (SecuredTaskTriggerBean) o;
            }
        }
        if (ret != null) {
            return ret;
        } else {
            return userInput;
        }
    }


    /**
     * Выполняет триггер
     *
     * @param sc        сессия
     * @param script    скрипт
     * @param userInput входные данные
     * @return объект
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.Secured
     */
    public Secured executeCommonTrigger(SessionContext sc, PluginCacheItem script, Secured userInput) throws GranException {
        if (script != null) {
            Object ret = null;
            String formula = script.getText();
            CalculatedValue cv = new CalculatedValue(formula, userInput);
            try {
                ret = cv.getValue();
            } catch (TargetError e) {
                throw new TriggerException(script.getName(), e.getTarget());
            } catch (EvalError ee) {
                throw new TriggerException(script.getName(), ee);
            } catch (Exception ex) {
                throw new TriggerException(script.getName(), ex);
            }
            try {
                if (ret != null) {
                    return (Secured) ret;
                }
            } catch (Exception e) {
                throw new TriggerException(script.getName(), e);
            }
        }
        return userInput;
    }

}
