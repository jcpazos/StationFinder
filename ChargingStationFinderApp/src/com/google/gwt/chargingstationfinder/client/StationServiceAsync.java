package com.google.gwt.chargingstationfinder.client;

import java.util.List;

import com.google.gwt.chargingstationfinder.shared.Review;
import com.google.gwt.chargingstationfinder.shared.Station;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.maps.gwt.client.LatLng;

public interface StationServiceAsync {
	
	public void addStation(Station s, AsyncCallback<Void> async);
	public void removeStation(String address, AsyncCallback<Void> async);
	public void getStations(AsyncCallback<List<Station>> async);
	public void checkIsAdmin(AsyncCallback<Void> async) throws NotAdminException;
	public void getUserEmailAddress(AsyncCallback<String> async) throws NotLoggedInException;
	public void updateStation(Station selectedStation, AsyncCallback<Void> async);

}
