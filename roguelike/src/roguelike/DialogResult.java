package roguelike;

/**
 * 
 * @param <T>
 */
public class DialogResult<T> {

  private boolean isCanceled;
  private T item;

  /**
   * 
   * @param argItm
   * @param argIsClosed
   */
  private DialogResult(T argItm, boolean argIsClosed) {
    item = argItm;
    isCanceled = argIsClosed;
  }

  /**
   * 
   * @return
   */
  public boolean isCanceled() {
    return isCanceled;
  }

  /**
   * 
   * @return
   */
  public T item() {
    return item;
  }

  /**
   * 
   * @param <T>
   * @param argItm
   * @return
   */
  public static <T> DialogResult<T> ok(T argItm) {
    return new DialogResult<>(argItm, false);
  }

  /**
   * 
   * @param <T>
   * @return
   */
  public static <T> DialogResult<T> cancel() {
    return new DialogResult<>(null, true);
  }
}
