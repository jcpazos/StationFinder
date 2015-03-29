package com.google.gwt.chargingstationfinder.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.chargingstationfinder.shared.MyLatLng;
import com.google.gwt.chargingstationfinder.shared.Review;
import com.google.gwt.chargingstationfinder.shared.Station;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.geolocation.client.Geolocation;
import com.google.gwt.geolocation.client.Position;
import com.google.gwt.geolocation.client.PositionError;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
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
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;
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
import com.google.maps.gwt.client.geometry.Spherical;
import com.google.maps.gwt.client.MarkerImage;
import com.google.maps.gwt.client.MarkerOptions;
import com.google.maps.gwt.client.MouseEvent;
import com.google.maps.gwt.client.TravelMode;
import com.xedge.jquery.client.JQuery;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class ChargingStationFinderApp implements EntryPoint {

	private LatLng userPosition;
	private List<Station> stations = new ArrayList<Station>();
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
	private TextBox newSymbolTextBox1 = new TextBox();
	private TextBox nearestStationTextBox = new TextBox();
	private Button addAddressButton = new Button("Find Nearest Station");
	private Button addAddressButton1 = new Button("Add Favourite Station");
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
	private Marker selectedMarker;
	private DirectionsRenderer rend = DirectionsRenderer.create();
	private Setting setting = new Setting();
	private MarkerImage BLUE_MARKER = MarkerImage.create("images/marker.png");
	private Button postReviewButton = new Button("Post Review");
	private TextArea commentBox = new TextArea();
	private MarkerImage GREEN_MARKER = MarkerImage.create("images/icon_green.png");
	private Station selectedStation;
	private HorizontalPanel menuBar = new HorizontalPanel();
	private MenuBar settingMenu= new MenuBar();
	private String userEmailAddress;
	private final String removeStationText = "Remove Favourite Station";
	private  ArrayList<Station> favouriteStations = new ArrayList<Station>();

	//	private String[][] favouriteStations = new String[23][4];
	private int index;

	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		// Check login status using login service.
		RootPanel.get("fb-root").setVisible(false);
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
		RootPanel.get("tweetBtn").getElement().getStyle().setProperty("visibility", "visible");
		RootPanel.get("fb-root").setVisible(true);
		RootPanel.get("btn-group").getElement().getStyle().setProperty("visibility", "visible");
		// Set up sign out hyperlink.
		signOutLink.setHref(loginInfo.getLogoutUrl());
		signOutLink.addStyleName("signOut");

		// Assemble Add Address panel.
		newSymbolTextBox.addStyleName("inputBox");
		newSymbolTextBox.getElement().setPropertyString("placeholder", "Enter Postal Code Here");
		addPanel.add(newSymbolTextBox);
		addPanel.add(addAddressButton);
		//addPanel.add(nearestStationTextBox);
		addPanel.addStyleName("addressInput");

		addPanel.add(addAddressButton1);
		addPanel.addStyleName("addressInput1");

		// Assemble control panel.
		controlPanel.add(addPanel);
		controlPanel.add(lastUpdatedLabel);
		initializeAddStationsButton();
		controlPanel.addStyleName("control");

		infoPanel.setText(0,0,"Address:");
		infoPanel.setText(1,0,"Operator:");
		infoPanel.setText(2,0,"Rating:");
		//infoPanel.setText(3,0,"Radius is now set to be " + setting.radius.getName());
		
		menuBar.addStyleName("menu");
		final MenuBar radiusMenu = new MenuBar(true);
		for (final RadiusSetting radius: setting.radius.getClass().getEnumConstants()) {
			radiusMenu.addItem(radius.getName(), new Command() {
				@Override
				public void execute() {
					setting.setRadius(radius);
					settingMenu.setAnimationEnabled(true);
					//infoPanel.setText(3, 0, "Radius is now set to be " + setting.radius.getName());
				}
			});
		}
		settingMenu.addItem("Radius setting", radiusMenu);
		menuBar.add(settingMenu);

		initializeReviewFunction();

		infoPanel.addStyleName("info");
		infoPanel.getCellFormatter().addStyleName(0, 0, "address");
		infoPanel.getCellFormatter().addStyleName(1, 0, "operator");
		infoPanel.getCellFormatter().addStyleName(2, 0, "rating");
		infoPanel.getCellFormatter().addStyleName(3, 0, "commentBoxCell");
		infoPanel.getCellFormatter().addStyleName(4, 0, "reviewButtonCell");
		
		RootPanel.get().add(signOutLink);
		RootPanel.get("control").add(controlPanel);
		RootPanel.get("info").add(infoPanel);
		RootPanel.get().addStyleName("background");
		RootPanel.get("menu").add(menuBar);

		// Move cursor focus to the input box.
		newSymbolTextBox.setFocus(true);	    

		loadMap();
	}

	private void initializeReviewFunction() {
		postReviewButton.addClickHandler(new com.google.gwt.event.dom.client.ClickHandler() {
			public void onClick(ClickEvent e){
				String comment = commentBox.getText();
				commentBox.setText("");
				Review r = new Review(4, comment);
				try {
					selectedStation.addReview(r);
				} catch (InvalidReviewException e1) {
					e1.printStackTrace();
				}
				stationService.updateStation(selectedStation, new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {

					}
					@Override
					public void onSuccess(Void result) {
						displayReviews(selectedStation.getReviews());
					}
				});
			}
		});
		
		commentBox.addStyleDependentName("comment");
		commentBox.getElement().setPropertyString("placeholder", "Share Your Experience at This Station");
		postReviewButton.addStyleDependentName("review");
		
		infoPanel.setWidget(3, 0, commentBox);
		infoPanel.setWidget(4, 0, postReviewButton);
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
		stationService.getStations(new AsyncCallback<List<Station>>(){

			@Override
			public void onFailure(Throwable caught) {
				return;

			}

			@Override
			public void onSuccess(List<Station> result) {
				if (result != null) {
					stations = result;
					try {
						stationService.getUserEmailAddress(new AsyncCallback<String>(){

							@Override
							public void onFailure(Throwable caught) {
								logger.log(Level.SEVERE, "fail");
								
							}

							@Override
							public void onSuccess(String result) {
								userEmailAddress = result;
								for (Station s : stations) {
									if (s.getUserEmails().contains(result)) {
										favouriteStations.add(s);
									}
								}
								displayStations();
								
							}});
					} catch (NotLoggedInException e) {
						e.printStackTrace();
					}
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
				parsingService.parseData(inputBox.getText(), new AsyncCallback<List<Station>>(){

					@Override
					public void onFailure(Throwable caught) {
						logger.log(Level.SEVERE, caught.getMessage());

					}

					@Override
					public void onSuccess(List<Station> result) {
						if (result == null) {
							displayInvalidURLBox();
							return;
						}
						try{
							addStations(result);
						}catch (Exception e) {

						}
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
				option.setIcon(BLUE_MARKER);
				userMarker.setOptions(option);
				userMarker.setMap(gMap);

				gMap.addClickListener(new GoogleMap.ClickHandler(){

					@Override
					public void handle(MouseEvent event) {
						userPosition = event.getLatLng();
						userMarker.setPosition(userPosition);
						try {
							showRoute(findNearestStation(userPosition));
						} catch (NoStationFoundException e) {
							//there is no station the exception displays a message
						}
					}
				});
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
						userPosition = myLatLng;
						userMarker.setPosition(myLatLng);
						try {
							showRoute(findNearestStation(myLatLng));
						} catch (NoStationFoundException e) {}
					}
				});

			}});	

	}	

	protected void addStations(List<Station> stations) throws InvalidReviewException {
		this.stations = stations;
		for (int i = 0; i<stations.size();i++) {
			Station station = stations.get(i);
			addStation(station);
		}

	}

	protected void addStation(final Station station) {
		stationService.addStation(station, new AsyncCallback<Void>() {

			public void onFailure(Throwable caught) {
				handleError(caught);
			}
			public void onSuccess(Void result) {
				displayStation(station, null);
			}
		});
	}

	private void displayStations() {
		for (Station s: stations) {
			if (s.getUserEmails().contains(userEmailAddress)) displayStation(s, "green");
			else displayStation(s, null);
		}
	}


	private void displayStation(Station s, String markerColour) {
		final Station station = s;
		final LatLng position = LatLngConverter.toLatLng((s.getPosition()));
		final String address = s.getAddress();
		final String operator = s.getOperator();

		final MarkerOptions markerOptions = MarkerOptions.create();
		markerOptions.setPosition(position);
		if (markerColour.equals("green")) markerOptions.setIcon(GREEN_MARKER);
		final Marker m = Marker.create(markerOptions);
		m.setMap(gMap);
		m.addClickListener(new ClickHandler(){

			@Override
			public void handle(MouseEvent event) {
				selectedMarker = m;
				selectedStation = station;
				addAddressButton1.setText("Add Favourite Station");
				if (favouriteStations.contains(selectedStation)) addAddressButton1.setText(removeStationText);
				showRoute(station);
				infoPanel.setText(0, 0, "Address " + address);
				infoPanel.setText(1, 0, "Operator " + operator);
				displayReviews(station.getReviews());
			}});
	}

	private void displayReviews(ArrayList<Review> reviews) {
		Integer rating = 0;
		for (int i = 0; i < reviews.size(); i++) {
			rating += reviews.get(i).getRating();
		}
		rating /= reviews.size();
		String text = rating.toString();

		for (int i = 0; i < reviews.size(); i++) {
			text = text.concat("\n" + reviews.get(i).getComment());
		}
		infoPanel.setText(2, 0, text);
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
		initializeAddAddressButton1();
		rend.setMap(gMap);
	}

	private void initializeAddAddressButton1() {
		addAddressButton1.addClickHandler(new com.google.gwt.event.dom.client.ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				try {
					stationService.getUserEmailAddress(new AsyncCallback<String>(){

						@Override
						public void onFailure(Throwable caught) {}

						@Override
						public void onSuccess(String result) {
							if (addAddressButton1.getText().equals(removeStationText)) {
								favouriteStations.remove(selectedStation);
								selectedStation.removeUserEmailAddress(result);
								selectedMarker.setIcon("http://maps.google.com/mapfiles/ms/icons/red-dot.png");
							}

							else if (favouriteStations.size() == 10) {
								Window.alert("You can't have more than 10 favourite station!");
								return;
							}

							else {
								favouriteStations.add(selectedStation);
								selectedStation.addUserEmailAddress(result);
								selectedMarker.setIcon(GREEN_MARKER);
							}

							stationService.updateStation(selectedStation, new AsyncCallback<Void>(){

								@Override
								public void onFailure(Throwable caught) {
								}

								@Override
								public void onSuccess(Void result) {
								}});
						}});
				} catch (NotLoggedInException e) {
					e.printStackTrace();
				}

			}});

	}

	private void handleError(Throwable error) {

		Window.alert(error.getMessage());
		if (error instanceof NotLoggedInException) {
			Window.Location.replace(loginInfo.getLogoutUrl());
		}
	}

	private void showRoute(Station dest) {

		DirectionsRequest req = DirectionsRequest.create();
		req.setOrigin(userPosition);
		req.setDestination(LatLngConverter.toLatLng((dest.getPosition())));
		req.setTravelMode(TravelMode.DRIVING);

		DirectionsService serv = DirectionsService.create();

		serv.route(req, new DirectionsService.Callback() {

			@Override
			public void handle(DirectionsResult result, DirectionsStatus status) {
				if(status == DirectionsStatus.OK)
					rend.setDirections(result);
			}
		});
	}


	private Station findNearestStation(LatLng from) throws NoStationFoundException {
		double minDistance = Double.POSITIVE_INFINITY;
		Station nearest = null;
		for (Station s : stations) {
			double distance = Spherical.computeDistanceBetween(from, LatLngConverter.toLatLng((s.getPosition())));
			if (distance < minDistance && distance <= setting.radius.radius()) {
				nearest = s;
				minDistance = distance;
			}
		}
		if (minDistance == Double.POSITIVE_INFINITY) {
			throw new NoStationFoundException();
		}

		resetTweetButton(nearest);
		return nearest;
	}

	private void resetTweetButton(Station minStation) {
		JQuery.select("#tweetBtn iframe").remove();
		Element e = DOM.createAnchor();
		e.addClassName("twitter-share-button");
		e.setAttribute("href", "http://twitter.com/share");
		e.setAttribute("data-url", "http://2-dot-selinatron.appspot.com/");
		e.setAttribute("data-text", "Hey guys, I'm going to charge my car at "
				+ minStation.getOperator() + ", " + minStation.getAddress() + ". Use this awesome app if you want to charge yours too!");
		JQuery.select("#tweetBtn").append(e);

		refreshTwitterButtons();

	}

	private static native void refreshTwitterButtons() /*-{
		$wnd.twttr.widgets.load(); 
	}-*/;

	private double calculateDistance(LatLng from, LatLng to) {
		return Math.sqrt(Math.pow(from.lat() - to.lat(), 2)
				+ Math.pow(from.lng() - to.lng(), 2));
	}
}
