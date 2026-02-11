package roguelike.items;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 
 */
public class Inventory implements Serializable {

  private static final long serialVersionUID = 2003563004618547276L;

  private List<Item> items;

  /**
   * 
   */
  public Inventory() {
    items = new ArrayList<>();
  }

  /**
   * 
   * @return
   */
  public boolean any() {
    // TODO: this method might require a size compare for other purposes
    return items != null && items.isEmpty() && items.size() > 0;
  }

  /**
   * 
   * @return
   */
  public int getCount() {
    return items.size();
  }

  /**
   * 
   * @param argIdx
   * @return
   */
  public Item getItem(int argIdx) {
    return items.get(argIdx);
  }

  /**
   * 
   * @param argItmId
   * @return
   */
  public Item getItem(UUID argItmId) {
    for (Item i : items) {
      if (i.isSameItem(argItmId)) {
        return i;
      }
    }

    return null;
  }

  /**
   * 
   * @return
   */
  public List<Item> getDroppableItems() {
    ArrayList<Item> droppable = new ArrayList<>();

    for (Item i : items) {
      if (i.isDroppable()) {
        droppable.add(i);
      }
    }
    return droppable;
  }

  public List<Item> allItems() {
    return items;
  }

  /**
   * 
   * @param argItm
   */
  public void add(Item argItm) {
    if (argItm == null) {
      throw new IllegalArgumentException("item cannot be null");
    }

    items.add(argItm);
    items = ItemStack.getItemStack(items);
  }

  /**
   * 
   * @param argItm
   * @return
   */
  public boolean remove(Item argItm) {
    if (argItm == null) {
      throw new IllegalArgumentException("item cannot be null");
    }

    return items.remove(argItm);
  }

  /**
   * 
   * @param argMaxSize
   * @return
   */
  public String[] getGroupedItemListAsText(int argMaxSize) {
    Map<Object, List<Item>> groupedItems = this.items.stream()
        .collect(Collectors.groupingBy(i -> i.getName()));

    String[] items = new String[Math.min(argMaxSize, groupedItems.size())];
    boolean displayEllipsis = false;

    if (argMaxSize < groupedItems.size()) {
      displayEllipsis = true;
    }

    Object[] keys = groupedItems.keySet().toArray();

    for (int i = 0; i < groupedItems.size(); i++) {
      int size = groupedItems.get(keys[i]).size();

      if (size == 1) {
        items[i] = keys[i].toString();
      }
      else {
        items[i] = keys[i].toString() + " (x" + size + ")";
      }
    }

    if (displayEllipsis) {
      items[items.length - 1] = String.format("(%d more)", groupedItems.size() - argMaxSize);
    }

    return items;
  }

  /**
   * 
   * @param argMaxSize
   * @return
   */
  public String[] getItemListAsText(int argMaxSize) {
    boolean displayEllipsis = false;

    if (argMaxSize < this.items.size()) {
      displayEllipsis = true;
    }

    String[] items = new String[Math.min(argMaxSize, this.items.size())];

    for (int i = 0; i < items.length; i++) {
      items[i] = this.items.get(i).getName();
    }

    if (displayEllipsis) {
      items[items.length - 1] = String.format("(%d more)", this.items.size() - argMaxSize);
    }

    return items;
  }
}
