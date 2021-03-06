package shippingTraffic.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A creator used to create maps.
 * 
 * @author Chenfeng ZHU
 * 
 */
public class MapCreator {

	public final static int SIZE_X = 400;
	public final static int SIZE_Y = 200;

	public final static int START_PADDING_X = 2;
	public final static int START_PADDING_Y = 2;

	private List<GridNode> water = new ArrayList<>(0);
	private List<GridNode> startNodes = new ArrayList<>(0);

	// these points are used in Ocean.
	private int line_x1;
	private int line_x2;
	private int river_y1, river_y2;
	private GridNode destiny = new GridNode();

	public MapCreator() {
		;
	}

	/**
	 * Create a simple and straight river. Store all the nodes of the water. THe
	 * width of the river is SIZE_Y * 0.4.
	 */
	public void createSimpleStraightRiver() {
		GridNode node = new GridNode();
		int y1 = SIZE_Y * 3 / 10;
		int y2 = SIZE_Y * 7 / 10;
		// Create a river.
		for (int i = 0; i < SIZE_X; i++) {
			for (int j = y1; j < y2; j++) {
				node = new GridNode(i, j);
				water.add(node);
			}
		}
		// Create start nodes.
		node = new GridNode(START_PADDING_X, SIZE_Y / 2, 0);
		startNodes.add(node);
		node = new GridNode(SIZE_X - START_PADDING_X, SIZE_Y / 2, 180);
		startNodes.add(node);
		System.out.println("Create a simple and straight river.");
	}

	/**
	 * Create a map of an Ocean.<br/>
	 * --------<br/>
	 * |~~~~~~~\<br/>
	 * |~~~~~~~~\<br/>
	 * |~~~~~~~~~\<br/>
	 * |~~~~~~~~~~---| y2<br/>
	 * |~~~~~~~~~~~~~|<br/>
	 * |~~~~~~~------| y1<br/>
	 * |~~~~~~~|<br/>
	 * --------x1 x2<br/>
	 */
	public void createOcean() {
		GridNode node = new GridNode();
		// --------
		// |~~~~~~~\
		// |~~~~~~~~\
		// |~~~~~~~~~\
		// |~~~~~~~~~~---| y2
		// |~~~~~~~~~~~~~|
		// |~~~~~~~------| y1
		// |~~~~~~~|
		// --------x1 x2
		int x1 = SIZE_X * 6 / 10;
		int x2 = SIZE_X * 8 / 10;
		int y1 = SIZE_Y * 2 / 10;
		int y2 = SIZE_Y * 4 / 10;
		// Create an ocean.
		for (int i = 0; i < x1; i++) {
			for (int j = 0; j < SIZE_Y; j++) {
				node = new GridNode(i, j);
				water.add(node);
			}
		}
		for (int i = x1; i < x2; i++) {
			for (int j = y1; j < SIZE_Y - ((i - x1) * 3 / 2); j++) {
				node = new GridNode(i, j);
				water.add(node);
			}
		}
		for (int i = x2; i < SIZE_X; i++) {
			for (int j = y1; j < y2; j++) {
				node = new GridNode(i, j);
				water.add(node);
			}
		}
		// Create start nodes.
		for (int j = SIZE_Y / 10; j < SIZE_Y * 9 / 10; j++) {
			node = new GridNode(START_PADDING_X, j);
			startNodes.add(node);
		}
		// Record some key positions.
		this.line_x1 = x1;
		this.line_x2 = x2;
		this.river_y1 = y1;
		this.river_y2 = y2;
		// define the destiny.
		this.destiny = new GridNode(SIZE_X - 10, (y1 + y2) / 2);
		System.out.println("Create an ocean.");
	}

	public void resetStartNodes(int type) {
		Random r = new Random();
		this.clearStartNodes();
		if (type == SimulationKind.SIMPLE_COLLISION_2) {
			// for the simple collision 2
			this.setStartNodesCollision();
		} else if (type == SimulationKind.OCEAN_TO_RIVER_2) {
			// for the entrance without avoidance of collision
			this.addStartNode(new GridNode(START_PADDING_X, 25, 0));
			this.addStartNode(new GridNode(START_PADDING_X, 150, 0));
		} else if (type == SimulationKind.OCEAN_TO_RIVER_3) {
			// for the advanced entrance
			this.addStartNode(new GridNode(START_PADDING_X, 25, r.nextInt(45))); // 0~45
			this.addStartNode(new GridNode(START_PADDING_X, 150, 0 - r
					.nextInt(45))); // -45~0
		} else if (type == SimulationKind.OCEAN_TO_RIVER_4) {
			this.setStartNodesAdvanced2();
		}
	}

	public void setStartNodesCollision() {
		this.clearStartNodes();
		GridNode node = new GridNode(START_PADDING_X, SIZE_Y / 2 + 1, 0);
		startNodes.add(node);
		node = new GridNode(SIZE_X - START_PADDING_X, SIZE_Y / 2 + 1, 180);
		startNodes.add(node);
		node = new GridNode(START_PADDING_X, SIZE_Y / 2 - 1, 0);
		startNodes.add(node);
		node = new GridNode(SIZE_X - START_PADDING_X, SIZE_Y / 2 - 1, 180);
		startNodes.add(node);
	}

	public void setStartNodesAdvanced2() {
		this.clearStartNodes();
		GridNode node = new GridNode(START_PADDING_X, 31, 7);
		startNodes.add(node);
		node = new GridNode(START_PADDING_X, 151, -21);
		startNodes.add(node);
	}

	public void setTestStartNodes() {
		this.clearStartNodes();
		GridNode node = new GridNode(START_PADDING_X, 106, 0);
		startNodes.add(node);
		node = new GridNode(START_PADDING_X, 92, 0);
		startNodes.add(node);
	}

	public List<GridNode> getWater() {
		return this.water;
	}

	public List<GridNode> getStartNodes() {
		return this.startNodes;
	}

	public void clearStartNodes() {
		this.startNodes.clear();
	}

	public void changeStartNodes(List<GridNode> startNodes) {
		this.startNodes = startNodes;
	}

	public void addStartNode(GridNode node) {
		this.startNodes.add(node);
	}

	public GridNode getDestiny() {
		return destiny;
	}

	public void setDestiny(GridNode destiny) {
		this.destiny = destiny;
	}

	public int getLineX1() {
		return this.line_x1;
	}

	public int getLineX2() {
		return this.line_x2;
	}

	public int getRiverY1() {
		return this.river_y1;
	}

	public int getRiverY2() {
		return this.river_y2;
	}

}
