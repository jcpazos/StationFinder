package com.google.gwt.chargingstationfinder.client;

public class Setting {

	RadiusSetting radius;
	public Setting() {
		this.radius = RadiusSetting.DEFAULT;    // Set the default radius
	}
	public void setRadius(RadiusSetting r) {
		this.radius = r;
	}
}
