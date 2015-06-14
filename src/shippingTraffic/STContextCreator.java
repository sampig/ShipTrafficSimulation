package shippingTraffic;

import repast.simphony.context.Context;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.space.continuous.RandomCartesianAdder;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.RandomGridAdder;
import repast.simphony.valueLayer.GridValueLayer;
import shippingTraffic.agent.CentralServer;
import shippingTraffic.agent.Ship;
import shippingTraffic.agent.SimpleAgent;
import shippingTraffic.agent.Water;
import shippingTraffic.util.GridNode;
import shippingTraffic.util.MapCreator;
import shippingTraffic.util.ShipCreator;
import shippingTraffic.util.TypeShip;

/**
 * Ship Traffic model on a continuous space.
 * 
 * @author Chenfeng ZHU
 */
public class STContextCreator implements ContextBuilder<SimpleAgent> {

	/**
	 * Build and return a context.<br/>
	 * Build a context consists of Layers and Agents. When this is called for
	 * the master context, the system will pass in a created context based on
	 * information given in the model file. When called for subcontexts, each
	 * subcontext that was added when the master context was built will be
	 * passed in.
	 * 
	 * @param context
	 * @return the built context.
	 */
	public Context<SimpleAgent> build(Context<SimpleAgent> context) {
		int xdim = MapCreator.SIZE_X; // The x dimension of the map
		int ydim = MapCreator.SIZE_Y; // The y dimension of the map

		// Create a 2D grid to model the discrete patches of water.
		// The inputs to the GridFactory include
		// the grid name, (defined in context.xml file.)
		// the context in which to place the grid,
		// and the grid parameters.
		// Grid parameters include
		// the border specification,
		// random adder for populating the grid with agents,
		// boolean for multiple occupancy,
		// and the dimensions of the grid.
		GridFactoryFinder.createGridFactory(null).createGrid(
				"Simple_Grid",
				context,
				new GridBuilderParameters<SimpleAgent>(
						new repast.simphony.space.grid.WrapAroundBorders(),
						new RandomGridAdder<SimpleAgent>(), true, xdim, ydim));

		// Create a 2D continuous space to model the map where the ships move.
		// The inputs to the Space Factory include
		// the space name, (defined in context.xml file.)
		// the context in which to place the space,
		// border specification,
		// random adder for populating the grid with agents,
		// and the dimensions of the grid.
		ContinuousSpaceFactoryFinder
				.createContinuousSpaceFactory(null)
				.createContinuousSpace(
						"Continuous_Space",
						context,
						new RandomCartesianAdder<SimpleAgent>(),
						new repast.simphony.space.continuous.WrapAroundBorders(),
						xdim, ydim, 1);

		// Create a 2D value layer to store the state of the water grid.
		// This is only used for visualization since it's faster to draw the
		// value layer in 2D displays compared with rendering each water patch
		// as an agent.
		GridValueLayer vl = new GridValueLayer("Water_Field", true,
				new repast.simphony.space.grid.WrapAroundBorders(), xdim, ydim);
		context.addValueLayer(vl);

		// The editable environment parameters in the GUI.
		Parameters p = RunEnvironment.getInstance().getParameters();

		// int simulationKind = p.getInteger("simulationKind");

		// Create the central Server.
		CentralServer server = new CentralServer(context);
		System.out.println("Start server: " + server.toString());
		server.startSimulation();

		// Populate the root context with the initial agents
		// Iterate over the number of ships
		ShipCreator ships = server.getShipCreator();
		for (GridNode node : ships.getList()) {
			Ship ship = new Ship(context, node.getX(), node.getY(),
					node.getDirection(), TypeShip.getRandomInstance());
			// ship.setHeading(node.getDirection());
			context.add(ship); // add the new ship to the root context
			server.getListShips().add(ship);
		}

		// Populate the patch grid with water
		// Iterate over the dimensions of the patch grid
		MapCreator map = server.getMapCreator(); // new MapCreator();
		// map.createSimpleStraightRiver(); // create a new map
		// map.createOcean();
		for (GridNode node : map.getWater()) {
			Water water = new Water(context, node.getX(), node.getY());
			water.toString();
		}

		// If running in batch mode, tell the scheduler when to end each run.
		if (RunEnvironment.getInstance().isBatch()) {
			double endAt = (Double) p.getValue("runLength");
			RunEnvironment.getInstance().endAt(endAt);
		}
		return context;
	}
}