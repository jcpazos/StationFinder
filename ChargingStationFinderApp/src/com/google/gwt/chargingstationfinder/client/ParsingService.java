package com.google.gwt.chargingstationfinder.client;

import java.util.List;

import com.google.gwt.chargingstationfinder.shared.Station;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("parser")
public interface ParsingService extends RemoteService {
  public List<Station> parseData(String url);
}