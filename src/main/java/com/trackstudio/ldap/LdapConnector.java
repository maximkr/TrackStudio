package com.trackstudio.ldap;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import net.jcip.annotations.Immutable;


@Immutable
public class LdapConnector {

    private final DirContext ctx;

    public LdapConnector(String host, String port, String userDN, String userDNpass, String useSSL) throws NamingException {
        Properties env = new Properties();

        env.put("java.naming.provider.url", "ldap://" + host + ":" + port);
        env.put("java.naming.referral", "follow");
        env.put("java.naming.batchsize", "0");
        env.put("java.naming.factory.initial", "com.sun.jndi.ldap.LdapCtxFactory");

        if (userDN != null && userDN.length()>0 && userDNpass != null && userDNpass.length()>0) {
            env.put("java.naming.security.authentication", "simple");
            env.put("java.naming.security.principal", userDN);
            env.put("java.naming.security.credentials", userDNpass);
        }
        if (useSSL!=null && useSSL.equalsIgnoreCase("yes")) {
            env.put("java.naming.security.protocol", "ssl" );
            env.put("java.naming.ldap.factory.socket", "javax.net.ssl.SSLSocketFactory");
        }
        ctx = new InitialDirContext(env);
    }


    public void disconnect(){
        try {
            ctx.close();
        } catch(NamingException e) {
            //System.out.println("Error Ldap disconnect");
        }
    }

    /* public String searchDN(String baseDN, String property, String propertyValue) throws NamingException {
     String[] attrIDs = {"dn","distinguishedName"};
     SearchControls ctls = new SearchControls();
     ctls.setReturningAttributes(attrIDs);
     ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
     String filter = "(&(objectClass=*)("+property+"={0}))";
     String[] filterParams = {propertyValue};

     NamingEnumeration results = ctx.search(baseDN, filter, filterParams, ctls);
     if (results.hasMoreElements()) {
         SearchResult sr = (SearchResult)results.next();
         String dn = getFixedDN(sr.getName(), baseDN);
         //System.out.println("Found:" + dn);
         return dn;
     }
     return null;
 }

 private String getFixedDN(String rdn, String base) {
     String result;
     if (rdn.length() > 0 && rdn.charAt(0) == '"') {
         int size = rdn.length() - 1;
         StringBuffer buf = new StringBuffer();
         for (int i = 1; i < size; i++) {
             if (rdn.charAt(i) == '/')
                 buf.append("\\");
             buf.append(rdn.charAt(i));
         }

         result = buf.toString();
     } else {
         result = rdn;
     }
     if (result.length() == 0)
         return base;
     if (base.length() == 0)
         return result;
     else
         return result + ", " + base;

 }   */

    public List<String> searchDN(String baseDN, String filter, String propertyValue) throws NamingException {
        String[] attrIDs = {"dn","distinguishedName"};
        SearchControls ctls = new SearchControls();
        ctls.setReturningAttributes(attrIDs);
        ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        String[] filterParams = {propertyValue};

        NamingEnumeration results = ctx.search(baseDN, filter, filterParams, ctls);
        List<String> dnList = new ArrayList<String>();
        while (results.hasMoreElements()) {
            SearchResult sr = (SearchResult) results.next();
            String dn = getFixedDN(sr.getNameInNamespace(), baseDN);
            if(dn != null) {// && dn.matches("(?i)^.*?" + propertyValue + ".*?$")) {
                dnList.add(dn);
            }
        }
        return dnList;
    }

    private String getFixedDN(String rdn, String base) {
        String result;
        if (rdn.contains("\\22")) {
            rdn = rdn.replaceAll("\\\\22", "\\\\\"");
        }
        if (rdn.length() > 0 && rdn.charAt(0) == '"') {
            int size = rdn.length() - 1;
            StringBuffer buf = new StringBuffer();
            for (int i = 1; i < size; i++) {
                if (rdn.charAt(i) == '/')
                    buf.append("\\");
                buf.append(rdn.charAt(i));
            }

            result = buf.toString();
        } else {
            result = rdn;
        }
        if (result.length() == 0)
            return base;
        if (base.length() == 0)
            return result;
        else {
            if (result.contains("\"")) {
                String tempUserDN = result.replaceAll("\\\\", "");
                String tempBaseDN = base.replaceAll("\\\\", "");
                if (!tempUserDN.toUpperCase().contains(tempBaseDN.toUpperCase())) {
                    return result + "," + base;
                } else {
                    return result;
                }
            }
            if (result.toUpperCase().indexOf(base.toUpperCase()) == -1) {
                return result + "," + base;
            } else {
                return result;
            }
        }
    }

//    public static void main(String[] arg) throws Exception {
//        String userDN = "cn=admin,dc=example,dc=com";
//        Properties env = new Properties();
//        env.put( Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory" );
//        env.put( Context.PROVIDER_URL, "ldap://localhost:389/" );
//        env.put(Context.SECURITY_AUTHENTICATION, "simple");
//        env.put(Context.SECURITY_PRINCIPAL, userDN);
//        env.put(Context.SECURITY_CREDENTIALS, "secret");
//
//        try {
//            DirContext ctx = new InitialDirContext(env);
//            String userTs = "cn=agent007,dc=example,dc=com";
//            String pssTs = "agent007";
//            ctx.addToEnvironment(Context.SECURITY_CREDENTIALS, pssTs);
//            Name composite = new CompositeName().add(userTs);
//            ctx.lookup(composite);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public DirContext getCtx() {
        return ctx;
    }
}
