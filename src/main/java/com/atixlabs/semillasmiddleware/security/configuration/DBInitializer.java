package com.atixlabs.semillasmiddleware.security.configuration;

import com.atixlabs.semillasmiddleware.app.didi.model.CertTemplate;
import com.atixlabs.semillasmiddleware.app.model.provider.model.ProviderCategory;
import com.atixlabs.semillasmiddleware.app.model.provider.repository.ProviderCategoryRepository;
import com.atixlabs.semillasmiddleware.app.model.configuration.ParameterConfiguration;
import com.atixlabs.semillasmiddleware.app.model.configuration.constants.ConfigurationCodes;
import com.atixlabs.semillasmiddleware.app.model.credential.constants.CredentialCategoriesCodes;
import com.atixlabs.semillasmiddleware.app.model.credential.constants.CredentialStatesCodes;
import com.atixlabs.semillasmiddleware.app.model.credentialState.CredentialState;
import com.atixlabs.semillasmiddleware.app.model.credentialState.RevocationReason;
import com.atixlabs.semillasmiddleware.app.model.credentialState.constants.RevocationReasonsCodes;
import com.atixlabs.semillasmiddleware.app.processControl.model.ProcessControl;
import com.atixlabs.semillasmiddleware.app.processControl.model.constant.ProcessControlStatusCodes;
import com.atixlabs.semillasmiddleware.app.processControl.model.constant.ProcessNamesCodes;
import com.atixlabs.semillasmiddleware.app.processControl.repository.ProcessControlRepository;
import com.atixlabs.semillasmiddleware.app.repository.CertTemplateRepository;
import com.atixlabs.semillasmiddleware.app.repository.CredentialStateRepository;
import com.atixlabs.semillasmiddleware.app.repository.ParameterConfigurationRepository;
import com.atixlabs.semillasmiddleware.app.repository.RevocationReasonRepository;
import com.atixlabs.semillasmiddleware.security.dto.UserEditRequest;
import com.atixlabs.semillasmiddleware.security.exceptions.ExistUserException;
import com.atixlabs.semillasmiddleware.security.model.Role;
import com.atixlabs.semillasmiddleware.security.repository.PermissionRepository;
import com.atixlabs.semillasmiddleware.security.model.Permission;
import com.atixlabs.semillasmiddleware.security.repository.MenuRepository;
import com.atixlabs.semillasmiddleware.security.model.Menu;
import com.atixlabs.semillasmiddleware.security.service.RoleService;
import com.atixlabs.semillasmiddleware.security.service.UserService;
import com.atixlabs.semillasmiddleware.util.DateUtil;
import com.google.common.collect.Sets;

import java.util.*;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    private ProcessControlRepository processControlRepository;

    private String enviroment;

    private ProviderCategoryRepository providerCategoryRepository;

private CertTemplateRepository certTemplateRepository;

    @Value("${didi.semillas.template_code_identity}")
    private String didiTemplateCodeIdentity;

    @Value("${didi.semillas.template_code_entrepreneurship}")
    private String didiTemplateCodeEntrepreneurship;

    @Value("${didi.semillas.template_code_dwelling}")
    private String didiTemplateCodeDwelling;

    @Value("${didi.semillas.template_code_benefit}")
    private String didiTemplateCodeBenefit;

    @Value("${didi.semillas.template_code_credit}")
    private String didiTemplateCodeCredit;

    @Value("${didi.semillas.template_code_sancor_salud}")
    private String didiTemplateCodeSancorSalud;

    @Autowired
    public DBInitializer(UserService userService, RoleService roleService, PermissionRepository permissionRepository, MenuRepository menuRepository, CredentialStateRepository credentialStateRepository, ParameterConfigurationRepository parameterConfigurationRepository, RevocationReasonRepository revocationReasonRepository, ProcessControlRepository processControlRepository, @Value("${deploy.enviroment}") String enviroment, CertTemplateRepository certTemplateRepository, ProviderCategoryRepository providerCategoryRepository) {
        this.userService = userService;
        this.roleService = roleService;
        this.permissionRepository = permissionRepository;
        this.menuRepository = menuRepository;
        this.credentialStateRepository = credentialStateRepository;
        this.parameterConfigurationRepository = parameterConfigurationRepository;
        this.revocationReasonRepository = revocationReasonRepository;
        this.processControlRepository = processControlRepository;
        this.enviroment = enviroment;
        this.certTemplateRepository = certTemplateRepository;
        this.providerCategoryRepository = providerCategoryRepository;
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

        if(enviroment!=null && enviroment.equals("production")){
            this.createProductionUsers();
        }else
            this.createUsers();

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

       Optional<CredentialState> credentialStateDefault = credentialStateRepository.findByStateName(CredentialStatesCodes.DEFAULT_DIDI.getCode());
       if (credentialStateDefault.isEmpty()) {
           credentialStateRepository.save(new CredentialState(CredentialStatesCodes.DEFAULT_DIDI.getCode()));
       }

        if(!parameterConfigurationRepository.findByConfigurationName(ConfigurationCodes.MAX_EXPIRED_AMOUNT.getCode()).isPresent()){
            ParameterConfiguration configuration = new ParameterConfiguration();
            configuration.setValue("500");
            configuration.setConfigurationName(ConfigurationCodes.MAX_EXPIRED_AMOUNT.getCode());
            parameterConfigurationRepository.save(configuration);
        }

        if(!parameterConfigurationRepository.findByConfigurationName(ConfigurationCodes.ID_DIDI_ISSUER.getCode()).isPresent()){
            ParameterConfiguration configuration = new ParameterConfiguration();
            configuration.setValue("1234567890");
            configuration.setConfigurationName(ConfigurationCodes.ID_DIDI_ISSUER.getCode());
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

        if(processControlRepository.findByProcessName(ProcessNamesCodes.BONDAREA.getCode()).isEmpty()){
            ProcessControl process = new ProcessControl();
            process.setProcessName(ProcessNamesCodes.BONDAREA.getCode());
            process.setStatus(ProcessControlStatusCodes.OK.getCode());
            processControlRepository.save(process);
        }
        if(processControlRepository.findByProcessName(ProcessNamesCodes.CREDENTIALS.getCode()).isEmpty()){
            ProcessControl process = new ProcessControl();
            process.setProcessName(ProcessNamesCodes.CREDENTIALS.getCode());
            process.setStatus(ProcessControlStatusCodes.OK.getCode());
            //set an initial time (then will be use to compare)
            process.setStartTime(DateUtil.getLocalDateTimeNowWithFormat("yyyy-MM-dd HH:mm:ss"));
            processControlRepository.save(process);
        }
        if(processControlRepository.findByProcessName(ProcessNamesCodes.CHECK_DEFAULTERS.getCode()).isEmpty()){
            ProcessControl process = new ProcessControl();
            process.setProcessName(ProcessNamesCodes.CHECK_DEFAULTERS.getCode());
            process.setStatus(ProcessControlStatusCodes.OK.getCode());
            //set an initial time (then will be use to compare)
            process.setStartTime(DateUtil.getLocalDateTimeNowWithFormat("yyyy-MM-dd HH:mm:ss"));
            processControlRepository.save(process);
        }
	
	    this.loadCertTemplatesValues();
        this.saveCategories();
    }

    private void saveCategories(){
        String[] categories = new String[]{"Salud", "Oportunidad", "Saber", "Sueño", "Finanza"};
        for (String category : categories) {
            if(!providerCategoryRepository.findByName(category).isPresent()) {
                providerCategoryRepository.save(new ProviderCategory(category));
            }
        }


    }

    private void createUsers() throws ExistUserException {
        //TODO change pass
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
//TODO users only for test, delete
        if (!userService.findByUsername("flor@atixlabs.com").isPresent()) {
            UserEditRequest userResponse = new UserEditRequest();
            userResponse.setUsername("flor@atixlabs.com");
            userResponse.setEmail("flor@atixlabs.com");
            userResponse.setPassword("flor");
            userResponse.setNewPassword("flor");
            userResponse.setConfirmNewPassword("flor");
            userResponse.setRole(com.atixlabs.semillasmiddleware.security.enums.Role.ROLE_ADMIN.role());
            userResponse.setName("Florencia");
            userResponse.setLastName("Atix");
            userService.createOrEdit(userResponse);
        }

        if (!userService.findByUsername("facu@atixlabs.com").isPresent()) {
            UserEditRequest userResponse = new UserEditRequest();
            userResponse.setUsername("facu@atixlabs.com");
            userResponse.setEmail("facu@atixlabs.com");
            userResponse.setPassword("facu");
            userResponse.setNewPassword("facu");
            userResponse.setConfirmNewPassword("facu");
            userResponse.setRole(com.atixlabs.semillasmiddleware.security.enums.Role.ROLE_ADMIN.role());
            userResponse.setName("Facundo");
            userResponse.setLastName("Atix");
            userService.createOrEdit(userResponse);
        }

        if (!userService.findByUsername("tamara@semillas.com").isPresent()) {
            UserEditRequest userResponse = new UserEditRequest();
            userResponse.setUsername("tamara@semillas.com");
            userResponse.setEmail("tamara@semillas.com");
            userResponse.setPassword("tamara");
            userResponse.setNewPassword("tamara");
            userResponse.setConfirmNewPassword("tamara");
            userResponse.setRole(com.atixlabs.semillasmiddleware.security.enums.Role.ROLE_ADMIN.role());
            userResponse.setName("Tamara");
            userResponse.setLastName("Semillas");
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
    }


    private void createProductionUsers() throws ExistUserException {
        //TODO change pass
        if (!userService.findByUsername("admin").isPresent()) {
            UserEditRequest userResponse = new UserEditRequest();
            userResponse.setUsername("admin");
            userResponse.setEmail("admin@semillas.org");
            userResponse.setPassword("admin1q2w3e4r");
            userResponse.setNewPassword("admin1q2w3e4r");
            userResponse.setConfirmNewPassword("admin1q2w3e4r");
            userResponse.setRole(com.atixlabs.semillasmiddleware.security.enums.Role.ROLE_ADMIN.role());
            userResponse.setName("Admin");
            userResponse.setLastName("Admin");
            userService.createOrEdit(userResponse);
        }

        if (!userService.findByUsername("atixlabs").isPresent()) {
            UserEditRequest userResponse = new UserEditRequest();
            userResponse.setUsername("atixlabs");
            userResponse.setEmail("info@atixlabs.com");
            userResponse.setPassword("atix1q2w3e4r");
            userResponse.setNewPassword("atix1q2w3e4r");
            userResponse.setConfirmNewPassword("atix1q2w3e4r");
            userResponse.setRole(com.atixlabs.semillasmiddleware.security.enums.Role.ROLE_ADMIN.role());
            userResponse.setName("Atix");
            userResponse.setLastName("Labs");
            userService.createOrEdit(userResponse);
        }

        if (!userService.findByUsername("tamara@semillas.com").isPresent()) {
            UserEditRequest userResponse = new UserEditRequest();
            userResponse.setUsername("tamara@semillas.com");
            userResponse.setEmail("tamara@semillas.com");
            userResponse.setPassword("tama1q2w3e4r");
            userResponse.setNewPassword("tama1q2w3e4r");
            userResponse.setConfirmNewPassword("tama1q2w3e4r");
            userResponse.setRole(com.atixlabs.semillasmiddleware.security.enums.Role.ROLE_ADMIN.role());
            userResponse.setName("Tamara");
            userResponse.setLastName("Semillas");
            userService.createOrEdit(userResponse);
        }

        if (!userService.findByUsername("cronUser@atixlabs.com").isPresent()) {
            UserEditRequest userResponse = new UserEditRequest();
            userResponse.setUsername("cronUser@atixlabs.com");
            userResponse.setEmail("cronUser@atixlabs.com");
            userResponse.setPassword("cron1q2w3e4r");
            userResponse.setNewPassword("cron1q2w3e4r");
            userResponse.setConfirmNewPassword("cron1q2w3e4r");
            userResponse.setRole(com.atixlabs.semillasmiddleware.security.enums.Role.ROLE_ADMIN.role());
            userResponse.setName("cronUser");
            userResponse.setLastName("cronUser");
            userService.createOrEdit(userResponse);
        }

        if (!userService.findByUsername("didiUser@atixlabs.com").isPresent()) {
            UserEditRequest userResponse = new UserEditRequest();
            userResponse.setUsername("didiUser@atixlabs.com");
            userResponse.setEmail("didiUser@atixlabs.com");
            userResponse.setPassword("didi1q2w3e4r");
            userResponse.setNewPassword("didi1q2w3e4r");
            userResponse.setConfirmNewPassword("didi1q2w3e4r");
            userResponse.setRole(com.atixlabs.semillasmiddleware.security.enums.Role.ROLE_ADMIN.role());
            userResponse.setName("didiUser");
            userResponse.setLastName("didiUser");
            userService.createOrEdit(userResponse);
        }
        if (!userService.findByUsername("viewer").isPresent()) {
            UserEditRequest userResponse = new UserEditRequest();
            userResponse.setUsername("viewer");
            userResponse.setEmail("viewer@semillas.com");
            userResponse.setPassword("viewer1q2w3e4r");
            userResponse.setNewPassword("viewer1q2w3e4r");
            userResponse.setConfirmNewPassword("viewer1q2w3e4r");
            userResponse.setRole(com.atixlabs.semillasmiddleware.security.enums.Role.ROLE_VIEWER.role());
            userResponse.setName("Viewer");
            userResponse.setLastName("Viewer");
            userService.createOrEdit(userResponse);
        }
    }

/**
     *    IDENTITY("Identidad"),
     *     DWELLING("Vivienda"),
     *     ENTREPRENEURSHIP("Emprendimiento"),
     *     BENEFIT("Beneficio Semillas"),
     *     CREDIT("Crediticia");
     */
    private void loadCertTemplatesValues(){
        if(!this.isCertTemplateValueExists(CredentialCategoriesCodes.IDENTITY)){
            CertTemplate certTemplate = new CertTemplate(CredentialCategoriesCodes.IDENTITY,didiTemplateCodeIdentity,"Semillas Identidad" );
            certTemplateRepository.save(certTemplate);
        }
        if(!this.isCertTemplateValueExists(CredentialCategoriesCodes.DWELLING)){
            CertTemplate certTemplate = new CertTemplate(CredentialCategoriesCodes.DWELLING,didiTemplateCodeDwelling,"Semillas Vivienda" );
            certTemplateRepository.save(certTemplate);
        }
        if(!this.isCertTemplateValueExists(CredentialCategoriesCodes.ENTREPRENEURSHIP)){
            CertTemplate certTemplate = new CertTemplate(CredentialCategoriesCodes.ENTREPRENEURSHIP,didiTemplateCodeEntrepreneurship,"Semillas Emprendimiento" );
            certTemplateRepository.save(certTemplate);
        }
        if(!this.isCertTemplateValueExists(CredentialCategoriesCodes.CREDIT)){
            CertTemplate certTemplate = new CertTemplate(CredentialCategoriesCodes.CREDIT,didiTemplateCodeCredit,"Semillas Crediticia" );
            certTemplateRepository.save(certTemplate);
        }
        if(!this.isCertTemplateValueExists(CredentialCategoriesCodes.BENEFIT)){
            CertTemplate certTemplate = new CertTemplate(CredentialCategoriesCodes.BENEFIT,didiTemplateCodeBenefit,"Semillas Beneficio" );
            certTemplateRepository.save(certTemplate);
        }

        if(!this.isCertTemplateValueExists(CredentialCategoriesCodes.BENEFIT_SANCOR)){
            CertTemplate certTemplate = new CertTemplate(CredentialCategoriesCodes.BENEFIT_SANCOR,didiTemplateCodeSancorSalud,"Sancor Salud" );
            certTemplateRepository.save(certTemplate);
        }
    }
    private boolean isCertTemplateValueExists(CredentialCategoriesCodes credentialCategoriesCodes){

        Optional<CertTemplate> certTemplate =  certTemplateRepository.findByCredentialCategoriesCodes(credentialCategoriesCodes);
        return certTemplate.isPresent();
    }
}
