package com.trackstudio.tools;

import java.util.Iterator;
import java.util.List;

public class ListBreaker<T extends List> implements Iterable<T> {

    private final T list;
    private final int splitBy;

    public ListBreaker(T list, int splitBy) {
        this.list = list;
        this.splitBy = splitBy;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int position = 0;

            @Override
            public boolean hasNext() {
                return (list.size() >= this.position + splitBy) || this.position == 0;
            }

            @Override
            public T next() {
                T sub;
                if (list.size() >= this.position + splitBy) {
                    sub = (T) list.subList(this.position, this.position + splitBy);
                } else {
                    sub = (T) list.subList(this.position, list.size());
                }
                this.position += splitBy;
                return sub;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
