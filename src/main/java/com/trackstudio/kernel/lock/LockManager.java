package com.trackstudio.kernel.lock;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

import net.jcip.annotations.Immutable;

/**
 * Класс для управления блокировками
 * <br/>
 * правила выставления локов - см #18908
 * Там вообще с локами алгоритм расстановки был такой (должны выполнятся все условия):
 * 1) Ставим в kernel
 * 2) Ставим на все public методы (на private не нужно т.к. их вызывают другие public)
 * 3)  Не ставим лок на часть метода, только целиком. Если очень нужно на часть - делаем 2 метода.
 * <p/>
 * Категорически нельзя в одном методе использовать и локи, и synchronized. Еще
 * нельзя ставить лок и вызывать synchronized-методы. Для этого мы делаем так:
 * в kernel используем только локи,  во всем остальном - только synchronized.
 * При этом kernel ко всему остальному не лезет и synch-методы не вызывает
 */

@Immutable
public class LockManager {

    private final static LockManager instance = new LockManager();
    private final ConcurrentMap<String, ReentrantLock> locks = new ConcurrentHashMap<String, ReentrantLock>(256);
    private final ThreadLocal<DBSession> session = new ThreadLocal<DBSession>();

    private LockManager() {
    }

    public ReentrantLock getLock(String id) {
        if (id==null)
            id="null";
        ReentrantLock rwl = locks.get(id);
        if (rwl!=null)
            return rwl;
        ReentrantLock newLock = new ReentrantLock();
        ReentrantLock existsLock = locks.putIfAbsent(id, newLock);
        if (existsLock==null) {
            return newLock;
        } else {
            return existsLock;
        }
    }
    /**
     * Возвращает экземпляр текущего класса
     *
     * @return экземпляр класса
     */
    public static LockManager getInstance() {
        return instance;
    }

    public boolean acquireConnection(String str) {
        return acquireConnection();
    }

    public boolean acquireConnection() {
        DBSession sess = session.get();

        if (sess == null) {
            //System.out.println("===== Open session");
            sess = new DBSession();
            session.set(sess);
            return true;
        }
        return false;
    }

    public void releaseConnection(String str) {
        releaseConnection();
    }

    public void releaseConnection() {
        DBSession sess = session.get();
        if (sess != null) {
            //System.out.println("===== Close session");
            sess.closeSession();
            session.set(null);
        }
    }

    public DBSession getDBSession() {
        DBSession sess = session.get();
        if (sess == null)
            new Throwable("Session is closed").printStackTrace();
        return sess;
    }

}