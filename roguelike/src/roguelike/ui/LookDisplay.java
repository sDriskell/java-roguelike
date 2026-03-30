package roguelike.ui;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

import roguelike.Game;
import roguelike.actors.Actor;
import roguelike.actors.Statistics;
import roguelike.items.Equipment.ItemSlot;
import roguelike.items.Inventory;
import roguelike.items.Weapon;
import roguelike.maps.MapArea;
import roguelike.ui.windows.TerminalBase;
import roguelike.ui.windows.TextWindow;
import roguelike.util.StringEx;
import squidpony.squidcolor.SColor;
import squidpony.squidcolor.SColorFactory;

/**
 * 
 */
public class LookDisplay extends TextWindow {

  private static final int BOTTOM_MARGIN = 1;
  private static final int TOP_MARGIN = 1;
  private TerminalBase terminal;
  private ArrayList<StringEx> textLines;

  /**
   * 
   * @param argTerm
   * @param argW
   * @param argH
   */
  public LookDisplay(TerminalBase argTerm, int argW, int argH) {
    super(argW, argH);
    setTerminal(argTerm);
  }

  /**
   * 
   * @param argTerm
   * @return
   */
  public LookDisplay setTerminal(TerminalBase argTerm) {
    size = new Rectangle(0, 0, argTerm.size().width, argTerm.size().height);
    terminal = argTerm;
    return this;
  }

  /**
   * 
   * @param argMap
   * @param x
   * @param y
   * @param argScrnLook
   */
  public void draw(MapArea argMap, int x, int y, Point argScrnLook) {
    int height = getHeight(argMap, x, y, true, "Looking at");
    draw(height);
  }

  /**
   * 
   * @param argMap
   * @param x
   * @param y
   * @param argDrawActor
   * @param argCaption
   * @return
   */
  public int getHeight(MapArea argMap, int x, int y, boolean argDrawActor, String argCaption) {
    textLines = getTextLines(argMap, x, y, argDrawActor);
    int height = Math.min(textLines.size(), this.size.height - 4);
    height += BOTTOM_MARGIN + TOP_MARGIN;

    return height;

  }

  /**
   * 
   * @param argH
   */
  public void draw(int argH) {
    int top = terminal.size().height - argH - 1;

    if (terminal.size().y > Game.current().getPlayer().position.y) {
      top = 0;
    }

    drawBoxShape(terminal, top, argH + 1, true);
    drawInfo(textLines, top, argH);
  }

  /**
   * 
   */
  public void erase() {
    terminal.fill(0, 0, size.width, size.height, ' ');
  }

  /**
   * 
   * @param argMap
   * @param x
   * @param y
   * @param argDrawActor
   * @return
   */
  private ArrayList<StringEx> getTextLines(MapArea argMap, int x, int y, boolean argDrawActor) {
    ArrayList<StringEx> textList = new ArrayList<>();
    Actor actor = argDrawActor ? argMap.getActorAt(x, y) : null;

    if (actor != null) {
      add(textList, "`" + actor.color().getName() + "`" + actor.getName() + " ="
          + actor.behavior().getDescription());
      add(textList, actor.getDescription());

      Weapon equipped = ItemSlot.RIGHT_HAND.getEquippedWeapon(actor);

      add(textList, " `Gray`Weapon");
      add(textList,
          "`White`" + equipped.getName() + " (" + equipped.defaultDamageType().name() + ")");
      add(textList, "");
      Statistics stats = actor.statistics();
      add(textList,
          String.format("`Bronze`Ref:`White`%3d `Bronze`Aim:`White`%3d `Bronze`Spd:`White`%3d",
              stats.reflexes(), stats.aiming(), actor.effectiveSpeed(argMap)));

      add(textList,
          String.format(" `Bronze`To:`White`%3d `Bronze`Co:`White`%3d `Bronze`Pe:`White`%3d ",
              stats.toughness.getTotalValue(), stats.conditioning.getTotalValue(),
              stats.perception.getTotalValue()));
      add(textList,
          String.format(" `Bronze`Qu:`White`%3d `Bronze`Wi:`White`%3d `Bronze`Pr:`White`%3d",
              stats.agility.getTotalValue(), stats.willpower.getTotalValue(),
              stats.presence.getTotalValue()));

      add(textList,
          String.format(" `Red`H:`White`%3d  `Bronze`MP:`White`%3d `Bronze`RP:`White`%3d ",
              actor.health().getCurrent(), stats.baseMeleePool(0), stats.baseRangedPool(0)));
      add(textList, String.format(" Can see player? `Red`%s",
          actor.canSee(Game.current().getPlayer(), argMap)));
    }

    Inventory inventory = argMap.getItemsAt(x, y);
    add(textList, "");

    if (argDrawActor) {
      add(textList, "On ground:");
    }

    int itemSize = (this.size.height - (BOTTOM_MARGIN + TOP_MARGIN)) - textList.size();
    String[] itemDescriptions = inventory.getGroupedItemListAsText(itemSize - BOTTOM_MARGIN);

    for (String string : itemDescriptions) {
      add(textList, " " + string);
    }

    return textList;
  }

  /**
   * 
   * @param arglns
   * @param argTop
   * @param argH
   */
  private void drawInfo(ArrayList<StringEx> arglns, int argTop, int argH) {
    SColor menuBgColor = SColorFactory.asSColor(30, 30, 30);
    TerminalBase background = terminal.withColor(menuBgColor, menuBgColor);
    TerminalBase text = terminal.withColor(SColor.WHITE, menuBgColor);
    int textY = TOP_MARGIN + argTop;

    background.fill(1, 1 + argTop, size.width - 2, argH - 1, ' ');

    for (int i = 0; i < arglns.size(); i++) {
      text.write(2, i + textY, arglns.get(i));
      if ((i + textY) >= (argH + argTop)) {
        text.write(3, i + textY + 2, "...");
        break;
      }
    }
  }

  /**
   * 
   * @param argList
   * @param argStr
   */
  private void add(ArrayList<StringEx> argList, String argStr) {
    StringEx str = new StringEx(argStr);
    StringEx[] lines = str.wordWrap(size.width - 2);

    for (StringEx line : lines) {
      argList.add(line);
    }
  }
}
