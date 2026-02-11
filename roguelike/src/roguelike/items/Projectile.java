package roguelike.items;

import roguelike.Game;
import roguelike.actions.combat.Attack;
import roguelike.actions.combat.RangedAttack;
import roguelike.actors.Actor;
import roguelike.items.Equipment.ItemSlot;
import roguelike.util.Coordinate;
import squidpony.squidgrid.util.BasicRadiusStrategy;
import squidpony.squidutility.Pair;

/**
 * 
 */
public class Projectile extends Weapon {

  private static final long serialVersionUID = -5440292549086531435L;

  /**
   * 
   */
  protected Projectile() {
    super(true);
  }

  @Override
  public boolean canUse(Actor argUsr, Actor argTgt) {
    Coordinate userPos = argUsr.getPosition();
    Coordinate tgtPos = argTgt.getPosition();
    float dist = tgtPos.distance(userPos, BasicRadiusStrategy.CIRCLE);
    return dist <= 1;
  }

  @Override
  public ItemType type() {
    return ItemType.PROJECTILE;
  }

  @Override
  public boolean canEquip(ItemSlot argSlt) {
    return super.canEquip(argSlt) || argSlt == ItemSlot.PROJECTILE;
  }

  @Override
  public Projectile asProjectile() {
    return this;
  }

  @Override
  public Attack getAttack() {
    double rndFact = Game.current().random().nextDouble() * baseDamage;
    int totDmg = (int) (baseDamage + rndFact / 2);
    return new RangedAttack(attackDescription, totDmg, this);
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public void onEquipped(Actor argAct) {
  }

  @Override
  public void onRemoved(Actor argAct) {
  }

  @Override
  public Pair<Item, Boolean> onUsed() {
    return new Pair<>(this, true);
  }
}
