package com.atixlabs.semillasmiddleware.app.model.credential.constants;

public enum CredentialTypesCodes {

    CREDENTIAL_CREDIT("CredentialCredit"),
    CREDENTIAL_DWELLING("CredentialDwelling"),
    CREDENTIAL_IDENTITY("CredentialIdentity"),
    CREDENTIAL_ENTREPRENEURSHIP("CredentialEntrepreneurship");

    private String code;

    CredentialTypesCodes(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }
}
