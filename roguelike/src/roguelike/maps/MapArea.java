package roguelike.maps;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.List;

import roguelike.Game;
import roguelike.actors.Actor;
import roguelike.actors.EnemyFactory;
import roguelike.items.Inventory;
import roguelike.items.Item;
import roguelike.ui.windows.TerminalBase;
import roguelike.util.Coordinate;
import roguelike.util.CurrentItemTracker;
import roguelike.util.Log;
import squidpony.squidcolor.SColor;
import squidpony.squidmath.RNG;

//TODO: remove Serializable implementation
/**
 * 
 */
public class MapArea implements Serializable {

  private static final long serialVersionUID = 1L;

  private Tile[][] map;
  private float[][] lightResistances;
  private boolean[][] walls;
  protected CurrentItemTracker<Actor> actors;
  protected int width;
  protected int height;
  protected int difficulty; // controls how difficult random enemies are here
  protected String name;

  /**
   * 
   * @param argW
   * @param argH
   * @param argMapBldr
   */
  protected MapArea(int argW, int argH, MapBuilderBase argMapBldr) {
    actors = new CurrentItemTracker<>();
    width = argW;
    height = argH;
    difficulty = 1;
    buildMapArea(argMapBldr);
  }

  /**
   * 
   * @param argW
   * @param argH
   * @param argMapBldr
   * @return
   */
  public static MapArea build(int argW, int argH, MapBuilderBase argMapBldr) {
    return new Dungeon(argW, argH, argMapBldr, 1, 10);
  }

  /**
   * 
   * @param in
   * @throws ClassNotFoundException
   * @throws IOException
   */
  private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
    in.defaultReadObject();
    Log.debug("Read map");
  }

  /**
   * 
   * @return
   */
  public int getWidth() {
    return width;
  }

  /**
   * 
   * @return
   */
  public int getHeight() {
    return this.height;
  }

  /**
   * 
   * @return
   */
  public String getName() {
    return this.name;
  }

  /**
   * 
   */
  public void spawnMonsters() {
    Log.verboseDebug("spawnMonsters");
    int maxActors = 10;

    if (actors.count() < maxActors && Game.current().random().nextInt(10) > 6) {
      /* create a new one somewhere close to the player */
      Coordinate position = findRandomNonVisibleTile();

      if (position != null) {
        Actor npc = EnemyFactory.createEnemy(position.x, position.y, difficulty);

        if (addActor(npc)) {
          Game
              .current()
              .displayMessage(npc.getName() + " created at " + position.x + ", " + position.y,
                  SColor.ALOEWOOD_BROWN);
        }
      }
    }
  }

  /**
   * 
   * @return
   */
  private Coordinate findRandomNonVisibleTile() {
    Coordinate playerPos = Game.current().getPlayer().getPosition();
    RNG rng = Game.current().random();

    for (int i = 0; i < 5; i++) {
      int x = rng.between(playerPos.x - 50, playerPos.x + 50);
      int y = rng.between(playerPos.y - 50, playerPos.y + 50);

      x = Math.max(0, Math.min(x, width - 1));
      y = Math.max(0, Math.min(y, height - 1));

      Tile tile = getTileAt(x, y);

      if (!tile.visible && !tile.isWall() && tile.canPass()) {
        return new Coordinate(x, y);
      }
    }

    return null;
  }

  /**
   * 
   * @return
   */
  public float[][] getLightValues() {
    return lightResistances;
  }

  /**
   * 
   * @return
   */
  public boolean[][] getWalls() {
    return walls;
  }

  /**
   * Updates internal arrays tracking light values and walls for FOV calculations.
   * This should not change very often.
   */
  public void updateValues() {
    lightResistances = new float[width][height];
    walls = new boolean[width][height];

    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        lightResistances[x][y] = map[x][y].getLighting();
        walls[x][y] = map[x][y].isWall();
      }
    }
  }

  /**
   * Determines the location of the upper-left position of the visible area, based
   * on the provided screen size and center point (generally the player's
   * location).
   * 
   * @param argScnCellsX The width of the screen in cells
   * @param argScnCellsY The height of the screen in cells
   * @param argCenter The point at which the screen should be centered on
   * @return The location of the upper left point, in cells, after adjusting for
   * map boundaries
   */
  public Coordinate getUpperLeftScreenTile(int argScnCellsX, int argScnCellsY,
      Coordinate argCenter) {
    int left = (int) Math.round(argCenter.x - (argScnCellsX / 2.0));
    int top = (int) Math.round(argCenter.y - (argScnCellsY / 2.0));

    left = Math.min(Math.max(left, 0), Math.max(width - argScnCellsX, 0));
    top = Math.min(Math.max(top, 0), Math.max(height - argScnCellsY, 0));

    return new Coordinate(left, top);
  }

  /**
   * Determines the visible screen area, in cells
   * 
   * @param argScnCellsX The width of the screen in cells
   * @param argScnCellsY The height of the screen in cells
   * @param argCenter The point at which the screen is centered on
   * @return A Rectangle representing the area that should be drawn to the screen,
   * in cells
   */
  public Rectangle getVisibleAreaInTiles(int argScnCellsX, int argScnCellsY, Coordinate argCenter) {
    Coordinate upperLeft = getUpperLeftScreenTile(argScnCellsX, argScnCellsY, argCenter);
    int w = Math.min(width - upperLeft.x, argScnCellsX);
    int h = Math.min(height - upperLeft.y, argScnCellsY);

    return new Rectangle(upperLeft.x, upperLeft.y, w, h);
  }

  /**
   * 
   * @param argTerm
   * @param argCenter
   * @return
   */
  public Rectangle getVisibleAreaInTiles(TerminalBase argTerm, Coordinate argCenter) {
    return getVisibleAreaInTiles(argTerm.size().width, argTerm.size().height, argCenter);
  }

  /**
   * Adds an item to the tile at x,y
   * 
   * @param argItm
   * @param x
   * @param y
   */
  public void addItem(Item argItm, int x, int y) {
    Inventory items = getItemsAt(x, y);
    items.add(argItm);
  }

  /**
   * Removes an item from the tile at x,y
   * 
   * @param argItm
   * @param x
   * @param y
   * @return True if the item was removed, false if there are no items on the tile
   * or the specific item wasn't in the list.
   */
  public boolean removeItem(Item argItm, int x, int y) {
    Inventory items = getItemsAt(x, y);

    if (!items.any()) {
      Log.warning("Failed! no items at " + x + "," + y);
      return false;
    }

    return items.remove(argItm);
  }

  /**
   * Returns an Inventory object with all the items at the specified tile
   * 
   * @param x
   * @param y
   * @return
   */
  public Inventory getItemsAt(int x, int y) {
    Tile tile = getTileAt(x, y);
    return tile.getItems();
  }

  /**
   * Returns a list of all actors in this map
   * 
   * @return
   */
  public List<Actor> getAllActors() {
    return actors.getAll();
  }

  /**
   * Returns the actor that is currently waiting to act.
   * 
   * @return
   */
  public Actor getCurrentActor() {
    return actors.getCurrent();
  }

  /**
   * 
   * @return
   */
  public Actor peekNextActor() {
    return actors.peek();
  }

  /**
   * Advances the current actor to the next in the queue.
   */
  public void nextActor(String argReason) {
    actors.advance();
  }

  /**
   * Returns the actor at the given position.
   * 
   * @param x
   * @param y
   * @return
   */
  public Actor getActorAt(int x, int y) {
    return isWithinBounds(x, y) ? getTileAt(x, y).getActor() : null;
  }

  /**
   * Adds an actor to this map.
   * 
   * @param argAct
   * @return True if the actor was added, false if there was already an actor at
   * the location specified by actor.getPosition().
   */
  public boolean addActor(Actor argAct) {
    Coordinate pos = argAct.getPosition();
    Tile tile = getTileAt(pos.x, pos.y);

    if (tile.getActor() != null) {
      return false;
    }

    actors.add(argAct);
    tile.setActor(argAct);
    return true;
  }

  /**
   * Moves an actor from one tile to another.
   * 
   * @param argAct
   * @param argNewPos
   * @return True if the move was successful, false otherwise.
   */
  public boolean moveActor(Actor argAct, Coordinate argNewPos) {
    Coordinate pos = argAct.getPosition();
    Tile tile = getTileAt(pos.x, pos.y);

    if (tile.getActor() != null && tile.moveActorTo(getTileAt(argNewPos))) {
      argAct.setPosition(argNewPos.x, argNewPos.y);
      return true;
    }

    return false;

  }

  /**
   * Removes an actor from the map.
   * 
   * @param argAct
   * @return True if the actor could be removed, false otherwise (for instance, if
   * the tile at the actor's position actually has no actor, which probably
   * indicates a bug)
   */
  public boolean removeActor(Actor argAct) {
    Log.debug("Removing actor " + argAct.getName());
    Coordinate pos = argAct.getPosition();
    Tile tile = getTileAt(pos.x, pos.y);

    if (tile.getActor() == null) {
      Log.warning("Failed!  actor=" + argAct.getName());
      return false;
    }

    Log.debug("Success!");

    actors.remove(argAct);
    Log.debug("     > actors count: " + actors.getAll().size());
    tile.setActor(null);
    return true;
  }

  /**
   * Returns the tile at the given position.
   * 
   * @param argPos
   * @return
   */
  public Tile getTileAt(Point argPos) {
    return getTileAt(argPos.x, argPos.y);
  }

  /**
   * 
   * @param x
   * @param y
   * @return
   */
  public Tile getTileAt(int x, int y) {
    return isWithinBounds(x, y) ? map[x][y] : null;
  }

  /**
   * 
   * @param argPos
   * @return
   */
  public int getSpeedModifier(Coordinate argPos) {
    return isWithinBounds(argPos.x, argPos.y) ? map[argPos.x][argPos.y].speedModifier : 0;
  }

  /**
   * Determines if the actor can move to the specified position.
   * 
   * @param argAct
   * @param argPos
   * @return True if a move is allowed, false otherwise.
   */
  public boolean canMoveTo(Actor argAct, Coordinate argPos) {
    return canMoveTo(argAct, argPos.x, argPos.y);
  }

  /**
   * Determines if the actor can move to the specified position.
   * 
   * @param argAct
   * @param x
   * @param y
   * @return True if a move is allowed, false otherwise.
   */
  public boolean canMoveTo(Actor argAct, int x, int y) {
    if (isWithinBounds(x, y)) {
      Tile tile = map[x][y];

      if (tile.canPass()) {
        return argAct.onMoveAttempting(this, tile);
      }
    }

    return false;
  }

  /**
   * 
   * @param argPos
   * @return
   */
  public boolean isVisible(Point argPos) {
    Tile tile = getTileAt(argPos);
    return tile != null && tile.isVisible();
  }

  /**
   * Returns true if the given location is within the boundaries of this map.
   * 
   * @param x
   * @param y
   * @return
   */
  public boolean isWithinBounds(int x, int y) {
    return !(x < 0 || x >= width || y < 0 || y >= height);
  }

  /**
   * Populates this map's tiles.
   * 
   * @param argMapBldr The MapBuilder used to construct this map.
   */
  private void buildMapArea(MapBuilderBase argMapBldr) {
    map = new Tile[width][height];
    name = argMapBldr.buildMap(map);
    updateValues();

    // TODO: pathfinding precalculations?

  }

}
