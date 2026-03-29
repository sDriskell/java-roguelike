package roguelike.actions;

import roguelike.actors.Actor;

/**
 * 
 */
public class FailAction extends Action {

  /**
   * 
   * @param argAct
   */
  public FailAction(Actor argAct) {
    super(argAct);
  }

  @Override
  public ActionResult onPerform() {
    return ActionResult.failure();
  }
}
