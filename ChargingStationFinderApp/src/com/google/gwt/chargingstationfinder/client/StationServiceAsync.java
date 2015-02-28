package com.google.gwt.chargingstationfinder.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface StationServiceAsync {
	
	public void addStation(double latitude, double longitude,String operator, String address,
			AsyncCallback<Void> async);
	public void removeStation(String address, AsyncCallback<Void> async);
	public void getStations(AsyncCallback<String[]> async);

}
