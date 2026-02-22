package roguelike.screens;

import java.awt.Rectangle;

import roguelike.Cursor;
import roguelike.Game;
import roguelike.functionalinterfaces.CursorCallback;
import roguelike.maps.MapArea;
import roguelike.ui.windows.TerminalBase;
import roguelike.util.Coordinate;

/**
 * 
 */
public class CursorScreen extends Screen {

  protected Cursor cursor;
  protected CursorCallback resultCallback;
  protected TerminalBase cloneTerminal;

  /**
   * 
   * @param argTerm
   * @param argCur
   * @param argCallBack
   */
  public CursorScreen(TerminalBase argTerm, Cursor argCur, CursorCallback argCallBack) {
    super(argTerm);

    if (argCur == null) {
      throw new IllegalArgumentException("cursor cannot be null");
    }

    if (argCallBack == null) {
      throw new IllegalArgumentException("resultCallback cannot be null");
    }

    cursor = argCur;
    resultCallback = argCallBack;
    cloneTerminal = argTerm.cloneTerminal();
    cursor.show();
  }

  @Override
  public void process() {
    if (cursor.process()) {
      resultCallback.setResult(cursor.result());
      restorePreviousScreen();
    }
  }

  @Override
  protected final void onDraw() {
    Rectangle drawableArea = getDrawableArea();
    Game game = Game.current();
    MapArea currentMap = game.getCurrentMapArea();
    Coordinate centerPos = game.getCenterScreenPosition();
    Rectangle screenArea = currentMap.getVisibleAreaInTiles(drawableArea.width, drawableArea.height,
        centerPos);

    /* redraw the previous terminal data */
    cloneTerminal.refresh(screenArea.x, screenArea.y, screenArea.width, screenArea.height);
    onDrawAdditional(currentMap, centerPos, screenArea);
    cursor.draw(terminal, screenArea);
  }

  /**
   * 
   * @param argCurMap
   * @param argCntrPos
   * @param argScreenArea
   */
  protected void onDrawAdditional(MapArea argCurMap, Coordinate argCntrPos,
      Rectangle argScreenArea) {
  }
}
