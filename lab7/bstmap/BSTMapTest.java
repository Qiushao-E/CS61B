package bstmap;

import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.*;

public class BSTMapTest {
    @Test
    public void basicTest() {
        BSTMap<Integer, String> map = new BSTMap<>();
        map.put(2, "I");
        map.put(1, "am");
        map.put(3, "a");
        map.put(6, "happy");
        map.put(5, "man");
        map.put(7, "!");
        map.put(5, "woman");
        assertTrue(map.containsKey(6));
        assertFalse(map.containsKey(0));
        map.printInOrder();
    }

    @Test
    public void setTest() {
        BSTMap<Integer, String> map = new BSTMap<>();
        map.put(2, "I");
        map.put(1, "am");
        map.put(3, "a");
        map.put(6, "happy");
        map.put(5, "man");
        map.put(7, "!");
        map.put(5, "woman");
        Set<Integer> intSet = map.keySet();
        assertTrue(intSet.contains(1));
    }
}
