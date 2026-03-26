package roguelike.ui.windows;

import squidpony.squidcolor.SColor;

/**
 * 
 */
public class ColorPair {
  private SColor bgColor;
  private SColor fgColor;

  /**
   * 
   * @param argFg
   * @param argBg
   */
  public ColorPair(SColor argFg, SColor argBg) {
    fgColor = argFg;
    bgColor = argBg;
  }

  /**
   * 
   * @param argFg
   */
  public ColorPair(SColor argFg) {
    fgColor = argFg;
    bgColor = SColor.BLACK;
  }

  public SColor foreground() {
    return fgColor;
  }

  public SColor background() {
    return bgColor;
  }
}
