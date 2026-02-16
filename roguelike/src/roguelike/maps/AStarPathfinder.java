package roguelike.maps;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import roguelike.util.Log;

/**
 * 
 * TODO: javadoc for attribute notes
 * <li><b>closed</b> set of nodes that have been searched through</li>
 * <li><b>open</b> set of nodes that we do not yet consider fully searched</li>
 * <li><b>map</b> the map being searched</li>
 * <li><b>maxSearchDistance</b> the maximum depth of search we're willing to
 * accept before giving up</li>
 * <li><b>nodes</b> the complete set of nodes across the map</li>
 */
public class AStarPathfinder {

  private ArrayList<Node> closed = new ArrayList<>();
  private SortedList open = new SortedList();
  private MapArea map;
  private int maxSearchDistance;
  private Node[][] nodes;

  /**
   * Create a path finder
   * 
   * @param heuristic The heuristic used to determine the search order of the map
   * @param argMap The map to be searched
   * @param argMaxDist The maximum depth we'll search before giving up
   * @param allowDiagMovement True if the search should try diag movement
   */
  public AStarPathfinder(MapArea argMap, int argMaxDist) {
    map = argMap;
    maxSearchDistance = argMaxDist;
    nodes = new Node[argMap.getWidth()][argMap.getHeight()];

    for (int x = 0; x < argMap.getWidth(); x++) {
      for (int y = 0; y < argMap.getHeight(); y++) {
        nodes[x][y] = new Node(x, y);
      }
    }
  }

  /**
   * 
   * @see PathFinder#findPath(Mover, int, int, int, int)
   * @param argMap
   * @param sx
   * @param sy
   * @param tx
   * @param ty
   * @return
   */
  public Path findPath(MapArea argMap, int sx, int sy, int tx, int ty) {
    Log.verboseDebug("Finding path from " + sx + "," + sy + " to " + tx + "," + ty);

    /*
     * initial state for A*. The closed group is empty. Only the starting tile is in
     * the open list and it's cost is zero, i.e. we're already there
     */
    nodes[sx][sy].cost = 0;
    nodes[sx][sy].depth = 0;
    closed.clear();
    open.clear();
    open.add(nodes[sx][sy]);
    nodes[tx][ty].parent = null;

    // while we haven't found the goal and haven't exceeded our max search depth
    int maxDepth = 0;

    while ((maxDepth < maxSearchDistance) && (open.size() != 0)) {
      /*
       * pull out the first node in our open list, this is determined to be the most
       * likely to be the next step based on our heuristic
       */
      Node current = getFirstInOpen();

      if (current == nodes[tx][ty]) {
        break;
      }

      removeFromOpen(current);
      addToClosed(current);

      List<Point> neighbors = MapHelpers.getNeighbors(argMap, current.x, current.y, 1);

      /*
       * search through all the neighbours of the current node evaluating them as next
       * steps
       */
      for (Point n : neighbors) {
        int xp = n.x;
        int yp = n.y;

        float nextStepCost = current.cost + getMovementCost(current.x, current.y, xp, yp);
        Node neighbor = nodes[xp][yp];

        if (nextStepCost < neighbor.cost) {
          if (inOpenList(neighbor)) {
            removeFromOpen(neighbor);
          }

          if (inClosedList(neighbor)) {
            removeFromClosed(neighbor);
          }
        }

        if (!inOpenList(neighbor) && !inClosedList(neighbor)) {
          neighbor.cost = nextStepCost;
          neighbor.heuristic = (float) MapHelpers.distance(xp, yp, tx, ty);
          maxDepth = Math.max(maxDepth, neighbor.setParent(current));
          addToOpen(neighbor);
        }
      }
    }

    /*
     * since we've got an empty open list or we've run out of search there was no
     * path. Just return null
     */
    if (nodes[tx][ty].parent == null) {
      return null;
    }

    /*
     * At this point we've definitely found a path so we can uses the parent
     * references of the nodes to find out way from the target location back to the
     * start recording the nodes on the way.
     */
    Path path = new Path();
    Node tgt = nodes[tx][ty];

    while (tgt != nodes[sx][sy]) {
      path.prependStep(tgt.x, tgt.y);
      tgt = tgt.parent;
    }

    path.prependStep(sx, sy);
    return path;
  }

  /**
   * Get the first element from the open list. This is the next one to be
   * searched.
   * 
   * @return The first element in the open list
   */
  protected Node getFirstInOpen() {
    return (Node) open.first();
  }

  /**
   * Add a node to the open list
   * 
   * @param argNode The node to be added to the open list
   */
  protected void addToOpen(Node argNode) {
    open.add(argNode);
  }

  /**
   * Check if a node is in the open list
   * 
   * @param argNode The node to check for
   * @return True if the node given is in the open list
   */
  protected boolean inOpenList(Node argNode) {
    return open.contains(argNode);
  }

  /**
   * Remove a node from the open list
   * 
   * @param argNode The node to remove from the open list
   */
  protected void removeFromOpen(Node argNode) {
    open.remove(argNode);
  }

  /**
   * Add a node to the closed list
   * 
   * @param argNode The node to add to the closed list
   */
  protected void addToClosed(Node argNode) {
    closed.add(argNode);
  }

  /**
   * Check if the node supplied is in the closed list
   * 
   * @param argNode The node to search for
   * @return True if the node specified is in the closed list
   */
  protected boolean inClosedList(Node argNode) {
    return closed.contains(argNode);
  }

  /**
   * Remove a node from the closed list
   * 
   * @param agNode The node to remove from the closed list
   */
  protected void removeFromClosed(Node agNode) {
    closed.remove(agNode);
  }

  // TODO: this is very obtuse to read, should rewrite in the affirmative
  /**
   * Check if a given location is valid for the supplied mover
   * 
   * @param mover The mover that would hold a given location
   * @param sx The starting x coordinate
   * @param sy The starting y coordinate
   * @param x The x coordinate of the location to check
   * @param y The y coordinate of the location to check
   * @return True if the location is valid for the given mover
   */
  protected boolean isValidLocation(int sx, int sy, int x, int y) {
    boolean invalid = !map.isWithinBounds(x, y);

    if ((!invalid) && ((sx != x) || (sy != y))) {
      invalid = MapHelpers.isBlocked(map, x, y, true);
    }

    return !invalid;
  }

  /**
   * Get the cost to move through a given location
   * 
   * @param mover The entity that is being moved
   * @param sx The x coordinate of the tile whose cost is being determined
   * @param sy The y coordiante of the tile whose cost is being determined
   * @param tx The x coordinate of the target location
   * @param ty The y coordinate of the target location
   * @return The cost of movement through the given tile
   */
  public float getMovementCost(int sx, int sy, int tx, int ty) {
    return ((map.getActorAt(tx, ty) != null) || !map.getTileAt(tx, ty).canPass()) ? 999 : 0;
  }

  /**
   * Get the heuristic cost for the given location. This determines in which order
   * the locations are processed.
   * 
   * @param mover The entity that is being moved
   * @param x The x coordinate of the tile whose cost is being determined
   * @param y The y coordiante of the tile whose cost is being determined
   * @param tx The x coordinate of the target location
   * @param ty The y coordinate of the target location
   * @return The heuristic cost assigned to the tile
   */
  public float getHeuristicCost(int x, int y, int tx, int ty) {
    return (float) Math.floor(MapHelpers.distanceSq(x, y, tx, ty));
  }

  /**
   * A simple sorted list
   * 
   * @author Kevin (who is Kevin?)
   */
  private class SortedList {

    private ArrayList<Node> list = new ArrayList<>();

    /**
     * Retrieve the first element from the list
     * 
     * @return The first element from the list
     */
    public Object first() {
      return list.get(0);
    }

    /**
     * Empty the list
     */
    public void clear() {
      list.clear();
    }

    /**
     * Add an element to the list - causes sorting
     * 
     * @param argNode The element to add
     */
    public void add(Node argNode) {
      list.add(argNode);
      Collections.sort(list);
    }

    /**
     * Remove an element from the list
     * 
     * @param argNode The element to remove
     */
    public void remove(Node argNode) {
      list.remove(argNode);
    }

    /**
     * Get the number of elements in the list
     * 
     * @return The number of element in the list
     */
    public int size() {
      return list.size();
    }

    /**
     * Check if an element is in the list
     * 
     * @param argNode The element to search for
     * @return True if the element is in the list
     */
    public boolean contains(Node argNode) {
      return list.contains(argNode);
    }
  }

  /**
   * A single node in the search graph
   * <li><b>x</b> coord of the node</li>
   * <li><b>y</b> coord of the node</li>
   * <li><b>cost</b> path cost for this node</li>
   * <li><b>parent</b> node's parent, how we reached it in the search</li>
   * <li><b>heuristic</b> heuristic cost of this node</li>
   * <li><b>depth</b> the search depth of this node</li>
   * 
   */
  private class Node implements Comparable<Node> {
    private int x;
    private int y;
    private float cost;
    private Node parent;
    private float heuristic;
    private int depth;

    /**
     * Create a new node
     * 
     * @param argX The x coordinate of the node
     * @param argY The y coordinate of the node
     */
    public Node(int argX, int argY) {
      x = argX;
      y = argY;
    }

    /**
     * Set the parent of this node
     * 
     * @param argParent The parent node which lead us to this node
     * @return The depth we have no reached in searching
     */
    public int setParent(Node argParent) {
      depth = argParent.depth + 1;
      parent = argParent;
      return depth;
    }

    /**
     * @see Comparable#compareTo(Node)
     */
    public int compareTo(Node argOther) {
      Node o = argOther;

      float f = heuristic + cost;
      float of = o.heuristic + o.cost;

      if (f < of) {
        return -1;
      }
      else if (f > of) {
        return 1;
      }
      else {
        return 0;
      }
    }

    /**
     * @see Object#equals(Object)
     */
    public boolean equals(Object argOther) {
      if (argOther instanceof Node) {
        Node o = (Node) argOther;
        return (o.x == x) && (o.y == y);
      }

      return false;
    }
  }

}
