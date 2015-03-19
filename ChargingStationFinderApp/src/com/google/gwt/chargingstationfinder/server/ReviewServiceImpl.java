package com.google.gwt.chargingstationfinder.server;

import com.google.gwt.chargingstationfinder.client.NotLoggedInException;
import com.google.gwt.chargingstationfinder.client.ReviewService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

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
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class ReviewServiceImpl extends RemoteServiceServlet implements
ReviewService {

	private static final Logger LOG = Logger.getLogger(ReviewServiceImpl.class.getName());
	private static final PersistenceManagerFactory PMF =
			JDOHelper.getPersistenceManagerFactory("transactions-optional");

	public void postReview(Review review) throws NotLoggedInException {
		checkLoggedIn();
		PersistenceManager pm = getPersistenceManager();
		try {
			review.setUser(getUser());
			pm.makePersistent(review);
		} finally {
			pm.close();
		}
	}

	public Review[] getReview() {
	    PersistenceManager pm = getPersistenceManager();
	    Review[] reviews = null;
	    try {
	      Query q = pm.newQuery(Review.class);
	      q.setOrdering("createDate");
	      reviews = (Review[]) q.execute();
	    } finally {
	      pm.close();
	    }
	    return reviews;
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
}
