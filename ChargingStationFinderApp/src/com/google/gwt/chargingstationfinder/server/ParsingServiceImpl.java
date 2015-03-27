package com.google.gwt.chargingstationfinder.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.chargingstationfinder.client.LatLngConverter;
import com.google.gwt.chargingstationfinder.client.ParsingService;
import com.google.gwt.chargingstationfinder.client.StationService;
import com.google.gwt.chargingstationfinder.shared.MyLatLng;
import com.google.gwt.chargingstationfinder.shared.Station;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.maps.gwt.client.LatLng;

public class ParsingServiceImpl extends RemoteServiceServlet implements
ParsingService {
	
	List<Station> stations = new ArrayList<Station>();
	//final static String myURL = "http://pastebin.com/raw.php?i=KvKTX3gA";

	@Override
	public List<Station> parseData(String myURL) {
		
		InputStream is = null;
		String[] stationInfo;
		try {
			URL url = new URL (myURL);
			URLConnection urlc = url.openConnection();
			is = urlc.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line = br.readLine();
			stationInfo = line.split(",");
			if (!isValidInput(stationInfo)) return null;
			int i =0;
			while ((line = br.readLine()) != null) {

				stationInfo = line.split(",");
				stations.add(toStation(stationInfo));
				i++;
			}
		} catch (IOException e) {
			return null;
		}
		return stations;
	}
	private boolean isValidInput(String[] station) {
		if (station[0].equals("LATITUDE") && station[1].equals("LONGITUDE") && station[2].equals("LOT_OPERATOR")
				&& station[3].equals("ADDRESS")) return true;
		return false;
	}
	
	private Station toStation(String[] s) {
		MyLatLng pos = new MyLatLng(Double.parseDouble(s[0]), Double.parseDouble(s[1]));
		return new Station(pos, s[2], s[3]);
	}
}
