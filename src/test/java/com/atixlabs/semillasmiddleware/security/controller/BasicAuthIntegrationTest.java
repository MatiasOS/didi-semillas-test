package com.atixlabs.semillasmiddleware.security.controller;

import com.atixlabs.semillasmiddleware.security.dto.JwtRequest;
import com.atixlabs.semillasmiddleware.security.model.Role;
import com.atixlabs.semillasmiddleware.security.model.User;
import com.atixlabs.semillasmiddleware.security.repository.RoleRepository;
import com.atixlabs.semillasmiddleware.security.repository.UserRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import static io.restassured.RestAssured.given;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Slf4j
public abstract class BasicAuthIntegrationTest {

    public final String URL_AUTH = "/auth/login";
    public final String URL_ISAUTH = "/auth/isauth";
    public final String URL_LOGOUT = "/auth/logout";

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected RoleRepository roleRepository;

    @LocalServerPort
    protected int port;

    @Before
    public void prepareUsers() {

        Role roleAdmin = new Role();
        roleAdmin.setCode("ROLE_ADMIN");
        roleAdmin.setDescription("Role Admin");

        roleRepository.save(roleAdmin);

        User admin = new User();
        admin.setActive(true);
        admin.setEmail("admin@semillas.com");
        admin.setName("Admin Name");
        admin.setLastName("Admin Lastname");
        admin.setUsername("admin");
        admin.setPassword("$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6");
        admin.setRole(roleAdmin);

        userRepository.save(admin);

    }

    @After
    public void cleanDB() {
        userRepository.deleteAll();
        roleRepository.deleteAll();

    }

    protected Response login(JwtRequest logging) {
        RestAssured.port = this.port;

        Response response  = given()
                .contentType(ContentType.JSON)
                .body(logging)
                .when().post(URL_AUTH);

        return response;
    }

    protected String loginAndGetToken(JwtRequest logging) {
        Response response = login(logging);
        return response.andReturn().jsonPath().getString("accessToken");
    }
}
