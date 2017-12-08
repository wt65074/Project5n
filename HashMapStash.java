// Will Tobey
// wtobey1@jhu.edu
import java.util.Iterator;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.List;

/**
 * Hash Table implementation that performs basic operations
 * in O(1) by way of a Cukoo hash table.
 *
 * @param <K> The key type.
 * @param <V> The value type.
*/
public class HashMapStash<K, V> implements Map<K, V> {

    static final double MAX_LOAD_FACTOR = 0.5;
    static final int INITIAL_SIZE = 16;
    static final int MAX_STASH_SIZE = 4;

    // Counts the number of elements.
    private int count = 0;

    // Stores how many displacements are okay before a rehash
    private int maxDisplacements = 0;

    // Hash Functions
    private HashFunction[] hashFunctions;

    // Stash
    private LinkedList<Entry<K, V>> stash;

    // Underlying array
    private Entry<K, V>[] table;

    // Number of hash functions
    private int hfCount = 2;
    private int subTableSize = INITIAL_SIZE / hfCount;

    public int rehashes = 0;
    public int unplanned = 0;

    // Entry pairs up a key and a value.
    private static class Entry<K, V> {
        K key;
        V value;
        int jHash;

        Entry(K k, V v) {
            this.key = k;
            this.value = v;
            jHash = key.hashCode();
        }

        @Override
        public String toString() {
            return this.key + ": " + this.value;
        } 

    }

    // Instantiates a new hashmap.
    public HashMapStash() {

        table = (Entry<K, V>[]) new Entry[INITIAL_SIZE];
        stash = new LinkedList<Entry<K, V>>();

        this.newHashFunctions(INITIAL_SIZE);

        this.updateMaxDisplacements(INITIAL_SIZE);

    }

    private void newHashFunctions(int size) {
        this.hashFunctions = new HashFunction[this.hfCount];
        for (int i = 0; i < this.hfCount; i++) {
            hashFunctions[i] = this.newPowerHashFunction(size / this.hfCount);
        }
    }

    private int subTableSize() {
        return this.table.length / this.hfCount;
    }

    // Computes the load factor.
    private double loadFactor() {
        return (double) this.count / (double) this.table.length;
    }

    // Hashes an entry.
    private int hash(Entry<K, V> e, int hashIndex) {
        return hashIndex * subTableSize + this.hashFunctions[hashIndex].hash(e.jHash);    
    }

    // Hashes a key.
    public int hash(K key, int hashIndex) {
        return hashIndex * subTableSize + this.hashFunctions[hashIndex].hash(key.hashCode());
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
        int max = (int) (Math.log(size) / Math.log(2)); // Compute log2(size)
        maxDisplacements = max > 2 ? max : 2; // Always allow at least 2.
    }

    private String printEntry(Entry<K, V> e) {
        String toReturn = new String("" + e);
        toReturn += " (";
        for (int i = 0; i < this.hfCount; i++) {
            toReturn += this.hash(e, i) + ",";
        }
        toReturn += ")";
        return toReturn;    
    }

    // Inserts an entry into the table.
    private void insert(Entry<K, V> e) {

        if (this.loadFactor() > 0.8) {
            this.rehash(this.table.length*2);
        }

        // Rehash the table if the load factor is too high.
        //System.out.println("Load Factor: " + this.count + "/" + this.table.length + " = " + this.loadFactor());
        //System.out.println("Load Factor: " + this.loadFactor() + " MAX: " + MAX_LOAD_FACTOR);
        
        Entry<K, V> displaced = place(e);

        if (displaced != null) {

            //System.out.println("Failed to place element: " + displaced);

            if (stash.size() < MAX_STASH_SIZE) {

                //System.out.println("Thats okay, there is only " + stash.size() + "elements in the stash");

                stash.addLast(displaced);
                displaced = null;

            } else {

                //System.out.println("Thats a bit of an issue becase the stash if full");

                int stashSize = stash.size();

                for (int i = 0; i < stashSize; i++) {

                    Entry<K, V> top = stash.removeFirst();
                    Entry<K, V> dse = place(top);

                    if (dse == null) {

                        stash.addLast(displaced);
                        displaced = null;
                        break;

                    } else {

                        stash.addLast(dse);

                    }

                }

            }
        }

        if (displaced != null) {

            //System.out.println("Oof, we couldn't place any of the elements in the stash. Gonna rehash");

            rehash(this.table.length * 2);
            displaced = place(displaced);
        }
        

        this.count++;

    }

    // Rehashes into a new underlying table of size size.
    private void rehash(int size) {

        this.subTableSize = size / this.hfCount;

        rehashes++;

        //System.out.println("Rehash");

        // Create new table
        Entry<K, V>[] temp = this.table;
        LinkedList<Entry<K, V>> tempStash= this.stash;

        // Create new hashing functions
        this.newHashFunctions(size);

        // Create a new empty table.
        this.table = (Entry<K, V>[]) new Entry[size];

        // Update the maximum displacements based on the new size.
        this.updateMaxDisplacements(size);

        // Place all entries in the table.
        // ADD? Limit on the number of rehashes
        for (Entry<K, V> e: temp) {
            if (e != null && this.place(e) != null) {
                // The placement has failed, reset table and try to hash again.
                this.table = temp;
                this.rehash(size);
                return;
            }
        }

        while (this.stash.size() > 0) {
            Entry<K,V> e = stash.removeFirst();
            if (e != null && this.place(e) != null) {
                // The placement has failed, reset table and try to hash again.
                this.table = temp;
                this.stash = tempStash;
                this.rehash(size);
                return;
            } else {

            }

        }
    }

    // Removes a key from the hash. Returns the value removed or null.
    private V removeKey(K k) {

        if (k == null) {
            return null;
        }

        for (int i = 0; i < this.hashFunctions.length; i++) {

            int hash = this.hash(k, i);
            Entry<K, V> found = this.table[hash];

            if (found != null && found.key.equals(k)) {
                table[hash] = null;
                this.count--;
                return found.value;
            }

        }

        int stashSize = this.stash.size();

        for (int i = 0; i < stashSize; i++) {
            Entry<K, V> found = this.stash.removeFirst();
            if (found.key.equals(k)) {
                return found.value;
            } else {
                this.stash.addLast(found);
            }
        }

        return null;
    }

    // Attempts to place an entry in the table. Returns the entry that couldn't be placed
    // if too many displacements are attempted. 
    private Entry<K, V> place(Entry<K, V> e) {

        if (e == null) { return null; }

        //System.out.println("Placing: " + this.printEntry(e));
        int funcIndex = 0;
        Entry<K, V> current = e;
        int displacements = 0;

        int position = -1;

        // Dont want to check the current one, only the other functions
            for (int i = 0; i < this.hfCount; i++) {

                // Get a new position from a function.
                int newPosition = hash(current, (funcIndex + i) % hfCount);
                   ////System.out.println("Checking position: " + newPosition);
                // Check if that position is empty
                if (this.table[newPosition] == null) {
                    // If it is, we will place at that position
                    position = newPosition;
                    funcIndex = i;
                    ////System.out.println("Chose position");
                    break;
                } else {
                    // If its not, but we havent seen a position yet,
                    // thats the new best position.
                    if (position == -1) {
                        position = newPosition;
                        funcIndex = i;
                    }
                }
            }

        while (displacements++ < this.maxDisplacements) {
            
            Entry<K, V> temp = this.table[position];
            this.table[position] = current;

            if (temp == null) {
                // We has succeeded placing the entry.
                return null;
            }

            current = temp;

            //System.out.println("Displaced at " + position + ": " + this.printEntry(current));

            // Choose the correct hash function to use.
            // If we displaced an element from its first hash position,
            // we need to move it to its second, and visa versa.

            // Calculates the index of the hash function in the hash function array
            // position / (table length / number of hash functions)
            // Ex: position 10 in table of size 24 with 3 hash functions
            // Should be the second hash function (8 spaces for each hash function)
            // 10 / (8) = 1
            // Modulo so that it wraps around to 1
            //funcIndex = newIndex % hfCount;
            funcIndex = funcIndex == 0 ? 1 : 0;
            position = hash(current, funcIndex);

            /*
            bestPosition = -1;

            // Dont want to check the current one, only the other functions
            for (int i = 0; i < this.hfCount - 1; i++) {

                // Get a new position from a function.
                int newPosition = hash(current, (funcIndex + i) % hfCount);
                //System.out.println("Checking position: " + newPosition);
                // Check if that position is empty
                if (this.table[newPosition] == null) {
                    // If it is, we will place at that position
                    bestPosition = newPosition;
                    //System.out.println("Chose position");
                    break;
                } else {
                    // If its not, but we havent seen a position yet,
                    // thats the new best position.
                    if (bestPosition == -1) {
                        bestPosition = newPosition;
                    }
                }
            }
            */
            

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

        for (int i = 0; i < this.hashFunctions.length; i++) {

            int hash = this.hash(k, i);
            Entry<K, V> found = this.table[hash];

            if (found != null && found.key.equals(k)) {
                return found;
            }

        }

        int stashSize = this.stash.size();

        for (int i = 0; i < stashSize; i++) {
            //System.out.println("Searching Stash");
            Entry<K, V> found = this.stash.removeFirst();
            if (found.key.equals(k)) {
                return found;
            } else {
                this.stash.addLast(found);
            }
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
        for (Entry<K, V> e: this.table) {
            if (e != null) {
                keys.add(e.key);
            }
        }

        return keys.iterator();

    }

    public void printAll() {

        for (int i = 0; i < this.subTableSize(); i++) {

            for (int a = i; a < this.table.length; a += this.subTableSize()) {
                //System.out.print("[");
                //System.out.print(table[a] != null ? table[a] : "*");
                //System.out.print("] ");
            }
            //System.out.println();
        }

        //System.out.println();

    }

    public static void main(String[] args) {
        
        HashMapStash<Integer, Integer> map = new HashMapStash<Integer, Integer>();
        Random r = new Random();

        for (int i = 0; i < 120 ; i++) {

            try {
                map.insert(r.nextInt(1000), i);
                map.printAll();
            } catch (IllegalArgumentException e) {

                
            }
        }
        
     }


}