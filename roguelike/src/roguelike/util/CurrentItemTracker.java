package roguelike.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CurrentItemTracker<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private ArrayList<T> list;
    private int currentItem;

    public CurrentItemTracker() {
        list = new ArrayList<>();
    }

    public int count() {
        return list.size();
    }

    public void add(T item) {
        list.add(item);
    }

    public void remove(T item) {
        list.remove(item);
        if (list.isEmpty()) {
            currentItem = 0;
            return;
        }
        currentItem = currentItem % list.size();
    }

    public List<T> getAll() {
        return Collections.unmodifiableList(list);
    }

    public T getCurrent() {
        if (list.isEmpty()) {
            return null;
        }
        return list.get(currentItem);
    }

    public T peek() {
        return list.get((currentItem + 1) % list.size());
    }

    public void advance() {
        if (list.isEmpty()) {
            return;
        }
        currentItem = (currentItem + 1) % list.size();
    }

    public void previous() {
        if (list.isEmpty()) {
            return;
        }
        currentItem = (currentItem + (list.size() - 1)) % list.size();
    }
}
