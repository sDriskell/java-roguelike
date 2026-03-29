package roguelike.actions;

import roguelike.DialogResult;
import roguelike.actors.Actor;
import roguelike.items.Equipment.ItemSlot;
import roguelike.items.Item;
import roguelike.items.ItemType;
import roguelike.ui.InputCommand;
import roguelike.ui.windows.ChooseItemCommandDialog;

/**
 * 
 */
public class ChooseItemCommandAction extends DialogInputRequiredAction<InputCommand> {

  private Item selectedItem;

  /**
   * 
   * @param argAct
   * @param argSelectedItm
   */
  protected ChooseItemCommandAction(Actor argAct, Item argSelectedItm) {
    super(argAct);
    selectedItem = argSelectedItm;
    dialog = new ChooseItemCommandDialog();
    showDialog(dialog);
  }

  @Override
  protected ActionResult onPerform() {
    DialogResult<InputCommand> choice = dialog.result();

    if (choice == null) {
      return ActionResult.incomplete();
    }

    if (choice.isCanceled()) {
      return ActionResult.alternate(new InventoryAction(actor));
    }

    ActionResult res;
    InputCommand activeItem = choice.item();

    if (activeItem != null) {

      switch (activeItem) {
        case EQUIP:
          if (selectedItem.type() == ItemType.RANGED_WEAPON) {
            ItemSlot.RANGED.equipItem(actor, selectedItem);
            res = ActionResult.success()
                .setMessage(actor.doAction("equips the %s", selectedItem.getName()));
          }
          else {
            ItemSlot.RIGHT_HAND.equipItem(actor, selectedItem);
            res = ActionResult.success()
                .setMessage(actor.doAction("equips the %s", selectedItem.getName()));
          }
          break;

        default:
          res = ActionResult.failure().setMessage("No command selected");
      }

    }
    else {
      res = ActionResult.failure().setMessage("No command selected");

    }
    return res;

  }

}
