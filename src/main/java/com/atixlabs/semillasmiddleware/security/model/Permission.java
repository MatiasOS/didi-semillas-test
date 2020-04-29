package com.atixlabs.semillasmiddleware.security.model;

import javax.persistence.*;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Entity
@Table(name = "permissions")
public class Permission extends AuditableEntity {

  @Id
  @Column(unique = true)
  private String code;

  private String description;

  public Permission() {}

  public Permission(String code, String description) {
    this.code = code;
    this.description = description;
  }
}
