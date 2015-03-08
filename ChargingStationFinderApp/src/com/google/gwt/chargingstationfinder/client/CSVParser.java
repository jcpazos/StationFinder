package com.google.gwt.chargingstationfinder.client;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.xml.sax.InputSource;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;



public class CSVParser {

	private String[][] stations;
	final static String myURL = "http://pastebin.com/raw.php?i=KvKTX3gA";
	//final static String fileLoc = "parsed/stations.txt"; 
	private Logger logger = Logger.getLogger(CSVParser.class.getName());
	ChargingStationFinderApp app;
	
	public CSVParser(ChargingStationFinderApp app) {
		this.stations = new String[23][4];
		this.app = app;
	}
	
	public void run(String[][] stations, String fileLoc) {
		try {
			sendRequest(fileLoc);
		} catch (RequestException e) {
			//logger.log(Level.SEVERE, e.getMessage());
		}
	}
	private void sendRequest(String fileLoc) throws RequestException{
		
		RequestBuilder rb = new RequestBuilder(RequestBuilder.GET, fileLoc);
		Request r= rb.sendRequest("", new RequestCallback() {
			@Override
			public void onResponseReceived(Request req, Response resp) {
				if (resp.getStatusCode() == 200) {	
					parseData(resp.getText());
				}  else {
					logger.log(Level.SEVERE, "HTTP error code " + 
							resp.getStatusCode() + ":" + 
							resp.getStatusText());
				}
			}

			@Override
			public void onError(Request res, Throwable throwable) {
				logger.log(Level.SEVERE, throwable.getMessage());
			}
		});
	}
	
	private void parseData(String text) {
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
}
