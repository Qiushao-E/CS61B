package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
    private int size;
    private int nextFirst;
    private int nextLast;
    private T[] items;
    private final int limitSize;
    private final double limitRatio;

    public ArrayDeque() {
        this.size = 0;
        items = (T[]) new Object[8];
        this.nextFirst = 4;
        this.nextLast = 5;
        this.limitSize = 16;
        this.limitRatio = 0.25;
    }

    @Override
    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }

    private class ArrayDequeIterator implements Iterator<T> {
        private int index;

        public ArrayDequeIterator() {
            index = (nextFirst + 1) % items.length;
        }

        @Override
        public boolean hasNext() {
            return index != nextLast;
        }

        @Override
        public T next() {
            T tmp = items[index];
            index = (index + 1) % items.length;
            return tmp;
        }
    }

    @Override
    public void addFirst(T item) {
        size += 1;
        if (nextFirst == nextLast) {
            resize(items.length * 2, nextFirst - 1, nextFirst + 1);
        }
        items[nextFirst] = item;
        if (nextFirst == 0) {
            nextFirst = items.length - 1;
        } else {
            nextFirst -= 1;
        }
    }

    @Override
    public void addLast(T item) {
        size += 1;
        if (nextFirst == nextLast) {
            resize(items.length * 2, nextFirst - 1, nextFirst + 1);
        }
        items[nextLast] = item;
        nextLast = (nextLast + 1) % items.length;
    }

    @Override
    public int size() {
        return this.size;
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
        int removeIndex = (nextFirst + 1) % items.length;
        size -= 1;
        T tmp = items[removeIndex];
        nextFirst = removeIndex;

        if (items.length > limitSize && ((double) size / items.length) <= limitRatio) {
            resize(items.length / 2, nextLast - 1, nextFirst + 1);
        }

        return tmp;
    }

    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        int removeIndex;
        size -= 1;
        if (nextLast == 0) {
            removeIndex = items.length - 1;
        } else {
            removeIndex = nextLast - 1;
        }
        T tmp = items[removeIndex];
        nextLast = removeIndex;

        if (items.length > limitSize && ((double) size / items.length) <= limitRatio) {
            resize(items.length / 2, nextLast - 1, nextFirst + 1);
        }

        return tmp;
    }

    @Override
    public T get(int index) {
        if (index > size - 1) {
            return null;
        }
        int arrayDequeIndex = (nextFirst + index + 1) % items.length;
        return items[arrayDequeIndex];
    }

    private void resize(int newLength, int firstIndex, int lastIndex) {
        T[] tmp = (T[]) new Object[newLength];
        if (nextFirst >= nextLast) {
            System.arraycopy(items, 0, tmp, 0, firstIndex + 1);
            System.arraycopy(items, lastIndex, tmp, newLength + lastIndex - items.length, items.length - lastIndex);
            nextFirst += (newLength - items.length);
        } else {
            int start, end, copyLength;
            if (nextLast == 0) {
                end = items.length;
            } else {
                end = nextLast - 1;
            }
            if (nextFirst == items.length) {
                start = 0;
            } else {
                start = nextFirst + 1;
            }
            copyLength = end - start + 1;
            System.arraycopy(items, start, tmp, 1, copyLength);
            nextFirst = 0;
            nextLast = copyLength + 1;
        }
        items = tmp;
    }

    public boolean equals(Object o) {
        boolean flag = o instanceof Deque;
        if (o == null || !flag) {
            return false;
        } else {
            Deque d = (Deque) o;
            if (size != d.size()) {
                return false;
            } else {
                for (int i = 0; i < size; i++) {
                    if (!this.get(i).equals(d.get(i))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
