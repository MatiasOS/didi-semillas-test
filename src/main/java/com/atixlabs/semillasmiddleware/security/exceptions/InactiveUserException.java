package com.atixlabs.semillasmiddleware.security.exceptions;

import com.atixlabs.semillasmiddleware.security.model.User;
import lombok.Getter;

@Getter
public class InactiveUserException extends Exception {

  private final User user;

  public InactiveUserException(User user) {
    this.user = user;
  }
}
