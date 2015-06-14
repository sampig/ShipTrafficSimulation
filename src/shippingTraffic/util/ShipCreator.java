package shippingTraffic.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A Ship Creator.
 * 
 * @author Chenfeng ZHU
 * 
 */
public class ShipCreator {

	private List<GridNode> list = new ArrayList<>(0);

	public ShipCreator() {
		;
	}

	/**
	 * Create 2 ships.
	 */
	public void createTwoShips() {
		GridNode node = new GridNode();
		int x = MapCreator.SIZE_X;
		int y = MapCreator.SIZE_Y;
		node = new GridNode(1, y / 2, 0);
		list.add(node);
		node = new GridNode(x - 1, y / 2, 180);
		list.add(node);
	}

	/**
	 * Create 2 ships randomly.
	 */
	public void createTwoRandomShips() {
		Random r = new Random();
		GridNode node = new GridNode();
		int x = MapCreator.SIZE_X;
		int y = MapCreator.SIZE_Y;
		node = new GridNode(1, y / 2 + (5 - r.nextInt(10)), 0);
		list.add(node);
		node = new GridNode(x - 1, y / 2 + (5 - r.nextInt(10)), 180);
		list.add(node);
	}

	/**
	 * Create ships.
	 * 
	 * @param num
	 *            number of ships.
	 */
	public void createShips(int num) {
		Random r = new Random();
		for (int i = 0; i < num; i++) {
			GridNode node = new GridNode(0, r.nextInt());
			list.add(node);
		}
	}

	/**
	 * Create ships.
	 * 
	 * @param num
	 *            number of ships.
	 * @param starts
	 *            starting point.
	 */
	public void createShips(int num, List<GridNode> starts) {
		Random r = new Random();
		list.clear();
		for (int i = 0; i < num; i++) {
			// GridNode node = new GridNode(0, r.nextInt(10));
			GridNode node = starts.get(r.nextInt(starts.size()));
			while (list.contains(node)) {
				node = starts.get(r.nextInt(starts.size()));
			}
			list.add(node);
		}
	}

	public List<GridNode> getList() {
		return list;
	}

}
