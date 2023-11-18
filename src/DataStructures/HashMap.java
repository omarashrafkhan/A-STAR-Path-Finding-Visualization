package DataStructures;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class HashMap<K, V> {

    private static final int DEFAULT_CAPACITY = 16;
    private static final double LOAD_FACTOR = 0.75;

    private List<LinkedList<Entry<K, V>>> buckets;
    private int size;

    public HashMap() {
        this(DEFAULT_CAPACITY);
    }

    public HashMap(int initialCapacity) {
        this.buckets = new ArrayList<>(initialCapacity);
        for (int i = 0; i < initialCapacity; i++) {
            buckets.add(new LinkedList<>());
        }
        this.size = 0;
    }

    public void put(K key, V value) {
        int index = getBucketIndex(key);
        LinkedList<Entry<K, V>> bucket = buckets.get(index);

        // Check if the key already exists in the bucket and update the value if it does
        for (Entry<K, V> entry : bucket) {
            if (entry.getKey().equals(key)) {
                entry.setValue(value);
                return;
            }
        }

        // Add a new entry to the bucket
        bucket.add(new Entry<>(key, value));
        size++;

        // Check if resizing is needed
        if ((double) size / buckets.size() > LOAD_FACTOR) {
            resize();
        }
    }

    public V get(K key) {
        int index = getBucketIndex(key);
        LinkedList<Entry<K, V>> bucket = buckets.get(index);

        // Search for the key in the bucket
        for (Entry<K, V> entry : bucket) {
            if (entry.getKey().equals(key)) {
                return entry.getValue();
            }
        }

        // Key not found
        return null;
    }

    public boolean containsKey(K key) {
        int index = getBucketIndex(key);
        LinkedList<Entry<K, V>> bucket = buckets.get(index);

        // Check if the key exists in the bucket
        for (Entry<K, V> entry : bucket) {
            if (entry.getKey().equals(key)) {
                return true;
            }
        }

        // Key not found
        return false;
    }

    public void remove(K key) {
        int index = getBucketIndex(key);
        LinkedList<Entry<K, V>> bucket = buckets.get(index);

        // Remove the entry with the specified key from the bucket
        bucket.removeIf(entry -> entry.getKey().equals(key));
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    private int getBucketIndex(K key) {
        return key.hashCode() % buckets.size();
    }

    private void resize() {
        // Double the number of buckets and rehash all entries
        List<LinkedList<Entry<K, V>>> newBuckets = new ArrayList<>(buckets.size() * 2);
        for (int i = 0; i < buckets.size() * 2; i++) {
            newBuckets.add(new LinkedList<>());
        }

        for (LinkedList<Entry<K, V>> bucket : buckets) {
            for (Entry<K, V> entry : bucket) {
                int newIndex = entry.getKey().hashCode() % newBuckets.size();
                newBuckets.get(newIndex).add(entry);
            }
        }

        buckets = newBuckets;
    }

    private static class Entry<K, V> {
        private K key;
        private V value;

        public Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        public void setValue(V value) {
            this.value = value;
        }
    }


}

