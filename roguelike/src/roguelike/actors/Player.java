package roguelike.actors;

import roguelike.actions.Action;
import roguelike.actors.behaviors.PlayerInputBehavior;
import roguelike.util.ActorUtils;
import roguelike.util.Log;
import squidpony.squidcolor.SColor;

/**
 * 
 */
public class Player extends Actor {

  private static final long serialVersionUID = 1L;

  private String characterName;

  /**
   * 
   */
  public Player() {
    super('@', SColor.WHITE);
    // TODO: load these during character creation somewhere
    statistics().speed.setBase(20);
    behavior = new PlayerInputBehavior(this);
  }

  /**
   * 
   * @param argAct
   * @return
   */
  public static boolean isPlayer(Actor argAct) {
    return argAct instanceof Player;
  }

  /**
   * 
   * @return
   */
  public String getCharacterName() {
    return characterName;
  }

  @Override
  public Action getNextAction() {
    return behavior.getAction();
  }

  @Override
  public String getName() {
    return "you";
  }

  @Override
  public String getMessageName() {
    return getName();
  }

  @Override
  public String getVerbSuffix() {
    return "";
  }

  @Override
  public String getDescription() {
    return "The player";
  }

  @Override
  public int getVisionRadius() {
    return visionRadius;
  }

  @Override
  public void onAttackedInternal(Actor argAtkr) {
    Log.verboseDebug("Player attacked by " + argAtkr.getName());
    Log.verboseDebug("AttackedBy count=" + attackedBy.size());
  }

  @Override
  public void onKilled() {
    // back to title screen
  }

  @Override
  protected String makeCorrectVerb(String argMsg) {
    return ActorUtils.makePlayerText(argMsg);
  }

}
