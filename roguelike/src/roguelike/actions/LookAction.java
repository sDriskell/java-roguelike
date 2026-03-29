package roguelike.actions;

import roguelike.Cursor;
import roguelike.actors.Actor;
import roguelike.maps.MapArea;
import roguelike.screens.LookScreen;
import roguelike.screens.Screen;
import roguelike.ui.InputCommand;
import roguelike.ui.LookCursor;
import roguelike.ui.windows.TerminalBase;
import roguelike.util.Log;

/**
 * 
 */
public class LookAction extends CursorInputRequiredAction<InputCommand> {

  /**
   * 
   * @param argAct
   * @param argMap
   */
  public LookAction(Actor argAct, MapArea argMap) {
    super(argAct);
    usesEnergy = false;
    cursor = new LookCursor(argAct.getPosition(), argMap);
    showCursor(cursor);
  }

  @Override
  protected void showCursor(Cursor cursor) {
    Screen current = Screen.currentScreen();
    TerminalBase screenTerm = current.terminal().getWindow(0, 0,
        current.getDrawableArea().width, current.getDrawableArea().height);
    current.setNextScreen(new LookScreen(screenTerm, (LookCursor) cursor, r -> result = r));
  }

  @Override
  protected ActionResult onPerform() {
    Log.debug("LookAction");
    return ActionResult.success();
  }
}
