// Will Tobey
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
