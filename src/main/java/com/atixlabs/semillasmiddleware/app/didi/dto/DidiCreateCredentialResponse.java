package com.atixlabs.semillasmiddleware.app.didi.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;

@Getter
@Setter
@ToString
public class DidiCreateCredentialResponse extends  DidiCreateCredentialCommonResponse{

    private ArrayList<DidiCredential> data;

}