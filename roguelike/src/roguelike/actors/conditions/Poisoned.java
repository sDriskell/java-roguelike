package roguelike.actors.conditions;

import roguelike.actors.Actor;
import roguelike.util.StringEx;
import squidpony.squidcolor.SColor;

/**
 * 
 * 
 */
public class Poisoned extends Condition {

  private static final long serialVersionUID = 1028488464330263032L;

  /**
   * 
   * @param argDur
   */
  public Poisoned(int argDur) {
    super(new StringEx("Poisoned", SColor.GREEN, SColor.BLACK), argDur);
  }

  @Override
  protected void onProcess(Actor argAct) {
    argAct.onDamaged(1);
  }

}
