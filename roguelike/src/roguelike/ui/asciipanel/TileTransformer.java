package roguelike.ui.asciipanel;

/**
 * 
 */
public interface TileTransformer {

  /**
   * 
   * @param x
   * @param y
   * @param argData
   */
  public void transformTile(int x, int y, AsciiCharacterData argData);
}