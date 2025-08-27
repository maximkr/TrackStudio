package com.trackstudio.tools;

import java.util.ArrayList;
import java.util.List;

/**
 * Process String.intern on all items
 */
public class Intern {

    public static ArrayList<String> process(List<String> src)
    {
        // Processing all items via intern
        ArrayList<String> internList = new ArrayList<String>();
        for (String item : src) {
            internList.add(item.intern());
        }
        return internList;
    }

    public static String process(String src)
    {
        if (src==null)
            return null;
        return src.intern();
    }
}
