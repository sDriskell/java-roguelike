package roguelike.ui.windows;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import roguelike.ui.asciipanel.AsciiPanel;
import roguelike.util.CharEx;

public class AsciiPanelTerminalView {
    private static final Logger LOG = LogManager.getLogger(AsciiPanelTerminalView.class);

    private TerminalBase terminal;
    private AsciiPanel asciiPanel;

    public AsciiPanelTerminalView(TerminalBase terminal, AsciiPanel panel) {
        this.terminal = terminal;
        this.asciiPanel = panel;

        this.terminal.setTerminalChanged(new TerminalChangeNotification() {

            @Override
            public void onChanged(int x, int y, CharEx c) {
                try {
                    asciiPanel.write(c.symbol(), x, y, c.foregroundColor(), c.backgroundColor());
                }
                catch (Exception e) {
                    LOG.error("char = {}", c.symbol());
                    throw e;
                }
            }
        });
    }
}
