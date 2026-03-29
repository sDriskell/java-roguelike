package roguelike.maps;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Stack;

import roguelike.Game;
import roguelike.util.CollectionUtils;
import roguelike.util.Log;
import roguelike.util.Symbol;
import squidpony.squidcolor.SColor;
import squidpony.squidgrid.util.DirectionCardinal;
import squidpony.squidutility.ProbabilityTable;

/**
 * 
 */
public class DungeonMapBuilder extends MapBuilderBase {

  private static final long serialVersionUID = 1L;

  private transient ArrayList<Room> rooms;
  private transient ArrayList<MapSection> mapSections;
  private int level;

  /**
   * 
   */
  public DungeonMapBuilder() {
    // TODO: magic numberish
    this(1);
  }

  /**
   * 
   * @param argLvl
   */
  public DungeonMapBuilder(int argLvl) {
    super("Dungeon, floor " + argLvl);
    rooms = new ArrayList<>();
    level = argLvl;
  }

  /**
   * 
   * @param in
   * @throws IOException
   * @throws ClassNotFoundException
   */
  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    in.defaultReadObject();
    rooms = new ArrayList<>();
  }

  @Override
  protected void onBuildMap(Tile[][] map) {
    int roomCount = 20;
    int maxTries = 50;

    for (int i = 0; i < maxTries; i++) {
      rooms.clear();

      mapSections = new ArrayList<>();
      mapSections.add(new MapSection(getSubRectangle(mapRect, 0, 0, .25, .25)));
      mapSections.add(new MapSection(getSubRectangle(mapRect, mapRect.width / 4, 0, .25, .25)));
      mapSections.add(new MapSection(
          getSubRectangle(mapRect, mapRect.width / 4, mapRect.height / 4, .25, .25)));
      mapSections.add(new MapSection(getSubRectangle(mapRect, 0, mapRect.height / 4, .25, .25)));

      fillMap(Symbol.WALL);
      Room startRoom = null;

      while (startRoom == null) {
        startRoom = chooseRandomStartRoom();
      }

      rooms.add(startRoom);

      // TODO: add the stairs going up at the player's starting position

      int startX = (int) startRoom.area.getCenterX();
      int startY = (int) startRoom.area.getCenterY();
      Game.current().getPlayer().setPosition(startX, startY);
      addStairsUp(new Point(startX, startY));

      int roomsGen = generateMainPath(startRoom);
      roomsGen += generateRandomRooms();

      if (roomsGen >= roomCount) {
        break;
      }
    }

    createRandomPools();
  }

  /**
   * 
   * @return
   */
  private Room chooseRandomStartRoom() {
    MapSection startInSection = randomMapSection();
    Rectangle startingArea = getRandomRectangleInside(startInSection.area);

    if (canCreateRoom(startingArea)) {
      return createRoom(startingArea);
    }

    return null;
  }

  /**
   * 
   * @param argStartRm
   * @return
   */
  private int generateMainPath(Room argStartRm) {
    int roomsGenerated = 0;
    int maxRooms = 15;
    Room currentRoom = null;
    Stack<Room> path = new Stack<>();
    path.push(argStartRm);
    currentRoom = argStartRm;

    for (int x = 0; x < maxRooms; x++) {
      if (path.isEmpty()) {
        break;
      }

      boolean doesFail = false;
      DirectionCardinal dir = null;
      Rectangle area = null;

      for (int i = 0; i < 10; i++) {
        doesFail = false;
        dir = getRandomDirection();
        Point initLoc = currentRoom.area.getLocation();
        area = getRectangleForRoom(dir, initLoc);

        if (!canCreateRoom(area)) {
          doesFail = true;
        }

        if (!doesFail) {
          break;
        }

      }

      if (dir == null || area == null) {
        continue;
      }

      ConnectionPoint door = generateRandomDoor(currentRoom, dir);

      if (door == null) {
        doesFail = true;
      }

      if (!doesFail) {
        Rectangle rect = area;
        Room newRoom = createRoom(rect);
        ConnectionPoint endPoint = buildCorridor(door, newRoom, area);

        if (endPoint != null) {
          setTile(door, Symbol.DUNGEON_FLOOR);
          setTile(endPoint, Symbol.DUNGEON_FLOOR);

          if (random.nextBoolean()) {
            setDoor(door);
          }

          currentRoom.doors.add(door);
          addRoom(newRoom);
          path.push(currentRoom);
          currentRoom = newRoom;
          roomsGenerated++;
        }
        else {
          doesFail = true;
          System.out.println("endPoint==null, x=" + x);
        }
      }

      if (doesFail) {
        for (ConnectionPoint doorPoint : currentRoom.doors) {
          setDoor(doorPoint);
          roomsGenerated--;
        }

        currentRoom = path.pop();
      }
    }

    /* put the stairs in the last room we generated */
    Point stairs = currentRoom.getRandomFloorTile();
    addStairsDown(stairs);
    return roomsGenerated;
  }

  private int generateRandomRooms() {
    int roomsGenerated = 0;
    int maxRooms = 20;

    Log.debug("Room count: " + rooms.size());

    for (int x = 0; x < maxRooms; x++) {

      Room randomRoom = CollectionUtils.getRandomElement(rooms);

      boolean fail = false;
      DirectionCardinal direction = null;
      Rectangle area = null;

      for (int i = 0; i < 10; i++) {
        fail = false;
        direction = getRandomDirection();
        Point initialLocation = randomRoom.area.getLocation();

        area = getRectangleForRoom(direction, initialLocation);

        if (!canCreateRoom(area)) {
          fail = true;
        }
        if (!fail)
          break;
      }
      if (direction == null || area == null || fail)
        continue;

      ConnectionPoint door = generateRandomDoor(randomRoom, direction);
      if (door == null)
        fail = true;

      if (!fail) {
        Rectangle rect = area;

        Room newRoom = createRoom(rect);

        ConnectionPoint endPoint = buildCorridor(door, newRoom, area);

        if (endPoint != null) {

          setTile(door, Symbol.BUILDING_FLOOR);
          setTile(endPoint, Symbol.BUILDING_FLOOR);

          if (random.nextBoolean())
            setDoor(door);

          randomRoom.doors.add(door);

          addRoom(newRoom);

          roomsGenerated++;

          connectToRandomRoom(randomRoom);
        }
        else {

          fail = true;
          System.out.println("endPoint==null, x=" + x);
        }
      }
    }

    return roomsGenerated;
  }

  /**
   * 
   * @param argDir
   * @param argInitLoc
   * @return
   */
  private Rectangle getRectangleForRoom(DirectionCardinal argDir, Point argInitLoc) {
    Rectangle area;
    int xOffset;
    int yOffset;
    area = new Rectangle(argInitLoc);

    area.width = random.between(6, 15);
    area.height = random.between(4, 10);

    xOffset = (argDir.deltaX * (area.width));
    yOffset = (argDir.deltaY * (area.height));

    xOffset += random.between(-3, 3);
    yOffset += random.between(-3, 3);

    area.x += xOffset;
    area.y += yOffset;
    return area;
  }

  /**
   * Creates a ConnectionPoint for a door in the given direction.
   * 
   * @param argRoom
   * @param argDir
   * @return
   */
  private ConnectionPoint generateRandomDoor(Room argRoom, DirectionCardinal argDir) {
    Point doorPoint = argRoom.getDoorCoordinate(map, argDir);
    return new ConnectionPoint(doorPoint, argDir, argRoom);
  }

  /**
   * Fills the map with DUNGEON_FLOOR inside the given Rectangle and returns a
   * Room with that area.
   * 
   * @param argArea
   * @return
   */
  private Room createRoom(Rectangle argArea) {
    Room room = null;
    room = new Room(argArea);
    room.fillRoom(map, tb, Symbol.DUNGEON_FLOOR);
    return room;
  }

  /**
   * 
   * @param argOrgPt
   * @param argRoom
   * @param argTgtArea
   * @return
   */
  private ConnectionPoint buildCorridor(ConnectionPoint argOrgPt, Room argRoom,
      Rectangle argTgtArea) {
    DirectionCardinal direction = argOrgPt.direction();

    Point endPoint = new Point(argOrgPt.x + (direction.deltaX), argOrgPt.y + (direction.deltaY));
    Point constrained = new Point(endPoint);
    MapHelpers.constrainToRectangle(constrained, mapRect.width - 1, mapRect.height - 1);

    if (!endPoint.equals(constrained)) {
      Log.debug("cannot construct corridor");
      return null;
    }

    setTile(endPoint, Symbol.DUNGEON_FLOOR);
    Rectangle floorTargetArea = new Rectangle(argTgtArea);
    floorTargetArea.grow(-1, -1);

    if (!floorTargetArea.contains(endPoint)) {
      boolean yFirst = random.nextBoolean();
      int targetX = (int) random.between(argTgtArea.getMinX() + 2, argTgtArea.getMaxX() - 2);
      int targetY = (int) random.between(argTgtArea.getMinY() + 2, argTgtArea.getMaxY() - 2);

      int xOffset = (int) (Math.signum((float) targetX - endPoint.x));
      int yOffset = (int) (Math.signum((float) targetY - endPoint.y));

      if (yFirst) {
        while (endPoint.y != targetY) {
          endPoint.translate(0, yOffset);
          setTile(endPoint, Symbol.DUNGEON_FLOOR);
        }
        while (endPoint.x != targetX) {
          endPoint.translate(xOffset, 0);
          setTile(endPoint, Symbol.DUNGEON_FLOOR);
        }
      }
      else {
        while (endPoint.x != targetX) {
          endPoint.translate(xOffset, 0);
          setTile(endPoint, Symbol.DUNGEON_FLOOR);
        }
        while (endPoint.y != targetY) {
          endPoint.translate(0, yOffset);
          setTile(endPoint, Symbol.DUNGEON_FLOOR);
        }
      }
    }
    return new ConnectionPoint(endPoint, direction, argRoom);
  }

  /**
   * 
   * @param argRoom
   * @return
   */
  private boolean connectToRandomRoom(Room argRoom) {
    if (random.nextDouble() < 0.3) {
      return false;
    }

    for (int x = 0; x < 3; x++) {
      Room randomRoom = CollectionUtils.getRandomElement(rooms);

      if (randomRoom == null) {
        return false;
      }

      int maxDistance = 25;

      if (argRoom.area.getLocation().distance(randomRoom.area.getLocation()) <= maxDistance) {
        // try to connect them

        if (argRoom.doors.stream().anyMatch(d -> d.isDoor)) {
          if (randomRoom.doors.stream().anyMatch(d -> d.isDoor)) {
            return false;
          }
        }

        ConnectionPoint randomDoor = CollectionUtils.getRandomElement(argRoom.doors);

        if (randomDoor != null) {
          ConnectionPoint endPoint = buildCorridor(randomDoor, argRoom, randomRoom.area);

          if (endPoint == null) {
            Log.debug("connect to random room, null endpoint");
          }
          else {
            Log.debug("endPoint=" + endPoint);
          }

          return true;
        }
      }
    }

    return false;
  }

  /**
   * 
   * @return
   */
  private MapSection randomMapSection() {
    ProbabilityTable<MapSection> sections = new ProbabilityTable<>();

    for (MapSection section : mapSections) {
      sections.add(section,
          (int) (((section.floorSpaces / (float) section.totalSpaces) + 1) * 100));
    }

    return sections.random();
  }

  /**
   * 
   * @param argPt
   */
  private void addStairsUp(Point argPt) {
    map[argPt.x][argPt.y] = new Stairs(this, false).setValues(Symbol.STAIRS_UP.getArtifact(), true,
        SColor.WHITE);
  }

  /**
   * 
   * @param argPt
   */
  private void addStairsDown(Point argPt) {
    map[argPt.x][argPt.y] = new Stairs(new DungeonMapBuilder(level + 1), true)
        .setValues(Symbol.STAIRS_DOWN.getArtifact(), true, SColor.WHITE);
  }

  /**
   * 
   * @param argRoom
   */
  private void addRoom(Room argRoom) {
    rooms.add(argRoom);
  }

  /**
   * 
   * @param argDoorPt
   */
  private void setDoor(ConnectionPoint argDoorPt) {
    map[argDoorPt.x][argDoorPt.y] = tb.buildTile(Symbol.DOOR);
    Log.debug("Created door at " + argDoorPt.x + ", " + argDoorPt.y);
    argDoorPt.isDoor = true;
  }

  /**
   * 
   */
  private void createRandomPools() {
    int numPools = random.between(1, 20);

    for (int i = 0; i < numPools; i++) {
      Room randomRoom = CollectionUtils.getRandomElement(rooms);

      int sx = randomRoom.getRandomX();
      int sy = randomRoom.getRandomY();
      int turns = random.between(4, 10);

      createPool(sx, sy, turns);
    }

  }

  /**
   * 
   * @param x
   * @param y
   * @param argCount
   */
  private void createPool(int x, int y, int argCount) {
    if (argCount <= 0 || !MapHelpers.isWithinBounds(map, x, y)) {
      return;
    }

    if (map[x][y].symbol == Symbol.DUNGEON_FLOOR.getArtifact()) {
      setTile(x, y, Symbol.SHALLOW_WATER);
    }

    if (random.nextBoolean()) {
      createPool(x - 1, y, argCount - 1);
    }
    if (random.nextBoolean()) {
      createPool(x + 1, y, argCount - 1);
    }
    if (random.nextBoolean()) {
      createPool(x, y - 1, argCount - 1);
    }
    if (random.nextBoolean()) {
      createPool(x, y + 1, argCount - 1);
    }
  }

  /**
   * Divide the map up into equal sections to try and get a reasonably even
   * distribution of rooms
   * 
   * @author John
   *
   */
  private class MapSection {
    public Rectangle area;
    public int floorSpaces;
    public int totalSpaces;

    /**
     * 
     * @param argArea
     */
    public MapSection(Rectangle argArea) {
      area = argArea;
      floorSpaces = 0;
      totalSpaces = argArea.width * argArea.height;
    }

    /**
     * 
     * @param argPt
     * @return
     */
    public boolean contains(Point argPt) {
      return area.contains(argPt);
    }

    /**
     * 
     * @param argRect
     */
    public void add(Rectangle argRect) {
      floorSpaces += (argRect.width * argRect.height);
    }
  }
}
