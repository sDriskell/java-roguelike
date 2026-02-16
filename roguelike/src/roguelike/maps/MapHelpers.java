package roguelike.maps;

import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import roguelike.util.Symbol;
import squidpony.squidmath.RNG;

/**
 * 
 */
public class MapHelpers {

  private MapHelpers() {
    // Utility class; no constructor needed.
  }

  /**
   * 
   * @param argMap
   * @param x
   * @param y
   * @param argRange
   * @return
   */
  public static List<Point> getNeighbors(MapArea argMap, int x, int y, int argRange) {
    return getNeighbors(argMap.getWidth(), argMap.getHeight(), x, y, argRange);
  }

  /**
   * 
   * @param argW
   * @param argH
   * @param x
   * @param y
   * @param argRange
   * @return
   */
  public static List<Point> getNeighbors(int argW, int argH, int x, int y, int argRange) {
    int size = argRange * 2 + 1;
    ArrayList<Point> neighbors = new ArrayList<>(size * size + 1);

    for (int zy = y - argRange; zy <= y + argRange; zy++) {
      for (int zx = x - argRange; zx <= x + argRange; zx++) {

        if (zx == x && zy == y) {
          continue;
        }

        if (zx >= 0 && zy >= 0 && zx < argW && zy < argH) {
          neighbors.add(new Point(zx, zy));
        }
      }
    }

    return neighbors;
  }

  /**
   * 
   * @param argMap
   * @param x
   * @param y
   * @param argAreActAllowed
   * @return
   */
  public static boolean isBlocked(MapArea argMap, int x, int y, boolean argAreActAllowed) {
    Tile tile = argMap.getTileAt(x, y);

    if (tile == null) {
      return true;
    }

    return (tile.getActor() != null && !argAreActAllowed) || !tile.canPass();
  }

  /**
   * 
   * @param argStartX
   * @param argStartY
   * @param argEndX
   * @param argEndY
   * @return
   */
  public static float distance(int argStartX, int argStartY, int argEndX, int argEndY) {
    return (float) Math.abs(argEndX - argStartX) + Math.abs(argEndY - argStartY);
  }

  /**
   * 
   * @param p1
   * @param p2
   * @return
   */
  public static float distance(Point p1, Point p2) {
    return distance(p1.x, p1.y, p2.x, p2.y);
  }

  /**
   * 
   * @param argStartX
   * @param argStartY
   * @param argEndX
   * @param argEndY
   * @return
   */
  public static float distanceSq(int argStartX, int argStartY, int argEndX, int argEndY) {
    int a = Math.abs(argEndX - argStartX);
    int b = Math.abs(argEndY - argStartY);

    return (float) Math.sqrt((a * a) + (float) (b * b));
  }

  /**
   * 
   * @param argRnd
   * @param argP1
   * @param argP2
   * @return
   */
  public static Queue<Point> findPath(RNG argRnd, Point argP1, Point argP2) {
    Queue<Point> points = new LinkedList<>();
    int xDist = argP2.x - argP1.x;
    int yDist = argP2.y - argP1.y;
    Point curPoint = (Point) argP1.clone();

    while (curPoint.x != argP2.x || curPoint.y != argP2.y) {
      if (curPoint.x != argP2.x && curPoint.y != argP2.y) {
        if (argRnd.nextBoolean()) {
          curPoint.x += Math.signum(xDist);
        }
        else {
          curPoint.y += Math.signum(yDist);
        }

      }
      else if (curPoint.x != argP2.x) {
        curPoint.x += Math.signum(xDist);

      }
      else if (curPoint.y != argP2.y) {
        curPoint.y += Math.signum(yDist);
      }

      points.add(new Point(curPoint.x, curPoint.y));
    }

    return points;
  }

  /**
   * 
   * @param argMap
   * @param x
   * @param y
   * @param argTile
   * @param argNoDiags
   * @return
   */
  public static int getAdjacentTiles(Tile[][] argMap, int x, int y, Symbol argTile,
      boolean argNoDiags) {
    int count = 0;
    List<Point> neighbors = getNeighbors(argMap.length, argMap[0].length, x, y, 1);

    for (Point p : neighbors) {
      if (argMap[p.x][p.y].symbol == argTile.symbol()) {

        if (argNoDiags) {
          /* diagonals */
          if (x != p.x && y != p.y) {
            continue;
          }
        }

        count++;
      }
    }

    return count;
  }

  /**
   * 
   * @param argPt
   * @param argW
   * @param argH
   */
  public static void constrainToRectangle(Point argPt, int argW, int argH) {
    if (argPt.x < 0) {
      argPt.x = 0;
    }
    if (argPt.y < 0) {
      argPt.y = 0;
    }
    if (argPt.x >= argW) {
      argPt.x = argW - 1;
    }
    if (argPt.y >= argH) {
      argPt.y = argH - 1;
    }
  }

  /**
   * 
   * @param map
   * @param x
   * @param y
   * @return
   */
  public static boolean isWithinBounds(Tile[][] map, int x, int y) {
    return !(x < 0 || y < 0 || x >= map.length || y >= map[0].length);
  }

  /**
   * 
   * @param argW
   * @param argH
   * @param x
   * @param y
   * @return
   */
  public static boolean isWithinBounds(int argW, int argH, int x, int y) {
    return !(x < 0 || y < 0 || x >= argW || y >= argH);
  }
}
