package com.google.gwt.chargingstationfinder.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.users.User;
import com.google.gwt.chargingstationfinder.client.InvalidReviewException;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable="true")
public class Station implements Serializable{
	
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long id;
	@Persistent
	private double lat;
	@Persistent
	private double lng;
	@Persistent
	private String address;
	@Persistent
	private String operator;
	@Persistent
	private ArrayList<String> comments;
	@Persistent
	private ArrayList<Integer> ratings;
	@Persistent
	private ArrayList<Date> dates;
	@Persistent
	private HashSet<String> favouriteUsers;
	@Persistent
	private ArrayList<String> userNames;
	
	public Station() {
	}
	
	public Station(MyLatLng position,String operator, String address) {
		setPosition(position);
		setAddress(address);
		setOperator(operator);
		comments = new ArrayList<String>();
		ratings = new ArrayList<Integer>();
		dates = new ArrayList<Date>();
		userNames = new ArrayList<String>();
		favouriteUsers = new HashSet<String>();
		id = (long) this.hashCode();
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public Long getId() {
		return this.id;
	}

	public MyLatLng getPosition() {
		return new MyLatLng(lat, lng);
	}

	public void setPosition(MyLatLng position) {
		this.lat = position.getLatitude();
		this.lng = position.getLongitude();
	}

	public String getAddress() {
		return address;
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

	public void addReview(Review r) throws InvalidReviewException {
		if (r.getRating() < 1 || r.getRating() > 5) {
			throw new InvalidReviewException("Please select a rating for this stating.");
		}else if (r.getComment().isEmpty() || r.getComment().length() > 200) {
			throw new InvalidReviewException("Comment should be within 200 characters");
		}else if (userNames.contains(r.getUserName())) {
			throw new InvalidReviewException("You can only review a station once; However, "
					+ "You may edit your previous review by clicking the edit link beside it.");
		}else {
			comments.add(r.getComment());
			ratings.add(r.getRating());
			dates.add(r.getDate());
			userNames.add(r.getUserName());
		}
	}
	
	public void removeReview(Review r) {
		for (int i = 0; i < userNames.size(); i++) {
			if (userNames.get(i).equals(r.getUserName())) {
				userNames.remove(i);
				comments.remove(i);
				dates.remove(i);
				ratings.remove(i);
				break;
			}
		}
	}
	
	public ArrayList<Review> getReviews() {
		ArrayList<Review> reviews = new ArrayList<Review>();
		for (int i = 0; i < comments.size(); i++) {
			Review r = new Review(ratings.get(i), comments.get(i));
			r.setUserName(userNames.get(i));
			r.setDate(dates.get(i));
			reviews.add(r);
		}
		return reviews;
	}
	
	public ArrayList<String> getComments() {
		return comments;
	}

	public ArrayList<Integer> getRatings() {
		return ratings;
	}

	public ArrayList<Date> getDates() {
		return dates;
	}
	
	public HashSet<String> getFavouriteUsers() {
		return this.favouriteUsers;
	}

	public void setComments(ArrayList<String> comments) {
		this.comments = comments;
	}

	public void setRatings(ArrayList<Integer> ratings) {
		this.ratings = ratings;
	}

	public void setDates(ArrayList<Date> dates) {
		this.dates = dates;
	}


	public ArrayList<String> getUserNames() {
		return userNames;
	}

	public void setUserNames(ArrayList<String> userNames) {
		this.userNames = userNames;
	}

	public void addFavouriteUser(String result) {
		favouriteUsers.add(result);
	}

	public void removeFavouriteUser(String result) {
		favouriteUsers.remove(result);
	}

}
