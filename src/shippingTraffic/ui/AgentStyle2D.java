package shippingTraffic.ui;

import java.awt.Color;

import repast.simphony.visualizationOGL2D.DefaultStyleOGL2D;
import shippingTraffic.agent.Obstacle;
import shippingTraffic.agent.CentralServer;
import shippingTraffic.agent.Ship;
import shippingTraffic.agent.Water;

/**
 * Define the color and size for the agents.
 * 
 * @author Chenfeng ZHU
 * 
 */
public class AgentStyle2D extends DefaultStyleOGL2D {

	@Override
	public Color getColor(Object o) {
		if (o instanceof Ship) {
			return Color.LIGHT_GRAY;
		} else if (o instanceof Obstacle) {
			return Color.RED;
		} else if (o instanceof Water) {
			return Color.BLUE;
		} else if (o instanceof CentralServer) {
			return Color.BLACK;
		}
		return null;
	}

	@Override
	public float getScale(Object o) {
		if (o instanceof Ship) {
			return (float) ((Ship) o).getSize();
		} else if (o instanceof Obstacle) {
			return 20f;
		} else if (o instanceof Water) {
			return 1f;
		} else if (o instanceof CentralServer) {
			return (float) CentralServer.SIZE;
		}
		return 1f;
	}
}