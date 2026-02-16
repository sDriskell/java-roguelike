package roguelike.maps;

import roguelike.util.Log;
import roguelike.util.Symbol;
import squidpony.squidcolor.SColor;
import squidpony.squidcolor.SColorFactory;
import squidpony.squidutility.ProbabilityTable;

/**
 * 
 */
public class TileBuilder {

  private ProbabilityTable<Character> trees;
  private ProbabilityTable<Character> ground;
  private ProbabilityTable<Character> water;
  private ProbabilityTable<SColor> waterColor;
  private ProbabilityTable<SColor> groundColor;

  /**
   * 
   */
  public TileBuilder() {
    trees = new ProbabilityTable<>();
    ground = new ProbabilityTable<>();
    water = new ProbabilityTable<>();

    waterColor = new ProbabilityTable<>();
    waterColor.add(SColor.DARK_BLUE, 10);
    waterColor.add(SColor.BLUE, 3);
    waterColor.add(SColorFactory.desaturate(SColor.DARK_BLUE, 0.2), 6);

    groundColor = new ProbabilityTable<>();
    groundColor.add(SColorFactory.dimmer(SColor.DARK_GRAY), 10);
    groundColor.add(SColorFactory.desaturate(SColor.BLACK, 0.4), 4);
    groundColor.add(SColorFactory.desaturate(SColor.BLACK_CHESTNUT_OAK, 0.8), 2);

    trees.add(Symbol.TREE2.symbol(), 15);
    trees.add(Symbol.TREE1.symbol(), 7);

    water.add(Symbol.WATER.symbol(), 20);

    ground.add(Symbol.GROUND1.symbol(), 30);
    ground.add(Symbol.GROUND2.symbol(), 1);
  }

  /**
   * 
   * @param argChar
   * @return
   */
  public Tile buildTile(Symbol argChar) {
    Tile t = new Tile();

    switch (argChar) {
      case WALL:
        t.setValues(argChar.symbol(), false, SColor.LIGHT_YELLOW_DYE, true).setLighting(1f);
        break;

      case DOOR:
        return new Door().setValues(argChar.symbol(), false, SColor.BIRCH_BROWN, true);

      case TREE:
        t.setValues(trees.random(), true, SColor.KELLY_GREEN).setLighting(0.5f)
            .setSpeedModifier(-10);
        break;

      case BUILDING_FLOOR:
        t.setValues(argChar.symbol(), true, SColor.EARTHEN_YELLOW);
        break;

      case WATER:
        t.setValues(water.random(), false,
            SColorFactory.blend(waterColor.random(), SColorFactory.asSColor(50, 150, 255), .5))
            .setLighting(0f).setBackground(waterColor.random());
        break;

      case SHALLOW_WATER:
        t.setValues(water.random(), true,
            SColorFactory.blend(waterColor.random(), SColorFactory.asSColor(30, 100, 255), .5))
            .setLighting(0f).setSpeedModifier(-15)
            .setBackground(SColorFactory.desaturate(waterColor.random(), 0.1));
        break;

      case MOUNTAIN:
        t.setValues(argChar.symbol(), false, SColor.WHITE_MOUSE, true);
        break;

      case HILLS:
        t.setValues(argChar.symbol(), true, SColor.BENI_DYE).setLighting(0);
        break;

      case DUNGEON_FLOOR:
        t.setValues(argChar.symbol(), true, SColor.AUBURN).setLighting(0.1f);
        break;

      case GROUND:
        t.setValues(ground.random(), true, SColorFactory.asSColor(50, 200, 100));
        break;

      case STAIRS_DOWN:
        return new Stairs(new DungeonMapBuilder(), true).setValues(argChar.symbol(), true,
            SColor.WHITE);

      case STAIRS_UP:
        return new Stairs(new DungeonMapBuilder(), false).setValues(argChar.symbol(), true,
            SColor.WHITE);

      case BOX_BOTTOM_LEFT_SINGLE:
        // TODO: change the character and make this a torch or something
        t.setValues(argChar.symbol(), false, SColor.ORANGE, true);
        break;

      default:
        t = buildTile(argChar.symbol());
    }

    return t;
  }

  /**
   * 
   * @param argTile
   * @return
   */
  Tile buildTile(char argTile) {
    Tile t = new Tile();

    // TODO: refactor char values in case to make readable without comments
    switch (argTile) {
      case '#': // wall
        t.setValues(Symbol.WALL.symbol(), false, SColor.DARK_GRAY, true).setLighting(1f);
        break;

      case '+': // door
        return new Door().setValues(argTile, false, SColor.BIRCH_BROWN, true);

      case 'T':
        t.setValues(trees.random(), true, SColor.KELLY_GREEN).setLighting(0.5f)
            .setSpeedModifier(-15);
        break;

      case '=': // floor of building
        t.setValues(',', true, SColor.EARTHEN_YELLOW);
        break;

      case '~': // water
        t.setValues(water.random(), false,
            SColorFactory.blend(waterColor.random(), SColorFactory.asSColor(50, 150, 255), .5))
            .setLighting(0f).setBackground(waterColor.random());
        break;

      case 'M': // mountain
        t.setValues('M', false, SColor.WHITE_MOUSE, true);
        break;

      case '*': // hills
        t.setValues('.', true, SColor.BENI_DYE).setLighting(0);
        break;

      case '-': // dungeon floor
        t.setValues('.', true, SColor.BOILED_RED_BEAN_BROWN);
        break;

      case '.': // ground
      default:
        Log.warning("Could not find a tile for character " + argTile + ": " + (int) argTile);
        t.setValues(ground.random(), true, SColorFactory.asSColor(50, 200, 100));
        break;
    }

    return t;
  }
}
