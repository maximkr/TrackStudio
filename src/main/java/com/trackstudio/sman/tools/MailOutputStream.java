package com.trackstudio.sman.tools;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MailOutputStream extends OutputStream {
    private boolean server = true;

    public MailOutputStream(boolean server) {
        this.server = server;
    }

    private static Log log = LogFactory.getLog(MailOutputStream.class);

    @Override
    public void write(int b) throws IOException {
//        log.debug((char) b);
    }
}