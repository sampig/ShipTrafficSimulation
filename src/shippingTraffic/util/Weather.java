package shippingTraffic.util;

/**
 * Define the values for different weather.
 * It is also a complex-type class for weather.
 * 
 * @author Chenfeng ZHU
 *
 */
public class Weather {

	public final static int SUNNY = 0;
	public final static int CLOUDY = 10;
	public final static int FOGGY = 15;
	public final static int RAINY = 20;
	public final static int STORM = 25;
	public final static int SNOWY = 30;

	private int weather = SUNNY;
	
	public Weather(int w) {
		this.weather = w;
	}

	/**
	 * Get the name of the weather.
	 * 
	 * @return name of weather
	 */
	public String getWeatherName() {
		String name = "";
		switch (weather) {
		case SUNNY:
			name="Sunny";
			break;
		case CLOUDY:
			name = "Cloudy";
			break;
		case FOGGY:
			name = "Foggy";
			break;
		case RAINY:
			name = "Rainy";
			break;
		case STORM:
			name = "Storm";
			break;
		case SNOWY:
			name = "Snow";
			break;
		default:
			break;
		}
		return name;
	}
	
	/**
	 * Change the weather.
	 * 
	 * @param new_weather the new weather
	 */
	public void changeWeather(int new_weather) {
		this.weather = new_weather;
	}

}
