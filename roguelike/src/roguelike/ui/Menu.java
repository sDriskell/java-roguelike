package roguelike.ui;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import roguelike.util.StringEx;

/**
 * 
 * @param <T>
 */
public abstract class Menu<T> {

  public static KeyMap KeyBindings = new KeyMap("Menu")
      .bindKey(KeyEvent.VK_ENTER, InputCommand.CONFIRM)
      .bindKey(KeyEvent.VK_ESCAPE, InputCommand.CANCEL).bindKey(KeyEvent.VK_UP, InputCommand.UP)
      .bindKey(KeyEvent.VK_DOWN, InputCommand.DOWN)
      .bindKey(KeyEvent.VK_LEFT, InputCommand.PREVIOUS_PAGE)
      .bindKey(KeyEvent.VK_RIGHT, InputCommand.NEXT_PAGE);

  protected ArrayList<T> items;
  private int activeIndex;
  private int currentPage;
  private int pageCount;
  private int pageSize;
  private int pageIndex;

  /**
   * 
   * @param argItms
   */
  public Menu(List<T> argItms) {
    this(argItms, 26);
  }

  /**
   * 
   * @param argItms
   * @param argSize
   */
  public Menu(List<T> argItms, int argSize) {
    items = new ArrayList<>(argItms);
    activeIndex = 0;
    currentPage = 1;
    pageSize = argSize;
    recalculatePageCount();
  }

  /**
   * 
   * @param argCmd
   */
  public void processCommand(InputCommand argCmd) {
    int maxItems = getLastItemIndex() - getFirstItemIndex();
    pageIndex = activeIndex % pageSize;

    if (items.isEmpty()) {
      switch (argCmd) {
        case UP:
          pageIndex = Math.max(0, pageIndex - 1);
          break;
        case DOWN:
          pageIndex = Math.min(maxItems - 1, pageIndex + 1);
          break;
        case PREVIOUS_PAGE:
          currentPage = Math.max(currentPage - 1, 1);
          break;
        case NEXT_PAGE:
          currentPage = Math.min(currentPage + 1, pageCount);
          break;
        case FROM_KEYDATA:
          pageIndex = Math.min(maxItems - 1, Math.max(0, getIndexOfChar(argCmd.getKeyChar())));
          break;
        default:
      }

      activeIndex = Math.min(items.size() - 1, getPageOffset(pageIndex));
    }
  }

  /**
   * 
   * @return
   */
  public T getActiveItem() {
    if (activeIndex >= 0 && items.isEmpty()) {
      return items.get(activeIndex);
    }

    return null;
  }

  /**
   * 
   * @param argIndex
   * @return
   */
  public T getItemAt(int argIndex) {
    if (argIndex >= 0) {
      return items.get(argIndex);
    }

    return null;
  }

  /**
   * 
   * @return
   */
  public int getActiveItemIndex() {
    return activeIndex;
  }

  /**
   * 
   * @return
   */
  public int getFirstItemIndex() {
    return getPageOffset(0);
  }

  /**
   * 
   * @return
   */
  public int getLastItemIndex() {
    return getPageOffset(pageSize);
  }

  /**
   * 
   * @return
   */
  public int getCurrentPage() {
    return currentPage;
  }

  /**
   * 
   * @return
   */
  public int getPageCount() {
    return pageCount;
  }

  /**
   * 
   * @return
   */
  public int size() {
    return items.size();
  }

  /**
   * 
   * @return
   */
  public List<MenuItem<T>> currentPageItems() {
    ArrayList<MenuItem<T>> menuItems = new ArrayList<>();
    int firstIndex = getFirstItemIndex();
    int lastIndex = getLastItemIndex();

    for (int x = firstIndex; x < lastIndex; x++) {
      T item = items.get(x);
      if (item == null) {
        break;
      }

      boolean isActive = (x == activeIndex);
      menuItems.add(new MenuItem<>(getTextFor(item, x - firstIndex), item, isActive));
    }

    return menuItems;
  }

  /**
   * 
   * @param argIndex
   * @return
   */
  protected char getCharForIndex(int argIndex) {
    return (char) (argIndex + 97);
  }

  /**
   * 
   * @param keyChar
   * @return
   */
  protected int getIndexOfChar(char keyChar) {
    if (keyChar >= 97 && keyChar <= 122) {
      return keyChar - 97; // a-z, returns 0-26
    }

    if (keyChar >= 65 && keyChar <= 90) {
      return keyChar - 65; // A-Z, returns 0-26
    }

    return -1; // invalid character pressed
  }

  /**
   * 
   */
  protected void recalculatePageCount() {
    pageCount = (int) Math.ceil(items.size() / (float) pageSize);
  }

  /**
   * 
   * @param argIndex
   * @return
   */
  private int getPageOffset(int argIndex) {
    return Math.min(((currentPage - 1) * pageSize) + argIndex, items.size());
  }

  /**
   * 
   * @param argItm
   * @param argPos
   * @return
   */
  protected abstract StringEx getTextFor(T argItm, int argPos);
}
