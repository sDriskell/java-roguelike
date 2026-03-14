package roguelike.ui.animations;

import java.awt.Point;

import roguelike.actors.Actor;
import roguelike.actors.Player;
import roguelike.ui.windows.TerminalBase;
import squidpony.squidcolor.SColor;
import squidpony.squidcolor.SColorFactory;

/**
 * 
 */
public class AttackMissedAnimation extends Animation {

  private static final String DESCRIPTION = "Missed!";
  private static final int MISSED_FRAMES = 15;

  private Actor target;

  /**
   * 
   * @param argTgt
   */
  public AttackMissedAnimation(Actor argTgt) {
    target = argTgt;
    totalFrames = MISSED_FRAMES;
  }

  @Override
  public boolean isBlocking() {
    return true;
  }

  @Override
  public void onNextFrame(TerminalBase argTerm) {
    Point offsetPos = getOffsetPosition(argTerm, target);
    int x = offsetPos.x;
    int y = offsetPos.y;
    y = Math.max(0, y - (currentFrame / 4));
    SColor foregroundColor;
    int yOffset = 0;

    if (Player.isPlayer(target)) {
      foregroundColor = SColor.GREEN;
      yOffset = (currentFrame / 4) * 2;
    }
    else {
      foregroundColor = SColor.APRICOT;
    }

    foregroundColor = SColorFactory.blend(foregroundColor, SColor.DARK_BROWN,
        currentFrame / (float) totalFrames);

    TerminalBase dmg = argTerm.withColor(foregroundColor);
    dmg.write(x, y + yOffset, DESCRIPTION);
  }

}
