package roguelike.actions;

/**
 * 
 */
public class ActionResult {

  final boolean isSuccess;
  final boolean isCompleted;
  final Action alternateAction;
  String message;

  /**
   * 
   * @param argIsSuccess
   */
  private ActionResult(boolean argIsSuccess) {
    this(argIsSuccess, true, null);
  }

  /**
   * 
   * @param argSuccess
   * @param argCompleted
   * @param alternateAction
   */
  private ActionResult(boolean argSuccess, boolean argCompleted, Action argAltAction) {
    isSuccess = argSuccess;
    isCompleted = argCompleted;
    alternateAction = argAltAction;
  }

  /**
   * 
   * @return
   */
  public static ActionResult success() {
    return new ActionResult(true);
  }

  /**
   * 
   * @return
   */
  public static ActionResult failure() {
    return new ActionResult(false);
  }

  /**
   * 
   * @param argAction
   * @return
   */
  public static ActionResult alternate(Action argAction) {
    return new ActionResult(false, true, argAction);
  }

  /**
   * 
   * @return
   */
  public static ActionResult incomplete() {
    return new ActionResult(false, false, null);
  }

  /**
   * 
   * @return
   */
  public boolean isSuccessful() {
    return isSuccess;
  }

  /**
   * 
   * @return
   */
  public boolean isCompleted() {
    return this.isCompleted;
  }

  /**
   * 
   * @return
   */
  public Action getAlternateAction() {
    return alternateAction;
  }

  /**
   * 
   * @return
   */
  public String getMessage() {
    return message;
  }

  /**
   * 
   * @param message
   * @return
   */
  public ActionResult setMessage(String argMsg) {
    message = argMsg;
    return this;
  }

}
