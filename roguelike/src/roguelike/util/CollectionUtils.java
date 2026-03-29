package roguelike.util;

import java.util.List;

import roguelike.Game;
import squidpony.squidmath.RNG;

/**
 * 
 */
public class CollectionUtils {

  private static final RNG rng = Game.current().random();

  private CollectionUtils() {
    // Utility class
  }

  /**
   * Returns a random element from the provided list. If the list is empty then
   * null is returned.
   *
   * @param <T>
   * @param argList
   * @return
   */
  public static <T> T getRandomElement(List<T> argList) {
    if (argList.isEmpty()) {
      return null;
    }

    return argList.get(rng.nextInt(argList.size()));
  }

  /**
   * 
   * @param <T>
   * @param argList
   * @return
   */
  public static <T> T getRandomElement(T[] argList) {
    if (argList.length <= 0) {
      return null;
    }

    return argList[rng.nextInt(argList.length)];
  }

}
