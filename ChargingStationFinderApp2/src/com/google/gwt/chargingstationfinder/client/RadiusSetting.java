package com.google.gwt.chargingstationfinder.client;

public enum RadiusSetting {
	
	// Add more options for distance here
    ONE_HUNDRED (100),
    TW0_HUNDREDS (200);
   
    private final double radius;   // in meters
    
    RadiusSetting(double radius) {
        this.radius = radius;
    }
    private double radius() {
    	return radius;
    }
}
