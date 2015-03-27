package com.google.gwt.chargingstationfinder.client;

import com.google.gwt.chargingstationfinder.shared.Review;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("review")
public interface ReviewService extends RemoteService {
	public void postReview(Review review) throws NotLoggedInException;
	public Review[] getReview();

}
