package roguelike.ui.windows;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import roguelike.Dialog;
import roguelike.DialogResult;
import roguelike.ui.InputCommand;
import roguelike.ui.KeyMap;
import roguelike.ui.Menu;
import roguelike.ui.MenuItem;
import roguelike.util.StringEx;
import squidpony.squidcolor.SColor;
import squidpony.squidcolor.SColorFactory;

/**
 * 
 */
public class ChooseItemCommandDialog extends Dialog<InputCommand> {

  // TODO: need a better key-binding approach
  private static final KeyMap KEY_BINDINGS = new KeyMap("ChooseItemCommand")
      .bindKey(KeyEvent.VK_ENTER, InputCommand.CONFIRM)
      .bindKey(KeyEvent.VK_ESCAPE, InputCommand.CANCEL).bindKey(KeyEvent.VK_UP, InputCommand.UP)
      .bindKey(KeyEvent.VK_DOWN, InputCommand.DOWN).bindKey(KeyEvent.VK_E, InputCommand.EQUIP)
      .bindKey(KeyEvent.VK_U, InputCommand.USE).bindKey(KeyEvent.VK_D, InputCommand.DROP)
      .bindKey(KeyEvent.VK_LEFT, InputCommand.PREVIOUS_PAGE)
      .bindKey(KeyEvent.VK_RIGHT, InputCommand.NEXT_PAGE);

  Menu<InputCommand> commands;

  /**
   * 
   */
  public ChooseItemCommandDialog() {
    // TODO: magic numbers
    super(20, 7, false);

    ArrayList<InputCommand> cmdList = new ArrayList<>();
    cmdList.add(InputCommand.EQUIP);
    cmdList.add(InputCommand.USE);
    cmdList.add(InputCommand.DROP);

    commands = new Menu<InputCommand>(cmdList) {

      @Override
      protected StringEx getTextFor(InputCommand argItm, int argPos) {
        String capFirstLetter = argItm.toString();
        String tgt = String
            .format("%s)%s", capFirstLetter.substring(0, 1).toLowerCase(),
                capFirstLetter.substring(1).toLowerCase());
        return new StringEx(tgt);
      }

      @Override
      protected int getIndexOfChar(char argKey) {
        switch (argKey) {
          case 'e':
            return 0;
          case 'u':
            return 1;
          case 'd':
            return 2;
        }

        return super.getIndexOfChar(argKey);
      }
    };
  }

  @Override
  protected KeyMap getKeyBindings() {
    return ChooseItemCommandDialog.KEY_BINDINGS;
  }

  @Override
  public Point getLocation() {
    Point p = super.getLocation();
    p.translate(2, 2);
    return p;
  }

  @Override
  protected DialogResult<InputCommand> onProcess(InputCommand argCmd) {
    DialogResult<InputCommand> result = null;

    if (argCmd != null) {
      switch (argCmd) {
        case EQUIP:
        case CONFIRM:
          InputCommand activeItem = commands.getActiveItem();
          if (activeItem != null) {
            result = DialogResult.ok(activeItem);
          }
          else {
            result = DialogResult.ok(null);
          }
        case CANCEL:
          if (result == null) {
            result = DialogResult.cancel();
          }
          break;
        default:
          commands.processCommand(argCmd);
      }
    }

    return result;
  }

  @Override
  protected void onDraw() {
    SColor menuBgColor = SColorFactory.asSColor(30, 30, 30);
    TerminalBase border = terminal.withColor(SColor.WHITE, SColor.BLACK);
    TerminalBase bg = terminal.withColor(menuBgColor, menuBgColor);
    TerminalBase txt = terminal.withColor(SColor.WHITE, menuBgColor);

    bg.fill(0, 0, size.width, size.height, ' ');
    border.fill(0, 0, size.width, 1, ' ');

    drawBoxShape(border);

    int currentPage = commands.getCurrentPage();
    int pageCount = commands.getPageCount();

    border.write(1, 0, String.format("Action?", currentPage, pageCount));

    int displayY = 2;

    for (MenuItem<InputCommand> item : commands.currentPageItems()) {
      String color = "";

      if (item.isActive()) {
        color = "`Alizarin`";
      }

      txt.write(2, displayY, color + item.getText());
      displayY++;
    }
  }

}
