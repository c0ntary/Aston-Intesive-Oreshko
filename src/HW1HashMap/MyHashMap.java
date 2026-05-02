package HW1HashMap;

public class MyHashMap<K, V> {

    private static class Entry<K, V> {
        K key;
        V value;
        Entry<K, V> next;

        Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    private Entry<K, V>[] table;
    private int capacity = 16;
    private int size = 0;

    @SuppressWarnings("unchecked")
    public MyHashMap() {
        table = new Entry[capacity];
    }

    private int hash(K key) {
        return (key == null) ? 0 : Math.abs(key.hashCode() % capacity);
    }

    public void put(K key, V value) {
        int index = hash(key);
        Entry<K, V> head = table[index];
        Entry<K, V> current = head;
        while (current != null) {
            if ((key == null && current.key == null) ||
                    (key != null && key.equals(current.key))) {
                current.value = value;
                return;
            }
            current = current.next;
        }
        Entry<K, V> newEntry = new Entry<>(key, value);
        newEntry.next = head;
        table[index] = newEntry;
        size++;
    }

    public V get(K key) {
        int index = hash(key);
        Entry<K, V> current = table[index];

        while (current != null) {
            if ((key == null && current.key == null) ||
                    (key != null && key.equals(current.key))) {
                return current.value;
            }
            current = current.next;
        }
        return null;
    }

    public V remove(K key) {
        int index = hash(key);
        Entry<K, V> current = table[index];
        Entry<K, V> prev = null;

        while (current != null) {
            if ((key == null && current.key == null) ||
                    (key != null && key.equals(current.key))) {
                if (prev == null) {
                    table[index] = current.next;
                } else {
                    prev.next = current.next;
                }
                size--;
                return current.value;
            }
            prev = current;
            current = current.next;
        }
        return null;
    }
}