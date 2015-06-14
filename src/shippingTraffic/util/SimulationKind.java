package shippingTraffic.util;

/**
 * Define the values for the kind of simulation.
 * 
 * @author Chenfeng ZHU
 * 
 */
public class SimulationKind {

	/**
	 * The simulation for simple collision.
	 */
	public final static int SIMPLE_COLLISION = 1;

	/**
	 * The simulation for 2nd simple collision.
	 */
	public final static int SIMPLE_COLLISION_2 = 2;

	/**
	 * The simulation for collision avoidance.
	 */
	public final static int AVOIDANCE_COLLISION = 10;

	/**
	 * The simulation for hit the lands.
	 */
	public final static int HITTING_LANDS = 11;

	/**
	 * Ships move parallelly from ocean into river.
	 */
	public final static int OCEAN_TO_RIVER_2 = 40;

	/**
	 * Ships start with ramdom directions from ocean to river.
	 */
	public final static int OCEAN_TO_RIVER_3 = 50;

	/**
	 * Check whether the simulation needs to detect the collision.
	 * 
	 * @param kind
	 * @return
	 */
	public static boolean isCollision(int kind) {
		switch (kind) {
		case SIMPLE_COLLISION:
		case SIMPLE_COLLISION_2:
			return true;
		}
		return false;
	}

	public static String getSimulationName(int kind) {
		String str = "";
		switch (kind) {
		case SIMPLE_COLLISION:
			str = "Simple Collision";
			break;
		case SIMPLE_COLLISION_2:
			str = "Simple Collision 2";
			break;
		case AVOIDANCE_COLLISION:
			str = "Avoidance Collision";
			break;
		}
		return str;
	}

}
