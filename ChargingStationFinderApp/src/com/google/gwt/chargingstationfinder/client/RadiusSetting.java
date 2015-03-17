package com.google.gwt.chargingstationfinder.client;

public enum RadiusSetting {
	
	// Add more options for distance here
    FIVE_HUNDRED (500),
    EIGHT_HUNDREDS (800),
    ONE_KM (1000),
    TWO_KM (2000),
    FIVE_KM (5000),
    TEN_KM (10000);
   
    private final double radius;   // in meters
    
    RadiusSetting(double radius) {
        this.radius = radius;
    }
    public double radius() {
    	return radius;
    }
}
