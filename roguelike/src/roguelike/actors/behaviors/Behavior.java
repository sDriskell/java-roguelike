package roguelike.actors.behaviors;

import java.io.Serializable;

import roguelike.actions.Action;
import roguelike.actors.Actor;

/**
 * 
 */
public abstract class Behavior implements Serializable {

  private static final long serialVersionUID = 1L;

  protected Actor actor;

  /**
   * 
   * @param argAct
   */
  protected Behavior(Actor argAct) {
    if (argAct == null) {
      throw new IllegalArgumentException("actor cannot be null");
    }

    this.actor = argAct;
  }

  /**
   * 
   * @return
   */
  public abstract boolean isHostile();

  /**
   * 
   */
  public void onNoAmmunition() {
  }

  /**
   * 
   * @param argAtkr
   */
  public void onAttacked(Actor argAtkr) {
  }

  /**
   * 
   * @return
   */
  public abstract Action getAction();

  /**
   * Allows the actor to change behaviors based on some criteria
   * 
   * @return
   */
  public abstract Behavior getNextBehavior();

  /**
   * 
   * @return
   */
  public abstract String getDescription();
}
