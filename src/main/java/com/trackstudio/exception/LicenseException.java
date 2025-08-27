package com.trackstudio.exception;

public class LicenseException extends UserException {

    public LicenseException(String m) {
        super(m);
    }

    public LicenseException(String m, Object[] o) {
        super(m, o);
    }
}