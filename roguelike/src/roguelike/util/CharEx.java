package roguelike.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import squidpony.squidcolor.SColor;
import squidpony.squidcolor.SColorFactory;

//TODO: remove Serializable implemenation
/**
 * 
 */
public class CharEx implements Serializable {

  private static final long serialVersionUID = -506720251888391231L;

  static SColor defaultForeground = SColor.WHITE;
  static SColor defaultBackground = SColor.BLACK;

  private transient SColor foreground;
  private transient SColor background;
  private char symbol;

  // TODO: better design pattern for spiraling
  /**
   * 
   * @param argSym
   */
  public CharEx(char argSym) {
    this(argSym, defaultForeground, defaultBackground);
  }

  /**
   * 
   * @param argSym
   * @param argFg
   */
  public CharEx(char argSym, SColor argFg) {
    this(argSym, argFg, defaultBackground);
  }

  /**
   * 
   * @param argSym
   * @param argFg
   * @param argBg
   */
  public CharEx(char argSym, SColor argFg, SColor argBg) {
    symbol = argSym;
    foreground = argFg;
    background = argBg;
  }

  /**
   * 
   * @param argTxt
   * @return
   */
  public static CharEx parse(String argTxt) {
    String[] elements = argTxt.split(":");

    if (elements == null || elements.length == 0) {
      throw new IllegalArgumentException("Invalid text passed to Character.parse");
    }

    return new CharEx(elements[0].charAt(0));
  }

  /**
   * 
   * @param argOut
   * @throws IOException
   */
  private void writeObject(ObjectOutputStream argOut) throws IOException {
    argOut.defaultWriteObject();
    argOut.writeInt(foreground.getRGB());
    argOut.writeInt(background.getRGB());
  }

  /**
   * 
   * @param argIn
   * @throws IOException
   * @throws ClassNotFoundException
   */
  private void readObject(ObjectInputStream argIn) throws IOException, ClassNotFoundException {
    argIn.defaultReadObject();
    foreground = SColorFactory.asSColor(argIn.readInt());
    background = SColorFactory.asSColor(argIn.readInt());
  }

  /**
   * 
   * @return
   */
  public char getSymbol() {
    return symbol;
  }

  /**
   * 
   * @return
   */
  public SColor getForegroundColor() {
    return foreground;
  }

  /**
   * 
   * @return
   */
  public SColor argBackgroundColor() {
    return background;
  }

  @Override
  public boolean equals(Object argO) {
    if (argO instanceof CharEx) {
      CharEx other = (CharEx) argO;
      return (Character.compare(other.symbol, this.symbol) == 0
          && other.background.equals(this.background) && other.foreground.equals(this.foreground));
    }
    return super.equals(argO);
  }

  // TODO: Override hashcode method

  /**
   * 
   * @return
   */
  public boolean isWhitespace() {
    return symbol == ' ';
  }
}
