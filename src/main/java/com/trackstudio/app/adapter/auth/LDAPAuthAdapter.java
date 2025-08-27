package com.trackstudio.app.adapter.auth;

import java.util.List;
import java.util.Locale;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.app.adapter.AuthAdapter;
import com.trackstudio.exception.GranException;
import com.trackstudio.exception.UserException;
import com.trackstudio.kernel.cache.UserRelatedInfo;
import com.trackstudio.kernel.cache.UserRelatedManager;
import com.trackstudio.ldap.LdapConnector;
import com.trackstudio.startup.Config;

import net.jcip.annotations.Immutable;

/**
 * Класс авторизации в LDAP
 */
@Immutable
public class LDAPAuthAdapter implements AuthAdapter {

    private static final Log log = LogFactory.getLog(LDAPAuthAdapter.class);

    /**
     * Инициализирует адаптер
     *
     * @return TRUE - инициализация прошла успешно, FALSE - нет
     */
    public boolean init() {
        return true;
    }

    /**
     * Возвращает текстовое описание адаптера
     *
     * @return adapter's description
     */
    public String getDescription() {
        return "LDAP Authentication Adapter";
    }

    /**
     * Производит авторизацию
     *
     * @param userId   ID пользователя
     * @param password пароль
     * @param result   Результат авторизации
     * @return TRUE - если авторизация прошла удачно, FALSE - если нет
     * @throws GranException при необходимости
     */
    public boolean authorizeImpl(String userId, String password, boolean result, HttpServletRequest request) throws GranException {
        log.trace("authorize");
        //if we already authenticated or ldap properties undefined - skip all
        if (result) {
            return result;
        }

        if (Config.getInstance().getProperty("trackstudio.useLDAP") != null && Config.getInstance().getProperty("trackstudio.useLDAP").equals("yes") && Config.getInstance().getProperty("ldap.host") != null) {
            String host = Config.getInstance().getProperty("ldap.host");
            String port = Config.getInstance().getProperty("ldap.port");
            String baseDN = Config.getInstance().getProperty("ldap.baseDN");
            String userDN = Config.getInstance().getProperty("ldap.userDN");
            String userDNpass = Config.getInstance().getProperty("ldap.userDNpass");
            String filter = Config.getInstance().getProperty("ldap.filter");
            String useSSL = Config.getInstance().getProperty("ldap.useSSL");

            if (userDNpass == null || userDN == null || userDNpass.length() == 0 || userDN.length() == 0) {
                log.info("userDN or userDNpass is empty, use anonimous authorization");
            } else {
                log.info("Authenticating as user DN = \"" + userDN + '\"');
            }

            if (baseDN == null)
                throw new GranException("property ldap.baseDN not found");

            if (port == null)
                throw new GranException("property ldap.port not found");

            String userLogin;
            UserRelatedInfo user = UserRelatedManager.getInstance().find(userId);
            if (Config.getInstance().getProperty("ldap.loginAttrTS") != null && Config.getInstance().getProperty("ldap.loginAttrTS").toLowerCase(Locale.ENGLISH).equals("name")) {
                userLogin = user.getName();
            } else {
                userLogin = user.getLogin();
            }

            if (filter == null)
                throw new GranException("property ldap.filter not found");

            if (password == null || password.length() == 0)
                throw new UserException("ERROR_EMPTY_PASSWORD");

            LdapConnector ldc = null;
            LdapConnector ldcAuth = null;

            try {
                log.debug("Connecting to LDAP server");
                ldc = new LdapConnector(host, port, userDN, userDNpass, useSSL);

                log.debug("Search for " + filter + " " + userLogin);
                for (String dc : baseDN.split(";")) {
                    log.debug("Search for " + filter + " " + userLogin + " BaseDN: " + dc);
                    List<String> loginUserDNlist = ldc.searchDN(dc, filter, userLogin);
                    log.debug("Total found users : " + loginUserDNlist.size());
                    if (loginUserDNlist != null) {
                        for(String loginUserDN : loginUserDNlist) {
                            if (loginUserDN != null) {
                                try {
                                    log.debug("Try to login in ldap as " + loginUserDN);
                                    ldcAuth = new LdapConnector(host, port, loginUserDN, password, useSSL);
                                    log.debug("Authetificated successfully: " + loginUserDN);
                                    return true;
                                } catch (Exception e) {
                                    log.error("Error", e);
                                } finally {
                                    if (ldcAuth != null) ldcAuth.disconnect();
                                }
                            }
                        }
                    }
                    log.debug("Not authetificated in LDAP: " + userLogin);
                }
            } catch (NamingException e) {
                log.debug("Error", e);
                throw new UserException(e, "LDAP_CANT_CONNECT_TO_SERVER");
            } finally {
                if (ldc != null) ldc.disconnect();
            }
        }
        return false;
    }

    /**
     * Меняет пароль пользователя. Реализация не нужна
     *
     * @param userId   ID пользователя
     * @param password пароль
     * @throws GranException при необходимости
     */
    public void changePasswordImpl(String userId, String password) throws GranException {
    }
}