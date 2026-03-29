package roguelike.ui;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.InputStream;

import javax.swing.JComponent;

import roguelike.screens.TitleScreen;
import roguelike.ui.asciipanel.AsciiPanel;
import roguelike.ui.windows.AsciiPanelTerminalView;
import roguelike.ui.windows.Terminal;
import roguelike.ui.windows.TerminalBase;
import roguelike.ui.windows.TerminalChangeNotification;
import roguelike.util.CharEx;
import roguelike.util.Log;

/**
 * 
 */
public class DisplayManager {
  private static final String FONT_NAME = "Nouveau_IBM.ttf";
  private static final String BACKUP_FONT_NAME = "Lucidia";

  private Font font;
  private JComponent displayPane;
  private TerminalBase mainDisplay;
  private int fontSize;
  private int gridWidth;
  private int gridHeight;
  private boolean dirty;

  private AsciiPanel asciiPanel;
  private AsciiPanelTerminalView terminalView;

  private static DisplayManager self;

  /**
   * 
   * @param argFontSize
   */
  public DisplayManager(int argFontSize) {
    fontSize = argFontSize;
    self = this;
  }

  /**
   * 
   * @return
   */
  public static DisplayManager instance() {
    return self;
  }

  /**
   * 
   * @return
   */
  public Font screenFont() {
    return font;
  }

  /**
   * 
   * @return
   */
  public JComponent displayPane() {
    return displayPane;
  }

  /**
   * 
   */
  public void refresh() {
    if (dirty) {
      asciiPanel.repaint();
      dirty = false;
    }
  }

  /**
   * 
   */
  public void setDirty() {
    dirty = true;
  }

  /**
   * 
   * @return
   */
  public TerminalBase getTerminal() {
    if (mainDisplay == null) {
      mainDisplay = new Terminal(gridWidth, gridHeight, new TerminalChangeNotification() {

        @Override
        public void onChanged(int x, int y, CharEx c) {
        }
      });
    }

    return mainDisplay;
  }

  /**
   * 
   * @return
   */
  public AsciiPanelTerminalView getTerminalView() {
    if (terminalView == null) {
      terminalView = new AsciiPanelTerminalView(getTerminal(), asciiPanel);
    }

    return terminalView;
  }

  /**
   * 
   * @param argWidth
   * @param argHeight
   */
  public void init(int argWidth, int argHeight) {
    Log.info("DisplayManager.init(" + argWidth + ", " + argHeight + ")");
    font = getFont(FONT_NAME);
    font = font.deriveFont((float) fontSize);

    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    ge.registerFont(font);

    gridWidth = argWidth;
    gridHeight = argHeight;
    asciiPanel = new AsciiPanel(argWidth, argHeight);
    displayPane = asciiPanel;
  }

  /**
   * 
   * @param argName
   * @return
   */
  private Font getFont(String argName) {
    Font font = null;
    String fName = "./assets/" + argName;

    try {
      InputStream is = TitleScreen.class.getResourceAsStream("/resources/assets/" + argName);
      font = Font.createFont(Font.TRUETYPE_FONT, is);

      System.out.println("Loaded " + fName);
    }
    catch (Exception ex) {
      ex.printStackTrace();
      System.err.println(fName + " not loaded.  Using " + FONT_NAME + " font.");
      font = new Font(BACKUP_FONT_NAME, Font.PLAIN, 24);
    }

    return font;
  }
}
