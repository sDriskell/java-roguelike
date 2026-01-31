package roguelike;

import java.awt.Point;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import squidpony.squidutility.Pair;

public class TurnResult implements Serializable {
  private static final long serialVersionUID = 1L;

  boolean isRunning;
  boolean hasPlayerActed;
  ArrayList<MessageDisplayProperties> messages;
  transient ArrayList<TurnEvent> events;
  transient Pair<Point, Boolean> currentLook = new Pair<>(null, false);

  /**
   * 
   * @param argIsRunning
   */
  private TurnResult(boolean argIsRunning) {
    isRunning = argIsRunning;
    messages = new ArrayList<>();
    events = new ArrayList<>();
  }

  /**
   * 
   * @param argIn
   * @throws ClassNotFoundException
   * @throws IOException
   */
  private void readObject(ObjectInputStream argIn) throws ClassNotFoundException, IOException {
    argIn.defaultReadObject();

    currentLook = new Pair<>(null, false);
    events = new ArrayList<>();
  }

  /**
   * 
   * @param argResult
   * @param argIsRunning
   * @return
   */
  public static TurnResult reset(TurnResult argResult, boolean argIsRunning) {
    if (argResult == null) {
      argResult = new TurnResult(argIsRunning);
    }

    argResult.isRunning = argIsRunning;
    argResult.hasPlayerActed = false;
    argResult.messages.clear();
    argResult.events.clear();
    argResult.currentLook.setFirst(null);
    argResult.currentLook.setSecond(true);

    return argResult;
  }

  /**
   * 
   * @return
   */
  public boolean playerActedThisTurn() {
    return hasPlayerActed;
  }

  /**
   * 
   */
  public void playerActed() {
    hasPlayerActed = true;
  }

  /**
   * 
   * @param argEvent
   * @return
   */
  public TurnResult addEvent(TurnEvent argEvent) {
    if (argEvent != null) {
      events.add(argEvent);
    }
    return this;
  }

  /**
   * 
   * @return
   */
  public boolean isRunning() {
    return this.isRunning;
  }

  /**
   * 
   * @return
   */
  public List<TurnEvent> getEvents() {
    return events;
  }

  /**
   * 
   * @return
   */
  public Pair<Point, Boolean> getCurrentLook() {
    return currentLook;
  }

  /**
   * 
   * @param argPoint
   * @param argDrawActor
   */
  public void setCurrentLook(Point argPoint, boolean argDrawActor) {
    this.currentLook.setFirst(argPoint);
    this.currentLook.setSecond(argDrawActor);
  }
}
