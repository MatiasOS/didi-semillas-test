package com.atixlabs.semillasmiddleware.security.configuration;

import com.atixlabs.semillasmiddleware.app.model.configuration.ParameterConfiguration;
import com.atixlabs.semillasmiddleware.app.model.configuration.constants.ConfigurationCodes;
import com.atixlabs.semillasmiddleware.app.model.credential.constants.CredentialStatesCodes;
import com.atixlabs.semillasmiddleware.app.model.credentialState.CredentialState;
import com.atixlabs.semillasmiddleware.app.model.credentialState.RevocationReason;
import com.atixlabs.semillasmiddleware.app.model.credentialState.constants.RevocationReasonsCodes;
import com.atixlabs.semillasmiddleware.app.repository.CredentialStateRepository;
import com.atixlabs.semillasmiddleware.app.repository.ParameterConfigurationRepository;
import com.atixlabs.semillasmiddleware.app.repository.RevocationReasonRepository;
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

    private UserService userService;

    private RoleService roleService;

    private PermissionRepository permissionRepository;

    private MenuRepository menuRepository;

    private CredentialStateRepository credentialStateRepository;

    private ParameterConfigurationRepository parameterConfigurationRepository;

    private RevocationReasonRepository revocationReasonRepository;

    @Autowired
    public DBInitializer(UserService userService, RoleService roleService, PermissionRepository permissionRepository, MenuRepository menuRepository, CredentialStateRepository credentialStateRepository, ParameterConfigurationRepository parameterConfigurationRepository, RevocationReasonRepository revocationReasonRepository) {
        this.userService = userService;
        this.roleService = roleService;
        this.permissionRepository = permissionRepository;
        this.menuRepository = menuRepository;
        this.credentialStateRepository = credentialStateRepository;
        this.parameterConfigurationRepository = parameterConfigurationRepository;
        this.revocationReasonRepository = revocationReasonRepository;
    }

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

        if (!userService.findByUsername("cronUser@atixlabs.com").isPresent()) {
            UserEditRequest userResponse = new UserEditRequest();
            userResponse.setUsername("cronUser@atixlabs.com");
            userResponse.setEmail("cronUser@atixlabs.com");
            userResponse.setPassword("admin");
            userResponse.setNewPassword("admin");
            userResponse.setConfirmNewPassword("admin");
            userResponse.setRole(com.atixlabs.semillasmiddleware.security.enums.Role.ROLE_ADMIN.role());
            userResponse.setName("cronUser");
            userResponse.setLastName("cronUser");
            userService.createOrEdit(userResponse);
        }

        if (!userService.findByUsername("didiUser@atixlabs.com").isPresent()) {
            UserEditRequest userResponse = new UserEditRequest();
            userResponse.setUsername("didiUser@atixlabs.com");
            userResponse.setEmail("didiUser@atixlabs.com");
            userResponse.setPassword("admin");
            userResponse.setNewPassword("admin");
            userResponse.setConfirmNewPassword("admin");
            userResponse.setRole(com.atixlabs.semillasmiddleware.security.enums.Role.ROLE_ADMIN.role());
            userResponse.setName("didiUser");
            userResponse.setLastName("didiUser");
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

        if (menuRepository.findByCode("VIEW_PROFILE").isPresent()) {
            menuRepository.save(menu.get());
        }

        //Credential States
        Optional<CredentialState> credentialStateOptional = credentialStateRepository.findByStateName(CredentialStatesCodes.CREDENTIAL_ACTIVE.getCode());
        if (credentialStateOptional.isEmpty()) {
            credentialStateRepository.save(new CredentialState(CredentialStatesCodes.CREDENTIAL_ACTIVE.getCode()));
        }
        Optional<CredentialState> credentialStateRevoke = credentialStateRepository.findByStateName(CredentialStatesCodes.CREDENTIAL_REVOKE.getCode());
        if (credentialStateRevoke.isEmpty()) {
            credentialStateRepository.save(new CredentialState(CredentialStatesCodes.CREDENTIAL_REVOKE.getCode()));
        }

        Optional<CredentialState> credentialStatePending = credentialStateRepository.findByStateName(CredentialStatesCodes.PENDING_DIDI.getCode());
        if (credentialStatePending.isEmpty()) {
            credentialStateRepository.save(new CredentialState(CredentialStatesCodes.PENDING_DIDI.getCode()));
        }

        if(!parameterConfigurationRepository.findByConfigurationName(ConfigurationCodes.MAX_EXPIRED_AMOUNT.getCode()).isPresent()){
            ParameterConfiguration configuration = new ParameterConfiguration();
            configuration.setExpiredAmountMax((float) 10250);
            configuration.setConfigurationName(ConfigurationCodes.MAX_EXPIRED_AMOUNT.getCode());
            parameterConfigurationRepository.save(configuration);
        }

        //revocation reasons
        if(revocationReasonRepository.findAll().size() == 0){
            RevocationReason updateReason = new RevocationReason(RevocationReasonsCodes.UPDATE_INTERNAL.getCode());
            revocationReasonRepository.save(updateReason);

            RevocationReason cancelledReason = new RevocationReason(RevocationReasonsCodes.CANCELLED.getCode());
            revocationReasonRepository.save(cancelledReason);


            RevocationReason expiredReason = new RevocationReason(RevocationReasonsCodes.EXPIRED_INFO.getCode());
            revocationReasonRepository.save(expiredReason);

            RevocationReason unlinkingReason = new RevocationReason(RevocationReasonsCodes.UNLINKING.getCode());
            revocationReasonRepository.save(unlinkingReason);

            RevocationReason defaultReason = new RevocationReason(RevocationReasonsCodes.DEFAULT.getCode());
            revocationReasonRepository.save(defaultReason);
        }

    }
}
