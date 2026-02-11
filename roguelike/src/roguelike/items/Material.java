package roguelike.items;

/**
 * 
 */
public enum Material {
  WOOD("wooden"),
  STONE("stone"),
  BRONZE("bronze"),
  IRON("iron"),
  STEEL("steel");

  public final String name;

  /**
   * 
   * @param argName
   */
  Material(String argName) {
    name = argName;
  }
}
