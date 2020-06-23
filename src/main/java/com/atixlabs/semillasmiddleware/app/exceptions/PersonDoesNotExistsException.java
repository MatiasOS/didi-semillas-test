package com.atixlabs.semillasmiddleware.app.exceptions;

import lombok.Getter;

@Getter
public class PersonDoesNotExistsException extends Exception {

  public PersonDoesNotExistsException(String s) {
    super(s);
  }
}
