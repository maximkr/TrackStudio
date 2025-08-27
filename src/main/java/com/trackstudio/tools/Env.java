package com.trackstudio.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.trackstudio.exception.GranException;

import net.jcip.annotations.ThreadSafe;

/**
 * Класс для работы с окружением
 */
@ThreadSafe
public class Env {
    private static Env instance;
    private final ConcurrentMap<String, String> procEnvironment = new ConcurrentHashMap<String, String>();

    private Env() throws IOException {
        String[] cmd;
        if (System.getProperty("os.name").toLowerCase(Locale.ENGLISH).indexOf("windows") == -1) {
            cmd = new String[]{"/usr/bin/env"};
        } else {
            cmd = new String[]{"cmd", "/c", "SET"};
        }
        Process proc = Runtime.getRuntime().exec(cmd);
        BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        String line;
        while ((line = in.readLine()) != null) {
            if (line.indexOf((int) '=') != -1) {
                procEnvironment.put(line.substring(0, line.indexOf('=')), line.substring(line.indexOf('=') + 1));
            }
        }
        in.close();
    }

    /**
     * Возвращает экземпляр окружения
     *
     * @return экземпляр окружения
     * @throws GranException при необходимости
     */
    public static synchronized Env getInstance() throws GranException {
        try {
            if (instance == null)
                instance = new Env();
            return instance;
        } catch (Exception e) {
            throw new GranException(e);
        }
    }

    /**
     * Возвращает конфиг
     *
     * @return конфиг
     */
    public String getTSConfig() {
        return procEnvironment.get("TS_CONFIG");
    }
}