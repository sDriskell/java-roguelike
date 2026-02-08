package roguelike.actors.conditions;

import java.io.Serializable;

import roguelike.actors.Actor;
import roguelike.util.StringEx;

/**
 * 
 */
public abstract class Condition implements Serializable {

  private static final long serialVersionUID = 1L;

  protected StringEx identifier;
  protected int duration;
  protected int initialDuration;

  /**
   * 
   * @param argIdent
   * @param argDur
   */
  protected Condition(StringEx argIdent, int argDur) {
    identifier = argIdent;
    duration = argDur;
    initialDuration = argDur;
  }

  /**
   * 
   * @return
   */
  public int getDuration() {
    return duration;
  }

  /**
   * 
   * @param argAct
   * @return
   */
  public final boolean process(Actor argAct) {
    if (duration > 0) {
      duration--;
      onProcess(argAct);
    }

    if (duration == 0) {
      onConditionRemoved(argAct);
      return true;
    }

    return false;
  }

  /**
   * 
   * @return
   */
  public StringEx identifier() {
    return identifier;
  }

  /**
   * 
   * @param argAct
   */
  public void onConditionAdded(Actor argAct) {
  }

  /**
   * 
   * @param argAct
   */
  protected abstract void onProcess(Actor argAct);

  /**
   * 
   * @param argAct
   */
  protected void onConditionRemoved(Actor argAct) {
  }
}
