package roguelike;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * 
 */
public class MessageLog implements Serializable {
  private static final long serialVersionUID = 1L;

  private LinkedList<MessageDisplayProperties> messages;
  private int maxSize = 100;

  /**
   * 
   */
  public MessageLog() {
    this.messages = new LinkedList<>();
  }

  /**
   * 
   * @param argMsgProps
   */
  public void add(MessageDisplayProperties argMsgProps) {
    messages.addFirst(argMsgProps);

    if (messages.size() > maxSize) {
      messages.removeLast();
    }
  }

  /***
   * 
   * @param string
   */
  public void add(String string) {
    if (string == null) {
      return;
    }

    add(new MessageDisplayProperties(string));
  }

  /**
   * 
   * @return
   */
  public int size() {
    return messages.size();
  }

  /**
   * 
   * @param argMaxLines
   * @return
   */
  public int size(int argMaxLines) {
    return Math.min(argMaxLines, messages.size());
  }

  /**
   * 
   * @return
   */
  public List<MessageDisplayProperties> getAll() {
    return messages;
  }

  /**
   * Returns messages in reverse order, so an index of 0 returns the most recent
   * message
   * 
   * @param argIndex
   * @return
   */
  public MessageDisplayProperties get(int argIndex) {
    return messages.get(argIndex);
  }
}
