package roguelike.maps;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import roguelike.actors.Actor;
import roguelike.items.Inventory;
import roguelike.items.Item;
import squidpony.squidcolor.SColor;
import squidpony.squidcolor.SColorFactory;

//TODO: Remove serializable implementation

/**
 * 
 */
public class Tile implements Serializable {

  private static final long serialVersionUID = 1L;

  protected boolean visible;
  protected boolean wall;
  protected boolean explored;
  protected float lighting;
  protected boolean isPassable;
  protected char symbol;
  protected int speedModifier;

  protected Inventory items;

  protected SColor lightValue;
  protected SColor color;
  protected SColor background;

  private Actor actor;

  /**
   * 
   */
  Tile() {
    visible = false;
    background = SColor.BLACK;
    lightValue = SColor.BLACK;
    items = new Inventory();
  }

  /**
   * 
   * @param out
   * @throws IOException
   */
  private void writeObject(ObjectOutputStream out) throws IOException {
    out.defaultWriteObject();
  }

  /**
   * 
   * @param in
   * @throws IOException
   * @throws ClassNotFoundException
   */
  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    in.defaultReadObject();
  }

  /**
   * Indicates whether this tile has been explored by the player
   * 
   * @return True if the player has visited this tile
   */
  public boolean isExplored() {
    return explored;
  }

  /**
   * Indicates this tile's visibility to the player
   * 
   * @return True if the player can see this tile
   */
  public boolean isVisible() {
    return this.visible;
  }

  public void setVisible(boolean argIsVis) {
    visible = argIsVis;

    if (argIsVis) {
      explored = true;
    }
  }

  /**
   * 
   * @param argSpeedMod
   * @return
   */
  public Tile setSpeedModifier(int argSpeedMod) {
    speedModifier = argSpeedMod;
    return this;
  }

  /**
   * Gets the color that this tile should be drawn in after lighting and
   * visibility is taken into account
   * 
   * @return
   */
  public SColor getLightedColorValue() {
    return lightValue;
  }

  /**
   * Sets the actual color this tile will be drawn in after taking lighting and
   * visibility into account
   * 
   * @param argLtVal
   */
  public void setLightedColorValue(SColor argLtVal) {
    lightValue = argLtVal;
  }

  public float getLighting() {
    return this.lighting;
  }

  public Tile setLighting(float argLight) {
    lighting = argLight;
    return this;
  }

  /**
   * 
   * @return
   */
  public char getSymbol() {
    if (actor != null && visible) {
      return actor.symbol();
    }

    if (items.any()) {
      return getTopItem().getSymbol();
    }

    return this.symbol;
  }

  /**
   * 
   * @return
   */
  public SColor getColor() {
    if (!visible) {
      if (explored) {
        return SColorFactory.dimmer(SColor.DARK_CERULEAN);
      }

      return SColor.BLACK;
    }

    if (actor != null) {
      return actor.color();
    }

    if (items.any()) {
      return getTopItem().getColor();
    }

    return this.color;
  }

  /**
   * 
   * @return
   */
  public SColor getBackground() {
    if (!visible) {
      if (explored) {
        return SColorFactory.dimmest(this.background);
      }

      return SColor.BLACK;
    }

    return this.background;
  }

  /**
   * 
   * @param argBack
   * @return
   */
  public Tile setBackground(SColor argBack) {
    background = argBack;
    return this;
  }

  /**
   * 
   * @return
   */
  public Actor getActor() {
    return this.actor;
  }

  /**
   * 
   * @param argAct
   */
  public void setActor(Actor argAct) {
    actor = argAct;
  }

  /**
   * 
   * @return
   */
  public boolean canPass() {
    return isPassable;
  }

  /**
   * 
   * @return
   */
  public boolean isWall() {
    return wall;
  }

  /**
   * 
   * @param argSym
   * @param argIsPass
   * @param argCol
   * @return
   */
  Tile setValues(char argSym, boolean argIsPass, SColor argCol) {
    symbol = argSym;
    isPassable = argIsPass;
    color = argCol;
    return this;
  }

  /**
   * 
   * @param argSym
   * @param argIsPass
   * @param argCol
   * @param argIsWall
   * @return
   */
  Tile setValues(char argSym, boolean argIsPass, SColor argCol, boolean argIsWall) {
    setValues(argSym, argIsPass, argCol);
    wall = argIsWall;

    if (this.wall) {
      lighting = 1f;
    }

    return this;
  }

  /**
   * 
   * @param argNewTile
   * @return
   */
  boolean moveActorTo(Tile argNewTile) {
    if (this.actor != null) {
      argNewTile.actor = actor;
      actor = null;
      return true;
    }
    else {
      System.out.println("Tried to move a null actor");
      return false;
    }
  }

  /**
   * 
   * @return
   */
  Inventory getItems() {
    return items;
  }

  /**
   * 
   * @return
   */
  private Item getTopItem() {
    return items.any() ? items.getItem(items.getCount() - 1) : null;

  }
}
