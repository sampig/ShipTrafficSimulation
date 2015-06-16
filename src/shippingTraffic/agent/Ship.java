package shippingTraffic.agent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import repast.simphony.annotate.AgentAnnot;
import repast.simphony.context.Context;
import repast.simphony.space.SpatialMath;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;
import shippingTraffic.util.GridNode;
import shippingTraffic.util.TypeShip;

/**
 * Ship Agent.<br/>
 * 
 * @author Chenfeng ZHU
 * 
 */
@AgentAnnot(displayName = "Ship")
public class Ship extends SimpleAgent {

	private String ID = UUID.randomUUID().toString();

	private Context<SimpleAgent> context;
	private ContinuousSpace<Object> space;
	private Grid<Object> grid;

	private TypeShip type = new TypeShip(TypeShip.SHIP_2);
	private double size;
	private double total_mass;
	// private double speed = 1;
	private GridNode position = new GridNode();
	private GridNode previousPos = new GridNode();
	private GridNode nextPos = new GridNode();

	//
	private List<int[]> changeCount = new ArrayList<>(0);
	public final static int CHANGE_COUNT = 10;
	private List<double[]> changeDirection = new ArrayList<>(0);
	private List<String> listID = new ArrayList<>(0);

	//
	private CentralServer server;
	private GridNode destiny = new GridNode();

	public Ship() {
		this.setHeading(0);
		type = new TypeShip(TypeShip.SHIP_2);
	}

	public Ship(int heading) {
		this.setHeading(heading);
	}

	public Ship(ContinuousSpace<Object> space, Grid<Object> grid) {
		this.space = space;
		this.grid = grid;
	}

	@SuppressWarnings("unchecked")
	public Ship(Context<SimpleAgent> context, int x, int y, int d) {
		this.context = context;
		context.add(this);
		Grid<Object> grid = (Grid<Object>) context.getProjection("Simple_Grid");
		ContinuousSpace<Object> space = (ContinuousSpace<Object>) context
				.getProjection("Continuous_Space");
		grid.moveTo(this, x, y);
		space.moveTo(this, x, y, 0);
		this.setHeading(d);
		position.setX(x);
		position.setY(y);
		position.setDirection(d);
		init();
	}

	public Ship(Context<SimpleAgent> context, int x, int y, int d, TypeShip type) {
		this(context, x, y, d);
		this.setType(type);
		System.out.println("Create a " + this.toString() + ".");
	}

	public void init() {
		server = (CentralServer) this.context.getObjects(CentralServer.class)
				.iterator().next();
	}

	@SuppressWarnings("rawtypes")
	public void step() {
		// Get the context in which the ship resides.
		Context context = ContextUtils.getContext(this);

		// save the current status.
		previousPos.copy(position);

		// Move the ship
		move();
		// System.out.println("Ship moves.");

		// Get the patch grid from the context
		Grid patch = (Grid) context.getProjection("Simple_Grid");

		// Get the ship's current patch
		GridPoint point = patch.getLocation(this);

		int x = point.getX(); // The x-ccordinate of the ship's current patch
		int y = point.getY(); // The y-ccordinate of the ship's current patch
		// System.out.println("Ship(" + getID() + "): (" + x + "," + y + ")");
		position.setX(x);
		position.setY(y);

		Water water = null;
		Ship anotherShip = null;
		for (Object o : patch.getObjectsAt(x, y)) {
			if (o instanceof Water) {
				water = (Water) o;
			}
			if (o instanceof Ship) {
				if (!o.equals(this)) {
					anotherShip = (Ship) o;
				}
			}
		}
		if (water == null || !water.isAvailable()) {
			// System.out.println(water+","+water.isAvailable());
			System.out.println("I(" + this.getID() + ") hit something.");
			this.hit(x, y);
		}
		if (anotherShip != null) {
			// this.collision(anotherShip);
		}

		//
		if (changeCount.size() > 0) {
			for (int i = 0; i < changeCount.size(); i++) {
				int[] cc = changeCount.get(i);
				double[] cd = changeDirection.get(i);
				if (cc.length < 3) {
					continue;
				}
				if (cc[0] > 0) {
					if (cc[0] == 1) {
						this.changeDirection(cd[0]);
					}
					cc[0]--;
				} else if (cc[1] > 0) {
					if (cc[1] == 1) {
						this.changeDirection(cd[1]);
					}
					cc[1]--;
				} else if (cc[2] > 0) {
					if (cc[2] == 1) {
						this.changeDirection(cd[2]);
					}
					cc[2]--;
				} else {
					changeCount.remove(cc);
					changeDirection.remove(cd);
					listID.remove(i);
				}
			}
		}
	}

	public void moveTowards(GridPoint pt) {
		if (!pt.equals(grid.getLocation(this))) {
			NdPoint myPoint = space.getLocation(this);
			NdPoint otherPoint = new NdPoint(pt.getX(), pt.getY());
			double angle = SpatialMath.calcAngleFor2DMovement(space, myPoint,
					otherPoint);
			space.moveByVector(this, 1, angle, 0);
			myPoint = space.getLocation(this);
			grid.moveTo(this, (int) myPoint.getX(), (int) myPoint.getY());
		}
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @param patch
	 */
	@SuppressWarnings("rawtypes")
	public void collide(int x, int y, Grid patch) {
		Water water = null;
		Ship anotherShip = null;
		for (Object o : patch.getObjectsAt(x, y)) {
			if (o instanceof Water) {
				water = (Water) o;
			}
			if (o instanceof Ship) {
				if (!o.equals(this)) {
					anotherShip = (Ship) o;
				}
			}
		}
		if (water == null || !water.isAvailable()) {
			System.out.println("I(" + this.getID() + ") hit something.");
			this.hit(x, y);
		}
		if (anotherShip != null) {
			this.collision(anotherShip);
		}
	}

	public void collision(Ship anotherShip) {
		System.out.println("Ship(" + this.getID() + ") and Ship("
				+ anotherShip.getID() + ") have a Collision.");
		anotherShip.destroyed();
		this.destroyed();
	}

	public void hit(int x, int y) {
		Obstacle obs = new Obstacle(this.context, x, y);
		this.context.add(obs);
		// send messages to server
		this.sendMsgHit(obs);
		this.destroyed();
	}

	/**
	 * Ship moors at the harbor.
	 */
	public void moor() {
		System.out.println("I(" + this.getID() + ") is moored");
		// this.setSpeed(0);
		this.destroyed();
	}

	public void destroyed() {
		super.end();
	}

	/**
	 * 
	 * Change ship's Direction by angle
	 * 
	 * @param direction
	 *            changed angle (degree)
	 * @param anotherID
	 *            ID of another ship
	 */
	public void changeDirection(double direction, Ship another) {
		if (listID.contains(another.getID())) {
			return;
		}
		int m = 1;
		int m2 = 1;
		if (this.getType().getMaxSpeed() < 2) {
			m = 4;
		}
		double angle = Math.abs(this.getHeading() - another.getHeading());
		if (angle < 90 || angle > 270) {
			if (this.getSpeed() == another.getSpeed()) {
				m2 = 8;
			} else {
				m2 = 4 / (int) Math.abs(this.getSpeed() - another.getSpeed());
			}
		}
		int[] cc = { CHANGE_COUNT * m, CHANGE_COUNT * m2, CHANGE_COUNT * m };
		changeCount.add(cc);
		double[] cd = { -direction, -direction, direction };
		changeDirection.add(cd);
		listID.add(another.getID());
		this.changeDirection(direction);
	}

	/**
	 * When the ship is too close to the riverside, make it
	 */
	public void adjustPath() {
		if (this.getPosition().getY() > destiny.getY()) {
			;
		}
	}

	/**
	 * Change ship's Direction by angle
	 * 
	 * @param direction
	 *            changed angle (degree)
	 */
	public void changeDirection(double direction) {
		this.changeHeading(direction);
	}

	public String getID() {
		return ID;
	}

	/**
	 * Get the type of ship.
	 * 
	 * @return Type of ship.
	 */
	public TypeShip getType() {
		return this.type;
	}

	public void setType(TypeShip type) {
		this.type = type;
		super.setSpeed(type.getMaxSpeed());
	}

	public double getSize() {
		if (size == 0) {
			return type.getSize();
		}
		return size;
	}

	public void setSize(double size) {
		this.size = size;
	}

	public double getTotal_mass() {
		if (total_mass == 0) {
			return type.getMass();
		}
		return total_mass;
	}

	public void setTotal_mass(double total_mass) {
		this.total_mass = total_mass;
	}

	public double getSpeed() {
		if (super.getSpeed() == 0) {
			return type.getMaxSpeed();
		}
		return super.getSpeed();
	}

	public double getSafeDistance() {
		double sd = 0;
		sd = type.getSafeDistance();
		return sd;
	}

	public double getLandSafeDistance() {
		double sd = 0;
		sd = type.getSafeDistance() / 2;
		return sd;
	}

	public GridNode getPosition() {
		return this.position;
	}

	public GridNode getPreviousPosition() {
		return this.previousPos;
	}

	public GridNode getNextPosition() {
		return this.nextPos;
	}

	public List<String> getListID() {
		return listID;
	}

	public String toString() {
		String str = "Ship-" + this.getType().getTypeName();
		str += "(" + this.getID() + "): "; // id
		str += position.toString();// location and direction
		return str;
	}

	private int status = RUNNING;
	public static final int RUNNING = 1;
	public static final int DIE = 0;
	public static final int PAUSE = 2;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * CentralServer tells the Ship about the destiny.
	 * 
	 * @param destiny
	 */
	public void setDestiny(GridNode destiny) {
		this.destiny = destiny;
	}

	/**
	 * Send message about hitting to server.
	 */
	public void sendMsgHit(Obstacle obs) {
		server.getListObs().add(obs);
		server.getListShips().remove(this);
		System.out.println("Current number: " + server.getListShips().size());
	}

	/**
	 * Send message about collision to server.
	 */
	public void sendMsgCollision() {
		;
	}

}
