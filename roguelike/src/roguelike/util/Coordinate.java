package roguelike.util;

import java.awt.Point;

import squidpony.squidgrid.util.DirectionIntercardinal;
import squidpony.squidgrid.util.RadiusStrategy;

//TODO: remove serialization
/**
 * 
 */
public class Coordinate extends Point {

  private static final long serialVersionUID = 9154408085653477925L;

  /**
   * 
   */
  public Coordinate() {
    // TODO: magic numbers
    this(0, 0);
  }

  /**
   * 
   * @param x
   * @param y
   */
  public Coordinate(int x, int y) {
    super(x, y);
  }

  /**
   * 
   * @param x
   * @param y
   */
  public void setPosition(int x, int y) {
    this.x = x;
    this.y = y;
  }

  /**
   * 
   * @param argXAmt
   * @param argYAmt
   */
  public void offsetPosition(int argXAmt, int argYAmt) {
    x += argXAmt;
    y += argYAmt;
  }

  /**
   * 
   * @param argXAmt
   * @param argYAmt
   * @return
   */
  public Coordinate createOffsetPosition(int argXAmt, int argYAmt) {
    return new Coordinate(x + argXAmt, y + argYAmt);
  }

  /**
   * 
   * @param argDir
   * @return
   */
  public Coordinate createOffsetPosition(DirectionIntercardinal argDir) {
    return createOffsetPosition(argDir.deltaX, argDir.deltaY);
  }

  /**
   * 
   * @param x
   * @param y
   * @return
   */
  public boolean isPosition(int x, int y) {
    return this.x == x && this.y == y;
  }

  /**
   * 
   * @param argOther
   * @param argRadStrat
   * @return
   */
  public float distance(Point argOther, RadiusStrategy argRadStrat) {
    return argRadStrat.radius(x, y, argOther.x, argOther.y);
  }
}
