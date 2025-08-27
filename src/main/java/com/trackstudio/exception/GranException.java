package com.trackstudio.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * General TrackStudio error
 */
public class GranException extends Exception {

    private static Log log = LogFactory.getLog(GranException.class);

    private Throwable tScause = null;

    public GranException() {
        super("General TrackStudio error");
        if (!(this instanceof UserException))
            //printStackTrace();
            log.error("Exception ", this);
    }

    public GranException(Exception e) {
        super(e.getMessage());
        tScause = e;
        e.printStackTrace();
        if (!(this instanceof UserException)) {
            if (!(e instanceof GranException))
                //e.printStackTrace();
                log.error("Exception ", e);
            //printStackTrace();
            log.error("Exception ", this);
        }
    }

    public GranException(Throwable e) {
        super(e.getMessage());
        tScause = e;
        if (!(this instanceof UserException)) {
            if (!(e instanceof GranException))
                //e.printStackTrace();
                log.error("Exception ", e);
            //printStackTrace();
            log.error("Exception ", this);
        }
    }

    public GranException(String str) {
        super(str);
        if (!(this instanceof UserException)) {
            printStackTrace();
        }
    }

    public GranException(Exception e, String s) {
        super(s);
        tScause = e;
        if (!(this instanceof UserException)) {
            if (!(e instanceof GranException))
                //e.printStackTrace();
                log.error("Exception ", e);
            //printStackTrace();
            log.error("Exception ", this);
        }
    }

    public GranException(Throwable e, String s) {
        super(s);
        tScause = e;
        if (!(this instanceof UserException)) {
            if (!(e instanceof GranException))
                //e.printStackTrace();
                log.error("Exception ", e);
            //printStackTrace();
            log.error("Exception ", this);
        }
    }

    public Throwable getTSCause() {
        if (tScause == null)
            return this;
        if (tScause instanceof GranException)
            return ((GranException) tScause).getTSCause();
        return tScause;
    }

    public static String printStackTrace(Throwable e) {
        String log = "";
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            log = sw.toString() + "\n";
        } catch (Exception ex) {
            log = ex.getMessage();
        }
        return log;
    }
}