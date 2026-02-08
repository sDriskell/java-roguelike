package roguelike.actors.conditions;

import roguelike.actors.Actor;
import roguelike.util.StringEx;
import squidpony.squidcolor.SColor;

/**
 * 
 */
public class ReducedVision extends Condition {

  private static final long serialVersionUID = 7729159918086465466L;

  private int oldVisRad;

  /**
   * 
   * @param argDur
   */
  public ReducedVision(int argDur) {
    super(new StringEx("Reduced Vision", SColor.BLUE_VIOLET, SColor.BLACK), argDur);
  }

  @Override
  public void onConditionAdded(Actor argAct) {
    oldVisRad = argAct.getVisionRadius();
    argAct.setVisionRadius((int) (oldVisRad * 0.33));
    argAct.doAction("can no longer see as well.");
  }

  @Override
  protected void onProcess(Actor argAct) {
  }

  @Override
  protected void onConditionRemoved(Actor argAct) {
    argAct.setVisionRadius(oldVisRad);
    argAct.doAction("'s vision has returned to normal.");
  }
}
