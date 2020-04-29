package com.atixlabs.semillasmiddleware.security.service;

import com.atixlabs.semillasmiddleware.security.dto.AuthenticatedUserDto;
import com.atixlabs.semillasmiddleware.security.dto.MenuDto;
import com.atixlabs.semillasmiddleware.security.dto.MenuItemDto;
import com.atixlabs.semillasmiddleware.security.dto.NavbarUserDto;
import com.atixlabs.semillasmiddleware.security.exceptions.InactiveUserException;
import com.atixlabs.semillasmiddleware.security.model.Menu;
import com.atixlabs.semillasmiddleware.security.model.Permission;
import com.atixlabs.semillasmiddleware.security.model.Role;
import com.atixlabs.semillasmiddleware.security.model.User;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserPermissionsService {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public Optional<AuthenticatedUserDto> findUserAuthenticated(String username, String password)
            throws InactiveUserException {
        Optional<User> user = userService.findByUsername(username);
        if (user.isPresent() && passwordEncoder.matches(password, user.get().getPassword())) {
            if (!user.get().isActive()) {
                throw new InactiveUserException(user.get());
            }
            AuthenticatedUserDto authenticatedUser = new AuthenticatedUserDto();
            authenticatedUser.setId(user.get().getId());
            authenticatedUser.setUsername(username);
            authenticatedUser.setEmail(user.get().getEmail());
            authenticatedUser.setName(user.get().getName());
            authenticatedUser.setLastname(user.get().getLastName());
            authenticatedUser.setRole(user.get().getRole().getDescription());
            authenticatedUser.setPermissions(
                    Sets.newHashSet(this.findPermissionsByRole(user.get().getRole())));
            authenticatedUser.setNavbar(
                    NavbarUserDto.builder().menus(Sets.newLinkedHashSet()).build());

            Collection<Menu> navbar = user.get().getRole().getMenus();
            for (Menu menu : navbar) {
                if (menu.isMainMenu()) {
                    MenuDto menuDto = new MenuDto(menu);
                    menuDto
                            .getItems()
                            .addAll(
                                    navbar.stream()
                                            .filter(m -> !m.isMainMenu() && m.getParent().equals(menu))
                                            .sorted(Comparator.comparingInt(Menu::getOrder))
                                            .map(MenuItemDto::new)
                                            .collect(Collectors.toList()));
                    authenticatedUser.getNavbar().getMenus().add(menuDto);
                }
            }
            return Optional.of(authenticatedUser);
        }

        return Optional.empty();
    }

    private Collection<String> findPermissionsByRole(Role role) {
        return role.getPermissions().stream().map(Permission::getCode).collect(Collectors.toList());
    }

    public Collection<String> findPermissions(Long id) {
        User user = userService.findById(id);
        return findPermissionsByRole(user.getRole());
    }

    public Set<MenuDto> findNavbar(Long id) {
        User user = userService.findById(id);
        return findNavbarDto(user.getRole());
    }

    private Set<MenuDto> findNavbarDto(Role role) {
        Set<MenuDto> navbar = Sets.newHashSet();
        for (Menu menu : role.getMenus()) {
            if (menu.isMainMenu()) {
                MenuDto menuDto = new MenuDto(menu);
                menuDto
                        .getItems()
                        .addAll(menu.getItems().stream().map(MenuItemDto::new).collect(Collectors.toList()));
                navbar.add(menuDto);
            }
        }
        return navbar;
    }
}
