package org.concurrent;

/*
  File: ReentrantLock.java

  Originally written by Doug Lea and released into the public domain.
  This may be used for any purposes whatsoever without acknowledgment.
  Thanks for the assistance and support of Sun Microsystems Labs,
  and everyone contributing, testing, and using this code.

  History:
  Date       Who                What
  11Jun1998  dl               Create public version
   5Aug1998  dl               replaced int counters with longs
*/

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A lock with the same semantics as builtin
 * Java synchronized locks: Once a thread has a lock, it
 * can re-obtain it any number of times without blocking.
 * The lock is made available to other threads when
 * as many releases as acquires have occurred.
 * <p>[<a href="http://gee.cs.oswego.edu/dl/classes/EDU/oswego/cs/dl/util/concurrent/intro.html"> Introduction to this package. </a>]
 */


public class ReentrantLock implements Sync {
    private static Log log = LogFactory.getLog(ReentrantLock.class);

    protected Thread owner_ = null;
    protected long holds_ = 0L;

    public void acquire() throws InterruptedException {
        if (Thread.interrupted()) throw new InterruptedException();
        Thread caller = Thread.currentThread();
        synchronized (this) {
            if (caller == owner_)
                ++holds_;
            else {
                try {
                    while (owner_ != null) wait();
                    owner_ = caller;
                    holds_ = 1L;
                } catch (InterruptedException ex) {
                    notify();
                    throw ex;
                }
            }
        }
    }


    public boolean attempt(long msecs) throws InterruptedException {
        if (Thread.interrupted()) throw new InterruptedException();
        Thread caller = Thread.currentThread();
        synchronized (this) {
            if (caller == owner_) {
                ++holds_;
                return true;
            } else if (owner_ == null) {
                owner_ = caller;
                holds_ = 1L;
                return true;
            } else if (msecs <= 0L)
                return false;
            else {
                long waitTime = msecs;
                long start = System.currentTimeMillis();
                try {
                    while (true) {
                        try {
                            wait(waitTime);
                        } catch (NullPointerException npe) {
                            log.debug("ReentrantLock NPE");
                            return false;
                        }
                        if (caller == owner_) {
                            ++holds_;
                            return true;
                        } else if (owner_ == null) {
                            owner_ = caller;
                            holds_ = 1L;
                            return true;
                        } else {
                            waitTime = msecs - (System.currentTimeMillis() - start);
                            if (waitTime <= 0L) {

                                //dnikitin: если прождав timeout лок не освободился, то мы его принудительно сбрасываем
                                holds_ = 0L;
                                owner_ = null;
                                notify();
                                return false;
                            }
                        }
                    }
                } catch (InterruptedException ex) {
                    notify();
                    throw ex;
                }
            }
        }
    }

    /**
     * Release the lock.
     *
     * @throws Error thrown if not current owner of lock
     */
    public synchronized void release() {
        if (Thread.currentThread() == owner_) {
            if (--holds_ == 0L) {
                owner_ = null;
                notify();
            }
        } else {
            //throw new Error("\n\n\nIllegal Lock usage\n\n\n");
            //dnikitin такое теперь может происходить, когда лок сняли после timeout-а
            log.debug("RELEASE LOCK");

        }
    }

    /**
     * Release the lock N times. <code>release(n)</code> is
     * equivalent in effect to:
     * <pre>
     *   for (int i = 0; i < n; ++i) release();
     * </pre>
     * <p/>
     *
     * @throws Error thrown if not current owner of lock
     *               or has fewer than N holds on the lock
     */
    public synchronized void release(long n) {
        if (Thread.currentThread() != owner_ || n > holds_) {
            //throw new Error("\n\n\nIllegal Lock usage\n\n\n");
            //dnikitin такое теперь может происходить, когда лок сняли после timeout-а
            log.debug("RELEASE LOCK");
        } else {
            holds_ -= n;
            if (holds_ == 0L) {
                owner_ = null;
                notify();
            }
        }
    }


    /**
     * Return the number of unreleased acquires performed
     * by the current thread.
     * Returns zero if current thread does not hold lock.
     */
    public synchronized long holds() {
        if (Thread.currentThread() != owner_) return 0L;
        return holds_;
    }


}
