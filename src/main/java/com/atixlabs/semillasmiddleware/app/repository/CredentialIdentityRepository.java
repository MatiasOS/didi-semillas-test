package com.atixlabs.semillasmiddleware.app.repository;

import com.atixlabs.semillasmiddleware.app.model.beneficiary.Person;
import com.atixlabs.semillasmiddleware.app.model.credential.CredentialBenefitSancor;
import com.atixlabs.semillasmiddleware.app.model.credential.CredentialCredit;
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

    @Query("SELECT DISTINCT ci.beneficiary FROM  CredentialIdentity ci WHERE ci.creditHolder = :holder and ci.beneficiary <> :holder")
    List<Person> findDistinctBeneficiaryFamilyByHolder(@Param("holder") Person holder);

    List<CredentialIdentity> findByCredentialState(CredentialState credentialState);

    Optional<CredentialIdentity> findTopByCreditHolderDniAndCredentialStateOrderByDateOfIssueDesc(Long holderDni, CredentialState credentialState);

    List<CredentialIdentity> findByCreditHolderDniAndCredentialStateAndRelationWithCreditHolder(Long holderDni, CredentialState credentialState, String relationWithHolder);

    List<CredentialIdentity> findByBeneficiaryDniAndCredentialStateAndRelationWithCreditHolder(Long beneficiaryDni, CredentialState credentialState, String relationWithHolder);

    List<CredentialIdentity> findByBeneficiaryDniAndCredentialStateInAndRelationWithCreditHolderAndCreditHolderDniNot(Long beneficiaryDni, List<CredentialState> credentialActivePending, String relationWithHolder, Long beneficiaryAsHolderDni);

    Boolean existsByBeneficiaryDniAndRelationWithCreditHolderAndCreditHolderDni(Long beneficiaryDni, String relationWithHolder, Long holderDni);

}
