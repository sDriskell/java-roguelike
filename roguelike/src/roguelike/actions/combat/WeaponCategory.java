package roguelike.actions.combat;

/**
 * 
 */
public enum WeaponCategory {
  SWORD,
  AXE,
  DAGGER,
  CLUB,
  SPEAR,
  POLEARM,
  BOW,
  THROWN,
  GUN,
  ARROW,
  BULLET,

  NATURAL, // for monsters, beasts, etc
  DEFAULT;

  /**
   * 
   * @param argCat
   * @return
   */
  public static WeaponCategory fromString(String argCat) {
    return WeaponCategory.valueOf(argCat.toUpperCase());
  }
}
