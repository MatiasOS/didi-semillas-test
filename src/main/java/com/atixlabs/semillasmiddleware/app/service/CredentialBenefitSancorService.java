package com.atixlabs.semillasmiddleware.app.service;

import com.atixlabs.semillasmiddleware.app.bondarea.model.Loan;
import com.atixlabs.semillasmiddleware.app.didi.model.DidiAppUser;
import com.atixlabs.semillasmiddleware.app.didi.service.DidiService;
import com.atixlabs.semillasmiddleware.app.exceptions.CredentialException;
import com.atixlabs.semillasmiddleware.app.model.beneficiary.Person;
import com.atixlabs.semillasmiddleware.app.model.configuration.ParameterConfiguration;
import com.atixlabs.semillasmiddleware.app.model.configuration.constants.ConfigurationCodes;
import com.atixlabs.semillasmiddleware.app.model.credential.CredentialBenefitSancor;
import com.atixlabs.semillasmiddleware.app.model.credential.constants.CredentialCategoriesCodes;
import com.atixlabs.semillasmiddleware.app.model.credential.constants.CredentialTypesCodes;
import com.atixlabs.semillasmiddleware.app.model.credentialState.CredentialState;
import com.atixlabs.semillasmiddleware.app.repository.CredentialBenefitSancorRepository;
import com.atixlabs.semillasmiddleware.app.repository.CredentialRepository;
import com.atixlabs.semillasmiddleware.app.repository.ParameterConfigurationRepository;
import com.atixlabs.semillasmiddleware.app.repository.RevocationReasonRepository;
import com.atixlabs.semillasmiddleware.app.sancor.model.SancorPolicy;
import com.atixlabs.semillasmiddleware.app.sancor.service.SancorPolicyService;
import com.atixlabs.semillasmiddleware.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class CredentialBenefitSancorService extends CredentialBenefitCommonService<CredentialBenefitSancor> {

    private CredentialBenefitSancorRepository credentialBenefitSancorRepository;


    private ParameterConfigurationRepository parameterConfigurationRepository;

    private SancorPolicyService sancorPolicyService;

    @Autowired
    public CredentialBenefitSancorService(PersonService personService, ParameterConfigurationRepository parameterConfigurationRepository, CredentialStateService credentialStateService, CredentialBenefitSancorRepository credentialBenefitSancorRepository, DidiService didiService, RevocationReasonRepository revocationReasonRepository, CredentialRepository credentialRepository, SancorPolicyService sancorPolicyService){
        super(personService,credentialStateService, didiService, revocationReasonRepository, credentialRepository);
        this.credentialBenefitSancorRepository = credentialBenefitSancorRepository;
        this.parameterConfigurationRepository = parameterConfigurationRepository;
        this.sancorPolicyService = sancorPolicyService;

    }


    @Override
    Optional<CredentialBenefitSancor> getHolderCredentialBenefit(Person holder) {
        return this.getCredentialBenefits(holder.getDocumentNumber(), holder.getDocumentNumber());
    }

    public Optional<CredentialBenefitSancor> getCredentialBenefits(Long holderDni, Long beneficiaryDni) {
        return credentialBenefitSancorRepository.findTopByCreditHolderDniAndBeneficiaryDniOrderByIdDesc(holderDni, beneficiaryDni);
    }


    @Override
    public Optional<CredentialBenefitSancor> getCredentialBenefitsHolder(Long holderDni){
        return this.getCredentialBenefits(holderDni, holderDni);
    }


    /**
     * Create Benefit Sancor credential without Id Didi and pending didi
     *
     * @param holder
     *
     * @return
     */
    @Override
    public CredentialBenefitSancor buildNewHolderBenefitsCredential(Person holder) throws CredentialException {
        CredentialBenefitSancor credentialBenefitSancor = new CredentialBenefitSancor();

        Optional<ParameterConfiguration> config = parameterConfigurationRepository.findByConfigurationName(ConfigurationCodes.ID_DIDI_ISSUER.getCode());

        if (config.isPresent()) {
            credentialBenefitSancor.setIdDidiIssuer(config.get().getValue());
        } else {
            //
            throw new CredentialException("Id Didi Issuer Not exists, cant build credential");
        }

        CredentialState statePendingDidi = credentialStateService.getCredentialPendingDidiState();
        credentialBenefitSancor.setCredentialState(statePendingDidi);

        credentialBenefitSancor.setCredentialDescription(CredentialTypesCodes.CREDENTIAL_BENEFITS_SANCOR.getCode());

        credentialBenefitSancor.setDateOfIssue(DateUtil.getLocalDateTimeNow());
        credentialBenefitSancor.setCredentialCategory(CredentialCategoriesCodes.BENEFIT_SANCOR.getCode());

        credentialBenefitSancor.setCreditHolder(holder);

        credentialBenefitSancor.setBeneficiary(holder);

        Optional<SancorPolicy>  opSancorPolicy = this.sancorPolicyService.findByCertificateClientDni(credentialBenefitSancor.getBeneficiaryDni());

        if(opSancorPolicy.isPresent()) {

            credentialBenefitSancor.addPolicyData(opSancorPolicy.get());
        }

        return credentialBenefitSancor;
    }

    public CredentialBenefitSancor buildHolderBenefitsCredential(CredentialBenefitSancor credentialBenefitSancor, Optional<SancorPolicy> opSancorPolicy) throws CredentialException {

        CredentialBenefitSancor newCredentialBenefitSancor = new CredentialBenefitSancor(credentialBenefitSancor);

        CredentialState statePendingDidi = credentialStateService.getCredentialPendingDidiState();
        newCredentialBenefitSancor.setCredentialState(statePendingDidi);

        newCredentialBenefitSancor.setDateOfIssue(DateUtil.getLocalDateTimeNow());

        if(opSancorPolicy.isPresent()) {
            newCredentialBenefitSancor.addPolicyData(opSancorPolicy.get());
        }

        return newCredentialBenefitSancor;
    }

    /**
     * Si el credito esta activo
     * Sancor
     *
     *     Si es el unico credito, revoco la credencial
     *     si tiene mas creditos no hago nada
     *sie sta en mora
     * Sancor
     *
     *     Si es el unico credito, revoco la credencial si es necesario
     *     Si tiene mas creditos no hago nada
     * @param loanFinalized
     * @param otherLoansActiveForHolder
     * @return
     * @throws CredentialException
     */
    public List<Loan> handleLoanFinalized(Loan loanFinalized, List<Loan> otherLoansActiveForHolder) throws CredentialException {

        if (otherLoansActiveForHolder == null || otherLoansActiveForHolder.isEmpty()) {
            Optional<Person> holder = this.personService.findByDocumentNumber(loanFinalized.getDniPerson());
            if(holder.isPresent()){
                log.info("Revoking Holder {} Sancor credentials");
                this.revokeHolderCredentialsBenefitsForLoan(holder.get());
            }
        }else{
            log.info("Holder {} has more credits actives, not revoke Sancor credential");
        }

        return new ArrayList<Loan>();
    }


        @Override
    public CredentialBenefitSancor saveCredentialBenefit(CredentialBenefitSancor credential) {
        credential = credentialBenefitSancorRepository.save(credential);
        if (credential.getIdHistorical() == null) {
            credential.setIdHistorical(credential.getId());
            credential = credentialBenefitSancorRepository.save(credential);

        }
        return credential;
    }

    public List<CredentialBenefitSancor> getCredentialBenefitSancorsOnPendindDidiState() throws CredentialException {
        CredentialState pendingDidiState = credentialStateService.getCredentialPendingDidiState();

        return  credentialBenefitSancorRepository.findByCredentialState(pendingDidiState);
    }

    public CredentialBenefitSancor save(CredentialBenefitSancor credentialBenefitSancor){
        return credentialBenefitSancorRepository.save(credentialBenefitSancor);
    }

    public List<CredentialBenefitSancor> getCredentialBenefitSancorActiveForDni(Long dni) throws CredentialException {
        Optional<CredentialState> activeDidiState = credentialStateService.getCredentialActiveState();

        return credentialBenefitSancorRepository.findByCreditHolderDniAndCredentialState(dni, activeDidiState.get());
    }

    public CredentialBenefitSancor buildNewOnPendidgDidi(CredentialBenefitSancor credentialBenefitSancor, DidiAppUser newDidiAppUser) throws CredentialException {
        CredentialBenefitSancor newCredentialBenefitSancor =  new CredentialBenefitSancor(credentialBenefitSancor);
        newCredentialBenefitSancor.setIdDidiReceptor(newDidiAppUser.getDid());

        this.resetStateOnPendingDidi(newCredentialBenefitSancor);

        return newCredentialBenefitSancor;
    }

    @Override
    protected Logger getLog() {
        return log;
    }
}
