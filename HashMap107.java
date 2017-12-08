// Will Tobey
// wtobey1@jhu.edu
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Random;
import java.util.List;

// Attemp to use asymetric tables and remove some other optimizations
// For example, tombstone thing is going to be less benificial when
// more things are placed in the first table.

/**
 * Hash Table implementation that performs basic operations
 * in O(1) by way of a Cukoo hash table.
 *
 * @param <K> The key type.
 * @param <V> The value type.
*/
public class HashMap107<K, V> implements Map<K, V> {

    static final double MAX_LOAD_FACTOR = 0.5;
    static final int INITIAL_SIZE = 24;
    static final int HASH_FUNCTIONS = 2;

    static final int HASH_ONE = 0;
    static final int HASH_TWO = 1;

    // Counts the number of elements.
    private int count = 0;

    // Stores how many displacements are okay before a rehash
    private int maxDisplacements = 0;

    // Hash Functions
    private HashFunction[] hashFunctions;

    // Underlying array
    private Entry<K, V>[] table;

    public int rehashes = 0;
    public int unplanned = 0;

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

    // Instantiates a new hashmap.
    public HashMap107() {

        table = (Entry<K, V>[]) new Entry[INITIAL_SIZE];
        hashFunctions = new HashFunction[HASH_FUNCTIONS];

        this.newHashFunctions(INITIAL_SIZE);

        this.updateMaxDisplacements(INITIAL_SIZE);

    }

    private void newHashFunctions(int size) {
        this.hashFunctions[0] = this.newPowerHashFunction(size / 3 * 2);
        this.hashFunctions[1] = this.newPowerHashFunction(size / 3);
    }

    // Computes the load factor.
    private double loadFactor() {
        return (double) this.count / (double) this.table.length;
    }

    // Hashes an entry.
    private int hash(Entry<K, V> e, int f) {
        return this.hash(e.key, f);
    }

    // Hashes a key.
    public int hash(K key, int f) {
        return f * (2 * table.length / 3) + this.hashFunctions[f].hash(key.hashCode());
    }

    // Returns a new prime hash function.
    public HashFunction newPrimeHashFunction(int size) {
        return UniversalHashes.prime(size);
    }

    // Returns a new power hash function.
    private HashFunction newPowerHashFunction(int size) {
        return UniversalHashes.power(size);
    }

    // Updates the maximum number of displacements.
    private void updateMaxDisplacements(int size) {
        maxDisplacements = (int) (Math.log(size) / Math.log(2)); // Compute log2(size)
    }

    // Inserts an entry into the table.
    private void insert(Entry<K, V> e) {

        // Rehash the table if the load factor is too high.
        if (this.loadFactor() >= MAX_LOAD_FACTOR) {
            this.rehash(this.table.length * 2);
        }

        Entry<K, V> displaced = place(e);
        
        while (displaced != null) {
            // The insertion has failed, and displaced still needs to be inserted
            // into the table. We rehash and double the length, because it is likely
            // that if we are seeing duplicate insertions, we are likely to see more,
            // and we are likely close to the max load factor. We should expand the array 
            // now because it is likely we will need to soon.
            rehash(this.table.length * 2);
            displaced = place(displaced);
        }

        this.count++;

    }

    // Rehashes into a new underlying table of size size.
    private void rehash(int size) {
        // Create new table
        Entry<K, V>[] temp = this.table;

        // Create new hashing functions
        this.newHashFunctions(size);

        // Create a new empty table.
        this.table = (Entry<K, V>[]) new Entry[size];

        // Update the maximum displacements based on the new size.
        this.updateMaxDisplacements(size);

        // Place all entries in the table.
        for (Entry<K, V> e: temp) {
            if (this.place(e) != null) {
                // The placement has failed, reset table and try to hash again.
                unplanned++;
                this.table = temp;
                this.rehash(size);
                return;
            }

        }

    }

    // Removes a key from the hash. Returns the value removed or null.
    private V removeKey(K k) {

        if (k == null) {
            return null;
        }

        // Check the first hash position.
        int hash1 = this.hash(k, HASH_ONE);
        Entry<K, V> foundH1 = table[hash1];

        if (foundH1 != null && k.equals(foundH1.key)) {
            table[hash1] = null;
            this.count--;
            return foundH1.value;
        }

        // Check the second hash position.
        int hash2 = this.hash(k, HASH_TWO);
        Entry<K, V> foundH2 = table[hash2];

        if (foundH2 != null && k.equals(foundH2.key)) {
            table[hash2] = null;
            this.count--;
            return foundH2.value;
        }

        return null;

    }

    // Attempts to place an entry in the table. Returns the entry that couldn't be placed
    // if too many displacements are attempted. 
    private Entry<K, V> place(Entry<K, V> e) {

        if (e == null) { return null; }

        int func = HASH_ONE;
        Entry<K, V> current = e;
        int displacements = 0;

        while (displacements++ < this.maxDisplacements) {

            int position = hash(current, func);

            Entry<K, V> temp = table[position];
            table[position] = current;

            if (temp == null) {
                // We has succeeded placing the entry.
                return null;
            }

            current = temp;

            // Choose the correct hash function to use.
            // If we displaced an element from its first hash position,
            // we need to move it to its second, and visa versa.
            func ^= 1;

        }

        // Failed the placement.
        // Will need to rehash and retry placing current.
        return current;

    }

    // Attempts to find and return an entry for a key.
    private Entry<K, V> find(K k) {

        if (k == null) {
            return null;
        }

        int hash1 = this.hash(k, HASH_ONE);
        Entry<K, V> foundH1 = table[hash1];

        // Check first hash value.
        if (foundH1 != null && k.equals(foundH1.key)) {
            return foundH1;
        }

        int hash2 = this.hash(k, HASH_TWO);
        Entry<K, V> foundH2 = table[hash2];

        // Check second hash value.
        if (foundH2 != null && k.equals(foundH2.key)) {
            return foundH2;
        }

        return null;

    }

    @Override
    public void insert(K k, V v) throws IllegalArgumentException {

        if (k == null || this.find(k) != null) {
            throw new IllegalArgumentException();
        }

        Entry<K, V> newEntry = new Entry<K, V>(k, v);
        this.insert(newEntry);

    }

    @Override
    public V remove(K k) throws IllegalArgumentException {

        V removed = this.removeKey(k);

        if (removed == null) {
            throw new IllegalArgumentException();
        }

        return removed;

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

        Entry<K, V> e = this.find(k);

        return e != null;

    }

    @Override
    public int size() {

        return this.count;

    }

    @Override
    public String toString() {

        StringBuilder s = new StringBuilder();
        s.append("{");
        for (int i = 0; i < this.table.length; i++) {
            Entry<K, V> e = this.table[i];
            if (e == null) { continue; }
            s.append("" + e.key + ": " + e.value);
            //s.append(" (" + this.hash(e, this.hashOne) + ", " + this.hash(e, this.hashTwo) + ")"); // REMOVE
            if (i < this.table.length - 1) {
                s.append(", ");
            }
        }
        s.append("}");
        return s.toString();

    }

    @Override
    public Iterator<K> iterator() {

        List<K> keys = new ArrayList<K>();
        for (Entry<K,V> e: this.table) {
            if (e != null) {
                keys.add(e.key);
            }
            
        }

        return keys.iterator();

    }

    public static void main(String[] args) {
        
        HashMapSub1<Integer, Integer> map = new HashMapSub1<Integer, Integer>();
        Random r = new Random();

        for (int i = 0; i < 100000; i++) {

            try {

                map.insert(r.nextInt(1000000), 0);
            } catch (IllegalArgumentException e) {

                
            }

        }

        for (int i = 0; i < 100000; i++) {
            map.has(r.nextInt(1000000));
        }

        
     }


}