package roguelike.screens;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

import roguelike.Game;
import roguelike.actors.Actor;
import roguelike.actors.Statistics;
import roguelike.functionalinterfaces.CursorCallback;
import roguelike.items.Equipment.ItemSlot;
import roguelike.items.Inventory;
import roguelike.items.Weapon;
import roguelike.maps.MapArea;
import roguelike.ui.LookCursor;
import roguelike.ui.windows.TerminalBase;
import roguelike.ui.windows.TextWindow;
import roguelike.util.Coordinate;
import roguelike.util.StringEx;
import squidpony.squidcolor.SColor;
import squidpony.squidcolor.SColorFactory;

/**
 * 
 */
public class LookScreen extends CursorScreen {

  private InformationPanel lookDisplay;
  private Point lookPoint;

  /**
   * 
   * @param argTrm
   * @param argCur
   * @param argResult
   */
  public LookScreen(TerminalBase argTrm, LookCursor argCur, CursorCallback argResult) {
    super(argTrm, argCur, argResult);

    lookDisplay = new InformationPanel(40, 20);
    argCur.setLookScreen(this);
  }

  /**
   * Sets the map coordinates that the LookDisplay should show information about.
   * If this is null, then the LookDisplay will be hidden.
   * 
   * @param argMap
   * @param argPos
   */
  public void lookAt(MapArea argMap, Point argPos) {
    lookPoint = argPos;
  }

  @Override
  protected void onDrawAdditional(MapArea argCurrent, Coordinate argCentPos, Rectangle argScnArea) {
    /* draw the look description box if there's anything here */
    drawLookDisplay(argCurrent);
  }

  /**
   * 
   * @param argCurrent
   */
  private void drawLookDisplay(MapArea argCurrent) {
    if (lookPoint == null) {
      return;
    }

    lookDisplay.draw(terminal, argCurrent, lookPoint.x, lookPoint.y);
  }

  /**
   * 
   */
  private class InformationPanel extends TextWindow {
    private static final int BOTTOM_MARGIN = 1;
    private static final int TOP_MARGIN = 1;

    /**
     * 
     * @param argW
     * @param argH
     */
    public InformationPanel(int argW, int argH) {
      super(argW, argH);
    }

    /**
     * 
     * @param argTerm
     * @param argMap
     * @param x
     * @param y
     */
    public void draw(TerminalBase argTerm, MapArea argMap, int x, int y) {
      Rectangle bounds = new Rectangle(lookPoint.x + 1, lookPoint.y + 1, size.width, size.height);

      if (isWithinTerminalBounds((int) bounds.getMaxX(), 1)) {
        // draw the box to the right
      }
      else {
        // draw the box to the left
        bounds.x -= (bounds.width + 1);
      }

      if (isWithinTerminalBounds(1, (int) bounds.getMaxY())) {
        // draw box down
      }
      else {
        // draw box up
        bounds.y -= (bounds.height + 1);
      }

      TerminalBase lookTerm = argTerm.getWindow(bounds.x, bounds.y, bounds.width, bounds.height);
      drawBoxShape(lookTerm);
      ArrayList<StringEx> lines = getTextLines(argMap, x, y, true);
      drawInfo(lookTerm, lines, 0, bounds.height);
    }

    /**
     * 
     * @param x
     * @param y
     * @return
     */
    private boolean isWithinTerminalBounds(int x, int y) {
      Rectangle terminalBounds = terminal.size();
      return terminalBounds.contains(x, y);
    }

    /**
     * 
     * @param argTerm
     * @param argTxtln
     * @param argTop
     * @param argH
     */
    private void drawInfo(TerminalBase argTerm, ArrayList<StringEx> argTxtln, int argTop,
        int argH) {
      SColor menuBgColor = SColorFactory.asSColor(30, 30, 30);
      TerminalBase background = argTerm.withColor(menuBgColor, menuBgColor);
      TerminalBase text = argTerm.withColor(SColor.WHITE, menuBgColor);
      int textY = TOP_MARGIN + argTop;
      background.fill(1, 1 + argTop, size.width - 2, argH - 2, ' ');

      for (int i = 0; i < argTxtln.size(); i++) {
        text.write(2, i + textY, argTxtln.get(i));

        if ((i + textY) >= (argH + argTop)) {
          text.write(3, i + textY + 2, "...");
          break;
        }
      }
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
      ArrayList<StringEx> txtLns = new ArrayList<>();
      Actor actor = argDrawActor ? argMap.getActorAt(x, y) : null;

      if (actor != null) {
        add(txtLns, "`" + actor.color().getName() + "`" + actor.getName() + " ="
            + actor.behavior().getDescription());
        add(txtLns, actor.getDescription());

        Weapon equipped = ItemSlot.RIGHT_HAND.getEquippedWeapon(actor);

        add(txtLns, " `Gray`Weapon");
        add(txtLns,
            "`White`" + equipped.getName() + " (" + equipped.defaultDamageType().name() + ")");
        add(txtLns, "");

        Statistics stats = actor.statistics();

        add(txtLns,
            String.format("`Bronze`Ref:`White`%3d `Bronze`Aim:`White`%3d `Bronze`Spd:`White`%3d",
                stats.reflexes(), stats.aiming(), actor.effectiveSpeed(argMap)));

        add(txtLns,
            String.format(" `Bronze`To:`White`%3d `Bronze`Co:`White`%3d `Bronze`Pe:`White`%3d ",
                stats.toughness.getTotalValue(), stats.conditioning.getTotalValue(),
                stats.perception.getTotalValue()));
        add(txtLns,
            String.format(" `Bronze`Qu:`White`%3d `Bronze`Wi:`White`%3d `Bronze`Pr:`White`%3d",
                stats.agility.getTotalValue(), stats.willpower.getTotalValue(),
                stats.presence.getTotalValue()));

        add(txtLns,
            String.format(" `Red`H:`White`%3d  `Bronze`MP:`White`%3d `Bronze`RP:`White`%3d ",
                actor.health().getCurrent(), stats.baseMeleePool(0), stats.baseRangedPool(0)));
        add(txtLns, String.format(" Can see player? `Red`%s",
            actor.canSee(Game.current().getPlayer(), argMap)));
      }

      Inventory inventory = argMap.getItemsAt(x, y);
      add(txtLns, "");

      if (argDrawActor) {
        add(txtLns, "On ground:");
      }

      int itemSize = (this.size.height - (BOTTOM_MARGIN + TOP_MARGIN)) - txtLns.size();
      String[] itemDescriptions = inventory.getGroupedItemListAsText(itemSize - BOTTOM_MARGIN);

      for (String string : itemDescriptions) {
        add(txtLns, " " + string);
      }

      return txtLns;
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
}
