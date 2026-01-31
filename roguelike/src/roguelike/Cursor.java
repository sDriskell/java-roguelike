package roguelike;

import java.awt.Rectangle;

import roguelike.maps.MapArea;
import roguelike.ui.InputCommand;
import roguelike.ui.InputManager;
import roguelike.ui.windows.TerminalBase;
import roguelike.util.Coordinate;
import squidpony.squidcolor.SColor;
import squidpony.squidgrid.util.BasicRadiusStrategy;
import squidpony.squidgrid.util.DirectionIntercardinal;
import squidpony.squidgrid.util.RadiusStrategy;

/**
 * 
 */
public class Cursor {

  private boolean isActive;

  protected RadiusStrategy radiusStrategy = BasicRadiusStrategy.SQUARE;
  protected char symbol;
  protected SColor color;
  protected Coordinate pos;
  protected MapArea mapArea;
  protected CursorResult result;
  protected int maxRadius = 0;

  /**
   * 
   * @param argInitPos
   * @param argMapArea
   */
  public Cursor(Coordinate argInitPos, MapArea argMapArea) {
    mapArea = argMapArea;
    symbol = '_';
    color = SColor.INDIGO_DYE;
    pos = new Coordinate(argInitPos.x, argInitPos.y);
  }

  /**
   * 
   * @param argInitPos
   * @param argMapArea
   * @param argMaxRadius
   */
  public Cursor(Coordinate argInitPos, MapArea argMapArea, int argMaxRadius) {
    this(argInitPos, argMapArea);
    maxRadius = argMaxRadius;
  }

  /**
   * 
   * @return
   */
  public final boolean waitingForResult() {
    return isActive;
  }

  /**
   * 
   */
  public final void show() {
    isActive = true;
    onShow();
  }

  /**
   * 
   * @param argTerm
   * @param argScrnArea
   */
  public final void draw(TerminalBase argTerm, Rectangle argScrnArea) {
    if (argTerm == null) {
      throw new IllegalArgumentException("terminal cannot be null");
    }

    if (!argScrnArea.contains(pos)) {
      int px = (int) Math.max(argScrnArea.getMinX(), Math.min(pos.x, argScrnArea.getMaxX() - 1));
      int py = (int) Math.max(argScrnArea.getMinY(), Math.min(pos.y, argScrnArea.getMaxY() - 1));
      setPosition(px, py);
    }

    int sx = pos.x - argScrnArea.x;
    int sy = pos.y - argScrnArea.y;
    onDraw(argTerm, sx, sy);
  }

  /**
   * 
   * @return
   */
  public final boolean process() {
    CursorResult processed = onProcess();

    if (processed != null) {
      isActive = false;
      this.result = processed;
    }
    return !isActive;
  }

  /**
   * 
   * @return
   */
  public final CursorResult result() {
    return result;
  }

  protected void onShow() {
    // TODO: is this necessary and what to do with it?
  }

  protected void onDraw(TerminalBase terminal, int sx, int sy) {
    terminal.withColor(SColor.TRANSPARENT, color).fill(sx, sy, 1, 1);
    MapArea mapArea = Game.current().getCurrentMapArea();

    if (!mapArea.getTileAt(pos).isExplored()) {
      terminal.withColor(SColor.TRANSPARENT, color).put(sx, sy, ' ');
    }
  }

  protected final CursorResult onProcess() {
    InputCommand cmd = InputManager.nextCommand();
    if (cmd != null) {
      DirectionIntercardinal direction = cmd.toDirection();

      if (direction != DirectionIntercardinal.NONE) {
        Coordinate newPosition = pos.createOffsetPosition(direction);

        if (isWithinBounds(newPosition)) {
          setPosition(newPosition);
        }

      }
      else {
        switch (cmd) {
          case CONFIRM:
            result = new CursorResult(pos, false);
            break;
          case CANCEL:
            result = new CursorResult(pos, true);
            break;
          default:
            result = onProcessCommand(cmd);
        }
      }
    }
    return result;
  }

  private void setPosition(int x, int y) {
    pos.x = x;
    pos.y = y;
  }

  private void setPosition(Coordinate argPos) {
    pos.x = argPos.x;
    pos.y = argPos.y;
  }

  private boolean isWithinBounds(Coordinate argPos) {
    if (mapArea.isWithinBounds(argPos.x, argPos.y) && onUpdatePosition(argPos)) {
      if (maxRadius > 0) {
        Coordinate playerLocation = Game.current().getPlayer().getPosition();
        float distance = playerLocation.distance(argPos, radiusStrategy);
        return distance <= maxRadius;
      }
      return true;
    }
    return false;
  }

  /**
   * Return false if the cursor can't be moved to the desired position, i.e.
   * outside field of vision
   * 
   * @return
   */
  protected boolean onUpdatePosition(Coordinate argPos) {
    return true;
  }

  /**
   * Allow derived cursors to implement additional commands (for instance, to
   * select a target)
   * 
   * @param cmd
   * @return
   */
  protected CursorResult onProcessCommand(InputCommand cmd) {
    return null;
  }
}
