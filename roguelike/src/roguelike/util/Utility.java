package roguelike.util;

/**
 * 
 */
public class Utility {

  /**
   * 
   * @param argTxt
   * @return
   */
  public static String capitalizeFirstLetter(String argTxt) {
    argTxt = argTxt.replace("!", "!_");
    argTxt = argTxt.replace(".", "._");
    String[] sentences = argTxt.split("[_]");
    StringBuilder sb = new StringBuilder();

    for (String s : sentences) {
      s = s.trim();

      if (s.length() > 1) {
        String fmtStr = String
            .format("%s%s", s.substring(0, 1).toUpperCase(), s.substring(1).toLowerCase());
        sb.append(fmtStr);

        if (!s.endsWith(".") && !s.endsWith("!")) {
          sb.append(".");
        }

        sb.append(" ");
      }
      else {
        sb.append(s);
      }
    }

    return sb.toString();
  }
}
