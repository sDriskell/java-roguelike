package roguelike.items;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import roguelike.actors.Actor;
import roguelike.util.Log;

/**
 * 
 */
public class Equipment implements Serializable {

  private static final long serialVersionUID = 1006420730103267096L;

  /**
   * 
   */
  public enum ItemSlot {
    HEAD(1 << 0),
    TORSO(1 << 1),
    LEFT_HAND(1 << 2),
    RIGHT_HAND(1 << 3),
    LEGS(1 << 4),
    RANGED(1 << 5),
    PROJECTILE(1 << 6),
    HANDS(LEFT_HAND.value & RIGHT_HAND.value);

    public final int value;

    /**
     * 
     * @param argVal
     */
    ItemSlot(int argVal) {
      value = argVal;
    }

    /**
     * 
     * @param argAct
     * @return
     */
    public Item getItem(Actor argAct) {
      return argAct.equipment().getEquipped(this);
    }

    /**
     * 
     * @param argAct
     * @return
     */
    public Weapon getEquippedWeapon(Actor argAct) {
      return argAct.equipment().getEquippedWeapon(this);
    }

    /**
     * 
     * @param argAct
     * @param argItm
     * @return
     */
    public Item equipItem(Actor argAct, Item argItm) {
      Item old = argAct.equipment().equipItem(this, argItm, argAct.inventory());

      if (old != null) {
        old.onRemoved(argAct);
      }

      argItm.onEquipped(argAct);
      return old;
    }

    /**
     * 
     * @param argAct
     * @return
     */
    public Item removeItem(Actor argAct) {
      Item item = argAct.equipment().removeItem(this, argAct.inventory());

      if (item != null) {
        item.onRemoved(argAct);
      }

      return item;
    }

  }

  private HashMap<ItemSlot, Item> equipped;
  private ArrayList<Weapon> equippedWeapons = new ArrayList<>();

  /**
   * 
   */
  public Equipment() {
    equipped = new HashMap<>();
    equippedWeapons.add(null);
    equippedWeapons.add(null);
  }

  /**
   * 
   * @return
   */
  public List<Weapon> getEquippedWeapons() {
    Weapon right = getEquippedWeapon(ItemSlot.RIGHT_HAND);
    Weapon left = getEquippedWeapon(ItemSlot.LEFT_HAND);

    equippedWeapons.set(0, right);
    equippedWeapons.set(1, left);

    return equippedWeapons;
  }

  /**
   * -
   * 
   * @return
   */
  public RangedWeapon getRangedWeapon() {
    Weapon wpn = getEquippedWeapon(ItemSlot.RANGED);

    if (wpn != null && wpn.type() == ItemType.RANGED_WEAPON) {
      return (RangedWeapon) wpn;
    }

    return null;
  }

  /**
   * 
   * @param argSlt
   * @param argItm
   * @param argInv
   * @return
   */
  Item equipItem(ItemSlot argSlt, Item argItm, Inventory argInv) {
    Item oldItem = equipped.put(argSlt, argItm);
    Item existingItem = argInv.getItem(argItm.getItemId());

    if (existingItem == null) {
      Log.debug("equipItem: Existing item=null, adding " + argItm.getItemId());
      argInv.add(argItm);
    }

    // TODO: add equipped indicator to items

    return oldItem;
  }

  /**
   * 
   * @param argSlt
   * @param argInv
   * @return
   */
  Item removeItem(ItemSlot argSlt, Inventory argInv) {
    Item item = equipped.getOrDefault(argSlt, null);
    equipped.put(argSlt, null);
    return item;
  }

  /**
   * 
   * @param argSlt
   * @return
   */
  Item getEquipped(ItemSlot argSlt) {
    return equipped.getOrDefault(argSlt, null);
  }

  /**
   * 
   * @param argSlt
   * @return
   */
  Weapon getEquippedWeapon(ItemSlot argSlt) {
    if (argSlt == ItemSlot.RIGHT_HAND || argSlt == ItemSlot.LEFT_HAND || argSlt == ItemSlot.RANGED
        || argSlt == ItemSlot.PROJECTILE) {
      Item wpn = equipped.getOrDefault(argSlt, null);

      if (wpn != null) {
        if (wpn.type() == ItemType.WEAPON || wpn.type() == ItemType.RANGED_WEAPON) {
          return wpn.asWeapon();
        }
        else if (wpn.type() == ItemType.PROJECTILE) {
          return wpn.asProjectile();
        }
      }
    }

    return null;
  }
}
