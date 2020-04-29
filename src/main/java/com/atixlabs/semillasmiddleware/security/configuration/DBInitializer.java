package com.atixlabs.semillasmiddleware.security.configuration;

import com.atixlabs.semillasmiddleware.security.dto.UserEditRequest;
import com.atixlabs.semillasmiddleware.security.model.Role;
import com.atixlabs.semillasmiddleware.security.repository.PermissionRepository;
import com.atixlabs.semillasmiddleware.security.model.Permission;
import com.atixlabs.semillasmiddleware.security.repository.MenuRepository;
import com.atixlabs.semillasmiddleware.security.model.Menu;
import com.atixlabs.semillasmiddleware.security.service.RoleService;
import com.atixlabs.semillasmiddleware.security.service.UserService;
import com.google.common.collect.Sets;

import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DBInitializer implements CommandLineRunner {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Override
    public void run(String... args) throws Exception {
        if (roleService.findByCode(com.atixlabs.semillasmiddleware.security.enums.Role.ROLE_ADMIN.role()) == null) {
            menuRepository.deleteAll();
            Menu mainUserMenu =
                    menuRepository.save(
                            Menu.builder().name("Profile").code("USERS").order(3).build());
            Menu viewUserMenu =
                    menuRepository.save(
                            Menu.builder()
                                    .name("View Profile")
                                    .code("VIEW_PROFILE")
                                    // .icon("user")
                                    .parent(mainUserMenu)
                                    .uri("/user-information")
                                    .order(0)
                                    .type("GET")
                                    .build());
            Menu logoutMenu =
                    menuRepository.save(
                            Menu.builder()
                                    .name("Logout")
                                    .code("LOGOUT")
                                    .parent(mainUserMenu)
                                    .order(1)
                                    .uri("/login")
                                    .type("GET")
                                    .build());
            /*
             * Menu mModifyUser =
             * menuRepository.save(Menu.builder().name("Modificar Usuario")
             * .code("MODIFY_USER") .icon("user") .parent(mainUserMenu)
             * .uri("/user-information") .type("POST") .build()); Menu mListUsers =
             * menuRepository.save(Menu.builder().name("Usuarios") .code("LIST_USER")
             * .icon("user") .parent(mainUserMenu) .uri("/users") .type("GET") .build());
             */

            Menu manageUsersMenu =
                    menuRepository.save(
                            Menu.builder()
                                    .name("Manage Users")
                                    .code("MANAGE_USERS")
                                    .order(2)
                                    .uri("/administration")
                                    .type("GET")
                                    .build());

            Permission viewProfilePermission =
                    permissionRepository.save(
                            Permission.builder().code("VIEW_PROFILE").description("Permiso ver perfil").build());

            Permission createUserPermission =
                    permissionRepository.save(
                            Permission.builder()
                                    .code("CREATE_USER")
                                    .description("Permiso crear usuario")
                                    .build());

            Permission viewListUserPermission =
                    permissionRepository.save(
                            Permission.builder()
                                    .code("LIST_USERS")
                                    .description("Permiso Listar usuarios")
                                    .build());

            Permission modifyUserPermission =
                    permissionRepository.save(
                            Permission.builder()
                                    .code("MODIFY_USERS")
                                    .description("Permiso Modificar usuarios")
                                    .build());

            Role roleAdmin =
                    roleService.save(
                            Role.builder()
                                    .code(com.atixlabs.semillasmiddleware.security.enums.Role.ROLE_ADMIN.role())
                                    .description("Administrator")
                                    .menus(Sets.newHashSet(mainUserMenu, manageUsersMenu, viewUserMenu, logoutMenu))
                                    .permissions(
                                            Sets.newHashSet(
                                                    createUserPermission,
                                                    modifyUserPermission,
                                                    viewListUserPermission,
                                                    viewProfilePermission))
                                    .build());
            Role roleViewer =
                    roleService.save(
                            Role.builder()
                                    .code(com.atixlabs.semillasmiddleware.security.enums.Role.ROLE_VIEWER.role())
                                    .description("Viewer")
                                    .menus(Sets.newHashSet(mainUserMenu, viewUserMenu, logoutMenu))
                                    .permissions(Sets.newHashSet(modifyUserPermission, viewProfilePermission))
                                    .build());
        }

        if (!userService.findByUsername("admin@atixlabs.com").isPresent()) {
            UserEditRequest userResponse = new UserEditRequest();
            userResponse.setUsername("admin@atixlabs.com");
            userResponse.setEmail("admin@atixlabs.com");
            userResponse.setPassword("admin");
            userResponse.setNewPassword("admin");
            userResponse.setConfirmNewPassword("admin");
            userResponse.setRole(com.atixlabs.semillasmiddleware.security.enums.Role.ROLE_ADMIN.role());
            userResponse.setName("Admin");
            userResponse.setLastName("Admin");
            userService.createOrEdit(userResponse);
        }
        if (!userService.findByUsername("viewer@atixlabs.com").isPresent()) {
            UserEditRequest userResponse = new UserEditRequest();
            userResponse.setUsername("viewer@atixlabs.com");
            userResponse.setEmail("viewer@atixlabs.com");
            userResponse.setPassword("viewer");
            userResponse.setNewPassword("viewer");
            userResponse.setConfirmNewPassword("viewer");
            userResponse.setRole(com.atixlabs.semillasmiddleware.security.enums.Role.ROLE_VIEWER.role());
            userResponse.setName("Viewer");
            userResponse.setLastName("Viewer");
            userService.createOrEdit(userResponse);
        }
        Optional<Menu> menu = menuRepository.findByCode("USERS");
        if (menu.isPresent() && menu.get().getName().contains("Profile")) {
            menu.get().setName("My account");
            menuRepository.save(menu.get());
        }
        menu = menuRepository.findByCode("VIEW_PROFILE");
        if (menu.isPresent()) {
            menuRepository.save(menu.get());
        }
    }
}
