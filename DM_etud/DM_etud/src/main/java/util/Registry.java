package util;

import java.util.HashMap;
import java.util.Map;

/**
 * Implements an identity map.
 * It keeps a record of all objects that have been read from the database in a single business
 * transaction.
 * Whenever you want an object, you check the Identity Map first to see if you already have it.
 * https://en.wikipedia.org/wiki/Identity_map_pattern
 * @param <K>
 * @param <V>
 */
public class Registry<K, V> {
    public Map<K, V> objectMap = new HashMap<>();

    public V getObject(K objKey) {
        return this.objectMap.get(objKey);
    }

    public void addObject(K key, V obj) {
        this.objectMap.put(key, obj);
    }

    public void removeObject(K key) { this.objectMap.remove(key); }

    public void removeAll() {
        this.objectMap.clear();
    }
}
