package roguelike.util;

import java.awt.Rectangle;
import java.util.Arrays;

/**
 * 
 * @param <E>
 */
public class ArrayUtils<E> {

  /**
   * 
   * @param argOrg
   * @param argArea
   * @return
   */
  public static boolean[][] getSubArray(boolean[][] argOrg, Rectangle argArea) {
    return getSubArray(argOrg, argArea.x, argArea.y, argArea.width, argArea.height);
  }

  /**
   * 
   * @param argOrg
   * @param x
   * @param y
   * @param argW
   * @param argH
   * @return
   */
  public static boolean[][] getSubArray(boolean[][] argOrg, int x, int y, int argW, int argH) {
    if (argOrg.length < x) {
      throw new IllegalArgumentException("invalid x: " + x + " original=" + argOrg.length);
    }

    if (argOrg[0].length < y) {
      throw new IllegalArgumentException("invalid y: " + y + " original=" + argOrg[0].length);
    }

    boolean[][] subArray = new boolean[argW][];

    for (int i = x, j = 0; i < (x + argW); i++, j++) {
      subArray[j] = Arrays.copyOfRange(argOrg[i], y, y + argH);
    }

    return subArray;
  }

  /**
   * 
   * @param argOrg
   * @param argArea
   * @return
   */
  public static float[][] getSubArray(float[][] argOrg, Rectangle argArea) {
    return getSubArray(argOrg, argArea.x, argArea.y, argArea.width, argArea.height);
  }

  /**
   * 
   * @param argOrg
   * @param x
   * @param y
   * @param argW
   * @param argH
   * @return
   */
  public static float[][] getSubArray(float[][] argOrg, int x, int y, int argW, int argH) {
    if (argOrg.length < x) {
      throw new IllegalArgumentException("invalid x: " + x + " original=" + argOrg.length);
    }
    if (argOrg[0].length < y) {
      throw new IllegalArgumentException("invalid y: " + y + " original=" + argOrg[0].length);
    }

    float[][] subArray = new float[argW][];

    for (int i = x, j = 0; i < (x + argW); i++, j++) {
      subArray[j] = Arrays.copyOfRange(argOrg[i], y, y + argH);
    }

    return subArray;
  }

  /**
   * 
   * @param <T>
   * @param argOrg
   * @param argArea
   * @return
   */
  public static <T> T[][] getSubArray(T[][] argOrg, Rectangle argArea) {
    return getSubArray(argOrg, argArea.x, argArea.y, (int) argArea.getMaxX(),
        (int) argArea.getMaxY());
  }

  /**
   * 
   * @param <T>
   * @param argOrg
   * @param x
   * @param y
   * @param argW
   * @param argH
   * @return
   */
  public static <T> T[][] getSubArray(T[][] argOrg, int x, int y, int argW, int argH) {
    T[][] subArray = Arrays.copyOfRange(argOrg, x, x + argW);

    for (int i = x, j = 0; i < argW; i++, j++) {
      subArray[j] = Arrays.copyOfRange(argOrg[i], y, y + argH);
    }

    return subArray;
  }
}
