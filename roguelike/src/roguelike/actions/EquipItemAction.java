package roguelike.actions;

import roguelike.actors.Actor;
import roguelike.items.Equipment.ItemSlot;
import roguelike.items.Item;
import roguelike.items.ItemType;

/**
 * 
 */
public class EquipItemAction extends Action {

  private Item item;
  private ItemSlot itemSlot;

  /**
   * 
   * @param argAct
   * @param argItm
   * @param atgItmSlt
   */
  public EquipItemAction(Actor argAct, Item argItm, ItemSlot atgItmSlt) {
    super(argAct);
    item = argItm;
    itemSlot = atgItmSlt;
  }

  @Override
  protected ActionResult onPerform() {
    ActionResult res;

    if (item.type() == ItemType.RANGED_WEAPON) {
      ItemSlot.RANGED.equipItem(actor, item);
      res = ActionResult.success().setMessage("Equipped ranged weapon: " + item.getName());
    }
    else {
      itemSlot.equipItem(actor, item);
      res = ActionResult.success().setMessage("Selected item: " + item.getName());
    }

    return res;
  }

}
