package roguelike.ui.asciipanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.LookupOp;
import java.awt.image.ShortLookupTable;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import roguelike.ui.MainWindow;

/*
 * TODO: I want to refactor this into something I prefer or break it down into a series of several smaller
 * classes.  Additionally
 */
/**
 * This simulates a code page 437 ASCII terminal display.
 * 
 * @author Trystan Spangler
 */
public class AsciiPanel extends JPanel {
  private static final long serialVersionUID = -4167851861147593092L;

  // TODO: Make into an enum
  public static final Color BLACK = new Color(0, 0, 0);
  public static final Color RED = new Color(128, 0, 0);
  public static final Color GREEN = new Color(0, 128, 0);
  public static final Color YELLOW = new Color(128, 128, 0);
  public static final Color BLUE = new Color(0, 0, 128);
  public static final Color MAGENTA = new Color(128, 0, 128);
  public static final Color CYAN = new Color(0, 128, 128);
  public static final Color WHITE = new Color(192, 192, 192);
  public static final Color DARK_GRAY = new Color(128, 128, 128);
  public static final Color BRIGHT_RED = new Color(255, 0, 0);
  public static final Color BRIGHT_GREEN = new Color(0, 255, 0);
  public static final Color BRIGHT_YELLOW = new Color(255, 255, 0);
  public static final Color BRIGHT_BLUE = new Color(0, 0, 255);
  public static final Color BRIGHT_MAGENTA = new Color(255, 0, 255);
  public static final Color BRIGHT_CYAN = new Color(0, 255, 255);
  public static final Color BRIGHT_WHITE = new Color(255, 255, 255);

  private static final String RESOURCE_LOCATION = "/resources/cp437.png";

  private Image offscreenBuffer;
  private Graphics offscreenGraphics;
  private int widthInCharacters;
  private int heightInCharacters;
  private int charWidth = 9;
  private int charHeight = 16;
  private Color defaultBackgroundColor;
  private Color defaultForegroundColor;
  private int cursorX;
  private int cursorY;
  private BufferedImage glyphSprite;
  private BufferedImage[] glyphs;
  private char[][] chars;
  private Color[][] backgroundColors;
  private Color[][] foregroundColors;
  private char[][] oldChars;
  private Color[][] oldBackgroundColors;
  private Color[][] oldForegroundColors;

  /**
   * Gets the height, in pixels, of a character.
   * 
   * @return
   */
  public int getCharHeight() {
    return charHeight;
  }

  /**
   * Gets the width, in pixels, of a character.
   * 
   * @return
   */
  public int getCharWidth() {
    return charWidth;
  }

  /**
   * Gets the height in characters. A standard terminal is 24 characters high.
   * 
   * @return
   */
  public int getHeightInCharacters() {
    return heightInCharacters;
  }

  /**
   * Gets the width in characters. A standard terminal is 80 characters wide.
   * 
   * @return
   */
  public int getWidthInCharacters() {
    return widthInCharacters;
  }

  /**
   * Gets the distance from the left new text will be written to.
   * 
   * @return
   */
  public int getCursorX() {
    return cursorX;
  }

  /**
   * Sets the distance from the left new text will be written to. This should be
   * equal to or greater than 0 and less than the the width in characters.
   * 
   * @param argCurX the distance from the left new text should be written to
   */
  public void setCursorX(int argCurX) {
    if (argCurX < 0 || argCurX >= widthInCharacters) {
      throw new IllegalArgumentException(
          "cursorX " + argCurX + " must be within range [0," + widthInCharacters + ").");
    }

    cursorX = argCurX;
  }

  /**
   * Gets the distance from the top new text will be written to.
   * 
   * @return
   */
  public int getCursorY() {
    return cursorY;
  }

  /**
   * Sets the distance from the top new text will be written to. This should be
   * equal to or greater than 0 and less than the the height in characters.
   * 
   * @param argCurY the distance from the top new text should be written to
   */
  public void setCursorY(int argCurY) {
    if (argCurY < 0 || argCurY >= heightInCharacters) {
      throw new IllegalArgumentException(
          "cursorY " + argCurY + " must be within range [0," + heightInCharacters + ").");
    }

    cursorY = argCurY;
  }

  /**
   * Sets the x and y position of where new text will be written to. The origin
   * (0,0) is the upper left corner. The x should be equal to or greater than 0
   * and less than the the width in characters. The y should be equal to or
   * greater than 0 and less than the the height in characters.
   * 
   * @param x the distance from the left new text should be written to
   * @param y the distance from the top new text should be written to
   */
  public void setCursorPosition(int x, int y) {
    setCursorX(x);
    setCursorY(y);
  }

  /**
   * Gets the default background color that is used when writing new text.
   * 
   * @return
   */
  public Color getDefaultBackgroundColor() {
    return defaultBackgroundColor;
  }

  /**
   * Sets the default background color that is used when writing new text.
   * 
   * @param argCol
   */
  public void setDefaultBackgroundColor(Color argCol) {
    if (argCol == null) {
      throw new NullPointerException("Default background color must not be null.");
    }

    defaultBackgroundColor = argCol;
  }

  /**
   * Gets the default foreground color that is used when writing new text.
   * 
   * @return
   */
  public Color getDefaultForegroundColor() {
    return defaultForegroundColor;
  }

  /**
   * Sets the default foreground color that is used when writing new text.
   * 
   * @param argCol
   */
  public void setDefaultForegroundColor(Color argCol) {
    if (argCol == null) {
      throw new NullPointerException("Default foreground color must not be null.");
    }

    defaultForegroundColor = argCol;
  }

  /**
   * Class constructor. Default size is 80x24.
   */
  public AsciiPanel() {
    this(80, 24);
  }

  /**
   * Class constructor specifying the width and height in characters.
   * 
   * @param argWidth
   * @param argHeight
   */
  public AsciiPanel(int argWidth, int argHeight) {
    super();

    if (argWidth < 1) {
      throw new IllegalArgumentException("width " + argWidth + " must be greater than 0.");
    }

    if (argHeight < 1) {
      throw new IllegalArgumentException("height " + argHeight + " must be greater than 0.");
    }

    widthInCharacters = argWidth;
    heightInCharacters = argHeight;
    setPreferredSize(new Dimension(charWidth * widthInCharacters, charHeight * heightInCharacters));

    defaultBackgroundColor = BLACK;
    defaultForegroundColor = WHITE;

    chars = new char[widthInCharacters][heightInCharacters];
    backgroundColors = new Color[widthInCharacters][heightInCharacters];
    foregroundColors = new Color[widthInCharacters][heightInCharacters];

    oldChars = new char[widthInCharacters][heightInCharacters];
    oldBackgroundColors = new Color[widthInCharacters][heightInCharacters];
    oldForegroundColors = new Color[widthInCharacters][heightInCharacters];

    glyphs = new BufferedImage[256];

    loadGlyphs();

    AsciiPanel.this.clear();
  }

  @Override
  public void update(Graphics argGphs) {
    paint(argGphs);
  }

  @Override
  public void paint(Graphics argGphs) {
    if (argGphs == null) {
      throw new NullPointerException();
    }

    if (offscreenBuffer == null) {
      offscreenBuffer = createImage(this.getWidth(), this.getHeight());
      offscreenGraphics = offscreenBuffer.getGraphics();
    }

    for (int x = 0; x < widthInCharacters; x++) {
      for (int y = 0; y < heightInCharacters; y++) {
        if (isSameGlyph(x, y)) {
          continue;
        }

        Color bg = backgroundColors[x][y];
        Color fg = foregroundColors[x][y];
        LookupOp op = setColors(bg, fg);
        BufferedImage img = op.filter(glyphs[chars[x][y]], null);
        offscreenGraphics.drawImage(img, x * charWidth, y * charHeight, null);

        oldBackgroundColors[x][y] = backgroundColors[x][y];
        oldForegroundColors[x][y] = foregroundColors[x][y];
        oldChars[x][y] = chars[x][y];
      }
    }

    argGphs.drawImage(offscreenBuffer, 0, 0, this);
  }

  /**
   * 
   * @param x
   * @param y
   * @return
   */
  private boolean isSameGlyph(int x, int y) {
    return oldBackgroundColors[x][y] == backgroundColors[x][y]
        && oldForegroundColors[x][y] == foregroundColors[x][y] && oldChars[x][y] == chars[x][y];
  }

  /**
   * 
   */
  private void loadGlyphs() {
    try {
      InputStream file = MainWindow.class.getResourceAsStream(RESOURCE_LOCATION);
      glyphSprite = ImageIO.read(file);
    }
    catch (IOException e) {
      System.err.println("loadGlyphs(): " + e.getMessage());
    }

    for (int i = 0; i < 256; i++) {
      int sx = (i % 32) * charWidth + 8;
      int sy = (i / 32) * charHeight + 8;

      glyphs[i] = new BufferedImage(charWidth, charHeight, BufferedImage.TYPE_INT_ARGB);
      glyphs[i].getGraphics().drawImage(glyphSprite, 0, 0, charWidth, charHeight, sx, sy,
          sx + charWidth, sy + charHeight, null);
    }
  }

  /**
   * Create a <code>LookupOp</code> object (lookup table) mapping the original
   * pixels to the background and foreground colors, respectively.
   * 
   * @param argBgCol the background color
   * @param argFgCol the foreground color
   * @return the <code>LookupOp</code> object (lookup table)
   */
  private LookupOp setColors(Color argBgCol, Color argFgCol) {
    short[] a = new short[256];
    short[] r = new short[256];
    short[] g = new short[256];
    short[] b = new short[256];

    byte bgr = (byte) (argBgCol.getRed());
    byte bgg = (byte) (argBgCol.getGreen());
    byte bgb = (byte) (argBgCol.getBlue());

    byte fgr = (byte) (argFgCol.getRed());
    byte fgg = (byte) (argFgCol.getGreen());
    byte fgb = (byte) (argFgCol.getBlue());

    for (int i = 0; i < 256; i++) {
      if (i == 0) {
        a[i] = (byte) 255;
        r[i] = bgr;
        g[i] = bgg;
        b[i] = bgb;
      }
      else {
        a[i] = (byte) 255;
        r[i] = fgr;
        g[i] = fgg;
        b[i] = fgb;
      }
    }

    short[][] table = { r, g, b, a };

    return new LookupOp(new ShortLookupTable(0, table), null);
  }

  /**
   * Clear the entire screen to whatever the default background color is.
   * 
   * @return this for convenient chaining of method calls
   */
  public AsciiPanel clear() {
    return clear(' ', 0, 0, widthInCharacters, heightInCharacters, defaultForegroundColor,
        defaultBackgroundColor);
  }

  /**
   * Clear the entire screen with the specified character and whatever the default
   * foreground and background colors are.
   * 
   * @param argChar the character to write
   * @return this for convenient chaining of method calls
   */
  public AsciiPanel clear(char argChar) {
    if (argChar < 0 || argChar >= glyphs.length) {
      throw new IllegalArgumentException(
          "character " + argChar + " must be within range [0," + glyphs.length + "].");
    }

    return clear(argChar, 0, 0, widthInCharacters, heightInCharacters, defaultForegroundColor,
        defaultBackgroundColor);
  }

  /**
   * Clear the entire screen with the specified character and whatever the
   * specified foreground and background colors are.
   * 
   * @param argChar the character to write
   * @param argForeground the foreground color or null to use the default
   * @param argBackground the background color or null to use the default
   * @return this for convenient chaining of method calls
   */
  public AsciiPanel clear(char argChar, Color argForeground, Color argBackground) {
    if (argChar < 0 || argChar >= glyphs.length) {
      throw new IllegalArgumentException(
          "character " + argChar + " must be within range [0," + glyphs.length + "].");
    }

    return clear(argChar, 0, 0, widthInCharacters, heightInCharacters, argForeground,
        argBackground);
  }

  // TODO: This needs to be refactored - clear methods are redundant.
  // TODO: builder design pattern
  /**
   * Clear the section of the screen with the specified character and whatever the
   * default foreground and background colors are.
   * 
   * @param argChar the character to write
   * @param argX the distance from the left to begin writing from
   * @param argY the distance from the top to begin writing from
   * @param argW the height of the section to clear
   * @param argH the width of the section to clear
   * @return this for convenient chaining of method calls
   */
  public AsciiPanel clear(char argChar, int argX, int argY, int argW, int argH) {
    if (argChar < 0 || argChar >= glyphs.length) {
      throw new IllegalArgumentException(
          "character " + argChar + " must be within range [0," + glyphs.length + "].");
    }

    if (argX < 0 || argX >= widthInCharacters) {
      throw new IllegalArgumentException(
          "x " + argX + " must be within range [0," + widthInCharacters + ").");
    }

    if (argY < 0 || argY >= heightInCharacters) {
      throw new IllegalArgumentException(
          "y " + argY + " must be within range [0," + heightInCharacters + ").");
    }

    if (argW < 1) {
      throw new IllegalArgumentException("width " + argW + " must be greater than 0.");
    }

    if (argH < 1) {
      throw new IllegalArgumentException("height " + argH + " must be greater than 0.");
    }

    if (argX + argW > widthInCharacters) {
      throw new IllegalArgumentException(
          "x + width " + (argX + argW) + " must be less than " + (widthInCharacters + 1) + ".");
    }

    if (argY + argH > heightInCharacters) {
      throw new IllegalArgumentException(
          "y + height " + (argY + argH) + " must be less than " + (heightInCharacters + 1) + ".");
    }

    return clear(argChar, argX, argY, argW, argH, defaultForegroundColor, defaultBackgroundColor);
  }

  /**
   * Clear the section of the screen with the specified character and whatever the
   * specified foreground and background colors are.
   * 
   * @param argChar the character to write
   * @param argX the distance from the left to begin writing from
   * @param argY the distance from the top to begin writing from
   * @param argW the height of the section to clear
   * @param argH the width of the section to clear
   * @param argFg the foreground color or null to use the default
   * @param background the background color or null to use the default
   * @return this for convenient chaining of method calls
   */
  public AsciiPanel clear(char argChar, int argX, int argY, int argW, int argH, Color argFg,
      Color background) {
    if (argChar < 0 || argChar >= glyphs.length) {
      throw new IllegalArgumentException(
          "character " + argChar + " must be within range [0," + glyphs.length + "].");
    }

    if (argX < 0 || argX >= widthInCharacters) {
      throw new IllegalArgumentException(
          "x " + argX + " must be within range [0," + widthInCharacters + ")");
    }

    if (argY < 0 || argY >= heightInCharacters) {
      throw new IllegalArgumentException(
          "y " + argY + " must be within range [0," + heightInCharacters + ")");
    }

    if (argW < 1) {
      throw new IllegalArgumentException("width " + argW + " must be greater than 0.");
    }

    if (argH < 1) {
      throw new IllegalArgumentException("height " + argH + " must be greater than 0.");
    }

    if (argX + argW > widthInCharacters) {
      throw new IllegalArgumentException(
          "x + width " + (argX + argW) + " must be less than " + (widthInCharacters + 1) + ".");
    }

    if (argY + argH > heightInCharacters) {
      throw new IllegalArgumentException(
          "y + height " + (argY + argH) + " must be less than " + (heightInCharacters + 1) + ".");
    }

    for (int xo = argX; xo < argX + argW; xo++) {
      for (int yo = argY; yo < argY + argH; yo++) {
        write(argChar, xo, yo, argFg, background);
      }
    }

    return this;
  }

  /**
   * Write a character to the cursor's position. This updates the cursor's
   * position.
   * 
   * @param argChar the character to write
   * @return this for convenient chaining of method calls
   */
  public AsciiPanel write(char argChar) {
    if (argChar < 0 || argChar >= glyphs.length) {
      throw new IllegalArgumentException(
          "character " + argChar + " must be within range [0," + glyphs.length + "].");
    }

    return write(argChar, cursorX, cursorY, defaultForegroundColor, defaultBackgroundColor);
  }

  /**
   * Write a character to the cursor's position with the specified foreground
   * color. This updates the cursor's position but not the default foreground
   * color.
   * 
   * @param argChar the character to write
   * @param argFg the foreground color or null to use the default
   * @return this for convenient chaining of method calls
   */
  public AsciiPanel write(char argChar, Color argFg) {
    if (argChar < 0 || argChar >= glyphs.length) {
      throw new IllegalArgumentException(
          "character " + argChar + " must be within range [0," + glyphs.length + "].");
    }

    return write(argChar, cursorX, cursorY, argFg, defaultBackgroundColor);
  }

  /**
   * Write a character to the cursor's position with the specified foreground and
   * background colors. This updates the cursor's position but not the default
   * foreground or background colors.
   * 
   * @param argChar the character to write
   * @param argFg the foreground color or null to use the default
   * @param argBg the background color or null to use the default
   * @return this for convenient chaining of method calls
   */
  public AsciiPanel write(char argChar, Color argFg, Color argBg) {
    if (argChar < 0 || argChar >= glyphs.length) {
      throw new IllegalArgumentException(
          "character " + argChar + " must be within range [0," + glyphs.length + "].");
    }

    return write(argChar, cursorX, cursorY, argFg, argBg);
  }

  /**
   * Write a character to the specified position. This updates the cursor's
   * position.
   * 
   * @param argChar the character to write
   * @param x the distance from the left to begin writing from
   * @param y the distance from the top to begin writing from
   * @return this for convenient chaining of method calls
   */
  public AsciiPanel write(char argChar, int x, int y) {
    if (argChar < 0 || argChar >= glyphs.length) {
      throw new IllegalArgumentException(
          "character " + argChar + " must be within range [0," + glyphs.length + "].");
    }

    if (x < 0 || x >= widthInCharacters) {
      throw new IllegalArgumentException(
          "x " + x + " must be within range [0," + widthInCharacters + ")");
    }

    if (y < 0 || y >= heightInCharacters) {
      throw new IllegalArgumentException(
          "y " + y + " must be within range [0," + heightInCharacters + ")");
    }

    return write(argChar, x, y, defaultForegroundColor, defaultBackgroundColor);
  }

  /**
   * Write a character to the specified position with the specified foreground
   * color. This updates the cursor's position but not the default foreground
   * color.
   * 
   * @param argChar the character to write
   * @param x the distance from the left to begin writing from
   * @param y the distance from the top to begin writing from
   * @param argFg the foreground color or null to use the default
   * @return this for convenient chaining of method calls
   */
  public AsciiPanel write(char argChar, int x, int y, Color argFg) {
    if (argChar < 0 || argChar >= glyphs.length) {
      throw new IllegalArgumentException(
          "character " + argChar + " must be within range [0," + glyphs.length + "].");
    }

    if (x < 0 || x >= widthInCharacters) {
      throw new IllegalArgumentException(
          "x " + x + " must be within range [0," + widthInCharacters + ")");
    }

    if (y < 0 || y >= heightInCharacters) {
      throw new IllegalArgumentException(
          "y " + y + " must be within range [0," + heightInCharacters + ")");
    }

    return write(argChar, x, y, argFg, defaultBackgroundColor);
  }

  /**
   * Write a character to the specified position with the specified foreground and
   * background colors. This updates the cursor's position but not the default
   * foreground or background colors.
   * 
   * @param argChar the character to write
   * @param x the distance from the left to begin writing from
   * @param y the distance from the top to begin writing from
   * @param argFg the foreground color or null to use the default
   * @param argBg the background color or null to use the default
   * @return this for convenient chaining of method calls
   */
  public AsciiPanel write(char argChar, int x, int y, Color argFg, Color argBg) {
    if (argChar < 0 || argChar >= glyphs.length) {
      throw new IllegalArgumentException(
          "character " + argChar + " must be within range [0," + glyphs.length + "].");
    }

    if (x < 0 || x >= widthInCharacters) {
      throw new IllegalArgumentException(
          "x " + x + " must be within range [0," + widthInCharacters + ")");
    }

    if (y < 0 || y >= heightInCharacters) {
      throw new IllegalArgumentException(
          "y " + y + " must be within range [0," + heightInCharacters + ")");
    }

    if (argFg == null) {
      argFg = defaultForegroundColor;
    }

    if (argBg == null) {
      argBg = defaultBackgroundColor;
    }

    chars[x][y] = argChar;
    foregroundColors[x][y] = argFg;
    backgroundColors[x][y] = argBg;
    cursorX = x + 1;
    cursorY = y;

    return this;
  }

  /**
   * Write a string to the cursor's position. This updates the cursor's position.
   * 
   * @param argStr the string to write
   * @return this for convenient chaining of method calls
   */
  public AsciiPanel write(String argStr) {
    if (argStr == null) {
      throw new NullPointerException("String must not be null");
    }

    if (cursorX + argStr.length() >= widthInCharacters) {
      throw new IllegalArgumentException("cursorX + string.length() " + (cursorX + argStr.length())
          + " must be less than " + widthInCharacters + ".");
    }

    return write(argStr, cursorX, cursorY, defaultForegroundColor, defaultBackgroundColor);
  }

  /**
   * Write a string to the cursor's position with the specified foreground color.
   * This updates the cursor's position but not the default foreground color.
   * 
   * @param argStr the string to write
   * @param argFg the foreground color or null to use the default
   * @return this for convenient chaining of method calls
   */
  public AsciiPanel write(String argStr, Color argFg) {
    if (argStr == null) {
      throw new NullPointerException("String must not be null");
    }

    if (cursorX + argStr.length() >= widthInCharacters) {
      throw new IllegalArgumentException("cursorX + string.length() " + (cursorX + argStr.length())
          + " must be less than " + widthInCharacters + ".");
    }

    return write(argStr, cursorX, cursorY, argFg, defaultBackgroundColor);
  }

  /**
   * Write a string to the cursor's position with the specified foreground and
   * background colors. This updates the cursor's position but not the default
   * foreground or background colors.
   * 
   * @param argStr the string to write
   * @param argFg the foreground color or null to use the default
   * @param argBg the background color or null to use the default
   * @return this for convenient chaining of method calls
   */
  public AsciiPanel write(String argStr, Color argFg, Color argBg) {
    if (argStr == null) {
      throw new NullPointerException("String must not be null");
    }

    if (cursorX + argStr.length() >= widthInCharacters) {
      throw new IllegalArgumentException("cursorX + string.length() " + (cursorX + argStr.length())
          + " must be less than " + widthInCharacters + ".");
    }

    return write(argStr, cursorX, cursorY, argFg, argBg);
  }

  /**
   * Write a string to the specified position. This updates the cursor's position.
   * 
   * @param argStr the string to write
   * @param x the distance from the left to begin writing from
   * @param y the distance from the top to begin writing from
   * @return this for convenient chaining of method calls
   */
  public AsciiPanel write(String argStr, int x, int y) {
    if (argStr == null) {
      throw new NullPointerException("String must not be null");
    }

    if (x + argStr.length() >= widthInCharacters) {
      throw new IllegalArgumentException("x + string.length() " + (x + argStr.length())
          + " must be less than " + widthInCharacters + ".");
    }

    if (x < 0 || x >= widthInCharacters) {
      throw new IllegalArgumentException(
          "x " + x + " must be within range [0," + widthInCharacters + ")");
    }

    if (y < 0 || y >= heightInCharacters) {
      throw new IllegalArgumentException(
          "y " + y + " must be within range [0," + heightInCharacters + ")");
    }

    return write(argStr, x, y, defaultForegroundColor, defaultBackgroundColor);
  }

  /**
   * Write a string to the specified position with the specified foreground color.
   * This updates the cursor's position but not the default foreground color.
   * 
   * @param argStr the string to write
   * @param x the distance from the left to begin writing from
   * @param y the distance from the top to begin writing from
   * @param argFg the foreground color or null to use the default
   * @return this for convenient chaining of method calls
   */
  public AsciiPanel write(String argStr, int x, int y, Color argFg) {
    if (argStr == null) {
      throw new NullPointerException("string must not be null");
    }

    if (x + argStr.length() >= widthInCharacters) {
      throw new IllegalArgumentException("x + string.length() " + (x + argStr.length())
          + " must be less than " + widthInCharacters + ".");
    }

    if (x < 0 || x >= widthInCharacters) {
      throw new IllegalArgumentException(
          "x " + x + " must be within range [0," + widthInCharacters + ")");
    }

    if (y < 0 || y >= heightInCharacters) {
      throw new IllegalArgumentException(
          "y " + y + " must be within range [0," + heightInCharacters + ")");
    }

    return write(argStr, x, y, argFg, defaultBackgroundColor);
  }

  /**
   * Write a string to the specified position with the specified foreground and
   * background colors. This updates the cursor's position but not the default
   * foreground or background colors.
   * 
   * @param argStr the string to write
   * @param x the distance from the left to begin writing from
   * @param y the distance from the top to begin writing from
   * @param argFg the foreground color or null to use the default
   * @param argBg the background color or null to use the default
   * @return this for convenient chaining of method calls
   */
  public AsciiPanel write(String argStr, int x, int y, Color argFg, Color argBg) {
    if (argStr == null) {
      throw new NullPointerException("String must not be null.");
    }

    if (x + argStr.length() >= widthInCharacters) {
      throw new IllegalArgumentException("x + string.length() " + (x + argStr.length())
          + " must be less than " + widthInCharacters + ".");
    }

    if (x < 0 || x >= widthInCharacters) {
      throw new IllegalArgumentException(
          "x " + x + " must be within range [0," + widthInCharacters + ").");
    }

    if (y < 0 || y >= heightInCharacters) {
      throw new IllegalArgumentException(
          "y " + y + " must be within range [0," + heightInCharacters + ").");
    }

    if (argFg == null) {
      argFg = defaultForegroundColor;
    }

    if (argBg == null) {
      argBg = defaultBackgroundColor;
    }

    for (int i = 0; i < argStr.length(); i++) {
      write(argStr.charAt(i), x + i, y, argFg, argBg);
    }

    return this;
  }

  /**
   * Write a string to the center of the panel at the specified y position. This
   * updates the cursor's position.
   * 
   * @param argStr the string to write
   * @param y the distance from the top to begin writing from
   * @return this for convenient chaining of method calls
   */
  public AsciiPanel writeCenter(String argStr, int y) {
    if (argStr == null) {
      throw new NullPointerException("String must not be null");
    }

    if (argStr.length() >= widthInCharacters) {
      throw new IllegalArgumentException(
          "string.length() " + argStr.length() + " must be less than " + widthInCharacters + ".");
    }

    int x = (widthInCharacters - argStr.length()) / 2;

    if (y < 0 || y >= heightInCharacters) {
      throw new IllegalArgumentException(
          "y " + y + " must be within range [0," + heightInCharacters + ")");
    }

    return write(argStr, x, y, defaultForegroundColor, defaultBackgroundColor);
  }

  /**
   * Write a string to the center of the panel at the specified y position with
   * the specified foreground color. This updates the cursor's position but not
   * the default foreground color.
   * 
   * @param argStr the string to write
   * @param y the distance from the top to begin writing from
   * @param argFg the foreground color or null to use the default
   * @return this for convenient chaining of method calls
   */
  public AsciiPanel writeCenter(String argStr, int y, Color argFg) {
    if (argStr == null) {
      throw new NullPointerException("string must not be null");
    }

    if (argStr.length() >= widthInCharacters) {
      throw new IllegalArgumentException(
          "string.length() " + argStr.length() + " must be less than " + widthInCharacters + ".");
    }

    int x = (widthInCharacters - argStr.length()) / 2;

    if (y < 0 || y >= heightInCharacters) {
      throw new IllegalArgumentException(
          "y " + y + " must be within range [0," + heightInCharacters + ")");
    }

    return write(argStr, x, y, argFg, defaultBackgroundColor);
  }

  /**
   * Write a string to the center of the panel at the specified y position with
   * the specified foreground and background colors. This updates the cursor's
   * position but not the default foreground or background colors.
   * 
   * @param argStr the string to write
   * @param y the distance from the top to begin writing from
   * @param argFg the foreground color or null to use the default
   * @param argBg the background color or null to use the default
   * @return this for convenient chaining of method calls
   */
  public AsciiPanel writeCenter(String argStr, int y, Color argFg, Color argBg) {
    if (argStr == null) {
      throw new NullPointerException("String must not be null.");
    }

    if (argStr.length() >= widthInCharacters) {
      throw new IllegalArgumentException(
          "string.length() " + argStr.length() + " must be less than " + widthInCharacters + ".");
    }

    int x = (widthInCharacters - argStr.length()) / 2;

    if (y < 0 || y >= heightInCharacters) {
      throw new IllegalArgumentException(
          "y " + y + " must be within range [0," + heightInCharacters + ").");
    }

    if (argFg == null) {
      argFg = defaultForegroundColor;
    }

    if (argBg == null) {
      argBg = defaultBackgroundColor;
    }

    for (int i = 0; i < argStr.length(); i++) {
      write(argStr.charAt(i), x + i, y, argFg, argBg);
    }

    return this;
  }

  /**
   * 
   * @param argTrns
   */
  public void withEachTile(TileTransformer argTrns) {
    withEachTile(0, 0, widthInCharacters, heightInCharacters, argTrns);
  }

  /**
   * 
   * @param argLeft
   * @param argTop
   * @param argW
   * @param argH
   * @param argTrns
   */
  public void withEachTile(int argLeft, int argTop, int argW, int argH, TileTransformer argTrns) {
    AsciiCharacterData data = new AsciiCharacterData();

    for (int x0 = 0; x0 < argW; x0++) {
      for (int y0 = 0; y0 < argH; y0++) {
        int x = argLeft + x0;
        int y = argTop + y0;

        if (x < 0 || y < 0 || x >= widthInCharacters || y >= heightInCharacters) {
          continue;
        }

        data.character = chars[x][y];
        data.foregroundColor = foregroundColors[x][y];
        data.backgroundColor = backgroundColors[x][y];

        argTrns.transformTile(x, y, data);

        chars[x][y] = data.character;
        foregroundColors[x][y] = data.foregroundColor;
        backgroundColors[x][y] = data.backgroundColor;
      }
    }
  }
}