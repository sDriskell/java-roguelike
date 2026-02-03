package roguelike.actions;

import roguelike.Game;
import roguelike.actors.Actor;

/**
 * 
 */
public class QuitAction extends Action {

  /**
   * 
   * @param argAct
   */
  public QuitAction(Actor argAct) {
    super(argAct);
    usesEnergy = false;
  }

  @Override
  public ActionResult onPerform() {
    Game.current().stopGame();
    return ActionResult.success();
  }

}
