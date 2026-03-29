package roguelike.ui.animations;

import java.awt.Point;

import roguelike.actors.Actor;
import roguelike.actors.Player;
import roguelike.ui.windows.TerminalBase;
import roguelike.util.Coordinate;
import squidpony.squidcolor.SColor;
import squidpony.squidcolor.SColorFactory;
import squidpony.squidgrid.util.DirectionIntercardinal;

/**
 * 
 */
public class AttackAnimation extends Animation {

  private Actor target;
  private String damage;

  private char attackChar;
  private Coordinate attackCharPoint;

  /**
   * 
   * @param argAtk
   * @param argTgt
   * @param argDmg
   */
  public AttackAnimation(Actor argAtk, Actor argTgt, String argDmg) {
    target = argTgt;
    damage = argDmg;
    totalFrames = 15;

    Coordinate attackerPos = argAtk.getPosition();
    Coordinate targetPos = argTgt.getPosition();

    DirectionIntercardinal dir = DirectionIntercardinal.getDirection(targetPos.x - attackerPos.x,
        targetPos.y - attackerPos.y);

    switch (dir) {
      case UP:
      case DOWN:
        attackChar = '|';
        break;
      case LEFT:
      case RIGHT:
        attackChar = '-';
        break;
      case UP_LEFT:
      case DOWN_RIGHT:
        attackChar = '\\';
        break;
      case UP_RIGHT:
      case DOWN_LEFT:
        attackChar = '/';
        break;
      default:
        attackChar = '*';
        break;
    }

    if (Math.floor(attackerPos.distance(targetPos)) <= 1) {
      attackCharPoint = targetPos;
    }
    else {
      attackCharPoint = attackerPos.createOffsetPosition(dir);
    }
  }

  @Override
  public boolean isBlocking() {
    return true;
  }

  @Override
  public void onNextFrame(TerminalBase argTerm) {
    Point offsetPos = getOffsetPosition(argTerm, target);
    Point attackCharPos = getOffsetPosition(argTerm, attackCharPoint);
    int x = offsetPos.x;
    int y = offsetPos.y;

    x = Math.max(0, x - (currentFrame / 4));
    y = Math.max(0, y - (currentFrame / 4));

    SColor backgroundColor = SColorFactory.blend(SColor.RED, SColor.BLACK,
        currentFrame / (float) totalFrames);
    SColor foregroundColor;
    int yOffset = 0;

    if (Player.isPlayer(target)) {
      foregroundColor = SColor.RED;
      yOffset = (currentFrame / 4) * 2;
    }
    else {
      foregroundColor = SColor.YELLOW;
    }

    foregroundColor = SColorFactory.blend(foregroundColor, SColor.BENI_DYE,
        currentFrame / (float) totalFrames);

    TerminalBase effect = argTerm.withColor(foregroundColor, backgroundColor);
    TerminalBase dmg = argTerm.withColor(foregroundColor);

    effect.fill(offsetPos.x, offsetPos.y, 1, 1);
    effect.put(attackCharPos.x, attackCharPos.y, attackChar);

    dmg.write(x, y + yOffset, damage.toString());
  }
}
