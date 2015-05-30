package shippingTraffic.util;


public class WeatherController {

	private Weather weather;

	public void init() {
		weather = new Weather(Weather.SUNNY);
	}
	
	public void setProbability() {
		;
	}
	
	public void changeWeather(int w) {
		weather.changeWeather(w);
	}

}
