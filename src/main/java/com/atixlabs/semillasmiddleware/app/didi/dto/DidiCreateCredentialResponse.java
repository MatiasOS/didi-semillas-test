package com.atixlabs.semillasmiddleware.app.didi.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;

@Getter
@Setter
@ToString
public class DidiCreateCredentialResponse {
    private String status;
    //private ArrayList<DidiCredential> data;

    private Object data;

    private String code;//when error
    private String message;//when error

}