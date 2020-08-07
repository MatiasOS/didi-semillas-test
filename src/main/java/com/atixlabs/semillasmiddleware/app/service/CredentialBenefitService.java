package com.atixlabs.semillasmiddleware.app.service;

import com.atixlabs.semillasmiddleware.app.bondarea.model.Loan;
import com.atixlabs.semillasmiddleware.app.didi.model.DidiAppUser;
import com.atixlabs.semillasmiddleware.app.didi.service.DidiService;
import com.atixlabs.semillasmiddleware.app.exceptions.CredentialException;
import com.atixlabs.semillasmiddleware.app.model.beneficiary.Person;
import com.atixlabs.semillasmiddleware.app.model.configuration.ParameterConfiguration;
import com.atixlabs.semillasmiddleware.app.model.configuration.constants.ConfigurationCodes;
import com.atixlabs.semillasmiddleware.app.model.credential.CredentialBenefitSancor;
import com.atixlabs.semillasmiddleware.app.model.credential.CredentialBenefits;
import com.atixlabs.semillasmiddleware.app.model.credential.CredentialCredit;
import com.atixlabs.semillasmiddleware.app.model.credential.constants.CredentialCategoriesCodes;
import com.atixlabs.semillasmiddleware.app.model.credential.constants.CredentialTypesCodes;
import com.atixlabs.semillasmiddleware.app.model.credential.constants.PersonTypesCodes;
import com.atixlabs.semillasmiddleware.app.model.credentialState.CredentialState;
import com.atixlabs.semillasmiddleware.app.model.credentialState.constants.RevocationReasonsCodes;
import com.atixlabs.semillasmiddleware.app.repository.CredentialBenefitsRepository;
import com.atixlabs.semillasmiddleware.app.repository.CredentialRepository;
import com.atixlabs.semillasmiddleware.app.repository.ParameterConfigurationRepository;
import com.atixlabs.semillasmiddleware.app.repository.RevocationReasonRepository;
import com.atixlabs.semillasmiddleware.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class CredentialBenefitService extends CredentialBenefitCommonService<CredentialBenefits> {

    //private PersonService personService;

   // private CredentialStateService credentialStateService;

    private CredentialBenefitsRepository credentialBenefitsRepository;

    private ParameterConfigurationRepository parameterConfigurationRepository;

    public CredentialBenefitService(PersonService personService, CredentialStateService credentialStateService, CredentialBenefitsRepository credentialBenefitsRepository, ParameterConfigurationRepository parameterConfigurationRepository, DidiService didiService, RevocationReasonRepository revocationReasonRepository, CredentialRepository credentialRepository){
       super(personService, credentialStateService, didiService, revocationReasonRepository, credentialRepository);
        //this.personService = personService;
        //this.credentialStateService = credentialStateService;
        this.credentialBenefitsRepository = credentialBenefitsRepository;
        this.parameterConfigurationRepository = parameterConfigurationRepository;
    }


    public void createCredentialsBenefitsFamilyForNewLoan(Loan loan) throws CredentialException {

        this.getLog().info("creating Credentials Benefits family for loan {}",loan.getIdBondareaLoan());
        Optional<Person> opHolder = personService.findByDocumentNumber(loan.getDniPerson());

        if (opHolder.isPresent()) {
            Person holder = opHolder.get();

            if (!holder.isInDefault()) { // If holder is not in default
                //Family
                Optional<List<Person>> opFamiliy = personService.findFamilyForHolder(holder);
                if ((opFamiliy.isPresent()) && (!opFamiliy.get().isEmpty())) {
                    List<Person> family = opFamiliy.get();
                    for (Person beneficiary : family) {
                        this.handleCredentialBenefitsForBeneficiary(holder, beneficiary, loan);
                    }
                } else {
                    this.getLog().info("The holder {} has no family ", holder.getDocumentNumber());
                }
            } else { //holder is in default
                String message = String.format("Credential Benefit for holder %d of loan %s is not created, holder is in default", holder.getDocumentNumber(), loan.getIdBondareaLoan());
                this.getLog().error(message);
                throw new CredentialException(message);
            }
        } else { //Holder not exists
            String message = String.format("Can't create Benefit Credential, Holder dni %d not exists", loan.getDateFirstInstalment());
            this.getLog().error(message);
            throw new CredentialException(message);
        }
    }

    /**
     * If holder is not in default
     * *             Benefit Holder
     * *                 If credential not exists, create credential in state Pending Didi
     * *                 If exists and is active, do nothing
     * *                 If exists and it is revoked, create a new one in Pending Didi status (is for finalize/cancelled loans)
     * *             Beneficio Familiar
     * *                 si no existe Creo credencial de beneficio en estado Pendiente de Didi
     * *                 si existe y está vigente no hago nada
     * *                 si existe y está revocada creó una nueva  la credencial como Pendiente de Didi
     *
     * @param holder
     * @param beneficiary
     * @throws CredentialException
     */
    protected void handleCredentialBenefitsForBeneficiary(Person holder, Person beneficiary, Loan loan) throws CredentialException {

        Optional<CredentialBenefits> opCredentialBenefitsBeneficiary = this.getCredentialBenefitsFamiliy(holder.getDocumentNumber(), beneficiary.getDocumentNumber());

        Optional<CredentialState> opStateRevoke = credentialStateService.getCredentialRevokeState();

        if (!opCredentialBenefitsBeneficiary.isPresent()) { //si no existe Creo credencial de beneficio en estado Pendiente de Didi

            CredentialBenefits credentialBenefits = this.buildNewFamiliyBenefitsCredential(holder, beneficiary);
            this.saveCredentialBenefit(credentialBenefits);

        } else {

            CredentialBenefits credentialBenefitsBeneficiary = opCredentialBenefitsBeneficiary.get();

            if (credentialBenefitsBeneficiary.getCredentialState().equals(opStateRevoke.get())) {
                CredentialBenefits newCredentialBenefitsBeneficiary = this.buildNewFamiliyBenefitsCredential(holder, beneficiary);
                newCredentialBenefitsBeneficiary.setIdHistorical(credentialBenefitsBeneficiary.getIdHistorical());
                this.saveCredentialBenefit(newCredentialBenefitsBeneficiary);

            } else { //credential is active or pending didi

                this.getLog().info(String.format("Credential Benefit for beneficiary %d and holder %d of loan %s is in state %s, credential not created", beneficiary.getDocumentNumber(), holder.getDocumentNumber(), loan.getIdBondareaLoan(), credentialBenefitsBeneficiary.getCredentialState().getStateName()));

            }
        }
    }

    /**
     * If holder is not on default
     *      Holder
     *          If credential exists
     *              If Credential is active and emmited, do nothing
     *              If Credential is Pending DIDI, no nothing
     *              If Credential is revoked Create new Credential
     *      Kinsman
     *      If credential exists
     *      *              If Credential is active and emmited, do nothing
     *      *              If Credential is Pending DIDI, no nothing
     *      *              If Credential is revoked Create new Credential

     * @param loan
     * @param holder
     */
    @Override
    public void updateCredentialBenefitForActiveLoan(Loan loan, Person holder) throws CredentialException {

        super.updateCredentialBenefitForActiveLoan(loan, holder);
        this.createCredentialsBenefitsFamilyForNewLoan(loan);
    }




    /**
     * *         Benefits Credential
     * *             Familiy
     * *                 If exists, is active and emmited, do revoke,
     * *                 If exists and is Pending Didi, revoke localy
     * *                 If exists and is revoked, do nothing
     * *                 If not exists, do nothing
     *
     * @throws CredentialException
     */
    public void revokeFamilyCredentialsBenefitsForLoan(Person holder) throws CredentialException {

        Optional<List<Person>> opFamily = personService.findFamilyForHolder(holder);

        if(opFamily.isPresent() && (!opFamily.get().isEmpty())) {

            List<Person> family = opFamily.get();

            log.info(String.format("Found %d people as family for holder %d", family.size(), holder.getDocumentNumber()));

            for (Person beneficiary : family) {

                Optional<CredentialBenefits> credentialBenefit = this.getFamiliyCredentialBenefit(holder, beneficiary);
                this.revokeCredentialsBenefitsForLoanInDefault(holder,beneficiary,credentialBenefit);

            }

        }else{
            log.info("Holder %d has no family, Beneficiaries Credential Benefits not created");
        }
    }

    @Override
    public Optional<CredentialBenefits> getHolderCredentialBenefit(Person holder){
        return this.getCredentialBenefits(holder.getDocumentNumber(), holder.getDocumentNumber(), PersonTypesCodes.HOLDER);
    }

    public Optional<CredentialBenefits> getFamiliyCredentialBenefit(Person holder, Person beneficiary){
        return this.getCredentialBenefits(holder.getDocumentNumber(), beneficiary.getDocumentNumber(), PersonTypesCodes.FAMILY);
    }


    @Override
    public Optional<CredentialBenefits> getCredentialBenefitsHolder(Long holderDni){
        return this.getCredentialBenefits(holderDni , holderDni, PersonTypesCodes.HOLDER);
    }

    public Optional<CredentialBenefits> getCredentialBenefitsFamiliy(Long holderDni, Long beneficiary){
        return this.getCredentialBenefits(holderDni , beneficiary, PersonTypesCodes.FAMILY);
    }


    public Optional<CredentialBenefits> getCredentialBenefits(Long holderDni, Long beneficiaryDni, PersonTypesCodes personTypesCodes) {
        Optional<CredentialBenefits> opCredentialBenefitsHolder = credentialBenefitsRepository.findTopByCreditHolderDniAndBeneficiaryDniAndBeneficiaryTypeOrderByIdDesc(holderDni, beneficiaryDni, personTypesCodes.getCode());
        return opCredentialBenefitsHolder;
    }


    @Override
    public CredentialBenefits buildNewHolderBenefitsCredential(Person holder) throws CredentialException{
         return this.buildNewBenefitsCredential(holder, holder, PersonTypesCodes.HOLDER);
    }

    public CredentialBenefits buildNewFamiliyBenefitsCredential(Person holder, Person beneficiary) throws CredentialException{
        return this.buildNewBenefitsCredential(holder, beneficiary, PersonTypesCodes.HOLDER);
    }

    /**
     * Create Benefit credentila without Id Didi and pending didi
     *
     * @param beneficiary
     * @param personType
     * @return
     */
    public CredentialBenefits buildNewBenefitsCredential(Person holder, Person beneficiary, PersonTypesCodes personType) throws CredentialException {
        CredentialBenefits credentialBenefits = new CredentialBenefits();

        Optional<ParameterConfiguration> config = parameterConfigurationRepository.findByConfigurationName(ConfigurationCodes.ID_DIDI_ISSUER.getCode());

        if (config.isPresent()) {
            credentialBenefits.setIdDidiIssuer(config.get().getValue());
        } else {
            //
            throw new CredentialException("Id Didi Issuer Not exists, cant build credential");
        }

        CredentialState statePendingDidi = credentialStateService.getCredentialPendingDidiState();
        credentialBenefits.setCredentialState(statePendingDidi);

        //Person is holder or family
        if (personType.equals(PersonTypesCodes.HOLDER)) {
            credentialBenefits.setBeneficiaryType(PersonTypesCodes.HOLDER.getCode());
            credentialBenefits.setCredentialDescription(CredentialTypesCodes.CREDENTIAL_BENEFITS.getCode());
        } else {
            credentialBenefits.setBeneficiaryType(PersonTypesCodes.FAMILY.getCode());
            credentialBenefits.setCredentialDescription(CredentialTypesCodes.CREDENTIAL_BENEFITS_FAMILY.getCode());
        }

        credentialBenefits.setDateOfIssue(DateUtil.getLocalDateTimeNow());
        credentialBenefits.setCredentialCategory(CredentialCategoriesCodes.BENEFIT.getCode());

        credentialBenefits.setCreditHolder(holder);

        credentialBenefits.setBeneficiary(beneficiary);

        return credentialBenefits;
    }


    @Override
    public CredentialBenefits saveCredentialBenefit(CredentialBenefits credentialBenefits) {
        credentialBenefits = credentialBenefitsRepository.save(credentialBenefits);
        if (credentialBenefits.getIdHistorical() == null) {
            credentialBenefits.setIdHistorical(credentialBenefits.getId());
            credentialBenefits = credentialBenefitsRepository.save(credentialBenefits);

        }
        return credentialBenefits;
    }

    public List<CredentialBenefits> getCredentialBenefitsOnPendindDidiState() throws CredentialException {
        CredentialState pendingDidiState = credentialStateService.getCredentialPendingDidiState();

        return  credentialBenefitsRepository.findByCredentialState(pendingDidiState);
    }

    public CredentialBenefits save(CredentialBenefits credentialBenefits){
        return credentialBenefitsRepository.save(credentialBenefits);
    }

    public List<CredentialBenefits> getCredentialBenefitsActiveForDni(Long dni) throws CredentialException {
        Optional<CredentialState> activeDidiState = credentialStateService.getCredentialActiveState();

        return credentialBenefitsRepository.findByCreditHolderDniAndCredentialState(dni, activeDidiState.get());
    }

    public CredentialBenefits buildNewOnPendidgDidi(CredentialBenefits credentialBenefits, DidiAppUser newDidiAppUser) throws CredentialException {
        CredentialBenefits newCredentialBenefits =  new CredentialBenefits(credentialBenefits);
        newCredentialBenefits.setIdDidiReceptor(newDidiAppUser.getDid());

        this.resetStateOnPendingDidi(newCredentialBenefits);

        return newCredentialBenefits;
    }


    @Override
    protected Logger getLog() {
        return log;
    }
}
