package com.google.gwt.chargingstationfinder.client;

public class Setting {

	RadiusSetting radius;
	public Setting() {
		this.radius = RadiusSetting.ONE_KM;    // Set the default radius to 1 km
	}
	public void setRadius(RadiusSetting radius) {
		this.radius = radius;
		
	}
	
}
