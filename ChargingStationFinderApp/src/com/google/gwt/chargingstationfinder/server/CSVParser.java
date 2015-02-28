package com.google.gwt.chargingstationfinder.server;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;




public class CSVParser {
	
	final static String myURL = "ftp://webftp.vancouver.ca/OpenData/csv/electric_vehicle_charging_stations.csv";
	
	
	public static void main(String[] args) {
		 
		CSVParser parser = new CSVParser();
		parser.run();
	 
	  }
	
	public void run() {
		
		InputStream is = null;
		ArrayList<Station> stations = new ArrayList<Station>();
		String[] station;
		
		try {
			URL url = new URL (myURL);
			URLConnection urlc = url.openConnection();
			is = urlc.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line = br.readLine();
			
			while ((line = br.readLine()) != null) {
			
			station = line.split(",");
			/*stations.add(new Station(Double.parseDouble(station[0]), Double.parseDouble(station[1]),
					station[2], station[3]));*/
			}
			
			for (Station s: stations) System.out.println(s.toString());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
