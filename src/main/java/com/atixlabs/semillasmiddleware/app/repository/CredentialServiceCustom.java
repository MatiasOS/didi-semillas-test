package com.atixlabs.semillasmiddleware.app.repository;

import com.atixlabs.semillasmiddleware.app.model.credential.Credential;

import java.util.List;

public interface CredentialServiceCustom {

    List<Credential> findCredentialsWithFilter(String credentialType, String name, String dniBeneficiary, String idDidiCredential, String dateOfExpiry, String dateOfIssue, String credentialState);

}
