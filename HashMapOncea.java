//James Oncea
//joncea1@jhu.edu

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

/** HashMap implementation of map.
 *  @param <K> keys type.
 *  @param <V> value type.
 */

public class HashMap<K, V> implements Map<K, V> {

    private static final int ARRAY_SIZE = 16;
    private int size;
    private int arraySize;
    private Node<K, V>[] nodes;

    private static class Node<K, V> {
        K key;
        V value;


        Node(K k, V v) {
            this.key = k;
            this.value = v;
        }

        public String toString() {
            return "Node<key: " + this.key
                + "; value: " + this.value
                + ">";
        }
    }

    /**
     * Constructor.
     */
    public HashMap() {
        this.arraySize = ARRAY_SIZE;
        this.size = 0;
        nodes =  (Node<K, V>[]) new Node[ARRAY_SIZE];
    }


    @Override
    public void insert(K k, V v) throws IllegalArgumentException {
        if (k == null) {
            throw new IllegalArgumentException("first argument to"
               + " insert is null");
        }

        if (this.find(k) > 0) {
            throw new IllegalArgumentException("first argument to"
               + " insert is already mapped");
        }


        // double table size if 50% full
        if (this.size >= this.arraySize / 2 - 1) {
            this.resize(2);
        }

        int i = this.hash(k);
        while (this.nodes[i] != null) {
            if (this.nodes[i].key == null) {
                break;
            }

            i = (i + 1) % this.arraySize;
        }

        this.nodes[i] = new Node<K, V>(k, v);
        this.size++;
    }

    @Override
    public V remove(K k) throws IllegalArgumentException {
        if (k == null) {
            throw new IllegalArgumentException("argument to remove() is null");
        }
        int i = this.find(k);
        if (i < 0) {
            throw new IllegalArgumentException("Key is not mapped");
        }

        V value = this.nodes[i].value;
        this.nodes[i].key = null;

        this.size--;

        //resize(1);

        return value;


    }

    @Override
    public void put(K k, V v) throws IllegalArgumentException {
        if (k == null) {
            throw new IllegalArgumentException("argument to put is null");
        }
        int index = this.find(k);
        if (index < 0) {
            throw new IllegalArgumentException("argument to put "
                + "is not mapped");
        }
        this.nodes[index].value = v;
    }

    /** Finds the index location of a given value.
     *  @param k key to be found
     *  @return returns the index of the keys.
     */
    private int find(K k) {


        int i = this.hash(k);
        while (this.nodes[i] != null) {
            if (this.nodes[i].key != null) {
                if (this.nodes[i].key.equals(k)) {
                    return i;
                }
            }

            i = (i + 1) % this.arraySize;
        }


        return -1;


    }

    @Override
    public V get(K k) throws IllegalArgumentException {
        if (k == null) {
            throw new IllegalArgumentException("argument to get() is null");
        }

        int i = this.find(k);

        if (this.find(k) < 0) {
            throw new IllegalArgumentException("first argument to"
               + " get is not mapped");
        }

        return this.nodes[i].value;
    }

    @Override
    public boolean has(K k) {
        if (k == null) {
            return false;
        }
        return this.find(k) > 0;
    }

    @Override
    public int size() {
        return this.size;
    }

    private int hash(K key) {
        return (key.hashCode() & 0x7fffffff) % this.arraySize;
    }

    // resizes the hash table to the given
    //capacity by re-hashing all of the keys

    private void resize(int multi) {

        int newSize = (this.arraySize * multi);
        Node<K, V>[] temp = (Node<K, V>[]) new Node[newSize];
        int currentSize = this.arraySize;
        this.arraySize = newSize;
        for (int i = 0; i < currentSize; i++) {
            if (this.nodes[i] != null) {
                int a;
                for (a = this.hash(this.nodes[i].key); temp[a] != null;
                    a = (a + 1) % this.arraySize) {
                    a = a;
                }
                temp[a] = new Node<K, V>(this.nodes[i].key,
                    this.nodes[i].value);

            }
        }
        this.nodes = temp;

    }


    @Override
    public Iterator<K> iterator() {
        List<K> list = new ArrayList<K>();
        for (int i = 0; i < this.arraySize; i++) {
            if (this.nodes[i] != null) {
                if (this.nodes[i].key != null) {
                    list.add(this.nodes[i].key);
                }
            }
        }
        return list.iterator();
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("{");
        for (Iterator<K> i = iterator(); i.hasNext();) {
            K key = i.next();
            s.append("" + key + ": " + this.get(key));
            if (i.hasNext()) {
                s.append(", ");
            }
        }
        s.append("}");
        return s.toString();
    }

}