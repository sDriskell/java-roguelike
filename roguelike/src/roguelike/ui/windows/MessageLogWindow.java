package roguelike.ui.windows;

import java.util.List;

import roguelike.Dialog;
import roguelike.DialogResult;
import roguelike.MessageDisplayProperties;
import roguelike.MessageLog;
import roguelike.ui.InputCommand;
import roguelike.ui.Menu;
import roguelike.ui.MenuItem;
import roguelike.util.StringEx;
import squidpony.squidcolor.SColor;

/**
 * 
 */
public class MessageLogWindow extends Dialog<InputCommand> {

  private Menu<MessageDisplayProperties> messageMenu;

  /**
   * 
   * @param argW
   * @param argH
   * @param argMsg
   */
  public MessageLogWindow(int argW, int argH, MessageLog argMsg) {
    super(argW, argH);

    messageMenu = new Menu<MessageDisplayProperties>(argMsg.getAll(), 25) {

      @Override
      protected StringEx getTextFor(MessageDisplayProperties item, int position) {
        return item.getText();
      }
    };
  }

  @Override
  protected DialogResult<InputCommand> onProcess(InputCommand argCmd) {
    DialogResult<InputCommand> result = null;
    if (argCmd != null) {

      switch (argCmd) {
        case CONFIRM:
        case CANCEL:
          return DialogResult.ok(argCmd);
        default:
          messageMenu.processCommand(argCmd);
      }
    }

    return result;
  }

  @Override
  protected void onDraw() {
    terminal.withColor(SColor.MOUSY_INDIGO).fill(0, 0, size.width, size.height, ' ');
    drawBoxShape(terminal);

    List<MenuItem<MessageDisplayProperties>> currentPage = messageMenu.currentPageItems();
    int y = 1;

    for (MenuItem<MessageDisplayProperties> item : currentPage) {
      StringEx text = item.getText();
      StringEx[] lines = text.wordWrap(size.width - 1);

      for (int x = 0; x < lines.length; x++) {
        terminal.write(1, y, lines[x]);
        y++;
      }
    }
  }
}
