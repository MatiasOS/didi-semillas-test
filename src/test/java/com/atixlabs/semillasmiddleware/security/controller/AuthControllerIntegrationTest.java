package com.atixlabs.semillasmiddleware.security.controller;

import com.atixlabs.semillasmiddleware.security.JwtTokenProvider;
import com.atixlabs.semillasmiddleware.security.dto.JwtRequest;
import com.atixlabs.semillasmiddleware.security.model.Role;
import com.atixlabs.semillasmiddleware.security.model.User;
import com.atixlabs.semillasmiddleware.security.repository.RoleRepository;
import com.atixlabs.semillasmiddleware.security.repository.UserRepository;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class AuthControllerIntegrationTest extends BasicAuthIntegrationTest{

   /* @Autowired
    private JwtTokenProvider jwtTokenProvider;


    protected boolean logout(String token) {
        Response response = given()
                .contentType(ContentType.JSON)
                .headers("Authorization", "Bearer " + token)
                .when().get(URL_LOGOUT);
        return response
                .getStatusCode() == HttpStatus.SC_OK;
    }

    @Test
    public void loginSuccessful() {

        Response response = login(JwtRequest.builder().username("admin@atixlabs.com").password("admin").build());

        response.then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);

    }

    @Test
    public void loginUnsuccessful() {

        login(JwtRequest.builder().username("invalid user").password("no pass").build())
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED);
    }


    private Response isAuthCheck(String token) {
        return  given()
                .contentType(ContentType.JSON)
                .headers("Authorization", "Bearer " + token)
                .when().get(URL_ISAUTH);

    }

    @Test
    public void logoutSuccesfull() {

        String token = loginAndGetToken(JwtRequest.builder().username("admin").password("password").build());

        Response response = isAuthCheck(token);
        response.then().assertThat().statusCode(HttpStatus.SC_OK);
        Assert.assertTrue(response.getBody().print().equals("true"));

        Assert.assertTrue(logout(token));

        Response responseOut = isAuthCheck(token);
        responseOut.then().assertThat().statusCode(HttpStatus.SC_UNAUTHORIZED);
        Assert.assertFalse(response.getBody().equals(true));


    } */
}
