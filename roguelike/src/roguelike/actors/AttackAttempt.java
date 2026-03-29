package roguelike.actors;

import java.io.Serializable;

/**
 * 
 */
public class AttackAttempt implements Serializable {

  private static final long serialVersionUID = 1L;

  private Actor actor;

  /**
   * 
   * @param argAct
   */
  public AttackAttempt(Actor argAct) {
    this.actor = argAct;
  }

  /**
   * 
   * @return
   */
  public Actor getActor() {
    return actor;
  }
}
