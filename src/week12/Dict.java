package week12;
// name: Kirsten Pleskot

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Dict<K, V> implements Map<K, V> {
    public static void main(String[] args) {
        Dict<String, Integer> dict = new Dict<>();
        dict.put("one", 1);
        dict.put("two", 2);
        dict.put("three", 3);
        System.out.println("Size: " + dict.size());
        System.out.println("Get 'two': " + dict.get("two"));
        dict.put("two", 22);
        System.out.println("Get 'two' after update: " + dict.get("two"));
        System.out.println("Contains key 'three': " + dict.containsKey("three"));
        System.out.println("Contains value 3: " + dict.containsValue(3));
    }

    final List<Pair<K, V>> entries;

    public Dict() {
        this.entries = new java.util.ArrayList<>();
    }

    @Override
    public V put(K key, V value) {
        for (int i = 0; i < entries.size(); i++) {
            Pair<K, V> entry = entries.get(i);
            if (entry.first().equals(key)) {
                V oldValue = entry.second();
                entries.set(i, new Pair<>(key, value));
                return oldValue;
            }
        }
        entries.add(new Pair<>(key, value));
        return null;
    }

    @Override
    public V remove(Object key) {
        for (int i = 0; i < entries.size(); i++) {
            Pair<K, V> entry = entries.get(i);
            if (entry.first().equals(key)) {
                V oldValue = entry.second();
                entries.remove(i);
                return oldValue;
            }
        }
        return null;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (Entry<? extends K, ? extends V> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        entries.clear();
    }

    @Override
    public Set<K> keySet() {
        return Set.copyOf(entries.stream().map(Pair::first).toList());
    }

    @Override
    public Collection<V> values() {
        return List.copyOf(entries.stream().map(Pair::second).toList());
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        throw new UnsupportedOperationException("I don't get this one");
    }

    @Override
    public V get(Object key) {
        for (Pair<K, V> entry : entries) {
            if (entry.first().equals(key)) {
                return entry.second();
            }
        }
        return null;
    }

    @Override
    public int size() {
        return entries.size();
    }

    @Override
    public boolean isEmpty() {
        return entries.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        for (Pair<K, V> entry : entries) {
            if (entry.first().equals(key)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        for (Pair<K, V> entry : entries) {
            if (entry.second().equals(value)) {
                return true;
            }
        }
        return false;
    }
}

// since Pair is supposed to be immutable, we can use a record here, and it's more idiomatic in new Java
record Pair<F, S>(F first, S second) {
    public Pair<S, F> swap() {
        return new Pair<>(second, first);
    }

    public <T> Pair<T, S> withFst(T newFirst) {
        return new Pair<>(newFirst, second);
    }

    public <T> Pair<F, T> withSnd(T newSecond) {
        return new Pair<>(first, newSecond);
    }
}

