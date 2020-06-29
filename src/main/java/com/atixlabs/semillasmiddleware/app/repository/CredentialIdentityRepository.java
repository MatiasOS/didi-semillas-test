package com.atixlabs.semillasmiddleware.app.repository;

import com.atixlabs.semillasmiddleware.app.model.beneficiary.Person;
import com.atixlabs.semillasmiddleware.app.model.credential.Credential;
import com.atixlabs.semillasmiddleware.app.model.credential.CredentialIdentity;
import com.atixlabs.semillasmiddleware.app.model.credentialState.CredentialState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CredentialIdentityRepository extends JpaRepository<CredentialIdentity, Long> {

    List<CredentialIdentity> findByCreditHolderDniAndCredentialStateIn(Long holderDni, List<CredentialState> credentialActivePending);

    List<CredentialIdentity> findByCreditHolderDniAndBeneficiaryDniAndCredentialStateIn(Long holderDni, Long beneficiaryDni, List<CredentialState> credentialActivePending);

    @Query("SELECT DISTINCT beneficiary WHERE creditHolder = :holder  and beneficiary <> :holder")
    Optional<List<Person>> findDistinctBeneficiaryFamilyByHolder(@Param("holder") Person holder);


}
