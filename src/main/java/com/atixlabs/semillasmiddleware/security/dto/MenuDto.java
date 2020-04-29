package com.atixlabs.semillasmiddleware.security.dto;

import com.atixlabs.semillasmiddleware.security.model.Menu;
import com.google.common.collect.Sets;
import lombok.Getter;

import java.util.Collection;

@Getter
public class MenuDto {

  private String key;

  private String description;

  private Collection<MenuItemDto> items;

  private String url;

  private Integer order;

  public MenuDto(Menu menu) {
    this.key = menu.getCode();
    this.description = menu.getName();
    this.url = menu.getUri();
    this.order = menu.getOrder();
    items = Sets.newHashSet();
  }
}
