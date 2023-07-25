package roguelike.screens;

import java.awt.Rectangle;

import roguelike.ui.DisplayManager;
import roguelike.ui.MainWindow;
import roguelike.ui.windows.TerminalBase;

public abstract class Screen {
    static final int WIDTH = MainWindow.WIDTH;
    static final int HEIGHT = MainWindow.HEIGHT;

    protected TerminalBase terminal;
    private static Screen nextScreen;
    private Screen previousScreen;

    protected Screen(TerminalBase terminal) {
        if (terminal == null) {
            throw new IllegalArgumentException("terminal cannot be null");
        }

        this.terminal = terminal;
        setNextScreen(this, false);
    }

    public static Screen currentScreen() {
        return nextScreen;
    }

    public final TerminalBase terminal() {
        return terminal;
    }

    public Rectangle getDrawableArea() {
        return new Rectangle(0, 0, terminal.size().width, terminal.size().height);
    }

    public final long draw() {
        long start = System.currentTimeMillis();
        onDraw();
        
        return System.currentTimeMillis() - start;
    }

    public final void setNextScreen(Screen screen) {
        setNextScreen(screen, true);
    }

    public abstract void process();

    protected Screen nextScreen() {
        return nextScreen;
    }

    protected final void setNextScreen(Screen screen, boolean storePrevious) {
        nextScreen = screen;
        
        if (storePrevious) {
            nextScreen.previousScreen = this;
        }
        DisplayManager.instance().turnOnDirty();
    }

    protected final void restorePreviousScreen() {
        if (previousScreen != null) {
            setNextScreen(previousScreen, false);
            previousScreen = null;
            onLeaveScreen();
        }
    }

    protected void onLeaveScreen() {
    }

    protected abstract void onDraw();

}
