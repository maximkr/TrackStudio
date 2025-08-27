package com.trackstudio.tools;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.trackstudio.exception.GranException;
import com.trackstudio.exception.SecurityViolationException;
import com.trackstudio.startup.Config;

import net.jcip.annotations.ThreadSafe;

/**
 * Класс для проверки правильности пароля
 */
@ThreadSafe
public class PasswordValidator {

    private static final String passListFile = "SimplePasswords.txt";
    private static volatile String passListPath;
    /**
     * Длинна хеша пароля
     */
    public static final int END_INDEX = 32;
    /**
     * Максимальное количество хранимых паролей
     */
    public static final int MAX_PASSWORDS = 7;

    private PasswordValidator() {
    }

    private static boolean findInPasswordsList(String password) throws GranException {

        if (passListPath == null)
            throw new GranException("Can't find path to SimplePasswords.txt file.");

        BufferedReader passwordsList;
        // opening file
        try {
            passwordsList = new BufferedReader(new FileReader(passListPath + '/' + passListFile));
        } catch (FileNotFoundException e) {
            throw new GranException(e);
        }
        // reading file
        try {
            String simplePassword;
            while ((simplePassword = passwordsList.readLine()) != null)
                if (simplePassword.compareToIgnoreCase(password) == 0)
                    return false;
            return true;

        } catch (IOException e) {
            throw new GranException(e);

        } finally {
            // closing file
            try {
                passwordsList.close();
            } catch (IOException e) {
                throw new GranException(e);
            }
        }
    }

    /**
     * Устанавливает путь к файлу простых паролей
     *
     * @param fullPath путь
     */
    public static void setPassFilePath(String fullPath) {
        passListPath = fullPath;
    }

    /**
     * Проверяет валидность пароля
     *
     * @param password пароль
     * @return TRUE - валидный, FALSE - нет
     * @throws GranException при необходимости
     */
    public static boolean passwordIsValid(String password) throws GranException {
        //check for minlength
        String minLength = Config.getProperty("trackstudio.security.password.min");
        if (minLength != null && minLength.length() > 0) {
            int min = Integer.parseInt(minLength);
            if (password.length() < min)
                throw new SecurityViolationException("ERROR_PASSWORD_MIN", new String[]{minLength});
        }
        if (Config.isTurnItOn("trackstudio.security.password.complex")) {
            if (!findInPasswordsList(password)) throw new SecurityViolationException("ERROR_PASSWORD_COMPLEX");
        }
        return true;
    }
}