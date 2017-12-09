// Will Tobey
// wtobey1@jhu.edu
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
// This splits into 2, uses asymetric

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
public class HashMap108<K, V> implements Map<K, V> {

    private static final double MAX_LOAD_FACTOR = 0.5;
    private static final int INITIAL_SIZE_ONE = 16;
    private static final int INITIAL_SIZE_TWO = 8;
    private static final int HASH_FUNCTIONS = 2;

    private static final int HASH_ONE = 0;
    private static final int HASH_TWO = 1;

    // Counts the number of elements.
    private int count;

    // Stores how many displacements are okay before a rehash
    private int maxDisplacements;

    // Hash Functions
    private HashFunction[] hashFunctions;

    // Underlying array
    private Entry<K, V>[] table1;
    private Entry<K, V>[] table2;

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

    /**
     * Instantiates a new hash map.
    */
    public HashMap108() {

        this.table1 = (Entry<K, V>[]) new Entry[INITIAL_SIZE_ONE];
        this.table2 = (Entry<K, V>[]) new Entry[INITIAL_SIZE_TWO];

        this.hashFunctions = new HashFunction[HASH_FUNCTIONS];

        this.newHashFunctions(INITIAL_SIZE_ONE);

        this.updateMaxDisplacements(INITIAL_SIZE_ONE);

    }

    private void newHashFunctions(int size) {
        this.hashFunctions[0] = this.newPowerHashFunction(size);
        this.hashFunctions[1] = this.newPowerHashFunction(size / 2);
    }

    // Computes the load factor.
    private double loadFactor() {
        return (double) this.count / (this.table1.length + this.table2.length);
    }

    // Hashes an entry.
    private int hash(Entry<K, V> e, int f) {
        return this.hash(e.key, f);
    }

    // Hashes a key.
    private int hash(K key, int f) {
        return this.hashFunctions[f].hash(key.hashCode());
    }

    // Returns a new prime hash function.
    private HashFunction newPrimeHashFunction(int size) {
        return UniversalHashes.prime(size);
    }

    // Returns a new power hash function.
    private HashFunction newPowerHashFunction(int size) {
        return UniversalHashes.power(size);
    }

    // Updates the maximum number of displacements.
    private void updateMaxDisplacements(int size) {
        // Compute log2(size)
        this.maxDisplacements = (int) (Math.log(size * 1.5) / Math.log(2));
    }

    // Rehashes into a new underlying table of size size.
    private void rehash(int size) {
        // Create new table
        Entry<K, V>[] temp1 = this.table1;
        Entry<K, V>[] temp2 = this.table2;

        // Create new hashing functions
        this.newHashFunctions(size);

        // Create a new empty table.
        this.table1 = (Entry<K, V>[]) new Entry[size];
        this.table2 = (Entry<K, V>[]) new Entry[size];

        // Update the maximum displacements based on the new size.
        this.updateMaxDisplacements(size);

        // Place all entries in the table.
        for (Entry<K, V> e: temp1) {
            if (this.place(e) != null) {
                // The placement has failed, reset table and try to hash again.
                this.table1 = temp1;
                this.table2 = temp2;
                this.rehash(size);
                return;
            }
        }

        for (Entry<K, V> e: temp2) {
            if (this.place(e) != null) {
                // The placement has failed, reset table and try to hash again.
                this.table1 = temp1;
                this.table2 = temp2;
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
        Entry<K, V> foundH1 = this.table1[hash1];

        if (foundH1 != null && k.equals(foundH1.key)) {
            this.table1[hash1] = null;
            this.count--;
            return foundH1.value;
        }

        // Check the second hash position.
        int hash2 = this.hash(k, HASH_TWO);
        Entry<K, V> foundH2 = this.table2[hash2];

        if (foundH2 != null && k.equals(foundH2.key)) {
            this.table2[hash2] = null;
            this.count--;
            return foundH2.value;
        }

        return null;

    }

    // Attempts to place an entry in the table. Returns
    // the entry that couldn't be placed if too many
    // displacements are attempted.
    private Entry<K, V> place(Entry<K, V> e) {

        if (e == null) {
            return null;
        }

        int func = HASH_ONE;
        Entry<K, V> current = e;
        int displacements = 0;

        while (displacements++ < this.maxDisplacements) {

            int position = this.hash(current, func);

            Entry<K, V>[] table = (func==HASH_ONE ? this.table1 : this.table2);

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
        Entry<K, V> foundH1 = this.table1[hash1];

        // Check first hash value.
        if (foundH1 != null && k.equals(foundH1.key)) {
            return foundH1;
        }

        int hash2 = this.hash(k, HASH_TWO);
        Entry<K, V> foundH2 = this.table2[hash2];

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

    // Inserts an entry into the table.
    private void insert(Entry<K, V> e) {

        // Rehash the table if the load factor is too high.
        if (this.loadFactor() >= MAX_LOAD_FACTOR) {
            this.rehash(this.table1.length * 2);
        }

        Entry<K, V> displaced = this.place(e);

        while (displaced != null) {
            // The insertion has failed, and displaced still needs
            // to be inserted into the table. We rehash and double
            // the length, because it is likely that if we are
            // seeing duplicate insertions, we are likely to see more
            // and we are likely close to the max load factor.
            // We should expand the array now b ecause it is likely
            // we will need to soon.
            this.rehash(this.table1.length * 2);
            displaced = this.place(displaced);
        }

        this.count++;

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
        for (int i = 0; i < this.table1.length; i++) {
            Entry<K, V> e = this.table1[i];
            if (e == null) {
                continue;
            }
            s.append("" + e.key + ": " + e.value);
        }
        for (int i = 0; i < this.table2.length; i++) {
            Entry<K, V> e = this.table2[i];
            if (e == null) {
                continue;
            }
            s.append("" + e.key + ": " + e.value);
            if (i < this.table2.length - 1) {
                s.append(", ");
            }
        }
        s.append("}");
        return s.toString();

    }

    @Override
    public Iterator<K> iterator() {

        List<K> keys = new ArrayList<K>();
        for (Entry<K, V> e: this.table1) {
            if (e != null) {
                keys.add(e.key);
            }
        }
        for (Entry<K, V> e: this.table2) {
            if (e != null) {
                keys.add(e.key);
            }
        }

        return keys.iterator();

    }

}