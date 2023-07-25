package roguelike.items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import roguelike.actors.Actor;
import squidpony.squidutility.Pair;

public class ItemStack extends Item {
    private static final Logger LOG = LogManager.getLogger(ItemStack.class);

    private static final long serialVersionUID = -3935638455081216485L;

    private List<Item> items;

    private ItemStack(String name, Item item) {
        super(true);
        this.name = name;
        this.items = new ArrayList<>();

        items.add(item);
    }

    private ItemStack(String name, List<Item> items) {
        super(true);
        this.name = name;
        this.items = new ArrayList<>(items);
    }

    public static List<Item> getItemStack(List<Item> items) {
        List<Item> stacks = new ArrayList<>();

        items.stream().filter(i -> !i.stackable).forEach(stacks::add);

        Map<Object, List<Item>> groups = items.stream()
                .flatMap(
                        i -> (i instanceof ItemStack ? ((ItemStack) i).unstack() : Arrays.asList(i))
                                .stream())
                .filter(i -> i.stackable).collect(Collectors.groupingBy(i -> i.name));

        for (Object key : groups.keySet()) {
            List<Item> groupedItems = groups.get(key);

            stacks.add(new ItemStack(key.toString(), groupedItems));
        }
        LOG.debug("Item stack count: {}", stacks.size());
        return stacks;
    }

    @Override
    public Weapon asWeapon() {
        if (items.isEmpty()) {
            return null;
        }
        return items.get(0).as(Weapon.class);
    }

    @Override
    public Projectile asProjectile() {
        if (items.isEmpty()) {
            return null;
        }
        return items.get(0).asProjectile();
    }

    @Override
    public ItemType type() {
        if (items.isEmpty()) {
            return ItemType.UNDEFINED;
        }
        return items.get(0).type();
    }

    @Override
    public String name() {
        return super.name() + " x" + items.size();
    }

    @Override
    public String getDescription() {
        if (items.isEmpty()) {
            return "empty";
        }
        return items.get(0).getDescription();
    }

    @Override
    public boolean isSameItem(UUID otherId) {
        if (this.itemId().equals(otherId))
            return true;

        for (Item i : items) {
            if (i.isSameItem(otherId))
                return true;
            LOG.debug("i.id = {}, other.id = {}", i.itemId(), otherId);
        }
        return false;
    }

    @Override
    public Pair<Item, Boolean> onUsed() {
        if (items.isEmpty()) {
            return null;
        }
        return new Pair<>(items.remove(items.size() - 1), items.isEmpty());
    }

    public List<Item> unstack() {
        return items;
    }

    @Override
    public boolean canUse(Actor user, Actor target) {
        if (items.isEmpty()) {
            return false;
        }
        return items.get(0).canUse(user, target);
    }
}
