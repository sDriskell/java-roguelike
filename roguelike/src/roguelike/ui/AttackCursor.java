package roguelike.ui;

import java.awt.Rectangle;

import roguelike.Cursor;
import roguelike.CursorResult;
import roguelike.Game;
import roguelike.actors.Actor;
import roguelike.actors.Player;
import roguelike.maps.MapArea;
import roguelike.maps.Tile;
import roguelike.ui.windows.TerminalBase;
import roguelike.util.ArrayUtils;
import roguelike.util.Coordinate;
import roguelike.util.CurrentItemTracker;
import squidpony.squidcolor.SColor;
import squidpony.squidcolor.SColorFactory;
import squidpony.squidgrid.fov.FOVSolver;
import squidpony.squidgrid.fov.ShadowFOV;
import squidpony.squidgrid.util.BasicRadiusStrategy;
import squidpony.squidgrid.util.RadiusStrategy;

/**
 * 
 */
public class AttackCursor extends Cursor {

  private SColor background;
  private FOVSolver fov = new ShadowFOV();
  private Coordinate initialPosition;

  boolean fovDrawn = false;
  boolean determinedActors = false;
  Rectangle screenArea;
  float[][] incomingLight;

  private CurrentItemTracker<Actor> targets;
  private Actor startTarget = null;

  /**
   * 
   * @param argInitPos
   * @param argMap
   * @param argMaxRad
   * @param argRadStrat
   */
  public AttackCursor(Coordinate argInitPos, MapArea argMap, int argMaxRad,
      RadiusStrategy argRadStrat) {
    super(argInitPos, argMap, argMaxRad);

    radiusStrategy = BasicRadiusStrategy.CIRCLE;
    background = SColor.DARK_CORAL;
    initialPosition = argInitPos;
    radiusStrategy = argRadStrat;
    targets = new CurrentItemTracker<>();
  }

  @Override
  protected void onDraw(TerminalBase argTerm, int sx, int sy) {
    // TODO: maybe some effects here, draw a line or something
    if (!fovDrawn) {
      determineFOVTiles(argTerm);
      fovDrawn = true;
    }

    drawFOV(argTerm);
    super.onDraw(argTerm, sx, sy);
    // update
    DisplayManager.instance().setDirty();
  }

  @Override
  protected boolean onUpdatePosition(Coordinate argPos) {
    int x = argPos.x - screenArea.x;
    int y = argPos.y - screenArea.y;

    return incomingLight[x][y] > 0;
  }

  @Override
  protected CursorResult onProcessCommand(InputCommand argCmd) {
    switch (argCmd) {
      case PREVIOUS_TARGET:
        // move to previous target
        targets.previous();
        break;

      case NEXT_TARGET:
        // move to next target
        targets.advance();
        break;

      default:
        return null;
    }

    Actor tgt = targets.getCurrent();

    if (tgt != null) {
      pos.setLocation(tgt.getPosition());
    }

    return null;
  }

  /**
   * 
   * @param argTerm
   */
  private void determineFOVTiles(TerminalBase argTerm) {
    MapArea currentMap = Game.current().getCurrentMapArea();
    screenArea = currentMap.getVisibleAreaInTiles(argTerm, initialPosition);
    float[][] lighting = ArrayUtils.getSubArray(currentMap.getLightValues(), screenArea);
    incomingLight = fov.calculateFOV(lighting, initialPosition.x - screenArea.x,
        initialPosition.y - screenArea.y, maxRadius + 1f);

  }

  /**
   * 
   * @param argTerm
   */
  private void drawFOV(TerminalBase argTerm) {
    MapArea currentMap = Game.current().getCurrentMapArea();

    for (int x = screenArea.x; x < screenArea.getMaxX(); x++) {
      for (int y = screenArea.y; y < screenArea.getMaxY(); y++) {
        int cX = x - screenArea.x;
        int cY = y - screenArea.y;

        Tile t = currentMap.getTileAt(x, y);

        if (incomingLight[cX][cY] > 0 && t.isVisible()) {
          if (!t.isWall()) {
            argTerm.withColor(SColor.TRANSPARENT, SColorFactory.dimmest(background)).fill(cX, cY, 1,
                1);
          }

          if (!determinedActors) {
            Actor a = t.getActor();

            if (a != null && !(a instanceof Player)) {
              targets.add(a);
            }
          }
        }
      }
    }

    if (!determinedActors && startTarget == null) {
      targetNearestEnemy();
    }

    determinedActors = true;
  }

  /**
   * Sets the cursor position to the enemy nearest to the player
   */
  private void targetNearestEnemy() {
    startTarget = targets.getAll().stream().sorted((t1, t2) -> {
      return Double.compare(t1.getPosition().distance(this.initialPosition),
          t2.getPosition().distance(this.initialPosition));
    }).findFirst().orElse(null);

    if (startTarget != null) {
      pos.setLocation(startTarget.getPosition());
    }
  }
}
