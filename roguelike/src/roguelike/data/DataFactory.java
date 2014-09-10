package roguelike.data;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import roguelike.MainScreen;
import roguelike.actors.Actor;
import roguelike.actors.behaviors.Behavior;
import roguelike.util.FileUtils;
import squidpony.squidcolor.SColor;
import squidpony.squidcolor.SColorFactory;

/**
 * Loads all config data from JSON file and provides a central repository
 * 
 * @author john
 * 
 */
public class DataFactory {

	private static DataFactory instance = new DataFactory();
	private Map<String, MonsterData> monsterData;
	private Map<String, WeaponData> weaponData;

	private DataFactory() {
		monsterData = new HashMap<String, MonsterData>();
		weaponData = new HashMap<String, WeaponData>();

		init();
	}

	public static DataFactory instance() {
		return instance;
	}

	public Map<String, MonsterData> getMonsters() {
		return monsterData;
	}

	public Map<String, WeaponData> getWeapons() {
		return weaponData;
	}

	public static Behavior createBehavior(String behavior, Actor actor) {
		Class<?> behaviorType;
		Constructor<?> constructor;
		Object instance = null;
		try {
			behaviorType = Class.forName("roguelike.actors.behaviors." + behavior + "Behavior");
			constructor = behaviorType.getConstructor(Actor.class);
			if (constructor != null) {
				instance = constructor.newInstance(actor);
			}
			else {
				System.out.println("Couldn't create instance of " + behavior);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}

		return (Behavior) instance;
	}

	private void init() {
		System.out.println("Initializing data from config files...");

		InputStream config = MainScreen.class.getResourceAsStream("/resources/config/data.json");
		String configString = "";
		try {
			configString = FileUtils.readFile(config);
		} catch (IOException e) {
			e.printStackTrace();
		}

		/* load monster data from JSON file */
		JSONObject jObj = new JSONObject(configString);

		JSONArray tables = jObj.getJSONArray("tables");
		for (int t = 0; t < tables.length(); t++) {
			JSONObject jtable = tables.getJSONObject(t);
			String tableName = jtable.getString("name");

			System.out.println("Reading " + tableName + "...");

			JSONArray jData = jtable.getJSONArray("records");

			if (tableName.equals("monsters")) {
				readMonsters(jData);
			}
			else if (tableName.equals("weapons")) {
				readWeapons(jData);
			}
		}
	}

	private void readMonsters(JSONArray jData) {
		for (int x = 0; x < jData.length(); x++) {
			JSONObject dataObj = jData.getJSONObject(x);

			String name = dataObj.getString("name");
			char symbol = dataObj.getString("symbol").charAt(0);
			SColor color = SColorFactory.colorForName(dataObj.getString("color"));
			color = color == null ? SColor.WHITE : color;
			int speed = dataObj.getInt("speed");
			String weapon = dataObj.getString("weapon");

			MonsterData data = new MonsterData(symbol, color, name);
			data.behavior = dataObj.getString("behavior");
			data.speed = speed;
			data.weapon = weapon;

			monsterData.put(name, data);
			System.out.println("     " + name);
		}
	}

	private void readWeapons(JSONArray jData) {
		for (int x = 0; x < jData.length(); x++) {
			JSONObject dataObj = jData.getJSONObject(x);

			String name = dataObj.getString("name");
			char symbol = dataObj.getString("symbol").charAt(0);
			SColor color = SColorFactory.colorForName(dataObj.getString("color"));
			color = color == null ? SColor.WHITE : color;
			int baseDamage = dataObj.getInt("baseDamage");
			int reach = dataObj.getInt("reach");
			boolean droppable = dataObj.getString("droppable").equals("1") ? true : false;
			String type = dataObj.getString("type");
			String attackDescription = dataObj.getString("attackDescription");

			WeaponData data = new WeaponData(name, baseDamage, reach, droppable, type, attackDescription);
			data.symbol = symbol;
			data.color = color;

			weaponData.put(name, data);
			System.out.println("     " + name);
		}
	}
}
