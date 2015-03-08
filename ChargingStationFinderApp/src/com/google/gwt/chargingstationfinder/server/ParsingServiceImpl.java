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

import com.google.gwt.chargingstationfinder.client.ParsingService;
import com.google.gwt.chargingstationfinder.client.StationService;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class ParsingServiceImpl extends RemoteServiceServlet implements
ParsingService {
	
	String[][] stations;
	//final static String myURL = "http://pastebin.com/raw.php?i=KvKTX3gA";

	@Override
	public String[][] parseData(String myURL) {
		
		InputStream is = null;
		String[] station;
		stations = new String[23][4];
		try {
			URL url = new URL (myURL);
			URLConnection urlc = url.openConnection();
			is = urlc.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line = br.readLine();
			station = line.split(",");
			if (!isValidInput(station)) return null;
			int i =0;
			while ((line = br.readLine()) != null) {

				station = line.split(",");
				stations[i] = station;
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
	
	

}
