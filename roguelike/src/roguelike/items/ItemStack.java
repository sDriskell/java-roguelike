package roguelike.items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import roguelike.actors.Actor;
import roguelike.util.Log;
import squidpony.squidutility.Pair;

/**
 * 
 */
public class ItemStack extends Item {

  private static final long serialVersionUID = -3935638455081216485L;

  private List<Item> items;

  /**
   * 
   * @param argName
   * @param artItm
   */
  private ItemStack(String argName, Item artItm) {
    super(true);
    name = argName;
    items = new ArrayList<>();
    items.add(artItm);
  }

  /**
   * 
   * @param argName
   * @param argItms
   */
  private ItemStack(String argName, List<Item> argItms) {
    super(true);
    name = argName;
    items = new ArrayList<>(argItms);
  }

  /**
   * 
   * @param argItms
   * @return
   */
  public static List<Item> getItemStack(List<Item> argItms) {
    List<Item> stacks = new ArrayList<>();
    argItms.stream().filter(i -> !i.stackable).forEach(i -> stacks.add(i));

    Map<Object, List<Item>> groups = argItms.stream()
        .flatMap(
            i -> (i instanceof ItemStack ? ((ItemStack) i).unstack() : Arrays.asList(i)).stream())
        .filter(i -> i.stackable).collect(Collectors.groupingBy(i -> i.name));

    for (Object key : groups.keySet()) {
      List<Item> groupedItems = groups.get(key);
      stacks.add(new ItemStack(key.toString(), groupedItems));
    }

    Log.debug("Item stack count: " + stacks.size());
    return stacks;
  }

  // TODO: check to see if this leads to an out of bounds index
  @Override
  public Weapon asWeapon() {
    return items.isEmpty() ? null : items.get(0).as(Weapon.class);
  }

  // TODO: check to see if this leads to an out of bounds index
  @Override
  public Projectile asProjectile() {
    return items.isEmpty() ? null : items.get(0).asProjectile();
  }

  // TODO: check to see if this leads to an out of bounds index
  @Override
  public ItemType type() {
    return items.isEmpty() ? ItemType.UNDEFINED : items.get(0).type();
  }

  @Override
  public String getName() {
    return super.getName() + " x" + items.size();
  }

  @Override
  public String getDescription() {
    return items.isEmpty() ? "empty" : items.get(0).getDescription();
  }

  @Override
  public boolean isSameItem(UUID otherId) {
    if (this.getItemId().equals(otherId)) {
      return true;
    }

    for (Item i : items) {
      if (i.isSameItem(otherId)) {
        return true;
      }

      Log.debug("i.id=" + i.getItemId() + ", other.id=" + otherId);
    }
    return false;
  }

  // TODO: check to see if this leads to an out of bounds index
  @Override
  public Pair<Item, Boolean> onUsed() {
    return items.isEmpty() ? null : new Pair<>(items.remove(items.size() - 1), items.isEmpty());
  }

  /**
   * 
   * @return
   */
  public List<Item> unstack() {
    return items;
  }

  // TODO: check to see if this leads to an out of bounds index
  @Override
  public boolean canUse(Actor argUsr, Actor argTgt) {
    return items.isEmpty() ? false : items.get(0).canUse(argUsr, argTgt);
  }
}
