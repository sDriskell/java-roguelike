package roguelike.actions;

import roguelike.CursorResult;
import roguelike.actions.combat.Attack;
import roguelike.actors.Actor;
import roguelike.items.RangedWeapon;
import roguelike.maps.MapArea;
import roguelike.ui.AttackCursor;
import roguelike.ui.InputCommand;
import roguelike.util.Log;
import squidpony.squidgrid.util.BasicRadiusStrategy;

/**
 * This is used by the player to display a target to choose which enemy to
 * attack - Npc's can just use AttackActions.
 * 
 * @author john
 * @author sdriskell
 *
 */
public class RangedAttackAction extends CursorInputRequiredAction<InputCommand> {

  private MapArea mapArea;
  private Actor target;
  private RangedWeapon weapon;

  /**
   * 
   * @param argAct
   * @param argMap
   * @param argWpn
   */
  public RangedAttackAction(Actor argAct, MapArea argMap, RangedWeapon argWpn) {
    super(argAct);
    mapArea = argMap;
    weapon = argWpn;

    int maxRange = Math.min(argAct.getVisionRadius(), argWpn.range());
    cursor = new AttackCursor(argAct.getPosition(), argMap, maxRange, BasicRadiusStrategy.CUBE);
    showCursor(cursor);
  }

  @Override
  protected ActionResult onPerform() {
    CursorResult res = cursor.result();

    if (res.isCanceled()) {
      return ActionResult.failure().setMessage("Canceled");
    }

    int x = res.getPosition().x;
    int y = res.getPosition().y;
    target = mapArea.getActorAt(x, y);

    if (target != null && target != actor) {
      // TODO: insert Ranged Attack animation here
      return attackTarget();
    }

    return ActionResult.success();
  }

  /**
   * 
   * @return
   */
  private ActionResult attackTarget() {
    if (!actor.isAlive()) {
      Log.warning(">>> onPerform() >>> Actor " + actor.getName() + " is dead!");
      return ActionResult.failure().setMessage("Actor " + actor.getName() + " is dead!");
    }

    if (!target.isAlive()) {
      return ActionResult.alternate(new WaitAction(actor))
          .setMessage(">>> onPerform() >>> Target " + target.getName() + " is dead!");
    }

    Attack atk = weapon.getAttack();

    if (atk != null) {
      atk.perform(this, target);
      return ActionResult.success();
    }
    else {
      /*
       * we can't attack but success the action anyway so it causes the actor to lose
       * energy and advance to the next actor
       */
      return ActionResult.success().setMessage("No weapon");
    }
  }
}
