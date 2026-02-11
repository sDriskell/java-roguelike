package roguelike.items;

import roguelike.ui.Menu;
import roguelike.util.StringEx;

/**
 * 
 */
public class InventoryMenu extends Menu<Item> {

  /**
   * 
   * @param argInv
   */
  public InventoryMenu(Inventory argInv) {
    super(argInv.allItems(), 26);
  }

  @Override
  protected StringEx getTextFor(Item item, int position) {
    String txtLn = String.format("%-40s %12d", item.getDescription(), 111);
    return new StringEx(getCharForIndex(position) + ") " + txtLn);
  }
}
