package com.atixlabs.semillasmiddleware.security.dto;

import java.util.Set;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class NavbarUserDto {

  private Set<MenuDto> menus;

}
