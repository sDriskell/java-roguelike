package roguelike.actions;

import java.awt.Point;

import roguelike.actors.Actor;
import roguelike.maps.MapArea;
import roguelike.maps.Stairs;
import roguelike.maps.Tile;

/**
 * 
 */
public class StairsDownAction extends Action {

  private MapArea map;

  /**
   * 
   * @param argAct
   * @param argMap
   */
  public StairsDownAction(Actor argAct, MapArea argMap) {
    super(argAct);
    map = argMap;
  }

  @Override
  protected ActionResult onPerform() {
    Point actPos = actor.getPosition();
    Tile t = map.getTileAt(actPos);

    if (!(t instanceof Stairs)) {
      return ActionResult.failure().setMessage("No stairs");
    }

    Stairs stairs = (Stairs) t;

    if (stairs.isDown()) {
      stairs.use();
      return ActionResult.success().setMessage(actor.doAction("walks down the stairs."));
    }
    else {
      return ActionResult.failure().setMessage("No stairs down.");
    }
  }

}
