package roguelike.maps;

import java.util.ArrayList;

/**
 * 
 */
public class Path {

  private ArrayList<Step> pathSteps = new ArrayList<>();

  int currentStep;

  /**
   * Create an empty path
   */
  public Path() {
    // TODO: handle empty constructor
  }

  /**
   * 
   * @return
   */
  public Step getCurrentStep() {
    return currentStep < pathSteps.size() ? getStep(currentStep) : null;
  }

  /**
   * 
   * @return
   */
  public int nextStep() {
    return ++currentStep;
  }

  /**
   * Get the length of the path, i.e. the number of steps
   * 
   * @return The number of steps in this path
   */
  public int getLength() {
    return pathSteps.size();
  }

  /**
   * Get the step at a given index in the path
   * 
   * @param argInd The index of the step to retrieve. Note this should be >= 0 and
   *   < getLength();
   * @return The step information, the position on the map.
   */
  public Step getStep(int argInd) {
    return pathSteps.get(argInd);
  }

  /**
   * Get the x coordinate for the step at the given index
   * 
   * @param argInd The index of the step whose x coordinate should be retrieved
   * @return The x coordinate at the step
   */
  public int getX(int argInd) {
    return getStep(argInd).stepX;
  }

  /**
   * Get the y coordinate for the step at the given index
   * 
   * @param argInd The index of the step whose y coordinate should be retrieved
   * @return The y coordinate at the step
   */
  public int getY(int argInd) {
    return getStep(argInd).stepY;
  }

  /**
   * Append a step to the path.
   * 
   * @param x The x coordinate of the new step
   * @param y The y coordinate of the new step
   */
  public void appendStep(int x, int y) {
    pathSteps.add(new Step(x, y));
  }

  /**
   * Prepend a step to the path.
   * 
   * @param x The x coordinate of the new step
   * @param y The y coordinate of the new step
   */
  public void prependStep(int x, int y) {
    pathSteps.add(0, new Step(x, y));
  }

  /**
   * Check if this path contains the given step
   * 
   * @param x The x coordinate of the step to check for
   * @param y The y coordinate of the step to check for
   * @return True if the path contains the given step
   */
  public boolean contains(int x, int y) {
    return pathSteps.contains(new Step(x, y));
  }

  /**
   * A single step within the path
   * 
   * @author Kevin Glass
   */
  public class Step {
    private int stepX;
    private int stepY;

    /**
     * Create a new step
     * 
     * @param x The x coordinate of the new step
     * @param y The y coordinate of the new step
     */
    public Step(int x, int y) {
      stepX = x;
      stepY = y;
    }

    /**
     * Get the x coordinate of the new step
     * 
     * @return The x coodindate of the new step
     */
    public int getX() {
      return stepX;
    }

    /**
     * Get the y coordinate of the new step
     * 
     * @return The y coordinate of the new step
     */
    public int getY() {
      return stepY;
    }

    /**
     * @see Object#hashCode()
     */
    public int hashCode() {
      return stepX * stepY;
    }

    /**
     * @see Object#equals(Object)
     */
    public boolean equals(Object argOth) {
      if (argOth instanceof Step) {
        Step o = (Step) argOth;
        return (o.stepX == stepX) && (o.stepY == stepY);
      }

      return false;
    }
  }
}