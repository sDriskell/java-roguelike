package roguelike.screens;

import java.awt.Rectangle;

import roguelike.ui.DisplayManager;
import roguelike.ui.MainWindow;
import roguelike.ui.windows.TerminalBase;

/**
 * 
 */
public abstract class Screen {
  static final int WIDTH = MainWindow.WIDTH;
  static final int HEIGHT = MainWindow.HEIGHT;

  protected TerminalBase terminal;
  private static Screen nextScreen;
  private Screen previousScreen;

  /**
   * 
   * @param argTerm
   */
  protected Screen(TerminalBase argTerm) {
    if (argTerm == null) {
      throw new IllegalArgumentException("terminal cannot be null");
    }

    terminal = argTerm;
    setNextScreen(this, false);
  }

  /**
   * 
   * @return
   */
  public static Screen currentScreen() {
    return nextScreen;
  }

  /**
   * 
   * @return
   */
  public final TerminalBase terminal() {
    return terminal;
  }

  /**
   * 
   * @return
   */
  public Rectangle getDrawableArea() {
    return new Rectangle(0, 0, terminal.size().width, terminal.size().height);
  }

  /**
   * 
   * @return
   */
  public final long draw() {
    long start = System.currentTimeMillis();
    onDraw();
    return System.currentTimeMillis() - start;
  }

  /**
   * 
   * @param argScn
   */
  public final void setNextScreen(Screen argScn) {
    setNextScreen(argScn, true);
  }

  /**
   * 
   */
  public abstract void process();

  /**
   * 
   * @return
   */
  protected Screen nextScreen() {
    return nextScreen;
  }

  /**
   * 
   * @param argScn
   * @param keepPrevious
   */
  protected final void setNextScreen(Screen argScn, boolean keepPrevious) {
    nextScreen = argScn;

    if (keepPrevious) {
      nextScreen.previousScreen = this;
    }

    DisplayManager.instance().setDirty();
  }

  /**
   * 
   */
  protected final void restorePreviousScreen() {
    if (previousScreen != null) {
      setNextScreen(previousScreen, false);
      previousScreen = null;
      onLeaveScreen();
    }
  }

  /**
   * 
   */
  protected void onLeaveScreen() {
  }

  /**
   * 
   */
  protected abstract void onDraw();

}
