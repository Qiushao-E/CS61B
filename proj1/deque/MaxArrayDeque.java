package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private final Comparator<T> cp;

    public MaxArrayDeque(Comparator<T> c) {
        super();
        this.cp = c;
    }

    public T max() {
        return max(this.cp);
    }

    public T max(Comparator<T> c) {
        if (this.size() == 0) {
            return null;
        }

        T maxItem = this.get(0);
        for (T i: this) {
            if (c.compare(i, maxItem) > 0) {
                maxItem = i;
            }
        }
        return maxItem;
    }

}
