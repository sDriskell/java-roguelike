package roguelike.items;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import roguelike.actions.combat.Attack;
import roguelike.actions.combat.DamageType;
import roguelike.actions.combat.WeaponCategory;
import roguelike.actors.conditions.Condition;
import roguelike.functionalinterfaces.StatisticProvider;
import roguelike.items.Equipment.ItemSlot;

//TODO: replace i/o stream components

/**
 * 
 */
public abstract class Weapon extends Item {

  private static final long serialVersionUID = 1L;

  protected Map<DamageType, int[]> damage = new HashMap<>();
  protected WeaponCategory weaponCategory;

  protected DamageType defaultDamageType;
  protected int baseDamage;
  protected String description;
  protected String attackDescription;
  protected int defenseTargetNumber;

  protected Condition canCauseCondition;
  protected StatisticProvider defenseAgainstConditionStat;
  protected int attackSuccessesToCause;
  protected int attributeSuccessesToDefend;

  protected Material material;
  protected int durability;
  protected int reach;

  /**
   * 
   * @param argIsStackable
   */
  protected Weapon(boolean argIsStackable) {
    super(argIsStackable);
    damage.put(DamageType.SLASHING, new int[2]);
    damage.put(DamageType.PIERCING, new int[2]);
    damage.put(DamageType.BLUNT, new int[2]);
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
  }

  @Override
  public Weapon asWeapon() {
    return this;
  }

  @Override
  public boolean canEquip(ItemSlot argSlt) {
    return (argSlt == ItemSlot.LEFT_HAND || argSlt == ItemSlot.RIGHT_HAND);
  }

  /**
   * 
   * @return
   */
  public int reach() {
    return reach;
  }

  /**
   * 
   * @return
   */
  public WeaponCategory weaponType() {
    return this.weaponCategory;
  }

  /**
   * 
   * @return
   */
  public DamageType defaultDamageType() {
    return defaultDamageType;
  }

  /**
   * 
   * @param argType
   * @return
   */
  public int getTargetNumber(DamageType argType) {
    return damage.get(argType)[0];
  }

  /**
   * 
   * @return
   */
  public int getDefenseTargetNumber() {
    return defenseTargetNumber;
  }

  /**
   * 
   * @param argType
   * @return
   */
  public int getDamageRating(DamageType argType) {
    return damage.get(argType)[1];
  }

  /**
   * 
   * @return
   */
  public int getReachInTiles() {
    return (int) Math.max(1, Math.floor(reach / 2f));
  }

  /**
   * 
   * 
   * @return
   */
  public abstract Attack getAttack();
}
