package IntList;

import static org.junit.Assert.*;
import org.junit.Test;

public class SquarePrimesTest {

    /**
     * Here is a test for isPrime method. Try running it.
     * It passes, but the starter code implementation of isPrime
     * is broken. Write your own JUnit Test to try to uncover the bug!
     */
    @Test
    public void testSquarePrimesSimple1() {
        IntList lst = IntList.of(14, 15, 16, 17, 18);
        boolean changed = IntListExercises.squarePrimes(lst);
        assertEquals("14 -> 15 -> 16 -> 289 -> 18", lst.toString());
        assertTrue(changed);
    }

    @Test
    public void test2() {
        IntList lst = IntList.of(3, 4, 4, 5);
        boolean changed = IntListExercises.squarePrimes(lst);
        assertEquals("9 -> 4 -> 4 -> 25", lst.toString());
        assertTrue(changed);
    }

    @Test
    public void test3() {
        IntList lst = IntList.of(4, 4, 4, 4);
        boolean changed = IntListExercises.squarePrimes(lst);
        assertEquals("4 -> 4 -> 4 -> 4", lst.toString());
        assertFalse(changed);
    }

    @Test
    public void test4() {
        IntList lst = IntList.of(3, 4, 3, 4);
        boolean changed = IntListExercises.squarePrimes(lst);
        assertEquals("9 -> 4 -> 9 -> 4", lst.toString());
        assertTrue(changed);
    }

    @Test
    public void test5() {
        IntList lst = IntList.of(4, 5);
        boolean changed = IntListExercises.squarePrimes(lst);
        assertEquals("4 -> 25", lst.toString());
        assertTrue(changed);
    }
}
