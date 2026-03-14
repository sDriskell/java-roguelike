package roguelike.ui;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 */
public class KeyMap {

  private Map<Integer, InputCommand> keyBindings;
  private Map<Integer, InputCommand> shiftKeyBindings;

  private String name;

  /**
   * 
   * @param argName
   */
  public KeyMap(String argName) {
    name = argName;
    keyBindings = new HashMap<>();
    shiftKeyBindings = new HashMap<>();
  }

  /**
   * 
   * @return
   */
  public String getName() {
    return name;
  }

  /**
   * 
   * @param argKey
   * @param argCmd
   * @return
   */
  public KeyMap bindKey(Integer argKey, InputCommand argCmd) {
    return bindKey(argKey, false, argCmd);
  }

  /**
   * 
   * @param argKey
   * @param isShiftKey
   * @param argCmd
   * @return
   */
  public KeyMap bindKey(Integer argKey, boolean isShiftKey, InputCommand argCmd) {
    if (isShiftKey) {
      shiftKeyBindings.put(argKey, argCmd);
    }
    else {
      keyBindings.put(argKey, argCmd);
    }

    return this;
  }

  /**
   * 
   * @param argKey
   * @return
   */
  public InputCommand getCommand(KeyEvent argKey) {
    if (argKey == null) {
      return null;
    }

    if (argKey.isShiftDown()) {
      return shiftKeyBindings.getOrDefault(argKey.getKeyCode(), null);
    }
    else {
      return keyBindings.getOrDefault(argKey.getKeyCode(), null);
    }
  }
}
