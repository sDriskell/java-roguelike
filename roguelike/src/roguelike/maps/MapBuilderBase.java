package roguelike.maps;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import roguelike.Game;
import roguelike.util.Symbol;
import squidpony.squidgrid.util.DirectionCardinal;
import squidpony.squidmath.RNG;

//TODO: remove Serializable implementation
/**
 * 
 */
public abstract class MapBuilderBase implements Serializable {

  private static final long serialVersionUID = 1L;

  protected transient RNG random = Game.current().random();
  protected transient TileBuilder tb = new TileBuilder();
  protected transient Tile[][] map;
  protected transient int width;
  protected transient int height;
  protected transient Rectangle mapRect;
  protected String mapName;

  /**
   * 
   * @param argMapname
   */
  protected MapBuilderBase(String argMapname) {
    mapName = argMapname;
  }

  public final String buildMap(Tile[][] argMap) {
    map = argMap;
    width = argMap.length;
    height = argMap[0].length;
    mapRect = new Rectangle(0, 0, width, height);

    onBuildMap(argMap);

    return mapName;
  }

  /**
   * 
   * @param out
   * @throws IOException
   */
  private void writeObject(ObjectOutputStream out) throws IOException {
    out.defaultWriteObject();
  }

  /**
   * 
   * @param in
   * @throws IOException
   * @throws ClassNotFoundException
   */
  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    in.defaultReadObject();
    tb = new TileBuilder();
    random = Game.current().random();
  }

  /**
   * Returns true if the area represented by the rectangle is all walls so we can
   * make a room here
   * 
   * @param argRct
   * @return
   */
  protected boolean canCreateRoom(Rectangle argRct) {
    if (!mapRect.contains(argRct)) {
      return false;
    }

    for (int x = (int) argRct.getMinX(); x < argRct.getMaxX(); x++) {
      for (int y = (int) argRct.getMinY(); y < argRct.getMaxY(); y++) {
        if (!map[x][y].isWall()) {
          return false;
        }
      }
    }

    return true;
  }

  /**
   * Returns a randomly chosen DirectionCardinal.
   * 
   * @return
   */
  protected DirectionCardinal getRandomDirection() {
    return DirectionCardinal.CARDINALS[random.between(0, 4)];
  }

  /**
   * Returns a Point that is randomly chosen from within the given Rectangle's
   * area.
   * 
   * @param argRect
   * @return
   */
  protected Point getRandomPoint(Rectangle argRect) {
    int x = (int) random.between(argRect.getMinX(), argRect.getMaxX());
    int y = (int) random.between(argRect.getMinY(), argRect.getMaxY());

    return new Point(x, y);
  }

  /**
   * Returns a rectangle of random size and location within the specified
   * containing rectangle.
   * 
   * @param argContain
   * @return
   */
  protected Rectangle getRandomRectangleInside(Rectangle argContain) {
    int w = random.betweenWeighted(1, argContain.width - 1, 5);
    int h = random.betweenWeighted(1, argContain.height - 1, 5);
    int x = random.betweenWeighted((int) argContain.getMinX(), (int) argContain.getMaxX() - w, 5);
    int y = random.betweenWeighted((int) argContain.getMinY(), (int) argContain.getMaxY() - h, 5);

    return new Rectangle(x, y, w, h);
  }

  /**
   * Returns a rectangle that is the specified percentage of the original
   * rectangle in width and height.
   * 
   * @param argOrg
   * @param argNewX
   * @param argNewY
   * @param argXperc
   * @param argYperc
   * @return
   */
  protected Rectangle getSubRectangle(Rectangle argOrg, int argNewX, int argNewY, double argXperc,
      double argYperc) {
    Rectangle newRect = new Rectangle(argOrg);
    newRect.setLocation(argNewX, argNewY);
    newRect.setSize((int) Math.floor(argOrg.width * argXperc),
        (int) Math.floor(argOrg.height * argYperc));

    return newRect;
  }

  /**
   * Fills entire map with the given character
   * 
   * @param map
   */
  protected void fillMap(Symbol argChar) {
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        map[x][y] = tb.buildTile(argChar);
      }
    }
  }

  /**
   * Fills the area given by rect with the specified character.
   * 
   * @param argRect
   * @param argChar
   */
  protected void fillRect(Rectangle argRect, Symbol argChar) {
    int startX = (int) argRect.getMinX();
    int startY = (int) argRect.getMinY();
    int endX = (int) argRect.getMaxX();
    int endY = (int) argRect.getMaxY();

    for (int x = startX; x < endX; x++) {
      for (int y = startY; y < endY; y++) {
        map[x][y] = tb.buildTile(argChar);
      }
    }
  }

  /**
   * 
   * @param argArea
   * @return
   */
  protected int getRandomX(Rectangle argArea) {
    return (int) random.between(argArea.getMinX(), argArea.getMaxX());
  }

  /**
   * 
   * @param argArea
   * @return
   */
  protected int getRandomY(Rectangle argArea) {
    return (int) random.between(argArea.getMinY(), argArea.getMaxY());
  }

  /**
   * 
   * @param x
   * @param y
   * @param argChar
   */
  protected void setTile(int x, int y, Symbol argChar) {
    if (MapHelpers.isWithinBounds(map, x, y)) {
      map[x][y] = tb.buildTile(argChar);
    }
  }

  /**
   * 
   * @param argPt
   * @param argChar
   */
  protected void setTile(Point argPt, Symbol argChar) {
    if (map[argPt.x][argPt.y].symbol == Symbol.WALL.symbol()) {
      map[argPt.x][argPt.y] = tb.buildTile(argChar);
    }
  }

  /**
   * 
   * @param argMap
   */
  protected abstract void onBuildMap(Tile[][] argMap);
}
