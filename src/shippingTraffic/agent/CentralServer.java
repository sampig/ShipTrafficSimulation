package shippingTraffic.agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;
import repast.simphony.util.ContextUtils;
import shippingTraffic.util.MapCreator;
import shippingTraffic.util.MathUtil;
import shippingTraffic.util.ShipCreator;
import shippingTraffic.util.SimulationKind;
import shippingTraffic.util.Weather;
import shippingTraffic.util.WeatherController;

/**
 * Central Server.<br/>
 * <code>CentralServer</code> choose the kind of simulation, and then use
 * <code>MapCreator</code> to create SimMap and use <code>ShipCreator</code> to
 * create Ship according to the kind.
 * 
 * @author Chenfeng ZHU
 * 
 * @see MapCreator
 * @see ShipCreator
 * 
 */
public class CentralServer extends SimpleAgent {

	private Context<SimpleAgent> context;
	private int simulationKind = 1;

	// agents controlled by server.
	private List<Ship> listShips = new ArrayList<>(0);
	private List<Obstacle> listObs = new ArrayList<>(0);
	private Weather weather;
	// ID, status(direction,count)
	private Map<String, double[]> mapShips = new HashMap<>(0);

	// Controller:
	private MapCreator mapCreator = new MapCreator();
	private ShipCreator shipCreator = new ShipCreator();
	private WeatherController weatherController = new WeatherController();

	// Graphical attributes:
	public final static int SIZE = 10;
	private int changeStepNum = 10;

	@SuppressWarnings("unchecked")
	public CentralServer(Context<SimpleAgent> context) {
		this.context = context;
		this.context.add(this);
		Grid<Object> grid = (Grid<Object>) context.getProjection("Simple_Grid");
		ContinuousSpace<Object> space = (ContinuousSpace<Object>) this.context
				.getProjection("Continuous_Space");
		grid.moveTo(this, (MapCreator.SIZE_X - 2), 2);
		space.moveTo(this, (MapCreator.SIZE_X - 2), 2, 0);
		// The editable environment parameters in the GUI.
		Parameters p = RunEnvironment.getInstance().getParameters();
		simulationKind = (Integer) p.getValue("simulationKind");
	}

	/**
	 * Start the simulation.
	 */
	public void startSimulation() {
		switch (simulationKind) {
		case SimulationKind.SIMPLE_COLLISION:
			this.simpleCollisionSim();
			break;
		case SimulationKind.SIMPLE_COLLISION_2:
			this.simpleCollisionSim2();
			break;
		case SimulationKind.AVOIDANCE_COLLISION:
			this.avoidanceCollisionSim();
			break;
		case SimulationKind.HITTING_LANDS:
			this.oceanSim();
			break;
		case SimulationKind.OCEAN_TO_RIVER_2:
			this.oceanSim(1);
			break;
		case SimulationKind.OCEAN_TO_RIVER_3:
			this.oceanSim(2);
			break;
		default:
			break;
		}
	}

	/**
	 * Simple Collision Simulation.
	 */
	public void simpleCollisionSim() {
		mapCreator.createSimpleStraightRiver();
		shipCreator.createTwoShips();
		weatherController.init();
	}

	public void simpleCollisionSim2() {
		mapCreator.createSimpleStraightRiver();
		mapCreator.resetStartNodes(simulationKind);
		shipCreator.createTwoRandomShips();
	}

	/**
	 * Avoidance of Collision Simulation.
	 */
	public void avoidanceCollisionSim() {
		mapCreator.createSimpleStraightRiver();
		shipCreator.createTwoShips();
		weatherController.init();
	}

	/**
	 * Running in the Ocean.
	 */
	public void oceanSim() {
		mapCreator.createOcean();
		// Get the parameters p and then specify the initial numbers of ships.
		Parameters p = RunEnvironment.getInstance().getParameters();
		int numShips = (Integer) p.getValue("initNumShip");
		shipCreator.createShips(numShips, mapCreator.getStartNodes());
	}

	public void oceanSim(int type) {
		mapCreator.createOcean();
		mapCreator.resetStartNodes(simulationKind);
		shipCreator.createShips(2, mapCreator.getStartNodes());
	}

	@SuppressWarnings("rawtypes")
	public void step() {
		// Get the context in which the ship resides.
		Context context = ContextUtils.getContext(this);
		// Get the patch grid from the context
		Grid patch = (Grid) context.getProjection("Simple_Grid");
		for (Object o : patch.getObjectsAt(0, 0)) {
			o.toString();
		}
		// only if it is the simple collision simulation, server will not check
		// the collision.
		if (!SimulationKind.isCollision(simulationKind)) {
			this.checkCollision();
			if (simulationKind != SimulationKind.HITTING_LANDS) {
				this.checkLand();
			}
		}
		this.collision();
		Iterator<Map.Entry<String, double[]>> entries = mapShips.entrySet()
				.iterator();
		while (entries.hasNext()) {
			Map.Entry<String, double[]> entry = entries.next();
			String key = (String) entry.getKey();
			double[] value = entry.getValue();
			if (value == null || Math.abs(value[1] + this.changeStepNum) < 1) {
				// resume the original status
				this.resumeDirection(key, value[0]);
				entries.remove();
			} else {
				entry.setValue(new double[] { value[0], value[1] - 1, value[2] });
				if (value[1] > 0) {
					; // this.changeDirection(key, value[2]);
				} else if (value[1] < 1 && value[1] > -1) {
					this.resumeDirection(key, value[0]);
					this.changeDirection(key, -value[2]);
				} else if (value[1] < 0) {
					;
				}
			}
		}
		// mooring
		// this.moor();
	}

	/**
	 * Collision happens.
	 */
	public void collision() {
		for (int i = 0; i < listShips.size(); i++) {
			Ship s1 = listShips.get(i);
			for (int j = i + 1; j < listShips.size(); j++) {
				Ship s2 = listShips.get(j);
				double d = MathUtil.calDistance(s1.getPosition(),
						s2.getPosition());
				// if two ships move far away from each other, no collision
				// would happen.
				if (d > 5) {
					continue;
				}
				double size = (s1.getSize() + s2.getSize()) / 2;
				if (MathUtil.calIntersection(s1.getPreviousPosition(),
						s1.getPosition(), s2.getPreviousPosition(),
						s2.getPosition(), size)) {
					s1.collision(s2);
					listShips.remove(s1);
					listShips.remove(s2);
				}
			}
		}
	}

	/**
	 * Check the collision.
	 */
	public void checkCollision() {
		for (int i = 0; i < listShips.size(); i++) {
			Ship s1 = listShips.get(i);
			for (int j = i + 1; j < listShips.size(); j++) {
				Ship s2 = listShips.get(j);
				double d = MathUtil.calDistance(s1.getPosition(),
						s2.getPosition());
				if (d < s1.getSafeDistance() + s2.getSafeDistance()) {
					// if (d < s1.getSafeDistance() || d < s2.getSafeDistance())
					// {
					this.changeDirection(s1, s2);
				}
			}
		}
	}

	/**
	 * Check the land ahead.
	 */
	public void checkLand() {
		int y = mapCreator.getDestiny().getY();
		int rx = mapCreator.getLineX();
		int rx2 = mapCreator.getLineX2();
		int y1 = mapCreator.getRiverY1();
		int y2 = mapCreator.getRiverY2();
		for (Ship s : listShips) {
			double turn = s.getType().getMax_turn();
			double sd = s.getSafeDistance();
			if (s.getPosition().getY() < y) {
				if (s.getPosition().getX() > rx) {
					continue;
				}
				if (s.getPosition().getY() >= y1 + sd
						&& s.getPosition().getY() <= y2 - sd) {
					s.setHeading(0);
					continue;
				}
				double dx = rx - (sd + y1 - s.getPosition().getY())
						/ Math.tan(Math.toRadians(s.getHeading() + turn));
				if (s.getPosition().getY() > y1) {
					continue;
				}
				if (s.getPosition().getX() >= dx - sd) {
					s.changeDirection(turn);
				}
			} else {
				if (s.getPosition().getX() > rx2) {
					continue;
				}
				if (s.getPosition().getY() <= y2 - sd
						&& s.getPosition().getY() >= y1 + sd) {
					s.setHeading(0);
					continue;
				}
				double dx = rx2 - (s.getPosition().getY() + sd - y2)
						/ Math.tan(Math.toRadians(-s.getHeading() + turn));
				if (s.getPosition().getX() >= dx - sd) {
					s.changeDirection(-turn);
				}
				// if (!mapCreator.getWater().contains(
				// new GridNode(s.getPosition().getX() + (int) sd, s
				// .getPosition().getY()))) {
				// s.changeDirection(-turn);
				// }
			}
		}
	}

	/**
	 * Re-arrange the entrance of the ships according to their speed.
	 */
	public void arrangeEntrance() {
		for (int i = 0; i < listShips.size(); i++) {
			Ship s1 = listShips.get(i);
			for (int j = i + 1; j < listShips.size(); j++) {
				Ship s2 = listShips.get(j);
				if (possibleAccident(s1, s2)) {
					this.changeDirection(s1, s2);
				}
			}
		}
	}

	public boolean possibleAccident(Ship s1, Ship s2) {
		return true;
	}

	/**
	 * Change the direction of ship(s), when a collision may happen between
	 * these two ships.
	 * 
	 * @param s1
	 *            one Ship
	 * @param s2
	 *            one Ship
	 */
	public void changeDirection(Ship s1, Ship s2) {
		// System.out.println("Change direction.");
		if (s1.getTotal_mass() <= s2.getTotal_mass()) {
			if (mapShips.get(s1.getID()) == null) {
				// save the current status of ship.
				mapShips.put(s1.getID(), new double[] { s1.getHeading(),
						changeStepNum, s1.getType().getMax_turn() });
				s1.changeDirection(s1.getType().getMax_turn());
			}
		} else {
			if (mapShips.get(s2.getID()) == null) {
				mapShips.put(s2.getID(), new double[] { s2.getHeading(),
						changeStepNum, s2.getType().getMax_turn() });
				s2.changeDirection(s2.getType().getMax_turn());
			}
		}
	}

	public void changeDirection(String id, double change_direction) {
		for (Ship s : listShips) {
			if (s.getID() == id) {
				s.changeDirection(change_direction);
			}
		}
	}

	public void resumeDirection(String id, double direction) {
		for (Ship s : listShips) {
			if (s.getID() == id) {
				s.setHeading(direction);
			}
		}
	}

	public void moor() {
		for (Ship s : listShips) {
			if (s.getPosition().getX() >= mapCreator.getDestiny().getX()) {
				s.moor();
			}
		}
	}

	public List<Ship> getListShips() {
		return listShips;
	}

	public List<Obstacle> getListObs() {
		return listObs;
	}

	public Weather getWeather() {
		return weather;
	}

	public int getSimulationKind() {
		return simulationKind;
	}

	public MapCreator getMapCreator() {
		return mapCreator;
	}

	public ShipCreator getShipCreator() {
		return shipCreator;
	}

	public WeatherController getWeatherController() {
		return weatherController;
	}

}
