package shippingTraffic.util;

/**
 * Define a node to store the position and direction of agents.
 * 
 * @author Chenfeng ZHU
 * 
 */
public class GridNode {

	private int x = 0;
	private int y = 0;
	private int direction = 0; // degree

	public GridNode() {
		this.x = 0;
		this.y = 0;
		this.direction = 0;
	}

	/**
	 * Construction with x and y.
	 * 
	 * @param x
	 *            axis_x
	 * @param y
	 *            axis_y
	 */
	public GridNode(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Construction with x, y and direction.
	 * 
	 * @param x
	 *            axis_x
	 * @param y
	 *            axis_y
	 * @param d
	 *            direction(degree)
	 */
	public GridNode(int x, int y, int d) {
		this.x = x;
		this.y = y;
		this.direction = d;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	/**
	 * Copy the values of node.
	 * 
	 * @param node
	 */
	public void copy(GridNode node) {
		this.x = node.getX();
		this.y = node.getY();
		this.direction = node.getDirection();
	}

	public String toString() {
		String str = "(" + x + ", " + y + ", " + direction + ")";
		return str;
	}

	public String toShortString() {
		String str = "(" + x + ", " + y + ")";
		return str;
	}

}
