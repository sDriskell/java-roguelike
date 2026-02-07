package roguelike.actors;

import java.io.Serializable;

//TODO: remove serializable implementation

/**
 * 
 */
public class Energy implements Serializable {

  private static final long serialVersionUID = 1L;
  private static final int ACTION_THRESHOLD = 100;

  int current;

  /**
   * 
   * @return
   */
  public int getCurrent() {
    return current;
  }

  /**
   * 
   * @param argAmt
   * @return
   */
  public boolean increase(int argAmt) {
    current += argAmt;
    return canAct();
  }

  /**
   * 
   */
  public void act() {
    if (canAct()) {
      current -= ACTION_THRESHOLD;
    }
  }

  /**
   * 
   * @return
   */
  public boolean canAct() {
    return current >= ACTION_THRESHOLD;
  }
}
