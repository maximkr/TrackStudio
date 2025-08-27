package com.trackstudio.app.adapter.auth;

import java.security.cert.X509Certificate;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.app.adapter.AuthAdapter;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.UserRelatedInfo;
import com.trackstudio.kernel.cache.UserRelatedManager;
import com.trackstudio.kernel.manager.KernelManager;

import net.jcip.annotations.Immutable;

@Immutable
public class SSLCerfAuthAdapter implements AuthAdapter {
    private static final Log log = LogFactory.getLog(SSLCerfAuthAdapter.class);
    private final ThreadLocal<UserRelatedInfo> user = new ThreadLocal<UserRelatedInfo>(); // different user session shouldn't override this

    @Override
    public boolean authorizeImpl(String id, String password, boolean result, HttpServletRequest request) throws GranException {
        if (request != null) {
            X509Certificate[] certs = (X509Certificate[]) request.getAttribute("javax.servlet.request.X509Certificate");
//            log.debug("Check X.509 - " + (certs != null));
            if (certs != null) {
                X509Certificate cert = certs[0];
                String login = parseLogin(cert.getSubjectDN().getName());
                log.debug("Check username - " + login);
                String usId = KernelManager.getUser().findByLogin(login);
                log.debug("Result checked username - " + (usId != null));
                if (usId != null) {
                    user.set(UserRelatedManager.getInstance().find(usId));
                    return true;
                }
            }
        }
        return false;
    }

    public UserRelatedInfo getUser() {
        return user.get();
    }

    private static String parseLogin(String text) {
        String[] cn = text.split(",");
        Map<String, String> map = new LinkedHashMap<String, String>();
        for (String value : cn) {
            String[] pair = value.split("=");
            if (pair.length == 2) {
                map.put(pair[0].trim(), pair[1].trim());
            }
        }
        return map.get("CN");
    }

    @Override
    public void changePasswordImpl(String userId, String password) throws GranException {
    }

    @Override
    public boolean init() {
        return true;
    }

    @Override
    public String getDescription() {
        return "SSL Certification Authentication Adapter";
    }
}
