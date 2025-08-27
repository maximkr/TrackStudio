package com.trackstudio.tools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;

import net.jcip.annotations.Immutable;

/**
 * Специальный класс-контейнер для хранения списка списков
 *
 * @param <G> ключ
 * @param <V> значение
 */
@Immutable
public class EggBasket<G, V> implements Map<G, List<V>> {

    private final Map<G, List<V>> map;

    /** Конструктор */
    public EggBasket() {
        map = new ConcurrentSkipListMap<G, List<V>>();
    }

    /**
     * Конструктор
     *
     * @param c компаратор
     */
    public EggBasket(Comparator<? super G> c) {
        map = new ConcurrentSkipListMap<G, List<V>>(c);
    }

    /**
     * Помещает список в коллекцию
     *
     * @param key   ключ
     * @param value значение
     * @return возвращает новый список
     * @deprecated патамучта
     */
    @Override
    public List<V> put(G key, List<V> value) {
        return map.put(key, value);
    }

    /**
     * Помещает щначение в коллекцию
     *
     * @param key   ключ
     * @param value значение
     */
    public void putItem(G key, V value) {
        if (containsKey(key)) {
            if (map.get(key).contains(value)) {
                map.get(key).remove(value);
                map.get(key).add(value);
            } else {
                map.get(key).add(value);
            }
        } else {
            List<V> arr = new ArrayList<V>();
            arr.add(value);
            put(key, arr);
        }
    }

    @Override
    public List<V> get(Object key) {
        final List<V> innerList = map.get(key);
        return innerList != null ? new ArrayList<V>(innerList) : innerList;
    }

    /**
     * returns elements in set
     * @param key
     * @return set if exists and empty set otherwise
     */
    public Set<V> getSet(G key){
        final List<V> innerList = map.get(key);
        if (innerList != null) {
            return new HashSet<V>(innerList);
        } else {
            return Collections.emptySet();
        }
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public List<V> remove(Object key) {
        return map.remove(key);
    }

    @Override
    public void putAll(Map<? extends G, ? extends List<V>> m) {
        map.putAll(m);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set<G> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<List<V>> values() {
        return map.values();
    }

    @Override
    public Set<Entry<G, List<V>>> entrySet() {
        return map.entrySet();
    }
}
