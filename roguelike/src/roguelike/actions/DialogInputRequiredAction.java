package roguelike.actions;

import roguelike.Dialog;
import roguelike.DialogResult;
import roguelike.actors.Actor;
import roguelike.screens.DialogScreen;
import roguelike.screens.Screen;

/**
 * 
 * @param <T>
 */
public abstract class DialogInputRequiredAction<T> extends CursorInputRequiredAction<T> {

  protected Dialog<T> dialog;
  protected DialogResult<T> dialogResult;

  /**
   * 
   * @param argAct
   */
  protected DialogInputRequiredAction(Actor argAct) {
    super(argAct);
  }

  // TODO: ternary statement? This seems like it could be restructured.
  @Override
  public boolean checkForIncomplete() {
    if (dialog != null && dialog.waitingForResult()) {
      return true;
    }

    return super.checkForIncomplete();
  }

  protected final void showDialog(Dialog<T> dialog) {
    Screen current = Screen.currentScreen();
    current
        .setNextScreen(new DialogScreen<T>(current.terminal(), dialog, r -> dialogResult = r));
  }
}
