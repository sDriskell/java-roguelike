package roguelike.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//TODO: remove serializable implementation
/**
 * 
 * @param <T>
 */
public class CurrentItemTracker<T> implements Serializable {

  private static final long serialVersionUID = 1L;

  private ArrayList<T> list;
  private int currentItem;

  /**
   * 
   */
  public CurrentItemTracker() {
    list = new ArrayList<>();
  }

  /**
   * 
   * @return
   */
  public int count() {
    return list.size();
  }

  /**
   * 
   * @param argItm
   */
  public void add(T argItm) {
    list.add(argItm);
  }

  /**
   * 
   * @param argItm
   */
  public void remove(T argItm) {
    list.remove(argItm);

    if (list.isEmpty()) {
      currentItem = 0;
      return;
    }

    currentItem = currentItem % list.size();
  }

  /**
   * 
   * @return
   */
  public List<T> getAll() {
    return Collections.unmodifiableList(list);
  }

  /**
   * 
   * @return
   */
  public T getCurrent() {
    if (list.isEmpty()) {
      return null;
    }

    return list.get(currentItem);
  }

  /*
   * 
   */
  public T peek() {
    return list.get((currentItem + 1) % list.size());
  }

  /**
   * 
   */
  public void advance() {
    if (list.isEmpty()) {
      return;
    }

    currentItem = (currentItem + 1) % list.size();
  }

  /**
   * 
   */
  public void previous() {
    if (list.isEmpty()) {
      return;
    }

    currentItem = (currentItem + (list.size() - 1)) % list.size();
  }
}
