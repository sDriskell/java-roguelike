package roguelike.ui;

import roguelike.Cursor;
import roguelike.maps.MapArea;
import roguelike.screens.LookScreen;
import roguelike.util.Coordinate;

/**
 * 
 */
public class LookCursor extends Cursor {

  private LookScreen lookScreen;

  /**
   * 
   * @param argInitPos
   * @param argArea
   */
  public LookCursor(Coordinate argInitPos, MapArea argArea) {
    super(argInitPos, argArea);
  }

  public void setLookScreen(LookScreen argScreen) {
    lookScreen = argScreen;
    setCurrentLookPoint(pos);
  }

  @Override
  protected boolean onUpdatePosition(Coordinate argPos) {
    setCurrentLookPoint(argPos);

    return true;
  }

  /**
   * 
   * @param argPos
   */
  private void setCurrentLookPoint(Coordinate argPos) {
    // Make sure that only visible tiles can be looked at
    if (!mapArea.getTileAt(argPos).isVisible()) {
      lookScreen.lookAt(mapArea, null);
      return;
    }

    if (mapArea.getActorAt(argPos.x, argPos.y) != null) {
      lookScreen.lookAt(mapArea, argPos);
      return;
    }
    else if (mapArea.getItemsAt(argPos.x, argPos.y).any()) {
      lookScreen.lookAt(mapArea, argPos);
      return;
    }

    lookScreen.lookAt(mapArea, null);
  }
}
