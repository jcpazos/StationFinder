package com.google.gwt.chargingstationfinder.client;

public enum RadiusSetting {
	
// Add more options for distance here
    FIVE_HUNDRED (500, "500 m"),
    EIGHT_HUNDREDS (800, "800 m"),
    ONE_KM (1000, "1 km"),
    TWO_KM (2000, "2 km"),
    DEFAULT (5000, "5 km"),
    TEN_KM (10000, "10 km");
   
    private final double radius;   // in meters
    private final String name;
    
    RadiusSetting(double radius, String name) {
        this.radius = radius;
        this.name = name;
    }
    public double radius() {
    	return radius;
    }
    public String getName() {
    	return name;
    }
}
