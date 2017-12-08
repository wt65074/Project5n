// Will Tobey
// wtobey1@jhu.edu
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Random;
import java.util.List;

// Third graded/ fourth submission

/**
 * Hash Table implementation that performs basic operations
 * in O(1) by way of a Cukoo hash table.
 *
 * @param <K> The key type.
 * @param <V> The value type.
*/
public class HashMapSub4<K, V> implements Map<K, V> {
 
    static final double MAX_LOAD_FACTOR = 0.5;
    static final int INITIAL_SIZE = 16;

    // Counts the number of elements.
    private int count = 0;

    // Stores how many displacements are okay before a rehash
    private int maxDisplacements = 0;

    // Hash Functions
    private HashFunction[] hashFunctions;

    private int hfCount = 2;

    // Underlying array
    private Entry<K, V>[] table;

    // Entry pairs up a key and a value.
    private static class Entry<K, V> {
        K key;
        V value;
        //int jHash;
        boolean tombstone = false;

        Entry() {
            tombstone = true;
        }

        Entry(K k, V v) {
            this.key = k;
            this.value = v;
            //jHash = key.hashCode();
        }

        @Override
        public String toString() {
            return this.key + ": " + this.value;
        } 

    }


    // Instantiates a new hashmap.
    public HashMapSub4() {

        table = (Entry<K, V>[]) new Entry[INITIAL_SIZE];

        this.newHashFunctions(INITIAL_SIZE);

        this.updateMaxDisplacements(INITIAL_SIZE);

    }

    private void newHashFunctions(int size) {
        this.hashFunctions = new HashFunction[this.hfCount];
        for (int i = 0; i < this.hfCount; i++) {
            hashFunctions[i] = this.newPowerHashFunction(size);
        }
    }

    // Computes the load factor.
    private double loadFactor() {
        return (double) this.count / (double) this.table.length;
    }

    // Hashes an entry.
    private int hash(Entry<K, V> e, int hashIndex) {
        return this.hash(e.key, hashIndex);    
    }

    // Hashes a key.
    public int hash(K key, int hashIndex) {
        return this.hashFunctions[hashIndex].hash(key.hashCode());
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
         // Always allow at least 2.
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
            // Possible to remove this call if there is a tombstone
            if (this.place(e) != null) {
                // The placement has failed, reset table and try to hash again.
                this.table = temp;
                this.rehash(size);
                return;
            }

        }

    }

    // Removes a key from the hash. Returns the value removed or null.
    private V removeKey(K k) {
        // Optimize this with the same optimization for find
        if (k == null) {
            return null;
        }

        int hash = this.hash(k, 0);
        Entry<K, V> found = this.table[hash];

        if (found != null && !found.tombstone && found.key.equals(k)) {
            table[hash] = new Entry<K, V>();
            this.count--;
            return found.value;
        } else if (found == null) {
            // If found is null and nothing has been deleted, then the key does not exist.
            //System.out.println("only executing one search");
            return null;
        }

        hash = hash ^ this.hash(k, 1);
        found = this.table[hash];

        if (found != null && !found.tombstone && found.key.equals(k)) {
            table[hash] = new Entry<K, V>();
            this.count--;
            return found.value;
        }

        return null;

    }

    
    private Entry<K, V> place(Entry<K, V> e) {

        if (e == null || e.tombstone) { return null; }

        Entry<K, V> current = e;
        int displacements = 0;

        int position = hash(current, 0);

        Entry<K, V> t = table[position];
        if (t == null || t.tombstone) {
            table[position] = current;
            return null;
        } else {
            int pos2 = position ^ hash(current, 1);
            t = table[pos2];
            if (t == null || t.tombstone) {
                table[pos2] = current;
                return null;
            }
        }

        while (displacements++ < this.maxDisplacements) {

            Entry<K, V> temp = table[position];
            table[position] = current;

            if (temp == null || temp.tombstone) {
                // We has succeeded placing the entry.
                return null;
            }

            current = temp;

            // Choose the correct hash function to use.
            // If we displaced an element from its first hash position,
            // we need to move it to its second, and visa versa.
            position = position ^ hash(temp, 1);

        }

        return current;

    }
    
    /*
    // Attempts to place an entry in the table. Returns the entry that couldn't be placed
    // if too many displacements are attempted. 
    private Entry<K, V> place(Entry<K, V> e) {

        if (e == null) { return null; }


        //System.out.println("Placing: " + this.printEntry(e));

        int hashFunc = 0;
        Entry<K, V> current = e;
        int displacements = 0;

        int position = hash(e, 0);

        Entry<K, V> temp = table[position];

        if (temp == null || temp.tombstone) {
            //System.out.println("Found empty spot, placing");
            table[position] = current;
            return null;

        } else {
            int positionTwo = hash(e, 1);
            temp = table[positionTwo];
            if (temp == null || temp.tombstone) {
                //System.out.println("Found Empty Spot, placing Secondary");
                table[positionTwo] = current;
                return null;
            }

        }

        while (displacements++ < this.maxDisplacements) {

            temp = table[position];
            table[position] = current;

            //System.out.println("Evicting " + this.printEntry(temp));

            if (temp == null) {
                // We has succeeded placing the entry.
                return null;
            }

            current = temp;

            // Choose the correct hash function to use.
            // If we displaced an element from its first hash position,
            // we need to move it to its second, and visa versa.
            if (hash(temp, 0) == position) {
                hashFunc = 1;
            } else {
                hashFunc = 0;
            }

            position = hash(e, hashFunc);

        }

        // Failed the placement.
        // Will need to rehash and retry placing current.
        return current;

    }
    
    private Entry<K, V> place(Entry<K, V> e) {

        if (e == null) { return null; }

        Entry<K, V> current = e;
        int displacements = 0;

        int position = hash(current, 0);

        while (displacements++ < this.maxDisplacements) {

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
            position = position ^ hash(temp, 1);

        }

        return current;
    }
    */
    
    private String printEntry(Entry<K, V> e) {
        String toReturn = new String("" + e);
        toReturn += " (";
        for (int i = 0; i < this.hfCount; i++) {
            toReturn += this.hash(e, i) + ",";
        }
        toReturn += ")";
        return toReturn;    
    }

    // Attempts to find and return an entry for a key.
    private Entry<K, V> find(K k) {

        if (k == null) {
            return null;
        }

        int hash = this.hash(k, 0);
        Entry<K, V> found = this.table[hash];

        if (found != null && k.equals(found.key)) {
            return found;
        } else if (found == null) {
            // If found is null and nothing has been deleted, then the key does not exist.
            //System.out.println("only executing one search");
            return null;
        }

        hash = hash ^ this.hash(k, 1);
        found = this.table[hash];

        if (found != null && k.equals(found.key)) {
            return found;
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
            if (e == null || e.tombstone) { continue; }
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
            if (e != null && !e.tombstone) {
                keys.add(e.key);
            }
            
        }

        return keys.iterator();

    }

    public void printAll() {

        for (int a = 0; a < this.table.length; a ++) {
                System.out.print("[");
                System.out.print(table[a] != null ? (table[a].tombstone ? "T" : table[a]) : "*");
                System.out.print("] ");
            }
            System.out.println();

    }

    public static void main(String[] args) {
        
        HashMapSub4<Integer, Integer> map = new HashMapSub4<Integer, Integer>();
        Random r = new Random();

        for (int i = 0; i < 100000; i++) {

            try {

                map.insert(r.nextInt(1000000), 0);
            } catch (IllegalArgumentException e) {

                
            }

        }

        for (int i = 0; i < 100; i++) {

            try {

                map.get(r.nextInt(1000000));
            } catch (IllegalArgumentException e) {

                
            }

        }

        
     }


}