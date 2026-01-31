package roguelike;

import roguelike.actions.combat.Attack;
import roguelike.actions.combat.RangedAttack;
import roguelike.actors.Actor;
import roguelike.ui.animations.Animation;
import roguelike.ui.animations.AttackAnimation;
import roguelike.ui.animations.AttackMissedAnimation;
import roguelike.ui.animations.RangedAttackAnimation;

/**
 * 
 */
public class TurnEvent {
  public static final int ATTACKED = 1;
  public static final int ATTACK_MISSED = 2;
  public static final int RANGED_ATTACKED = 3;

  private Actor initiator;
  private Actor target;
  private String message;
  private Animation animation;
  private int type;

  /**
   * 
   * @param argInitiator
   * @param argTarget
   * @param argType
   */
  private TurnEvent(Actor argInitiator, Actor argTarget, int argType) {
    initiator = argInitiator;
    target = argTarget;
    type = argType;
  }

  /**
   * 
   * @param argInitiator
   * @param argTgt
   * @param argMsg
   * @param argAttack
   * @return
   */
  public static TurnEvent attack(Actor argInitiator, Actor argTgt, String argMsg,
      Attack argAttack) {
    if (argAttack instanceof RangedAttack) {
      return rangedAttack(argInitiator, argTgt, argAttack);
    }

    return new TurnEvent(argInitiator, argTgt, ATTACKED).setMessage(argMsg)
        .setAnimation(new AttackAnimation(argInitiator, argTgt, "" + argAttack.getDamage()));
  }

  /**
   * 
   * @param initiator
   * @param target
   * @param message
   * @return
   */
  public static TurnEvent attackMissed(Actor argInitiator, Actor argTgt, String argMsg) {
    return new TurnEvent(argInitiator, argTgt, ATTACK_MISSED).setMessage(argMsg)
        .setAnimation(new AttackMissedAnimation(argTgt));
  }

  /**
   * 
   * @param argInitiator
   * @param argTgt
   * @param argAttack
   * @return
   */
  public static TurnEvent rangedAttack(Actor argInitiator, Actor argTgt, Attack argAttack) {
    return new TurnEvent(argInitiator, argTgt, RANGED_ATTACKED)
        .setAnimation(new RangedAttackAnimation(argInitiator, argTgt, "" + argAttack.getDamage()));
  }

  /**
   * 
   * @return
   */
  public int getType() {
    return type;
  }

  /**
   * 
   * @return
   */
  public Actor getInitiator() {
    return initiator;
  }

  /**
   * 
   * @return
   */
  public Actor getTarget() {
    return target;
  }

  /**
   * 
   * @return
   */
  public String getMessage() {
    return message;
  }

  /**
   * 
   * @param argAnimation
   * @return
   */
  public TurnEvent setAnimation(Animation argAnimation) {
    animation = argAnimation;
    return this;
  }

  /**
   * 
   * @return
   */
  public Animation getAnimation() {
    return this.animation;
  }

  /**
   * 
   * @param argMsg
   * @return
   */
  private TurnEvent setMessage(String argMsg) {
    message = argMsg;
    return this;
  }
}
