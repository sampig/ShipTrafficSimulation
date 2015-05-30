package shippingTraffic.ui;

import java.awt.Color;

import repast.simphony.valueLayer.ValueLayer;
import repast.simphony.visualizationOGL2D.ValueLayerStyleOGL;

/**
 * Style for Water value layer in 2D display.
 * 
 * @author Chenfeng ZHU
 */
public class WaterStyle2D implements ValueLayerStyleOGL {

	protected ValueLayer layer;

	public void init(ValueLayer layer) {
		this.layer = layer;
	}

	public float getCellSize() {
		return 15.0f;
	}

	/**
	 * @return the color based on the value at given coordinates.
	 */
	public Color getColor(double... coordinates) {
		double value = layer.get(coordinates);
		if (value == 1) {
			return Color.BLUE;
		} else {
			return new Color(0, 250, 0);
		}
	}
}