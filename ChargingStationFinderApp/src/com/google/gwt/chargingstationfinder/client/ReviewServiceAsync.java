package com.google.gwt.chargingstationfinder.client;

import com.google.gwt.chargingstationfinder.server.Review;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ReviewServiceAsync {

	void postReview(Review review, AsyncCallback<Void> callback);

	void getReview(AsyncCallback<Review[]> callback);

}
