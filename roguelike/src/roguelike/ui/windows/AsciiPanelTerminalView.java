package roguelike.ui.windows;

import roguelike.ui.asciipanel.AsciiPanel;
import roguelike.util.CharEx;

/**
 * 
 */
public class AsciiPanelTerminalView {

  private TerminalBase terminal;
  private AsciiPanel asciiPanel;

  public AsciiPanelTerminalView(TerminalBase argterm, AsciiPanel argPnl) {
    terminal = argterm;
    asciiPanel = argPnl;

    terminal.setTerminalChanged(new TerminalChangeNotification() {

      @Override
      public void onChanged(int x, int y, CharEx c) {
        try {
          asciiPanel.write(c.symbol(), x, y, c.foregroundColor(), c.backgroundColor());
        }
        catch (Exception e) {
          System.out.println("char=" + c.symbol());
          throw e;
        }
      }
    });
  }
}
