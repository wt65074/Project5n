// Will Tobey
// wtobey1@jhu.edu
import java.util.Iterator;
import java.util.ArrayList;

/**
 * Hash Table implementation that performs basic operations
 * in O(1) by way of a Cukoo hash table.
 *
 * @param <K> The key type.
 * @param <V> The value type.
*/
public class HashMapSplit<K, V> implements Map<K, V> {

    private static final double MAX_LOAD = 0.5;
    private static final int INITIAL_SIZE = 16;
    private static final int MAX_DISPLACEMENTS = 50;

    // Entry pairs up a key and a value.
    private static class Entry<K, V> {
        K key;
        V value;

        Entry(K k, V v) {
            this.key = k;
            this.value = v;
        }

        @Override
        public String toString() {
            return this.key + ": " + this.value;
        } 

    }

    /// Hash function 1 to go into the first table.
    private HashFunction hfOne;

    /// Hash function 2 for the second table.
    private HashFunction hfTwo;

    // Tables to hold data
    private Entry<K, V>[] tableOne;
    private Entry<K, V>[] tableTwo;
    //private ArrayList<Entry<K, V>> tableOne;
    //private ArrayList<Entry<K, V>> tableTwo;

    // Temporarily holds a value that could not be placed in attempted rehash.
    private Entry<K, V> unhashed;

    // Counts the number of elements in each table
    private int tableOneCount = 0;
    private int tableTwoCount = 0;

    /// Tracks the number of elements.
    private int count = 0;

    public HashMap() {
        
        tableOne = (Entry<K, V>[]) new Entry[100];

    }

    // PRIVATE FUNCTIONS --

    /**
     * Returns the current size of the tables.
     *
     * @return The current size of the tables.
    */
    private int tableSize() {

        return this.tableOne.length;

    }

    /**
     * Computes the load factor for the data structure.
     *
     * @return The load function (number of elements / the size of the table)
    */
    private int loadFactor() {

        return this.count / this.tableSize();

    }

    /**
     * Creates two new prime hash functions.
     *
     * @param size The size of the table.
    */
    private void newPrimeHashFunctions(int size) {

        hfOne = UniversalHashes.prime(size);
        hfTwo = UniversalHashes.prime(size);

    }

    /**
     * Creates two new power hash functions.
     *
     * @param size The size of the table.
    */
    private void newPowerHashFunctions(int size) {

        hfOne = UniversalHashes.power(size);
        hfTwo = UniversalHashes.power(size);

    }

    /**
     * Rehashes the table.
     *
     * @param size The size of the new arrays for the rehash.
    */
    private void rehash(int size) {

        System.out.println("Rehash");

        // Generate new hash functions
        this.newPrimeHashFunctions(size);

        // Create a temporary pair of arrays.
        Entry[] tOne = this.tableOne;
        Entry[] tTwo = this.tableTwo;

        this.tableOne = Entry[size];
        this.tableTwo = Entry[size];

        // Reset the size of each table.
        this.tableOneCount = 0;
        this.tableTwoCount = 0;

        for (Entry e: tOne) {

            if (!this.place(this.tableOne, this.hfOne, e, 0)) {

                // Reset the tables, rehash again.
                this.tableOne = tOne;
                this.tableTwo = tTwo;
                this.rehash(size);
                return;


            }

        }

        for (Entry e: tTwo) {

            if (!this.place(this.tableOne, this.hfOne, e, 0)) {

                // Reset the tables, rehash again.
                this.tableOne = tOne;
                this.tableTwo = tTwo;
                this.rehash(size);
                return;

            }

        }

    }

    /// Returns a hash value for a entry.
    private int hashEntry(Entry e, HashFunction f) {

        return this.hashKey(e.key, f);

    }

    /// Returns a hash value for a key.
    private int hashKey(K key, HashFunction f) {

        int javaHash = key.hashCode();
        return f.hash(javaHash);

    }

    /**
     * Places an element into one of two tables.
     *
     * @param table The table to place the element in.
     * @param func The hash function.
     * @param e The entry to place.
     * @param c The number of times the funtion has been called.
     *
     * @return Null if the placement succeeds, the entry that needs to be placed if it fails and we need to rehash.
    */
    private boolean place(Entry[] table, HashFunction func, Entry e, int c) {

        if (e == null) { return false; }

        if (c >= MAX_DISPLACEMENTS) {

            // We need to rehash
            // Return the element that still needs to be placed
            return false;

        }

        int universalHash = this.hashEntry(e, func);

        Entry displaced = table[universalHash];
        table[universalHash] = e;


        // We have a displaced an element, so now it needs to be placed in the other table.
        if (displaced != null) {

            if (table == this.tableOne) {

                // Place the displaced element in the other table.
                return this.place(this.tableTwo, this.hfTwo, displaced, ++c);

            } else {

                // Place the displaced element in the other table.
                return this.place(this.tableOne, this.hfOne, displaced, ++c);

            }

        }

        if (table == this.tableOne) { this.tableOneCount++; }
        else { this.tableOneCount++; }

        return true;

    }

    /// Inserts a new element.
    private void insert(Entry e) {

        // Keep trying to place the element until a place succeeds. 
        while (!place(this.tableOne, this.hfOne, e, 0)) {
            this.rehash(this.tableSize());
        }

        this.count++;

        // If the load factor is too high, rehash/expand the table.
        if (this.loadFactor() > MAX_LOAD) {
            this.rehash(this.tableSize() * 2);
        }

    }


    /**
     * Find a key in the hashtable.
     *
     * @return The entry if found, or null if not.
    */
    private Entry find(K k) {

        if (k == null) {
            return null;
        }

        int hashOne = this.hashKey(k, this.hfOne);
        int hashTwo = this.hashKey(k, this.hfTwo);

        if (this.tableOne[hashOne].key == k) {

            // Return the value from the first table.
            return this.tableOne[hashOne];

        } else if (this.tableTwo[hashTwo].key == k) {

            // Return the value from the second table.
            return this.tableTwo[hashTwo];

        } else {

            // Key not found, return null.
            return null;

        }

    }

    // Returns the removed value, or returns null if not found.
    private V removeKey(K k) {

        if (k == null) {
            return null;
        }

        int hashOne = this.hashKey(k, this.hfOne);
        int hashTwo = this.hashKey(k, this.hfTwo);

        if (this.tableOne[hashOne].key == k) {

            Entry e = this.tableOne[hashOne];
            this.tableOne[hashOne] = null;
            this.tableOneCount--;
            return e.value;

        } else if (this.tableTwo[hashTwo].key == k) {

            Entry e = this.tableTwo[hashTwo];
            this.tableTwo[hashTwo] = null;
            this.tableTwoCount--;
            return e.value;

        } else {

            // Key not found, return null.
            return null;

        }

    } 

    // MAP INTERFACE IMPLEMENTATION --

    @Override
    public void insert(K k, V v) throws IllegalArgumentException {

        if (this.find(k) != null) {
            throw new IllegalArgumentException();
        }

        Entry newEntry = new Entry(k, v);

        this.insert(newEntry);

    }

    @Override
    public V remove(K k) throws IllegalArgumentException {

        V value = removeKey(k);

        if (value == null) {
            throw new IllegalArgumentException();
        }

        return value;

    }

    @Override
    public void put(K k, V v) throws IllegalArgumentException {

        Entry e = this.find(k);

        if (e == null) {
            throw new IllegalArgumentException();
        }

        e.value = v;

    }

    @Override
    public V get(K k) throws IllegalArgumentException {

        Entry e = this.find(k);

        if (e == null) {
            throw new IllegalArgumentException();
        }

        return e.value;

    }

    @Override
    public boolean has(K k) {

        return this.find(k) != null;

    }

    @Override
    public int size() {

        return this.count;

    }

    @Override
    public String toString() {

        StringBuilder s = new StringBuilder();
        s.append("{");

        for (Entry e: this.tableOne) {
            s.append(e.toString());
            s.append(",");
        }

        for (Entry e: this.tableTwo) {
            s.append(e.toString());
            s.append(",");
        }

        s.append("}");
        return s.toString();

    }

    @Override
    public Iterator<K> iterator() {

        return (new ArrayList<K>()).iterator();

    }

    public static void main(String[] args) {
        
        HashMap<String, Integer> map = new HashMap<String, Integer>();

        map.insert("Hi", 1);

        System.out.println(map);

    }

}