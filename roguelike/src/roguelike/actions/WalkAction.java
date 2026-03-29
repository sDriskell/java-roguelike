package roguelike.actions;

import roguelike.actors.Actor;
import roguelike.maps.Door;
import roguelike.maps.MapArea;
import roguelike.maps.Tile;
import roguelike.util.Coordinate;
import roguelike.util.Log;
import squidpony.squidgrid.util.DirectionIntercardinal;

/**
 * 
 */
public class WalkAction extends Action {

  private MapArea mapArea;
  private DirectionIntercardinal direction;
  private boolean canOpenDoors;

  /**
   * 
   * @param argAct
   * @param argMap
   * @param argDir
   */
  public WalkAction(Actor argAct, MapArea argMap, DirectionIntercardinal argDir) {
    this(argAct, argMap, argDir, true);
  }

  /**
   * 
   * @param argAct
   * @param argMap
   * @param argDir
   * @param argCanOpenDoors
   */
  public WalkAction(Actor argAct, MapArea argMap, DirectionIntercardinal argDir,
      boolean argCanOpenDoors) {
    super(argAct);
    mapArea = argMap;
    direction = argDir;
    canOpenDoors = argCanOpenDoors;
  }

  @Override
  public ActionResult onPerform() {
    if (!actor.isAlive()) {
      Log.warning(">>> Actor is dead! " + actor.getName());
      return ActionResult.success().setMessage("Actor is dead");
    }

    if (direction.deltaX == 0 && direction.deltaY == 0) {
      return ActionResult.alternate(new FailAction(actor));
    }
    Coordinate pos = actor.getPosition().createOffsetPosition(direction);

    if (mapArea.getActorAt(pos.x, pos.y) != null) {
      // TODO: behavior.getAttackAction() - allow player to confirm, allow
      // npc's to attack or not depending on behavior

      return ActionResult.alternate(new AttackAction(actor, mapArea.getActorAt(pos.x, pos.y)));
    }

    if (!mapArea.canMoveTo(actor, pos)) {
      Tile tile = mapArea.getTileAt(pos);

      if (tile instanceof Door && canOpenDoors)
        return ActionResult.alternate(new OpenDoorAction(actor, tile));

      return ActionResult.failure().setMessage(actor.doAction("can't move there"));
    }

    // we can move here
    mapArea.moveActor(actor, pos);

    return ActionResult.success();
  }
}
