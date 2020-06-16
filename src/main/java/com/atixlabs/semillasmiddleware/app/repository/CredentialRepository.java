package com.atixlabs.semillasmiddleware.app.repository;

import com.atixlabs.semillasmiddleware.app.model.credential.Credential;
import com.atixlabs.semillasmiddleware.app.model.credential.CredentialCredit;
import com.atixlabs.semillasmiddleware.app.model.credentialState.CredentialState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public interface CredentialRepository extends JpaRepository<Credential, Long> , CredentialRepositoryCustom{

    //ArrayList<Credential> findByCredentialStateAndBeneficiaryDniIn(CredentialState credentialState, ArrayList<Long> dniList);

    ArrayList<Credential> findByCredentialCategoryAndCredentialState(String code, CredentialState credentialStatePending);

    Optional<Credential> findByBeneficiaryDniAndCredentialCategoryAndCredentialStateIn(Long beneficiaryDni, String credentialCategoryCode, List<CredentialState> credentialStateActivePending);

    List<Credential> findByCreditHolderDni(Long dni);

    List<Credential> findByBeneficiaryDni(Long dni);

    List<Credential> findByCreditHolderDniAndBeneficiaryDniAndCredentialStateIn(Long dniHolder, Long dniBeneficiary, List<CredentialState> states);

    List<Credential> findByBeneficiaryDniAndCredentialStateIn(Long dni, List<CredentialState> states);

    List<Credential> findByCreditHolderDniAndIdDidiReceptorNullOrIdDidiReceptorAndAndCredentialCategoryAndCredentialState(Long dniHolder, String didReceptor, String credentialCategoryCode, CredentialState state);

    //did
    List<Credential> findByIdDidiReceptor(String idReceptor);

}
