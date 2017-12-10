// Will Tobey
// wtobey1@jhu.edu

import java.util.Iterator; 
import org.junit.Test;
import org.junit.Before;
import java.util.HashMap;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

public class MapTestBase {

    Map<Integer, Integer> map;

    @Test
    public void testHas() {

        for (int i = 0; i < 1000; i++) {
            map.insert(i, 1);
        }

        for (int i = 1000; i < 2000; i++) {
            assertTrue(!map.has(i));
        }

        for (int i = 0; i < 1000; i++) {
            map.remove(i);
        }

        for (int i = 0; i < 1000; i++) {
            assertTrue(!map.has(i));
        }

    }

    @Test
    public void testInsert() {

        for (int i = 0; i < 1000; i++) {
            
            map.insert(i, i);
            
        }

        for (int i = 0; i < 1000; i++) {
            
            assertTrue(map.get(i) == i);
            
        }

        for (int i = 0; i < 1000; i++) {
            
            assertTrue(map.has(i));
            
        }

        assertTrue(map.size() == 1000);

    }

    @Test
    public void testRemove() {

        for (int i = 0; i < 1000; i++) {
            
            map.insert(i, 1);

        }

        for (int i = 0; i < 1000; i++) {
            
            map.remove(i);

        }

        for (int i = 0; i < 1000; i++) {

            assertTrue(!map.has(i));

        }

        assertTrue(map.size() == 0);

    }

    @Test
    public void testIterator() {

        // Just used to quickly check if iterator has some stuff
        // Not cheating, just dont want to use my own map class
        // to verify my map class is working.
        HashMap<Integer, Integer> hashMap = new HashMap<Integer, Integer>();

        for (int i = 0; i < 1000; i++) {
            map.insert(i, 1);
            hashMap.put(i, 1);

        }

        int count = 0;

        Iterator<Integer> iterator = map.iterator();

        while( iterator.hasNext() ) {
            count++;
            hashMap.remove(iterator.next());

        }

        // IF every element has been removed from the hash map, 
        // the iterator got all elements.
        assertTrue(hashMap.size() == 0);

        assertTrue(count == map.size());

    }

    @Test
    public void testToString() {


        // I cant really test a whole lot here because of the randomness of the
        // hash maps. The keys could be printed in any order, so the only tests
        // I can really make are checking it when empty and after one insertion.

        assertEquals("{}", map.toString());

        map.insert(1, 0);

        assertEquals("{1: 0}", map.toString());


    }

    @Test
    public void testSize() {

        for (int i = 0; i < 1000; i++) {
            map.insert(i, 1);
        }

        assertTrue(map.size() == 1000);

        for (int i = 0; i < 500; i++) {
            map.remove(i);
        }

        assertTrue(map.size() == 500);

        for (int i = 500; i < 1000; i++) {
            map.remove(i);
        }

        assertTrue(map.size() == 0);

    }

    @Test
    public void testPut() {

        for (int i = 0; i < 1000; i++) {
            map.insert(i, 1);
            map.put(i, 2);
        }

        for (int i = 0; i < 1000; i++) {
            assertTrue(map.get(i) == 2);
        }

    }

    @Test
    public void testGet() {

        for (int i = 0; i < 1000; i++) {
            map.insert(i, 1);
        }

        // Test Get
        for (int i = 0; i < 1000; i++) {
            assertTrue(map.get(i) == 1);
        }

        for (int i = 0; i < 500; i++) {
            map.put(i, 2);
        }
        for (int i = 500; i < 750; i++) {
            map.put(i, 3);
        }
        for (int i = 750; i < 100; i++) {
            map.put(i, 4);
        }

        for (int i = 0; i < 500; i++) {
            assertTrue(map.get(i) == 2);
        }
        for (int i = 500; i < 750; i++) {
            assertTrue(map.get(i) == 3);
        }
        for (int i = 750; i < 100; i++) {
            assertTrue(map.get(i) == 4);
        }

    }

    @Test(expected = IllegalArgumentException.class)
    public void testInsertExceptionNull() {

        map.insert(null, 1);

    }

    @Test(expected = IllegalArgumentException.class)
    public void testInsertExceptionDuplicate() {
        
        for (int i = 0; i < 1000; i++) {
            map.insert(i, 10);
        }

        map.insert(121, 1);

    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveExceptionNull() {

        map.remove(null);

    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveExceptionNonExistent() {

        for (int i = 0; i < 100; i++) {
            map.insert(i, 10);
        }

        map.remove(101);

    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutExceptionNull() {

        map.put(null, 1);

    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutExceptionNonExistent() {
        for (int i = 0; i < 100; i++) {
            map.insert(i, 10);
        }

        map.put(101, 20);
    }


    @Test (expected = IllegalArgumentException.class)
    public void testGetExceptionNull() {
        
        map.get(null);
        
    }

    @Test (expected = IllegalArgumentException.class)
    public void testGetExceptionNonExistent() {
        for (int i = 0; i < 100; i++) {
            map.insert(i, 10);
        }

        map.get(101);
    }


}