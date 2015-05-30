package shippingTraffic.agent;

import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;
import repast.simphony.util.ContextUtils;
import repast.simphony.valueLayer.GridValueLayer;

/**
 * The Water Agent.
 * 
 * @author Chenfeng ZHU
 * 
 */
public class Water extends SimpleAgent {

	private boolean available = true; // boolean for water is available

	private static final int AVAILABLE = 1;
	private static final int OCCUPIED = 0;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Water(Context<SimpleAgent> context, int x, int y) {
		// add the water to the root context
		context.add(this);

		Grid grid = (Grid) context.getProjection("Simple_Grid");
		ContinuousSpace space = (ContinuousSpace) context
				.getProjection("Continuous_Space");

		// move the water to its position on the patch grid
		grid.moveTo(this, x, y);

		// and to its position on the continuous space
		space.moveTo(this, x, y, 0);

		GridValueLayer vl = (GridValueLayer) context
				.getValueLayer("Water_Field");

		vl.set((available ? AVAILABLE : OCCUPIED), grid.getLocation(this)
				.toIntArray(null));
	}

	@Override
	public void step() {
		// Get the probability
		Parameters p = RunEnvironment.getInstance().getParameters();
		int probObstacle = ((Double) p.getValue("probabilityObstacle"))
				.intValue();

		// If the water is not available
		// Randomly remove the obstacle from the water.
		if (!available) {
			if (probObstacle <= 0) {
				available = true;
				updateValueLayer();
			} else {
				// available = false;
			}
		}
	}

	/**
	 * The water is occupied by other agents.
	 */
	public void occupy() {
		available = false;
		updateValueLayer();
	}

	/**
	 * Update this value layer.
	 */
	@SuppressWarnings("rawtypes")
	private void updateValueLayer() {
		GridValueLayer vl = (GridValueLayer) ContextUtils.getContext(this)
				.getValueLayer("Water_Field");
		Grid grid = (Grid) ContextUtils.getContext(this).getProjection(
				"Simple_Grid");
		vl.set((available ? AVAILABLE : OCCUPIED), grid.getLocation(this)
				.toIntArray(null));
	}

	/**
	 * Check whether the position is available.
	 * 
	 * @return TRUE if available
	 */
	public boolean isAvailable() {
		return available;
	}

}
