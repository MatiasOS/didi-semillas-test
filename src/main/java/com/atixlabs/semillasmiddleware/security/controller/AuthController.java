package com.atixlabs.semillasmiddleware.security.controller;

import com.atixlabs.semillasmiddleware.security.configuration.CustomUser;
import com.atixlabs.semillasmiddleware.security.dto.JwtRequest;
import com.atixlabs.semillasmiddleware.security.dto.JwtResponse;
import com.atixlabs.semillasmiddleware.security.JwtTokenProvider;
import com.atixlabs.semillasmiddleware.security.service.JwtUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*", methods= {RequestMethod.GET,RequestMethod.POST})
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private JwtUserDetailsService userDetailsService;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {
        Authentication authentication = this.authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());

        SecurityContextHolder.getContext().setAuthentication(authentication);
        CustomUser customUser = (CustomUser) authentication.getPrincipal();

       // final UserDetails userDetails = userDetailsService
        //        .loadUserByUsername(authenticationRequest.getUsername());

        final String token = jwtTokenProvider.generateToken(customUser);
        return ResponseEntity.ok(new JwtResponse(token));
    }

    private Authentication authenticate(String username, String password) throws Exception {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            return authentication;
        } catch (DisabledException e) {
            logger.info("USER_DISABLED", e);
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            logger.info("INVALID_CREDENTIALS", e);
            throw new Exception("INVALID_CREDENTIALS", e);
        } catch (Exception e){
            logger.info("VARDOOO", e);
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }


    @RequestMapping(value = "/isauth", method = RequestMethod.GET)
    public Boolean isAuth() throws Exception {
        return true;
    }


    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public void logout(@AuthenticationPrincipal CustomUser usuarioActual) {
        logger.info("Perform authentication Logout " + usuarioActual.getUsername());
        jwtTokenProvider.revoqueToken(usuarioActual.getUsername());
    }
}

