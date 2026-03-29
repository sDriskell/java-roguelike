package roguelike.util;

import roguelike.Game;
import squidpony.squidmath.RNG;

/**
 * 
 */
public class DiceRolls {

  private static final int TARGET_NUMBER = 6;
  private static final int DICE_TYPE = 10;

  /**
   * Makes the number of rolls indicated and returns the amount of successes
   * 
   * @param argPoolSize
   */
  public static int roll(int argPoolSize) {
    return roll(argPoolSize, TARGET_NUMBER);
  }

  /**
   * 
   * @param argPoolSize
   * @param argTgtNum
   * @return
   */
  public static int roll(int argPoolSize, int argTgtNum) {
    RNG rng = Game.current().random();
    int successes = 0;

    for (int x = 0; x < argPoolSize; x++) {
      // +1 because max is exclusive
      int result = rng.between(1, DICE_TYPE + 1);

      if (result > argTgtNum) {
        successes++;
      }
    }

    return successes;
  }
}
