package roguelike.actions.combat;

import roguelike.Game;
import roguelike.actions.Action;
import roguelike.actors.Actor;
import roguelike.items.RangedWeapon;
import squidpony.squidcolor.SColor;

/**
 * 
 */
public class NoAmmunitionAttack extends Attack {

  /**
   * 
   * @param argBaseDmg
   * @param argWpn
   */
  public NoAmmunitionAttack(int argBaseDmg, RangedWeapon argWpn) {
    super(
        "does not have any " + argWpn.ammunitionType().name + "s ready with which to shoot the %s",
        argBaseDmg, argWpn);
  }

  @Override
  protected boolean onPerform(Action argAct, Actor argTgt) {
    Actor act = argAct.getActor();
    act.behavior().onNoAmmunition();

    Game.current().displayMessage(
        act.doAction("has no %ss readied.", ((RangedWeapon) weapon).ammunitionType().name),
        SColor.GRAPE_MOUSE);

    return false;
  }

}
