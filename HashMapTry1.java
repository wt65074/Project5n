// Will Tobey
// wtobey1@jhu.edu
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Random;

/**
 * Hash Table implementation that performs basic operations
 * in O(1) by way of a Cukoo hash table.
 *
 * @param <K> The key type.
 * @param <V> The value type.
*/
public class HashMap<K, V> implements Map<K, V> {

    private static final double MAX_LOAD = 0.5;
    private static final int INITIAL_SIZE = 16;
    private static final int MAX_DISPLACEMENTS = 50;
    private static int rehashes = 0;

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
    private Entry<K, V>[] table;
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

        table = (Entry<K, V>[]) new Entry[INITIAL_SIZE];

        this.newPrimeHashFunctions(INITIAL_SIZE);

    }

    // PRIVATE FUNCTIONS --

    /**
     * Returns the current size of the tables.
     *
     * @return The current size of the tables.
    */
    private int tableSize() {

        return this.table.length;

    }

    /**
     * Computes the load factor for the data structure.
     *
     * @return The load function (number of elements / the size of the table)
    */
    private double loadFactor() {
        //System.out.println("Load Factor = " + this.count + "/" + this.tableSize() + "(" + ((double) this.count / this.tableSize()) + ")");
        return (double) this.count / this.tableSize();

    }

    /**
     * Creates two new prime hash functions.
     *
     * @param size The size of the table.
    */
    private void newPrimeHashFunctions(int size) {

        this.hfOne = UniversalHashes.prime(size);
        this.hfTwo = UniversalHashes.prime(size);


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

        System.out.println("Rehash (" + size + ") : " + this);

        int rehash = rehashes++;
        
        // Generate new hash functions
        this.newPrimeHashFunctions(size);

        // Create a temporary pair of arrays.
        Entry<K, V>[] temp = this.table;

        this.table = (Entry<K, V>[]) new Entry[size];

        // Reset the size of each table.
        this.tableOneCount = 0;
        this.tableTwoCount = 0;

        for (Entry<K, V> e: temp) {

            if (this.place(e) != null) {

                System.out.println("Failed to place " + e + " rehash: " + rehash);

                // Reset the tables, rehash again.
                this.table = temp;
                System.out.println("Secondary Rehash");
                this.rehash(size);
                return;


            }
        }

        System.out.println("Successfully Rehashed");

    }

    /// Returns a hash value for a entry.
    private int hashEntry(Entry<K, V> e, HashFunction f) {

        return this.hashKey(e.key, f);

    }

    /// Returns a hash value for a key.
    private int hashKey(K key, HashFunction f) {

        int javaHash = key.hashCode();
        int hash = f.hash(javaHash);
        if (hash < 0 || hash > this.tableSize()) {
            System.out.println("Weird hash: " + hash);
        }
        return hash;

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
    private Entry<K, V> place(Entry<K, V> e) {

        if (e == null) {
            return null;
        }

        int count = 0;

        Entry<K, V> current = e;

        int index = this.hashEntry(current, this.hfOne);
        while (count < MAX_DISPLACEMENTS) {

            Entry<K, V> temp = table[index];
            if (temp == null) {
                table[index] = current;
                return null;
            }

            table[index] = current;
            current = temp;

            if (index == this.hashEntry(current, this.hfOne)) {
                index = this.hashEntry(current, this.hfTwo);
            } else {
                index = this.hashEntry(current, this.hfOne);
            }

            ++count;
        }

        return current;

    }

    /// Inserts a new element.
    private void insert(Entry<K, V> e) {

        // Keep trying to place the element until a place succeeds.
        Entry<K, V> placed = place(e);
        while (placed != null) {
            System.out.println("\nInsertion Conflict for " + placed + ": { h1: " + this.hashEntry(placed, this.hfOne) + ", h2: " + this.hashEntry(placed, this.hfTwo) + " }");
            System.out.println(this);
            System.out.println();
            this.rehash(this.tableSize());
            placed = place(placed);
        }

        this.count++;

        // If the load factor is too high, rehash/expand the table.
        if (this.loadFactor() > MAX_LOAD) {
            System.out.println("Planned Rehash");
            this.rehash(this.tableSize() * 2);
        }

    }


    /**
     * Find a key in the hashtable.
     *
     * @return The entry if found, or null if not.
    */
    private Entry<K, V> find(K k) {

        if (k == null) {
            return null;
        }

        //System.out.println(this.hashKey(k, this.hfOne));
        //System.out.println(this.hashKey(k, this.hfTwo));

        int hashOne = this.hashKey(k, this.hfOne);
        int hashTwo = this.hashKey(k, this.hfTwo);

        if (this.table[hashOne] != null && this.table[hashOne].key.equals(k)) {

            // Return the value from the first table.
            return this.table[hashOne];

        } else if (this.table[hashTwo] != null && this.table[hashTwo].key.equals(k)) {

            // Return the value from the second table.
            return this.table[hashTwo];

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

        if (this.table[hashOne] != null && this.table[hashOne].key.equals(k)) {

            Entry<K, V> e = this.table[hashOne];
            this.table[hashOne] = null;
            this.tableOneCount--;
            return e.value;

        } else if (this.table[hashTwo] != null && this.table[hashTwo].key.equals(k)) {

            Entry<K, V> e = this.table[hashTwo];
            this.table[hashTwo] = null;
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

        Entry<K, V> newEntry = new Entry<K, V>(k, v);
        System.out.println("Inserting: " + k);

        this.insert(newEntry);

        System.out.println("Inserted: " + k);

        System.out.println(this);

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

        Entry<K, V> e = this.find(k);

        if (e == null) {
            throw new IllegalArgumentException();
        }

        e.value = v;

    }

    @Override
    public V get(K k) throws IllegalArgumentException {

        Entry<K, V> e = this.find(k);

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

    private String tableStrings() {
        StringBuilder s = new StringBuilder();
        for (Entry<K, V> e: this.table) {
            if (e == null) { 
                s.append(" * ");
                continue;
            }
            s.append(" " + e.toString() + " ");
        }

        return s.toString();

    }

    @Override
    public String toString() {

        StringBuilder s = new StringBuilder();
        s.append("{");
        for (int i = 0; i < this.table.length; i++) {
            Entry<K, V> e = this.table[i];
            if (e == null) { continue; }
            s.append("" + e.key + ": " + e.value);
            s.append(" (" + this.hashEntry(e, this.hfOne) + ", " + this.hashEntry(e, this.hfTwo) + ")"); // REMOVE
            if (i < this.table.length - 1) {
                s.append(", ");
            }
        }
        s.append("}");
        return s.toString();

    }

    @Override
    public Iterator<K> iterator() {

        return (new ArrayList<K>()).iterator();

    }

    public static void main(String[] args) {
        
        HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
        Random r = new Random();

        for (int i = 0; i < 20; i++) {

            try {

                map.insert(r.nextInt(1000), 0);

            } catch (IllegalArgumentException e) {
                
            }

        }

        /*
        for (int i = 0; i < 1000000; i++) {
            map.insert(i, i);
        }

        for (int i = 0; i < 1000000; i++) {

            try {
                map.get(i);
            } catch (IllegalArgumentException e) {
                System.out.println(i + " Not found");
            }
        }

        for (int i = 0; i < 1000000; i++) {

            try {
                map.remove(i);
            } catch (IllegalArgumentException e) {
                System.out.println(i + " Not found");
            }
        }
        */

    }

}