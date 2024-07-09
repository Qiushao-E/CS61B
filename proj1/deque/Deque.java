package deque;

import java.util.Iterator;

/**
 * @param <T>
 */
public interface Deque<T> {
    void addFirst(T item);
    /* add an item at the first of the deque */

    void addLast(T item);
    /* add an item at the last of the deque */

    default boolean isEmpty(){
        return this.size() == 0;
    }
    /* return if the deque is empty */

    int size();
    /* return the size of the deque */

    void printDeque();
    /* print the deque */

    T removeFirst();
    /* remove the first item of the deque and return it */

    T removeLast();
    /* remove the last item of the deque and return it */

    T get(int index);
    /* return the item at the index position, using iterator */


}
