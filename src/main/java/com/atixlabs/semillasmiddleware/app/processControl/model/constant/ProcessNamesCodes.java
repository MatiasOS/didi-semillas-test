package com.atixlabs.semillasmiddleware.app.processControl.model.constant;

public enum ProcessNamesCodes {
    BONDAREA("BONDAREA_SYNC"),
    CREDENTIALS("GENERATE_CREDENTIALS");

    private String code;

    ProcessNamesCodes(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }
}
