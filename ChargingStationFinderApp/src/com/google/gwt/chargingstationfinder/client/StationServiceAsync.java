package com.google.gwt.chargingstationfinder.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.maps.gwt.client.LatLng;

public interface StationServiceAsync {
	
	public void addStation(double latitude, double longitude,String operator, String address,
			AsyncCallback<Void> async);
	public void removeStation(String address, AsyncCallback<Void> async);
	public void getStations(AsyncCallback<String[][]> async);
	public void checkIsAdmin(AsyncCallback<Void> async) throws NotAdminException;
	public void getFavouriteStations(AsyncCallback<String[][]> async) throws NotLoggedInException;
	  public void removeFavouriteStation(String address,AsyncCallback<Void> async) throws NotLoggedInException;
	  public void addFavouriteStation(double latitude, double longitude,String operator, String address,
			  AsyncCallback<Void> async) throws NotLoggedInException;

}
