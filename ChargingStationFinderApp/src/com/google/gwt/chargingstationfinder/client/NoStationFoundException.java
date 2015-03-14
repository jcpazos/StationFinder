package com.google.gwt.chargingstationfinder.client;

import com.google.gwt.user.client.Window;

public class NoStationFoundException extends Exception {

	public NoStationFoundException() {
	    Window.alert ("No charging station found within the given radius!");
	  }
	}
