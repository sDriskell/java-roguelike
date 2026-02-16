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
   * @param argWth
   * @param argHgt
   * @param argMapBuild
   * @param argDif
   * @param argTotFlrs
   */
  public Dungeon(int argWth, int argHgt, MapBuilderBase argMapBuild, int argDif, int argTotFlrs) {
    super(argWth, argHgt, argMapBuild);
    difficulty = argDif;
    currentFloor = 1;
    totalFloors = argTotFlrs;
  }

}
