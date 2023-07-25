package roguelike.items;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class Inventory implements Serializable {
    private static final long serialVersionUID = 2003563004618547276L;

    private List<Item> items;

    public Inventory() {
        items = new ArrayList<>();
    }

    public boolean any() {
        return !items.isEmpty();
    }

    public int getCount() {
        return items.size();
    }

    public Item getItem(int index) {
        return items.get(index);
    }

    public Item getItem(UUID itemId) {
        for (Item i : items)
            if (i.isSameItem(itemId)) {
                return i;
            }
        return null;
    }

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

    public void add(Item item) {
        if (item == null)
            throw new IllegalArgumentException("item cannot be null");

        items.add(item);

        items = ItemStack.getItemStack(items);
    }

    public boolean remove(Item item) {
        if (item == null)
            throw new IllegalArgumentException("item cannot be null");

        return items.remove(item);
    }

    public String[] getGroupedItemListAsText(int maxSize) {

        Map<Object, List<Item>> groupedItems =
                this.items.stream().collect(Collectors.groupingBy(Item::name));

        String[] itemArray = new String[Math.min(maxSize, groupedItems.size())];
        boolean displayEllipsis = false;
        if (maxSize < groupedItems.size()) {
            displayEllipsis = true;
        }
        Object[] keys = groupedItems.keySet().toArray();
        for (int i = 0; i < groupedItems.size(); i++) {
            int size = groupedItems.get(keys[i]).size();
            if (size == 1) {
                itemArray[i] = keys[i].toString();
            }
            else {
                itemArray[i] = keys[i].toString() + " (x" + size + ")";
            }
        }
        if (displayEllipsis)
            itemArray[itemArray.length - 1] =
                    String.format("(%d more)", groupedItems.size() - maxSize);

        return itemArray;
    }

    public String[] getItemListAsText(int maxSize) {
        boolean displayEllipsis = false;
        if (maxSize < this.items.size()) {
            displayEllipsis = true;
        }
        String[] itemArray = new String[Math.min(maxSize, this.items.size())];

        for (int i = 0; i < itemArray.length; i++) {
            itemArray[i] = this.items.get(i).name();
        }
        if (displayEllipsis)
            itemArray[itemArray.length - 1] =
                    String.format("(%d more)", this.items.size() - maxSize);

        return itemArray;
    }
}
