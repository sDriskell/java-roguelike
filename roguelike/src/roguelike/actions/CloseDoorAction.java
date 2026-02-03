package roguelike.actions;

import roguelike.Game;
import roguelike.actors.Actor;
import roguelike.maps.Door;
import roguelike.maps.MapArea;
import roguelike.maps.Tile;
import roguelike.ui.InputManager;
import roguelike.util.Coordinate;
import squidpony.squidgrid.util.DirectionIntercardinal;

/**
 * 
 */
public class CloseDoorAction extends Action {

  private DirectionIntercardinal direction;
  private MapArea map;

  /**
   * 
   * @param argAct
   * @param argMap
   */
  public CloseDoorAction(Actor argAct, MapArea argMap) {
    super(argAct);
    direction = DirectionIntercardinal.NONE;
    map = argMap;
    Game.current().displayMessage("Direction?");
  }

  /**
   * 
   * @param argAct
   * @param argMap
   * @param argDirection
   */
  public CloseDoorAction(Actor argAct, MapArea argMap, DirectionIntercardinal argDirection) {
    super(argAct);
    map = argMap;
    direction = argDirection;
  }

  @Override
  protected ActionResult onPerform() {

    if (direction == DirectionIntercardinal.NONE || direction == null) {
      direction = InputManager.nextDirection();

      if (direction == null) {
        return ActionResult.incomplete();
      }

      if (direction == DirectionIntercardinal.NONE) {
        return ActionResult.failure().setMessage("Invalid direction");
      }
    }

    Coordinate pos = actor.getPosition().createOffsetPosition(direction);
    Tile tile = map.getTileAt(pos);

    if (tile instanceof Door) {
      ((Door) tile).close(map);
      return ActionResult.success();
    }
    else {
      return ActionResult.failure().setMessage("No door in that direction");
    }
  }
}
