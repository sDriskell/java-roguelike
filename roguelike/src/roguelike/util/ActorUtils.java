package roguelike.util;

import java.awt.Point;

import roguelike.actors.Actor;
import roguelike.maps.MapArea;
import squidpony.squidgrid.los.BresenhamLOS;
import squidpony.squidgrid.los.LOSSolver;
import squidpony.squidgrid.util.BasicRadiusStrategy;

/**
 * 
 */
public class ActorUtils {

  private static LOSSolver losSolver = new BresenhamLOS();

  private ActorUtils() {
    // Hidden utility class constructor.
  }

  /**
   * 
   * @param argAct
   * @param argOther
   * @param argMapArea
   * @return
   */
  public static boolean canSee(Actor argAct, Actor argOther, MapArea argMapArea) {
    Point position = argAct.getPosition();
    int startx = position.x;
    int starty = position.y;
    int targetx = argOther.getPosition().x;
    int targety = argOther.getPosition().y;

    float force = 1;
    float decay = 1f / argAct.getVisionRadius();

    boolean visible = losSolver
        .isReachable(argMapArea.getLightValues(), startx, starty, targetx, targety, force, decay,
            BasicRadiusStrategy.CIRCLE);

    Log.verboseDebug(argAct.getName() + " canSee " + argOther.getName() + "=" + visible);
    return visible;
  }

  /**
   * 
   * @param argTxt
   * @return
   */
  public static String makePlayerText(String argTxt) {
    String[] words = argTxt.split(" ");

    if (words[0].equals("has")) {
      words[0] = "have";
    }
    else if (words[0].endsWith("Es")) {
      words[0] = words[0].substring(0, words[0].length() - 1);
    }
    else if (words[0].endsWith("es")) {
      words[0] = words[0].substring(0, words[0].length() - 2);
    }
    else if (words[0].endsWith("s")) {
      words[0] = words[0].substring(0, words[0].length() - 1);
    }
    else if (words[0].equals("'s")) {
      words[0] = "r"; // xxx's = your
    }

    StringBuilder builder = new StringBuilder();
    for (String word : words) {
      builder.append(" ");
      builder.append(word);
    }

    return builder.toString().trim();
  }
}
