package com.google.gwt.chargingstationfinder.client;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
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
import com.google.gwt.user.client.ui.Label;

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
	private VerticalPanel mainPanel = new VerticalPanel();
	private FlexTable addressFlexTable = new FlexTable();
	private HorizontalPanel addPanel = new HorizontalPanel();
	private TextBox newSymbolTextBox = new TextBox();
	private Button addAddressButton = new Button("Add");
	private Button addStationsButton = new Button("AddStations");
	private Label lastUpdatedLabel = new Label();
	private Anchor signInLink = new Anchor("Sign In");
	private Anchor signOutLink = new Anchor("Sign Out");
	private LoginInfo loginInfo = null;
	private Label loginLabel = new Label(
		      "Please sign in to your Google Account to access the StationFinder application.");
	private VerticalPanel loginPanel = new VerticalPanel();
	private CSVParser parser = new CSVParser(this);
	
	private FlowPanel mapPanel = new FlowPanel();
	private VerticalPanel controlPanel = new VerticalPanel();
	private FlexTable infoPanel = new FlexTable();

	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		
		// Check login status using login service.
		  System.out.println("HI");
		    LoginServiceAsync loginService = GWT.create(LoginService.class);
		    loginService.login(GWT.getHostPageBaseURL(), new AsyncCallback<LoginInfo>() {
		      public void onFailure(Throwable error) {
		    	  handleError(error);
		      }

		      public void onSuccess(LoginInfo result) {
		        loginInfo = result;
		        if(loginInfo.isLoggedIn()) {
		          loadStationFinderApp();
		        } else {
		          loadLogin();
		        }
		      }
		    });
		  }

	protected void loadLogin() {
		// Assemble login panel.
		signInLink.setHref(loginInfo.getLoginUrl());
		loginPanel.add(loginLabel);
		loginPanel.add(signInLink);
		RootPanel.get().add(loginPanel);
	}

    private void loadStationFinderApp() {
    	
    	initializeStations();
		// Set up sign out hyperlink.
		signOutLink.setHref(loginInfo.getLogoutUrl());
		signOutLink.addStyleName("signOut");

		// Assemble Add Address panel.
		newSymbolTextBox.addStyleName("inputBox");
		newSymbolTextBox.getElement().setPropertyString("placeholder", "Enter Address Here");
		addPanel.add(newSymbolTextBox);
		addPanel.add(addAddressButton);
		addPanel.addStyleName("addressInput");

		// Assemble control panel.
		controlPanel.add(addPanel);
		controlPanel.add(lastUpdatedLabel);
		initializeAddStationsButton(); 

		controlPanel.addStyleName("control");

		infoPanel.setText(0,0,"Address:");
		infoPanel.setText(1,0,"Operator:");
		infoPanel.setText(2,0,"Rating:");


		infoPanel.addStyleName("info");
		infoPanel.getCellFormatter().addStyleName(0, 0, "address");
		infoPanel.getCellFormatter().addStyleName(1, 0, "operator");
		infoPanel.getCellFormatter().addStyleName(2, 0, "rating");

		RootPanel.get().add(signOutLink);
		RootPanel.get("control").add(controlPanel);
		RootPanel.get("info").add(infoPanel);
		RootPanel.get().addStyleName("background");

		// Move cursor focus to the input box.
		newSymbolTextBox.setFocus(true);	    

		loadMap();
    }

	private void initializeAddStationsButton() {
		try {
			stationService.checkIsAdmin(new AsyncCallback<Void>(){

				@Override
				public void onFailure(Throwable caught) {
					caught.getMessage();
					
				}

				@Override
				public void onSuccess(Void result) {
					addStationsButton.addStyleName("adminButton");
					controlPanel.insert(addStationsButton, 0);
			    	//Unfortunately GWT has two classes ClickHandler that do very different things
			    	addStationsButton.addClickHandler(new com.google.gwt.event.dom.client.ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {
							parser.run(stations, "parsed/stations.txt");
						}
				  });}
				});
		} catch (NotAdminException e) {
			//do nothing, user is not an admin
		}
	}

	private void initializeStations() {
		stationService.getStations(new AsyncCallback<String[][]>(){

			@Override
			public void onFailure(Throwable caught) {
				return;
				
			}

			@Override
			public void onSuccess(String[][] result) {
				if (result != null) {
					stations = result;
					displayStations(); 
				}
				
			}});
	}

	private void loadMap() {

		mapPanel.addStyleName("map");
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
			stationService.addStation(Double.parseDouble(station[0]), 
					Double.parseDouble(station[1]),station[2], station[3], 
					new AsyncCallback<Void>() {
				
				public void onFailure(Throwable caught) {
					handleError(caught);
				}
				public void onSuccess(Void result) {
					displayStation(station);
				}
			});
		}

    private void displayStations() {
    	int i =0;
    	for (String[] s: stations) {
    	    logger.log(Level.SEVERE, "i is" + i);
    		displayStation(s);
    		i++;
    	}
    }
	
	private void displayStation(String[] s) {
		LatLng position = LatLng.create(Double.parseDouble(s[0]), Double.parseDouble(s[1]));
		final String address = s[2];
		final String operator = s[3];
		
		InfoWindowOptions windowOptions = InfoWindowOptions.create();
		windowOptions.setContent(s[2] + "\r\n," + s[3]);
		final InfoWindow iw = InfoWindow.create(windowOptions);
		
		MarkerOptions markerOptions = MarkerOptions.create();
		markerOptions.setPosition(position);
		final Marker m = Marker.create(markerOptions);
		
		m.setMap(gMap);
		m.addClickListener(new ClickHandler(){

			@Override
			public void handle(MouseEvent event) {
				iw.open(gMap, m);
				infoPanel.setText(0, 0, address);
				infoPanel.setText(1, 0, operator);
			}});
	}

	private void displayMap(FormPanel formPanel) {

	    RootPanel.get("map").add(mapPanel);

	    MapOptions options = MapOptions.create();

	    options.setZoom(12);
	    options.setMapTypeId(MapTypeId.ROADMAP);
	    options.setDraggable(true);
	    options.setMapTypeControl(true);
	    options.setScaleControl(true);
	    options.setScrollwheel(true);	

	    gMap = GoogleMap.create(mapPanel.getElement(), options);
	    gMap.setCenter(this.userPosition);
	}
	
	private void handleError(Throwable error) {
		logger.log(Level.SEVERE, "no");
	    Window.alert(error.getMessage());
	    if (error instanceof NotLoggedInException) {
	      Window.Location.replace(loginInfo.getLogoutUrl());
	    }
	  }
}