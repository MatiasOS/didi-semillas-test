package com.atixlabs.semillasmiddleware.security.exceptions;

import com.atixlabs.semillasmiddleware.security.model.User;
import lombok.Getter;

@Getter
public class ExistUserException extends Exception {

  private final User user;

  public ExistUserException(User user) {
    this.user = user;
  }
}
