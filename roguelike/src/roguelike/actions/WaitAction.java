package roguelike.actions;

import roguelike.actors.Actor;

/**
 * 
 */
public class WaitAction extends Action {

  /**
   * 
   * @param argAct
   */
  public WaitAction(Actor argAct) {
    super(argAct);
  }

  @Override
  protected ActionResult onPerform() {
    return ActionResult.success();
  }

}
