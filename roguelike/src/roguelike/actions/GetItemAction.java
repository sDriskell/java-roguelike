package roguelike.actions;

import roguelike.Game;
import roguelike.actors.Actor;
import roguelike.items.Inventory;
import roguelike.items.Item;
import roguelike.maps.MapArea;
import squidpony.squidcolor.SColor;

/**
 * 
 */
public class GetItemAction extends Action {

  private MapArea map;
  private Item item;

  /**
   * 
   * @param argAct
   * @param argMap
   */
  public GetItemAction(Actor argAct, MapArea argMap) {
    super(argAct);
    map = argMap;
  }

  /**
   * 
   * @param argAct
   * @param argMap
   * @param argItem
   */
  public GetItemAction(Actor argAct, MapArea argMap, Item argItem) {
    super(argAct);
    this.map = argMap;
    this.item = argItem;
  }

  @Override
  protected ActionResult onPerform() {

    if (item != null) {
      Inventory inv = map.getItemsAt(actor.getPosition().x, actor.getPosition().y);
      Item pickUp = inv.getItem(this.item.itemId());
      return pickUpItem(inv, pickUp);
    }

    Inventory inv = map.getItemsAt(actor.getPosition().x, actor.getPosition().y);

    if (!inv.any()) {
      return ActionResult.failure().setMessage("No items here!");
    }

    // TODO: display menu allowing player to choose item
    Item firstItem = inv.getItem(inv.getCount() - 1);
    return pickUpItem(inv, firstItem);

  }

  /**
   * 
   * @param argInv
   * @param argPickUp
   * @return
   */
  private ActionResult pickUpItem(Inventory argInv, Item argPickUp) {

    if (argPickUp != null) {
      actor.inventory().add(argPickUp);
      argInv.remove(argPickUp);

      String msg = actor.doAction("picks up the %s", argPickUp.name());
      Game.current().displayMessage(msg, SColor.LIGHT_BLUE);

      return ActionResult.success();
    }
    return ActionResult.failure().setMessage("Selected item doesn't exist...");
  }
}
