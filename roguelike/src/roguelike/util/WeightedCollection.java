package roguelike.util;

import java.util.ArrayList;

import squidpony.squidutility.Pair;

/**
 * 
 * @param <T>
 */
public class WeightedCollection<T> {

  private final ArrayList<Pair<Integer, T>> table = new ArrayList<>();
  private int total = 0;

  /**
   * Returns the first object whose weighted position is greater than the
   * specified value.
   * 
   * Returns null if no elements have been put in the table.
   * 
   * @return
   */
  public T getItem(int argVal) {
    if (table.isEmpty()) {
      return null;
    }

    int index = argVal;

    // Start looping at second item
    for (int i = 0; i < table.size(); i++) {
      index -= table.get(i).getFirst();

      if (index < 0) {
        return table.get(i).getSecond();
      }
    }

    /*
     * Something went wrong, shouldn't have been able to get all the way through
     * without finding an item
     */
    return null;
  }

  /**
   * Adds the given item to the table and sorts it.
   * 
   * Weight must be greater than 0.
   * 
   * @param argItm
   * @param argWgt
   */
  public void add(T argItm, int argWgt) {
    table.add(new Pair<>(argWgt, argItm));
    total += argWgt;

    table
        .sort(
            ((Pair<Integer, T> o1, Pair<Integer, T> o2) -> o1.getFirst().compareTo(o2.getFirst())));
  }
}
