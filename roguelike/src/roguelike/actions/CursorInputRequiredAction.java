package roguelike.actions;

import roguelike.Cursor;
import roguelike.CursorResult;
import roguelike.actors.Actor;
import roguelike.screens.CursorScreen;
import roguelike.screens.Screen;

/**
 * 
 * @param <T>
 */
public abstract class CursorInputRequiredAction<T> extends Action {

  protected Cursor cursor;
  protected CursorResult result;

  /**
   * 
   * @param argAct
   */
  protected CursorInputRequiredAction(Actor argAct) {
    super(argAct);
  }

  @Override
  public boolean checkForIncomplete() {
    return cursor != null && cursor.waitingForResult();
  }

  /**
   * 
   * @param argCur
   */
  protected void showCursor(Cursor argCur) {
    Screen current = Screen.currentScreen();
    current
        .setNextScreen(new CursorScreen(current.terminal(), argCur, r -> result = r));
  }
}
