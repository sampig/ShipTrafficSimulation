package shippingTraffic.util;

/**
 * 
 * @author Chenfeng ZHU
 * 
 * @see MapCreator
 * @see ShipCreator
 * 
 */
public class SimulationEnv {

	// Controller:
	private MapCreator mapCreator = new MapCreator();
	private ShipCreator shipCreator = new ShipCreator();
	private WeatherController weatherController = new WeatherController();

	public MapCreator getMapCreator() {
		return mapCreator;
	}

	public ShipCreator getShipCreator() {
		return shipCreator;
	}

	public WeatherController getWeatherController() {
		return weatherController;
	}

}
