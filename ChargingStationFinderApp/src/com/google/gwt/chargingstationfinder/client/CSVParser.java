package com.google.gwt.chargingstationfinder.client;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;




public class CSVParser {
	
	final static String myURL = "ftp://webftp.vancouver.ca/OpenData/csv/electric_vehicle_charging_stations.csv";
	
	
	public void run(String[][] stations) {
		
		InputStream is = null;
		String[] station;
		
		try {
			URL url = new URL (myURL);
			URLConnection urlc = url.openConnection();
			is = urlc.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line = br.readLine();
			int i = 0;
			while ((line = br.readLine()) != null) {
				station = line.split(",");
				for (int j =0 ; j<4; j++) {
					stations[i][j] = station[j];
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
