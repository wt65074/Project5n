BalancedBSTBench.java                                                                               000644  000765  000024  00000021477 13211553043 015407  0                                                                                                    ustar 00wtobey                          staff                           000000  000000                                                                                                                                                                         // Will Tobey
// wtobey1@jhu.edu

import com.github.phf.jb.Bench;
import com.github.phf.jb.Bee;

import java.util.Random;
import java.util.Iterator;

/**
 * Bench marks for BinarySearchTreeMap.
 * Bench marks conducted with jaybee
 * For problem 1 of project 4.
*/
public final class BalancedBSTBench {

    private static final int RANDOM_SIZE = 500000; // Random number top rangeswhay
    private static final int LINEAR_SIZE = 5000; // Number of items in each map
    private static int count = 0;

    private static final Random r = new Random();

    /*
     * Runs LINEAR_SIZE times randomly choosing whether to remove an 
     * element in the map.
    */
    private static void removeMany(Map<Integer, Integer> map) {
        
        Iterator<Integer> iterator = map.iterator();

        int key = 0;

        while (iterator.hasNext()) {

            key = iterator.next();

            // Randomly decide with about 25% probablility whether to 
            // remove an object or not.
            if (r.nextInt(RANDOM_SIZE) % 4 == 0) {
                map.remove(key);
            }

        }

        /*
        Note: Here, when remove every object, the linear tests stalled. 
        I think it might be possible the test was running too fast since
        each removal removed the node at the top of the tree. To combat this
        I introduced randomized removals, which allow the test to run.
        */

    }

    /*
     * Runs the has method LINEAR_SIZE times.
    */
    private static void hasMany(Map<Integer, Integer> map) {


        Iterator<Integer> iterator = map.iterator();

        int key = 0;

        while (iterator.hasNext()) {

            key = iterator.next();

            // Randomly decide with about 25% probablility whether to 
            // remove an object or not.
            map.has(key);

        }

    }

    /*
     * Runs the put method LINEAR_SIZE times.
    */
    private static void putMany(Map<Integer, Integer> map) {

        Iterator<Integer> iterator = map.iterator();

        int key = 0;

        while (iterator.hasNext()) {

            key = iterator.next();

            // Randomly decide with about 25% probablility whether to 
            // remove an object or not.
            map.put(key, 1);

        }

    }

    /*
     * Runs the get method LINEAR_SIZE times.
    */
    private static void getMany(Map<Integer, Integer> map) {

        Iterator<Integer> iterator = map.iterator();
        int key = 0;

        while (iterator.hasNext()) {

            key = iterator.next();

            // Randomly decide with about 25% probablility whether to 
            // remove an object or not.
            map.get(key);

        }

    }

    /*
     *Inserts LINEAR_SIZE randomly keyed <integers, integer> into a map.
    */
    private static void insertManyRandom(Map<Integer, Integer> map) {

        for (int i = 0; i < LINEAR_SIZE; i++) {

            try {

                map.insert(r.nextInt(), 0);

            } catch (IllegalArgumentException e) {
                
            }

        }

    }

    /*
     *Inserts LINEAR_SIZE sequentially keyed <integers, integer> into a map.
    */
    private static void insertManyLinear(Map<Integer, Integer> map) {

        //System.out.println("Insert many");

        for (int i = 0; i < LINEAR_SIZE; i++) {

            try {

                map.insert(i, 0);

            } catch (IllegalArgumentException e) {
                
            }
        }
        //System.out.println("INserted many " + count++);

    }



    /*
     * Performs a random comination of insertions and removals
     * in an approximatly 1 to 1 ratio. Insertions are performed
     * linearly. Removals are performed on the last inserted object.
    */
    private static void linearInsertionsAndRemovals(Map<Integer, Integer> map) {

        // Tracks the last inserted value, so we know what to remove.
        int lastKey = -1;

        for (int i = 1; i < LINEAR_SIZE; i++) {

            if (r.nextInt(RANDOM_SIZE) % 2 == 0 && lastKey != -1) {
                map.remove(i - 1);
                lastKey = -1;
            }

            else {
                map.insert(i, 0);
                lastKey = i;
            }

        }

    }
    
    /*
     * Performs a random comination of insertions and removals
     * in an approximatly 1 to 1 ratio. Insertions are performed
     * ranomly. Removals are performed on the last inserted object.
    */
    private static void randomInsertionsAndRemovals(Map<Integer, Integer> map) {

        // Tracks the last inserted key, so we know what to remove.
        int lastKey = -1;

        for (int i = 1; i < LINEAR_SIZE; i++) {

            if (r.nextInt(RANDOM_SIZE) % 2 == 0 && lastKey != -1) {
                map.remove(lastKey);
                lastKey = -1;
            }

            else {
                int key = r.nextInt(RANDOM_SIZE);

                try {
                    map.insert(key, 0);
                } catch (IllegalArgumentException e) {
                    // Doesnt update the key if it wasnt inserted.
                    key = lastKey;
                }

                lastKey = key;
            }

        }

    }
    
    @Bench
    public static void randomInsert(Bee b) {
        for (int n = 0; n < b.reps(); n++) {
            b.stop();
            Map<Integer, Integer> map = map();
            b.start();
            insertManyRandom(map);
        }
    }


    @Bench
    public static void linearInsert(Bee b) {



        for (int n = 0; n < b.reps(); n++) {
            b.stop();
            Map<Integer, Integer> map = map();
            b.start();
            insertManyLinear(map);
        }
    }
    
    @Bench
    public static void linearRemove(Bee b) {

        //System.out.println("Linear Remove");

        for (int n = 0; n < b.reps(); n++) {
            b.stop();
            Map<Integer, Integer> map = map();
            insertManyLinear(map);
            b.start();
            removeMany(map);
        }
        
    }

    @Bench
    public static void randomRemove(Bee b) {

        //System.out.println("Random Remove");

        for (int n = 0; n < b.reps(); n++) {
            b.stop();
            Map<Integer, Integer> map = map();
            insertManyRandom(map);
            b.start();
            removeMany(map);
        }

    }

    @Bench
    public static void linearInsertionRemoval(Bee b ) {

        //System.out.println("Linear Insert Removal");

        for (int n = 0; n < b.reps(); n++) {
            b.stop();
            Map<Integer, Integer> map = map();
            b.start();
            linearInsertionsAndRemovals(map);
        }
    }

    @Bench
    public static void randomInsertionRemoval(Bee b) {

        //System.out.println("Random Insert Removal");

        for (int n = 0; n < b.reps(); n++) {
            b.stop();
            Map<Integer, Integer> map = map();
            b.start();
            randomInsertionsAndRemovals(map);
        }
    }
    @Bench
    public static void linearHas(Bee b) {

        //System.out.println("Linear Has");

        for (int n = 0; n < b.reps(); n++) {
            b.stop();
            Map<Integer, Integer> map = map();
            insertManyLinear(map);
            b.start();
            hasMany(map);
        }

    }

    @Bench
    public static void randomHas(Bee b) {

        //System.out.println("Random Has");

        for (int n = 0; n < b.reps(); n++) {
            b.stop();
            Map<Integer, Integer> map = map();
            insertManyRandom(map);
            b.start();
            hasMany(map);
        }
    }


    @Bench
    public static void linearGet(Bee b) {

        //System.out.println("Linear Get");

        for (int n = 0; n < b.reps(); n++) {
            b.stop();
            Map<Integer, Integer> map = map();
            insertManyLinear(map);
            b.start();
            getMany(map);
        }
    }

    @Bench
    public static void randomGet(Bee b) {

        //System.out.println("Random Get");

        for (int n = 0; n < b.reps(); n++) {
            b.stop();
            Map<Integer, Integer> map = map();
            insertManyRandom(map);
            b.start();
            getMany(map);
        }
    }

    @Bench
    public static void linearPut(Bee b) {

        //System.out.println("Linear Put");

        for (int n = 0; n < b.reps(); n++) {
            b.stop();
            Map<Integer, Integer> map = map();
            insertManyLinear(map);
            b.start();
            putMany(map);
        }
    }

    @Bench
    public static void randomPut(Bee b) {

        //System.out.println("Random Put");

        for (int n = 0; n < b.reps(); n++) {
            b.stop();
            Map<Integer, Integer> map = map();
            insertManyRandom(map);
            b.start();
            putMany(map);
        }
    }

    static private Map<Integer, Integer> map() {
        return new HashMap<Integer, Integer>();
    }


}
                                                                                                                                                                                                 HashFunction.java                                                                                   000644  000765  000024  00000000634 13210414720 014763  0                                                                                                    ustar 00wtobey                          staff                           000000  000000                                                                                                                                                                         /**
 * Hash functions from int to int.
 *
 * These are really "secondary" hash functions. We assume that you obtain
 * a (good!) initial hash using, for example, the hashCode() method. That
 * int is then further transformed by this hash function.
 */
public interface HashFunction {
    /**
     * Compute the hash.
     * @param i Integer to hash.
     * @return Hashed integer.
     */
    int hash(int i);
}
                                                                                                    HashMap.java                                                                                        000644  000765  000024  00000021326 13211552657 013731  0                                                                                                    ustar 00wtobey                          staff                           000000  000000                                                                                                                                                                         // Will Tobey
// wtobey1@jhu.edu
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Random;
import java.util.List;

/**
 * Hash Table implementation that performs basic operations
 * in O(1) by way of a Cukoo hash table.
 *
 * @param <K> The key type.
 * @param <V> The value type.
*/
public class HashMap<K, V> implements Map<K, V> {

    static final double MAX_LOAD_FACTOR = 0.5;
    static final int INITIAL_SIZE = 16;
    private int maxDisplacements = 0;

    private int count = 0;

    public int rehashes = 0;

    // Hash functions
    public HashFunction hashOne;
    public HashFunction hashTwo;

    // Table
    private Entry<K, V>[] table;


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

    public HashMap() {

        table = (Entry<K, V>[]) new Entry[INITIAL_SIZE];

        this.hashOne = this.newPowerHashFunction(INITIAL_SIZE);
        this.hashTwo = this.newPowerHashFunction(INITIAL_SIZE);

        this.updateMaxDisplacements(INITIAL_SIZE);

    }

    private double loadFactor() {
        //System.out.println("Calculating Load Factor: " + this.count + "/" + this.table.length);
        return (double) this.count / (double) this.table.length;
    }

    private int hash(Entry<K, V> e, HashFunction f) {
        return this.hash(e.key, f);
    }

    public int hash(K key, HashFunction f) {
        return f.hash(key.hashCode());
    }

    public HashFunction newPrimeHashFunction(int size) {
        return UniversalHashes.prime(size);
    }

    private HashFunction newPowerHashFunction(int size) {
        return UniversalHashes.power(size);
    }

    private void updateMaxDisplacements(int size) {

        int max = (int) (Math.log(size) / Math.log(2));

        // Source
        maxDisplacements = max > 2 ? max : 2;

    }

    private void insert(Entry<K, V> e) {

        if (this.loadFactor() >= MAX_LOAD_FACTOR) {

            //System.out.println("Executing Planned Rehash");

            this.rehash(this.table.length * 2);

        }

        Entry<K, V> current = place(e);
        
        while (current != null) {

            //System.out.println("Place Failed, displaced: " + current);

            // Im doing this because it is silly to rehash right before we are going to rehash again
            if (this.loadFactor() >= MAX_LOAD_FACTOR - 0.05) {
                rehash(this.table.length * 2);
            } else {
                rehash(this.table.length * 2);
            }
            

            current = place(current);

        }

        this.count++;

    }

    private void rehash(int size) {

        rehashes++;

        //System.out.println("Begining Rehash, Size: " + size + "---------------------------------");
        //System.out.println("Load Factor: " + this.loadFactor());

        // Create new table
        Entry<K, V>[] temp = this.table;

        /*
        There is no need to allocate new tables for the rehashing: We may simply run through the tables to delete and perform the usual insertion procedure on all keys found not to be at their intended position in the table.

        — Pagh & Rodler, "Cuckoo Hashing"[1]
        */

        // Create new hashing functions
        this.hashOne = this.newPowerHashFunction(size);
        this.hashTwo = this.newPowerHashFunction(size);

        this.table = (Entry<K, V>[]) new Entry[size];

        this.updateMaxDisplacements(size);

        // Empty table
        for (Entry<K, V> e: temp) {
            if (this.place(e) != null) {
                //System.out.println("Rehash Failed");
                this.table = temp;
                this.rehash(size);
                return;
            }

        }

        //System.out.println("Rehash Succeeded ---------------------------------");

    }

    private V removeKey(K k) {

        int hash1 = this.hash(k, this.hashOne);

        Entry<K, V> foundH1 = table[hash1];

        if (foundH1 != null && foundH1.key.equals(k)) {

            table[hash1] = null;

            return foundH1.value;

        }

        int hash2 = this.hash(k, this.hashTwo);

        Entry<K, V> foundH2 = table[hash2];

        if (foundH2 != null && foundH2.key.equals(k)) {

            table[hash2] = null;

            return foundH2.value;

        }

        return null;

    }

    private Entry<K, V> place(Entry<K, V> e) {

        if (e == null) { return null; }

        //System.out.println("Placing: " + e + " with hashes: { " + hash(e, hashOne) + ", " + hash(e, hashTwo) + "}");
        //System.out.println("Into: " + this);

        HashFunction func = this.hashOne;

        Entry<K, V> current = e;

        int displacements = 0;

        while (displacements++ < this.maxDisplacements) {

            int position = hash(current, func);

            //System.out.println("Placing at position: " + position + " Options: {" + hash(current, hashOne) + ", " + hash(current, hashTwo) + "}");

            Entry<K, V> temp = table[position];
            table[position] = current;

            if (temp == null) {
                return null;
            }

            //System.out.println("Displaced: " + temp.key);

            current = temp;

            //System.out.println("Displacements: " + displacements);
            if (hash(temp, this.hashOne) == position) {
                func = this.hashTwo;
            } else {
                func = this.hashOne;
            }

        }

        //System.out.println("Need Rehash. Could not place: " + e);

        return current;

    }

    private Entry<K, V> find(K k) {

        int hash1 = this.hash(k, this.hashOne);

        Entry<K, V> foundH1 = table[hash1];

        if (foundH1 != null && foundH1.key.equals(k)) {
            return foundH1;
        }

        int hash2 = this.hash(k, this.hashTwo);

        Entry<K, V> foundH2 = table[hash2];

        if (foundH2 != null && foundH2.key.equals(k)) {
            return foundH2;
        }

        return null;

    }

    @Override
    public void insert(K k, V v) throws IllegalArgumentException {

        if (this.find(k) != null) {
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
            s.append(" (" + this.hash(e, this.hashOne) + ", " + this.hash(e, this.hashTwo) + ")"); // REMOVE
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

        
        HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
        Random r = new Random();

        for (int i = 0; i < 200; i++) {

            try {

                map.insert(i, 0);
                System.out.println("Has: " + map.has(i));
            } catch (IllegalArgumentException e) {

                
            }

        }

        for (int i = 0; i < 200; i++) {

            try {
                map.remove(i);
                System.out.println("Has: " + map.has(i));
            } catch (IllegalArgumentException e) {

            }


        }

        
        
     }


}                                                                                                                                                                                                                                                                                                          HashMapSplit.java                                                                                   000644  000765  000024  00000022073 13210717776 014751  0                                                                                                    ustar 00wtobey                          staff                           000000  000000                                                                                                                                                                         // Will Tobey
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

}                                                                                                                                                                                                                                                                                                                                                                                                                                                                     HashMapTry1.java                                                                                    000644  000765  000024  00000025651 13211406002 014474  0                                                                                                    ustar 00wtobey                          staff                           000000  000000                                                                                                                                                                         // Will Tobey
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

}                                                                                       Map.java                                                                                            000644  000765  000024  00000003717 13206063527 013126  0                                                                                                    ustar 00wtobey                          staff                           000000  000000                                                                                                                                                                         /**
 * Maps from arbitrary keys to arbitrary values.
 *
 * Keys must be unique, not null, and should be immutable; values have
 * no constraints. We use Java's IllegalArgumentException below instead
 * of providing our own, specific exception classes; we do this mostly
 * to show you yet another style of interface design.
 *
 * Maps are a <b>very</b> popular abstraction. Some languages, Go and
 * Python for example, have them "built in" already. Maps are also
 * known as "dictionaries" or "associative arrays" in other contexts.
 *
 * @param <K> Type for keys.
 * @param <V> Type for values.
 */
public interface Map<K, V> extends Iterable<K> {
    /**
     * Insert a new key/value pair.
     *
     * @param k The key.
     * @param v The value to be associated with k.
     * @throws IllegalArgumentException If k is null or already mapped.
     */
    void insert(K k, V v) throws IllegalArgumentException;

    /**
     * Remove an existing key/value pair.
     *
     * @param k The key.
     * @return The value that was associated with k.
     * @throws IllegalArgumentException If k is null or not mapped.
     */
    V remove(K k) throws IllegalArgumentException;

    /**
     * Update the value associated with a key.
     *
     * @param k The key.
     * @param v The value to be associated with k.
     * @throws IllegalArgumentException If k is null or not mapped.
     */
    void put(K k, V v) throws IllegalArgumentException;

    /**
     * Get the value associated with a key.
     *
     * @param k The key.
     * @return The value associated with k.
     * @throws IllegalArgumentException If k is null or not mapped.
     */
    V get(K k) throws IllegalArgumentException;

    /**
     * Check existence of a key.
     *
     * @param k The key.
     * @return True if k is mapped, false otherwise (even for null!).
     */
    boolean has(K k);

    /**
     * Number of mappings.
     *
     * @return Number of key/value pairs in the map.
     */
    int size();
}
                                                 OrderedMap.java                                                                                     000644  000765  000024  00000000333 13204132171 014410  0                                                                                                    ustar 00wtobey                          staff                           000000  000000                                                                                                                                                                         /**
 * Ordered maps from comparable keys to arbitrary values.
 *
 * @param <K> Type for keys.
 * @param <V> Type for values.
 */
public interface OrderedMap<K extends Comparable<? super K>, V>
    extends Map<K, V> {
}
                                                                                                                                                                                                                                                                                                     SimpleMap.java                                                                                      000644  000765  000024  00000006225 13210414712 014264  0                                                                                                    ustar 00wtobey                          staff                           000000  000000                                                                                                                                                                         import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

/**
 * Maps from arbitrary keys to arbitrary values.
 *
 * This implementation uses an unsorted ArrayList internally so all
 * operations take O(n) time. The iterator() method makes a copy of
 * all keys, so it takes O(n) as well. The only positive thing here
 * is its simplicity.
 *
 * Note that we could do slightly better (in several ways!) by using
 * two arrays in parallel and managing our own insert/remove. You
 * should probably think about this because it makes for a great exam
 * question... :-)
 *
 * @param <K> Type for keys.
 * @param <V> Type for values.
 */
public class SimpleMap<K, V> implements Map<K, V> {
    // Entry pairs up a key and a value.
    private class Entry {
        K key;
        V value;

        Entry(K k, V v) {
            this.key = k;
            this.value = v;
        }
    }

    private List<Entry> data;

    /**
     * Create an empty map.
     */
    public SimpleMap() {
        this.data = new ArrayList<>();
    }

    // Find entry for key k, throw exception if k is null.
    private Entry find(K k) {
        if (k == null) {
            throw new IllegalArgumentException("cannot handle null key");
        }

        int size = this.data.size();
        for (int i = 0; i < size; i++) {
            Entry e = this.data.get(i);
            if (k.equals(e.key)) {
                return e;
            }
        }
        return null;
    }

    // Find entry for key k, throw exception if k not mapped.
    private Entry findForSure(K k) {
        Entry e = this.find(k);
        if (e == null) {
            throw new IllegalArgumentException("cannot find key " + k);
        }
        return e;
    }

    @Override
    public void insert(K k, V v) {
        Entry e = this.find(k);
        if (e != null) {
            throw new IllegalArgumentException("duplicate key " + k);
        }
        e = new Entry(k, v);
        this.data.add(e);
    }

    @Override
    public V remove(K k) {
        Entry e = this.findForSure(k);
        V v = e.value;
        this.data.remove(e);
        return v;
    }

    @Override
    public void put(K k, V v) {
        Entry e = this.findForSure(k);
        e.value = v;
    }

    @Override
    public V get(K k) {
        Entry e = this.findForSure(k);
        return e.value;
    }

    @Override
    public boolean has(K k) {
        if (k == null) {
            return false;
        }
        return this.find(k) != null;
    }

    @Override
    public int size() {
        return this.data.size();
    }

    @Override
    public Iterator<K> iterator() {
        List<K> keys = new ArrayList<K>();
        for (Entry e: this.data) {
            keys.add(e.key);
        }
        return keys.iterator();
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("{");
        for (int i = 0; i < this.data.size(); i++) {
            Entry e = this.data.get(i);
            s.append("" + e.key + ": " + e.value);
            if (i < this.data.size() - 1) {
                s.append(", ");
            }
        }
        s.append("}");
        return s.toString();
    }
}
                                                                                                                                                                                                                                                                                                                                                                           Spell.java                                                                                          000644  000765  000024  00000005255 13211553224 013461  0                                                                                                    ustar 00wtobey                          staff                           000000  000000                                                                                                                                                                         import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * A simple spell-checker.
 *
 * Load dictionary file given as command line argument, check text from
 * standard input, print misspelled words to standard output. Dictionary
 * files should have one lower-case word per line, nothing else. The
 * definiton of "word" in the input text is rather arbitrary and won't
 * make the linguists among you very happy.
 */
public final class Spell {
    private static Map<String, Integer> dictionary;
    private static Pattern pattern = Pattern.compile("[\\s[^a-zA-Z]]+");

    // Make checkstyle happy.
    private Spell() {}

    // Load dictionary file with given name.
    private static void load(String name) throws IOException {
        FileReader file = new FileReader(name);
        BufferedReader reader = new BufferedReader(file);

        dictionary = new HashMap<String, Integer>();

        String line;
        while ((line = reader.readLine()) != null) {
            String word = line.trim();
            dictionary.insert(word, 1); // we really want a set...
        }

        reader.close();
        file.close();
    }

    // Check standard input against dictionary, print misspelled
    // words to standard output.
    private static void check() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        String line;
        while ((line = reader.readLine()) != null) {
            String[] words = pattern.split(line, 0);
            for (String word: words) {
                if (word.length() == 0) {
                    continue;
                }
                if (!dictionary.has(word.toLowerCase())) {
                    System.out.println(word);
                }
            }
        }

        reader.close();
        input.close();
    }

    /**
     * Main method.
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.printf("Error: Need path to dictionary file!\n");
            System.exit(1);
        }
        if (args.length > 1) {
            System.err.printf("Warning: Extra command line arguments!\n");
        }

        try {
            load(args[0]);
        } catch (IOException e) {
            System.err.printf("Error: Cannot load dictionary %s!\n", args[0]);
            System.exit(1);
        }

        try {
            check();
        } catch (IOException e) {
            System.err.printf("Error: Cannot process input!\n");
            System.exit(1);
        }
    }
}
                                                                                                                                                                                                                                                                                                                                                   UniversalHashes.java                                                                                000644  000765  000024  00000011257 13211031627 015503  0                                                                                                    ustar 00wtobey                          staff                           000000  000000                                                                                                                                                                         import java.util.Random;
import java.math.BigInteger;

/**
 * Generate (or approximate in any case) universal hash functions. Thanks to
 * Kelvin Qian (Fall 2017) for a helpful question and follow-up bug report.
 */
public final class UniversalHashes {
    private static final Random RAND = new Random();

    // Make checkstyle happy.
    private UniversalHashes() {}

    /**
     * Force given integer positive. This is *not* abs, it just switches the
     * MSB off.
     */
    private static int pos(int i) {
        return i & ~(1 << 31);
    }

    /**
     * The classic Carter/Wegman construction. Works for any m &gt; 0 but uses
     * expensive modulo operations internally.
     *
     * @param m Size of the hash table, m &gt; 0.
     * @return Random hash function that yields 0..m-1.
     */
    public static HashFunction prime(int m) {
        if (m <= 0) {
            throw new IllegalArgumentException("m not positive!");
        }
        BigInteger bigM = BigInteger.valueOf(m);
        BigInteger bigP = bigM.nextProbablePrime(); // so that's a maybe
        int p = bigP.intValue();
        int b = RAND.nextInt(p);
        int a = RAND.nextInt(p);
        while (a == 0) {
            a = RAND.nextInt(p);
        }
        assert a > 0 && b >= 0;
        return prime(a, b, p, m);
    }

    // Separate method because of anonymous class restrictions.
    private static HashFunction prime(int a, int b, int p, int m) {
        return new HashFunction() {
            public int hash(int i) {
                return pos((a * i + b) % p) % m;
            }

            public String toString() {
                return "((" + a + " * x + " + b + ") % " + p + ") % " + m;
            }
        };
    }

    /**
     * The newer Dietzfelbinger construction. Works only for m = 2^M but should
     * be a lot faster to compute. (But note that 0 hashes to 0 which is sad.)
     *
     * @param m Size of the hash table, m = 2^M, 1 &lt; M &lt; 32.
     * @return Random hash function that yields 0..m-1.
     */
    public static HashFunction power(int m) {
        int bit = Integer.highestOneBit(m);
        if ((m & bit) != m || m <= 0) {
            throw new IllegalArgumentException("m not a positive power of 2!");
        }
        int bigM = Integer.numberOfTrailingZeros(bit);
        int w = 32;
        int exp = w - bigM;
        int b = RAND.nextInt(1 << exp); // too small for small inputs?
        int a = RAND.nextInt();
        while (a <= 0 || (a & 1) == 0) {
            a = RAND.nextInt();
        }
        assert a > 0 && b >= 0;
        return power(a, b, exp);
    }

    // Separate method because of anonymous class restrictions.
    private static HashFunction power(int a, int b, int exp) {
        return new HashFunction() {
            public int hash(int i) {
                return (a * i + b) >>> exp; // zero-fills high bits!
            }

            public String toString() {
                return "(" + a + " * x + " + b + ") >>> " + exp;
            }
        };
    }

    // Hash a few integers through given hash function.
    private static void hashSome(HashFunction h) {
        for (int i = 42; i < 47; i++) {
            System.out.printf("\t[%d => %d]", i, h.hash(i));
        }
        System.out.println();
    }

    // Hash a lot of integers through given hash function.
    private static void hashCollisions(HashFunction h, int n, int m) {
        int[] hits = new int[m];
        for (int j = 0; j < n; j++) {
            hits[pos(h.hash(RAND.nextInt()))] += 1;
        }
        for (int j = 0; j < hits.length; j++) {
            System.out.printf("\t%d",  hits[j]);
        }
        System.out.println();
    }

    /**
     * Main method demonstrates how to make and use hash functions.
     *
     * @param args Command line arguments (ignored).
     */
    public static void main(String[] args) {
        // Hash a few for a variety of tables sizes.
        for (int i = 4; i < 13; i += 2) {
            int size = 1 << i;

            System.out.printf(" size: %d\n", size);
            HashFunction pr = prime(size);
            System.out.printf("prime: %s\n", pr);
            hashSome(pr);

            HashFunction po = power(size);
            System.out.printf("power: %s\n", po);
            hashSome(po);

            System.out.println();
        }

        // Hash a lot and count collisions for small tables.
        System.out.println("prime collisions:");
        for (int i = 0; i < 8; i++) {
            HashFunction pr = prime(16);
            hashCollisions(pr, 32768, 16);
        }
        System.out.println("power collisions:");
        for (int i = 0; i < 8; i++) {
            HashFunction po = power(16);
            hashCollisions(po, 32768, 16);
        }
    }
}
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 