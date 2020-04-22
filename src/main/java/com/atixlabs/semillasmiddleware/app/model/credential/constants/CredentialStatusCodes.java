package com.atixlabs.semillasmiddleware.app.model.credential.constants;

public enum CredentialStatusCodes {

    CREDENTIAL_PENDING_BONDAREA("Pendiente-bondarea"),
    CREDENTIAL_PENDING_DIDI("Pendiente-didi");


    private String code;

    CredentialStatusCodes(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }
}
