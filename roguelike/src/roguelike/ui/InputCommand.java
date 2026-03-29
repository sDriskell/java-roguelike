package roguelike.ui;

import squidpony.squidgrid.util.DirectionIntercardinal;

/**
 * 
 */
public enum InputCommand {

  NEW,
  LOAD,
  QUIT,
  SAVE_QUIT,

  CANCEL,
  CONFIRM,

  LEFT,
  UP,
  RIGHT,
  DOWN,
  UP_LEFT,
  UP_RIGHT,
  DOWN_LEFT,
  DOWN_RIGHT,

  STAIRS_DOWN,
  STAIRS_UP,

  NEXT_PAGE,
  PREVIOUS_PAGE,

  OPEN_DOOR,
  CLOSE_DOOR,

  INVENTORY,
  LOOK,
  SHOW_MESSAGES,

  ATTACK,
  RANGED_ATTACK,
  PREVIOUS_TARGET,
  NEXT_TARGET,

  SEARCH,
  REST,

  TALK,
  PICK_UP,

  EQUIP,
  USE,
  DROP,

  FROM_KEYDATA;

  private int keyData;
  private char keyChar;

  /**
   * 
   */
  InputCommand() {
    // TODO: number's purpose?
    this(-999, ' ');
  }

  /**
   * 
   * @param argKey
   * @param argChar
   */
  InputCommand(int argKey, char argChar) {
    keyData = argKey;
    keyChar = argChar;
  }

  /**
   * 
   * @param argKeyData
   * @param argChar
   * @return
   */
  public static InputCommand fromKey(int argKeyData, char argChar) {
    return FROM_KEYDATA.setKeyData(argKeyData, argChar);
  }

  /**
   * 
   * @return
   */
  public int getKeyData() {
    return keyData;
  }

  /**
   * 
   * @return
   */
  public char getKeyChar() {
    return keyChar;
  }

  /**
   * 
   * @return
   */
  public DirectionIntercardinal toDirection() {
    switch (this) {
      case UP:
        return DirectionIntercardinal.UP;
      case LEFT:
        return DirectionIntercardinal.LEFT;
      case RIGHT:
        return DirectionIntercardinal.RIGHT;
      case DOWN:
        return DirectionIntercardinal.DOWN;
      case UP_LEFT:
        return DirectionIntercardinal.UP_LEFT;
      case UP_RIGHT:
        return DirectionIntercardinal.UP_RIGHT;
      case DOWN_LEFT:
        return DirectionIntercardinal.DOWN_LEFT;
      case DOWN_RIGHT:
        return DirectionIntercardinal.DOWN_RIGHT;
      default:
        return DirectionIntercardinal.NONE;
    }
  }

  /**
   * 
   * @param argKeyData
   * @param argChar
   * @return
   */
  private InputCommand setKeyData(int argKeyData, char argChar) {
    keyData = argKeyData;
    keyChar = argChar;
    return this;
  }
}
