package com.atixlabs.semillasmiddleware.app.didi.dto;

public class DidiDeleteCredentialBody {
    private String reason;
    public DidiDeleteCredentialBody(String reason){
        switch (reason) {
            case "Expiracion de datos":
                this.reason = "EXPIRATION";
                break;
            case "Desvinculacion":
                this.reason = "UNLINKING";
                break;
            default:
                this.reason = "OTHER";
        }
    }
    public static String getReasonCode(String reason) {
        switch (reason) {
            case "Expiracion de datos":
                return "EXPIRATION";
            case "Desvinculacion":
                return "UNLINKING";
            default:
                return "OTHER";
        }
    }
}
