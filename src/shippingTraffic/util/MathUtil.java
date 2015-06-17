package shippingTraffic.util;

/**
 * A utility for mathematical calculation.
 * 
 * @author Chenfeng ZHU
 * 
 */
public class MathUtil {

	/**
	 * Calculate the geometric distance between node1 and node2.<br/>
	 * Equation: d=sqrt((x1-x2)^2+(y1-y2)^2)
	 * 
	 * @param n1
	 *            GridNode
	 * @param n2
	 *            GridNode
	 * @return the geometric distance
	 */
	public static double calDistance(GridNode n1, GridNode n2) {
		double d = 0;
		d = Math.sqrt((n1.getX() - n2.getX()) * (n1.getX() - n2.getX())
				+ (n1.getY() - n2.getY()) * (n1.getY() - n2.getY()));
		return d;
	}

	/**
	 * Calculate the practical distance between node1 and node2. Because the
	 * safe distance not only depends the distance between two nodes, but also
	 * is affected by the directions of two nodes. <br/>
	 * Equation:
	 * 
	 * @param n1
	 *            GridNode
	 * @param n2
	 *            GridNode
	 * @return the practical distance
	 */
	public static double calDistanceAdv(GridNode n1, GridNode n2) {
		double d = 0;
		d = Math.sqrt((n1.getX() - n2.getX()) * (n1.getX() - n2.getX())
				+ (n1.getY() - n2.getY()) * (n1.getY() - n2.getY()));
		if (Math.abs(n1.getDirection() - n2.getDirection()) >= 90) {
			;
		}
		return d;
	}

	/**
	 * 
	 * @param n1
	 *            previous point of ship1
	 * @param n2
	 *            current point of ship1
	 * @param n3
	 *            previous point of ship2
	 * @param n4
	 *            current point of ship2
	 * @return positive(safe), negative(dangerous), 0(no change)
	 */
	public static int checkSafeDistance(GridNode n1, GridNode n2, GridNode n3,
			GridNode n4) {
		if (n1 == null || n2 == null || n3 == null | n4 == null) {
			return 1;
		}
		int x1 = n1.getX();
		int y1 = n1.getY();
		int x2 = n2.getX();
		int y2 = n2.getY();
		int x3 = n3.getX();
		int y3 = n3.getY();
		int x4 = n4.getX();
		int y4 = n4.getY();
		// previous distance
		double l13 = Math.sqrt((x3 - x1) * (x3 - x1) + (y3 - y1) * (y3 - y1));
		// current distance
		double l24 = Math.sqrt((x4 - x2) * (x4 - x2) + (y4 - y2) * (y4 - y2));
		if (l24 > l13) {
			return 1;
		} else if (l24 == l13) {
			return 0;
		}
		return -1;
	}

	/**
	 * Check whether there will be an intersection of paths of two ships. The
	 * path of ship1 is line12 (n1,n2); the path of ship2 is line34(n3,n4).
	 * 
	 * @param n1
	 *            previous point of ship1
	 * @param n2
	 *            current point of ship1
	 * @param n3
	 *            previous point of ship2
	 * @param n4
	 *            current point of ship2
	 * @return true if there is an intersection false if there is no
	 *         intersections
	 */
	public static boolean calIntersection(GridNode n1, GridNode n2,
			GridNode n3, GridNode n4, double r) {
		if (n1 == null || n2 == null || n3 == null | n4 == null) {
			return false;
		}
		int x1 = n1.getX();
		int y1 = n1.getY();
		int x2 = n2.getX();
		int y2 = n2.getY();
		int x3 = n3.getX();
		int y3 = n3.getY();
		int x4 = n4.getX();
		int y4 = n4.getY();
		// two ships arrived at the same point at the same time
		if (x2 == x4 && y2 == y4) {
			return true;
		}
		int i = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
		// the two paths are partly coincided if two ships move parallelly
		if (i == 0) {
			if (checkPointInLine(n1, n3, n4) || checkPointInLine(n2, n3, n4)) {
				return true;
			} else if (calDistance(n1, n3) <= r || calDistance(n2, n4) <= r) {
				return true;
			} else {
				return false;
			}
		}
		// there is an intersection of two paths otherwise.
		double xi = ((x1 * y2 - y1 * x2) * (x3 - x4) - (x1 - x2)
				* (x3 * y4 - y3 * x4))
				/ i;
		double yi = ((x1 * y2 - y1 * x2) * (y3 - y4) - (y1 - y2)
				* (x3 * y4 - y3 * x4))
				/ i;
		if (checkPointInLine(xi, yi, x1, y1, x2, y2)) {
			return true;
		} else if (calDistance(n1, n3) <= r || calDistance(n2, n4) <= r) {
			return true;
		}
		return false;
	}

	/**
	 * Check whether the point n1 is in Line(n2,n3).
	 * 
	 * @param n1
	 *            the point
	 * @param n2
	 *            one endpoint of the line
	 * @param n3
	 *            the other endpoint of the line
	 * @return
	 */
	public static boolean checkPointInLine(GridNode n1, GridNode n2, GridNode n3) {
		int x1 = n1.getX();
		int y1 = n1.getY();
		int x2 = n2.getX();
		int y2 = n2.getY();
		int x3 = n3.getX();
		int y3 = n3.getY();
		double l12 = Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
		double l13 = Math.sqrt((x3 - x1) * (x3 - x1) + (y3 - y1) * (y3 - y1));
		double l23 = Math.sqrt((x3 - x2) * (x3 - x2) + (y3 - y2) * (y3 - y2));
		double diff = Math.abs(l23 - (l12 + l13));
		if (diff < 0.001) {
			return true;
		}
		return false;
	}

	/**
	 * Check whether the point n1(x1,y1) is in Line(n2(x2,y2), n3(x3,y3)).
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param x3
	 * @param y3
	 * @return
	 */
	public static boolean checkPointInLine(double x1, double y1, double x2,
			double y2, double x3, double y3) {
		double l12 = Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
		double l13 = Math.sqrt((x3 - x1) * (x3 - x1) + (y3 - y1) * (y3 - y1));
		double l23 = Math.sqrt((x3 - x2) * (x3 - x2) + (y3 - y2) * (y3 - y2));
		double diff = Math.abs(l23 - (l12 + l13));
		if (diff < 0.001) {
			return true;
		}
		return false;
	}

	/**
	 * Compare the direction of Ship1 and the direction for Ship2 to Ship1.
	 * Decide that Ship1 should turn left or right.
	 * 
	 * @param n1
	 *            Position of Ship1
	 * @param n2
	 *            Position of Ship2
	 * @return false(left) true(right)
	 * 
	 */
	public static boolean chooseDirection(GridNode n1, GridNode n2) {
		int d = n1.getDirection();
		double d2 = Math.toDegrees(Math.atan2((n2.getY() - n1.getY()),
				(n2.getX() - n1.getX())));
		if (d2 > d) {
			// System.out.println("RIGHT---d: " + d + ", d2: " + d2 + ", " + n1
			// + ", " + n2);
			return true;
		}
		// System.out.println("LEFT---d: " + d + ", d2: " + d2 + ", " + n1 +
		// ", " + n2);
		return false;
	}

}
