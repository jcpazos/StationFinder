package com.google.gwt.chargingstationfinder.server;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.*;
import javax.persistence.Table;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Station {
	
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long id;
	@Persistent
	protected double latitude;
	@Persistent
	protected double longitude;
	@Persistent
	protected String address;
	@Persistent
	protected String operator;
	
	public Station(double latitude, double longitude,String operator, String address) {
		this.address = address;
		this.latitude = latitude;
		this.longitude = longitude;
		this.operator = operator;
	}
	
	public Long getId() {
		return this.id;
	}

	public String getAddress() {
		return address;
	}
	
	public double getLatitude() {
		return latitude;
	}
	
	public double getLongitude() {
		return longitude;
	}
	
	public String getOperator() {
		return operator;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		return result;
	}
	
	public String toString() {
		return (latitude +"," +longitude + "," + operator + "," + address);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Station other = (Station) obj;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		return true;
	}
	
	

}
