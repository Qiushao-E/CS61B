package deque;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Performs some basic Array Deque tests
 */
public class ArrayDequeTest {
    /**
     * Build the basic ArrayDeque and test the add methods with resizing the array
     */
    @Test
    public void buildTest() {
        ArrayDeque<Integer> intDeque = new ArrayDeque<>();
        for (int i = 0; i < 50; i++){
            intDeque.addFirst(i);
            intDeque.addLast(-i);
        }
        intDeque.printDeque();
    }

    /**
     * Test the get method
     */
    @Test
    public void getTest() {
        ArrayDeque<Integer> intDeque = new ArrayDeque<>();
        for (int i = 0; i < 50; i++){
            intDeque.addLast(i);
        }
        int item = intDeque.get(28);
        assertEquals(28, item);
        assertNull(intDeque.get(1000));
    }

    @Test
    public void sizeTest() {
        ArrayDeque<Integer> intDeque = new ArrayDeque<>();
        for (int i = 0; i < 50; i++){
            intDeque.addLast(i);
            assertEquals(i + 1, intDeque.size());
        }
    }

    /**
     * Test the remove methods, with resizing the length of the array
     */
    @Test
    public void removeTest() {
        ArrayDeque<Integer> intDeque = new ArrayDeque<>();
        for (int i = 0; i < 50; i++) {
            intDeque.addLast(i);
        }
        for (int i = 0; i < 20; i++) {
            int tmpFirst = intDeque.removeFirst();
            assertEquals(i, tmpFirst);
            int tmpLast = intDeque.removeLast();
            assertEquals(49 - i, tmpLast);
        }
    }

    /**
     * Test the array in the case that its size is larger than 100000
     */
    @Test
    public void bigSizeArrayTest() {
        ArrayDeque<Integer> intDeque = new ArrayDeque<>();
        for (int i = 0; i < 100100; i++) {
            intDeque.addLast(i);
            intDeque.addFirst(i);
            assertEquals(2 * i + 2, intDeque.size());
        }
        int item = intDeque.get(0);
        assertEquals(100099, item);
        for (int i = 0; i < 100100; i++){
            intDeque.removeFirst();
            intDeque.removeLast();
        }
    }

    /**
     * Test the isEmpty method
     */
    @Test
    public void isEmptyTest() {
        ArrayDeque<Integer> intDeque = new ArrayDeque<>();
        assertTrue(intDeque.isEmpty());
        for (int i = 0; i < 50; i++) {
            intDeque.addLast(i);
            assertFalse(intDeque.isEmpty());
        }
    }

    /**
     * Test different type of the item in the array
     */
    @Test
    public void differentTypeTest() {
        ArrayDeque<String> stringArrayDeque = new ArrayDeque<>();
        ArrayDeque<Double> doubleArrayDeque = new ArrayDeque<>();
        ArrayDeque<Boolean> booleanArrayDeque = new ArrayDeque<>();

        String[] stringItems = {"I", "am", "a", "happy", "student", "with", "CS61B"};
        for (int i = 0; i < 5; i++) {
            doubleArrayDeque.addLast((double) i + 0.114514);
            booleanArrayDeque.addLast(true);
            booleanArrayDeque.addLast(false);
        }
        for (String i: stringItems){
            stringArrayDeque.addLast(i);
        }

        stringArrayDeque.printDeque();
        doubleArrayDeque.printDeque();
        booleanArrayDeque.printDeque();
    }
}
