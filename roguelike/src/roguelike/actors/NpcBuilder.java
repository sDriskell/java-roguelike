package roguelike.actors;

import roguelike.functionalinterfaces.BehaviorProvider;
import roguelike.items.Equipment.ItemSlot;
import roguelike.items.Inventory;
import roguelike.items.Item;
import squidpony.squidcolor.SColor;

/**
 * 
 */
public class NpcBuilder {

  private Npc npc;

  /**
   * 
   * @param argName
   * @param argSym
   * @param argCol
   */
  private NpcBuilder(String argName, char argSym, SColor argCol) {
    npc = new Npc(argSym, argCol, argName);
  }

  /**
   * 
   * @return
   */
  public Npc buildNpc() {
    return npc;
  }

  /**
   * 
   * @param argName
   * @param argSym
   * @param argCol
   * @param argDif
   * @return
   */
  public static NpcBuilder withIdentifiers(String argName, char argSym, SColor argCol, int argDif) {
    NpcBuilder nb = new NpcBuilder(argName, argSym, argCol);
    nb.npc.difficulty = argDif;
    return nb;
  }

  /**
   * 
   * @param argDesc
   * @return
   */
  public NpcBuilder withDescription(String argDesc) {
    npc.description = argDesc;
    return this;
  }

  /**
   * 
   * @param argVisRad
   * @return
   */
  public NpcBuilder withVisionRadius(int argVisRad) {
    npc.visionRadius = argVisRad;
    return this;
  }

  /**
   * 
   * @param argBehavior
   * @return
   */
  public NpcBuilder withBehavior(BehaviorProvider argBehavior) {
    npc.behavior = argBehavior.create(npc);
    return this;
  }

  /**
   * 
   * @param argInv
   * @return
   */
  public NpcBuilder withInventory(Inventory argInv) {
    npc.inventory.allItems().addAll(argInv.allItems());
    return this;
  }

  /**
   * 
   * @param argItm
   * @return
   */
  public NpcBuilder addItem(Item argItm) {
    npc.inventory.add(argItm);
    return this;
  }

  /**
   * 
   * @param argItm
   * @param argSlot
   * @return
   */
  public NpcBuilder equipItem(Item argItm, ItemSlot argSlot) {
    argSlot.equipItem(npc, argItm);
    return this;
  }

  /**
   * 
   * @param argSpd
   * @return
   */
  public NpcBuilder withSpeed(int argSpd) {
    npc.statistics.speed.setBase(argSpd);
    return this;
  }

  /**
   * 
   * @param argTgh
   * @param argCond
   * @param argPerc
   * @param argAgi
   * @param argWill
   * @param argPres
   * @return
   */
  public NpcBuilder withStats(int argTgh, int argCond, int argPerc, int argAgi, int argWill,
      int argPres) {
    npc.statistics.toughness.setBase(argTgh);
    npc.statistics.conditioning.setBase(argCond);
    npc.statistics.perception.setBase(argPerc);
    npc.statistics.agility.setBase(argAgi);
    npc.statistics.willpower.setBase(argWill);
    npc.statistics.presence.setBase(argPres);

    return this;
  }

  /**
   * 
   * @param argHlth
   * @return
   */
  public NpcBuilder withHealth(int argHlth) {
    npc.health.setMaximum(argHlth, true);
    return this;
  }

  /**
   * 
   * @param argRlxBonus
   * @return
   */
  public NpcBuilder withReflexBonus(int argRlxBonus) {
    npc.statistics.reflexBonus = argRlxBonus;
    return this;
  }

  /**
   * 
   * @param argAimBonus
   * @return
   */
  public NpcBuilder withAimingBonus(int argAimBonus) {
    npc.statistics.aimingBonus = argAimBonus;
    return this;
  }
}
