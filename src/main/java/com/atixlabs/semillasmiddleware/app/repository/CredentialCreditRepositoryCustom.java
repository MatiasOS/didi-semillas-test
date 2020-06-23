package com.atixlabs.semillasmiddleware.app.repository;

import com.atixlabs.semillasmiddleware.app.model.credential.Credential;
import com.atixlabs.semillasmiddleware.app.model.credential.CredentialCredit;

import java.util.List;

public interface CredentialCreditRepositoryCustom {

    List<CredentialCredit> findLastCreditsDistinctIdHistoricalOrderByDateOfIssueDesc(Long dniHolder);

}
