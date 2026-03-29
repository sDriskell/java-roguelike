package roguelike.actors;

import java.util.List;

import roguelike.actions.Action;
import roguelike.actors.behaviors.Behavior;
import roguelike.items.Inventory;
import roguelike.items.Item;
import roguelike.maps.MapArea;
import squidpony.squidcolor.SColor;

/**
 * 
 */
public class Npc extends Actor {

  private static final long serialVersionUID = 1L;

  protected String name = "";
  protected String description = "";
  int difficulty = 1;

  /**
   * 
   * @param argSym
   * @param argCol
   * @param argName
   */
  Npc(char argSym, SColor argCol, String argName) {
    super(argSym, argCol);
    name = argName;
  }

  /**
   * 
   * @param argBehavior
   */
  public void setBehavior(Behavior argBehavior) {
    behavior = argBehavior;
  }

  @Override
  public Action getNextAction() {
    if (behavior != null) {
      return behavior.getAction();
    }
    return null;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public void onAttackedInternal(Actor argAtkr) {
    if (behavior != null)
      behavior = behavior.getNextBehavior();
  }

  @Override
  public void onTurnFinished() {
    if (behavior != null) {
      behavior = behavior.getNextBehavior();
    }
  }

  @Override
  public void onKilled() {
    behavior = null;

    // chance to drop whatever is in inventory
    Inventory inv = this.inventory();
    MapArea map = game.getCurrentMapArea();

    List<Item> droppableItems = inv.getDroppableItems();

    for (int x = 0; x < droppableItems.size(); x++) {
      // TODO: remove magic float in comparison
      if (game.random().nextFloat() < 0.7) {
        Item i = droppableItems.get(x);
        map.addItem(i, getPosition().x, getPosition().y);

        game.displayMessage("Dropped " + i.getName(), SColor.GREEN);
      }

    }
  }

  @Override
  protected String makeCorrectVerb(String argMap) {
    return argMap;
  }
}
