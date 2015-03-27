package com.google.gwt.chargingstationfinder.client;

import java.util.List;

import com.google.appengine.api.users.User;
import com.google.gwt.chargingstationfinder.shared.Review;
import com.google.gwt.chargingstationfinder.shared.Station;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.maps.gwt.client.LatLng;

@RemoteServiceRelativePath("station")
public interface StationService extends RemoteService {
  public void addStation(Station s) throws NotLoggedInException, NotAdminException;
  public void removeStation(String address) throws NotLoggedInException;
  public List<Station> getStations() throws NotLoggedInException;
  public void checkIsAdmin() throws NotAdminException;
  public void updateStation(Station s);
}
