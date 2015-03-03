package com.google.gwt.chargingstationfinder.client;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.chargingstationfinder.server.Station;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.geolocation.client.Geolocation;
import com.google.gwt.geolocation.client.Position;
import com.google.gwt.geolocation.client.PositionError;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.maps.gwt.client.GoogleMap;
import com.google.maps.gwt.client.InfoWindow;
import com.google.maps.gwt.client.InfoWindowOptions;
import com.google.maps.gwt.client.LatLng;
import com.google.maps.gwt.client.MapOptions;
import com.google.maps.gwt.client.MapTypeId;
import com.google.maps.gwt.client.KmlLayer;
import com.google.maps.gwt.client.Marker;
import com.google.maps.gwt.client.Marker.ClickHandler;
import com.google.maps.gwt.client.MarkerOptions;
import com.google.maps.gwt.client.MouseEvent;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class ChargingStationFinderApp implements EntryPoint {
	
	private LatLng userPosition;
	private String[][] stations;
	private GoogleMap gMap;
	private FormPanel formPanel;
	private Logger logger = Logger.getLogger(ChargingStationFinderApp.class.getName());
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";
	private final StationServiceAsync stationService = GWT.create(StationService.class);
	private final String mapURL = "http://vanmapp1.vancouver.ca/gmaps/covmap_data.htm?map=electric_vehicle_charging_stations.kmz";

	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		
		CSVParser parser = new CSVParser(this);
		parser.run(stations);
		loadMap();
		
	}

	private void loadMap() {
		formPanel = new FormPanel();
		Geolocation geoLocation = Geolocation.getIfSupported();
		
		userPosition = LatLng.create(49.259909, -123.162542);
		
		geoLocation.getCurrentPosition(new Callback<Position,PositionError>() {
			
			public void onFailure(PositionError reason) {
				reason.getMessage();
			}

			
			public void onSuccess(Position result) {
				userPosition = LatLng.create(result.getCoordinates().getLatitude(),
						result.getCoordinates().getLongitude());
				
			}});
		
		displayMap(formPanel);
	}
	
	protected void addStations(String[][] stations) {
		this.stations = stations;
	}

	protected void addStation(final String[] station) {
			/*stationService.addStation(Double.parseDouble(station[0]), 
					Double.parseDouble(station[1]),station[2], station[3], 
					new AsyncCallback<Void>() {
				
				public void onFailure(Throwable caught) {
					handleError(caught);
				}
				public void onSuccess(Void result) {
					displayStation(station);
				}
			});*/
			displayStation(station);
		}


	private void displayStation(String[] s) {
		LatLng position = LatLng.create(Double.parseDouble(s[0]), Double.parseDouble(s[1]));
		
		InfoWindowOptions windowOptions = InfoWindowOptions.create();
		windowOptions.setContent(s[2] + "\r\n" + s[3]);
		final InfoWindow iw = InfoWindow.create(windowOptions);
		
		MarkerOptions markerOptions = MarkerOptions.create();
		markerOptions.setPosition(position);
		final Marker m = Marker.create(markerOptions);
		m.setMap(gMap);
		
		m.addClickListener(new ClickHandler(){

			@Override
			public void handle(MouseEvent event) {
				iw.open(gMap, m);
				
			}});
	}

	private void displayMap(FormPanel formPanel) {
		formPanel.setWidth("500px");
	    formPanel.setHeight("650px");

	    RootPanel.get().add(formPanel);

	    MapOptions options = MapOptions.create();

	    options.setZoom(6);
	    options.setMapTypeId(MapTypeId.ROADMAP);
	    options.setDraggable(true);
	    options.setMapTypeControl(true);
	    options.setScaleControl(true);
	    options.setScrollwheel(true);	

	    gMap = GoogleMap.create(formPanel.getElement(), options);
	    gMap.setCenter(this.userPosition);
	}
	
	private void handleError(Throwable error) {
		logger.log(Level.SEVERE, "no");
	    Window.alert(error.getMessage());
	    if (error instanceof NotLoggedInException) {
	      //Window.Location.replace(loginInfo.getLogoutUrl());
	    }
	  }
}
