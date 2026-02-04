package roguelike.actions.combat;

import roguelike.actions.Action;
import roguelike.actors.Actor;
import roguelike.items.Weapon;

/**
 * 
 */
public abstract class Attack {

  protected String description;
  protected int baseDamage;
  protected DamageType damageType;
  protected Weapon weapon;

  /**
   * 
   * @param argDesc
   * @param argBaseDmg
   * @param argWpn
   */
  protected Attack(String argDesc, int argBaseDmg, Weapon argWpn) {
    description = argDesc;
    baseDamage = argBaseDmg;
    weapon = argWpn;

    damageType = argWpn.defaultDamageType();
  }

  protected Attack(String argDesc, int argBaseDmg, Weapon argWpn, DamageType argDmgType) {
    description = argDesc;
    baseDamage = argBaseDmg;
    weapon = argWpn;
    damageType = argDmgType;
  }

  /**
   * 
   * @return
   */
  public int getDamage() {
    return baseDamage;
  }

  /**
   * 
   * @return
   */
  public Weapon getWeapon() {
    return weapon;
  }

  /**
   * 
   * @return
   */
  public DamageType getDamageType() {
    return damageType;
  }

  /**
   * 
   * @param argAct
   * @param argTgt
   * @return
   */
  public final boolean perform(Action argAct, Actor argTgt) {
    Actor attacker = argAct.getActor();
    attacker.doAction(description, argTgt.getMessageName());
    return onPerform(argAct, argTgt);
  }

  /**
   * 
   * @param argAct
   * @param argTgt
   * @return
   */
  // TODO: implement various types of attacks
  protected abstract boolean onPerform(Action argAct, Actor argTgt);
}
