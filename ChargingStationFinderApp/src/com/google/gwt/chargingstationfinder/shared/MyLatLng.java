package com.google.gwt.chargingstationfinder.shared;

import java.io.Serializable;



public class MyLatLng implements Serializable{

	private double latitude;
	private double longitude;
	
	private MyLatLng() {
		
	}
	
	public MyLatLng(double lat, double lng) {
		this.latitude = lat;
		this.longitude = lng;
	}
	
	public double getLatitude() {
		return latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public void setAltitude(double longitude) {
		this.longitude = longitude;
	}
	
//	public static MyLatLng parseMyLatlng(LatLng pos) {
//		return new MyLatLng(pos.lat(), pos.lng());
//	}
//	
//	public LatLng convertToLatLng(MyLatLng pos) {
//		return LatLng.create(pos.getLatitude(), pos.getLongitude());
//	}
}
