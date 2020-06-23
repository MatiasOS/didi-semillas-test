package com.atixlabs.semillasmiddleware.app.bondarea.model.constants;

public enum LoanStatusCodes {

    ACTIVE("Active"), //this constant is used in LoanRepository for query!
    FINALIZED("Finalized"),
    PENDING("Pending"),
    CANCELLED("Cancelled");


    private String code;

    LoanStatusCodes(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }
}
