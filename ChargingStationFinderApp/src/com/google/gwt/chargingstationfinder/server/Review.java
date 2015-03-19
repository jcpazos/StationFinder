package com.google.gwt.chargingstationfinder.server;

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
	private User user;
	@Persistent
	private Date date;
	@Persistent
	private Station station;
	@Persistent
	private int rating;
	@Persistent
	private String comment;

	public Review(Station station, int rating, String comment) throws InvalidReviewException {
		if (rating < 1 || rating > 5) {
			throw new InvalidReviewException("Rating should be 1-5");
		}else if (comment.isEmpty() || comment.length() > 50) {
			throw new InvalidReviewException("Comment should contain 1-50 characters");
		}else {
			this.station = station;
			this.rating = rating;
			this.comment = comment;
		}
	}
	
	public Long getId() {
		return this.id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
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

	public Station getStation() {
		return station;
	}

}
