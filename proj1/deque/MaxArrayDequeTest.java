package deque;

import org.junit.Test;

import java.util.Comparator;

import static org.junit.Assert.*;

public class MaxArrayDequeTest {
    Comparator<Integer> integerBiggerComparator = new Comparator<Integer>() {
        @Override
        public int compare(Integer o1, Integer o2) {
            return o1 - o2;
        }
    };
    Comparator<Integer> integerSmallerComparator = new Comparator<Integer>() {
        @Override
        public int compare(Integer o1, Integer o2) {
            return o2 - o1;
        }
    };
    /**
     * Test the max method
     */
    @Test
    public void maxTest(){
        MaxArrayDeque<Integer> integerMaxArrayDeque = new MaxArrayDeque<>(integerBiggerComparator);
        integerMaxArrayDeque.addFirst(5);
        integerMaxArrayDeque.addLast(2);
        integerMaxArrayDeque.addFirst(0);
        integerMaxArrayDeque.addLast(1);
        integerMaxArrayDeque.addFirst(1);
        integerMaxArrayDeque.addLast(4);
        integerMaxArrayDeque.addFirst(5);
        integerMaxArrayDeque.addLast(1);
        integerMaxArrayDeque.addFirst(4);
        integerMaxArrayDeque.addLast(123);
        int max = integerMaxArrayDeque.max();
        int min = integerMaxArrayDeque.max(integerSmallerComparator);
        assertEquals(123, max);
        assertEquals(0, min);
    }
}
