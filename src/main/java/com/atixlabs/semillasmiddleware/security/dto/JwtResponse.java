package com.atixlabs.semillasmiddleware.security.dto;

import lombok.Getter;
import java.io.Serializable;

//TODO DELETE
@Deprecated
@Getter
public class JwtResponse implements Serializable {

    private final String accessToken;

    public JwtResponse(String accessToken) {
        this.accessToken = accessToken;
    }

}