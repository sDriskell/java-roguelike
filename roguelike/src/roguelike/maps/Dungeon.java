package roguelike.maps;

/**
 * 
 */
public class Dungeon extends MapArea {

  private static final long serialVersionUID = 627520295057387283L;

  private boolean hasSpecialFloors;
  private String name;
  private int currentFloor;
  private int totalFloors;

  /**
   * 
   * @param argWidth
   * @param argHeight
   * @param argMapBuild
   * @param argDif
   * @param argTotFlrs
   */
  public Dungeon(int argWidth, int argHeight, MapBuilderBase argMapBuild, int argDif, int argTotFlrs) {
    super(argWidth, argHeight, argMapBuild);
    difficulty = argDif;
    currentFloor = 1;
    totalFloors = argTotFlrs;
  }

}
