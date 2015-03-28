package com.google.gwt.chargingstationfinder.client;

import java.util.List;

import com.google.gwt.chargingstationfinder.shared.Station;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ParsingServiceAsync {
	
	public void parseData(String url, AsyncCallback<List<Station>> async);

}
