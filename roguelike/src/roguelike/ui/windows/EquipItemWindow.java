package roguelike.ui.windows;

import roguelike.Dialog;
import roguelike.DialogResult;
import roguelike.items.Equipment.ItemSlot;
import roguelike.ui.InputCommand;

/**
 * 
 */
public class EquipItemWindow extends Dialog<ItemSlot> {

  /**
   * 
   * @param argW
   * @param argH
   */
  public EquipItemWindow(int argW, int argH) {
    super(argW, argH, false);
  }

  @Override
  protected DialogResult<ItemSlot> onProcess(InputCommand argCmd) {
    return null;
  }

  @Override
  protected void onDraw() {
    // TODO Auto-generated method stub

  }

}
