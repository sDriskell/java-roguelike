package roguelike.maps;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

import roguelike.Game;
import roguelike.util.Symbol;
import roguelike.util.WeightedCollection;
import squidpony.squidmath.PerlinNoise;
import squidpony.squidmath.RNG;
import squidpony.squidutility.SCollections;

//TODO: lots of weighted values that are used but unsure what they are
/**
 * 
 */
public class MapBuilder extends MapBuilderBase {

  private static final long serialVersionUID = 1L;

  private ArrayList<Rectangle> buildings = new ArrayList<>();

  /**
   * 
   */
  protected MapBuilder() {
    super("Outside");
  }

  @Override
  public void onBuildMap(Tile[][] map) {
    int width = map.length;
    int height = map[0].length;

    // fill in edges with walls
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        if (x == 0 || y == 0 || x == width - 1 || y == height - 1) {
          map[x][y] = tb.buildTile(Symbol.WALL);
        }
        else {
          map[x][y] = tb.buildTile(Symbol.GROUND);
        }
      }
    }

    ArrayList<Point> startingPoints = createLandscape(random, map);

    for (int i = 0; i < 30; i++) {
      int x = random.between(0, width);
      int y = random.between(0, height);
      createBuilding(map, x, y);
    }

    Point playerPos = SCollections.getRandomElement(startingPoints);

    // place stairs
    Game.current().getPlayer().setPosition(playerPos.x, playerPos.y);
  }

  /**
   * 
   * @param argMap
   * @param x
   * @param y
   */
  private void createBuilding(Tile[][] argMap, int x, int y) {
    int w = random.between(5, 40);
    int h = random.between(5, 40);
    Rectangle mapBounds = new Rectangle(0, 0, argMap.length, argMap[0].length);
    Rectangle buildingBounds = new Rectangle(x, y, w, h);

    if (!mapBounds.contains(buildingBounds)) {
      return;
    }

    for (Rectangle building : buildings) {
      if (building.intersects(buildingBounds)) {
        return;
      }
    }

    int doorX;
    int doorY;

    if (random.nextDouble() < 0.5) {
      // door on vertical axis
      doorX = random.nextDouble() < 0.5 ? 0 : w - 1;
      doorY = random.between(2, h - 2);
    }
    else {
      // door on horizontal axis
      doorX = random.between(2, w - 2);
      doorY = random.nextDouble() < 0.5 ? 0 : h - 1;
    }

    doorX += x;
    doorY += y;

    for (int bx = x; bx < buildingBounds.getMaxX(); bx++) {
      for (int by = y; by < buildingBounds.getMaxY(); by++) {
        if (bx == doorX && by == doorY) {
          argMap[bx][by] = tb.buildTile(Symbol.DOOR);
        }
        else if (bx == x || bx == buildingBounds.getMaxX() - 1 || by == y
            || by == buildingBounds.getMaxY() - 1) {
          argMap[bx][by] = tb.buildTile(Symbol.WALL);
        }
        else {
          argMap[bx][by] = tb.buildTile(Symbol.BUILDING_FLOOR);
        }
      }
    }

    buildings.add(buildingBounds);
  }

  /**
   * 
   * @param argRng
   * @param argMap
   * @return
   */
  private ArrayList<Point> createLandscape(RNG argRng, Tile[][] argMap) {
    int w = argMap.length;
    int h = argMap[0].length;
    float z = argRng.nextFloat();
    float factor = (float) argRng.between(0.005, 0.03);

    /* Collection whose max weight should not be greater than 100 */
    WeightedCollection<Symbol> tiles = new WeightedCollection<>();
    tiles.add(Symbol.WATER, -50);
    tiles.add(Symbol.TREE, 30);
    tiles.add(Symbol.GROUND, 40);
    tiles.add(Symbol.HILLS, 50);
    tiles.add(Symbol.MOUNTAIN, 90);
    // TODO: max weight for each symbol must be 100 or less or total collection
    // should be 100 or less?

    ArrayList<Point> validStartingPoints = new ArrayList<>();

    for (int x = 0; x < w; x++) {
      for (int y = 0; y < h; y++) {
        float x1 = x * factor;
        float y1 = y * factor;

        double oct1 = (1 / 15f) * PerlinNoise.noise(x1, y1, z);
        double oct2 = (2 / 15f) * PerlinNoise.noise(x1 * 2, y1 * 2, z);
        double oct3 = (4 / 15f) * PerlinNoise.noise(x1 * 4, y1 * 4, z);
        double oct4 = (8 / 15f) * PerlinNoise.noise(x1 * 8, y1 * 8, z);

        double total = oct1 + oct2 + oct3 + oct4;
        float t = ((float) (total) * 100);
        Symbol tileChar = tiles.getItem((int) t);

        if (tileChar != Symbol.WATER) {
          validStartingPoints.add(new Point(x, y));
        }

        argMap[x][y] = tb.buildTile(tileChar);
      }
    }

    return validStartingPoints;
  }
}
