package roguelike.util;

/**
 * 
 */
public class Log {
  public static void verboseDebug(String argMsg) {
  }

  public static void debug(String argMsg) {
    System.out.println("DEBUG: " + argMsg);
  }

  public static void warning(String argMsg) {
    System.out.println("WARNING: " + argMsg);
  }

  public static void info(String argMsg) {
    System.out.println("INFO: " + argMsg);
  }
}
