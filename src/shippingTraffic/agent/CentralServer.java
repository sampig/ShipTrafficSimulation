package shippingTraffic.agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;
import repast.simphony.util.ContextUtils;
import shippingTraffic.util.GridNode;
import shippingTraffic.util.MapCreator;
import shippingTraffic.util.MathUtil;
import shippingTraffic.util.ShipCreator;
import shippingTraffic.util.SimulationKind;
import shippingTraffic.util.TypeShip;
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
	// private int changeStepNum = 10;
	private int initNumShip = 0;
	private int maxNumShip = 0;

	private int delayCreation = 0; // after steps, ships are recreated.
	public final static int DELAY_CREATION = 10;

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
		initNumShip = (Integer) p.getValue("initNumShip");
		maxNumShip = p.getInteger("shipMaxNum");
	}

	/**
	 * Start the simulation. Prepare for the simulation.
	 */
	public void startSimulation() {
		switch (simulationKind) {
		case SimulationKind.SIMPLE_COLLISION:
		case SimulationKind.SIMPLE_COLLISION_2:
			this.preSimpleCollisionSim();
			maxNumShip = 2;
			break;
		case SimulationKind.AVOIDANCE_COLLISION:
			this.preAvoidanceCollisionSim();
			break;
		case SimulationKind.HITTING_LANDS:
			this.preOceanSim();
			break;
		case SimulationKind.OCEAN_TO_RIVER_2:
			this.preOceanSim(1);
			break;
		case SimulationKind.OCEAN_TO_RIVER_3:
			this.preOceanSim(2);
			break;
		default:
			break;
		}
		this.printSimulationEnv();
	}

	/**
	 * Simple Collision Simulation.
	 */
	public void preSimpleCollisionSim() {
		mapCreator.createSimpleStraightRiver();
		mapCreator.setStartNodesCollision();
		shipCreator.createShips(2, mapCreator.getStartNodes());
		weatherController.init();
	}

	/**
	 * Avoidance of Collision Simulation.
	 */
	public void preAvoidanceCollisionSim() {
		mapCreator.createSimpleStraightRiver();
		shipCreator.createTwoShips();
		weatherController.init();
	}

	/**
	 * Running in the Ocean.
	 */
	public void preOceanSim() {
		mapCreator.createOcean();
		mapCreator.setTestStartNodes();
		// Get the parameters p and then specify the initial numbers of ships.
		shipCreator.createShips(initNumShip, mapCreator.getStartNodes());
	}

	public void preOceanSim(int type) {
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
		switch (simulationKind) {
		case SimulationKind.OCEAN_TO_RIVER_2:
		case SimulationKind.OCEAN_TO_RIVER_3:
			this.checkLand();
		case SimulationKind.AVOIDANCE_COLLISION:
		case SimulationKind.HITTING_LANDS:
			this.checkCollision();
		case SimulationKind.SIMPLE_COLLISION:
		case SimulationKind.SIMPLE_COLLISION_2:
		default:
			this.collisionEvent();
		}

		// check the status of the whole map.
		this.checkShips();
		this.checkObstacles();

		// when delayCreation is not 0, after steps, ships would be recreated.
		if (delayCreation > 0) {
			if (delayCreation == 1) {
				int num = maxNumShip - listShips.size();
				shipCreator.createShips(num, mapCreator.getStartNodes());
				for (GridNode node : shipCreator.getList()) {
					Ship ship = new Ship(this.context, node.getX(),
							node.getY(), node.getDirection(),
							TypeShip.getRandomInstance());
					this.context.add(ship);
					getListShips().add(ship);
				}
			}
			delayCreation--;
		}
	}

	/**
	 * Collision happens.
	 */
	public void collisionEvent() {
		for (int i = 0; i < listShips.size(); i++) {
			Ship s1 = listShips.get(i);
			for (int j = i + 1; j < listShips.size(); j++) {
				Ship s2 = listShips.get(j);
				if (s1.getPreviousPosition().getX() == 0
						&& s1.getPreviousPosition().getY() == 0) {
					continue;
				}
				double d = MathUtil.calDistance(s1.getPosition(),
						s2.getPosition());
				// if two ships move far away from each other, no collision
				// would happen.
				if (d > 5) {
					continue;
				}
				double size = (s1.getSize() + s2.getSize()) / 2;
				if (this.simulationKind == SimulationKind.SIMPLE_COLLISION) {
					size = 0.0001;
				}
				if (MathUtil.calIntersection(s1.getPreviousPosition(),
						s1.getPosition(), s2.getPreviousPosition(),
						s2.getPosition(), size)) {
					// collision happens.
					s1.collision(s2);
					// remove the ships from the list.
					listShips.remove(s1);
					listShips.remove(s2);
					System.out.println("Current number: " + listShips.size());
					// add obstacle into the list.
					int x = (s1.getPosition().getX() + s2.getPosition().getX()) / 2;
					int y = (s1.getPosition().getY() + s2.getPosition().getY()) / 2;
					obstacleAppear(x, y);
					// if ships are not enough, prepare to create new ones.
					if (listShips.size() < this.maxNumShip) {
						delayCreation = DELAY_CREATION;
					}
				}
			}
		}
	}

	public void checkShips() {
		for (Ship ship : listShips) {
			if (ship == null) {
				listShips.remove(ship);
			}
			if (listShips.size() < this.maxNumShip) {
				delayCreation = DELAY_CREATION;
			}
		}
	}

	public void obstacleAppear(int x, int y) {
		Obstacle obs = new Obstacle(this.context, x, y);
		this.context.add(obs);
		getListObs().add(obs);
	}

	public void checkObstacles() {
		for (Obstacle obs : listObs) {
			if (obs == null) {
				listObs.remove(obs);
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
				if (s1.getListID().contains(s2.getID())
						|| s2.getListID().contains(s1.getID())) {
					continue;
				}
				double d = MathUtil.calDistance(s1.getPosition(),
						s2.getPosition());
				if (d < s1.getSafeDistance() + s2.getSafeDistance()) {
					// if two ships move away from each other, it is safe.
					if (MathUtil.checkSafeDistance(s1.getPreviousPosition(),
							s1.getPosition(), s2.getPreviousPosition(),
							s2.getPosition())) {
						continue;
					}
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
				// mapShips.put(s1.getID(), new double[] { s1.getHeading(),
				// changeStepNum, s1.getType().getMax_turn() });
				double turn = s1.getType().getMax_turn();
				if (MathUtil
						.chooseDirection(s1.getPosition(), s2.getPosition())) {
					turn = -turn;
				}
				s1.changeDirection(turn, s2);
			}
		} else {
			if (mapShips.get(s2.getID()) == null) {
				double turn = s2.getType().getMax_turn();
				if (MathUtil
						.chooseDirection(s2.getPosition(), s1.getPosition())) {
					turn = -turn;
				}
				s2.changeDirection(turn, s1);
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
				listShips.remove(s);
			}
		}
	}

	/**
	 * Output the simulation environment.
	 */
	public void printSimulationEnv() {
		StringBuffer sb = new StringBuffer();
		sb.append("Simulation Parameters: {\n");
		sb.append("\tSimulation Kind: "
				+ SimulationKind.getSimulationName(simulationKind) + "\n");
		sb.append("\tInitial Number: " + this.initNumShip + "\n");
		sb.append("\tMax Number: " + this.maxNumShip + "\n");
		sb.append("\tStart Nodes: " + mapCreator.getStartNodes().toString()
				+ "\n");
		sb.append("}\n");
		System.out.println(sb.toString());
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
