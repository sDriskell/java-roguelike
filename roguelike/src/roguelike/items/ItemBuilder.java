package roguelike.items;

/**
 * 
 */
public abstract class ItemBuilder {

  protected Item item;

  /**
   * 
   * @param argItm
   */
  protected ItemBuilder(Item argItm) {
    if (argItm == null) {
      throw new IllegalArgumentException("item cannot be null");
    }

    item = argItm;
  }

  /**
   * 
   * @param argIsDrop
   * @return
   */
  public ItemBuilder withDroppable(boolean argIsDrop) {
    item.droppable = argIsDrop;
    return this;
  }

  /**
   * 
   * @param argWgt
   * @return
   */
  public ItemBuilder withWeight(int argWgt) {
    item.weight = argWgt;
    return this;
  }

}
