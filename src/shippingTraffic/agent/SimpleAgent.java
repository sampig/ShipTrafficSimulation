package shippingTraffic.agent;

import repast.simphony.annotate.AgentAnnot;
import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;
import repast.simphony.util.ContextUtils;

/**
 * A simple class for all agents.
 * 
 * @author Chenfeng ZHU
 * 
 */
@AgentAnnot(displayName = "Agent")
public class SimpleAgent {
	private double speed = 1; // The default speed of the agent ()
	private double heading = 0; // The default direction of the agent (degree)

	/**
	 * Shedule the step method for agents. The method is scheduled starting at
	 * tick one with an interval of 1 tick. Specifically, the step starts at 1,
	 * and recurs at 2,3,4,...etc
	 */
	@ScheduledMethod(start = 1, interval = 1, shuffle = true)
	public void step() {
	}

	/**
	 * Move the agent be default.<br/>
	 * The agent is aware of its location in the continuous space andwhich patch
	 * it is on
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void move() {
		// Get the context in which the agent is residing
		Context context = ContextUtils.getContext(this);

		// Get the patch grid from the context
		Grid patch = (Grid) context.getProjection("Simple_Grid");

		// Get the continuous space from the context
		ContinuousSpace space = (ContinuousSpace) context
				.getProjection("Continuous_Space");

		// Get the agent's point coordinate from the space
		NdPoint point = space.getLocation(this);

		double x = point.getX(); // The x coordinate on the 2D continuous space
		double y = point.getY(); // The y coordinate on the 2D continuous space

		// Move the agent on the space by one unit according to its new heading
		space.moveByVector(this, speed, Math.toRadians(heading), 0, 0);

		// Move the agent to its new patch (the patch may not actually change)
		patch.moveTo(this, (int) x, (int) y);
	}

	// End the agent
	@SuppressWarnings("rawtypes")
	public void end() {
		// Get the context in which the agent resides.
		Context context = ContextUtils.getContext(this);
		// Remove the agent from the context if the context is not empty
		if (context.size() > 1) {
			context.remove(this);
		}
		// Otherwise if the context is empty, end the simulation
		else {
			RunEnvironment.getInstance().endRun();
		}
	}

	public int isShip() {
		return 0;
	}

	/**
	 * Get the direction.
	 * 
	 * @return
	 */
	public double getHeading() {
		return heading;
	}

	/**
	 * Set the direction.
	 * 
	 * @param heading
	 *            (degree)
	 */
	public void setHeading(double heading) {
		this.heading = heading;
	}

	public void changeHeading(double heading) {
		this.heading += heading;
		// this.heading = (this.heading + 360) % 360;
	}

	/**
	 * Get the speed.
	 * 
	 * @return
	 */
	public double getSpeed() {
		return speed;
	}

	/**
	 * Set the speed.
	 * 
	 * @param speed
	 */
	public void setSpeed(double speed) {
		this.speed = speed;
	}

}
