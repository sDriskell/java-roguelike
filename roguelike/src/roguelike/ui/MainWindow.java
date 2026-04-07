package roguelike.ui;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import roguelike.screens.Screen;
import roguelike.screens.TitleScreen;
import roguelike.util.Log;

/**
 * 
 */
public class MainWindow {

  public static final int SCREEN_WIDTH = 1200;
  public static final int SCREEN_HEIGHT = 690;
  public static final int CELL_WIDTH = 9; // In AsciiPanel, this is constant
  public static final int CELL_HEIGHT = 16;

  public static final int WIDTH = SCREEN_WIDTH / CELL_WIDTH;
  public static final int HEIGHT = SCREEN_HEIGHT / CELL_HEIGHT;
  public static final int STAT_WIDTH = 50;
  public static final int FONT_SIZE = 14;
  public static final int CURSOR_IMAGE_HEIGHT = 16;
  public static final int CURSOR_IMAGE_WIDTH = 16;

  private static final String BLANK_CURSOR = "Blank Cursor";
  private static final String FRAME_NAME = "Untitled Roguelike";

  private static boolean hideMouse = true;

  private JFrame frame;

  static final int FRAMES_PER_SECOND = 40;
  static final int SKIP_TICKS = 1000 / FRAMES_PER_SECOND;

  private Screen currentScreen;
  private DisplayManager displayManager;

  /**
   * 
   */
  public MainWindow() {
    displayManager = new DisplayManager(FONT_SIZE);
    frame = initFrame(FRAME_NAME, hideMouse);
    setKeyBindings();

    /*
     * draw the title screen once before loop, since it won't process until the
     * player presses a key
     */
    currentScreen = new TitleScreen(displayManager.getTerminal());
    currentScreen.draw();
    displayManager.setDirty();
    displayManager.refresh();

    long nextTick = System.currentTimeMillis();

    // TODO: figure out what this does clearly.
    while (true) {
      currentScreen.process();
      currentScreen = Screen.currentScreen();
      long drawTicks = currentScreen.draw();

      displayManager.setDirty();
      displayManager.refresh();

      nextTick += SKIP_TICKS;
      long sleepTime = nextTick - System.currentTimeMillis();

      if (sleepTime >= 0) {
        try {
          Thread.sleep(sleepTime);
        }
        catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
      else {
        Log.warning("draw SLEEPTIME < 0: " + sleepTime + " drawTicks=" + drawTicks);
      }
    }
  }

  /**
   * 
   */
  public void setKeyBindings() {
    KeyMap defaultKeys = new KeyMap("Default");

    defaultKeys
        .bindKey(KeyEvent.VK_UP, InputCommand.UP).bindKey(KeyEvent.VK_DOWN, InputCommand.DOWN)
        .bindKey(KeyEvent.VK_LEFT, InputCommand.LEFT).bindKey(KeyEvent.VK_RIGHT, InputCommand.RIGHT)
        .bindKey(KeyEvent.VK_UP, true, InputCommand.UP_LEFT)
        .bindKey(KeyEvent.VK_RIGHT, true, InputCommand.UP_RIGHT)
        .bindKey(KeyEvent.VK_DOWN, true, InputCommand.DOWN_RIGHT)
        .bindKey(KeyEvent.VK_LEFT, true, InputCommand.DOWN_LEFT)

        .bindKey(KeyEvent.VK_PERIOD, true, InputCommand.STAIRS_DOWN)
        .bindKey(KeyEvent.VK_COMMA, true, InputCommand.STAIRS_UP)

        .bindKey(KeyEvent.VK_ENTER, InputCommand.CONFIRM)
        .bindKey(KeyEvent.VK_ESCAPE, InputCommand.CANCEL)
        .bindKey(KeyEvent.VK_SLASH, true, InputCommand.PREVIOUS_TARGET)
        .bindKey(KeyEvent.VK_SLASH, InputCommand.NEXT_TARGET)
        .bindKey(KeyEvent.VK_M, InputCommand.SHOW_MESSAGES)

        .bindKey(KeyEvent.VK_R, InputCommand.RANGED_ATTACK)
        .bindKey(KeyEvent.VK_A, InputCommand.ATTACK)

        .bindKey(KeyEvent.VK_PERIOD, InputCommand.REST)
        .bindKey(KeyEvent.VK_C, InputCommand.CLOSE_DOOR)

        .bindKey(KeyEvent.VK_I, InputCommand.INVENTORY).bindKey(KeyEvent.VK_E, InputCommand.EQUIP)

        .bindKey(KeyEvent.VK_G, InputCommand.PICK_UP).bindKey(KeyEvent.VK_L, InputCommand.LOOK);

    InputManager.setActiveKeybindings(defaultKeys);
    InputManager.defaultKeyBindings = defaultKeys;
  }

  /**
   * Initializes the setup of the MainWindow JFrame object. This includes setting,
   * packing, visibility, display panel, mouse hiding, etc.
   * 
   * @param argName String name for frame; if null or blank the default FRAME_NAME
   *   will be assigned to the frame.
   * @param argHideMouse if true hides mouse pointer when over frame
   * @return initialized JFrame
   */
  private JFrame initFrame(String argName, boolean argHideMouse) {
    if (argName == null || argName.isBlank()) {
      argName = FRAME_NAME;
    }

    JFrame jframe = new JFrame(argName);
    jframe.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    try {
      jframe.setIconImage(ImageIO.read(new File("./icon.png")));
    }
    catch (IOException ex) {
      // Default to the Java icon.
    }

    InputManager.registerWithFrame(jframe);

    displayManager.init(WIDTH, HEIGHT);
    jframe.add(displayManager.displayPane());
    jframe.pack();

    jframe.setLocationRelativeTo(null);
    jframe.setVisible(true);
    jframe.setResizable(false);

    if (argHideMouse) {
      hideMouseCursor(jframe);
    }

    return jframe;
  }

  /**
   * Hides the mouse pointer whenever it moves through the JFrame passed in the
   * argument.
   * 
   * @param argFrame the JFRame object that will disable mouse pointer visibility
   *   in it
   */
  private void hideMouseCursor(JFrame argFrame) {
    BufferedImage cursorImg = new BufferedImage(CURSOR_IMAGE_WIDTH, CURSOR_IMAGE_HEIGHT,
        BufferedImage.TYPE_INT_ARGB);

    Cursor blankCursor = Toolkit
        .getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), BLANK_CURSOR);

    argFrame.getContentPane().setCursor(blankCursor);
  }

  /**
   * @return MainWindow JFrame object
   */
  public JFrame getFrame() {
    return frame;
  }
}
