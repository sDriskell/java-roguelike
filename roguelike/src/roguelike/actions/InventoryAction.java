package roguelike.actions;

import roguelike.actors.Actor;
import roguelike.items.Item;
import roguelike.screens.InventoryScreen;
import roguelike.screens.Screen;
import roguelike.util.Log;

/**
 * 
 */
public class InventoryAction extends DialogInputRequiredAction<Item> {

  /**
   * 
   * @param argAct
   */
  public InventoryAction(Actor argAct) {
    super(argAct);
    usesEnergy = false;
    Screen scn = Screen.currentScreen();
    scn.setNextScreen(new InventoryScreen(scn, scn.terminal()));
  }

  @Override
  protected ActionResult onPerform() {
    Log.debug("InventoryAction");
    return ActionResult.success();
  }

  @Override
  public boolean checkForIncomplete() {
    return super.checkForIncomplete();
  }

}
