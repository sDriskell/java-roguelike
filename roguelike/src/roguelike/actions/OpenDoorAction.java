package roguelike.actions;

import roguelike.Game;
import roguelike.actors.Actor;
import roguelike.maps.Door;
import roguelike.maps.Tile;

/**
 * 
 */
public class OpenDoorAction extends Action {

  private Tile tile;

  /**
   * 
   * @param argAct
   * @param argTile
   */
  public OpenDoorAction(Actor argAct, Tile argTile) {
    super(argAct);
    tile = argTile;
  }

  @Override
  protected ActionResult onPerform() {
    if (!(tile instanceof Door)) {
      return ActionResult.failure();
    }

    Door door = (Door) tile;
    door.open(Game.current().getCurrentMapArea());
    return ActionResult.success();
  }

}
