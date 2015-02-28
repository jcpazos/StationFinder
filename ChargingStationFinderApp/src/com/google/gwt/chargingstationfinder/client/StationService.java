package com.google.gwt.chargingstationfinder.client;

import com.google.appengine.api.users.User;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("station")
public interface StationService extends RemoteService {
  public void addStation(double latitude, double longitude,String operator, String address) throws NotLoggedInException, NotAdminException;
  public void removeStation(String address) throws NotLoggedInException;
  public String[] getStations() throws NotLoggedInException;
}
