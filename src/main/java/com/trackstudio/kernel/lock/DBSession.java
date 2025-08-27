package com.trackstudio.kernel.lock;

import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.trackstudio.exception.GranException;
import com.trackstudio.startup.Config;

import net.jcip.annotations.ThreadSafe;
import org.hibernate.Transaction;
import org.hibernate.boot.model.naming.IllegalIdentifierException;
import org.hibernate.resource.transaction.spi.TransactionStatus;

@ThreadSafe
public class DBSession {
    private final SessionFactory sf;

    protected DBSession() {
        try {
            sf = Config.getInstance().getSessionFactory();
        } catch (GranException e) {
            throw new IllegalStateException(e);
        }
    }

    protected void closeSession() {
        if (sf.getCurrentSession().getTransaction().getStatus() == TransactionStatus.ACTIVE) {
            sf.getCurrentSession().getTransaction().commit();
        }

        if (sf.getCurrentSession().isOpen()) {
            sf.getCurrentSession().close();
        }
    }

    public void commit() {
        if (sf.getCurrentSession().getTransaction().getStatus() == TransactionStatus.ACTIVE) {
            sf.getCurrentSession().flush();
            sf.getCurrentSession().clear();
            sf.getCurrentSession().getTransaction().commit();
        }
    }

    public Session getSession() {
        if (sf.getCurrentSession().getTransaction().getStatus() != TransactionStatus.ACTIVE) {
            if (sf.getCurrentSession().getTransaction().getStatus() == TransactionStatus.MARKED_ROLLBACK) {
                sf.getCurrentSession().flush();
                sf.getCurrentSession().clear();
                sf.getCurrentSession().getTransaction().commit();
            }
            sf.getCurrentSession().getTransaction().begin();
        }
        return sf.getCurrentSession();
    }

}
