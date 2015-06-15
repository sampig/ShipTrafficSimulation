package shippingTraffic.agent;

import repast.simphony.annotate.AgentAnnot;
import repast.simphony.context.Context;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;
import shippingTraffic.util.GridNode;

@AgentAnnot(displayName = "Obstacle")
public class Obstacle extends SimpleAgent {

	// private Context<SimpleAgent> context;

	private GridNode position = new GridNode();
	private int delayDisappear = 5;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Obstacle(Context<SimpleAgent> context, int x, int y) {
		// this.context = context;
		context.add(this);
		Grid grid = (Grid) context.getProjection("Simple_Grid");
		ContinuousSpace space = (ContinuousSpace) context
				.getProjection("Continuous_Space");
		grid.moveTo(this, x, y);
		space.moveTo(this, x, y, 0);
		position.setX(x);
		position.setY(y);
		System.out.println("Obstacle appears: " + this.toString());
	}

	@Override
	public void step() {
		if (delayDisappear > 0) {
			delayDisappear--;
		} else if (delayDisappear == 0) {
			this.disappear();
		} else {
			// wait for clean.
		}
	}

	/**
	 * Set the delay time for the disappear of obstacles. If the value is
	 * negative, that means the obstacle will NOT disappear automatically and
	 * has to be cleaned manually.
	 * 
	 * @param delay
	 */
	public void setDelay(int delay) {
		this.delayDisappear = delay;
	}

	/**
	 * Obstacle is cleaned.
	 */
	public void disappear() {
		System.out.println("Obstacle has been cleaned.");
		super.end();
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(super.toString() + "");
		sb.append(position.toShortString());
		return sb.toString();
	}

}
