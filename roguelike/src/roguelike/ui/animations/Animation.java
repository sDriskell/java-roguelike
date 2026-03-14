package roguelike.ui.animations;

import java.awt.Point;
import java.awt.Rectangle;

import roguelike.Game;
import roguelike.actors.Actor;
import roguelike.ui.windows.TerminalBase;
import roguelike.util.Coordinate;

/**
 * 
 */
public abstract class Animation {

  protected int currentFrame = 0;
  protected int totalFrames;

  /**
   * True if the animation should prevent user input while it's running
   * 
   * @return
   */
  public abstract boolean isBlocking();

  /**
   * Draws the current frame of the animation and advances to the next one.
   * 
   * @param argTerm
   * @return True if the last frame was just drawn and the animation should be
   * removed.
   */
  public final boolean nextFrame(TerminalBase argTerm) {
    if (currentFrame < totalFrames) {
      onNextFrame(argTerm);
    }

    return ++currentFrame > totalFrames;
  }

  /**
   * 
   * @param argTerm
   * @param argTgt
   * @return
   */
  protected Point getOffsetPosition(TerminalBase argTerm, Actor argTgt) {
    Coordinate targetPos = argTgt.getPosition();
    return getOffsetPosition(argTerm, targetPos);
  }

  /**
   * 
   * @param argTerm
   * @param argTgtPos
   * @return
   */
  protected Point getOffsetPosition(TerminalBase argTerm, Coordinate argTgtPos) {
    Game g = Game.current();
    Rectangle termSize = argTerm.size();
    Point upperLeft = g.getCurrentMapArea().getUpperLeftScreenTile(termSize.width, termSize.height,
        g.getPlayer().getPosition());

    return new Point(argTgtPos.x - upperLeft.x, argTgtPos.y - upperLeft.y);
  }

  /**
   * 
   * @param argTerm
   */
  public abstract void onNextFrame(TerminalBase argTerm);
}
