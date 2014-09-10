package roguelike.actors;

import java.util.LinkedList;
import java.util.Queue;

import roguelike.actions.Action;
import roguelike.actions.combat.CombatHandler;
import roguelike.items.Equipment;
import roguelike.items.Inventory;
import roguelike.maps.MapArea;
import roguelike.maps.Tile;
import roguelike.util.Coordinate;
import squidpony.squidcolor.SColor;
import squidpony.squidgrid.los.BresenhamLOS;
import squidpony.squidgrid.los.LOSSolver;

public abstract class Actor {

	protected Coordinate position;
	protected char symbol;
	protected SColor color;

	protected Queue<AttackAttempt> attacked;
	protected Queue<AttackAttempt> attackedBy;

	private Energy energy;
	private Statistics statistics;
	private CombatHandler combat;
	private Health health;
	private Inventory inventory;
	private Equipment equipment;

	protected LOSSolver losSolver;

	protected Actor(char symbol, SColor color) {
		this.symbol = symbol;
		this.color = color;
		this.position = new Coordinate();

		this.energy = new Energy();
		this.statistics = new Statistics();
		this.combat = new CombatHandler(this);
		this.health = new Health(100);
		this.inventory = new Inventory();
		this.equipment = new Equipment();

		attacked = new LinkedList<AttackAttempt>();
		attackedBy = new LinkedList<AttackAttempt>();

		losSolver = new BresenhamLOS();
	}

	public Energy getEnergy() {
		return this.energy;
	}

	public Statistics getStatistics() {
		return this.statistics;
	}

	public CombatHandler getCombatHandler() {
		return this.combat;
	}

	public Health getHealth() {
		return this.health;
	}

	public Inventory getInventory() {
		return this.inventory;
	}

	public Equipment getEquipment() {
		return this.equipment;
	}

	public Coordinate getPosition() {
		return position;
	}

	public void setPosition(int x, int y) {
		position.setPosition(x, y);
	}

	public void offsetPosition(int xAmount, int yAmount) {
		position.offsetPosition(xAmount, yAmount);
	}

	public char getSymbol() {
		return this.symbol;
	}

	public SColor getColor() {
		return this.color;
	}

	public Coordinate chooseTarget() {
		// TODO: fix this
		return new Coordinate(0, 0);
	}

	public boolean isAlive() {
		return getHealth().getCurrent() > 0;
	}

	/**
	 * This is called after all other checks to move have been made, to allow
	 * the actor a final chance to prevent the move (for example, moving over
	 * certain tiles or opening doors could be disabled based on the actor type)
	 * 
	 * @param mapArea
	 * @param tile
	 * @return
	 */
	public boolean onMoveAttempting(MapArea mapArea, Tile tile) {
		// TODO: check for things like moving over certain tiles, opening doors
		return true;
	}

	public String getDescription() {
		return getName();
	}

	public int getVisionRadius() {
		return 15;
	}

	public AttackAttempt getLastAttacked() {
		return attacked.poll();
	}

	public AttackAttempt getLastAttackedBy() {
		return attackedBy.poll();
	}

	public final void finishTurn() {
		if (attacked.size() > 5)
			((LinkedList<AttackAttempt>) attacked).removeLast();
		if (attackedBy.size() > 5)
			((LinkedList<AttackAttempt>) attackedBy).removeLast();

		onTurnFinished();
	}

	protected void onTurnFinished() {
	}

	public void onKilled() {
	}

	public abstract String getName();

	public abstract Action getNextAction();

	public final void onAttacked(Actor attacker) {
		attackedBy.add(new AttackAttempt(attacker));
		onAttackedInternal(attacker);
	}

	protected abstract void onAttackedInternal(Actor attacker);
}
