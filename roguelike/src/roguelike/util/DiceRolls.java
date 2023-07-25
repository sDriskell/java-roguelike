package roguelike.util;

import roguelike.Game;
import squidpony.squidmath.RNG;

public class DiceRolls {
    private static final int TARGET_NUMBER = 6;
    private static final int DICE_TYPE = 10;

    /**
     * Makes the number of rolls indicated and returns the amount of successes
     * 
     * @param poolSize
     */
    public static int roll(int poolSize) {
        return roll(poolSize, TARGET_NUMBER);
    }

    public static int roll(int poolSize, int targetNumber) {
        RNG rng = Game.current().random();
        int successes = 0;
        
        for (int x = 0; x < poolSize; x++) {
            /* +1 because max is exclusive*/
            int result = rng.between(1, DICE_TYPE + 1); 
            
            if (result > targetNumber) {
                successes++;
            }
        }
        return successes;
    }
}
