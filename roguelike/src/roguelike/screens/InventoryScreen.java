package roguelike.screens;

import roguelike.Game;
import roguelike.actors.Actor;
import roguelike.items.Equipment.ItemSlot;
import roguelike.items.Inventory;
import roguelike.items.Item;
import roguelike.ui.InputCommand;
import roguelike.ui.InputManager;
import roguelike.ui.Menu;
import roguelike.ui.MenuItem;
import roguelike.ui.windows.TerminalBase;
import roguelike.ui.windows.TextWindow;
import roguelike.util.StringEx;
import squidpony.squidcolor.SColor;

/**
 * 
 */
public class InventoryScreen extends Screen {

  private static final String TITLE = "Inventory";

  private Inventory inventory;
  private Menu<Item> menu;

  /**
   * 
   * @param argPrevious
   * @param argTerm
   */
  public InventoryScreen(Screen argPrevious, TerminalBase argTerm) {
    super(argTerm);

    terminal = argTerm;
    inventory = Game.current().getPlayer().inventory();

    menu = new Menu<Item>(inventory.allItems()) {
      protected StringEx getTextFor(Item argItm, int argPos) {
        // TODO: what is 111?
        String line = String.format("%-40s %12d", argItm.getName(), 111);
        return new StringEx(getCharForIndex(argPos) + ") " + line);
      }
    };

    InputManager.setActiveKeybindings(Menu.KeyBindings);
  }

  @Override
  protected void onLeaveScreen() {
    InputManager.previousKeyMap();
  }

  @Override
  public void onDraw() {
    // TODO: magic numbers
    int x = (int) ((60 / 2f) - (TITLE.length() / 2f));

    TerminalBase bg = terminal.withColor(SColor.BLACK, SColor.BLACK);
    bg.fill(0, 1, terminal.size().width, terminal.size().height, ' ');
    TextWindow.drawBoxShape(terminal, 1, terminal.size().height - 2, terminal.size().width, false);

    TerminalBase leftBox = terminal.getWindow(0, 1, 60, terminal.size().height - 2);
    TextWindow.drawBoxShape(leftBox, 0, leftBox.size().height, leftBox.size().width, false);

    TerminalBase term = terminal.getWindow(5, 0, 20, 20).withColor(SColor.LIGHT_BLUE);
    term.write(x, 1, TITLE);

    int currentPage = menu.getCurrentPage();
    int pageCount = menu.getPageCount();

    terminal.write(1, terminal.size().height - 2,
        String.format("Pg `Gray`(%d/%d)", currentPage, pageCount));
    int displayY = 3;

    for (MenuItem<Item> item : menu.currentPageItems()) {
      String color = "";

      if (item.isActive()) {
        color = "`Alizarin`";
      }

      terminal.write(2, displayY, color + item.getText());
      displayY++;
    }
  }

  @Override
  public void process() {
    InputCommand cmd = InputManager.nextCommandPreserveKeyData();

    if (cmd != null) {
      switch (cmd) {
        case EQUIP:
        case CONFIRM:
          equipItem(menu.getActiveItem());

        case CANCEL:
          restorePreviousScreen();
          break;

        default:
          menu.processCommand(cmd);
      }
    }
  }

  /**
   * 
   * @param argItm
   */
  public void equipItem(Item argItm) {
    Actor actor = Game.current().getPlayer();
    ItemSlot[] slots = new ItemSlot[] { ItemSlot.PROJECTILE, ItemSlot.RANGED, ItemSlot.RIGHT_HAND,
        ItemSlot.LEFT_HAND };

    for (int i = 0; i < slots.length; i++) {
      if (argItm.canEquip(slots[i])) {
        slots[i].equipItem(actor, argItm);
        Game.current().displayMessage(actor.doAction("equips the %s", argItm.getName()));
        break;
      }
    }
  }

  /**
   * 
   * @param argItm
   * @param argSlt
   */
  public void equipItem(Item argItm, ItemSlot argSlt) {
    Actor actor = Game.current().getPlayer();
    argSlt.equipItem(actor, argItm);
    Game.current().displayMessage(actor.doAction("equips the %s", argItm.getName()));
  }
}
