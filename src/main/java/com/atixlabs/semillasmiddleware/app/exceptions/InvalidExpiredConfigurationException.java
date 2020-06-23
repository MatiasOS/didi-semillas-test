package com.atixlabs.semillasmiddleware.app.exceptions;

import com.atixlabs.semillasmiddleware.security.model.User;
import lombok.Getter;

@Getter
public class InvalidExpiredConfigurationException extends Exception {


  public InvalidExpiredConfigurationException(String s) {
    super(s);
  }
}
