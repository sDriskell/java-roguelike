package roguelike.maps;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

import roguelike.Game;
import roguelike.util.CollectionUtils;
import roguelike.util.Symbol;
import squidpony.squidgrid.util.DirectionCardinal;
import squidpony.squidmath.RNG;
import squidpony.squidutility.ProbabilityTable;

/**
 * 
 */
public class Room {

  protected final RNG random = Game.current().random();

  protected final ArrayList<Point> floorTiles;
  public final ArrayList<ConnectionPoint> doors;
  public final Rectangle area;

  /**
   * 
   * @param argArea
   */
  public Room(Rectangle argArea) {
    area = argArea;
    doors = new ArrayList<>();
    floorTiles = new ArrayList<>();
  }

  /**
   * 
   * @return
   */
  public int bottom() {
    return (int) area.getMaxY() - 1;
  }

  /**
   * 
   * @return
   */
  public int top() {
    return (int) area.getMinY();
  }

  /**
   * 
   * @return
   */
  public int left() {
    return (int) area.getMinX();
  }

  /**
   * 
   * @return
   */
  public int right() {
    return (int) area.getMaxX() - 1;
  }

  /**
   * 
   * @return
   */
  public int getRandomX() {
    return (int) random.between(area.getMinX() + 1, area.getMaxX() - 1);
  }

  /**
   * 
   * @return
   */
  public int getRandomY() {
    return (int) random.between(area.getMinY() + 1, area.getMaxY() - 1);
  }

  /**
   * 
   * @return
   */
  public Point getRandomFloorTile() {
    return floorTiles.size() > 1 ? floorTiles.get(random.between(0, floorTiles.size())) : null;
  }

  /**
   * 
   * @param argMap
   * @param argDir
   * @return
   */
  public Point getDoorCoordinate(Tile[][] argMap, DirectionCardinal argDir) {
    Point p = null;

    switch (argDir) {
      case DOWN:
        p = new Point(this.getRandomX(), this.bottom());
        break;
      case UP:
        p = new Point(this.getRandomX(), this.top());
        break;
      case LEFT:
        p = new Point(this.left(), this.getRandomY());
        break;
      case RIGHT:
        p = new Point(this.right(), this.getRandomY());
        break;
      default:
        return null;
    }

    return p;
  }

  /**
   * 
   * @param argMap
   * @param argDir
   * @return
   */
  public Point addRandomDoorToRoom(Tile[][] argMap, DirectionCardinal argDir) {
    ConnectionPoint p = new ConnectionPoint(getDoorCoordinate(argMap, argDir), argDir, this);

    if (argMap[p.x][p.y].isWall()) {
      this.doors.add(p);
      return p;
    }

    return null;
  }

  /**
   * 
   * @param argPt
   * @param argDoorDirection
   * @return
   */
  public Point getDoorFrom(Point argPt, DirectionCardinal argDoorDirection) {
    switch (argDoorDirection) {
      case DOWN:
        return new Point(argPt.x, this.bottom());
      case UP:
        return new Point(argPt.x, this.top());
      case LEFT:
        return new Point(this.left(), argPt.y);
      case RIGHT:
        return new Point(this.right(), argPt.y);
      default:
        return null;
    }
  }

  /**
   * 
   * @param argDir
   * @return
   */
  public Point getExistingDoor(DirectionCardinal argDir) {
    final Point start;
    final Point end;

    switch (argDir) {
      case DOWN:
        start = new Point(this.left(), this.bottom());
        end = new Point(this.right(), this.bottom());
        break;
      case UP:
        start = new Point(this.left(), this.top());
        end = new Point(this.right(), this.top());
        break;
      case LEFT:
        start = new Point(this.left(), this.top());
        end = new Point(this.left(), this.bottom());
        break;
      case RIGHT:
        start = new Point(this.right(), this.top());
        end = new Point(this.right(), this.bottom());
        break;
      default:
        return null;
    }

    List<Point> candidates = doors.stream()
        .filter(d -> d.x == start.x || d.x == end.x || d.y == start.y || d.y == end.y)
        .collect(Collectors.toList());

    if (candidates.isEmpty()) {
      return CollectionUtils.getRandomElement(candidates);
    }

    return null;
  }

  /**
   * 
   * @param argMap
   * @param argBuilder
   * @param argTile
   */
  public void fillRoom(Tile[][] argMap, TileBuilder argBuilder, Symbol argTile) {
    Rectangle rect = this.area;

    for (int x = (int) rect.getMinX() + 1; x < rect.getMaxX() - 1; x++) {
      for (int y = (int) rect.getMinY() + 1; y < rect.getMaxY() - 1; y++) {
        argMap[x][y] = argBuilder.buildTile(argTile);

        if (!argMap[x][y].isWall() && isFloorAdjacentToWall(argMap, x, y)) {
          floorTiles.add(new Point(x, y));
        }
      }
    }
  }

  /**
   * 
   * @param argMap
   * @param argBuilder
   * @param argTiles
   */
  public void fillRoom(Tile[][] argMap, TileBuilder argBuilder, ProbabilityTable<Symbol> argTiles) {
    Rectangle rect = area;

    for (int x = (int) rect.getMinX() + 1; x < rect.getMaxX() - 1; x++) {
      for (int y = (int) rect.getMinY() + 1; y < rect.getMaxY() - 1; y++) {
        argMap[x][y] = argBuilder.buildTile(argTiles.random());

        if (!argMap[x][y].isWall() && isFloorAdjacentToWall(argMap, x, y)) {
          floorTiles.add(new Point(x, y));
        }
      }
    }
  }

  /**
   * 
   * @param argPt
   */
  protected void addFloorTile(Point argPt) {
    if (area.contains(argPt) && !floorTiles.contains(argPt)) {
      floorTiles.add(argPt);
    }
  }

  /**
   * 
   * @param argMap
   * @param x
   * @param y
   * @return
   */
  protected boolean isFloorAdjacentToWall(Tile[][] argMap, int x, int y) {
    return ((x > 0 && argMap[x - 1][y].isWall()) || (y > 0 && argMap[x][y - 1].isWall())
        || (x < argMap.length - 1 && argMap[x + 1][y].isWall())
        || (y < argMap.length - 1 && argMap[x][y + 1].isWall()));
  }

  /**
   * 
   * @param argPts
   * @param arcTile
   * @param argRoom
   * @param argMap
   * @param argBuilder
   */
  protected void fillPath(Queue<Point> argPts, char arcTile, Room argRoom, Tile[][] argMap,
      TileBuilder argBuilder) {
    Point p = argPts.poll();

    while (p != null) {
      argRoom.addFloorTile(p);
      argMap[p.x][p.y] = argBuilder.buildTile(arcTile);
      p = argPts.poll();
    }
  }
}