package com.atixlabs.semillasmiddleware.app.repository;

import com.atixlabs.semillasmiddleware.app.model.credential.CredentialIdentity;
import com.atixlabs.semillasmiddleware.app.model.credentialState.CredentialState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CredentialIdentityRepository extends JpaRepository<CredentialIdentity, Long> {

    List<CredentialIdentity> findByCreditHolderDniAndCredentialStateIn(Long holderDni, List<CredentialState> credentialActivePending);

    List<CredentialIdentity> findByCreditHolderDniAndBeneficiaryDniAndCredentialStateIn(Long holderDni, Long beneficiaryDni, List<CredentialState> credentialActivePending);

    List<CredentialIdentity> findByCreditHolderDniNotAndBeneficiaryDniAndCredentialDescriptionAndCredentialStateIn(Long dniNotHolder, Long beneficiaryDni, String credentialType, List<CredentialState> credentialActivePending);

    //didi
    List<CredentialIdentity> findByIdDidiReceptorAndBeneficiaryDniAndCredentialDescriptionAndCredentialStateIn(String idDidiReceptor, Long beneficiaryDni, String credentialType, List<CredentialState> states);
}
