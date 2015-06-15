package shippingTraffic.util;

import java.util.Random;

/**
 * Define different types of ships. Set the default attributes for ships. It is
 * also a simple class for the type of ship. It contains attributes of ships:
 * <ul>
 * <li>type name</li>
 * <li>mass of ship</li>
 * <li>size of ship</li>
 * <li>max speed of ship</li>
 * <li>max angle the ship could turn</li>
 * <li>its safe distance</li>
 * </ul>
 * All these attributes are decided based on type.
 * 
 * @author Chenfeng ZHU
 * 
 */
public class TypeShip {

	/**
	 * SHIP_1
	 */
	public final static int SHIP_1 = 1;
	/**
	 * SHIP_2
	 */
	public final static int SHIP_2 = 2;
	/**
	 * SHIP_3
	 */
	public final static int SHIP_3 = 3;

	private int type = SHIP_1;
	private String name = ""; // name of ship
	private double mass = 0; // mass of ship
	private double size = 0; // size of ship
	private double max_speed = 0; // max speed of ship
	private double max_turn = 45; // the max angle ship could turn
	private double safe_distance = 25; // safe distance of ship
	private double warning_distance = 5; //

	public TypeShip(int type) {
		this.type = type;
		init();
	}

	/**
	 * According to the type of ship, set the initial values.
	 */
	private void init() {
		String name = "";
		double mass = 0;
		double size = 0;
		switch (type) {
		case SHIP_1:
			name = "SHIP_1";
			mass = 1;
			size = 2;
			max_speed = 2;
			max_turn = 60;
			safe_distance *= 1;
			break;
		case SHIP_2:
			name = "SHIP_2";
			mass = 2;
			size = 5;
			max_speed = 2;
			max_turn = 45;
			safe_distance *= 1;
			break;
		case SHIP_3:
			name = "SHIP_3";
			mass = 3;
			size = 10;
			max_speed = 1;
			max_turn = 30;
			safe_distance *= 1.3;
			break;
		default:
			break;
		}
		this.name = name;
		this.mass = mass;
		this.size = size;
	}

	/**
	 * Get one TypeShip randomly.
	 * 
	 * @return One ship Type
	 */
	public static TypeShip getRandomInstance() {
		Random r = new Random();
		int v = r.nextInt(3) + 1;
		TypeShip type = new TypeShip(v);
		return type;
	}

	public String getTypeName() {
		return this.name;
	}

	public double getMass() {
		return this.mass;
	}

	public double getSize() {
		return this.size;
	}

	public double getCapacity() {
		double capacity = 0;
		return capacity;
	}

	public double getMaxSpeed() {
		return this.max_speed;
	}

	/**
	 * Get the max angle ship could turn.
	 * 
	 * @return max angle (degree)
	 */
	public double getMax_turn() {
		return max_turn;
	}

	/**
	 * Get the safe distance.
	 * 
	 * @return safe distance
	 */
	public double getSafeDistance() {
		return this.safe_distance;
	}

	public double getWarningDistance() {
		return this.warning_distance;
	}

}
