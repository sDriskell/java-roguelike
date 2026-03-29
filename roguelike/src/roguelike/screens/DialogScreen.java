package roguelike.screens;

import roguelike.Dialog;
import roguelike.functionalinterfaces.DialogCallback;
import roguelike.ui.windows.TerminalBase;

/**
 * 
 * @param <T>
 */
public class DialogScreen<T> extends Screen {

  private Dialog<T> dialog;
  private DialogCallback<T> resultCallback;

  /**
   * 
   * @param argTrm
   * @param argDlg
   * @param argResult
   */
  public DialogScreen(TerminalBase argTrm, Dialog<T> argDlg, DialogCallback<T> argResult) {
    super(argTrm);

    dialog = argDlg;
    resultCallback = argResult;
    dialog.showInPane(argTrm);
    dialog.show();
  }

  @Override
  public void onDraw() {
    dialog.draw();
  }

  @Override
  public void process() {
    if (dialog.process()) {
      resultCallback.setResult(dialog.result());
      restorePreviousScreen();
    }
  }

}
