package roguelike.functionalinterfaces;

import roguelike.DialogResult;

/**
 * 
 * @param <T>
 */
public interface DialogCallback<T> {

  /**
   * 
   * @param argRes
   */
  public void setResult(DialogResult<T> argRes);
}
