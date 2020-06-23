package com.atixlabs.semillasmiddleware.app.processControl.model.constant;

public enum ProcessControlStatusCodes {
    OK("OK"),
    RUNNING("RUNNING"),
    FAIL("ERROR");

    private String code;

    ProcessControlStatusCodes(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }
}
