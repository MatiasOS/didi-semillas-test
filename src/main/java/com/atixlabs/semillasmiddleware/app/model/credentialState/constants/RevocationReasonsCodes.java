package com.atixlabs.semillasmiddleware.app.model.credentialState.constants;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum RevocationReasonsCodes {

    //this reasons are internal reasons
    UPDATE_INTERNAL("UPDATE"),
    CANCELLED("CANCELLED"),
    DEFAULT("DEFAULT"),
    //----
    EXPIRED_INFO("Expiracion de datos"),
    UNLINKING("Desvinculacion");


    private String code;

    RevocationReasonsCodes(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

}
