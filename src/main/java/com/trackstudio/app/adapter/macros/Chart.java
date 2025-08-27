package com.trackstudio.app.adapter.macros;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.trackstudio.exception.GranException;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.tools.Null;

import net.jcip.annotations.Immutable;

@Immutable
public abstract class Chart {
    protected final int PERIODS = 40;
    protected final String options;
    protected final SecuredTaskBean task;
    protected final CopyOnWriteArrayList<SecuredTaskBean> list;

    public Chart(SecuredTaskBean task, List<SecuredTaskBean> list, String options) {
        this.task = task;
        ArrayList tempList = new ArrayList(list);
        Collections.sort(tempList, new Comparator<SecuredTaskBean>() {
            @Override
            public int compare(SecuredTaskBean o1, SecuredTaskBean o2) {
                try {
                    int j = o1.getSubmitdate().compareTo(o2.getSubmitdate());
                    if (j != 0) return j;
                    else return o1.compareTo(o2);
                } catch (GranException ge) {
                    return -1;
                }
            }
        });

        this.list = new CopyOnWriteArrayList(Null.removeNullElementsFromList(tempList));
        this.options = options;
    }

    public abstract String calculate() throws GranException;

}
