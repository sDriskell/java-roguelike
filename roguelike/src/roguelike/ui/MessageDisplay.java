package roguelike.ui;

import roguelike.MessageDisplayProperties;
import roguelike.MessageLog;
import roguelike.ui.windows.TerminalBase;
import roguelike.util.Log;
import roguelike.util.StringEx;
import squidpony.squidcolor.SColor;
import squidpony.squidcolor.SColorFactory;

/**
 * 
 */
public class MessageDisplay {

  private TerminalBase terminal;
  private int numLines;
  private MessageLog messages;

  /**
   * 
   * @param argMsgs
   * @param argTrm
   * @param argNumLn
   */
  public MessageDisplay(MessageLog argMsgs, TerminalBase argTrm, int argNumLn) {
    terminal = argTrm;
    numLines = argNumLn;
    messages = argMsgs;

    Log.debug("MessageDisplay w=" + argTrm.size().width + ", h=" + argTrm.size().height);
  }

  /**
   * 
   * @param argMsg
   */
  public void display(String argMsg) {
    display(new MessageDisplayProperties(argMsg));
  }

  /**
   * 
   * @param argMsg
   * @param argColor
   */
  public void display(String argMsg, SColor argColor) {
    display(new MessageDisplayProperties(argMsg, argColor));
  }

  /**
   * 
   * @param argMsg
   */
  public void display(MessageDisplayProperties argMsg) {

    messages.add(argMsg);
  }

  /**
   * 
   */
  public void draw() {
    terminal.withColor(SColor.RED).fill(0, 0, terminal.size().width, terminal.size().height, ' ');
    int msgCount = 0;
    int maxSize = messages.size(numLines);

    for (int x = 0; x < maxSize; x++) {
      MessageDisplayProperties props = messages.get(x);
      StringEx[] lines = props.getText().wordWrap(terminal.size().width - 6);
      TerminalBase colorTerm = terminal.withColor(
          SColorFactory.blend(props.getColor(), SColor.BLACK_CHESTNUT_OAK, (x / (float) numLines)));

      String prefix = "> ";
      int startIdx = maxSize - msgCount - lines.length + 1;

      for (int i = 0; (i < lines.length) && (msgCount < maxSize); i++) {
        if (i > 0) {
          prefix = "";
        }

        colorTerm.write(0, startIdx + i, prefix + lines[i].toString());
        msgCount++;
      }
    }
  }
}
