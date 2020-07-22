package com.atixlabs.semillasmiddleware.app.didi.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;

@Getter
@Setter
@ToString
public class DidiCreateCredentialWithMicroCrendentialsResponse extends  DidiCreateCredentialCommonResponse{

    private DidiCredentialWithMicroCredentials data;



}
