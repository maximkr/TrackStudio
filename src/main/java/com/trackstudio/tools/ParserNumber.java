package com.trackstudio.tools;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.jcip.annotations.Immutable;

@Immutable
public class ParserNumber {
    private static final Log log = LogFactory.getLog(ParserNumber.class);
    /**
     * Method return case Number or null. it checks follows class Integer, Long, Float, Double
     * @param value value
     * @param cl Class for cast case
     * @param <T> Generic
     * @return return need value
     */
    public static <T extends Number> Number parseValueOrNull(String value, Class<T> cl) {
        try {
            if (value != null) {
                if (cl.equals(Integer.class)) {
                    return Integer.parseInt(value);
                } else if (cl.equals(Long.class)) {
                    return Long.parseLong(value);
                } else if (cl.equals(Float.class)) {
                    return Float.parseFloat(value);
                } else if (cl.equals(Double.class)) {
                    return Double.parseDouble(value);
                }
            }
        } catch (NumberFormatException e) {
            if (value != null && !value.isEmpty()) {
               log.debug("NumberFormatException value - " + value + " cast case - " + cl.getSimpleName());
            }
            return null;
        }
        return null;
    }
}
