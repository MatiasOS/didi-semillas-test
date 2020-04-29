package com.atixlabs.semillasmiddleware.security.controller;

import com.atixlabs.semillasmiddleware.security.JwtTokenProvider;
import com.atixlabs.semillasmiddleware.security.configuration.CustomUser;
import com.atixlabs.semillasmiddleware.security.dto.AuthenticatedUserDto;
import com.atixlabs.semillasmiddleware.security.dto.JwtAuthenticationResponse;
import com.atixlabs.semillasmiddleware.security.dto.JwtRequest;
import com.atixlabs.semillasmiddleware.security.exceptions.InactiveUserException;
import com.atixlabs.semillasmiddleware.security.service.JwtUserDetailsService;
import com.atixlabs.semillasmiddleware.security.service.UserPermissionsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/auth")
@CrossOrigin(
        origins = "*",
        methods = {RequestMethod.GET, RequestMethod.POST})
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private JwtUserDetailsService userDetailsService;

    @Autowired
    private UserPermissionsService userPermissionsService;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {
        try {
            Authentication authentication = this.authenticate(authenticationRequest.getUsername().trim(), authenticationRequest.getPassword());

            SecurityContextHolder.getContext().setAuthentication(authentication);
            CustomUser customUser = (CustomUser) authentication.getPrincipal();

            final String token = jwtTokenProvider.generateToken(customUser);
            log.info("Generated token [" + token + "] for user [" + authenticationRequest.getUsername() + "]");

            Optional<AuthenticatedUserDto> authenticatedUserDto;

            authenticatedUserDto = userPermissionsService.findUserAuthenticated(authenticationRequest.getUsername(), authenticationRequest.getPassword());


            if (authenticatedUserDto.isPresent()) {
                authenticatedUserDto.get().setAccessToken(token);
                return ResponseEntity.ok(authenticatedUserDto);
            } else {
                return ResponseEntity.ok(new JwtAuthenticationResponse(token));
            }
        } catch (InactiveUserException ex) {
            log.error("User [" + authenticationRequest.getUsername() + "] inactive");
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Unable to login. Please contact the system administrator");
        }

    }

    private Authentication authenticate(String username, String password) throws Exception {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            return authentication;
        } catch (DisabledException e) {
            log.info("USER_DISABLED", e);
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            log.info("INVALID_CREDENTIALS", e);
            throw new Exception("INVALID_CREDENTIALS", e);
        } catch (Exception e) {
            log.info("UNKNOWN ERROR", e);
            throw new Exception("UNKNOWN_ERROR", e);
        }
    }


    @RequestMapping(value = "/isauth", method = RequestMethod.GET)
    public Boolean isAuth() throws Exception {
        return true;
    }


    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public void logout(@AuthenticationPrincipal CustomUser usuarioActual) {
        //   logger.info("Perform authentication Logout " + usuarioActual.getUsername());
        jwtTokenProvider.revoqueToken(usuarioActual.getUsername());
    }


}

