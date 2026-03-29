package roguelike.actors.behaviors;

import roguelike.Game;
import roguelike.actions.Action;
import roguelike.actions.WaitAction;
import roguelike.actions.WalkAction;
import roguelike.actors.Actor;
import roguelike.actors.AttackAttempt;
import roguelike.maps.MapArea;
import roguelike.util.Coordinate;
import roguelike.util.Log;
import squidpony.squidgrid.util.DirectionIntercardinal;

/**
 * 
 */
public class RandomWalkBehavior extends Behavior {

  private static final long serialVersionUID = 1L;

  private static final String WALKING_RANDOMLY = "Walking randomly";

  /**
   * 
   * @param argAct
   */
  public RandomWalkBehavior(Actor argAct) {
    super(argAct);
  }

  @Override
  public boolean isHostile() {
    return false;
  }

  @Override
  public Action getAction() {
    MapArea map = Game.current().getCurrentMapArea();
    double rnd = Game.current().random().nextDouble();
    DirectionIntercardinal direction;

    if (rnd < 0.25) {
      direction = DirectionIntercardinal.UP;
    }
    else if (rnd < 0.5) {
      direction = DirectionIntercardinal.LEFT;
    }
    else if (rnd < 0.75) {
      direction = DirectionIntercardinal.DOWN;
    }
    else {
      direction = DirectionIntercardinal.RIGHT;
    }

    Coordinate pos = actor.getPosition().createOffsetPosition(direction);

    if (map.canMoveTo(actor, pos) && map.getActorAt(pos.x, pos.y) == null) {
      return new WalkAction(actor, map, direction);
    }

    return new WaitAction(actor);
  }

  @Override
  public Behavior getNextBehavior() {
    AttackAttempt lastAtk = actor.getLastAttackedBy();

    if (lastAtk != null) {
      Log.debug("RandomWalkBehavior: Switching to targeted attack behavior");
      return new TargetedAttackBehavior(actor, lastAtk.getActor());
    }

    return this;
  }

  @Override
  public String getDescription() {
    return WALKING_RANDOMLY;
  }
}
