package roguelike.actors.behaviors;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import roguelike.Game;
import roguelike.actions.Action;
import roguelike.actions.AttackAction;
import roguelike.actors.Actor;
import roguelike.actors.Player;
import roguelike.util.Coordinate;
import squidpony.squidcolor.SColor;
import squidpony.squidgrid.fov.BasicRadiusStrategy;
import squidpony.squidgrid.fov.RadiusStrategy;

/**
 * 
 */
public class TargetedAttackBehavior extends EnemyBehavior {

  private static final long serialVersionUID = 1L;

  private Actor target;
  private transient RadiusStrategy radiusStrategy = BasicRadiusStrategy.CIRCLE;

  /**
   * 
   * @param argAct
   * @param argTgt
   */
  protected TargetedAttackBehavior(Actor argAct, Actor argTgt) {
    super(argAct);

    Game.current().displayMessage(argAct.getName() + " is now attacking " + argTgt.getName(),
        SColor.PURPLE);

    target = argTgt;
    nextBehavior = this;
  }

  /**
   * 
   * @param out
   * @throws IOException
   */
  private void writeObject(ObjectOutputStream out) throws IOException {
    out.defaultWriteObject();
  }

  /**
   * 
   * @param in
   * @throws IOException
   * @throws ClassNotFoundException
   */
  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    in.defaultReadObject();
    radiusStrategy = BasicRadiusStrategy.CIRCLE;
  }

  @Override
  public boolean isHostile() {
    return Player.isPlayer(target);
  }

  @Override
  public Action getAction() {
    Coordinate actPos = actor.getPosition();
    Coordinate tgtPos = target.getPosition();

    float rad = radiusStrategy.radius(actPos.x, actPos.y, tgtPos.x, tgtPos.y);

    if (canAttackTarget(rad)) {
      if (actor.canSee(target, Game.current().getCurrentMapArea())) {
        nextBehavior = this;
        return new AttackAction(actor, target);
      }
      else {
        Game.current().displayMessage(target.getName() + " is no longer in sight range.");
      }
    }
    else if (target.isAlive() && isTargetVisible()) {
      if (actor.equipment().getEquippedWeapons().stream().filter(w -> w != null)
          .findAny() != null) {
        nextBehavior = this;
      }
    }

    // if we can't attack, switch behavior to searching for the player
    nextBehavior = new SearchForPlayerBehavior(actor);
    return nextBehavior.getAction();
  }

  @Override
  public Behavior getNextBehavior() {
    if (actor.isAlive()) {
      return nextBehavior;
    }

    return null;
  }

  /**
   * 
   * @return
   */
  private boolean isTargetVisible() {
    Coordinate actorPos = actor.getPosition();
    Coordinate targetPos = target.getPosition();
    float rad = radiusStrategy.radius(actorPos.x, actorPos.y, targetPos.x, targetPos.y);

    if (rad <= actor.getVisionRadius()) {
      return actor.canSee(target, Game.current().getCurrentMapArea());
    }

    return false;
  }

  @Override
  public String getDescription() {
    return "Attacking " + target.getMessageName();
  }
}
