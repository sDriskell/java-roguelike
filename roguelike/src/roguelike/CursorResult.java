package roguelike;

import roguelike.util.Coordinate;

/**
 * 
 */
public class CursorResult {
  private Coordinate pos;
  private boolean isCanx;

  /**
   * 
   * @param argPos
   * @param argIsCanx
   */
  public CursorResult(Coordinate argPos, boolean argIsCanx) {
    pos = argPos;
    isCanx = argIsCanx;
  }

  /**
   * 
   * @return
   */
  public Coordinate getPosition() {
    return pos;
  }

  /**
   * 
   * @return
   */
  public boolean isCanceled() {
    return isCanx;
  }
}
