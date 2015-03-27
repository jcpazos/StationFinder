package com.google.gwt.chargingstationfinder.client;

import com.google.gwt.chargingstationfinder.shared.MyLatLng;
import com.google.maps.gwt.client.LatLng;

public class LatLngConverter {
	public static LatLng toLatLng(MyLatLng pos) {
		return LatLng.create(pos.getLatitude(), pos.getLongitude());
	}
	
	public static MyLatLng toMyLatLng(LatLng pos) {
		return new MyLatLng(pos.lat(), pos.lng());
	}
}
