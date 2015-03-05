package com.google.gwt.chargingstationfinder.client;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;



public class CSVParser {

	private String[][] stations;
	final static String myURL = "http://pastebin.com/raw.php?i=Re1qJKj3";
	final static String fileLoc = "parsed/stations.txt"; 
	private Logger logger = Logger.getLogger(CSVParser.class.getName());
	ChargingStationFinderApp app;
	
	public CSVParser(ChargingStationFinderApp app) {
		this.stations = new String[23][4];
		this.app = app;
	}
	
	public void run(String[][] stations) {
		try { 
			sendRequest();
		} catch (RequestException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
	}
	private void sendRequest() throws RequestException {
		Request r = new RequestBuilder(RequestBuilder.GET, fileLoc).sendRequest("", new RequestCallback() {
			@Override
			public void onResponseReceived(Request req, Response resp) {
				String text = resp.getText();
				String[] splitArray = text.split("\\r?\\n");
				ArrayList<String> splitStations = new ArrayList<String>(Arrays.asList(splitArray));
				splitStations.remove(0);
				int i = 0;
				for (String s: splitStations) {
					String[] station = s.split(",");
					for (int j=0; j<4;j++) {
						stations[i][j] = station[j];
					}
					app.addStation(stations[i]);
					i++;
				}
				app.addStations(stations);
			}

			@Override
			public void onError(Request res, Throwable throwable) {
				logger.log(Level.SEVERE, throwable.getMessage());
			}
		});
	}
}
