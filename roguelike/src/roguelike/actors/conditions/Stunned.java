package roguelike.actors.conditions;

import roguelike.actors.Actor;
import roguelike.util.StringEx;
import squidpony.squidcolor.SColor;

/**
 * 
 */
public class Stunned extends Condition {

  private static final long serialVersionUID = -5457623122809872866L;

  /**
   * 
   * @param argDur
   */
  public Stunned(int argDur) {
    super(new StringEx("Stunned", SColor.ZINNWALDITE, SColor.BLACK), argDur);
  }

  @Override
  public void onConditionAdded(Actor argAct) {
    argAct.statistics().reflexBonus -= 5;
    argAct.statistics().aimingBonus -= 5;
  }

  @Override
  protected void onProcess(Actor argAct) {
  }

  @Override
  protected void onConditionRemoved(Actor argAct) {
    argAct.statistics().reflexBonus += 5;
    argAct.statistics().aimingBonus += 5;
  }
}
