package com.atixlabs.semillasmiddleware.app.bondarea.model.constants;

public enum LoanStateCodes {

    OK("Ok"),
    DEFAULT("Default");


    private String code;

    LoanStateCodes(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }
}
