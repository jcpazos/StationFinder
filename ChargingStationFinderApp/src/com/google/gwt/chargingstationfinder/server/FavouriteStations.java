package com.google.gwt.chargingstationfinder.server;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.users.User;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class FavouriteStations extends Station {
	
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long id;
	
	@Persistent
	private User user;
	
	public FavouriteStations(double latitude, double longitude,String operator, String address, User user) {
		super(latitude, longitude, operator, address);
		this.user = user;
		
	}
	
	public User getUser() {
		return user;
	}

}
