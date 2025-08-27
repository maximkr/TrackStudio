package com.trackstudio.tools;

import com.trackstudio.exception.GranException;

public interface BiFunEx<F, S, R> {
    R apply(F first, S second) throws GranException;
}
