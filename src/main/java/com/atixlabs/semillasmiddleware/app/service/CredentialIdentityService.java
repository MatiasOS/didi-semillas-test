package com.atixlabs.semillasmiddleware.app.service;

import com.atixlabs.semillasmiddleware.app.didi.model.DidiAppUser;
import com.atixlabs.semillasmiddleware.app.didi.service.DidiService;
import com.atixlabs.semillasmiddleware.app.exceptions.CredentialException;
import com.atixlabs.semillasmiddleware.app.model.credential.CredentialBenefits;
import com.atixlabs.semillasmiddleware.app.model.credential.CredentialIdentity;
import com.atixlabs.semillasmiddleware.app.model.credential.constants.CredentialRelationHolderType;
import com.atixlabs.semillasmiddleware.app.model.credentialState.CredentialState;
import com.atixlabs.semillasmiddleware.app.repository.CredentialIdentityRepository;
import com.atixlabs.semillasmiddleware.app.repository.CredentialRepository;
import com.atixlabs.semillasmiddleware.app.repository.RevocationReasonRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class CredentialIdentityService extends CredentialCommonService {

    private CredentialIdentityRepository credentialIdentityRepository;


    public CredentialIdentityService( CredentialStateService credentialStateService, DidiService didiService, RevocationReasonRepository revocationReasonRepository, CredentialRepository credentialRepository, CredentialIdentityRepository credentialIdentityRepository){
        super(credentialStateService, didiService, revocationReasonRepository, credentialRepository);
        this.credentialIdentityRepository = credentialIdentityRepository;
    }

    public List<CredentialIdentity> getCredentialIdentityOnPendindDidiState() throws CredentialException {
        CredentialState pendingDidiState = credentialStateService.getCredentialPendingDidiState();

        return  credentialIdentityRepository.findByCredentialState(pendingDidiState);
    }


    /**
     * get credential type
     * Id Didi (Holder) (not param dni), Dni Holder (not param dni), Dni beneficiary (param dni), type HOLDER
     * @param dni
     * @return
     * @throws CredentialException
     */
    public List<CredentialIdentity> getAllCredentialIdentityActivesOrPendingDidiForBeneficiaryDniAsFamilyAndTypeHolder(Long dni) throws CredentialException {
        //dni is beneficiary but not holder, and type is holder
        return credentialIdentityRepository.findByBeneficiaryDniAndCredentialStateInAndRelationWithCreditHolderAndCreditHolderDniNot(dni, getActiveAndPendingDidiStates(), CredentialRelationHolderType.HOLDER.getCode(), dni);

    }

    /**
     * get credential type
     * Id Didi (Beneficiary) (param dni), Dni Holder (not param dni), Dni beneficiary (param dni), type KINSMAN
     * @param holderDni, beneficiaryDni
     * @return
     */
    public Boolean existsCredentialIdentityActivesOrPendingDidiForBeneficiaryDniAsFamilyAndTypeKinsman(Long holderDni, Long beneficiaryDni) {
        //dni is beneficiary but not holder, and type is holder
        return credentialIdentityRepository.existsByBeneficiaryDniAndRelationWithCreditHolderAndCreditHolderDni(beneficiaryDni, CredentialRelationHolderType.KINSMAN.getCode(), holderDni);

    }



    /**
     * Abuelo Beneficiario en la encuesta, Flor Familiar
     * Se crean las Credenciales de identidad
     * 1- Titular  Id didi (Abuelo),  Dni Titular (Abuelo), DNI Beneficiario (Abuelo), Caracter (Titular)
     * 2-Familiar Id didi (Abuelo),  Dni Titular (Abuelo), DNI Beneficiario (Flor),  Caracter (Titular)
     *
     * Flor genera si id didi
     * Se crean las credenciales
     * 5-Familiar  Id didi (Flor),  Dni Titular (Abuelo), DNI Beneficiario (Flor),  Caracter (Familiar)
     * @param dni
     * @return
     */
    public List<CredentialIdentity> getAllCredentialIdentityActivesForDni(Long dni) throws CredentialException {
        List<CredentialIdentity> asHolder = this.getCredentialIdentityActivesForDniAndTypeHolder(dni);
        List<CredentialIdentity> asFamily = this.getCredentialIdentityActivesForDniAndTypeFamiliy(dni);

        List<CredentialIdentity> credentials = new ArrayList<CredentialIdentity>();

        if(asHolder != null)
            credentials.addAll(asHolder);

        if(asFamily != null)
            credentials.addAll(asFamily);

        return credentials;
    }

    public List<CredentialIdentity> getCredentialIdentityActivesForDniAndTypeFamiliy(Long dni) throws CredentialException {
        Optional<CredentialState> activeDidiState = credentialStateService.getCredentialActiveState();

        return credentialIdentityRepository.findByBeneficiaryDniAndCredentialStateAndRelationWithCreditHolder(dni, activeDidiState.get(), CredentialRelationHolderType.KINSMAN.getCode());
    }

    public List<CredentialIdentity> getCredentialIdentityActivesForDniAndTypeHolder(Long dni) throws CredentialException {
        Optional<CredentialState> activeDidiState = credentialStateService.getCredentialActiveState();

        return credentialIdentityRepository.findByCreditHolderDniAndCredentialStateAndRelationWithCreditHolder(dni, activeDidiState.get(), CredentialRelationHolderType.HOLDER.getCode());
    }


    public CredentialIdentity save(CredentialIdentity credentialIdentity){
        return credentialIdentityRepository.save(credentialIdentity);
    }

    public CredentialIdentity buildNewOnPendidgDidi(CredentialIdentity credentialIdentity, DidiAppUser newDidiAppUser) throws CredentialException {
        CredentialIdentity newCredentialIdentity =  new CredentialIdentity(credentialIdentity);
        newCredentialIdentity.setIdDidiReceptor(newDidiAppUser.getDid());

        this.resetStateOnPendingDidi(newCredentialIdentity);

        return newCredentialIdentity;
    }

    public CredentialIdentity buildNewOnPendidgDidiAsKinsmanType(CredentialIdentity credentialIdentity, DidiAppUser newDidiAppUser) throws CredentialException {
        CredentialIdentity newCredentialIdentity =  new CredentialIdentity(credentialIdentity, CredentialRelationHolderType.KINSMAN.getCode());
        newCredentialIdentity.setIdDidiReceptor(newDidiAppUser.getDid());

        this.resetStateOnPendingDidi(newCredentialIdentity);

        return newCredentialIdentity;
    }

    public List<CredentialIdentity> findByCreditHolderDniAndCredentialStateIn(Long holderDni, List<CredentialState> credentialActivePending){
        return credentialIdentityRepository.findByCreditHolderDniAndCredentialStateIn(holderDni,credentialActivePending);
    }

    public List<CredentialIdentity> findByCreditHolderDniAndBeneficiaryDniAndCredentialStateIn(Long holderDni, Long beneficiaryDni, List<CredentialState> credentialActivePending){
        return credentialIdentityRepository.findByCreditHolderDniAndBeneficiaryDniAndCredentialStateIn(holderDni,beneficiaryDni,credentialActivePending);
    }



    @Override
    protected Logger getLog() {
        return log;
    }
}
