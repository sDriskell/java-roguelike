package roguelike.ui.windows;

import java.util.ArrayList;

import roguelike.Dialog;
import roguelike.DialogResult;
import roguelike.Game;
import roguelike.actors.Actor;
import roguelike.actors.Statistics;
import roguelike.items.Equipment.ItemSlot;
import roguelike.items.Inventory;
import roguelike.items.Item;
import roguelike.items.Weapon;
import roguelike.maps.MapArea;
import roguelike.ui.InputCommand;
import squidpony.squidcolor.SColor;
import squidpony.squidcolor.SColorFactory;

/**
 * 
 */
public class LookDialog extends Dialog<InputCommand> {

  private MapArea mapArea;
  private int x;
  private int y;

  /**
   * 
   * @param argMapArea
   * @param x
   * @param y
   */
  public LookDialog(MapArea argMapArea, int x, int y) {
    super(50, 20);
    mapArea = argMapArea;
    this.x = x;
    this.y = y;

    System.out.println("LookDialog created");
  }

  @Override
  protected void onDraw() {
    SColor menuBgCol = SColorFactory.asSColor(30, 30, 30);
    TerminalBase bg = terminal.withColor(menuBgCol, menuBgCol);
    TerminalBase txt = terminal.withColor(SColor.WHITE, menuBgCol);

    ArrayList<String> textList = new ArrayList<>();
    bg.fill(0, 0, size.width, size.height, ' ');
    Actor act = mapArea.getActorAt(x, y);

    if (act != null) {
      textList.add("`" + act.color().getName() + "`" + act.getDescription());
      textList.add("");
      Weapon equipped = ItemSlot.RIGHT_HAND.getEquippedWeapon(act);
      textList
          .add(" `Gray`Weapon: `White`" + equipped.getDescription() + " ("
              + equipped.defaultDamageType().name() + ")");

      Statistics stats = act.statistics();
      textList
          .add(String
              .format(
                  " `Bronze`MP:`White`%3d `Bronze`RP:`White`%3d `Bronze`Ref:`White`%3d `Bronze`Aim:`White`%3d `Bronze`Spd:`White`%3d",
                  stats.baseMeleePool(0), stats.baseRangedPool(0), stats.reflexes(), stats.aiming(),
                  act.effectiveSpeed(mapArea)));

      textList
          .add(String
              .format(
                  " `Bronze`To:`White`%3d `Bronze`Co:`White`%3d `Bronze`Pe:`White`%3d "
                      + "`Bronze`Qu:`White`%3d `Bronze`Wi:`White`%3d `Bronze`Pr:`White`%3d",
                  stats.toughness.getTotalValue(), stats.conditioning.getTotalValue(),
                  stats.perception.getTotalValue(), stats.agility.getTotalValue(),
                  stats.willpower.getTotalValue(), stats.presence.getTotalValue()));

      textList.add(String.format(" `Red`H:`White`%3d", act.health().getCurrent()));
      textList.add("");
      textList
          .add(String
              .format(" Can see player? `Red`%s", act.canSee(Game.current().getPlayer(), mapArea)));
    }

    int textY = 2;
    Inventory inv = mapArea.getItemsAt(x, y);
    textList.add("");
    textList.add("On ground:");

    if (inv != null && inv.any()) {
      for (Item i : inv.allItems()) {
        textList.add(i.getName());
      }
    }

    for (int x = 0; x < textList.size(); x++) {
      txt.write(2, x + textY, textList.get(x));

      if ((x + textY) >= this.size.height - 4) {
        txt.write(3, x + textY + 2, "...");
        break;
      }
    }
  }

  @Override
  protected DialogResult<InputCommand> onProcess(InputCommand argCmd) {
    if (argCmd != null) {
      return DialogResult.ok(InputCommand.CONFIRM);
    }

    return null;
  }
}
