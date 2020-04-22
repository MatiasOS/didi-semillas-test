package com.atixlabs.semillasmiddleware.app.model.credential.constants;

public enum CredentialTypesCodes {

    CREDENTIAL_CREDIT("Creditos Semillas"),
    CREDENTIAL_DWELLING("Credito Viviendo"),
    CREDENTIAL_IDENTITY("Identidad - Titular"),
    CREDENTIAL_IDENTITY_FAMILIAR("Identidad - Familiar"),
    CREDENTIAL_ENTREPRENEURSHIP("Credito Emprendedor"),
    CREDENTIAL_ACADEMIC_CREDIT("Credito Escolar"),
    CREDENTIAL_BENEFITS("Beneficios Sembrando Semillas Titular"),
    CREDENTIAL_BENEFITS_FAMILIAR("Beneficios Sembrando Semillas Familiar"),
    CREDENTIAL_PERSONAL_DATA("Datos Personales Titular"),
    CREDENTIAL_PERSONAL_DATA_FAMILIAR("Datos Personales Familiar");

    private String code;

    CredentialTypesCodes(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }
}
