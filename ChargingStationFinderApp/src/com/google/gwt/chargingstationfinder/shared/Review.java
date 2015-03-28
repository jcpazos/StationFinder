package com.google.gwt.chargingstationfinder.shared;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.users.User;
import com.google.gwt.chargingstationfinder.client.InvalidReviewException;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Review implements Serializable {

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long id;
	@Persistent
	private String user;
	@Persistent
	private Date date;
	@Persistent
	private int rating;
	@Persistent
	private String comment;

	public Review(int rating, String comment) {
		this.rating = rating;
		this.comment = comment;

	}

	public Long getId() {
		return this.id;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public Date getDate() {
		return date;
	}

	public int getRating() {
		return rating;
	}

	public String getComment() {
		return comment;
	}

	protected void setDate(Date date) {
		this.date = date;
	}

}
