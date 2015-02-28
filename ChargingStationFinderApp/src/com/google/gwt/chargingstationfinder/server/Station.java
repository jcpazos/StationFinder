package com.google.gwt.chargingstationfinder.server;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.users.User;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Station {
	
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long id;
	
	@Persistent
	private User user;
	@Persistent
	private double latitude;
	@Persistent
	private double longitude;
	@Persistent
	private String address;
	@Persistent
	private String operator;
	
	public Station(double latitude, double longitude,String operator, String address, User u) {
		this.address = address;
		this.latitude = latitude;
		this.longitude = longitude;
		this.operator = operator;
		this.user = u;
	}
	
	public Long getId() {
		return this.id;
	}

	public User getUser() {
		return this.user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}

	public Object getAddress() {
		return address;
	}

}
