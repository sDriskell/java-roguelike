package roguelike.ui.windows;

import roguelike.util.CharEx;

/**
 * 
 */
public abstract class TerminalChangeNotification {

  /**
   * 
   * @param x
   * @param y
   * @param c
   */
  public abstract void onChanged(int x, int y, CharEx c);
}
