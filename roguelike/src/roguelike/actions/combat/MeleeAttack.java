package roguelike.actions.combat;

import roguelike.actions.Action;
import roguelike.actors.Actor;
import roguelike.items.Weapon;

/**
 * 
 */
public class MeleeAttack extends Attack {

  /**
   * 
   * @param argDesc
   * @param argBaseDmg
   * @param argWpn
   */
  public MeleeAttack(String argDesc, int argBaseDmg, Weapon argWpn) {
    super(argDesc, argBaseDmg, argWpn);
  }

  @Override
  public boolean onPerform(Action argAct, Actor argTgt) {
    return argAct.getActor().combatHandler().processAttack(argAct, argTgt, this);
  }

}
