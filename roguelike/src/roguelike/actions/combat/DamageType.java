package roguelike.actions.combat;

import roguelike.items.Weapon;

/**
 * 
 */
public enum DamageType {
  SLASHING,
  PIERCING,
  BLUNT;

  /**
   * 
   * @param argWpn
   * @return
   */
  public int getTargetNumber(Weapon argWpn) {
    return argWpn.getTargetNumber(this);
  }

  /**
   * 
   * @param argWpn
   * @return
   */
  public int getDamageRating(Weapon argWpn) {
    return argWpn.getDamageRating(this);
  }
}
