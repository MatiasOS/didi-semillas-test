package com.atixlabs.semillasmiddleware.security.controller;

import com.atixlabs.semillasmiddleware.security.configuration.CustomUser;
import com.atixlabs.semillasmiddleware.security.dto.FilterUserDto;
import com.atixlabs.semillasmiddleware.security.dto.MenuDto;
import com.atixlabs.semillasmiddleware.security.dto.UserEditRequest;
import com.atixlabs.semillasmiddleware.security.exceptions.ExistUserException;
import com.atixlabs.semillasmiddleware.security.exceptions.InexistentUserException;
import com.atixlabs.semillasmiddleware.security.exceptions.PasswordNotMatchException;
import com.atixlabs.semillasmiddleware.security.model.User;
import com.atixlabs.semillasmiddleware.security.service.UserPermissionsService;
import com.atixlabs.semillasmiddleware.security.service.UserService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@RestController
@Slf4j
@Validated
@RequestMapping(UsersController.URL_MAPPING_USERS)
@CrossOrigin(
    origins = "*",
    methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PATCH})
public class UsersController {

  public static final String URL_MAPPING_USERS = "/users";

  @Autowired
  private UserService userService;

  @Autowired
  private UserPermissionsService userPermissionsService;

  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @GetMapping("/filters")
  public Page<User> findAllWithFilters(
      @AuthenticationPrincipal CustomUser currentUser,
      @RequestParam("page") @Min(0) @Max(9999999) int page,
      @RequestParam("size") @Min(1) @Max(9999) int size,
      @RequestParam(required = false) String searchCriteria,
      @RequestParam(required = false) Boolean enabled,
      @RequestParam(required = false) String role) {
    Optional<Long> ignoreId =
        (currentUser != null && currentUser.getId() != null)
            ? Optional.of(currentUser.getId())
            : Optional.empty();
    Pageable pageRequest =
        PageRequest.of(
            page, size, Sort.by("name").ascending().and(Sort.by("lastName").ascending()));

    return userService.findUsersFilteredAndPaginated(
        FilterUserDto.builder()
            .id(ignoreId)
            .search(searchCriteria)
            .enabled(Optional.ofNullable(enabled))
            .role(Optional.ofNullable(role))
            .build(),
        pageRequest);
  }

  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @GetMapping
  public Collection<User> findAll() {
    return Lists.newArrayList(userService.getAll());
  }

  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @GetMapping(params = {"page", "size"})
  public Page<User> findPaginated( @AuthenticationPrincipal CustomUser currentUser,
      @RequestParam("page") @Min(0) @Max(9999999) int page,
      @RequestParam("size") @Min(1) @Max(9999) int size,
      @RequestParam(required = false) String searchCriteria) {
    Optional<Long> ignoreId =
        (currentUser != null && currentUser.getId() != null)
            ? Optional.of(currentUser.getId())
            : Optional.empty();
    Pageable pageRequest =
        PageRequest.of(
            page, size, Sort.by("name").ascending().and(Sort.by("lastName").ascending()));
    return userService.findUsersFilteredAndPaginated(
        FilterUserDto.builder().id(ignoreId).search(searchCriteria).build(), pageRequest);
  }

  @PreAuthorize("@permission.hasRole(#currentUser, 'ROLE_ADMIN')")
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public User createUser(
      @AuthenticationPrincipal CustomUser currentUser, @RequestBody @Valid UserEditRequest user)
      throws ExistUserException {
    return userService.createOrEdit(user);
  }

  // TODO: modificar excepciones
  @PreAuthorize("@permission.hasRole(authentication.principal, 'ROLE_ADMIN')")
  @GetMapping("/{id}")
  public User findOne(@PathVariable @Min(1) Long id) {
    return userService.findOne(id);
  }

  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @GetMapping("/disable/{id}")
  public void disable(@PathVariable @Min(1) Long id) {
    userService.disable(id);
  }

  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @GetMapping("/enable/{id}")
  public void enable(@PathVariable @Min(1) Long id) {
    userService.enable(id);
  }

  @GetMapping("/permissions/{id}")
  public Collection<String> findPermissions(@PathVariable @Min(1) Long id) {
    return userPermissionsService.findPermissions(id);
  }

  @GetMapping("/navbar")
  public Collection<MenuDto> getNavbar(@AuthenticationPrincipal CustomUser currentUser) {
    return userPermissionsService.findNavbar(currentUser.getId());
  }

  @PreAuthorize("@permission.canUpdate(#currentUser, #id,'ROLE_ADMIN' )")
  @PutMapping("/{id}")
  public ResponseEntity<User> putUser(
      @AuthenticationPrincipal CustomUser currentUser,
      @PathVariable @Min(1) Long id,
      @RequestBody @Valid UserEditRequest updatedUser)
      throws ExistUserException {

    try {
      userService.findById(id);
    } catch (UsernameNotFoundException ex) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
    if (!id.equals(updatedUser.getId())) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    return ResponseEntity.status(HttpStatus.OK).body(userService.createOrEdit(updatedUser));
  }

  // TODO: check password to see if it is valid, maybe map to a @validpassword attribute
  // in a new class.
  @PreAuthorize("@permission.canUpdate(#currentUser, #id,'ROLE_ADMIN' )")
  @PatchMapping("/{id}")
  public ResponseEntity<String> updateUser(
      @AuthenticationPrincipal CustomUser currentUser,
      @PathVariable @Min(1) Long id,
      @RequestBody Map<String, Object> updates)
      throws Exception {
    Optional<User> updatedUser;
    try {
      updatedUser = userService.updateUser(updates, id);
    } catch (InexistentUserException ex) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
    } catch (PasswordNotMatchException ex) {
      return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body("Passwords do not match.");
    }

    if (!updatedUser.isPresent()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);

    return ResponseEntity.status(HttpStatus.OK).body("Updated succesfully");
  }

  @GetMapping("/profile")
  public User getProfile(@AuthenticationPrincipal CustomUser currentUser) {
    return userService.findOne(currentUser.getId());
  }
}
