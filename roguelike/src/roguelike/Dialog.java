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
   * @param argW
   * @param argH
   */
  protected Dialog(int argW, int argH) {
    this(argW, argH, NOT_FULL_SCREEN);
  }

  /**
   * 
   * @param argW
   * @param argH
   * @param argIsFullScn
   */
  protected Dialog(int argW, int argH, boolean argIsFullScn) {
    super(argW, argH);
    isOpen = false;
    isFullScreen = argIsFullScn;
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
   * @param argH
   * @param argW
   * @return
   */
  protected Point getLocation(int argH, int argW) {
    int x = (int) ((argH / 2f) - (size.width / 2f));
    int y = (int) ((argW / 2f) - (size.height / 2f));
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
