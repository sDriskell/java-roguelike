package roguelike.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import squidpony.squidcolor.SColor;
import squidpony.squidcolor.SColorFactory;

//TODO: remove serializable property of class
/**
 * String composed of Characters to provide color info
 * 
 * @author john
 */
public class StringEx extends ArrayList<CharEx> {

  /**
   * 
   */
  private class CharacterParseResult {
    public List<Character> parsedColor;
    public boolean readColor;
    public boolean isTextChar;
  }

  private static final long serialVersionUID = -276852334648421745L;

  private String text;

  public StringEx() {
  }

  /**
   * 
   * @param argChars
   */
  public StringEx(List<CharEx> argChars) {
    super(argChars);

    char[] strChars = new char[argChars.size()];

    for (int x = 0; x < argChars.size(); x++) {
      strChars[x] = argChars.get(x).getSymbol();
    }

    text = new String(strChars);
  }

  /**
   * 
   * @param argTxt
   */
  public StringEx(String argTxt) {
    this(argTxt, CharEx.defaultForeground, CharEx.defaultBackground);
  }

  /**
   * 
   * @param argxt
   * @param argFg
   * @param argBg
   */
  public StringEx(String argxt, SColor argFg, SColor argBg) {
    text = argxt;
    SColor fg = argFg;
    SColor bg = argBg;
    CharacterParseResult res = new CharacterParseResult();

    for (int x = 0; x < argxt.length(); x++) {
      char c = argxt.charAt(x);
      res = parseColor(c, res.parsedColor);

      if (res.readColor) {
        fg = toColor(res.parsedColor);
        res.parsedColor = null;
      }
      else if (res.isTextChar) {
        this.add(new CharEx(c, fg, bg));
      }
    }
  }

  @Override
  public String toString() {
    return text;
  }

  /**
   * 
   * @param argLnWidth
   * @return
   */
  public StringEx[] wordWrap(int argLnWidth) {
    List<StringEx> lines = new ArrayList<>();
    StringEx line;

    int lastWrapPoint = 0;
    int prevLastWrapPoint = 0;
    int thisLineStart = 0;

    for (int i = 0; i < size(); i++) {
      CharEx c = this.get(i);

      if (c.isWhitespace()) {
        if (lastWrapPoint > 0) {
          prevLastWrapPoint = lastWrapPoint;
        }

        lastWrapPoint = i;
      }

      // wrap if we got too long
      if (i - thisLineStart >= argLnWidth) {
        if (lastWrapPoint != 0) {
          // have a recent point to wrap at, so word wrap
          if (lastWrapPoint - thisLineStart > argLnWidth && lastWrapPoint > 0) {
            line = substring(thisLineStart, prevLastWrapPoint - thisLineStart);
            thisLineStart = prevLastWrapPoint;
          }
          else {

            line = substring(thisLineStart, lastWrapPoint - thisLineStart);
            thisLineStart = lastWrapPoint;
          }

          lastWrapPoint = 0;
        }
        else {
          // no convenient point to word wrap, so character wrap
          line = substring(thisLineStart, i - thisLineStart);
          thisLineStart = i;
        }

        line = line.trim();
        lines.add(line);
      }
    }

    // add the last bit
    line = substring(thisLineStart);
    line = line.trim();
    lines.add(line);

    return lines.toArray(new StringEx[0]);
  }

  public StringEx substring(int argStartIdx, int argLen) {
    StringEx substring = new StringEx();
    substring
        .addAll(this
            .stream().skip(argStartIdx).limit((long) argStartIdx + argLen)
            .collect(Collectors.toList()));

    return substring;
  }

  /**
   * 
   * @param argStartIdx
   * @return
   */
  public StringEx substring(int argStartIdx) {
    return substring(argStartIdx, size() - argStartIdx);
  }

  /**
   * 
   * @return
   */
  public StringEx trim() {
    StringEx trimmed = new StringEx(this);

    // trim from the front
    while ((trimmed.size() > 0) && (trimmed.get(0).isWhitespace())) {
      trimmed.remove(0);
    }

    // trim from the end
    while ((trimmed.size() > 0) && (trimmed.get(trimmed.size() - 1).isWhitespace())) {
      trimmed.remove(trimmed.size() - 1);
    }

    return trimmed;
  }

  /**
   * 
   * @param c
   * @param argReadChars
   * @return
   */
  private CharacterParseResult parseColor(char c, List<Character> argReadChars) {
    CharacterParseResult res = new CharacterParseResult();
    if (c == '`') {
      if (argReadChars == null) {
        res.parsedColor = new ArrayList<>();
      }
      else {
        // we're done reading the string
        res.parsedColor = argReadChars;
        res.readColor = true;
      }
    }
    else if (argReadChars != null) {
      res.parsedColor = argReadChars;
      res.parsedColor.add(c);
    }
    else {
      res.isTextChar = true;
    }

    return res;
  }

  /**
   * 
   * @param argCol
   * @return
   */
  private SColor toColor(List<Character> argCol) {
    char[] chars = new char[argCol.size()];

    for (int x = 0; x < chars.length; x++) {
      chars[x] = argCol.get(x);
    }

    return SColorFactory.colorForName(new String(chars));
  }
}
