package com.atixlabs.semillasmiddleware.security.controller;

import com.atixlabs.semillasmiddleware.security.model.Role;
import com.atixlabs.semillasmiddleware.security.repository.RoleRepository;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@Slf4j
@Validated
@RequestMapping(RolesController.URL_MAPPING)
@CrossOrigin(
    origins = "*",
    methods = {RequestMethod.GET, RequestMethod.POST})
public class RolesController {

  public static final String URL_MAPPING = "/roles";

  @Autowired
  private RoleRepository roleRepository;

  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @GetMapping
  public Collection<Role> findAll() {
    return Lists.newArrayList(roleRepository.findAll());
  }
}
