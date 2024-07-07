package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {
    private int size;
    private final Node sentinel;

    public class Node {
        private Node prev;
        private Node next;
        private T item;
    } /* use the double linked list and the circle data structure */

    public LinkedListDeque() {
        this.size = 0;
        sentinel = new Node();
        sentinel.item = null;
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
    }

    @Override
    public Iterator<T> iterator() {
        return new LinkedListDequeIterator();
    }

    private class LinkedListDequeIterator implements Iterator<T> {
        private Node current;

        public LinkedListDequeIterator() {
            current = sentinel.next;
        }
        @Override
        public boolean hasNext() {
            return current != sentinel;
        }

        @Override
        public T next() {
            T tmp = current.item;
            current = current.next;
            return tmp;
        }
    }

    @Override
    public void addFirst(T item) {
        size += 1;

        Node tmp = new Node();
        tmp.item = item;
        tmp.next = sentinel.next;
        sentinel.next.prev = tmp;
        sentinel.next = tmp;
        tmp.prev = sentinel;
    }

    @Override
    public void addLast(T item) {
        size += 1;

        Node tmp = new Node();
        tmp.item = item;
        tmp.prev = sentinel.prev;
        sentinel.prev.next = tmp;
        sentinel.prev = tmp;
        tmp.next = sentinel;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        for (T i: this) {
            System.out.print(i.toString() + ' ');
        }
        System.out.println();
    }

    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }

        size -= 1;

        T tmp = sentinel.next.item;
        sentinel.next = sentinel.next.next;
        sentinel.next.prev = sentinel;
        return tmp;
    }

    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }

        size -= 1;

        T tmp = sentinel.prev.item;
        sentinel.prev = sentinel.prev.prev;
        sentinel.prev.next = sentinel;
        return tmp;
    }

    @Override
    public T get(int index) {
        if (index > size - 1) {
            return null;
        }
        Node current = sentinel.next;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current.item;
    }

    public T getRecursive(int index) {
        if (index > size - 1) {
            return null;
        }
        return getRecursiveHelper(index, sentinel.next);
    }

    private T getRecursiveHelper(int index, Node current) {
        if (index == 0) {
            return current.item;
        } else {
            return getRecursiveHelper(index - 1, current.next);
        }
    }

    @Override
    public boolean equals(Object o) {
        boolean flag = o instanceof LinkedListDeque;
        if (o == null || !flag) {
            return false;
        } else {
            LinkedListDeque lld = (LinkedListDeque) o;
            if (size != lld.size()){
                return false;
            } else {
                for (int i = 0; i < size; i++){
                    if (!this.get(i).equals(lld.get(i))){
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
