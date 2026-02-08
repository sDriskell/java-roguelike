package roguelike.actors.behaviors;

import roguelike.actors.Actor;
import roguelike.items.Weapon;
import roguelike.util.Coordinate;
import squidpony.squidgrid.util.BasicRadiusStrategy;

/**
 * 
 */
public abstract class EnemyBehavior extends Behavior {

  private static final long serialVersionUID = 1L;

  protected Behavior nextBehavior;

  /**
   * 
   * @param argAct
   */
  protected EnemyBehavior(Actor argAct) {
    super(argAct);
  }

  @Override
  public boolean isHostile() {
    return true;
  }

  /**
   * 
   * @param argDist
   * @return
   */
  protected boolean canAttackTarget(float argDist) {
    int range = 1;

    Weapon maxRange = actor.equipment().getEquippedWeapons().stream().max((w1, w2) -> {
      if (w1 == null || w2 == null) {
        return 0;
      }

      return Integer.compare(w1.reach(), w2.reach());
    }).orElse(null);

    if (maxRange != null) {
      range = maxRange.getReachInTiles();
    }

    return (argDist <= range);
  }

  /**
   * 
   * @param argTgt
   * @return
   */
  protected boolean canAttackTarget(Actor argTgt) {
    Coordinate actPos = this.actor.getPosition();
    Coordinate tgtPos = argTgt.getPosition();

    return canAttackTarget(tgtPos.distance(actPos, BasicRadiusStrategy.CIRCLE));
  }
}
