package roguelike.ui.animations;

import java.awt.Point;
import java.util.Queue;

import roguelike.actors.Actor;
import roguelike.ui.windows.TerminalBase;
import squidpony.squidcolor.SColor;
import squidpony.squidmath.Bresenham;

/**
 * 
 */
public class RangedAttackAnimation extends Animation {

  private static final int RANGED_FRAMES = 8;

  private Actor target;
  private Queue<Point> path;
  private AttackAnimation damageAnim;

  /**
   * 
   * @param argAtkr
   * @param argTgt
   * @param argDmg
   */
  public RangedAttackAnimation(Actor argAtkr, Actor argTgt, String argDmg) {
    target = argTgt;
    path = Bresenham.line2D(argAtkr.getPosition(), argTgt.getPosition());
    damageAnim = new AttackAnimation(argAtkr, argTgt, argDmg);
    totalFrames = damageAnim.totalFrames + RANGED_FRAMES;
  }

  @Override
  public boolean isBlocking() {
    return true;
  }

  @Override
  public void onNextFrame(TerminalBase terminal) {
    Point offsetPos = getOffsetPosition(terminal, target);
    int x = offsetPos.x - target.getPosition().x;
    int y = offsetPos.y - target.getPosition().y;

    if (this.currentFrame < RANGED_FRAMES) {
      int numTiles = (int) Math.ceil(path.size() / (float) 8.0f);

      for (int i = 0; i < numTiles; i++) {
        Point p = path.poll();

        if (p != null) {
          terminal.withColor(SColor.LIGHT_GRAY).put(p.x + x, p.y + y, '`');
        }
      }
    }
    else {
      damageAnim.nextFrame(terminal);
    }
  }
}
