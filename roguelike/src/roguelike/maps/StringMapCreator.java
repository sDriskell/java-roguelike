package roguelike.maps;

import roguelike.util.CurrentItemTracker;

/**
 * 
 */
public class StringMapCreator {

  private String mapTiles;
  private TileBuilder tileBuilder;
  private CurrentItemTracker<Character> tiles;

  /**
   * 
   * @param argTiles
   */
  public StringMapCreator(String argTiles) {
    mapTiles = argTiles;
    tileBuilder = new TileBuilder();
    tiles = new CurrentItemTracker<>();

    for (int x = 0; x < this.mapTiles.length(); x++) {
      tiles.add(this.mapTiles.charAt(x));
    }
  }

  /**
   * 
   * @return
   */
  public Tile nextTile() {
    tiles.advance();
    return tileBuilder.buildTile(tiles.getCurrent());
  }
}
