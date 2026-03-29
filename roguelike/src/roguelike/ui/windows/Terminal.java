package roguelike.ui.windows;

import java.awt.Rectangle;

import roguelike.maps.MapHelpers;
import roguelike.util.CharEx;
import squidpony.squidcolor.SColor;

/**
 * 
 */
public class Terminal extends TerminalBase {

  /**
   * 
   * @param argW
   * @param argH
   * @param argTermChange
   */
  public Terminal(int argW, int argH, TerminalChangeNotification argTermChange) {
    this(new Rectangle(0, 0, argW, argH), new CharEx[argW][argH], argTermChange);
  }

  Terminal(final Rectangle argArea, final CharEx[][] argData,
      final TerminalChangeNotification argTermChange) {
    super(argTermChange);

    colors = new ColorPair(SColor.WHITE, SColor.BLACK);
    size = argArea;
    data = argData;
    cursor = new TerminalCursor() {

      @Override
      public boolean put(int x, int y, CharEx c) {
        int sx = getX(x);
        int sy = getY(y);

        if (!MapHelpers.isWithinBounds(argData.length, argData[0].length, sx, sy)) {
          return false;
        }

        CharEx existing = argData[sx][sy];

        if (existing != null && existing.equals(c)) {
          return false;
        }

        argData[sx][sy] = c;
        return true;
      }

      @Override
      public boolean bg(int x, int y) {
        int sx = getX(x);
        int sy = getY(y);

        if (!MapHelpers.isWithinBounds(argData.length, argData[0].length, sx, sy)) {
          return false;
        }

        CharEx c = argData[getX(x)][getY(y)];
        CharEx c2 = new CharEx(c.getSymbol(), c.getForegroundColor(), colors.background());
        argData[sx][sy] = c2;
        argTermChange.onChanged(sx, sy, c2);

        return true;
      }

      private int getX(int x) {
        return size.x + x;
      }

      private int getY(int y) {
        return size.y + y;
      }
    };
  }

  @Override
  public TerminalBase getWindow(int x, int y, int a, int argH) {
    Rectangle area = new Rectangle(x, y, a, argH);
    return new Terminal(area, data, terminalChanged);
  }

  @Override
  public TerminalBase withColor(SColor argCol) {
    Terminal term = new Terminal(size, data, terminalChanged);
    term.colors = new ColorPair(argCol);
    return term;
  }

  @Override
  public TerminalBase withColor(SColor argFgCol, SColor argBgCol) {
    Terminal term = new Terminal(size, data, terminalChanged);
    term.colors = new ColorPair(argFgCol, argBgCol);
    return term;
  }

}
