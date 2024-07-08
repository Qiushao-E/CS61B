package deque;

import java.util.Iterator;

/**
 * @param <T>
 */
public interface Deque<T> {
    public void addFirst(T item);
    /* add an item at the first of the deque */

    public void addLast(T item);
    /* add an item at the last of the deque */

    public boolean isEmpty();
    /* return if the deque is empty */

    public int size();
    /* return the size of the deque */

    public void printDeque();
    /* print the deque */

    public T removeFirst();
    /* remove the first item of the deque and return it */

    public T removeLast();
    /* remove the last item of the deque and return it */

    public T get(int index);
    /* return the item at the index position, using iterator */

    public Iterator<T> iterator();
    /* return the iterator of the deque */

    public boolean equals(Object o);
    /* return if o is equal to the deque */

}
