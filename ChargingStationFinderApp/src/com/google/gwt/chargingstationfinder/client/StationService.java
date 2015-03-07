package com.google.gwt.chargingstationfinder.client;

import com.google.appengine.api.users.User;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.maps.gwt.client.LatLng;

@RemoteServiceRelativePath("station")
public interface StationService extends RemoteService {
  public void addStation(double latitude, double longitude,String operator, String address) throws NotLoggedInException, NotAdminException;
  public void removeStation(String address) throws NotLoggedInException;
  public String[][] getStations() throws NotLoggedInException;
  public void checkIsAdmin() throws NotAdminException;
}
