package roguelike.ui;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
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

  private JFrame frame;

  static final int FRAMES_PER_SECOND = 40;
  static final int SKIP_TICKS = 1000 / FRAMES_PER_SECOND;

  private JComponent displayPane;
  private Screen currentScreen;
  private DisplayManager displayManager;

  /**
   * 
   */
  public MainWindow() {
    displayManager = new DisplayManager(FONT_SIZE);
    initFrame();
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
   * 
   */
  private void initFrame() {
    frame = new JFrame("Untitled Roguelike");
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    try {
      frame.setIconImage(ImageIO.read(new File("./icon.png")));
    }
    catch (IOException ex) {
      /*
       * If it fails, it will default to Java icon.
       */
    }

    InputManager.registerWithFrame(frame);

    displayManager.init(WIDTH, HEIGHT);
    displayPane = displayManager.displayPane();

    frame.add(displayPane);
    frame.pack();

    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
    frame.setResizable(false);

    Log.info("Window size: " + frame.getSize().width + "x" + frame.getSize().height);

    hideMouseCursor(frame);
  }

  /**
   * Hides the mouse pointer whenever it moves through the JFrame passed in the
   * argument.
   * 
   * @param argFrame the JFRame object that will disable mouse pointer visibility
   *   in
   */
  private void hideMouseCursor(JFrame argFrame) {
    BufferedImage cursorImg = new BufferedImage(CURSOR_IMAGE_WIDTH, CURSOR_IMAGE_HEIGHT,
        BufferedImage.TYPE_INT_ARGB);

    Cursor blankCursor = Toolkit
        .getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank cursor");

    argFrame.getContentPane().setCursor(blankCursor);
  }

}
