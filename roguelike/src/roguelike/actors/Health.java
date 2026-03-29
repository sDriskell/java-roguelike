package roguelike.actors;

import java.io.Serializable;

//TODO: remove serializable implementation

/**
 * 
 */
public class Health implements Serializable {

  private static final long serialVersionUID = 1L;

  private int current;
  private int maximum;

  /**
   * 
   * @param argMax
   */
  public Health(int argMax) {
    maximum = argMax;
    current = argMax;
  }

  /**
   * 
   * @return
   */
  public int getCurrent() {
    return current;
  }

  /**
   * 
   * @return
   */
  public int getMaximum() {
    return maximum;
  }

  /**
   * 
   * @param argMax
   */
  public void setMaximum(int argMax) {
    setMaximum(argMax, argMax < current);
  }

  /**
   * 
   * @param argMax
   * @param isSetToMax
   */
  public void setMaximum(int argMax, boolean isSetToMax) {
    maximum = argMax;

    if (isSetToMax) {
      current = argMax;
    }
  }

  public void heal(int argAMt) {
    current = Math.min(current + argAMt, maximum);
  }

  boolean damage(int argAmt) {
    current -= argAmt;
    return current <= 0;
  }
}
