package com.atixlabs.semillasmiddleware.app.model.credential.constants;

public enum CredentialCategoriesCodes {

    FINANCE("Finanzas"),
    IDENTITY("Identidad"),
    LABOR("Laboral"),
    DWELLING("Vivienda"),
    BENEFIT("Beneficio Semillas"),
    EDUCATION("Educación");

    private String code;

    CredentialCategoriesCodes(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }
}
