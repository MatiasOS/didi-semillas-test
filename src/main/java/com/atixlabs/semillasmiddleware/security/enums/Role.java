package com.atixlabs.semillasmiddleware.security.enums;

public enum Role {
  ROLE_ADMIN("ROLE_ADMIN"),
  ROLE_VIEWER("ROLE_VIEWER");

  private String role;

  Role(String role) {
    this.role = role;
  }

  public String role() {
    return this.role;
  }
}
