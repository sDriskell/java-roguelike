package roguelike;

import roguelike.actors.Player;
import roguelike.data.DataFactory;
import roguelike.items.Equipment.ItemSlot;
import roguelike.items.MeleeWeapon;
import roguelike.items.WeaponFactory;
import roguelike.maps.MapArea;
import roguelike.maps.MapBuilder;
import squidpony.squidmath.RNG;

public class GameLoader {

	private static GameLoader gameLoader = new GameLoader();

	/**
	 * Singleton DataFactory
	 */
	final DataFactory dataFactory;

	private GameLoader() {
		dataFactory = DataFactory.instance();
	}

	public static GameLoader instance() {
		return gameLoader;
	}

	public Game newGame() {
		Game game = new Game();
		Player player = game.getPlayer();

		// player.setPosition(1, 1);

		// TODO: make a real map
		MapArea currentMapArea = new MapArea(300, 300, new MapBuilder());
		currentMapArea.addActor(player);

		game.setCurrentMapArea(currentMapArea);
		return game;
	}

	public Game load() {
		// TODO: load game from file instead
		return newGame();
	}

	public Player createPlayer() {
		Player player = new Player();

		MeleeWeapon sword = (MeleeWeapon) WeaponFactory.create("sword");
		MeleeWeapon spear = (MeleeWeapon) WeaponFactory.create("spear");

		player.getInventory().add(spear);

		ItemSlot.RIGHT_ARM.equipItem(player, sword);

		return player;
	}

	public Player loadPlayer() {
		// TODO: load player from file instead
		return createPlayer();
	}

	public RNG getRandom() {
		return new RNG();
	}
}
