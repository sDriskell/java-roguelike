package roguelike.actions.combat;

import roguelike.actions.Action;
import roguelike.actors.Actor;
import roguelike.items.Projectile;

/**
 * 
 */
public class RangedAttack extends Attack {

  /**
   * 
   * @param argDesc
   * @param argBaseDmg
   * @param argWpn
   */
  public RangedAttack(String argDesc, int argBaseDmg, Projectile argWpn) {
    super(argDesc, argBaseDmg, argWpn);
  }

  @Override
  public boolean onPerform(Action argAct, Actor argTgt) {
    return argAct.getActor().combatHandler().processAttack(argAct, argTgt, this);
  }

}
