package com.atixlabs.semillasmiddleware.app.service;

import com.atixlabs.semillasmiddleware.app.bondarea.model.Loan;
import com.atixlabs.semillasmiddleware.app.bondarea.model.constants.LoanStatusCodes;
import com.atixlabs.semillasmiddleware.app.bondarea.repository.LoanRepository;
import com.atixlabs.semillasmiddleware.app.didi.service.DidiService;
import com.atixlabs.semillasmiddleware.app.exceptions.NoExpiredConfigurationExists;
import com.atixlabs.semillasmiddleware.app.exceptions.PersonDoesNotExists;
import com.atixlabs.semillasmiddleware.app.model.DIDHistoric.DIDHisotoric;
import com.atixlabs.semillasmiddleware.app.model.beneficiary.Person;
import com.atixlabs.semillasmiddleware.app.model.configuration.ParameterConfiguration;
import com.atixlabs.semillasmiddleware.app.model.configuration.constants.ConfigurationCodes;
import com.atixlabs.semillasmiddleware.app.model.credential.*;
import com.atixlabs.semillasmiddleware.app.model.credential.constants.CredentialCategoriesCodes;
import com.atixlabs.semillasmiddleware.app.model.credential.constants.CredentialStatesCodes;
import com.atixlabs.semillasmiddleware.app.model.credential.constants.CredentialTypesCodes;
import com.atixlabs.semillasmiddleware.app.model.credentialState.CredentialState;
import com.atixlabs.semillasmiddleware.app.repository.*;
import com.atixlabs.semillasmiddleware.excelparser.app.categories.Category;
import com.atixlabs.semillasmiddleware.excelparser.app.categories.DwellingCategory;
import com.atixlabs.semillasmiddleware.excelparser.app.categories.EntrepreneurshipCategory;
import com.atixlabs.semillasmiddleware.app.model.credential.CredentialBenefits;
import com.atixlabs.semillasmiddleware.app.model.credential.CredentialCredit;
import com.atixlabs.semillasmiddleware.app.repository.CredentialCreditRepository;
import com.atixlabs.semillasmiddleware.app.repository.PersonRepository;
import com.atixlabs.semillasmiddleware.excelparser.app.categories.AnswerCategoryFactory;
import com.atixlabs.semillasmiddleware.excelparser.app.categories.PersonCategory;
import com.atixlabs.semillasmiddleware.excelparser.app.constants.Categories;
import com.atixlabs.semillasmiddleware.excelparser.app.dto.SurveyForm;
import com.atixlabs.semillasmiddleware.app.model.credential.constants.*;
import com.atixlabs.semillasmiddleware.excelparser.dto.ProcessExcelFileResult;
import com.atixlabs.semillasmiddleware.app.model.credential.Credential;
import com.atixlabs.semillasmiddleware.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Service
public class CredentialService {

    private CredentialRepository credentialRepository;
    private CredentialCreditRepository credentialCreditRepository;
    private CredentialIdentityRepository credentialIdentityRepository;
    private CredentialEntrepreneurshipRepository credentialEntrepreneurshipRepository;
    private CredentialDwellingRepository credentialDwellingRepository;
    private PersonRepository personRepository;
    private LoanRepository loanRepository;
    private CredentialBenefitsRepository credentialBenefitsRepository;
    private DIDHistoricRepository didHistoricRepository;
    private CredentialStateRepository credentialStateRepository;
    private ParameterConfigurationRepository parameterConfigurationRepository;
    private AnswerCategoryFactory answerCategoryFactory;
    private DidiService didiService;


    @Autowired
    public CredentialService(
            CredentialCreditRepository credentialCreditRepository,
            CredentialRepository credentialRepository,
            PersonRepository personRepository,
            LoanRepository loanRepository,
            CredentialBenefitsRepository credentialBenefitsRepository,
            DIDHistoricRepository didHistoricRepository,
            CredentialStateRepository credentialStateRepository,
            AnswerCategoryFactory answerCategoryFactory,
            CredentialIdentityRepository credentialIdentityRepository,
            CredentialEntrepreneurshipRepository credentialEntrepreneurshipRepository,
            CredentialDwellingRepository credentialDwellingRepository,
            ParameterConfigurationRepository parameterConfigurationRepository,
            DidiService didiService) {
            this.credentialCreditRepository = credentialCreditRepository;
            this.credentialRepository = credentialRepository;
            this.personRepository = personRepository;
            this.loanRepository = loanRepository;
            this.credentialBenefitsRepository = credentialBenefitsRepository;
            this.didHistoricRepository = didHistoricRepository;
            this.credentialStateRepository = credentialStateRepository;
            this.parameterConfigurationRepository = parameterConfigurationRepository;
            this.answerCategoryFactory = answerCategoryFactory;
            this.credentialIdentityRepository = credentialIdentityRepository;
            this.credentialEntrepreneurshipRepository = credentialEntrepreneurshipRepository;
            this.credentialDwellingRepository = credentialDwellingRepository;
            this.didiService = didiService;
    }


    public void buildAllCredentialsFromForm(SurveyForm surveyForm, ProcessExcelFileResult processExcelFileResult) {
        log.info("buildAllCredentialsFromForm: " + this.toString());
        if (validateAllCredentialsFromForm(surveyForm, processExcelFileResult))
            saveAllCredentialsFromForm(surveyForm);
    }


    /**
     * The following are non-public methods, isolating functionality.
     * to make public methods easier to read.
     *
     * @param surveyForm
     */
    private boolean validateAllCredentialsFromForm(SurveyForm surveyForm, ProcessExcelFileResult processExcelFileResult) {
        log.info("  validateIdentityCredentialFromForm");

        //1-get all people data from form, creditHolder will be a beneficiary as well.
        ArrayList<Category> categoryArrayList = surveyForm.getAllCompletedCategories();

        //2-get creditHolder Data
        PersonCategory creditHolderPersonCategory = (PersonCategory) surveyForm.getCategoryByUniqueName(Categories.BENEFICIARY_CATEGORY_NAME.getCode(), null);
        Person creditHolder = Person.getPersonFromPersonCategory(creditHolderPersonCategory);

        //2-verify each person is new, or his data has not changed.
        boolean allCredentialsNewOrInactive = true;
        for (Category category : categoryArrayList) {
            switch (category.getCategoryName()) {
                case BENEFICIARY_CATEGORY_NAME:
                case SPOUSE_CATEGORY_NAME:
                case CHILD_CATEGORY_NAME:
                case KINSMAN_CATEGORY_NAME:
                    PersonCategory beneficiaryPersonCategory = (PersonCategory) category;
                    Person beneficiary = Person.getPersonFromPersonCategory(beneficiaryPersonCategory);
                    if (isCredentialAlreadyExistent(beneficiary.getDocumentNumber(), CredentialCategoriesCodes.IDENTITY.getCode(), processExcelFileResult))
                        allCredentialsNewOrInactive = false;
                    break;
                case ENTREPRENEURSHIP_CATEGORY_NAME:
                    if (isCredentialAlreadyExistent(creditHolder.getDocumentNumber(), CredentialCategoriesCodes.ENTREPRENEURSHIP.getCode(), processExcelFileResult))
                        allCredentialsNewOrInactive = false;
                    break;
                case DWELLING_CATEGORY_NAME:
                    if (isCredentialAlreadyExistent(creditHolder.getDocumentNumber(), CredentialCategoriesCodes.DWELLING.getCode(), processExcelFileResult))
                        allCredentialsNewOrInactive = false;
                    break;
            }
        }
        return allCredentialsNewOrInactive;
    }

    private boolean isCredentialAlreadyExistent(Long beneficiaryDni, String credentialCategoryCode, ProcessExcelFileResult processExcelFileResult) {

        List<String> statesCodesToFind = new ArrayList<>();
        statesCodesToFind.add(CredentialStatesCodes.PENDING_DIDI.getCode());
        statesCodesToFind.add(CredentialStatesCodes.CREDENTIAL_ACTIVE.getCode());

        List<CredentialState> credentialStateActivePending = credentialStateRepository.findByStateNameIn(statesCodesToFind);

        Optional<Credential> credentialOptional = credentialRepository.findByBeneficiaryDniAndCredentialCategoryAndCredentialStateIn(
                beneficiaryDni,
                credentialCategoryCode,
                credentialStateActivePending
        );
        if (credentialOptional.isEmpty())
            return false;
        else
            processExcelFileResult.addRowError(
                    "Warning CREDENCIAL DUPLICADA",
                    "Ya existe una credencial de tipo " + credentialCategoryCode +
                            " en estado " + credentialOptional.get().getCredentialState().getStateName()+
                            " para el DNI " + beneficiaryDni + " si desea continuar debe revocarlas manualmente"
            );
        return true;
    }

    private void saveAllCredentialsFromForm(SurveyForm surveyForm) {
        //1-get creditHolder Data
        PersonCategory creditHolderPersonCategory = (PersonCategory) surveyForm.getCategoryByUniqueName(Categories.BENEFICIARY_CATEGORY_NAME.getCode(), null);
        Person creditHolder = Person.getPersonFromPersonCategory(creditHolderPersonCategory);

        //1-get all data from form
        ArrayList<Category> categoryArrayList = surveyForm.getAllCompletedCategories();

        //4-Now working with each beneficiary
        for (Category category : categoryArrayList) {
            saveCredential(category, creditHolder);
        }
    }


    private void saveCredential(Category category, Person creditHolder) {
        log.info("  saveCredential: " + category.getCategoryName());
        switch (category.getCategoryName()) {
            case BENEFICIARY_CATEGORY_NAME:
            case CHILD_CATEGORY_NAME:
            case SPOUSE_CATEGORY_NAME:
            case KINSMAN_CATEGORY_NAME:
                credentialIdentityRepository.save(buildIdentityCredential(category, creditHolder));
                break;
            case ENTREPRENEURSHIP_CATEGORY_NAME:
                credentialEntrepreneurshipRepository.save(buildEntrepreneurshipCredential(category, creditHolder));
                break;
            case DWELLING_CATEGORY_NAME:
                credentialDwellingRepository.save(buildDwellingCredential(category, creditHolder));
                break;
        }
    }

    public List<Credential> findCredentials(String credentialType, String name, String dniBeneficiary, String
            idDidiCredential, String dateOfExpiry, String dateOfIssue, List<String> credentialState) {
        List<Credential> credentials;
        credentials = credentialRepository.findCredentialsWithFilter(credentialType, name, dniBeneficiary, idDidiCredential, dateOfExpiry, dateOfIssue, credentialState);

        return credentials;
    }

    private Person savePersonIfNew(Person person) {
        Optional<Person> personOptional = personRepository.findByDocumentNumber(person.getDocumentNumber());
        if (personOptional.isEmpty())
            return personRepository.save(person);
        if (!(person.equalsIgnoreId(person, personOptional.get()))) {
            person.setId(personOptional.get().getId());
            return personRepository.save(person);
        }
        return personOptional.get();
    }

    private void buildCredential(Person creditHolder, Credential credential) {
        creditHolder = savePersonIfNew(creditHolder);

        credential.setDateOfIssue(DateUtil.getLocalDateTimeNow());
        credential.setCreditHolder(creditHolder);
        credential.setCreditHolderDni(creditHolder.getDocumentNumber());
        credential.setCreditHolderFirstName(creditHolder.getFirstName());
        credential.setCreditHolderLastName(creditHolder.getLastName());

        //the beneficiary is the same as the credit holder for all credentials but identity
        //buildIdentityCredential overwrites this value with the different members.
        credential.setBeneficiary(creditHolder);
        credential.setBeneficiaryDni(creditHolder.getDocumentNumber());
        credential.setBeneficiaryFirstName(creditHolder.getFirstName());
        credential.setBeneficiaryLastName(creditHolder.getLastName());

        //credential.setCredentialStatus(CredentialStatusCodes.CREDENTIAL_PENDING_BONDAREA.getCode());
        Optional<CredentialState> credentialStateOptional = credentialStateRepository.findByStateName(CredentialStatesCodes.PENDING_DIDI.getCode());
        credentialStateOptional.ifPresent(credential::setCredentialState);
    }

    private CredentialIdentity buildIdentityCredential(Category category, Person creditHolder) {
        PersonCategory beneficiaryPersonCategory = (PersonCategory) category;
        Person beneficiary = Person.getPersonFromPersonCategory(beneficiaryPersonCategory);
        beneficiary = savePersonIfNew(beneficiary);

        CredentialIdentity credentialIdentity = new CredentialIdentity();
        buildCredential(creditHolder, credentialIdentity);

        credentialIdentity.setBeneficiary(beneficiary);
        credentialIdentity.setBeneficiaryDni(beneficiary.getDocumentNumber());
        credentialIdentity.setBeneficiaryFirstName(beneficiary.getFirstName());
        credentialIdentity.setBeneficiaryLastName(beneficiary.getLastName());

        credentialIdentity.setCredentialCategory(CredentialCategoriesCodes.IDENTITY.getCode());


        credentialIdentity.setBeneficiaryGender(beneficiary.getGender());
        credentialIdentity.setBeneficiaryBirthDate(beneficiary.getBirthDate());

        switch (beneficiaryPersonCategory.getPersonType()) {
            case BENEFICIARY:
                credentialIdentity.setCredentialDescription(CredentialTypesCodes.CREDENTIAL_IDENTITY.getCode());
                credentialIdentity.setRelationWithCreditHolder(PersonTypesCodes.HOLDER.getCode());
                break;
            case SPOUSE:
            case CHILD:
            case OTHER_KINSMAN:
                credentialIdentity.setCredentialDescription(CredentialTypesCodes.CREDENTIAL_IDENTITY_FAMILY.getCode());
                credentialIdentity.setRelationWithCreditHolder(PersonTypesCodes.FAMILY.getCode());
                break;
        }

        return credentialIdentity;
    }

    private CredentialEntrepreneurship buildEntrepreneurshipCredential(Category category, Person creditHolder) {
        EntrepreneurshipCategory entrepreneurshipCategory = (EntrepreneurshipCategory) category;

        CredentialEntrepreneurship credentialEntrepreneurship = new CredentialEntrepreneurship();
        buildCredential(creditHolder, credentialEntrepreneurship);
        credentialEntrepreneurship.setEntrepreneurshipType(entrepreneurshipCategory.getType());
        credentialEntrepreneurship.setStartActivity(entrepreneurshipCategory.getActivityStartDate());
        credentialEntrepreneurship.setMainActivity(entrepreneurshipCategory.getMainActivity());
        credentialEntrepreneurship.setEntrepreneurshipName(entrepreneurshipCategory.getName());
        credentialEntrepreneurship.setEntrepreneurshipAddress(entrepreneurshipCategory.getAddress());
        credentialEntrepreneurship.setEndActivity(entrepreneurshipCategory.getActivityEndingDate());

        credentialEntrepreneurship.setCredentialCategory(CredentialCategoriesCodes.ENTREPRENEURSHIP.getCode());
        credentialEntrepreneurship.setCredentialDescription(CredentialCategoriesCodes.ENTREPRENEURSHIP.getCode());

        return credentialEntrepreneurship;
    }

    private CredentialDwelling buildDwellingCredential(Category category, Person creditHolder) {
        DwellingCategory entrepreneurshipCategory = (DwellingCategory) category;

        CredentialDwelling credentialDwelling = new CredentialDwelling();
        buildCredential(creditHolder, credentialDwelling);

        credentialDwelling.setDwellingType(entrepreneurshipCategory.getDwellingType());
        credentialDwelling.setDwellingAddress(entrepreneurshipCategory.getDistrict());
        credentialDwelling.setPossessionType(entrepreneurshipCategory.getHoldingType());

        credentialDwelling.setCredentialCategory(CredentialCategoriesCodes.DWELLING.getCode());
        credentialDwelling.setCredentialDescription(CredentialCategoriesCodes.DWELLING.getCode());

        return credentialDwelling;
    }

    /**
     * Create a new credential credit if the id bondarea of the credit does not exist.
     * Then it creates the benefits credential to the holder
     * @param loan
     * @throws PersonDoesNotExists
     */
    public void createNewCreditCredentials(Loan loan) throws PersonDoesNotExists {
        //beneficiarieSSSS -> the credit group will be created by separate (not together)
        log.info("Creating Credential Credit ");
        Optional<CredentialCredit> opCreditExistence = credentialCreditRepository.findByIdBondareaCredit(loan.getIdBondareaLoan());
        if (opCreditExistence.isEmpty()) {
            Optional<Person> opBeneficiary = personRepository.findByDocumentNumber(loan.getDniPerson());
            if (opBeneficiary.isPresent()) {
                CredentialCredit credit = this.buildCreditCredential(loan, opBeneficiary.get());
                loan.setHasCredential(true);

                credit = credentialCreditRepository.save(credit);
                //get the new id and save it on id historic
                credit.setIdHistorical(credit.getId());
                credentialCreditRepository.save(credit);
                log.info("Credential Credit created for dni: " + opBeneficiary.get().getDocumentNumber());

                loanRepository.save(loan);

                //after create credit, will create benefit holder credential
                this.createNewBenefitsCredential(opBeneficiary.get(), PersonTypesCodes.HOLDER);
            } else {
                log.error("Person with dni "+ loan.getDniPerson() + " has not been created. The loan exists but the survey with this person has not been loaded");
                throw new PersonDoesNotExists("Person with dni " + loan.getDniPerson() + " has not been created. The loan exists but the survey with this person has not been loaded");
                //this error is important, have to be shown in front
            }
        } else {
            loan.setHasCredential(true);
            loanRepository.save(loan);
            log.error("The credit with idBondarea " + loan.getIdBondareaLoan() + " has an existent credential");
        }
    }


    public CredentialCredit buildCreditCredential(Loan loan, Person beneficiary){
        CredentialCredit credentialCredit = new CredentialCredit();
        credentialCredit.setIdBondareaCredit(loan.getIdBondareaLoan());
        // TODO we need the type from bondarea - credentialCredit.setCreditType();
        credentialCredit.setIdGroup(loan.getIdGroup());
        credentialCredit.setCurrentCycle(loan.getCycleDescription()); // si cambia, se tomara como cambio de ciclo
        //TODO data for checking - credentialCredit.totalCycles;

        credentialCredit.setAmountExpiredCycles(0);
        credentialCredit.setCreditState(loan.getStatus());
        credentialCredit.setExpiredAmount(loan.getExpiredAmount());
        credentialCredit.setCreationDate(loan.getCreationDate());

        //Added Modification CreditHolderDni and CreditHolderId
        credentialCredit.setBeneficiary(beneficiary);
        credentialCredit.setBeneficiaryDni(beneficiary.getDocumentNumber());
        credentialCredit.setBeneficiaryFirstName(beneficiary.getFirstName());
        credentialCredit.setBeneficiaryLastName(beneficiary.getLastName());

        credentialCredit.setCreditHolderDni(beneficiary.getDocumentNumber());
        credentialCredit.setCreditHolder(beneficiary);
        credentialCredit.setCreditHolderFirstName(beneficiary.getFirstName());
        credentialCredit.setCreditHolderLastName(beneficiary.getLastName());
        //End creditHolder changes

        //Credential Parent fields
        credentialCredit.setDateOfIssue(DateUtil.getLocalDateTimeNow());
        credentialCredit.setBeneficiary(beneficiary);


        //TODO this should be took from DB - credentialCredit.setIdDidiIssuer();
        Optional<DIDHisotoric> opActiveDid = didHistoricRepository.findByIdPersonAndIsActive(beneficiary.getId(), true);
        if (opActiveDid.isPresent()) {
            credentialCredit.setIdDidiReceptor(opActiveDid.get().getIdDidiReceptor());
            credentialCredit.setIdDidiCredential(opActiveDid.get().getIdDidiReceptor());
            Optional<CredentialState> opStateActive = credentialStateRepository.findByStateName(CredentialStatesCodes.CREDENTIAL_ACTIVE.getCode());
            if (opStateActive.isPresent()) {
                credentialCredit.setCredentialState(opStateActive.get());
            }

        } else {
            //Person do not have a DID yet -> set as pending didi
            Optional<CredentialState> opStateActive = credentialStateRepository.findByStateName(CredentialStatesCodes.PENDING_DIDI.getCode());
            if (opStateActive.isPresent()) {
                credentialCredit.setCredentialState(opStateActive.get());
            }
        }

        //This depends of the type of loan from bondarea
        credentialCredit.setCredentialDescription(CredentialTypesCodes.CREDENTIAL_CREDIT.getCode());
        credentialCredit.setCredentialCategory(CredentialCategoriesCodes.CREDIT.getCode());// TODO this column will be no longer useful

        return credentialCredit;
    }


    public void createNewBenefitsCredential(Person beneficiary, PersonTypesCodes personType) {
        log.info("Creating Credential Benefits");
        List<CredentialState> pendingAndActiveState = credentialStateRepository.findByStateNameIn(List.of(CredentialStatesCodes.CREDENTIAL_ACTIVE.getCode(), CredentialStatesCodes.PENDING_DIDI.getCode()));
        if (pendingAndActiveState.size() == 2) {
            Optional<CredentialBenefits> opBenefits = credentialBenefitsRepository.findByBeneficiaryDniAndCredentialStateInAndBeneficiaryType(beneficiary.getDocumentNumber(), pendingAndActiveState, personType.getCode());
            //create benefit if person does not have one or | do not have the type wanted to create. Or is not Active nor pending
            if (opBenefits.isEmpty()) {
                CredentialBenefits benefits = this.buildBenefitsCredential(beneficiary, personType);

                //get the new id and save it on id historic
                benefits = credentialBenefitsRepository.save(benefits);
                benefits.setIdHistorical(benefits.getId());
                credentialBenefitsRepository.save(benefits);
                log.info("Credential Credit created for dni: " + beneficiary.getDocumentNumber());
            } else {
                log.info("Person with dni " + beneficiary.getDocumentNumber() + " had already a credential benefits");
            }
        }
    }


    /**
     *
     * @param beneficiary
     * @param personType
     * @return
     */
    public CredentialBenefits buildBenefitsCredential(Person beneficiary, PersonTypesCodes personType){
            CredentialBenefits benefits = new CredentialBenefits();

            //Person is holder or family
            if (personType.equals(PersonTypesCodes.HOLDER)) {
                benefits.setBeneficiaryType(PersonTypesCodes.HOLDER.getCode());
                benefits.setCredentialCategory(CredentialCategoriesCodes.BENEFIT.getCode());
                benefits.setCredentialDescription(CredentialTypesCodes.CREDENTIAL_BENEFITS.getCode());
            } else {
                benefits.setBeneficiaryType(PersonTypesCodes.FAMILY.getCode());
                benefits.setCredentialCategory(CredentialCategoriesCodes.BENEFIT.getCode());
                benefits.setCredentialDescription(CredentialTypesCodes.CREDENTIAL_BENEFITS_FAMILY.getCode());
            }

            benefits.setDateOfIssue(DateUtil.getLocalDateTimeNow());


            //Added Modification CreditHolderDni and CreditHolderId
            benefits.setBeneficiary(beneficiary);
            benefits.setBeneficiaryDni(beneficiary.getDocumentNumber());
            benefits.setBeneficiaryFirstName(beneficiary.getFirstName());
            benefits.setBeneficiaryLastName(beneficiary.getLastName());


            benefits.setCreditHolderDni(beneficiary.getDocumentNumber());
            benefits.setCreditHolder(beneficiary);
            benefits.setCreditHolderFirstName(beneficiary.getFirstName());
            benefits.setCreditHolderLastName(beneficiary.getLastName());
            //End creditHolder changes

            //TODO this should be took from DB - credentialCredit.setIdDidiIssuer();

            Optional<DIDHisotoric> opActiveDid = didHistoricRepository.findByIdPersonAndIsActive(beneficiary.getId(), true);
            if (opActiveDid.isPresent()) {
                //set did and credential to active
                benefits.setIdDidiReceptor(opActiveDid.get().getIdDidiReceptor());
                benefits.setIdDidiCredential(opActiveDid.get().getIdDidiReceptor());
                Optional<CredentialState> opStateActive = credentialStateRepository.findByStateName(CredentialStatesCodes.CREDENTIAL_ACTIVE.getCode());
                if (opStateActive.isPresent()) {
                    benefits.setCredentialState(opStateActive.get());
                }
            } else {
                //Person do not have a DID yet -> set as pending didi
                Optional<CredentialState> opStateActive = credentialStateRepository.findByStateName(CredentialStatesCodes.PENDING_DIDI.getCode());
                if (opStateActive.isPresent())
                    benefits.setCredentialState(opStateActive.get());
            }

            return benefits;
        }

    /**
     * Validate if the credential needs to be updated.
     * The credential could be in state active | pending | revoke. In each case the credential can be update, as long as the loan has the same id.
     *
     * @param loan
     * @return Credential Credit or null
     */
    public CredentialCredit validateCredentialCreditToUpdate(Loan loan) {
        //get the credit credential by idBondarea and get the last created for this credential credit
        Optional<CredentialCredit> opCredit = credentialCreditRepository.findFirstByIdBondareaCreditOrderByDateOfIssueDesc(loan.getIdBondareaLoan());
        if (opCredit.isPresent()) {
            // if it does not have finish date (finishDate indicate that the credit has finished or has been canceled)
            if (opCredit.get().getFinishDate() == null) {
                CredentialCredit credit = opCredit.get();
                //todo this validation need to consider the case when the credit group has been revoked
                if (!(Float.compare(loan.getExpiredAmount(), credit.getExpiredAmount()) == 0) || !loan.getCycleDescription().equals(credit.getCurrentCycle()) || !(loan.getStatus().equals(credit.getCreditState())))/*||  loan.getTotalCuotas...*/ {
                    // the loan has changed, return credit to be update
                    return credit;
                } else {
                    return null;
                }
            }
        } else {
            // the credit had been set that has a credential credit, but no credential credit exist with the bondarea id
            // the next time loans are going to be check, a new credential credit would be create
            loan.setHasCredential(false);
            loanRepository.save(loan);
            return null;
        }
        return null;
    }


    /**
     * 2nd Step in the process "Generate", after create the new credits.
     * This process will check the previous credential credit and his loan, to update and | or revoke.
     * If there has been a change, credential will be revoke, then generate a new one.
     *
     * @param loan
     * @param credit
     * @throws NoExpiredConfigurationExists
     * @throws PersonDoesNotExists
     */
    public void updateCredentialCredit(Loan loan, CredentialCredit credit) throws NoExpiredConfigurationExists, PersonDoesNotExists{
        log.info("Updating credential credit " + credit.getId());
        // revoke credit -> save id historic
        Long idHistoricCredit = credit.getIdHistorical();
        revokeCredentialOnlyOnSemillas(credit);

        Optional<Person> opBeneficiary = personRepository.findByDocumentNumber(loan.getDniPerson());
        if (opBeneficiary.isPresent()) {
            CredentialCredit updateCredit = this.buildCreditCredential(loan, opBeneficiary.get());
            updateCredit.setIdHistorical(idHistoricCredit); //assign the old historic.
            //set the amount expired cycles of thque  previous credential to accumulate the expired cycles
            updateCredit.setAmountExpiredCycles(credit.getAmountExpiredCycles());
            updateCredit = credentialCreditRepository.save(updateCredit);


            // if credit is finalized credential will be revoke
            if (loan.getStatus().equals(LoanStatusCodes.FINALIZED.getCode())){
                updateCredit.setFinishDate(DateUtil.getLocalDateTimeNow().toLocalDate());
                credentialCreditRepository.save(updateCredit);
                log.info("Credential Credit is set to finalize, for credential id " + updateCredit.getId());

                //revoke only the benefits if the holder does not have another credit. And revoke the familiar benefits given by this credit.
                List<CredentialState> pendingAndActiveState = credentialStateRepository.findByStateNameIn(List.of(CredentialStatesCodes.CREDENTIAL_ACTIVE.getCode(), CredentialStatesCodes.PENDING_DIDI.getCode()));
                List<CredentialBenefits> benefitsHolder = credentialBenefitsRepository.findByCreditHolderDniAndCredentialStateInAndBeneficiaryType(updateCredit.getBeneficiaryDni(), pendingAndActiveState, PersonTypesCodes.HOLDER.getCode());
                //there have to be only 1. The holder only have at max 1 holder benefits.
                if(benefitsHolder.size() > 0)
                    this.revokeCredential(benefitsHolder.get(0).getId());
                else {
                    log.info("There is no active or pending benefits of the holder to be revoked");
                }
            }
            else{
                if(loan.getStatus().equals(LoanStatusCodes.CANCELLED.getCode())){
                    updateCredit.setFinishDate(DateUtil.getLocalDateTimeNow().toLocalDate());
                    credentialCreditRepository.save(updateCredit);
                    log.info("Credential Credit is set to cancelled, for credential id " + updateCredit.getId());

                    //todo la revocacion no es como la mora. Ver especificaciones
                    //Revoke each credential credit of the credit group
                    List<CredentialCredit> creditGroup =  this.getCreditGroup(loan.getIdGroup());
                    for (CredentialCredit creditOfGroup: creditGroup) {
                        this.revokeComplete(creditOfGroup);
                    }
                    //revoke the whole group including the benefits of them and his familiars
                    this.revokeCredential(updateCredit.getId());
                }
                else {
                        // validate the expired amount (need to be lower than the sum expiredAmount of all the credit group)
                        List<CredentialCredit> creditGroup = this.getCreditGroup(loan.getIdGroup());
                        BigDecimal amountExpired = sumExpiredAmount(creditGroup);

                        Optional<ParameterConfiguration> config = parameterConfigurationRepository.findByConfigurationName(ConfigurationCodes.MAX_EXPIRED_AMOUNT.getCode());
                        if (config.isPresent()) {
                            BigDecimal maxAmount = new BigDecimal(Float.toString(config.get().getExpiredAmountMax()));
                            if (amountExpired.compareTo(maxAmount) > 0) {
                                int cyclesExpired = updateCredit.getAmountExpiredCycles();
                                cyclesExpired++;
                                updateCredit.setAmountExpiredCycles(cyclesExpired);
                                credentialCreditRepository.save(updateCredit);
                                log.info("Credit is default. Count +1 cycles expired for credential credit id: " + updateCredit.getId());

                                //revoke the whole group including the benefits of them and his familiars
                                this.revokeCredential(updateCredit.getId());

                            } else {
                                //if credit has no expired amount
                                // try to create credential benefits in case holder does not have
                                //TODO here need to validate if its ok to generate again the benefits
                                this.createNewBenefitsCredential(opBeneficiary.get(), PersonTypesCodes.HOLDER);
                            }


                        } else {
                            log.error("There is no configuration for getting the maximum expired amount.");
                            throw new NoExpiredConfigurationExists("There is no configuration for getting the maximum expired amount. Impossible to check the credential credit");
                        }
                    }
                }

            log.info("Update process finished for credential credit id: " + updateCredit.getId());
        }
        else {
            log.error("Person had been created and credential credit too, but person has been deleted eventually");
            throw new PersonDoesNotExists("Person had been created and credential credit too, but person has been deleted eventually");
        }
    }



    /**
     * Accumulate the expired amount of the credit group.
     * This able to check if the group is default.
     *
     * @param group
     * @return BigDecimal (sum)
     */
    private BigDecimal sumExpiredAmount(List<CredentialCredit> group){
        BigDecimal amountExpired = BigDecimal.ZERO;

        for (CredentialCredit credit: group) {
            amountExpired = amountExpired.add(new BigDecimal(Float.toString(credit.getExpiredAmount())));
        }

        return amountExpired;
    }

    /**
     * Revocation with the business logic.
     * For particular revocations use, this.revokeComplete()
     * @param id
     * @return
     */
    public boolean revokeCredential(Long id) {
        boolean haveRevokeOk = true;

        log.info("Filtering credential with id: "+ id);
        //validate credential is in bd
        Optional<Credential> opCredential = credentialRepository.findById(id);
        if (opCredential.isPresent()) {
            //get the credential
            Credential credentialToRevoke = opCredential.get();
            CredentialTypesCodes credentialType;
            try {
                credentialType = CredentialTypesCodes.getEnumByStringValue(opCredential.get().getCredentialDescription());
            }
            catch (IllegalArgumentException ex){
                log.error("Impossible to revoke credential. There is no credential with type " + credentialToRevoke.getCredentialDescription());
                return false;
            }

            log.info("credential type of : "+ credentialType.getCode());
            switch (credentialType) {
                case CREDENTIAL_DWELLING:
                case CREDENTIAL_ENTREPRENEURSHIP:
                case CREDENTIAL_BENEFITS_FAMILY:
                    this.revokeComplete(credentialToRevoke);
                    break;

                case CREDENTIAL_IDENTITY:
                    //find all the identities that the dni of the holder is into. (with state active or pending)
                    List<CredentialState> activePendingStates = credentialStateRepository.findByStateNameIn(List.of(CredentialStatesCodes.CREDENTIAL_ACTIVE.getCode(), CredentialStatesCodes.PENDING_DIDI.getCode()));
                    List<CredentialIdentity> holderIdentities = credentialIdentityRepository.findByCreditHolderDniAndCredentialStateIn(credentialToRevoke.getCreditHolderDni(), activePendingStates);

                    if (holderIdentities.size() == 0) {
                        log.info("There is no credential type " + credentialType.getCode() + " to revoke! The credentials are not in state pending or active");
                        haveRevokeOk = false;
                    }
                    for (Credential credential : holderIdentities) {
                        this.revokeComplete(credential);
                    }

                    break;

                case CREDENTIAL_IDENTITY_FAMILY:
                    //revoke the identities of the familiar: the one created by the survey and if it exists, the one created because the person download the app. (with state active or pending)
                    activePendingStates = credentialStateRepository.findByStateNameIn(List.of(CredentialStatesCodes.CREDENTIAL_ACTIVE.getCode(), CredentialStatesCodes.PENDING_DIDI.getCode()));
                    List<CredentialIdentity> familiarIdentities = credentialIdentityRepository.findByCreditHolderDniAndBeneficiaryDniAndCredentialStateIn(credentialToRevoke.getCreditHolderDni(),
                                                                    credentialToRevoke.getBeneficiaryDni(), activePendingStates);
                    if (familiarIdentities.size() == 0) {
                        log.info("There is no credential type " + credentialType.getCode() + " to revoke! The credentials are not in state pending or active");
                        haveRevokeOk = false;
                    }
                    for (Credential credential : familiarIdentities) {
                        this.revokeComplete(credential);
                    }
                    break;

                case CREDENTIAL_BENEFITS:
                    //revoke benefit if the holder does not have another credit(active or pending, and did not finish) and also revoke benefits family .
                    activePendingStates = credentialStateRepository.findByStateNameIn(List.of(CredentialStatesCodes.CREDENTIAL_ACTIVE.getCode(), CredentialStatesCodes.PENDING_DIDI.getCode()));
                    List<CredentialCredit> creditsActivePending = credentialCreditRepository.findByCreditHolderDniAndCredentialStateInAndFinishDateIsNull(credentialToRevoke.getCreditHolderDni(), activePendingStates);
                    if (creditsActivePending.size() == 0) {
                        this.revokeComplete(credentialToRevoke);
                        //if holder benefits is revoke, the familiar benefits too
                        //get the familiar benefits in which the holder is within
                        List<CredentialBenefits> familiarBenefits = credentialBenefitsRepository.findByCreditHolderDniAndCredentialStateInAndBeneficiaryType(credentialToRevoke.getCreditHolderDni(), activePendingStates, PersonTypesCodes.FAMILY.getCode());
                        for (CredentialBenefits familiarBenefit : familiarBenefits) {
                            this.revokeComplete(familiarBenefit);
                        }
                    }
                    else {
                         log.info("Impossible to revoke credential benefit. There is/are not credential/s credit in state active or pending.");
                         haveRevokeOk = false;
                    }

                    break;

                case CREDENTIAL_CREDIT:
                    //get the credit credential to get the group
                    Optional<CredentialCredit> credentialCredit = credentialCreditRepository.findById(credentialToRevoke.getId());
                    if(credentialCredit.isPresent()) {
                        //get the group that is not revoked
                        List<CredentialCredit> creditsGroup = this.getCreditGroup(credentialCredit.get().getIdGroup());
                        //for each holder credit -> revoke credit -> revoke benefits -> revoke familiar benefits
                        for (CredentialCredit credit: creditsGroup) {
                             haveRevokeOk = this.revokeComplete(credit); //todo validate succesfull revocation to continue
                             //get benefits with holder dni (holder benefits and familiar benefits)
                             activePendingStates = credentialStateRepository.findByStateNameIn(List.of(CredentialStatesCodes.CREDENTIAL_ACTIVE.getCode(), CredentialStatesCodes.PENDING_DIDI.getCode()));
                            List<CredentialBenefits> benefits = credentialBenefitsRepository.findByCreditHolderDniAndCredentialStateIn(credit.getCreditHolderDni(), activePendingStates);

                            if(benefits.size() == 0){
                                log.info("For the dni holder: " + credit.getCreditHolderDni() + " there is no benefits in active nor pending");
                            }

                            for (CredentialBenefits benefit : benefits) {
                                this.revokeComplete(benefit);
                            }
                        }

                    }
                    else{
                         log.error("Error you are trying to revoke ");
                         haveRevokeOk = false;
                    }

                    break;
            }

        } else {
            //todo throw non-existent credential ?
            log.error("Error you are trying to revoke ");
            haveRevokeOk = false;
        }

        return  haveRevokeOk;
    }

    /**
     * Get the credit group with the idGroup as long as the credits are not revoked.
     * @param idGroup
     * @return List<CredentialCredit>
     */
    private List<CredentialCredit> getCreditGroup(String idGroup){
        List<CredentialState> activePendingStates = credentialStateRepository.findByStateNameIn(List.of(CredentialStatesCodes.CREDENTIAL_ACTIVE.getCode(), CredentialStatesCodes.PENDING_DIDI.getCode()));
        //get the group that is not revoked
        List<CredentialCredit> creditsGroup = credentialCreditRepository.findByIdGroupAndCredentialStateIn(idGroup, activePendingStates );

        return creditsGroup;
    }

    /**
     * Revoke on DB and revoke on didi
     *
     * @param credentialToRevoke
     * @return boolean
     */
    public boolean revokeComplete(Credential credentialToRevoke) {
        //here is important to manage the different actions, and need to be synchronize at the end.
        boolean revokedOk;  //todo call revoke on didi
        log.info("Starting revoking process for credential id: " + credentialToRevoke.getId() + " | credential type: " + credentialToRevoke.getCredentialDescription());
        log.info("Revoking on didi");
        if (didiService.didiDeleteCertificate(credentialToRevoke.getIdDidiCredential())) {
            // if didi fail the credential need to know that is needed to be revoked (here think in the best resolution).
            // if this revoke came from the revocation business we will need to throw an error to rollback any change done before.
            revokedOk = this.revokeCredentialOnlyOnSemillas(credentialToRevoke);
        }else{
            log.info("There was an error deleting credential id: "+ credentialToRevoke.getId() + " on didi");
             revokedOk = false;
        }

        return revokedOk;
    }

    /**
     * Revoke only for internal usage. Only revokes the credential on the DB.
     *
     * @param credentialToRevoke
     * @return boolean
     */
    public boolean revokeCredentialOnlyOnSemillas(Credential credentialToRevoke) {
        log.info("Revoking the credential on semillas " + credentialToRevoke.getId());
        boolean haveRevoke = true;

        //validate if the credential is in db
        Optional<Credential> credential = credentialRepository.findById(credentialToRevoke.getId());
        if (credential.isEmpty()) {
            haveRevoke = false;
            log.error("The credential with id: " + credentialToRevoke.getId() + " is not in the database");
        } else {

            //get revoke state
            Optional<CredentialState> opStateRevoke = credentialStateRepository.findByStateName(CredentialStatesCodes.CREDENTIAL_REVOKE.getCode());
            if (opStateRevoke.isPresent()) {
                //revoke if the credential is not revoked yet
                if (credential.get().getCredentialState().equals(opStateRevoke.get())) {
                    log.info("The credential " + credential.get().getId() + " has already been revoked");
                    haveRevoke = false;
                } else {

                    credentialToRevoke.setCredentialState(opStateRevoke.get());
                    //todo: set the reason of revocation ?
                    credentialToRevoke.setDateOfRevocation(DateUtil.getLocalDateTimeNow());
                    credentialRepository.save(credentialToRevoke);
                    log.info("Credential with id " + credentialToRevoke.getId() + " has been revoked!"); //then append also the reason
                }
            } else {
                haveRevoke = false;
                log.error("The state revoke could not be found");
            }
        }



        return haveRevoke;
    }


}

