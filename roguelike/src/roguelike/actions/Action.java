package roguelike.actions;

import roguelike.actors.Actor;

/**
 * 
 */
public abstract class Action {

  protected Actor actor;
  protected boolean usesEnergy;

  /**
   * 
   * @param argActor
   */
  protected Action(Actor argActor) {
    actor = argActor;
    usesEnergy = true;
  }

  /**
   * 
   * @return
   */
  public Actor getActor() {
    return actor;
  }

  /**
   * 
   * @return
   */
  public final ActionResult perform() {
    if (checkForIncomplete()) {
      return ActionResult.incomplete();
    }

    ActionResult res = onPerform();

    if (res.isSuccess && usesEnergy) {
      actor.energy().act();
    }

    return res;
  }

  /**
   * 
   * @return
   */
  protected boolean checkForIncomplete() {
    return false;
  }

  /**
   * 
   * @return
   */
  protected abstract ActionResult onPerform();
}
