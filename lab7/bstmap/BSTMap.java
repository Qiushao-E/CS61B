package bstmap;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {
    private int size;
    private BSTNode root;

    /**
     * The nested class in the BSTMap. It includes a key-value pair and two pointers towards its children.
     */
    private class BSTNode {
        K key;
        V value;
        BSTNode left, right;

        BSTNode(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    /**
     * Create a new BSTMap with nothing.
     */
    public BSTMap() {
        this.size = 0;
    }

    /**
     * Removes all the mappings from this map.
     */
    @Override
    public void clear() {
        size = 0;
        this.root = null;
    }

    /**
     * Returns true if this map contains a mapping for the specified key.
     */
    @Override
    public boolean containsKey(K key) {
        return containsKeyHelper(key, root);
    }

    private boolean containsKeyHelper(K key, BSTNode T) {
        if (T == null) {
            return false;
        } else {
            int flag = key.compareTo(T.key);
            if (flag == 0) {
                return true;
            } else if (flag > 0) {
                return containsKeyHelper(key, T.right);
            } else {
                return containsKeyHelper(key, T.left);
            }
        }
    }

    /**
     * Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     */
    @Override
    public V get(K key) {
        return getHelper(key, root);
    }

    private V getHelper(K key, BSTNode T) {
        if (T == null) {
            return null;
        } else {
            int flag = key.compareTo(T.key);
            if (flag == 0) {
                return T.value;
            } else if (flag > 0) {
                return getHelper(key, T.right);
            } else {
                return getHelper(key, T.left);
            }
        }
    }

    /**
     * Returns the number of key-value mappings in this map.
     */
    @Override
    public int size() {
        return this.size;
    }

    /**
     * Associates the specified value with the specified key in this map.
     */
    @Override
    public void put(K key, V value) {
        root = putHelper(key, value, root);
    }

    private BSTNode putHelper(K key, V value, BSTNode T) {
        if (T == null) {
            T = new BSTNode(key, value);
            size += 1;
        } else {
            int flag = key.compareTo(T.key);
            if (flag == 0) {
                T.value = value;
            } else if (flag > 0) {
                T.right = putHelper(key, value, T.right);
            } else {
                T.left = putHelper(key, value, T.left);
            }
        }
        return T;
    }
    /**
     * Returns a Set view of the keys contained in this map. Not required for Lab 7.
     * If you don't implement this, throw an UnsupportedOperationException.
     */
    @Override
    public Set<K> keySet() {
        Set<K> s = new HashSet<>();
        return keySetHelper(s, root);
    }

    private Set<K> keySetHelper(Set<K> s, BSTNode T) {
        if (T == null || T.key == null){
            return s;
        } else {
            s = keySetHelper(s, T.left);
            s.add(T.key);
            s = keySetHelper(s, T.right);
        }
        return s;
    }

    /**
     * Removes the mapping for the specified key from this map if present.
     * Not required for Lab 7. If you don't implement this, throw an
     * UnsupportedOperationException.
     */
    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    /**
     * Removes the entry for the specified key only if it is currently mapped to
     * the specified value. Not required for Lab 7. If you don't implement this,
     * throw an UnsupportedOperationException.
     */
    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<K> iterator() {
        return this.keySet().iterator();
    }

    public void printInOrder() {
        this.printHelper(root);
    }

    private void printHelper(BSTNode T) {
        if (T == null || T.key == null) {
            return;
        } else {
            printHelper(T.left);
            System.out.print(T.key.toString() + " ");
            printHelper(T.right);
        }
    }
}
