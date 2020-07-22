package com.atixlabs.semillasmiddleware.app.didi.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCertificateResult {

    private DidiCreateCredentialCommonResponse didiCreateCredentialCommonResponse;

    private String certificateId;

    public CreateCertificateResult(DidiCreateCredentialCommonResponse didiCreateCredentialCommonResponse, String certificateId){
        this.didiCreateCredentialCommonResponse = didiCreateCredentialCommonResponse;
        this.certificateId = certificateId;
    }
}
