package roguelike;

import java.io.Serializable;

import roguelike.util.StringEx;
import roguelike.util.Utility;
import squidpony.squidcolor.SColor;

/**
 * 
 */
public class MessageDisplayProperties implements Serializable {
  private static final long serialVersionUID = 1L;

  private StringEx text;
  private SColor color;

  /**
   * 
   * @param argTxt
   */
  public MessageDisplayProperties(String argTxt) {
    this(argTxt, SColor.LIGHT_GRAY);
  }

  /**
   * 
   * @param argTxt
   * @param argCol
   */
  public MessageDisplayProperties(String argTxt, SColor argCol) {
    if (argTxt == null) {
      throw new IllegalArgumentException("text cannot be null");
    }

    text = new StringEx(Utility.capitalizeFirstLetter(argTxt), argCol, SColor.BLACK);
    color = argCol;
  }

  /**
   * 
   * @return
   */
  public StringEx getText() {
    return text;
  }

  /**
   * 
   * @return
   */
  public SColor getColor() {
    return color;
  }
}
