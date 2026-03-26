package roguelike.ui.windows;

import java.awt.Rectangle;

import roguelike.util.Symbol;

/**
 * 
 */
public class TextWindow {

  private static Symbol[] doubleLines = new Symbol[] { Symbol.BOX_TOP_LEFT_DOUBLE,
      Symbol.BOX_BOTTOM_LEFT_DOUBLE, Symbol.BOX_TOP_RIGHT_DOUBLE, Symbol.BOX_BOTTOM_RIGHT_DOUBLE,
      Symbol.BOX_TOP_DOUBLE, Symbol.BOX_LEFT_DOUBLE };

  private static Symbol[] singleLines = new Symbol[] { Symbol.BOX_TOP_LEFT_SINGLE,
      Symbol.BOX_BOTTOM_LEFT_SINGLE, Symbol.BOX_TOP_RIGHT_SINGLE, Symbol.BOX_BOTTOM_RIGHT_SINGLE,
      Symbol.BOX_TOP_SINGLE, Symbol.BOX_LEFT_SINGLE };

  protected Rectangle size;

  /**
   * 
   * @param argW
   * @param argH
   */
  protected TextWindow(int argW, int argH) {
    this.size = new Rectangle(0, 0, argW, argH);
  }

  /**
   * 
   * @param argTerm
   */
  protected void drawBoxShape(TerminalBase argTerm) {
    drawBoxShape(argTerm, 0, size.height, false);
  }

  /**
   * 
   * @param argTerm
   * @param argTop
   * @param argH
   * @param argIsDoubleLines
   */
  protected void drawBoxShape(TerminalBase argTerm, int argTop, int argH,
      boolean argIsDoubleLines) {
    int width = size.width;

    TextWindow.drawBoxShape(argTerm, argTop, argH, width, argIsDoubleLines);
  }

  /**
   * 
   * @param argTerm
   * @param argTop
   * @param argH
   * @param argW
   * @param argIsDoubleLines
   */
  public static void drawBoxShape(TerminalBase argTerm, int argTop, int argH, int argW,
      boolean argIsDoubleLines) {
    Symbol[] lines = argIsDoubleLines ? TextWindow.doubleLines : TextWindow.singleLines;

    for (int x = 0; x < argW; x++) {
      for (int y = 0; y < argH; y++) {
        int sY = argTop + y;

        if (y == 0 || y == argH - 1) {
          if (x == 0) {
            if (y == 0) {
              argTerm.put(x, sY, lines[0].symbol());
            }
            else {
              argTerm.put(x, sY, lines[1].symbol());
            }
          }
          else if (x == argW - 1) {
            if (y == 0) {
              argTerm.put(x, sY, lines[2].symbol());
            }
            else {
              argTerm.put(x, sY, lines[3].symbol());
            }
          }
          else {
            argTerm.put(x, sY, lines[4].symbol());
          }
        }
        else if (x == 0 || x == argW - 1) {
          argTerm.put(x, sY, lines[5].symbol());
        }
      }
    }
  }

}
