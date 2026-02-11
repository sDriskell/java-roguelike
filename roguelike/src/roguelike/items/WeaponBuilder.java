package roguelike.items;

import roguelike.actions.combat.DamageType;
import roguelike.actions.combat.WeaponCategory;
import roguelike.actors.conditions.Condition;
import roguelike.functionalinterfaces.StatisticProvider;
import roguelike.items.Equipment.ItemSlot;
import squidpony.squidcolor.SColor;

//TODO: A builder pattern might be a good approach here.  Trim down arg count for factory methods

/**
 * 
 */
public class WeaponBuilder extends ItemBuilder {

  private Weapon weapon;

  /**
   * 
   * @param argWpn
   */
  private WeaponBuilder(Weapon argWpn) {
    super(argWpn);
    weapon = argWpn;
  }

  /**
   * 
   * @return
   */
  private Weapon weapon() {
    return (Weapon) item;
  }

  /**
   * 
   * @param argName
   * @param argAtkDesc
   * @param argSym
   * @param argCol
   * @return
   */
  public static WeaponBuilder melee(String argName, String argAtkDesc, char argSym, SColor argCol) {
    Weapon wpn = new MeleeWeapon();

    wpn.name = argName;
    wpn.attackDescription = argAtkDesc;
    wpn.symbol = argSym;
    wpn.color = argCol;

    return new WeaponBuilder(wpn);
  }

  /**
   * 
   * @param argName
   * @param argAtkDesc
   * @param argSym
   * @param argCol
   * @param argMaxRange
   * @param argProjType
   * @return
   */
  public static WeaponBuilder ranged(String argName, String argAtkDesc, char argSym, SColor argCol,
      int argMaxRange, WeaponCategory argProjType) {
    RangedWeapon rwpn = new RangedWeapon();
    rwpn.name = argName;
    rwpn.attackDescription = argAtkDesc;
    rwpn.symbol = argSym;
    rwpn.color = argCol;
    rwpn.maxRange = argMaxRange;

    if (argProjType == null) {
      rwpn.projectileType = null;
      rwpn.requiresProjectiles = false;
    }
    else {
      rwpn.projectileType = argProjType;
      rwpn.requiresProjectiles = true;
    }

    return new WeaponBuilder(rwpn);
  }

  /**
   * 
   * @param argName
   * @param argAtkDesc
   * @param argSym
   * @param argCol
   * @return
   */
  public static WeaponBuilder projectile(String argName, String argAtkDesc, char argSym,
      SColor argCol) {
    Projectile proj = new Projectile();
    proj.name = argName;
    proj.attackDescription = argAtkDesc;
    proj.symbol = argSym;
    proj.color = argCol;

    return new WeaponBuilder(proj);
  }

  /**
   * 
   * @param argDesc
   * @return
   */
  public WeaponBuilder withDescription(String argDesc) {
    weapon.description = argDesc;
    return this;
  }

  /**
   * 
   * @param argCat
   * @return
   */
  public WeaponBuilder withCategory(WeaponCategory argCat) {
    weapon.weaponCategory = argCat;
    return this;
  }

  /**
   * 
   * @param argDmgType
   * @param argTgtNum
   * @param argDmgVal
   * @return
   */
  public WeaponBuilder withTargetNumberAndDamageValue(DamageType argDmgType, int argTgtNum,
      int argDmgVal) {
    weapon.damage.put(argDmgType, new int[] { argTgtNum, argDmgVal });
    return this;
  }

  /**
   * 
   * @param argTgtNum
   * @return
   */
  public WeaponBuilder withDefenseTargetNumber(int argTgtNum) {
    weapon.defenseTargetNumber = argTgtNum;
    return this;
  }

  /**
   * 
   * @param argCond
   * @param argAtkSucc
   * @param argStat
   * @param argAttrSucc
   * @return
   */
  public WeaponBuilder canCauseCondition(Condition argCond, int argAtkSucc,
      StatisticProvider argStat, int argAttrSucc) {
    weapon.canCauseCondition = argCond;
    weapon.attackSuccessesToCause = argAtkSucc;
    weapon.defenseAgainstConditionStat = argStat;
    weapon.attributeSuccessesToDefend = argAttrSucc;
    return this;
  }

  /**
   * 
   * @param argIsEquippable
   * @return
   */
  public WeaponBuilder canEquip(ItemSlot argIsEquippable) {
    weapon.equippable = argIsEquippable;
    return this;
  }

  /**
   * 
   * @param argReach
   * @return
   */
  public WeaponBuilder withReach(MeleeRange argReach) {
    weapon.reach = argReach.reach;
    return this;
  }

  /**
   * 
   * @param argRange
   * @return
   */
  public WeaponBuilder withRange(int argRange) {
    ((RangedWeapon) weapon).maxRange = argRange;
    return this;
  }

  /**
   * 
   * @param argDmgType
   * @return
   */
  public WeaponBuilder withDefaultDamageType(DamageType argDmgType) {
    weapon.defaultDamageType = argDmgType;
    return this;
  }

  @Override
  public WeaponBuilder withDroppable(boolean argIsDroppable) {
    return (WeaponBuilder) super.withDroppable(argIsDroppable);
  }

  @Override
  public WeaponBuilder withWeight(int argWt) {
    return (WeaponBuilder) super.withWeight(argWt);
  }

  /**
   * 
   * @return
   */
  public Weapon build() {
    /* validate */
    if (weapon.defaultDamageType == null)
      throw new RuntimeException("Default damage type cannot be null");
    if (weapon.weaponCategory == null)
      throw new RuntimeException("Weapon category cannot be null");

    return weapon;
  }
}
