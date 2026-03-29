package roguelike;

import java.awt.Point;

import roguelike.ui.InputCommand;
import roguelike.ui.InputManager;
import roguelike.ui.KeyMap;
import roguelike.ui.Menu;
import roguelike.ui.windows.TerminalBase;
import roguelike.ui.windows.TextWindow;

/**
 * 
 * @param <T>
 */
public abstract class Dialog<T> extends TextWindow {
  // Box drawing tiles: "┻┗┛┫┳┣┃━┏┓╋"

  private static final boolean NOT_FULL_SCREEN = false;

  private boolean isOpen;
  private DialogResult<T> result;
  private boolean isFullScreen;
  protected TerminalBase terminal;

  /**
   * 
   * @param argWidth
   * @param argHeight
   */
  protected Dialog(int argWidth, int argHeight) {
    this(argWidth, argHeight, NOT_FULL_SCREEN);
  }

  /**
   * 
   * @param argWidth
   * @param argHeight
   * @param argIsFullScrn
   */
  protected Dialog(int argWidth, int argHeight, boolean argIsFullScrn) {
    super(argWidth, argHeight);
    isOpen = false;
    isFullScreen = argIsFullScrn;
  }

  /**
   * 
   * @return
   */
  public Point getLocation() {
    return size.getLocation();
  }

  public boolean showFullscreen() {
    return isFullScreen;
  }

  /**
   * 
   * @return
   */
  public boolean waitingForResult() {
    return isOpen;
  }

  /**
   * 
   * @param argTerm
   */
  public final void showInPane(TerminalBase argTerm) {
    int w = argTerm.size().width;
    int h = argTerm.size().height;
    Point loc = getLocation(w, h);
    size.setLocation(loc);

    this.terminal = argTerm.getWindow(loc.x, loc.y, w, h);
  }

  /**
   * 
   */
  public final void draw() {
    if (terminal == null) {
      return;
    }

    onDraw();
  }

  /**
   * 
   */
  public final void show() {
    InputManager.setActiveKeybindings(getKeyBindings());
    isOpen = true;
    onShow();
  }

  /**
   * 
   * @return
   */
  public final boolean process() {
    InputCommand nextCmd = InputManager.nextCommandPreserveKeyData();
    DialogResult<T> result = onProcess(nextCmd);

    if (result != null) {
      InputManager.previousKeyMap();
      isOpen = false;
    }
    this.result = result;

    return !isOpen;
  }

  /**
   * 
   * @return
   */
  public final DialogResult<T> result() {
    return this.result;
  }

  /**
   * 
   * @param argWidth
   * @param argHeight
   * @return
   */
  protected Point getLocation(int argWidth, int argHeight) {
    int x = (int) ((argWidth / 2f) - (size.width / 2f));
    int y = (int) ((argHeight / 2f) - (size.height / 2f));
    return new Point(x, y);
  }

  /**
   * 
   * @return
   */
  protected KeyMap getKeyBindings() {
    return Menu.KeyBindings;
  }

  /**
   * 
   */
  protected void onShow() {
  }

  /**
   * 
   * @param argCmd
   * @return
   */
  protected abstract DialogResult<T> onProcess(InputCommand argCmd);

  /**
   * 
   */
  protected abstract void onDraw();
}
