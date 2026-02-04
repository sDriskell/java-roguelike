package roguelike.actions.combat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import roguelike.Game;
import roguelike.MessageDisplayProperties;
import roguelike.TurnEvent;
import roguelike.actions.Action;
import roguelike.actors.Actor;
import roguelike.actors.Player;
import roguelike.actors.Statistics;
import roguelike.items.Equipment.ItemSlot;
import roguelike.items.Weapon;
import roguelike.util.DiceRolls;
import roguelike.util.Log;
import roguelike.util.Utility;
import squidpony.squidcolor.SColor;

/**
 * Handles combat...
 * 
 * @author john
 * 
 */
public class CombatHandler implements Serializable {

  private static final long serialVersionUID = 1L;

  protected transient Game game = Game.current();
  private Actor actor;

  /**
   * 
   * @param argAct
   */
  public CombatHandler(Actor argAct) {
    this.actor = argAct;
  }

  /**
   * 
   * @param argInput
   * @throws ClassNotFoundException
   * @throws IOException
   */
  private void readObject(ObjectInputStream argInput) throws ClassNotFoundException, IOException {
    argInput.defaultReadObject();
    game = Game.current();
  }

  /**
   * Returns an attack that the actor will use to try and hit the target
   * 
   * @param argTgt
   * @return
   */
  public Attack getAttack(Actor argTgt) {
    // TODO: implement attack choosing behavior, etc
    // TODO: dual wielding should return 1 attack dependent on skill
    Weapon weapon = actor.equipment().getEquippedWeapons().stream().filter(w -> w != null)
        .findFirst().orElse(null);

    if (weapon != null) {
      weapon.onAttacking(actor, argTgt);
      return weapon.getAttack();
    }
    return null;
  }

  /**
   * Resolves the attack, and returns either the original attack or a modified
   * attack taking into account the defender's stats, resistances, etc
   * 
   * @param argAtkr
   * @param argAtk
   * @return
   */
  public Attack defend(Actor argAtkr, Attack argAtk) {
    // TODO: implement defending behavior, resistances, etc
    Statistics attackerStats = argAtkr.statistics();
    Statistics defenderStats = actor.statistics();

    int aWeaponProficiency = 3;
    int dWeaponProficiency = 3;
    int attackManeuver = 0; // modifier for different attack types, etc
    int armorValue = getArmorValue(); // armor value defender is wearing
    int defenseManeuver = 0; // modifier for defense like evade, dodge, etc

    int attackWeaponTN = 7; // TODO: get these from weapon data or maneuver
    int defendWeaponTN = getDefenderTargetNumber(argAtk);

    int attackSuccessPool = getAttackerSuccessPool(attackerStats, aWeaponProficiency,
        attackManeuver);
    int defendSuccessPool = getDefenderSuccessPool(defenderStats, dWeaponProficiency,
        defenseManeuver, argAtk);

    int reachDiff = determineReachDifference(argAtkr, argAtk);

    if (reachDiff > 0) {
      attackSuccessPool += reachDiff;
    }
    else if (reachDiff < 0) {
      defendSuccessPool += -reachDiff;
    }

    // This determines whether the attack landed or not
    int attackerSuccesses = DiceRolls.roll(attackSuccessPool, attackWeaponTN);
    int defenderSuccesses = DiceRolls.roll(defendSuccessPool, defendWeaponTN);
    int total = attackerSuccesses - defenderSuccesses;

    Log.debug(
        "S (A): " + attackerSuccesses + ", TN=" + attackWeaponTN + ", pool=" + attackSuccessPool);
    Log.debug(
        "S (D): " + defenderSuccesses + ", TN=" + defendWeaponTN + ", pool=" + defendSuccessPool);

    logCombatMessage(
        argAtkr.doAction("rolls %d (%d) successes against %s: total %d (%s)", attackerSuccesses,
            defenderSuccesses, actor.getMessageName(), total, (total > 0 ? "Hit" : "Miss")));

    if (total > 0) {
      int baseDamage = getTotalDamage(argAtkr, argAtk, armorValue, total);
      argAtk.baseDamage = baseDamage;
      return argAtk;
    }
    else {
      return new MeleeAttack("misses %s!", 0, argAtk.getWeapon());
    }
  }

  /**
   * Called when an attack connects
   * 
   * @param attack
   * @param attacker
   * @return True if the attack killed the target
   */
  public void onDamaged(Actor argAtkr, Attack argAtk) {
    actor.onDamaged(argAtk.baseDamage);
  }

  /**
   * Processes the attack after the target has defended it
   * 
   * @param argAct
   * @param argAtk
   * @param argTgt
   * @return The result from onDamaged() - true if the attack killed the target
   */
  boolean processAttack(Action argAct, Actor argTgt, Attack argAtk) {
    game = Game.current();
    argAtk = argTgt.combatHandler().defend(actor, argAtk);
    boolean isDead;

    if (argAtk.baseDamage > 0) {
      argTgt.combatHandler().onDamaged(actor, argAtk);
      isDead = !argTgt.isAlive();

      String msg = getAttackMessage(argTgt, argAtk);
      SColor color = SColor.ORANGE;

      if (Player.isPlayer(argTgt)) {
        color = SColor.RED;
      }

      game.displayMessage(actor.doAction(msg), color);
      /* add an event so we can show an animation */
      game.addEvent(TurnEvent.attack(actor, argTgt, "" + argAtk.getDamage(), argAtk));

    }
    else {
      isDead = false;
      game.displayMessage(actor.getMessageName() + " missed " + argTgt.getMessageName() + ".",
          SColor.DARK_TAN);

      game.addEvent(TurnEvent.attackMissed(actor, argTgt, "Missed"));
    }

    argTgt.onAttacked(actor);
    // return true if this attack killed the target
    return isDead;
  }

  /**
   * 
   * @param argAtkrStats
   * @param argAtkWpnProf
   * @param argAtkManeuver
   * @return
   */
  private int getAttackerSuccessPool(Statistics argAtkrStats, int argAtkWpnProf,
      int argAtkManeuver) {
    return argAtkrStats.baseMeleePool(argAtkWpnProf) + argAtkManeuver;
  }

  /**
   * 
   * @param argDefStats
   * @param argDefWpnProf
   * @param argDefManeuver
   * @param argAtk
   * @return
   */
  private int getDefenderSuccessPool(Statistics argDefStats, int argDefWpnProf, int argDefManeuver,
      Attack argAtk) {
    if (argAtk instanceof RangedAttack) {
      return argDefStats.baseEvadePool();
    }
    return argDefStats.baseMeleePool(argDefWpnProf) + argDefManeuver;
  }

  /**
   * 
   * @param attack
   * @return
   */
  private int getDefenderTargetNumber(Attack attack) {
    if (attack instanceof RangedAttack)
      return 8; // TODO: change this?

    Weapon firstWeapon = actor.equipment().getEquippedWeapons().stream().filter(w -> w != null)
        .findFirst().orElse(null);
    if (firstWeapon != null)
      return firstWeapon.getDefenseTargetNumber();

    return 8; // default TN with no weapon
  }

  private int determineReachDifference(Actor argAtkr, Attack argAtk) {
    Weapon defendingWeapon = ItemSlot.RIGHT_HAND.getEquippedWeapon(actor);

    int attackingReach = argAtk.getWeapon().reach();
    int defendingReach = defendingWeapon == null ? 0 : defendingWeapon.reach();
    int reachDiff = attackingReach - defendingReach;

    // TODO: need to switch this penalty to the combatant who was damaged most
    // recently
    // TODO: maybe can use a Condition for this? with a duration of 1 turn that
    // removes itself when an attack hits

    if (argAtkr.wasAttackedThisRound()) {
      reachDiff *= -1;
      logCombatMessage(String.format("Reach advantage applied to %s", actor.getMessageName()));
    }
    else if (actor.wasAttackedThisRound()) {

    }

    Log.debug("Attacker reach: " + attackingReach);
    Log.debug("Defender reach: " + defendingReach);

    logCombatMessage(String.format("%s has reach of %d, %s has reach of %d",
        argAtkr.getMessageName(), attackingReach, actor.getMessageName(), defendingReach));
    return reachDiff;
  }

  /**
   * Determine damage of the attack based on how successful it was and the stats
   * of attacker/defender
   * 
   * @param argAtkr
   * @param argAtk
   * @param argArmVal
   * @param argTot
   * @return
   */
  private int getTotalDamage(Actor argAtkr, Attack argAtk, int argArmVal, int argTot) {
    int baseDamage = argTot + argAtk.getWeapon().getDamageRating(argAtk.getDamageType());
    baseDamage += (argAtkr.statistics().toughness.getTotalValue() / 2.0f);
    baseDamage -= (actor.statistics().toughness.getTotalValue() / 2.0f);

    // TODO: get armor here
    baseDamage -= argArmVal;
    return baseDamage;
  }

  /**
   * 
   * @param argMsg
   */
  private void logCombatMessage(String argMsg) {
    Game.current().messages().add(new MessageDisplayProperties(argMsg));
  }

  /**
   * 
   * @param argTgt
   * @param argAtk
   * @return
   */
  private String getAttackMessage(Actor argTgt, Attack argAtk) {
    int tgtHp = argTgt.health().getCurrent();
    String msg = "";

    try {
      String atkDesc = String.format(argAtk.description, argTgt.getMessageName());
      msg = String.format("%s for %d %s damage", atkDesc, argAtk.baseDamage, argAtk.damageType);

      if (tgtHp > 0) {
        msg += ". (" + tgtHp + " left)";
      }
      else {
        msg += (". " + Utility.capitalizeFirstLetter(argTgt.getMessageName() + " is dead."));
      }
    }
    catch (Exception e) {
      msg = "ERROR: " + argAtk.description;
    }

    return msg;
  }

  /**
   * 
   * @return
   */
  private int getArmorValue() {
    return 0;
  }
}
