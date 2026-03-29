package roguelike.items;

import roguelike.actions.combat.Attack;
import roguelike.actions.combat.NoAmmunitionAttack;
import roguelike.actions.combat.WeaponCategory;
import roguelike.actors.Actor;
import roguelike.items.Equipment.ItemSlot;
import roguelike.util.Coordinate;
import squidpony.squidgrid.util.BasicRadiusStrategy;
import squidpony.squidutility.Pair;

/**
 * 
 */
public class RangedWeapon extends Weapon {

  private static final long serialVersionUID = -340930226160562519L;

  protected int maxRange;
  protected boolean requiresProjectiles;
  protected WeaponCategory projectileType;
  protected int remainingAmmunition;

  protected Actor owner;

  /**
   * 
   */
  protected RangedWeapon() {
    super(false);
  }

  /**
   * 
   * @return
   */
  public int range() {
    return maxRange;
  }

  @Override
  public Weapon asWeapon() {
    return this;
  }

  @Override
  public ItemType type() {
    return ItemType.RANGED_WEAPON;
  }

  @Override
  public boolean canEquip(ItemSlot argSlt) {
    return argSlt == ItemSlot.RANGED;
  }

  @Override
  public Attack getAttack() {
    if (requiresProjectiles) {
      Projectile proj = getProjectile();

      if (proj != null) {
        return proj.getAttack();
      }
    }

    return new NoAmmunitionAttack(0, this);
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public String getName() {
    return name;
  }

  public WeaponType ammunitionType() {
    return WeaponType.ARROW;
  }

  @Override
  public void onEquipped(Actor argAct) {
    owner = argAct;
  }

  @Override
  public void onRemoved(Actor argAct) {
    owner = null;
    argAct.doAction("removed the %s", this.name);
  }

  @Override
  public boolean canUse(Actor argUsr, Actor argTgt) {
    Coordinate userPos = argUsr.getPosition();
    Coordinate tgtPos = argTgt.getPosition();

    float dist = tgtPos.distance(userPos, BasicRadiusStrategy.CIRCLE);
    return dist <= this.maxRange;
  }

  /**
   * 
   * @return
   */
  public Projectile getProjectile() {
    if (owner == null) {
      return null;
    }

    Item projectile = ItemSlot.PROJECTILE.getItem(owner);

    if (projectile != null) {
      Pair<Item, Boolean> used = projectile.onUsed();

      if (used != null) {
        if (used.getSecond().booleanValue()) {
          // TODO: update this to decrement the count instead of setting to null
          owner.inventory().remove(projectile);
          ItemSlot.PROJECTILE.removeItem(owner);
        }

        return (Projectile) used.getFirst();
      }
    }

    return null;
  }
}
