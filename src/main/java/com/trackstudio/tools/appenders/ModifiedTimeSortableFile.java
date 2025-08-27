package com.trackstudio.tools.appenders;

import java.io.File;
import java.io.Serializable;
import java.net.URI;

/**
 * The Class ModifiedTimeSortableFile extends java.io.File class and
 * implements Comparable to sort files list based upon their modified date
 */
public class ModifiedTimeSortableFile extends File implements Serializable, Comparable<File> {
    private static final long serialVersionUID = 1373373728209668895L;

    public ModifiedTimeSortableFile(String parent, String child) {
        super(parent, child);
    }


    public ModifiedTimeSortableFile(URI uri) {
        super(uri);
    }

    public ModifiedTimeSortableFile(File parent, String child) {
        super(parent, child);
    }


    public ModifiedTimeSortableFile(String string) {
        super(string);
    }

    public int compareTo(File anotherPathName) {
        long thisVal = this.lastModified();
        long anotherVal = anotherPathName.lastModified();
        return (thisVal<anotherVal ? -1 : (thisVal==anotherVal ? 0 : 1));
    }
}
