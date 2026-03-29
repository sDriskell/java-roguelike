package roguelike.ui;

import java.awt.event.KeyEvent;
import java.util.Stack;

import javax.swing.JFrame;

import roguelike.util.Log;
import squidpony.squidgrid.gui.SGKeyListener;
import squidpony.squidgrid.gui.SGKeyListener.CaptureType;
import squidpony.squidgrid.util.DirectionIntercardinal;

/**
 * 
 */
public class InputManager {

  public static KeyMap defaultKeyBindings;

  private static SGKeyListener keyListener = new SGKeyListener(false, CaptureType.DOWN);
  private static boolean inputReceived;
  private static boolean inputEnabled = true;
  private static KeyMap activeKeyMap = new KeyMap(".");
  private static Stack<KeyMap> keyBindings = new Stack<>();

  private InputManager() {
    // Hidden constructor for utility class.
  }

  /**
   * 
   * @param argF
   */
  public static void registerWithFrame(JFrame argF) {
    argF.addKeyListener(keyListener);
  }

  /**
   * 
   * @return
   */
  public static InputCommand nextCommand() {
    return nextCommand(nextKey(), false);
  }

  /**
   * 
   * @return
   */
  public static InputCommand nextCommandPreserveKeyData() {
    return nextCommand(nextKey(), true);
  }

  /**
   * 
   * @return
   */
  public static DirectionIntercardinal nextDirection() {
    InputCommand cmd = nextCommandPreserveKeyData();
    return cmd == null ? null : cmd.toDirection();
  }

  /**
   * 
   * @return
   */
  public static boolean inputReceived() {
    boolean input = inputReceived;
    inputReceived = !inputReceived;
    return input;
  }

  /**
   * 
   * @param argIsEnabled
   */
  public static void setInputEnabled(boolean argIsEnabled) {
    inputEnabled = argIsEnabled;
  }

  /**
   * 
   * @param argKeyMap
   * @return
   */
  public static KeyMap setActiveKeybindings(KeyMap argKeyMap) {
    KeyMap old = activeKeyMap;

    if (!argKeyMap.getName().equals(old.getName())) {
      Log.debug("switching keyMap to " + argKeyMap.getName());
      activeKeyMap = argKeyMap;

      if (!old.getName().equals("."))
        keyBindings.push(old);
    }
    return old;
  }

  /**
   * Sets the active key map to the previous one in the stack.
   * 
   * @return The new active key map.
   */
  public static KeyMap previousKeyMap() {
    if (keyBindings.isEmpty()) {
      return null;
    }

    activeKeyMap = keyBindings.pop();
    return activeKeyMap;
  }

  /**
   * 
   * @param argKey
   * @param argGetKeyData
   * @return
   */
  private static InputCommand nextCommand(KeyEvent argKey, boolean argGetKeyData) {
    InputCommand cmd = activeKeyMap.getCommand(argKey);
    if (cmd == null && argKey != null && argGetKeyData) {
      return InputCommand.fromKey(argKey.getKeyCode(), argKey.getKeyChar());
    }

    return cmd;
  }

  /**
   * 
   * @return
   */
  private static KeyEvent nextKey() {
    if (!inputEnabled) {
      return null;
    }

    KeyEvent key = keyListener.next();

    if (key != null) {
      DisplayManager.instance().setDirty();
    }

    return key;
  }

  /*
   * 
   * potential key bindings
   * 
   * COMBAT
   * 
   * ++++++++
   * 
   * s: slash
   * 
   * t: thrust
   * 
   * b: blunt
   * 
   * a / <move into opponent>: use attack type with lowest TN of equipped weapon
   * 
   * r: ranged attack
   * 
   * S: change stance
   * 
   * M: change active maneuvers (attack/defense)
   * 
   * m: use active attack maneuver
   * 
   * 
   * 
   * OTHER
   * 
   * +++++++++
   * 
   * c: character info
   * 
   * i: inventory
   * 
   * T: talk
   */
}
