package com.atixlabs.semillasmiddleware.app.didi.repository;

import com.atixlabs.semillasmiddleware.app.didi.model.DidiAppUser;
import com.atixlabs.semillasmiddleware.app.model.credential.CredentialBenefits;
import com.atixlabs.semillasmiddleware.app.model.credential.CredentialCredit;
import com.atixlabs.semillasmiddleware.app.model.credentialState.CredentialState;
import org.checkerframework.checker.nullness.Opt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public interface DidiAppUserRepository extends JpaRepository<DidiAppUser, Long> {

    Optional<DidiAppUser> findByDniAndActiveTrue(Long dni);

    List<DidiAppUser> findByActiveTrue();

    Optional<DidiAppUser> findTopByActiveFalseAndDniOrderByDateOfRegistrationDesc(Long dni);

    //ArrayList<DidiAppUser> findBySyncStatusIn(ArrayList<String> didiSyncStatus);
}
