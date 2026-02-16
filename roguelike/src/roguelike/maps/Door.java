package roguelike.maps;

/**
 * 
 */
public class Door extends Tile {

  private static final long serialVersionUID = 1L;

  private boolean isOpen;

  /**
   * 
   * @param argMap
   */
  public void open(MapArea argMap) {
    if (!isOpen) {
      isOpen = true;
      isPassable = true;
      wall = false;
      lighting = 0f;

      argMap.updateValues();
    }
  }

  /**
   * 
   * @param argMap
   */
  public void close(MapArea argMap) {
    if (isOpen) {
      isOpen = false;
      isPassable = false;
      wall = true;
      lighting = 1f;

      argMap.updateValues();
    }
  }

  @Override
  public char getSymbol() {
    if (getActor() != null && visible) {
      return getActor().symbol();
    }

    return isOpen ? '/' : symbol;
  }
}
