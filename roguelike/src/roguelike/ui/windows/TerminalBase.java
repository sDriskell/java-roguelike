package roguelike.ui.windows;

import java.awt.Point;
import java.awt.Rectangle;

import roguelike.util.CharEx;
import roguelike.util.StringEx;
import squidpony.squidcolor.SColor;

/**
 * 
 */
public abstract class TerminalBase {

  protected CharEx[][] data;
  protected Rectangle size;
  protected ColorPair colors;
  protected TerminalCursor cursor;

  protected TerminalChangeNotification terminalChanged;

  /**
   * 
   * @param argTermChanged
   */
  protected TerminalBase(TerminalChangeNotification argTermChanged) {
    setTerminalChanged(argTermChanged);
  }

  /**
   * 
   * @param argTermChanged
   */
  public void setTerminalChanged(TerminalChangeNotification argTermChanged) {
    if (argTermChanged == null) {
      throw new IllegalArgumentException("terminal changed notification is null");
    }

    terminalChanged = argTermChanged;
  }

  /**
   * 
   * @return
   */
  public Point location() {
    return size.getLocation();
  }

  /**
   * 
   * @return
   */
  public Rectangle size() {
    return this.size;
  }

  /**
   * 
   * @return
   */
  public TerminalBase cloneTerminal() {
    final TerminalBase self = this;

    TerminalBase clone = new TerminalBase(terminalChanged) {
      private TerminalBase parent = self;

      @Override
      public TerminalBase withColor(SColor argFg, SColor argBg) {
        return parent.withColor(argFg, argBg);
      }

      @Override
      public TerminalBase withColor(SColor argCol) {
        return parent.withColor(argCol);
      }

      @Override
      public TerminalBase getWindow(int x, int y, int argW, int argH) {
        return parent.getWindow(x, y, argW, argH);
      }
    };

    clone.data = new CharEx[data.length][data[0].length];

    for (int i = 0; i < data.length; i++) {
      for (int j = 0; j < data[0].length; j++) {
        clone.data[i][j] = data[i][j];
      }
    }

    clone.size = this.size;
    clone.colors = this.colors;
    clone.cursor = this.cursor;

    return clone;
  }

  /**
   * 
   * @param x
   * @param y
   * @param argW
   * @param argH
   * @return
   */
  public abstract TerminalBase getWindow(int x, int y, int argW, int argH);

  /**
   * 
   * @param argCol
   * @return
   */
  public abstract TerminalBase withColor(SColor argCol);

  /**
   * 
   * @param argFg
   * @param argBg
   * @return
   */
  public abstract TerminalBase withColor(SColor argFg, SColor argBg);

  /**
   * 
   * @param x
   * @param y
   * @param argTxt
   * @return
   */
  public TerminalBase write(int x, int y, String argTxt) {
    return write(x, y, new StringEx(argTxt, colors.foreground(), colors.background()));
  }

  /**
   * 
   * @param x
   * @param y
   * @param argTxt
   * @return
   */
  public TerminalBase write(int x, int y, StringEx argTxt) {
    CharEx[][] temp = new CharEx[argTxt.size()][1];

    for (int i = 0; i < argTxt.size(); i++) {
      temp[i][0] = argTxt.get(i);
    }

    put(x, y, temp);
    return this;
  }

  /**
   * 
   * @param x
   * @param y
   * @param c
   * @return
   */
  public TerminalBase put(int x, int y, CharEx[][] c) {
    for (int i = 0; i < c.length; i++) {
      for (int j = 0; j < c[0].length; j++) {
        put(x + i, y + j, c[i][j]);
      }
    }

    return this;
  }

  /**
   * 
   * @param x
   * @param y
   * @param c
   * @return
   */
  public TerminalBase put(int x, int y, CharEx c) {
    if (cursor.put(x, y, c)) {
      terminalChanged.onChanged(x + size.x, y + size.y, c);
    }

    return this;
  }

  /**
   * 
   * @param x
   * @param y
   * @param c
   * @return
   */
  public TerminalBase put(int x, int y, char c) {
    CharEx ch = new CharEx(c, colors.foreground(), colors.background());
    put(x, y, ch);
    return this;
  }

  /**
   * 
   * @param x
   * @param y
   * @param argW
   * @param argH
   * @param c
   * @return
   */
  public TerminalBase fill(int x, int y, int argW, int argH, char c) {
    for (int i = 0; i < argW; i++) {
      for (int j = 0; j < argH; j++) {
        put(i + x, j + y, new CharEx(c, colors.foreground(), colors.background()));
      }
    }

    return this;
  }

  /**
   * 
   * @param x
   * @param y
   * @param argW
   * @param argH
   * @return
   */
  public TerminalBase fill(int x, int y, int argW, int argH) {
    for (int i = 0; i < argW; i++) {
      for (int j = 0; j < argH; j++) {
        cursor.bg(i + x, j + y);
      }
    }

    return this;
  }

  /**
   * 
   * @param x
   * @param y
   * @param argW
   * @param argH
   * @return
   */
  public TerminalBase refresh(int x, int y, int argW, int argH) {
    for (int i = 0; i < argW; i++) {
      for (int j = 0; j < argH; j++) {
        CharEx oldChar = this.data[i + x][j + y];
        put(i + x, j + y, oldChar);
      }
    }

    return this;
  }

}
