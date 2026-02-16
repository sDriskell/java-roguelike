package roguelike.maps;

import java.awt.Point;

import squidpony.squidgrid.util.DirectionCardinal;

/**
 * 
 */
public class ConnectionPoint extends Point {

  private static final long serialVersionUID = 6448455982964320426L;

  private DirectionCardinal direction;
  private boolean isConnected;
  private Room room;
  private Room connectedRoom;
  public boolean isDoor;

  /**
   * 
   * @param x
   * @param y
   * @param argDir
   * @param argRoom
   */
  public ConnectionPoint(int x, int y, DirectionCardinal argDir, Room argRoom) {
    super(x, y);
    direction = argDir;
    room = argRoom;
  }

  /**
   * 
   * @param argPnt
   * @param argDir
   * @param argRoom
   */
  public ConnectionPoint(Point argPnt, DirectionCardinal argDir, Room argRoom) {
    super(argPnt);
    direction = argDir;
    room = argRoom;
  }

  /**
   * 
   * @param argRoom
   */
  public void connectTo(Room argRoom) {
    if (argRoom == null) {
      throw new IllegalArgumentException("connectTo room cannot be null");
    }

    connectedRoom = argRoom;
    isConnected = true;
  }

  /**
   * 
   * @return
   */
  public DirectionCardinal direction() {
    return direction;
  }

  /**
   * 
   * @return
   */
  public boolean isConnected() {
    return isConnected;
  }

  /**
   * 
   * @return
   */
  public Room room() {
    return room;
  }

  /**
   * 
   * @return
   */
  public Room connectedRoom() {
    return connectedRoom;
  }
}
