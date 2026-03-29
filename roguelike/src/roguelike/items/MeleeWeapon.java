package roguelike.items;

import roguelike.Game;
import roguelike.actions.combat.Attack;
import roguelike.actions.combat.MeleeAttack;
import roguelike.actors.Actor;
import roguelike.util.Coordinate;
import squidpony.squidgrid.util.BasicRadiusStrategy;

/**
 * 
 */
public class MeleeWeapon extends Weapon {

  private static final long serialVersionUID = 682348343481995648L;

  /**
   * 
   */
  protected MeleeWeapon() {
    super(false);
  }

  @Override
  public Attack getAttack() {
    double rndFact = Game.current().random().nextDouble() * baseDamage;
    int totDmg = (int) (baseDamage + rndFact / 2);

    return new MeleeAttack(attackDescription, totDmg, this);
  }

  @Override
  public ItemType type() {
    return ItemType.WEAPON;
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
  public boolean canUse(Actor argUsr, Actor argTgt) {
    Coordinate userPos = argUsr.getPosition();
    Coordinate tgtPos = argTgt.getPosition();
    float dist = tgtPos.distance(userPos, BasicRadiusStrategy.CIRCLE);
    return dist <= getReachInTiles();
  }
}
