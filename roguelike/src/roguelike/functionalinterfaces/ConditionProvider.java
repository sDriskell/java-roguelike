
package roguelike.functionalinterfaces;

import roguelike.actors.conditions.Condition;

/**
 * 
 */
public interface ConditionProvider {

  /**
   * 
   * @param argDur
   * @return
   */
  public Condition get(int argDur);
}
