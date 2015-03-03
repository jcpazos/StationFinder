package com.google.gwt.chargingstationfinder.server;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.chargingstationfinder.client.NotAdminException;
import com.google.gwt.chargingstationfinder.client.NotLoggedInException;
import com.google.gwt.chargingstationfinder.client.StationService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class StationServiceImpl extends RemoteServiceServlet implements
StationService {
	
	private static final Logger LOG = Logger.getLogger(StationServiceImpl.class.getName());
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
	        LOG.log(Level.WARNING, "removeStock deleted "+deleteCount+" Stocks");
	      }
	    } finally {
	      pm.close();
	    }
	  }

	  public String[] getStations() throws NotLoggedInException {
	    checkLoggedIn();
	    PersistenceManager pm = getPersistenceManager();
	    List<String> symbols = new ArrayList<String>();
	    try {
	      Query q = pm.newQuery(Station.class, "user == u");
	      q.declareParameters("com.google.appengine.api.users.User u");
	      q.setOrdering("createDate");
	      List<Station> stations = (List<Station>) q.execute(getUser());
	      for (Station station : stations) {
	        //symbols.add(station.getSymbol());
	    	 //finish implementation for getStations
	      }
	    } finally {
	      pm.close();
	    }
	    return (String[]) symbols.toArray(new String[0]);
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
