package test.com.google.gwt.chargingstationfinder;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.MalformedURLException;

import org.junit.Before;
import org.junit.Test;

import com.google.gwt.chargingstationfinder.client.CSVParser;
import com.google.gwt.chargingstationfinder.client.ChargingStationFinderApp;
import com.google.gwt.chargingstationfinder.client.ParsingService;
import com.google.gwt.chargingstationfinder.client.StationService;
import com.google.gwt.chargingstationfinder.server.ParsingServiceImpl;
import com.google.gwt.core.shared.GWT;


public class ParsingTests {
	
	String[][] stations;
	
	private ParsingServiceImpl parsingService = new ParsingServiceImpl();
	
	@Test
	public void testInvalidAddressEntered() {
		stations = parsingService.parseData("dasdsad");
		assertTrue(stations == null);
		stations = parsingService.parseData("https://www.facebook.com/");
	    assertTrue(stations == null);
		stations = parsingService.parseData("http://stackoverflow.com/questions/5051643/messagebox-in-gwt");
		assertTrue(stations == null);
		
	}
	
	@Test public void testCorrectParsing() {
		stations = parsingService.parseData("http://pastebin.com/raw.php?i=KvKTX3gA");
		System.out.println(stations[0][0]);
		assertTrue(isStationEquals("49.222495514518997","-123.100262444394010","City of Vancouver","6810 Main Street", stations[0]));
		
		assertTrue(isStationEquals("49.300049999999999","-123.130193000000010","Vancouver Aquarium","845 Avison Way", stations[12]));
		
		assertTrue(isStationEquals("49.260359999999999","-123.123900000000010","Vancouver General Hospital","890 W.12th Ave.", stations[21]));
	}
	
	public boolean isStationEquals(String lat, String lon, String operator, String address, String[] station) {
		if (station[0].equals(lat) && station[1].equals(lon) && station[2].equals(operator) && station[3].equals(address))
			return true;
		return false;
	}

}
