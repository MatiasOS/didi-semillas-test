package com.atixlabs.semillasmiddleware.security.dto;

import com.atixlabs.semillasmiddleware.security.model.Menu;
import lombok.Getter;

@Getter
public class MenuItemDto extends MenuDto {

  private String type;

  public MenuItemDto(Menu menu) {
    super(menu);
    this.type = menu.getType();
  }
}
