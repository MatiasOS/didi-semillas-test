package com.atixlabs.semillasmiddleware.app.processControl.exception;

import lombok.Getter;

@Getter
public class InvalidProcessException extends Exception {

  public InvalidProcessException(String s) {
    super(s);
  }
}
