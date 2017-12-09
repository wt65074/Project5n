// Will Tobey
// wtobey1@jhu.edu

import java.util.Iterator; 
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

public class MapTestBase {

    Map<Integer, Integer> map;

    @Test
    public void testHas() {

        for (int i = 0; i < 1000; i++) {
            map.insert(i, 1);
            assertTrue(map.has(i));
        }

        for (int i = 0; i < 1000; i++) {
            map.remove(i);
            assertTrue(!map.has(i));
        }

    }

    @Test
    public void testInsert() {

        for (int i = 0; i < 1000; i++) {
            map.insert(i, 1);
            assertTrue(map.has(i));
            assertTrue(map.get(i) == 1);
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
            assertTrue(!map.has(i));
        }

        assertTrue(map.size() == 0);
    }

    @Test
    public void testIterator() {

        /*
        for (int i = 0; i < 1000; i++) {
            map.insert(i, 1);
        }

        int count = 0;

        Iterator<Integer> iterator = map.iterator();

        while( iterator.hasNext() ) {
            int key = iterator.next();
            assertTrue(key == count++);
        }
    */
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

        for (int i = 0; i < 1000; i++) {
            map.remove(i);
        }

        assertTrue(map.size() == 0);

    }

    @Test
    public void testPut() {

        for (int i = 0; i < 1000; i++) {
            map.insert(i, 1);
            map.put(i, 2);
            assertTrue(map.get(i) == 2);
        }

    }

    @Test
    public void testGet() {

        for (int i = 0; i < 1000; i++) {
            map.insert(i, 1);
            map.put(i, 2);
            assertTrue(map.get(i) == 2);
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

    @Test
    public void testRemoveException() {

        try {
            map.remove(null);
            assertTrue(false);
        } catch (IllegalArgumentException e) {

        }

        try {
            map.remove(1);
            assertTrue(false);
        }
        catch (IllegalArgumentException e) {

        }
    }

    @Test
    public void testPutException() {

        try {
            map.put(null, 1);
            assertTrue(false);
        } catch (IllegalArgumentException e) {

        }

        try {
            map.put(1, 1);
            assertTrue(false);
        }
        catch (IllegalArgumentException e) {

        }
    }

    @Test
    public void testGetException() {

        try {
            map.get(null);
            assertTrue(false);
        } catch (IllegalArgumentException e) {

        }

        try {
            map.get(1);
            assertTrue(false);
        }
        catch (IllegalArgumentException e) {

        }
    }

}