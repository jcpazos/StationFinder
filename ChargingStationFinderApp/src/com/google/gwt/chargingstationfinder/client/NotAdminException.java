package com.google.gwt.chargingstationfinder.client;

import java.io.Serializable;

public class NotAdminException extends Exception implements Serializable {

  public NotAdminException() {
    super();
  }

  public NotAdminException(String message) {
    super(message);
  }

}
