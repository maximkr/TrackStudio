// ikozhekin
package com.trackstudio.exception;

public class SimplePasswordException extends BadPasswordException {

    public SimplePasswordException() {
        super("ERROR_PASSWORD_TOO_SIMPLE");
    }
}

