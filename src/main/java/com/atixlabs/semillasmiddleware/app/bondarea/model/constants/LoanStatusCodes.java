package com.atixlabs.semillasmiddleware.app.bondarea.model.constants;

public enum LoanStatusCodes {

    ACTIVE("Al dia"), //this constant is used in LoanRepository for query!
    FINALIZED("Finalizado"),
    PENDING("Pendiente"),
    CANCELLED("Cancelado");


    private String code;

    LoanStatusCodes(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }
}
