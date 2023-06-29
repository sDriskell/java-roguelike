package roguelike.util;

import java.awt.Rectangle;
import java.util.Arrays;

public class ArrayUtils<E> {
    private static final String ORIGINAL = " original=";
    private static final String INVALID_X = "invalid x: ";
    private static final String INVALID_Y = "invalid y: ";

	public static boolean[][] getSubArray(boolean[][] original, Rectangle area) {
		return getSubArray(original, area.x, area.y, area.width, area.height);
	}

	public static boolean[][] getSubArray(boolean[][] original, int x, int y, int width, int height) {
		if (original.length < x)
			throw new IllegalArgumentException(INVALID_X + x + ORIGINAL + original.length);
		if (original[0].length < y)
			throw new IllegalArgumentException(INVALID_Y + y + ORIGINAL + original[0].length);

		boolean[][] subArray = new boolean[width][];
		for (int i = x, j = 0; i < (x + width); i++, j++) {
			subArray[j] = Arrays.copyOfRange(original[i], y, y + height);
		}
		return subArray;
	}

	public static float[][] getSubArray(float[][] original, Rectangle area) {
		return getSubArray(original, area.x, area.y, area.width, area.height);
	}

	public static float[][] getSubArray(float[][] original, int x, int y, int width, int height) {
		if (original.length < x)
			throw new IllegalArgumentException(INVALID_X + x + ORIGINAL + original.length);
		if (original[0].length < y)
			throw new IllegalArgumentException(INVALID_Y + y + ORIGINAL + original[0].length);

		float[][] subArray = new float[width][];

		for (int i = x, j = 0; i < (x + width); i++, j++) {
			subArray[j] = Arrays.copyOfRange(original[i], y, y + height);
		}
		return subArray;
	}

	public static <T> T[][] getSubArray(T[][] original, Rectangle area) {
		return getSubArray(original, area.x, area.y, (int) area.getMaxX(), (int) area.getMaxY());
	}

	public static <T> T[][] getSubArray(T[][] original, int x, int y, int width, int height) {

		T[][] subArray = Arrays.copyOfRange(original, x, x + width);
		for (int i = x, j = 0; i < width; i++, j++) {
			subArray[j] = Arrays.copyOfRange(original[i], y, y + height);
		}

		return subArray;
	}
}
