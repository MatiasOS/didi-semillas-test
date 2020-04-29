package com.atixlabs.semillasmiddleware.security.dto;

import java.io.Serializable;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AuthenticatedUserDto implements Serializable {

  private static final String tokenType = "Bearer";

  private Long id;

  private String username;

  private String email;

  private String name;

  private String lastname;

  private String accessToken;

  private NavbarUserDto navbar;

  private String role;

  private String territory;

  private Set<String> permissions;
}
