package roguelike.actors;

import java.awt.Point;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.UUID;

import roguelike.Game;
import roguelike.actions.Action;
import roguelike.actions.combat.CombatHandler;
import roguelike.actors.behaviors.Behavior;
import roguelike.actors.conditions.Condition;
import roguelike.items.Equipment;
import roguelike.items.Inventory;
import roguelike.maps.MapArea;
import roguelike.maps.Tile;
import roguelike.util.ActorUtils;
import roguelike.util.Coordinate;
import roguelike.util.Log;
import squidpony.squidcolor.SColor;
import squidpony.squidcolor.SColorFactory;

//TODO: Remove serializable implementation and create an Actor interface.
/**
 * 
 */
public abstract class Actor implements Serializable {

  private static final long serialVersionUID = 1L;

  protected transient Game game = Game.current();
  protected UUID actorId = UUID.randomUUID();

  protected char symbol;
  protected int visionRadius;
  protected boolean attackedThisRound;

  protected final Energy energy;
  protected final Statistics statistics;
  protected final CombatHandler combat;
  protected final Health health;
  protected final Equipment equipment;
  protected final Inventory inventory;
  protected ArrayList<Condition> conditions;

  protected Behavior behavior;
  protected Stack<AttackAttempt> attacked;
  protected Stack<AttackAttempt> attackedBy;
  protected transient SColor color;

  public final Coordinate position;

  /**
   * 
   * @param argSym
   * @param argCol
   */
  protected Actor(char argSym, SColor argCol) {
    if (argCol == null) {
      throw new IllegalArgumentException("color cannot be null: " + argSym);
    }

    symbol = argSym;
    color = argCol;
    position = new Coordinate();
    energy = new Energy();
    statistics = new Statistics();
    combat = new CombatHandler(this);
    health = new Health(20);
    inventory = new Inventory();
    equipment = new Equipment();
    attacked = new Stack<>();
    attackedBy = new Stack<>();
    conditions = new ArrayList<>();
    visionRadius = 15;
  }

  /**
   * 
   * @param out
   * @throws IOException
   */
  private void writeObject(ObjectOutputStream out) throws IOException {
    out.defaultWriteObject();
    out.writeInt(color.getRGB());
    Log.debug("writing actor: " + actorId);
  }

  /**
   * 
   * @param in
   * @throws IOException
   * @throws ClassNotFoundException
   */
  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    in.defaultReadObject();

    game = Game.current();
    color = SColorFactory.asSColor(in.readInt());

    Log.debug("reading actor: " + actorId);
    Log.debug("game=" + game.toString());
  }

  /**
   * 
   * @return
   */
  public char symbol() {
    return symbol;
  }

  /**
   * 
   * @return
   */
  public SColor color() {
    return color;
  }

  /**
   * 
   * @return
   */
  public List<Condition> conditions() {
    return conditions;
  }

  /**
   * 
   * @param argCon
   */
  public void addCondition(Condition argCon) {
    conditions.add(argCon);
    argCon.onConditionAdded(this);
  }

  /**
   * 
   * @return
   */
  public Behavior behavior() {
    return behavior;
  }

  /**
   * 
   * @return
   */
  public Energy energy() {
    return energy;
  }

  /**
   * 
   * @return
   */
  public Statistics statistics() {
    return statistics;
  }

  /**
   * 
   * @return
   */
  public Health health() {
    return health;
  }

  /**
   * 
   * @return
   */
  public Inventory inventory() {
    return inventory;
  }

  /**
   * 
   * @return
   */
  public Equipment equipment() {
    return equipment;
  }

  /**
   * 
   * @return
   */
  public boolean isAlive() {
    return health().getCurrent() > 0;
  }

  /**
   * 
   * @param argMap
   * @return
   */
  public int effectiveSpeed(MapArea argMap) {
    int tileSpdMod = argMap.getSpeedModifier(getPosition());
    int speed = statistics.speed.getTotalValue();
    speed = Math.max(1, speed + tileSpdMod); // always at least 1 speed even if 0 or negative

    return speed;
  }

  /**
   * 
   * @return
   */
  public boolean wasAttackedThisRound() {
    return attackedThisRound;
  }

  /**
   * 
   * @return
   */
  public CombatHandler combatHandler() {
    return this.combat;
  }

  /**
   * 
   * @return
   */
  public Coordinate getPosition() {
    return position;
  }

  /**
   * 
   * @param x
   * @param y
   */
  public void setPosition(int x, int y) {
    position.setPosition(x, y);
  }

  /**
   * 
   * @param argXAmt
   * @param argYAmt
   */
  public void offsetPosition(int argXAmt, int argYAmt) {
    position.offsetPosition(argXAmt, argYAmt);
  }

  /**
   * 
   * @return
   */
  public String getDescription() {
    return getName();
  }

  /**
   * 
   * @return
   */
  public int getVisionRadius() {
    return visionRadius;
  }

  /**
   * 
   * @param argRad
   */
  public void setVisionRadius(int argRad) {
    visionRadius = Math.max(1, argRad);
  }

  /**
   * 
   * @param argOther
   * @return
   */
  public boolean isAdjacentTo(Actor argOther) {
    Point actorPos = position;
    Point otherPos = argOther.position;

    return Math.floor(actorPos.distance(otherPos)) <= 1;
  }

  /**
   * 
   * @param argOther
   * @param argMap
   * @return
   */
  public boolean canSee(Actor argOther, MapArea argMap) {
    if (!argOther.isAlive()) {
      return false;
    }

    return ActorUtils.canSee(this, argOther, argMap);
  }

  /**
   * 
   * @return
   */
  public AttackAttempt getLastAttacked() {
    return attacked.isEmpty() ? attacked.pop() : null;
  }

  /**
   * 
   * @return
   */
  public AttackAttempt getLastAttackedBy() {
    return attackedBy.isEmpty() ? attackedBy.pop() : null;
  }

  /**
   * 
   * @return
   */
  public String getMessageName() {
    return "the " + getName();
  }

  /**
   * 
   * @return
   */
  public String getVerbSuffix() {
    return "s";
  }

  /**
   * 
   * @param argAct
   * @param argParams
   * @return
   */
  public String doAction(String argAct, Object... argParams) {
    try {
      return getMessageName() + " " + makeCorrectVerb(String.format(argAct, argParams));
    }
    catch (Exception e) {
      return "ERROR: " + argAct;
    }
  }

  /**
   * 
   */
  public final void applyConditions() {
    ArrayList<Condition> toRemove = new ArrayList<>();

    for (Condition condition : conditions) {
      if (condition.process(this)) {
        toRemove.add(condition);
      }
    }
    conditions.removeAll(toRemove);
  }

  /**
   * 
   */
  public final void finishTurn() {
    while (attacked.size() > 1) {
      ((Stack<AttackAttempt>) attacked).remove(0);
    }

    while (attackedBy.size() > 1) {
      ((Stack<AttackAttempt>) attackedBy).remove(0);
    }

    attackedThisRound = false;
    Log.verboseDebug("Actor.finishTurn(): " + getName());

    applyConditions();
    onTurnFinished();
  }

  /**
   * 
   */
  public final void dead() {
    game = Game.current();
    onKilled();

    MapArea currentArea = game.getCurrentMapArea();

    if (Player.isPlayer(this)) {
      finishTurn();
      game.reset();
    }

    currentArea.removeActor(this);
    /* display bloodstain */
    currentArea.getTileAt(this.getPosition()).setBackground(SColorFactory.dimmer(SColor.DARK_RED));
    game.displayMessage("Target is dead");
  }

  /**
   * 
   * @param argAtkr
   */
  public final void onAttacked(Actor argAtkr) {
    attackedBy.add(new AttackAttempt(argAtkr));
    attackedThisRound = true;

    if (behavior != null) {
      behavior.onAttacked(argAtkr);
    }

    onAttackedInternal(argAtkr);
  }

  /**
   * 
   * @param argAmt
   */
  public final void onDamaged(int argAmt) {
    if (health.damage(argAmt)) {
      dead();
    }
  }

  /**
   * 
   * @return
   */
  public abstract String getName();

  /**
   * 
   * @return
   */
  public abstract Action getNextAction();

  /**
   * 
   */
  protected void onKilled() {
  }

  /**
   * This is called after all other checks to move have been made, to allow the
   * actor a final chance to prevent the move (for example, moving over certain
   * tiles or opening doors could be disabled based on the actor type)
   * 
   * @param argMap
   * @param argTile
   * @return
   */
  public boolean onMoveAttempting(MapArea argMap, Tile argTile) {
    // TODO: check for things like moving over certain tiles, opening doors
    return true;
  }

  /**
   * 
   */
  protected void onTurnFinished() {
  }

  /**
   * 
   * @param argAtkr
   */
  protected abstract void onAttackedInternal(Actor argAtkr);

  /**
   * 
   * @param argMsg
   * @return
   */
  protected abstract String makeCorrectVerb(String argMsg);
}
