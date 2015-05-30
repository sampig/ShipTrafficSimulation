package shippingTraffic.agent;

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

	private ContinuousSpace<Object> space;
	private Grid<Object> grid;

	private TypeShip type = new TypeShip(TypeShip.SHIP_2);
	private double size;
	private double total_mass;
	// private double speed = 1;
	private GridNode position = new GridNode();
	private GridNode previousPos = new GridNode();
	private GridNode nextPos = new GridNode();

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
		System.out.println("Create a " + this.toString() + ".");
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
			this.destroyed();
		}
		if (anotherShip != null) {
			// this.collision(anotherShip);
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
			this.destroyed();
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

	/**
	 * Ship moors at the harbor.
	 */
	public void moor() {
		this.setSpeed(0);
		System.out.println("I(" + this.getID() + ") is moored");
	}

	public void destroyed() {
		super.end();
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
			return type.getMaxSpeed() / 2;
		}
		return super.getSpeed();
	}

	public double getSafeDistance() {
		double sd = 0;
		sd = type.getSafeDistance();
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

}
