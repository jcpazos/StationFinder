package com.google.gwt.chargingstationfinder.client;

import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("parser")
public interface ParsingService extends RemoteService {
  public String[][] parseData(String url);
}