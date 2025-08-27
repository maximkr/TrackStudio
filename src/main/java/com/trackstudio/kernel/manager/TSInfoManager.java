/*
 * @(#)TSInfoManager.java
 * Copyright 2009 TrackStudio, Inc. All rights reserved.
 */
package com.trackstudio.kernel.manager;

import com.trackstudio.exception.GranException;

import net.jcip.annotations.Immutable;

/**
 * Класс TSInfoManager содержит методы для получения информации о TS
 */
@Immutable
public class TSInfoManager extends KernelManager {

    /**
     * Конструктор по умолчанию
     */
    protected TSInfoManager() {
    }

    /**
     * Возвращает версию TS
     *
     * @return версия
     */
    public String getTSVersion() {
        return "6.0 OS";
    }

}