package com.trackstudio.tools.textfilter;

import java.util.StringTokenizer;

import net.jcip.annotations.Immutable;

/**
 * Класс для фиксации длинных строк
 */
@Immutable
public class LongStringFixer {

    private static final int MAX_STRING_LENGTH = 90;

    /**
     * Вставляет CR/LF в длинные строки
     *
     * @param input входная строка
     * @return форматированный текст
     */
    public static String fixLongString(String input) {
        if (input != null) {
            StringBuilder result = new StringBuilder();
            StringTokenizer st = new StringTokenizer(input, "\n", true);
            while (st.hasMoreTokens()) {
                String token = st.nextToken();
                while (token.length() > MAX_STRING_LENGTH + 1) {
                    int delim = token.substring(0, MAX_STRING_LENGTH).lastIndexOf(' ');
                    if (delim == -1) {
                        result.append(token.substring(0, MAX_STRING_LENGTH)).append('\n');
                        token = token.substring(MAX_STRING_LENGTH);
                    } else {
                        result.append(token.substring(0, delim)).append('\n');
                        token = token.substring(delim + 1);
                    }
                }
                result.append(token);
            }
            return result.toString();
        } else {
            return null;
        }
    }
}