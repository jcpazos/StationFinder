package com.google.gwt.chargingstationfinder.server;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.chargingstationfinder.client.NotAdminException;
import com.google.gwt.chargingstationfinder.client.NotLoggedInException;
import com.google.gwt.chargingstationfinder.client.StationService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.maps.gwt.client.LatLng;

public class StationServiceImpl extends RemoteServiceServlet implements
StationService {
	
	private Logger logger = Logger.getLogger(StationServiceImpl.class.getName());
	  private static final PersistenceManagerFactory PMF =
	      JDOHelper.getPersistenceManagerFactory("transactions-optional");

	  public void addStation(double latitude, double longitude,String operator, String address) throws NotLoggedInException, NotAdminException {
	    checkLoggedIn();
	    checkIsAdmin();
	    PersistenceManager pm = getPersistenceManager();
	    try {
	      pm.makePersistent(new Station(latitude, longitude, operator, address));
	    } finally {
	      pm.close();
	    }
	  }


	public void removeStation(String address) throws NotLoggedInException {
	    checkLoggedIn();
	    PersistenceManager pm = getPersistenceManager();
	    try {
	      long deleteCount = 0;
	      Query q = pm.newQuery(Station.class, "user == u");
	      q.declareParameters("com.google.appengine.api.users.User u");
	      List<Station> stations = (List<Station>) q.execute(getUser());
	      for (Station station : stations) {
	        if (address.equals(station.getAddress())) {
	          deleteCount++;
	          pm.deletePersistent(station);
	        }
	      }
	      if (deleteCount != 1) {
	        logger.log(Level.WARNING, "removeStock deleted "+deleteCount+" Stocks");
	      }
	    } finally {
	      pm.close();
	    }
	  }

	  public String[][] getStations() throws NotLoggedInException {
	    checkLoggedIn();
	    PersistenceManager pm = getPersistenceManager();
	    String[][] returnVal = null;
	    try {
	      Query q = pm.newQuery(Station.class);
	      List<Station> stations = (List<Station>) q.execute();
	      int i=0;
	      if (stations.size() == 0) return null;
	      if (stations.get(0) == null) return null;
	      returnVal = new String[stations.size()][4];
	      for (Station station : stations) {
	        	  returnVal[i][0] = Double.toString(station.getLatitude());
	        	  returnVal[i][1] = Double.toString(station.getLongitude());
	        	  returnVal[i][2] = station.getOperator();
	        	  returnVal[i][3] = station.getAddress(); 
	        	  i++;
	          }
	    } finally {
	      pm.close();
	      
	    }
	    return returnVal;
	  }

	  private void checkLoggedIn() throws NotLoggedInException {
	    if (getUser() == null) {
	      throw new NotLoggedInException("Not logged in.");
	    }
	  }

	  private User getUser() {
	    UserService userService = UserServiceFactory.getUserService();
	    return userService.getCurrentUser();
	  }

	  private PersistenceManager getPersistenceManager() {
	    return PMF.getPersistenceManager();
	  }
	  
	  public void checkIsAdmin() throws NotAdminException {
			if (!UserServiceFactory.getUserService().isUserAdmin())
				throw new NotAdminException("User is not an admin");
			
		}

}
