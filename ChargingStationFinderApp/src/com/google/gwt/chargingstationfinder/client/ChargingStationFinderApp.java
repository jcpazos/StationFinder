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
import com.google.gwt.geolocation.client.Geolocation;
import com.google.gwt.geolocation.client.Position;
import com.google.gwt.geolocation.client.PositionError;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.maps.gwt.client.GoogleMap;
import com.google.maps.gwt.client.LatLng;
import com.google.maps.gwt.client.MapOptions;
import com.google.maps.gwt.client.MapTypeId;
import com.google.maps.gwt.client.KmlLayer;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class ChargingStationFinderApp implements EntryPoint {
	
	private LatLng userPosition;
	private String[][] stations;
	private static final Logger LOG = Logger.getLogger(ChargingStationFinderApp.class.getName());
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";
	private final StationServiceAsync stationService = GWT.create(StationService.class);
	private final String mapURL = "http://vanmapp1.vancouver.ca/gmaps/covmap_data.htm?map=electric_vehicle_charging_stations.kmz";
	 private VerticalPanel mainPanel = new VerticalPanel();
	  private FlexTable addressFlexTable = new FlexTable();
	  private HorizontalPanel addPanel = new HorizontalPanel();
	  private TextBox newSymbolTextBox = new TextBox();
	  private Button addAddressButton = new Button("Add");
	  private Label lastUpdatedLabel = new Label();

	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		
		/*CSVParser parser = new CSVParser();
		parser.run(stations);
		addStations();*/
		
		addressFlexTable.setText(0, 0, "Address");
		

	    // Assemble Add Address panel.
	    addPanel.add(newSymbolTextBox);
	    addPanel.add(addAddressButton);

	    // Assemble Main panel.
	    mainPanel.add(addressFlexTable);
	    mainPanel.add(addPanel);
	    mainPanel.add(lastUpdatedLabel);

	    // Associate the Main panel with the HTML host page.
	    RootPanel.get("adressList").add(mainPanel);

	    // Move cursor focus to the input box.
	    newSymbolTextBox.setFocus(true);

		
		FormPanel formPanel = new FormPanel();
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
		
		GoogleMap gMap = displayMap(formPanel);
		
		
	}

	private void addStations() {
		for (final String[] s: stations) {
			stationService.addStation(Double.parseDouble(s[0]), Double.parseDouble(s[1]), 
					s[2], s[3], new AsyncCallback<Void>() {
				
				public void onFailure(Throwable caught) {
					handleError(caught);
				}
				public void onSuccess(Void result) {
					displayStation(s);
				}
			});
		}

	}

	private void displayStation(String[] s) {
		// TODO Auto-generated method stub
	}

	private GoogleMap displayMap(FormPanel formPanel) {
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

	    GoogleMap gMap = GoogleMap.create(formPanel.getElement(), options);
        LOG.log(Level.SEVERE, this.userPosition.toString());
	    gMap.setCenter(this.userPosition);
	    return gMap;
	}
	
	private void handleError(Throwable error) {
	    Window.alert(error.getMessage());
	    if (error instanceof NotLoggedInException) {
	      //Window.Location.replace(loginInfo.getLogoutUrl());
	    }
	  }
}
