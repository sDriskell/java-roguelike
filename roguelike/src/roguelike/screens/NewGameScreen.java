package roguelike.screens;

import roguelike.GameLoader;
import roguelike.ui.windows.TerminalBase;

/**
 * 
 */
public class NewGameScreen extends Screen {

  /**
   * 
   * @param argTerm
   */
  protected NewGameScreen(TerminalBase argTerm) {
    super(argTerm);
  }

  @Override
  public void onDraw() {
    terminal.fill(0, 0, terminal.size().width, terminal.size().height, ' ');
  }

  @Override
  public void process() {
    setNextScreen(new MainScreen(terminal, GameLoader.newGame()), false);
  }
}
