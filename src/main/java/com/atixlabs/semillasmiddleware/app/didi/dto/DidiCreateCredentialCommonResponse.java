package com.atixlabs.semillasmiddleware.app.didi.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public abstract  class DidiCreateCredentialCommonResponse {
    protected String status;

    protected String errorCode;//when error
    protected String message;//when error
}
