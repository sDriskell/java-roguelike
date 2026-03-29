package roguelike.items;

import java.io.Serializable;
import java.util.UUID;

import roguelike.actors.Actor;
import roguelike.items.Equipment.ItemSlot;
import squidpony.squidcolor.SColor;
import squidpony.squidutility.Pair;

/**
 * 
 */
public abstract class Item implements Serializable {

  private static final long serialVersionUID = 1L;

  private UUID itemId = UUID.randomUUID();

  protected String name;
  protected char symbol = '?';
  protected SColor color = SColor.WHITE;
  protected int weight;
  protected boolean droppable;
  protected ItemSlot equippable;

  final boolean stackable;

  /**
   * 
   * @param argIsStackable
   */
  protected Item(boolean argIsStackable) {
    droppable = true;
    stackable = argIsStackable;
  }

  /**
   * 
   * @return
   */
  public final UUID getItemId() {
    return itemId;
  }

  /**
   * 
   * @param argId
   * @return
   */
  public boolean isSameItem(UUID argId) {
    return getItemId() == argId;
  }

  /**
   * 
   * @return
   */
  public abstract ItemType type();

  /**
   * 
   * @return
   */
  public String getName() {
    return name == null ? "???" : name;
  }

  /**
   * 
   * @return
   */
  public char getSymbol() {
    return symbol;
  }

  /**
   * 
   * @return
   */
  public SColor getColor() {
    return color;
  }

  /**
   * 
   * @return
   */
  public int getWeight() {
    return weight;
  }

  /**
   * 
   * @return
   */
  public abstract String getDescription();

  /**
   * 
   * @return
   */
  boolean isDroppable() {
    return droppable;
  }

  // TODO: do something with this
  /**
   * Methods to cast to a specific subtype
   * 
   * @return
   */
  public Weapon asWeapon() {
    return null;
  }

  // TODO: do something with this
  /**
   * 
   * @return
   */
  public Projectile asProjectile() {
    return null;
  }

  /**
   * 
   * @param <T>
   * @param argType
   * @return
   */
  @SuppressWarnings("unchecked")
  public <T> T as(Class<T> argType) {
    return (T) this;
  }

  /**
   * 
   * @param argSlt
   * @return
   */
  public boolean canEquip(ItemSlot argSlt) {
    return (equippable == null || (argSlt.value & equippable.value) == argSlt.value) ? true : false;
  }

  /**
   * 
   * @param argUser
   * @param argTgt
   * @return
   */
  public abstract boolean canUse(Actor argUser, Actor argTgt);

  /**
   * Called when the item is used, returns an item that is the result of using
   * this one. If null, the item should be removed from inventory
   * 
   * @return
   */
  public Pair<Item, Boolean> onUsed() {
    return new Pair<>(this, false);
  }

  /**
   * 
   * @param argAct
   */
  public void onEquipped(Actor argAct) {
  }

  /**
   * |
   * 
   * @param argAct
   */
  public void onRemoved(Actor argAct) {
  }

  /**
   * 
   * @param argAct
   * @param argTgt
   */
  public void onThrown(Actor argAct, Actor argTgt) {
  }

  /**
   * 
   * @param argAct
   * @param argTgt
   */
  public void onAttacked(Actor argAct, Actor argTgt) {
  }

  /**
   * 
   * @param argAct
   * @param argTgt
   */
  public void onAttacking(Actor argAct, Actor argTgt) {
  }
}
