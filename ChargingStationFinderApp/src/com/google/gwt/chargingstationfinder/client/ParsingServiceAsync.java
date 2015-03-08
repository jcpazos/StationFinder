package com.google.gwt.chargingstationfinder.client;

import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ParsingServiceAsync {
	
	public void parseData(String url, AsyncCallback<String[][]> async);

}
