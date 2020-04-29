package com.atixlabs.semillasmiddleware.security.dto;

import java.io.Serializable;

import com.atixlabs.semillasmiddleware.security.model.annotations.ValidPassword;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserEditRequest implements Serializable {

  private Long id;

  private String username;

  private String name;

  private String lastName;

  private String password;

  private String newPassword;

  @ValidPassword
  private String confirmNewPassword;

  private String email;

  private String phone;

  private String role;

  private String type;

  private String selectedKey;
}
