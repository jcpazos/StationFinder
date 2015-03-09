package com.google.gwt.chargingstationfinder.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.geolocation.client.Geolocation;
import com.google.gwt.geolocation.client.Position;
import com.google.gwt.geolocation.client.PositionError;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.maps.gwt.client.DirectionsRenderer;
import com.google.maps.gwt.client.DirectionsRequest;
import com.google.maps.gwt.client.DirectionsResult;
import com.google.maps.gwt.client.DirectionsService;
import com.google.maps.gwt.client.DirectionsStatus;
import com.google.maps.gwt.client.Geocoder;
import com.google.maps.gwt.client.GeocoderRequest;
import com.google.maps.gwt.client.GeocoderResult;
import com.google.maps.gwt.client.GeocoderStatus;
import com.google.maps.gwt.client.GoogleMap;
import com.google.maps.gwt.client.LatLng;
import com.google.maps.gwt.client.MapOptions;
import com.google.maps.gwt.client.MapTypeId;
import com.google.maps.gwt.client.Marker;
import com.google.maps.gwt.client.Marker.ClickHandler;
import com.google.maps.gwt.client.MarkerOptions;
import com.google.maps.gwt.client.MouseEvent;
import com.google.maps.gwt.client.TravelMode;

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
	private final ParsingServiceAsync parsingService = GWT.create(ParsingService.class);
	private HorizontalPanel addPanel = new HorizontalPanel();
	private TextBox newSymbolTextBox = new TextBox();
	private Button addAddressButton = new Button("Find Nearest Station");
	private Button addStationsButton = new Button("AddStations");
	private Label lastUpdatedLabel = new Label();
	private Anchor signInLink = new Anchor("Sign In");
	private Anchor signOutLink = new Anchor("Sign Out");
	private LoginInfo loginInfo = null;
	private Label loginLabel = new Label(
			"Please sign in to your Google Account to access the StationFinder application.");
	private VerticalPanel loginPanel = new VerticalPanel();
	private TextBox inputBox = new TextBox();
	private DialogBox dialogInputBox = new DialogBox();

	private FlowPanel mapPanel = new FlowPanel();
	private VerticalPanel controlPanel = new VerticalPanel();
	private FlexTable infoPanel = new FlexTable();
	private Marker userMarker = Marker.create();
	private DirectionsRenderer rend = DirectionsRenderer.create();

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

		// Set up sign out hyperlink.
		signOutLink.setHref(loginInfo.getLogoutUrl());
		signOutLink.addStyleName("signOut");

		// Assemble Add Address panel.
		newSymbolTextBox.addStyleName("inputBox");
		newSymbolTextBox.getElement().setPropertyString("placeholder", "Enter Postal Code Here");
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
							requestParsingInput();
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

	private void requestParsingInput() {
		Button okButton = new Button("Ok");
		final Label l = new Label("Text");
		inputBox.getElement().setPropertyString("placeholder", "Enter url Here");


		VerticalPanel vPanel = new VerticalPanel();
		vPanel.add(inputBox);
		vPanel.add(okButton);
		dialogInputBox.setWidget(vPanel);
		dialogInputBox.center();
		dialogInputBox.show();
		okButton.addClickHandler(new com.google.gwt.event.dom.client.ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				l.setText(inputBox.getText());
				dialogInputBox.hide();
				parsingService.parseData(inputBox.getText(), new AsyncCallback<String[][]>(){

					@Override
					public void onFailure(Throwable caught) {
						logger.log(Level.SEVERE, caught.getMessage());

					}

					@Override
					public void onSuccess(String[][] result) {
						if (result == null) {
							displayInvalidURLBox();
							return;
						}
						addStations(result);
						displayStations();
					}});

			}});
	}

	private void displayInvalidURLBox() {
		Button okButton = new Button("Ok");
		final DialogBox  db = new DialogBox();
		db.add(okButton);
		db.setText("Invalid file to parse, please try again");
		db.center();
		okButton.addClickHandler(new com.google.gwt.event.dom.client.ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				db.hide();
			}});
	}

	private void loadMap() {

		mapPanel.addStyleName("map");
		Geolocation geoLocation = Geolocation.getIfSupported();

		geoLocation.getCurrentPosition(new Callback<Position,PositionError>() {

			public void onFailure(PositionError reason) {
				reason.getMessage();
			}


			public void onSuccess(Position result) {
				userPosition = LatLng.create(result.getCoordinates().getLatitude(),
						result.getCoordinates().getLongitude());
				displayMap(formPanel);
				MarkerOptions option = MarkerOptions.create();
				option.setPosition(userPosition);
				Marker marker = Marker.create(option);
				marker.setMap(gMap);
			}
		});
	}


	private void initializeaddNewSymbolTextBox() {
		addAddressButton.addClickHandler(new com.google.gwt.event.dom.client.ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				String text = newSymbolTextBox.getText();
				Geocoder gcc = Geocoder.create();
				final GeocoderRequest gcr = GeocoderRequest.create();
				gcr.setAddress(text);
				gcc.geocode(gcr, new Geocoder.Callback() {

					@Override
					public void handle(JsArray<GeocoderResult> a,
							GeocoderStatus b) {
						GeocoderResult result = a.get(0);
						LatLng myLatLng = result.getGeometry().getLocation();
						userMarker.setPosition(myLatLng);
						showRoute(findNearestStation(myLatLng));
					}
				});

			}});	

	}

	protected void addStations(String[][] stations) {
		this.stations = stations;
		for (int i = 0; i<stations.length;i++) {
			String[] station = stations[i];
			addStation(station);
		}
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
			displayStation(s);
			i++;
		}
	}

	private void displayStation(String[] s) {
		LatLng position = LatLng.create(Double.parseDouble(s[0]), Double.parseDouble(s[1]));
		final String address = s[2];
		final String operator = s[3];

		final MarkerOptions markerOptions = MarkerOptions.create();
		markerOptions.setPosition(position);
		final Marker m = Marker.create(markerOptions);
		m.setMap(gMap);
		m.addClickListener(new ClickHandler(){

			@Override
			public void handle(MouseEvent event) {
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
		initializeStations();
		initializeaddNewSymbolTextBox();
		rend.setMap(gMap);
	}

	private void handleError(Throwable error) {
		Window.alert(error.getMessage());
		if (error instanceof NotLoggedInException) {
			Window.Location.replace(loginInfo.getLogoutUrl());
		}
	}

	private void showRoute(LatLng nearestStation) {

		DirectionsRequest req = DirectionsRequest.create();
		req.setOrigin(userPosition);
		req.setDestination(nearestStation);
		req.setTravelMode(TravelMode.DRIVING);

		DirectionsService serv = DirectionsService.create();

		serv.route(req, new DirectionsService.Callback() {

			@Override
			public void handle(DirectionsResult result, DirectionsStatus status) {
				// TODO Auto-generated method stub	
				if(status == DirectionsStatus.OK)
					rend.setDirections(result);
			}
		});
	}

	private LatLng findNearestStation(LatLng from) {
		double minDistance = Double.POSITIVE_INFINITY;
		LatLng nearest = null;
		for (String[] s : stations) {
			LatLng to = LatLng.create(Double.parseDouble(s[0]), Double.parseDouble(s[1]));
			double distance = calculateDistance(from, to);
			if (distance < minDistance) {
				nearest = to;
				minDistance = distance;
			}
		}
		return nearest;
	}

	private double calculateDistance(LatLng from, LatLng to) {
		return Math.sqrt(Math.pow(from.lat() - to.lat(), 2)
				+ Math.pow(from.lng() - to.lng(), 2));
	}
}