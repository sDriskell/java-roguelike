package roguelike.functionalinterfaces;

import roguelike.actors.Actor;
import roguelike.actors.behaviors.Behavior;

/**
 * 
 */
public interface BehaviorProvider {

  /**
   * 
   * @param argAct
   * @return
   */
  public Behavior create(Actor argAct);
}
