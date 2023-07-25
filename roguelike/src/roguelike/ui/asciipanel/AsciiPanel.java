package roguelike.ui.asciipanel;

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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import roguelike.ui.MainWindow;
import squidpony.squidcolor.SColor;

/**
 * This simulates a code page 437 ASCII terminal display.
 * 
 * @author Trystan Spangler
 */
public class AsciiPanel extends JPanel {
    private static final Logger LOG = LogManager.getLogger(AsciiPanel.class);

    private static final long serialVersionUID = -4167851861147593092L;

    private static final String MUST_BE_GREATER_THAN_ZERO = " must be greater than 0.";
    private static final String MUST_BE_LESS_THAN = " must be less than ";
    private static final String MUST_BE_WITHIN_RANGE_0 = " must be within range [0,";

    public static final SColor black = new SColor(0, 0, 0);
    public static final SColor white = new SColor(192, 192, 192);

    private Image offscreenBuffer;
    private Graphics offscreenGraphics;
    private int widthInCharacters;
    private int heightInCharacters;
    private int charWidth = 9;
    private int charHeight = 16;

    private SColor defaultBackgroundColor;
    private SColor defaultForegroundColor;
    private int cursorX;
    private int cursorY;
    private BufferedImage glyphSprite;
    private BufferedImage[] glyphs;
    private char[][] chars;
    private SColor[][] backgroundColors;
    private SColor[][] foregroundColors;
    private char[][] oldChars;
    private SColor[][] oldBackgroundColors;
    private SColor[][] oldForegroundColors;

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
     * @param cursorX the distance from the left new text should be written to
     */
    public void setCursorX(int cursorX) {
        checkXRange(cursorX);

        this.cursorX = cursorX;
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
     * @param cursorY the distance from the top new text should be written to
     */
    public void setCursorY(int cursorY) {
        checkYRange(cursorY);

        this.cursorY = cursorY;
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
    public SColor getDefaultBackgroundColor() {
        return defaultBackgroundColor;
    }

    /**
     * Sets the default background color that is used when writing new text.
     * 
     * @param defaultBackgroundColor
     */
    public void setDefaultBackgroundColor(SColor defaultBackgroundColor) {
        if (defaultBackgroundColor == null) {
            throw new IllegalArgumentException("defaultBackgroundColor must not be null.");
        }
        this.defaultBackgroundColor = defaultBackgroundColor;
    }

    /**
     * Gets the default foreground color that is used when writing new text.
     * 
     * @return
     */
    public SColor getDefaultForegroundColor() {
        return defaultForegroundColor;
    }

    /**
     * Sets the default foreground color that is used when writing new text.
     * 
     * @param defaultForegroundColor
     */
    public void setDefaultForegroundColor(SColor defaultForegroundColor) {
        if (defaultForegroundColor == null) {
            throw new IllegalArgumentException("defaultForegroundColor must not be null.");
        }
        this.defaultForegroundColor = defaultForegroundColor;
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
     * @param width
     * @param height
     */
    public AsciiPanel(int width, int height) {
        super();

        checkWidthRange(width);

        checkHeightRange(height);

        widthInCharacters = width;
        heightInCharacters = height;
        setPreferredSize(
                new Dimension(charWidth * widthInCharacters, charHeight * heightInCharacters));

        defaultBackgroundColor = black;
        defaultForegroundColor = white;

        chars = new char[widthInCharacters][heightInCharacters];
        backgroundColors = new SColor[widthInCharacters][heightInCharacters];
        foregroundColors = new SColor[widthInCharacters][heightInCharacters];

        oldChars = new char[widthInCharacters][heightInCharacters];
        oldBackgroundColors = new SColor[widthInCharacters][heightInCharacters];
        oldForegroundColors = new SColor[widthInCharacters][heightInCharacters];

        glyphs = new BufferedImage[256];

        loadGlyphs();

        AsciiPanel.this.clear();
    }

    @Override
    public void update(Graphics g) {
        paint(g);
    }

    @Override
    public void paint(Graphics g) {
        if (g == null) {
            throw new IllegalArgumentException();
        }

        if (offscreenBuffer == null) {
            offscreenBuffer = createImage(this.getWidth(), this.getHeight());
            offscreenGraphics = offscreenBuffer.getGraphics();
        }

        for (int x = 0; x < widthInCharacters; x++) {
            for (int y = 0; y < heightInCharacters; y++) {
                if (doColorsMatch(x, y)) {
                    continue;
                }

                SColor bg = backgroundColors[x][y];
                SColor fg = foregroundColors[x][y];

                LookupOp op = setColors(bg, fg);
                BufferedImage img = op.filter(glyphs[chars[x][y]], null);
                offscreenGraphics.drawImage(img, x * charWidth, y * charHeight, null);

                oldBackgroundColors[x][y] = backgroundColors[x][y];
                oldForegroundColors[x][y] = foregroundColors[x][y];
                oldChars[x][y] = chars[x][y];
            }
        }

        g.drawImage(offscreenBuffer, 0, 0, this);
    }

    /**
     * Checks if background and foreground colors match between current and previous
     * 
     * @param x range
     * @param y range
     * @return true if old and current colors match; false if different
     */
    private boolean doColorsMatch(int x, int y) {
        return oldBackgroundColors[x][y] == backgroundColors[x][y]
                && oldForegroundColors[x][y] == foregroundColors[x][y]
                && oldChars[x][y] == chars[x][y];
    }

    private void loadGlyphs() {
        try {
            InputStream file = MainWindow.class.getResourceAsStream("/resources/cp437.png");
            glyphSprite = ImageIO.read(file);
        }
        catch (IOException e) {
            LOG.error("loadGlyphs(): {}", e.getMessage());
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
     * @param bgColor the background color
     * @param fgColor the foreground color
     * @return the <code>LookupOp</code> object (lookup table)
     */
    private LookupOp setColors(SColor bgColor, SColor fgColor) {
        short[] a = new short[256];
        short[] r = new short[256];
        short[] g = new short[256];
        short[] b = new short[256];

        byte bgr = (byte) (bgColor.getRed());
        byte bgg = (byte) (bgColor.getGreen());
        byte bgb = (byte) (bgColor.getBlue());

        byte fgr = (byte) (fgColor.getRed());
        byte fgg = (byte) (fgColor.getGreen());
        byte fgb = (byte) (fgColor.getBlue());

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
     * @param character the character to write
     * @return this for convenient chaining of method calls
     */
    public AsciiPanel clear(char character) {
        checkCharRange(character);

        return clear(character, 0, 0, widthInCharacters, heightInCharacters, defaultForegroundColor,
                defaultBackgroundColor);
    }

    /**
     * Clear the entire screen with the specified character and whatever the
     * specified foreground and background colors are.
     * 
     * @param character  the character to write
     * @param foreground the foreground color or null to use the default
     * @param background the background color or null to use the default
     * @return this for convenient chaining of method calls
     */
    public AsciiPanel clear(char character, SColor foreground, SColor background) {
        checkCharRange(character);

        return clear(character, 0, 0, widthInCharacters, heightInCharacters, foreground,
                background);
    }

    /**
     * Clear the section of the screen with the specified character and whatever the
     * default foreground and background colors are.
     * 
     * @param character the character to write
     * @param x         the distance from the left to begin writing from
     * @param y         the distance from the top to begin writing from
     * @param width     the height of the section to clear
     * @param height    the width of the section to clear
     * @return this for convenient chaining of method calls
     */
    public AsciiPanel clear(char character, int x, int y, int width, int height) {
        checkCharRange(character);

        checkXRange(x);
        checkYRange(y);

        checkWidthRange(width);
        checkHeightRange(height);

        checkXAndWidthRange(x, width);

        checkYAndHeightRange(y, height);

        return clear(character, x, y, width, height, defaultForegroundColor,
                defaultBackgroundColor);
    }

    /**
     * Clear the section of the screen with the specified character and whatever the
     * specified foreground and background colors are.
     * 
     * @param character  the character to write
     * @param x          the distance from the left to begin writing from
     * @param y          the distance from the top to begin writing from
     * @param width      the height of the section to clear
     * @param height     the width of the section to clear
     * @param foreground the foreground color or null to use the default
     * @param background the background color or null to use the default
     * @return this for convenient chaining of method calls
     */
    public AsciiPanel clear(char character, int x, int y, int width, int height, SColor foreground,
            SColor background) {
        checkCharRange(character);

        checkXRange(x);
        checkYRange(y);

        checkWidthRange(width);
        checkHeightRange(height);

        checkXAndWidthRange(x, width);
        checkYAndHeightRange(y, height);

        for (int xo = x; xo < x + width; xo++) {
            for (int yo = y; yo < y + height; yo++) {
                write(character, xo, yo, foreground, background);
            }
        }
        return this;
    }

    /**
     * Write a character to the cursor's position. This updates the cursor's
     * position.
     * 
     * @param character the character to write
     * @return this for convenient chaining of method calls
     */
    public AsciiPanel write(char character) {
        checkCharRange(character);

        return write(character, cursorX, cursorY, defaultForegroundColor, defaultBackgroundColor);
    }

    /**
     * Write a character to the cursor's position with the specified foreground
     * color. This updates the cursor's position but not the default foreground
     * color.
     * 
     * @param character  the character to write
     * @param foreground the foreground color or null to use the default
     * @return this for convenient chaining of method calls
     */
    public AsciiPanel write(char character, SColor foreground) {
        checkCharRange(character);

        return write(character, cursorX, cursorY, foreground, defaultBackgroundColor);
    }

    /**
     * Write a character to the cursor's position with the specified foreground and
     * background colors. This updates the cursor's position but not the default
     * foreground or background colors.
     * 
     * @param character  the character to write
     * @param foreground the foreground color or null to use the default
     * @param background the background color or null to use the default
     * @return this for convenient chaining of method calls
     */
    public AsciiPanel write(char character, SColor foreground, SColor background) {
        checkCharRange(character);

        return write(character, cursorX, cursorY, foreground, background);
    }

    /**
     * Write a character to the specified position. This updates the cursor's
     * position.
     * 
     * @param character the character to write
     * @param x         the distance from the left to begin writing from
     * @param y         the distance from the top to begin writing from
     * @return this for convenient chaining of method calls
     */
    public AsciiPanel write(char character, int x, int y) {
        checkCharRange(character);

        checkXRange(x);
        checkYRange(y);

        return write(character, x, y, defaultForegroundColor, defaultBackgroundColor);
    }

    /**
     * Write a character to the specified position with the specified foreground
     * color. This updates the cursor's position but not the default foreground
     * color.
     * 
     * @param character  the character to write
     * @param x          the distance from the left to begin writing from
     * @param y          the distance from the top to begin writing from
     * @param foreground the foreground color or null to use the default
     * @return this for convenient chaining of method calls
     */
    public AsciiPanel write(char character, int x, int y, SColor foreground) {
        checkCharRange(character);

        checkXRange(x);
        checkYRange(y);

        return write(character, x, y, foreground, defaultBackgroundColor);
    }

    /**
     * Write a character to the specified position with the specified foreground and
     * background colors. This updates the cursor's position but not the default
     * foreground or background colors.
     * 
     * @param character  the character to write
     * @param x          the distance from the left to begin writing from
     * @param y          the distance from the top to begin writing from
     * @param foreground the foreground color or null to use the default
     * @param background the background color or null to use the default
     * @return this for convenient chaining of method calls
     */
    public AsciiPanel write(char character, int x, int y, SColor foreground, SColor background) {
        checkCharRange(character);

        checkXRange(x);
        checkYRange(y);

        if (foreground == null) {
            foreground = defaultForegroundColor;
        }

        if (background == null) {
            background = defaultBackgroundColor;
        }

        chars[x][y] = character;
        foregroundColors[x][y] = foreground;
        backgroundColors[x][y] = background;
        cursorX = x + 1;
        cursorY = y;

        return this;
    }

    /**
     * Write a string to the cursor's position. This updates the cursor's position.
     * 
     * @param string the string to write
     * @return this for convenient chaining of method calls
     */
    public AsciiPanel write(String string) {
        checkIfStringIsNull(string);

        checkCursorXAndStringRange(string);

        return write(string, cursorX, cursorY, defaultForegroundColor, defaultBackgroundColor);
    }

    /**
     * Write a string to the cursor's position with the specified foreground color.
     * This updates the cursor's position but not the default foreground color.
     * 
     * @param string     the string to write
     * @param foreground the foreground color or null to use the default
     * @return this for convenient chaining of method calls
     */
    public AsciiPanel write(String string, SColor foreground) {
        checkIfStringIsNull(string);

        checkCursorXAndStringRange(string);

        return write(string, cursorX, cursorY, foreground, defaultBackgroundColor);
    }

    /**
     * Write a string to the cursor's position with the specified foreground and
     * background colors. This updates the cursor's position but not the default
     * foreground or background colors.
     * 
     * @param string     the string to write
     * @param foreground the foreground color or null to use the default
     * @param background the background color or null to use the default
     * @return this for convenient chaining of method calls
     */
    public AsciiPanel write(String string, SColor foreground, SColor background) {
        checkIfStringIsNull(string);

        checkCursorXAndStringRange(string);

        return write(string, cursorX, cursorY, foreground, background);
    }

    /**
     * Write a string to the specified position. This updates the cursor's position.
     * 
     * @param string the string to write
     * @param x      the distance from the left to begin writing from
     * @param y      the distance from the top to begin writing from
     * @return this for convenient chaining of method calls
     */
    public AsciiPanel write(String string, int x, int y) {
        checkIfStringIsNull(string);
        checkXAndStringRange(string, x);

        checkXRange(x);
        checkYRange(y);

        return write(string, x, y, defaultForegroundColor, defaultBackgroundColor);
    }

    /**
     * Write a string to the specified position with the specified foreground color.
     * This updates the cursor's position but not the default foreground color.
     * 
     * @param string     the string to write
     * @param x          the distance from the left to begin writing from
     * @param y          the distance from the top to begin writing from
     * @param foreground the foreground color or null to use the default
     * @return this for convenient chaining of method calls
     */
    public AsciiPanel write(String string, int x, int y, SColor foreground) {
        checkIfStringIsNull(string);
        checkXAndStringRange(string, x);

        checkXRange(x);
        checkYRange(y);

        return write(string, x, y, foreground, defaultBackgroundColor);
    }

    /**
     * Write a string to the specified position with the specified foreground and
     * background colors. This updates the cursor's position but not the default
     * foreground or background colors.
     * 
     * @param string     the string to write
     * @param x          the distance from the left to begin writing from
     * @param y          the distance from the top to begin writing from
     * @param foreground the foreground color or null to use the default
     * @param background the background color or null to use the default
     * @return this for convenient chaining of method calls
     */
    public AsciiPanel write(String string, int x, int y, SColor foreground, SColor background) {
        checkIfStringIsNull(string);
        checkXAndStringRange(string, x);

        checkXRange(x);
        checkYRange(y);

        if (foreground == null) {
            foreground = defaultForegroundColor;
        }

        if (background == null) {
            background = defaultBackgroundColor;
        }

        for (int i = 0; i < string.length(); i++) {
            write(string.charAt(i), x + i, y, foreground, background);
        }

        return this;
    }

    /**
     * Write a string to the center of the panel at the specified y position. This
     * updates the cursor's position.
     * 
     * @param string the string to write
     * @param y      the distance from the top to begin writing from
     * @return this for convenient chaining of method calls
     */
    public AsciiPanel writeCenter(String string, int y) {
        checkIfStringIsNull(string);

        checkStringRange(string);

        int x = (widthInCharacters - string.length()) / 2;

        if (y < 0 || y >= heightInCharacters) {
            throw new IllegalArgumentException(
                    "y " + y + MUST_BE_WITHIN_RANGE_0 + heightInCharacters + ")");
        }
        return write(string, x, y, defaultForegroundColor, defaultBackgroundColor);
    }

    /**
     * Write a string to the center of the panel at the specified y position with
     * the specified foreground color. This updates the cursor's position but not
     * the default foreground color.
     * 
     * @param string     the string to write
     * @param y          the distance from the top to begin writing from
     * @param foreground the foreground color or null to use the default
     * @return this for convenient chaining of method calls
     */
    public AsciiPanel writeCenter(String string, int y, SColor foreground) {
        checkIfStringIsNull(string);
        checkStringRange(string);

        checkYRange(y);

        int x = (widthInCharacters - string.length()) / 2;

        return write(string, x, y, foreground, defaultBackgroundColor);
    }

    /**
     * Write a string to the center of the panel at the specified y position with
     * the specified foreground and background colors. This updates the cursor's
     * position but not the default foreground or background colors.
     * 
     * @param string     the string to write
     * @param y          the distance from the top to begin writing from
     * @param foreground the foreground color or null to use the default
     * @param background the background color or null to use the default
     * @return this for convenient chaining of method calls
     */
    public AsciiPanel writeCenter(String string, int y, SColor foreground, SColor background) {
        checkIfStringIsNull(string);
        checkStringRange(string);

        checkYRange(y);

        int x = (widthInCharacters - string.length()) / 2;

        if (foreground == null) {
            foreground = defaultForegroundColor;
        }

        if (background == null) {
            background = defaultBackgroundColor;
        }

        for (int i = 0; i < string.length(); i++) {
            write(string.charAt(i), x + i, y, foreground, background);
        }

        return this;
    }

    public void withEachTile(TileTransformer transformer) {
        withEachTile(0, 0, widthInCharacters, heightInCharacters, transformer);
    }

    public void withEachTile(int left, int top, int width, int height,
            TileTransformer transformer) {
        AsciiCharacterData data = new AsciiCharacterData();

        for (int x0 = 0; x0 < width; x0++) {
            for (int y0 = 0; y0 < height; y0++) {
                int x = left + x0;
                int y = top + y0;

                if (x < 0 || y < 0 || x >= widthInCharacters || y >= heightInCharacters) {
                    continue;
                }

                data.character = chars[x][y];
                data.foregroundColor = foregroundColors[x][y];
                data.backgroundColor = backgroundColors[x][y];

                transformer.transformTile(x, y, data);

                chars[x][y] = data.character;
                foregroundColors[x][y] = data.foregroundColor;
                backgroundColors[x][y] = data.backgroundColor;
            }
        }
    }

    /**
     * Checks the range of the height to be equal to or greater than one.
     * 
     * @param height int value to be tested
     * @throws IllegalArgumentException if height is zero or less
     */
    private void checkHeightRange(int height) {
        if (height < 1) {
            throw new IllegalArgumentException("height " + height + MUST_BE_GREATER_THAN_ZERO);
        }
    }

    /**
     * Checks the range of the width to be equal to or greater than one.
     * 
     * @param width int value to be tested
     * @throws IllegalArgumentException if width is zero or less
     */
    private void checkWidthRange(int width) {
        if (width < 1) {
            throw new IllegalArgumentException("width " + width + MUST_BE_GREATER_THAN_ZERO);
        }
    }

    /**
     * Checks char range in comparison to glyphs.
     * 
     * @param character to measured for length
     * @throws IllegalArgumentException if char is not within range
     */
    private void checkCharRange(char character) {
        if (character < 0 || character >= glyphs.length) {
            throw new IllegalArgumentException(
                    "character " + character + MUST_BE_WITHIN_RANGE_0 + glyphs.length + "].");
        }
    }

    /**
     * Checks if y is in range with height in characters.
     * 
     * @param y int representing y coordinate
     * @throws IllegalARgumentException if y is outside of range
     */
    private void checkYRange(int y) {
        if (y < 0 || y >= heightInCharacters) {
            throw new IllegalArgumentException(
                    "y " + y + MUST_BE_WITHIN_RANGE_0 + heightInCharacters + ").");
        }
    }

    /**
     * Checks if x is in range with width in characters
     * 
     * @param x int representing x coordinate
     * @throws IllegalArgumentException if x is outside of range
     */
    private void checkXRange(int x) {
        if (x < 0 || x >= widthInCharacters) {
            throw new IllegalArgumentException(
                    "x " + x + MUST_BE_WITHIN_RANGE_0 + widthInCharacters + ").");
        }
    }

    /**
     * Checks if the sum of y and height is within range.
     * 
     * @param y      int representing y coordinate
     * @param height int representing height for range
     * @throws IllegalArgumentException if the sum of y and height is out of range
     */
    private void checkYAndHeightRange(int y, int height) {
        if (y + height > heightInCharacters) {
            throw new IllegalArgumentException("y + height " + (y + height)
                    + MUST_BE_GREATER_THAN_ZERO + (heightInCharacters + 1) + ".");
        }
    }

    /**
     * Checks if the sum of x and width is within range.
     * 
     * @param x     int representing x coordinate
     * @param width int representing height of range
     * @throws IllegalArgumentException if the sum of x and height is out of range
     */
    private void checkXAndWidthRange(int x, int width) {
        if (x + width > widthInCharacters) {
            throw new IllegalArgumentException("x + width " + (x + width)
                    + MUST_BE_GREATER_THAN_ZERO + (widthInCharacters + 1) + ".");
        }
    }

    /**
     * Checks the sum of String length and x arguments are within range.
     * 
     * @param string String object whose length is measured
     * @param x      int is x coordinate measured
     * @throws IllegalArgumentException if the sum of the length of String and x
     *                                  exceed range
     */
    private void checkXAndStringRange(String string, int x) {
        if (x + string.length() >= widthInCharacters) {
            throw new IllegalArgumentException("x + string.length() " + (x + string.length())
                    + MUST_BE_LESS_THAN + widthInCharacters + ".");
        }
    }

    /**
     * Checks the sum of String length and cursor's X coordinate to see if within
     * range.
     * 
     * @param string String object whose length is measured in conjunction with
     *               cursor's x
     * @throws IllegalArgumentException if cursorX and string length exceeds range
     */
    private void checkCursorXAndStringRange(String string) {
        if (cursorX + string.length() >= widthInCharacters) {
            throw new IllegalArgumentException("cursorX + string.length() "
                    + (cursorX + string.length()) + MUST_BE_LESS_THAN + widthInCharacters + ".");
        }
    }

    /**
     * Simple check if string is null.
     * 
     * @param string String object checked if null
     * @throws IllegalArgumentException if string is null
     */
    private void checkIfStringIsNull(String string) {
        if (string == null) {
            throw new IllegalArgumentException("string must not be null.");
        }
    }

    /**
     * Checks String length in comparison to width.
     * 
     * @param string String object whose length is measured
     * @throws IllegalArgumentException if string length exceeds width
     */
    private void checkStringRange(String string) {
        if (string.length() >= widthInCharacters) {
            throw new IllegalArgumentException("string.length() " + string.length()
                    + MUST_BE_LESS_THAN + widthInCharacters + ".");
        }
    }
}