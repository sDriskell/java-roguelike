package roguelike.ui.windows;

import roguelike.util.CharEx;

/**
 * 
 */
abstract class TerminalCursor {

  /**
   * 
   * @param x
   * @param y
   * @param c
   * @return
   */
  public abstract boolean put(int x, int y, CharEx c);

  /**
   * 
   * @param x
   * @param y
   * @return
   */
  public abstract boolean bg(int x, int y);
}
