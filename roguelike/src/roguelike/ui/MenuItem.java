package roguelike.ui;

import roguelike.util.StringEx;

/**
 * 
 * @param <T>
 */
public class MenuItem<T> {

  private StringEx text;
  private T item;
  private boolean isActive;

  /**
   * 
   * @param argTxt
   * @param argItm
   * @param argIsActive
   */
  public MenuItem(StringEx argTxt, T argItm, boolean argIsActive) {
    text = argTxt;
    item = argItm;
    isActive = argIsActive;
  }

  /**
   * 
   * @param argTxt
   * @param argItm
   */
  public MenuItem(String argTxt, T argItm) {
    this(new StringEx(argTxt), argItm, false);
  }

  /**
   * 
   * @param argTxt
   * @param argItm
   * @param argIsActive
   */
  public MenuItem(String argTxt, T argItm, boolean argIsActive) {
    this(argTxt, argItm);
    isActive = argIsActive;
  }

  /**
   * 
   * @param argIsActive
   * @return
   */
  public MenuItem<T> setActive(boolean argIsActive) {
    isActive = argIsActive;
    return this;
  }

  /**
   * 
   * @return
   */
  public StringEx getText() {
    return text;
  }

  /**
   * 
   * @return
   */
  public T item() {
    return this.item;
  }

  /**
   * 
   * @return
   */
  public boolean isActive() {
    return isActive;
  }
}
