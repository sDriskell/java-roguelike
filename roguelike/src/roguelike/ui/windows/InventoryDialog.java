package roguelike.ui.windows;

import roguelike.Dialog;
import roguelike.DialogResult;
import roguelike.items.InventoryMenu;
import roguelike.items.Item;
import roguelike.ui.InputCommand;
import roguelike.ui.MenuItem;
import squidpony.squidcolor.SColor;
import squidpony.squidcolor.SColorFactory;

/**
 * 
 */
public class InventoryDialog extends Dialog<Item> {

  private InventoryMenu menu;

  /**
   * 
   * @param argMenu
   */
  public InventoryDialog(InventoryMenu argMenu) {
    // TODO: magic numbers
    super(60, 30);
    menu = argMenu;
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

    int currentPage = menu.getCurrentPage();
    int pageCount = menu.getPageCount();

    border.write(1, 0, String.format("Inventory `Gray`(%d/%d)", currentPage, pageCount));

    int displayY = 2;

    for (MenuItem<Item> item : menu.currentPageItems()) {
      String color = "";

      if (item.isActive()) {
        color = "`Alizarin`";
      }

      txt.write(2, displayY, color + item.getText());
      displayY++;
    }
  }

  @Override
  protected DialogResult<Item> onProcess(InputCommand argCmd) {
    DialogResult<Item> result = null;

    if (argCmd != null) {
      switch (argCmd) {
        case CONFIRM:
          Item activeItem = menu.getActiveItem();

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
          menu.processCommand(argCmd);
      }
    }

    return result;
  }

}
