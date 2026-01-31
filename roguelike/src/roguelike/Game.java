package roguelike;

import java.awt.Point;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.Queue;

import roguelike.actions.Action;
import roguelike.actions.ActionResult;
import roguelike.actors.Actor;
import roguelike.actors.Energy;
import roguelike.actors.Player;
import roguelike.items.Inventory;
import roguelike.maps.MapArea;
import roguelike.ui.DisplayManager;
import roguelike.util.Coordinate;
import roguelike.util.Log;
import squidpony.squidcolor.SColor;
import squidpony.squidmath.RNG;

/**
 * ...
 * 
 * @author John - jrdrg
 * @author Shane - sDriskell
 * 
 */
public class Game implements Serializable {
  private static final long serialVersionUID = 1L;

  public static final int MAP_WIDTH = 83;
  public static final int MAP_HEIGHT = 43;

  private static Game currentGame;

  private RNG rng;
  private boolean isRunning;
  private boolean isPlayerDead;
  private Player player;
  private MapArea currentMapArea;
  private Queue<Action> queuedActions;
  private TurnResult currentTurnResult;

  Cursor activeCursor;
  MessageLog messages;

  /**
   * This should only be called by GameLoader
   * 
   * @param gameLoader
   */
  Game() {
    queuedActions = new LinkedList<>();
    rng = GameLoader.getRandom();
    currentGame = this;
    Log.debug("Created Game");

    messages = new MessageLog();
    player = GameLoader.createPlayer();
  }

  /**
   * 
   * @param in
   * @throws ClassNotFoundException
   * @throws IOException
   */
  private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
    in.defaultReadObject();
    currentGame = this;
  }

  /**
   * Always returns the current game
   * 
   * @return
   */
  public static Game current() {
    return currentGame;
  }

  /**
   * 
   * @return
   */
  public MessageLog messages() {
    return messages;
  }

  /**
   * 
   * @return
   */
  public RNG random() {
    return rng;
  }

  /**
   * 
   * @return
   */
  public boolean isRunning() {
    return isRunning;
  }

  /**
   * 
   * @return
   */
  public boolean isPlayerDead() {
    if (isPlayerDead) {
      isPlayerDead = false;
      return true;
    }
    return false;
  }

  /**
   * 
   * @return
   */
  public Player getPlayer() {
    return player;
  }

  /**
   * 
   * @return
   */
  public Coordinate getCenterScreenPosition() {
    return player.position;
  }

  /**
   * 
   * @return
   */
  public MapArea getCurrentMapArea() {
    return currentMapArea;
  }

  /**
   * 
   * @param argMapArea
   */
  public void setCurrentMapArea(MapArea argMapArea) {
    if (argMapArea == null) {
      return;
    }

    currentMapArea = argMapArea;
  }

  /**
   * 
   */
  public void initialize() {
    Log.debug("Initializing Game");
    isRunning = true;
  }

  /**
   * 
   * @return
   */
  public TurnResult processTurn() {
    if (isRunning) {
      currentTurnResult = onProcessing();
      return currentTurnResult;
    }
    return TurnResult.reset(currentTurnResult, false);
  }

  /**
   * 
   */
  public void stopGame() {
    isRunning = false;
  }

  /**
   * 
   */
  public void reset() {
    isPlayerDead = true;
  }

  /**
   * Displays a message in the bottom pane of the UI
   * 
   * @param msg
   */
  public void displayMessage(String msg) {
    messages.add(msg);
  }

  /**
   * 
   * @param argMsg
   * @param argColor
   */
  public void displayMessage(String argMsg, SColor argColor) {
    messages.add(new MessageDisplayProperties(argMsg, argColor));
  }

  /**
   * 
   * @param argEvent
   */
  public void addEvent(TurnEvent argEvent) {
    currentTurnResult.addEvent(argEvent);
  }

  /**
   * 
   * @param argPoint
   */
  public void setCurrentlyLookingAt(Point argPoint) {
    setCurrentlyLookingAt(argPoint, true);
  }

  /**
   * 
   * @param argPoint
   * @param argShouldDrawActor
   */
  public void setCurrentlyLookingAt(Point argPoint, boolean argShouldDrawActor) {
    currentTurnResult.setCurrentLook(argPoint, argShouldDrawActor);
  }

  /**
   * Processes one turn
   * 
   * @return
   */
  private TurnResult onProcessing() {
    TurnResult turnResult = null;

    turnResult = TurnResult.reset(currentTurnResult, isRunning);
    currentTurnResult = turnResult;

    showItemsOnPlayerSquare();

    while (true) {

      while (!queuedActions.isEmpty()) {
        if (executeQueuedActions(turnResult) != null) {
          return turnResult;
        }
      }

      while (queuedActions.isEmpty()) {
        if (getCurrentActions(turnResult) != null) {
          return turnResult;
        }
      }

      if (isPlayerDead)
        return turnResult;

    }
  }

  /**
   * Queues an action for the current actor
   * 
   * @param argTurnResult
   * @return
   */
  private TurnResult getCurrentActions(TurnResult argTurnResult) {
    Actor actor = currentMapArea.getCurrentActor();

    while (!actor.isAlive()) {
      currentMapArea.nextActor("Attempting to act on dead actor: " + actor.getName());
      actor = currentMapArea.getCurrentActor();
    }

    int speed = actor.effectiveSpeed(currentMapArea);
    Energy energy = actor.energy();

    if (energy.canAct() || energy.increase(speed)) {
      Action action = actor.getNextAction();
      if (action != null) {
        queuedActions.add(action);
      }
      else {
        return argTurnResult;
      }
    }
    else { // advance to next actor
      currentMapArea.nextActor("getCurrentActions, !canAct, queueSize=" + queuedActions.size());

      if (Player.isPlayer(actor)) {
        // TODO: process things that happen every turn after player queues actions
        Game.currentGame.currentMapArea.spawnMonsters();
        Log.verboseDebug("Game: Queue length: " + queuedActions.size());
      }
    }

    return null;
  }

  /**
   * Executes the current action in the queue
   * 
   * @param argTurnResult
   * @return
   */
  private TurnResult executeQueuedActions(TurnResult argTurnResult) {
    Action currentAction = queuedActions.remove();

    // don't perform the action if the actor is dead
    if (!currentAction.getActor().isAlive()) {
      currentMapArea.nextActor("executeQueuedActions, currentAction actor !isAlive: "
          + currentAction.getActor().getName());
      return argTurnResult;
    }

    ActionResult result = currentAction.perform();
    messages.add(result.getMessage());

    /*
     * if the result is completed we can proceed, else put it back on the queue
     */
    if (result.isCompleted()) {

      while (result.getAlternateAction() != null) {
        Action alternate = result.getAlternateAction();
        result = alternate.perform();
        messages.add(result.getMessage());

        if (!result.isCompleted())
          queuedActions.add(alternate);
      }

      Actor currentActor = currentAction.getActor();
      if (currentActor != null && !currentActor.energy().canAct()) {

        if (result.isSuccess()) {
          currentActor.finishTurn();
          DisplayManager.instance().setDirty(); // make sure we show the result of the action
        }
        else {
          currentMapArea.nextActor("executeQueuedActions, !currentActor.canAct && !success");
          return argTurnResult;
        }

      }
      else {

        Log.warning(String.format("Game: Actor=%s Alive=%s Action=%s", currentActor.getName(),
            currentActor.isAlive(), currentAction));
        Log.warning(
            "Game: Remaining energy: " + currentActor.energy().getCurrent() + " Result=" + result);
        Log.warning("Game: M=" + result.getMessage() + ", S=" + result.isSuccess() + ", C="
            + result.isCompleted());

        /*
         * bug fix for infinite loop with enemy pathfinding where they can't move to a
         * square they want to and fail the walk action
         */
        if (!result.isSuccess())
          currentMapArea.nextActor("executeQueueActions, can act but not success");

        return argTurnResult;
      }

    }
    else { // incomplete action

      queuedActions.add(currentAction);
    }

    /* return when player's actions are performed so we can redraw */
    if (Player.isPlayer(currentAction.getActor())) {
      argTurnResult.playerActed();
      return argTurnResult;
    }

    return null;
  }

  /**
   * 
   */
  private void showItemsOnPlayerSquare() {
    if (currentTurnResult.getCurrentLook().getFirst() != null)
      return;

    Coordinate playerPos = player.getPosition();
    Inventory inventory = currentMapArea.getItemsAt(playerPos.x, playerPos.y);
    if (inventory != null && inventory.any()) {
      setCurrentlyLookingAt(playerPos, false);
    }
  }
}
