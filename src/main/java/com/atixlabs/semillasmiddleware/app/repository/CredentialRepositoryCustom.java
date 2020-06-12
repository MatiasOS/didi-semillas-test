package com.atixlabs.semillasmiddleware.app.repository;

import com.atixlabs.semillasmiddleware.app.model.credential.Credential;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CredentialRepositoryCustom {

    List<Credential> findCredentialsWithFilter(String credentialType, String name, String dniBeneficiary, String idDidiCredential, String lastUpdate, List<String> credentialState, Pageable page);

}
