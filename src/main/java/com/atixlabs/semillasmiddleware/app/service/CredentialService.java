package com.atixlabs.semillasmiddleware.app.service;

import com.atixlabs.semillasmiddleware.app.bondarea.model.Loan;
import com.atixlabs.semillasmiddleware.app.bondarea.model.constants.LoanStateCodes;
import com.atixlabs.semillasmiddleware.app.bondarea.model.constants.LoanStatusCodes;
import com.atixlabs.semillasmiddleware.app.bondarea.repository.LoanRepository;
import com.atixlabs.semillasmiddleware.app.bondarea.service.LoanService;
import com.atixlabs.semillasmiddleware.app.didi.service.DidiService;
import com.atixlabs.semillasmiddleware.app.exceptions.PersonDoesNotExistsException;
import com.atixlabs.semillasmiddleware.app.model.DIDHistoric.DIDHisotoric;
import com.atixlabs.semillasmiddleware.app.model.beneficiary.Person;
import com.atixlabs.semillasmiddleware.app.model.credential.*;
import com.atixlabs.semillasmiddleware.app.model.credential.constants.CredentialCategoriesCodes;
import com.atixlabs.semillasmiddleware.app.model.credential.constants.CredentialStatesCodes;
import com.atixlabs.semillasmiddleware.app.model.credential.constants.CredentialTypesCodes;
import com.atixlabs.semillasmiddleware.app.model.credentialState.CredentialState;
import com.atixlabs.semillasmiddleware.app.model.credentialState.RevocationReason;
import com.atixlabs.semillasmiddleware.app.model.credentialState.constants.RevocationReasonsCodes;
import com.atixlabs.semillasmiddleware.app.processControl.exception.InvalidProcessException;
import com.atixlabs.semillasmiddleware.app.processControl.model.ProcessControl;
import com.atixlabs.semillasmiddleware.app.processControl.model.constant.ProcessControlStatusCodes;
import com.atixlabs.semillasmiddleware.app.processControl.model.constant.ProcessNamesCodes;
import com.atixlabs.semillasmiddleware.app.processControl.service.ProcessControlService;
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
import com.atixlabs.semillasmiddleware.app.model.credential.constants.*;
import com.atixlabs.semillasmiddleware.app.model.credential.Credential;
import com.atixlabs.semillasmiddleware.excelparser.app.dto.SurveyForm;
import com.atixlabs.semillasmiddleware.excelparser.dto.ProcessExcelFileResult;
import com.atixlabs.semillasmiddleware.util.DateUtil;
import lombok.extern.flogger.Flogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
    private RevocationReasonRepository revocationReasonRepository;
    private LoanService loanService;
    private ProcessControlService processControlService;

    @Value("${credentials.pageSize}")
    private String size;


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
            DidiService didiService,
            RevocationReasonRepository revocationReasonRepository, LoanService loanService, ProcessControlService processControlService) {
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
            this.revocationReasonRepository = revocationReasonRepository;
            this.loanService = loanService;
            this.processControlService = processControlService;
    }


    public Optional<Credential> getCredentialById(Long id) {
        //validate credential is in bd
        return credentialRepository.findById(id);
    }

    public Page<Credential> findCredentials(String credentialType, String name, String dniBeneficiary, String
            idDidiCredential, String lastUpdate, List<String> credentialState, Integer pageNumber) {
        List<Credential> credentials;
        Pageable pageable = null;
        if (pageNumber != null && pageNumber > 0 && this.size != null)
            pageable = PageRequest.of(pageNumber, Integer.parseInt(size), Sort.by(Sort.Direction.ASC, "updated"));

        credentials = credentialRepository.findCredentialsWithFilter(credentialType, name, dniBeneficiary, idDidiCredential, lastUpdate, credentialState, pageable);

        return new PageImpl<>(credentials, pageable, credentials.size());
    }

    public Map<Long, String> getRevocationReasonsForUser() {
        Map<Long, String> revocationReasons = new HashMap<>();
        Optional<RevocationReason> expiredReason = revocationReasonRepository.findByReason(RevocationReasonsCodes.EXPIRED_INFO.getCode());
        if (expiredReason.isPresent())
            revocationReasons.put(expiredReason.get().getId(), expiredReason.get().getReason());
        else
            log.error("Error getting expired reason of revocation");

        Optional<RevocationReason> unlinkingReason = revocationReasonRepository.findByReason(RevocationReasonsCodes.UNLINKING.getCode());
        if (unlinkingReason.isPresent())
            revocationReasons.put(unlinkingReason.get().getId(), unlinkingReason.get().getReason());
        else
            log.error("Error getting unlinking reason of revocation");

        return revocationReasons;
    }

    public Optional<String> getReasonFromId(Long idReason) {
        if (idReason != null) {
            Optional<RevocationReason> reason = revocationReasonRepository.findById(idReason);
            if (reason.isPresent()) {
                //validate if the reason could be one allowed to the user.
                Map<Long, String> reasonsForUser = getRevocationReasonsForUser();
                if (reasonsForUser.containsValue(reason.get().getReason()))
                    return Optional.of(reason.get().getReason());
            }
        }
        return Optional.empty();
    }

    private void handleDefaultCredits(LocalDateTime lastTimeProcessRun) {

        List<Loan> loansModifiedInDefault = loanService.findLastLoansModifiedInDefault(lastTimeProcessRun);

        //TODO Que pasa algun paso falla, como lo retomo?? deberia remarcar el horario de los creditos para reejecutarlos
        this.revokeCredentialCredits(loansModifiedInDefault);
        this.revokeCredentialBenefits(loansModifiedInDefault);

    }

    //TODO handle PersonDoesNotExistsException and create loans to update for control in new cycle
    private void handleActiveCredits(LocalDateTime lastTimeProcessRun) throws PersonDoesNotExistsException {

        List<Loan> loansModifiedActive = loanService.findLastLoansModifiedActive(lastTimeProcessRun);

        //TODO Que pasa algun paso falla, como lo retomo?? deberia remarcar el horario de los creditos para reejecutarlos

        for (Loan loan : loansModifiedActive) {
            //check default holder
            this.updateOrCreateCredentialsCreditsAndCredentialBenefits(loan);
        }

    }


    //TODO refactor, what happen with credential credits??
    private void handleFinalizeCredits(LocalDateTime lastTimeProcessRun) throws PersonDoesNotExistsException {

        List<Loan> loansModifiedFinalized = loanService.findLastLoansModifiedFinalized(lastTimeProcessRun);

        //TODO Que pasa algun paso falla, como lo retomo?? deberia remarcar el horario de los creditos para reejecutarlos

        List<CredentialState> pendingAndActiveState = credentialStateRepository.findByStateNameIn(List.of(CredentialStatesCodes.CREDENTIAL_ACTIVE.getCode(), CredentialStatesCodes.PENDING_DIDI.getCode()));

        for (Loan loan : loansModifiedFinalized) {

            Optional<CredentialCredit> opCredit = credentialCreditRepository.findFirstByIdBondareaCreditOrderByDateOfIssueDesc(loan.getIdBondareaLoan());
            if (opCredit.isPresent()) {
                opCredit.get().setFinishDate(DateUtil.getLocalDateTimeNow().toLocalDate());
                credentialCreditRepository.save(opCredit.get());
                log.info("Credential Credit is set to FINALIZE, for credential id historic" + opCredit.get().getIdHistorical());

                List<CredentialBenefits> benefitsHolder = credentialBenefitsRepository.findByCreditHolderDniAndCredentialStateInAndBeneficiaryType(loan.getDniPerson(), pendingAndActiveState, PersonTypesCodes.HOLDER.getCode());
                //there have to be only 1. The holder only have at max 1 holder benefits.
                if (benefitsHolder.size() > 0) {
                    if (this.revokeCredential(benefitsHolder.get(0).getId(), RevocationReasonsCodes.CANCELLED.getCode()))
                        log.info("The credential has been set to finish successfully");
                    else
                        log.error("The credential was not set to finish");
                } else {
                    log.info("There is no active or pending benefits of the holder to be revoked");
                }
            }


        }

    }

    private void handleCancelledCredits(LocalDateTime lastTimeProcessRun) {

        List<Loan> loansModifiedFinalized = loanService.findLastLoansModifiedCancelled(lastTimeProcessRun);

        //TODO Que pasa algun paso falla, como lo retomo?? deberia remarcar el horario de los creditos para reejecutarlos

        List<CredentialState> pendingAndActiveState = credentialStateRepository.findByStateNameIn(List.of(CredentialStatesCodes.CREDENTIAL_ACTIVE.getCode(), CredentialStatesCodes.PENDING_DIDI.getCode()));

        for (Loan loan : loansModifiedFinalized) {

            Optional<CredentialCredit> opCredit = credentialCreditRepository.findFirstByIdBondareaCreditOrderByDateOfIssueDesc(loan.getIdBondareaLoan());
            CredentialCredit updateCredit = opCredit.get();
            if (loan.getStatus().equals(LoanStatusCodes.CANCELLED.getCode())) {
                updateCredit.setFinishDate(DateUtil.getLocalDateTimeNow().toLocalDate());
                credentialCreditRepository.save(updateCredit);
                log.info("Credential Credit is set to CANCEL, for credential id historic" + updateCredit.getIdHistorical());

                //Revoke credential credit
                boolean haveRevokeOk = this.revokeComplete(updateCredit, RevocationReasonsCodes.CANCELLED.getCode());

                List<CredentialBenefits> benefitsHolder = credentialBenefitsRepository.findByCreditHolderDniAndCredentialStateInAndBeneficiaryType(updateCredit.getBeneficiaryDni(), pendingAndActiveState, PersonTypesCodes.HOLDER.getCode());

                //there have to be only 1 being holder of it. The holder only have at max 1 holder benefits.
                if (benefitsHolder.size() > 0) {
                    //revoke only the benefits if the holder does not have another credit. And revoke the familiar benefits given by this credit.
                    boolean result = this.revokeCredential(benefitsHolder.get(0).getId(), RevocationReasonsCodes.CANCELLED.getCode());
                    haveRevokeOk = haveRevokeOk && result;
                } else
                    log.info("There is no active or pending benefits of the holder to be revoked");

                //check results
                if (haveRevokeOk)
                    log.info("The credential has been set to cancel successfully");
                else
                    log.error("The credential was not set to cancel");

            }
        }
    }

    private void updateOrCreateCredentialsCreditsAndCredentialBenefits(Loan loan) throws PersonDoesNotExistsException {

        log.info("Updating credential credit for loan " + loan.getIdBondareaLoan());

        Optional<Person> opHolder = personRepository.findByDocumentNumber(loan.getDniPerson());

        if (opHolder.isPresent()) {

            if (!opHolder.get().isInDefault()) {

                Optional<CredentialCredit> opCredit = credentialCreditRepository.findFirstByIdBondareaCreditOrderByDateOfIssueDesc(loan.getIdBondareaLoan());
                if (opCredit.isPresent()) {
                    this.updateCreditCredential(opCredit.get(), loan);
                    this.updateCredencialBenefitsForLoan(loan);
                } else {
                    this.createCredentialCredit(loan);
                }
            }
        } else {
            log.info("holder " + opHolder.get().getDocumentNumber() + " is en default, dont generate credentials for loan " + loan.getIdBondareaLoan());
        }

    }

    private void updateCreditCredential(CredentialCredit credit, Loan loan) {

        log.info(" updating credential " + credit.getId() + " for loan " + loan.getIdBondareaLoan());
        ///TODO hacer el compare de datos, para verificar si realmente es necesaria la revocacion - creacion (en caso de que se retome el proceso por una falla en un paso posterior)

        Long idHistoricCredit = credit.getIdHistorical();

        this.revokeComplete(credit, RevocationReasonsCodes.UPDATE_INTERNAL.getCode());

        //create new credential given the loan and the old credential
        Optional<Person> opBeneficiary = personRepository.findByDocumentNumber(loan.getDniPerson());
        if (opBeneficiary.isPresent()) {
            CredentialCredit updateCredit = this.buildCreditCredential(loan, opBeneficiary.get());
            updateCredit.setIdHistorical(idHistoricCredit); //assign the old historic.
            //set the amount expired cycles of the previous credential to accumulate the expired cycles
            updateCredit.setAmountExpiredCycles(credit.getAmountExpiredCycles());

            updateCredit = credentialCreditRepository.save(updateCredit);

            //TODO EMMIT DIDI
        }
    }

    private void updateCredencialBenefitsForLoan(Loan loan) {
        Optional<Person> opHolder = personRepository.findByDocumentNumber(loan.getDniPerson());
        if (opHolder.isPresent()) {
            log.info("updating benefit credential for holder" + opHolder.get().getDocumentNumber());

            //TODO update benefits for holder and family
            //List<CredentialState> activePendingStates = credentialStateRepository.findByStateNameIn(List.of(CredentialStatesCodes.CREDENTIAL_ACTIVE.getCode(), CredentialStatesCodes.PENDING_DIDI.getCode()));
            //get All credential for holder, own and familiy
            // List<CredentialBenefits> credentialBenefitHolder = credentialBenefitsRepository.findByCreditHolderDniAndCredentialStateIn(holderInDefault.getDocumentNumber(), activePendingStates);

        }
    }

    private void createCredentialCredit(Loan loan) throws PersonDoesNotExistsException {
        this.createNewCreditCredentials(loan);//TODO <-refactor this, one type of credential for method, and create de familiy benefits
        //TODO EMMIT DIDI
    }

    /**
     * Generate and update the credentials credit.
     * Checking for defaulters, and revoking or activate credentials (credit and benefit)
     */
    //TODO refactor
    public void generateCredentials() throws InvalidProcessException, PersonDoesNotExistsException {
        //check if process in credentials is not running
        if (!processControlService.isProcessRunning(ProcessNamesCodes.BONDAREA) && !processControlService.isProcessRunning(ProcessNamesCodes.CHECK_DEFAULTERS)) {

            LocalDateTime lastTimeProcessRun = processControlService.getProcessTimeByProcessCode(ProcessNamesCodes.CREDENTIALS.getCode());

            ProcessControl processCrendentialControl = processControlService.setStatusToProcess(ProcessNamesCodes.CREDENTIALS, ProcessControlStatusCodes.RUNNING);

            try {
                this.handleDefaultCredits(lastTimeProcessRun);
                this.handleActiveCredits(lastTimeProcessRun);
                this.handleFinalizeCredits(lastTimeProcessRun);
                this.handleCancelledCredits(lastTimeProcessRun);

                //
                //   this.updateCredentialCredit(loan);
            } catch (PersonDoesNotExistsException ex) {
                log.error(ex.getMessage());
            } catch (Exception ex) {
                log.error("Error updating credentials credit ! " + ex.getMessage());
                processControlService.setStatusToProcess(ProcessNamesCodes.CREDENTIALS.getCode(), ProcessControlStatusCodes.FAIL.getCode());
            }


        //create credentials
        List<Loan> newLoans = loanService.findLoansWithoutCredential();

        for (Loan newLoan : newLoans) {
            try {
                this.createNewCreditCredentials(newLoan);
            } catch (PersonDoesNotExistsException ex) {
                log.error(ex.getMessage());
            }
        }

        //finish process
        processControlService.setStatusToProcess(ProcessNamesCodes.CREDENTIALS.getCode(), ProcessControlStatusCodes.OK.getCode());
    } else

    {
        log.info("Generate credentials can't run ! Process " + ProcessNamesCodes.BONDAREA.getCode() + " or " + ProcessNamesCodes.CHECK_DEFAULTERS.getCode() + " is still running");
    }

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
                            " en estado " + credentialOptional.get().getCredentialState().getStateName() +
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

    //todo move into credential class
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

    //todo move into credential type class
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

    //todo move into credential type class
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

    //todo move into credential type class
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
     *
     * @param loan
     * @throws PersonDoesNotExistsException
     */
    public void createNewCreditCredentials(Loan loan) throws PersonDoesNotExistsException {
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

                //after create credit, will create benefit holder credential if the holder has his identity
                List<CredentialState> activePendingStates = credentialStateRepository.findByStateNameIn(List.of(CredentialStatesCodes.CREDENTIAL_ACTIVE.getCode(), CredentialStatesCodes.PENDING_DIDI.getCode()));
                List<CredentialIdentity> holderIdentities = credentialIdentityRepository.findByCreditHolderDniAndBeneficiaryDniAndCredentialStateIn(opBeneficiary.get().getDocumentNumber(), opBeneficiary.get().getDocumentNumber(), activePendingStates);
                //with the same dni in holder and beneficiary, there could be only one identity holder.
                if (holderIdentities.size() > 0)
                    this.createNewBenefitsCredential(holderIdentities.get(0));

            } else {
                //log.error("Person with dni "+ loan.getDniPerson() + " has not been created. The loan exists but the survey with this person has not been loaded"); log will be show in catch
                throw new PersonDoesNotExistsException("Person with dni " + loan.getDniPerson() + " has not been created. The loan exists but the survey with this person has not been loaded");
                //this error is important, have to be shown in front
            }
        } else {
            loan.setHasCredential(true);
            loanRepository.save(loan);
            log.error("The credit with idBondarea " + loan.getIdBondareaLoan() + " has an existent credential");
        }
    }


    public CredentialCredit buildCreditCredential(Loan loan, Person beneficiary) {
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



    /**
     * This will try to create a new benefit credential for the beneficiary.
     * The benefits familiar depends on if he has his OWN credential credit (so he download the app)
     *
     * @param identity
     */
    public void createNewBenefitsCredential(CredentialIdentity identity) {
        //check if person is valid to create a benefit of the type required.
        if (this.isValidPersonForNewBenefits(identity.getCreditHolder(), identity.getBeneficiary())) {

            log.info("Creating Credential Benefits");
            CredentialBenefits benefits = null;
            if (identity.getCreditHolderDni().equals(identity.getBeneficiaryDni()))
                benefits = this.buildBenefitsCredential(identity.getBeneficiary(), identity.getCreditHolder());
            else
                benefits = this.buildBenefitsCredential(identity.getBeneficiary(), identity.getCreditHolder());

            if (benefits != null) {
                credentialBenefitsRepository.save(benefits);
                log.info("Credential benefits created for dni: " + identity.getBeneficiary().getDocumentNumber());
            }
        }
    }

    private boolean isValidPersonForNewBenefits(Person holder, Person beneficiary) {
        List<CredentialState> pendingAndActiveState = credentialStateRepository.findByStateNameIn(List.of(CredentialStatesCodes.CREDENTIAL_ACTIVE.getCode(), CredentialStatesCodes.PENDING_DIDI.getCode()));

        Long holderDni = holder.getDocumentNumber();
        Long beneficiaryDni = beneficiary.getDocumentNumber();
        //is holder
        if (holderDni.equals(beneficiaryDni)) {
            //get actual benefits of the holder
            List<CredentialBenefits> holderBenefit = credentialBenefitsRepository.findByCreditHolderDniAndCredentialStateInAndBeneficiaryType(beneficiaryDni, pendingAndActiveState, PersonTypesCodes.HOLDER.getCode());

            //if he doesnt have a credential benefits, is valid to create
            if (holderBenefit.size() == 0)
                return true;
        } else {
            //is familiar
            List<CredentialIdentity> identitiesFamiliar = credentialIdentityRepository.findByCreditHolderDniAndBeneficiaryDniAndCredentialStateIn(holderDni, beneficiaryDni, pendingAndActiveState);
            //this mean, the beneficiary familiar, have his own identity because he download the app, and the identity familiar created by the survey.
            if (identitiesFamiliar.size() == 2) {
                //validate if he doesnt have a benefit with this holder dni
                Optional<CredentialBenefits> opCredentialBenefit = credentialBenefitsRepository.findByCreditHolderDniAndBeneficiaryDniAndCredentialStateIn(holderDni, beneficiaryDni, pendingAndActiveState);
                return opCredentialBenefit.isEmpty();
            }
        }
        return false;
    }


    /**
     * @param beneficiary
     * @param holder
     * @return
     */
    public CredentialBenefits buildBenefitsCredential(Person beneficiary, Person holder){
            CredentialBenefits benefits = new CredentialBenefits();

            //Person is holder or family
            if (holder.getDocumentNumber().equals(beneficiary.getDocumentNumber())) {
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


            benefits.setCreditHolderDni(holder.getDocumentNumber());
            benefits.setCreditHolder(holder);
            benefits.setCreditHolderFirstName(holder.getFirstName());
            benefits.setCreditHolderLastName(holder.getLastName());
            //End creditHolder changes

        //TODO this should be took from DB - credentialCredit.setIdDidiIssuer();

        //todo this logic, is manage by didi (delete it)
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
     * 2nd Step in the process "Generate", after create the new credits.
     * This process will check the previous credential credit and his loan, to update and | or revoke.
     * If there has been a change, credential will be revoke, then generate a new one.
     *
     * @param loan
     * @throws PersonDoesNotExistsException
     */
    //TODO eliminar
    public void updateCredentialCredit(Loan loan) throws PersonDoesNotExistsException {
        boolean haveRevokeOk = true;

        //get the last credential credit (could be in any state)
        Optional<CredentialCredit> opCredit = credentialCreditRepository.findFirstByIdBondareaCreditOrderByDateOfIssueDesc(loan.getIdBondareaLoan()); //todo importante chequear si es asc o desc
        if (opCredit.isPresent()) {
            CredentialCredit credit = opCredit.get();

            //If credential credit is revoked and default, return
            if (credit.getCredentialState().getStateName().equals(CredentialStatesCodes.CREDENTIAL_REVOKE.getCode()) && loan.getState().equals(LoanStateCodes.DEFAULT.getCode())) {
                log.info("Credential credit is revoked and in default, no need to update, dni: " + credit.getCreditHolderDni());
                return;
            }

            //if credit is in default, only revoke.
            if (loan.getState().equals(LoanStateCodes.DEFAULT.getCode())) {
                //credit is in default. Revoke credential credit.
                //sum +1 on expired cycle and then revoke credential credit
                haveRevokeOk = this.revokeDefaultCredentialCredit(credit);

                if (haveRevokeOk)
                    log.info("The credential has been revoked for default successfully");
                else
                    log.error("The credential was not set to default");
                return;
            }

            log.info("Updating credential credit historic " + credit.getIdHistorical()); //the id historical is the same even if the credential is going to be revoke (the log is more clear)
            // save id historic (before revoking)
            Long idHistoricCredit = credit.getIdHistorical();
            //revoke credential to create the new one ("update")
            this.revokeComplete(credit, RevocationReasonsCodes.UPDATE_INTERNAL.getCode());

            //create new credential given the loan and the old credential
            Optional<Person> opBeneficiary = personRepository.findByDocumentNumber(loan.getDniPerson());
            if (opBeneficiary.isPresent()) {
                CredentialCredit updateCredit = this.buildCreditCredential(loan, opBeneficiary.get());
                updateCredit.setIdHistorical(idHistoricCredit); //assign the old historic.
                //set the amount expired cycles of the previous credential to accumulate the expired cycles
                updateCredit.setAmountExpiredCycles(credit.getAmountExpiredCycles());

                updateCredit = credentialCreditRepository.save(updateCredit);

                List<CredentialState> pendingAndActiveState = credentialStateRepository.findByStateNameIn(List.of(CredentialStatesCodes.CREDENTIAL_ACTIVE.getCode(), CredentialStatesCodes.PENDING_DIDI.getCode()));

                // if credit is finalized the credential credit wont be revoke
                if (loan.getStatus().equals(LoanStatusCodes.FINALIZED.getCode())) {
                    updateCredit.setFinishDate(DateUtil.getLocalDateTimeNow().toLocalDate());
                    credentialCreditRepository.save(updateCredit);
                    log.info("Credential Credit is set to FINALIZE, for credential id historic" + updateCredit.getIdHistorical());

                    List<CredentialBenefits> benefitsHolder = credentialBenefitsRepository.findByCreditHolderDniAndCredentialStateInAndBeneficiaryType(updateCredit.getBeneficiaryDni(), pendingAndActiveState, PersonTypesCodes.HOLDER.getCode());
                    //there have to be only 1. The holder only have at max 1 holder benefits.
                    if (benefitsHolder.size() > 0) {
                        //revoke only the benefits if the holder does not have another credit. And revoke the familiar benefits given by this credit.
                        haveRevokeOk = this.revokeCredential(benefitsHolder.get(0).getId(), RevocationReasonsCodes.CANCELLED.getCode());

                        if (haveRevokeOk)
                            log.info("The credential has been set to finish successfully");
                        else
                            log.error("The credential was not set to finish");
                    } else {
                        log.info("There is no active or pending benefits of the holder to be revoked");
                    }
                } else {
                    if (loan.getStatus().equals(LoanStatusCodes.CANCELLED.getCode())) {
                        updateCredit.setFinishDate(DateUtil.getLocalDateTimeNow().toLocalDate());
                        credentialCreditRepository.save(updateCredit);
                        log.info("Credential Credit is set to CANCEL, for credential id historic" + updateCredit.getIdHistorical());

                        //Revoke credential credit
                        haveRevokeOk = this.revokeComplete(updateCredit, RevocationReasonsCodes.CANCELLED.getCode());

                        List<CredentialBenefits> benefitsHolder = credentialBenefitsRepository.findByCreditHolderDniAndCredentialStateInAndBeneficiaryType(updateCredit.getBeneficiaryDni(), pendingAndActiveState, PersonTypesCodes.HOLDER.getCode());

                        //there have to be only 1. The holder only have at max 1 holder benefits.
                        if (benefitsHolder.size() > 0) {
                            //revoke only the benefits if the holder does not have another credit. And revoke the familiar benefits given by this credit.
                            boolean result = this.revokeCredential(benefitsHolder.get(0).getId(), RevocationReasonsCodes.CANCELLED.getCode());
                            haveRevokeOk = haveRevokeOk && result;
                        } else
                            log.info("There is no active or pending benefits of the holder to be revoked");

                        //check results
                        if (haveRevokeOk)
                            log.info("The credential has been set to cancel successfully");
                        else
                            log.error("The credential was not set to cancel");

                    } else {
                        //credit is ok

                    }
                }

                log.info("Update process finished for credential credit id historic: " + updateCredit.getIdHistorical());
            } else {
                log.error("Person had been created and credential credit too, but person has been deleted eventually !");
                throw new PersonDoesNotExistsException("Error: Person had been created and credential credit too, but person has been deleted eventually");
            }
        }
    }

    /**
     * Check holders status, to search for default and revoke or to reactive credentials.
     * Given the modified credits check if they are
     *
     * @param modifiedLoans
     */
    //TODO delete
    public void checkHolders(List<Loan> modifiedLoans) {
        log.info("Checking holder defaulters or not defaulters");
        //get the holders of the modified loans
        List<Long> dnisOfHolders = modifiedLoans.stream().map(Loan::getDniPerson).distinct().collect(Collectors.toList());
        List<Person> holdersOfModifiedLoans = personRepository.findByDocumentNumberIn(dnisOfHolders);

        //TODO add familiares
        for (Person holder : holdersOfModifiedLoans) {
            if (holder.isInDefault())
                this.revokeDefaultPerson(holder);
            else
                //the holder doesn't have any credit in default check if a holder need to re active credentials
                this.checkToActivateCredentials(holder);
        }
    }


    public void revokeCredentialBenefits(List<Loan> defaultLoans) {

        //get the holders of the modified loans
        List<Long> holdersDnis = defaultLoans.stream().map(Loan::getDniPerson).distinct().collect(Collectors.toList());
        List<Person> holdersInDefault = personRepository.findByDocumentNumberIn(holdersDnis);

        log.info("Revoking Benefits Credentials for " + (holdersInDefault != null ? holdersInDefault.size() : 0) + " holders");

        for (Person holder : holdersInDefault) {
            if (holder.isInDefault()) {
                this.revokeAllCredentialBenefitsForHolder(holder);
                // this.revokeAllCredentialBenefitsForKinsmansHolder(holder);
            } else
                log.error("Holder " + holder.getDocumentNumber() + " is not in default but has default credits ");
        }
    }


    public void revokeCredentialCredit(Loan loan) {

        log.info("Revoke Credentials for Loan " + loan.getIdBondareaLoan());

        Optional<CredentialCredit> opCredit = credentialCreditRepository.findFirstByIdBondareaCreditOrderByDateOfIssueDesc(loan.getIdBondareaLoan());

        if (opCredit.isPresent()) {
            CredentialCredit credit = opCredit.get();

            //If credential credit is revoked and default, return
            if (credit.getCredentialState().getStateName().equals(CredentialStatesCodes.CREDENTIAL_REVOKE.getCode())) {
                log.info("Credential credit is revoked and in default, no need to update, credit : " + credit.getIdBondareaCredit());
                return;
            }

            //if credit is in default, only revoke.
            if (loan.getState().equals(LoanStateCodes.DEFAULT.getCode())) {
                //credit is in default. Revoke credential credit.
                //sum +1 on expired cycle and then revoke credential credit
                if (this.revokeDefaultCredentialCredit(credit))
                    log.info("The credential for loan " + credit.getIdBondareaCredit() + " has been revoked for default successfully");
                else
                    log.error("The credential for loan " + credit.getIdBondareaCredit() + " was not set to default");
                return;
            }

        }
    }

    public void revokeCredentialCredits(List<Loan> loans) {

        for (Loan loan : loans) {

            this.revokeCredentialCredit(loan);
        }

    }

    /**
     * Revoke given the holder. Try to Revoke credential credits in state active or pending. Revoke cred benefits with holder dni.
     *
     * @param holderInDefault
     */
    //TODO delete
    public void revokeDefaultPerson(Person holderInDefault) {
        log.info("Holder " + holderInDefault.getDocumentNumber() + " is in default, checking if its needed to revoke benefits");
        List<Boolean> haveRevokeBenefits = new ArrayList<>();

        List<CredentialState> activePendingStates;
        //get benefits with holder dni (holder benefits and familiar benefits)
        activePendingStates = credentialStateRepository.findByStateNameIn(List.of(CredentialStatesCodes.CREDENTIAL_ACTIVE.getCode(), CredentialStatesCodes.PENDING_DIDI.getCode()));
        List<CredentialBenefits> benefitsBeingHolder = credentialBenefitsRepository.findByCreditHolderDniAndCredentialStateIn(holderInDefault.getDocumentNumber(), activePendingStates);

        for (CredentialBenefits benefit : benefitsBeingHolder) {
            boolean result = this.revokeComplete(benefit, RevocationReasonsCodes.DEFAULT.getCode());
            haveRevokeBenefits.add(result);
        }

        if (haveRevokeBenefits.contains(false))
            log.info("There was a problem revoking a/the credential benefits for person: " + holderInDefault.getDocumentNumber());

    }


    public void revokeAllCredentialBenefitsForHolder(Person holderInDefault) {
        log.info("Holder " + holderInDefault.getDocumentNumber() + " is in default, checking if its needed to revoke benefits");

        List<CredentialState> activePendingStates = credentialStateRepository.findByStateNameIn(List.of(CredentialStatesCodes.CREDENTIAL_ACTIVE.getCode(), CredentialStatesCodes.PENDING_DIDI.getCode()));

        //get All credential for holder, own and familiy
        List<CredentialBenefits> benefitsBeingHolder = credentialBenefitsRepository.findByCreditHolderDniAndCredentialStateIn(holderInDefault.getDocumentNumber(), activePendingStates);

        for (CredentialBenefits benefit : benefitsBeingHolder) {
            if (!this.revokeComplete(benefit, RevocationReasonsCodes.DEFAULT.getCode())) {
                log.error("There was a problem revoking a/the credential benefits " + benefit.getId() + " holder " + benefit.getCreditHolderDni() + " beneficiary " + benefit.getBeneficiaryDni());
                //TODO handle this error
            }
        }

    }

/*Eliminar
    public void revokeAllCredentialBenefitsForKinsmansHolder(Person holderInDefault) {
        log.info("Holder "+ holderInDefault.getDocumentNumber() + " is in default, checking if its needed to revoke benefits for his family members");

        List<Person> family = personRepository.

        List<CredentialState> activePendingStates = credentialStateRepository.findByStateNameIn(List.of(CredentialStatesCodes.CREDENTIAL_ACTIVE.getCode(), CredentialStatesCodes.PENDING_DIDI.getCode()));
        List<CredentialBenefits> benefitsBeingHolder = credentialBenefitsRepository.findByCreditHolderDniAndCredentialStateIn(holderInDefault.getDocumentNumber(), activePendingStates);

        for (CredentialBenefits benefit : benefitsBeingHolder) {
            if(!this.revokeComplete(benefit, RevocationReasonsCodes.DEFAULT.getCode())){
                log.error("There was a problem revoking a/the credential benefits " + benefit.getId()+" holder "+benefit.getCreditHolderDni()+" beneficiary "+benefit.getBeneficiaryDni());
                //TODO handle this error
            }
        }

    }*/

    public void revokeCredentialBenefitsForBeneficiary(Person holder, Person beneficiary, RevocationReasonsCodes revocationCode) {
        log.info("revoking credential for beneficiary " + beneficiary.getDocumentNumber() + " holder " + holder.getDocumentNumber());

        //get benefits with holder dni (holder benefits and familiar benefits)
        List<CredentialState> activePendingStates = credentialStateRepository.findByStateNameIn(List.of(CredentialStatesCodes.CREDENTIAL_ACTIVE.getCode(), CredentialStatesCodes.PENDING_DIDI.getCode()));
        Optional<CredentialBenefits> benefitsBeingHolder = credentialBenefitsRepository.findByCreditHolderDniAndBeneficiaryDniAndCredentialStateIn(holder.getDocumentNumber(), beneficiary.getDocumentNumber(), activePendingStates);

        if (benefitsBeingHolder.isPresent()) {
            if (!this.revokeComplete(benefitsBeingHolder.get(), RevocationReasonsCodes.DEFAULT.getCode())) {
                log.error("There was a problem revoking a/the credential benefits " + benefitsBeingHolder.get().getId() + " holder " + benefitsBeingHolder.get().getCreditHolderDni() + " beneficiary " + benefitsBeingHolder.get().getBeneficiaryDni());
            }
        }

    }

    /**
     * Revoke de credential credit. Then create new one to update the cycle expired.
     * And finally, revoke the one created before.
     *
     * @param creditToRevoke
     * @return boolean
     */
    //TODO ELIMINAR
    /*private boolean revokeDefaultCredentialCredit(CredentialCredit creditToRevoke){
        //revoke the previous credit and save the historic id
        log.info("Set Credential is in default for dni " + creditToRevoke.getCreditHolderDni());
        log.info("Revoking credential credit to update his expired cycle");
        Long idHistoricCredit = creditToRevoke.getIdHistorical();
        this.revokeComplete(creditToRevoke, RevocationReasonsCodes.DEFAULT.getCode());

        Optional<Loan> loan = loanRepository.findByIdBondareaLoan(creditToRevoke.getIdBondareaCredit());
        CredentialCredit updateCredit = this.buildCreditCredential(loan.get(), creditToRevoke.getCreditHolder());
        updateCredit.setIdHistorical(idHistoricCredit); //assign the old historic.
        //set the amount expired cycles of the previous credential to accumulate the expired cycles
        updateCredit.setAmountExpiredCycles(creditToRevoke.getAmountExpiredCycles());

        //increase +1 expired cycles
        int cyclesExpired = updateCredit.getAmountExpiredCycles() + 1;
        updateCredit.setAmountExpiredCycles(cyclesExpired);
        credentialCreditRepository.save(updateCredit);
        log.info("Credit is default. Count +1 cycles expired for credential credit id: " + updateCredit.getId());

        //revoke the whole group including the benefits of them and his familiars
        return this.revokeComplete(updateCredit, RevocationReasonsCodes.DEFAULT.getCode());
    }*/
    private boolean revokeDefaultCredentialCredit(CredentialCredit creditToRevoke) {
        //revoke the previous credit and save the historic id
        log.info("Set Credential is in default for " + creditToRevoke.getIdBondareaCredit() + " dni " + creditToRevoke.getCreditHolderDni());

        return this.revokeComplete(creditToRevoke, RevocationReasonsCodes.DEFAULT.getCode());

    }


    /**
     * If its need, the credential benefits of holder and the familiars are created again.
     * Then the credential credit.
     *
     * @param holder
     */
    private void checkToActivateCredentials(Person holder) {
        log.info("Checking if holder " + holder.getDocumentNumber() + " needs to reactivate the benefits");
        List<CredentialState> activePendingStates;
        activePendingStates = credentialStateRepository.findByStateNameIn(List.of(CredentialStatesCodes.CREDENTIAL_ACTIVE.getCode(), CredentialStatesCodes.PENDING_DIDI.getCode()));
        //find the family of the holder and get the dnis
        List<CredentialIdentity> identitiesOfThEntireFamily = credentialIdentityRepository.findByCreditHolderDniAndCredentialStateIn(holder.getDocumentNumber(), activePendingStates);

        //try to create his benefit whether he is holder or familiar
        identitiesOfThEntireFamily.forEach(aCredentialIdentity -> {
            this.createNewBenefitsCredential(aCredentialIdentity);
        });

    }


    //TODO all of the methods of revocation, could be separated in a special service

    /**
     * Revocation with the business logic.
     * For particular revocations use, this.revokeComplete()
     *
     * @param id
     * @return
     */
    public boolean revokeCredential(Long id, String reasonCode) {
        boolean haveRevokeOk = true;
        CredentialTypesCodes credentialType;

        log.info("Filtering credential with id: " + id);
        Optional<Credential> opCredentialToRevoke = getCredentialById(id);
        if (opCredentialToRevoke.isPresent()) {
            Credential credentialToRevoke = opCredentialToRevoke.get();

            //get the credential type
            try {
                credentialType = CredentialTypesCodes.getEnumByStringValue(credentialToRevoke.getCredentialDescription());
            } catch (IllegalArgumentException ex) {
                log.error("Impossible to revoke credential. There is no credential with type " + credentialToRevoke.getCredentialDescription());
                return false;
            }

            //todo rollback if fail
            log.info("credential type of : " + credentialType.getCode());
            switch (credentialType) {
                case CREDENTIAL_DWELLING:
                case CREDENTIAL_ENTREPRENEURSHIP:
                case CREDENTIAL_BENEFITS_FAMILY:
                    haveRevokeOk = this.revokeComplete(credentialToRevoke, reasonCode);
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
                        haveRevokeOk = this.revokeComplete(credential, reasonCode);
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
                        haveRevokeOk = this.revokeComplete(credential, reasonCode);
                    }

                    break;

                case CREDENTIAL_BENEFITS:
                    //revoke benefit if the holder does not have another credit(active or pending, and did not finish) and also revoke benefits family .
                    activePendingStates = credentialStateRepository.findByStateNameIn(List.of(CredentialStatesCodes.CREDENTIAL_ACTIVE.getCode(), CredentialStatesCodes.PENDING_DIDI.getCode()));
                    List<CredentialCredit> creditsActivePending = credentialCreditRepository.findByCreditHolderDniAndCredentialStateIn(credentialToRevoke.getCreditHolderDni(), activePendingStates);
                    if (creditsActivePending.size() == 0) {
                        haveRevokeOk = this.revokeComplete(credentialToRevoke, reasonCode);

                        if (haveRevokeOk) {
                            //get the familiar benefits in which the holder is within
                            List<CredentialBenefits> familiarBenefits = credentialBenefitsRepository.findByCreditHolderDniAndCredentialStateInAndBeneficiaryType(credentialToRevoke.getCreditHolderDni(), activePendingStates, PersonTypesCodes.FAMILY.getCode());
                            for (CredentialBenefits familiarBenefit : familiarBenefits) {
                                haveRevokeOk = this.revokeComplete(familiarBenefit, reasonCode);
                            }
                        }
                    } else {
                        log.info("Impossible to revoke credential benefit. There are credits in state active or pending.");
                        haveRevokeOk = false;
                    }

                    break;

                case CREDENTIAL_CREDIT:
                    //get the credit credential to get the group
                    Optional<CredentialCredit> credentialCredit = credentialCreditRepository.findById(credentialToRevoke.getId());
                    if (credentialCredit.isPresent()) {
                        //get the group that is not revoked
                        List<CredentialCredit> creditsGroup = this.getCreditGroup(credentialCredit.get().getIdGroup());
                        //for each holder credit -> revoke credit -> revoke benefits -> revoke familiar benefits
                        for (CredentialCredit credit : creditsGroup) {
                            haveRevokeOk = this.revokeComplete(credit, reasonCode); //todo validate successful revocation to continue

                            //get benefits with holder dni (holder benefits and familiar benefits)
                            activePendingStates = credentialStateRepository.findByStateNameIn(List.of(CredentialStatesCodes.CREDENTIAL_ACTIVE.getCode(), CredentialStatesCodes.PENDING_DIDI.getCode()));
                            List<CredentialBenefits> benefits = credentialBenefitsRepository.findByCreditHolderDniAndCredentialStateIn(credit.getCreditHolderDni(), activePendingStates);

                            if (benefits.size() == 0) {
                                log.info("For the dni holder: " + credit.getCreditHolderDni() + " there is no benefits in active nor pending");
                            } else {
                                for (CredentialBenefits benefit : benefits) {
                                    haveRevokeOk = this.revokeComplete(benefit, reasonCode);
                                }
                            }
                        }
                    } else {
                        log.error("Error you are trying to revoke a credit that no longer exist, id: " + credentialToRevoke.getId());
                        haveRevokeOk = false;
                    }

                    break;
            }
        } else {
            //todo throw non-existent credential ?
            log.error("Error you are trying to revoke a non existent credential " + id);
            haveRevokeOk = false;
        }

        return haveRevokeOk;
    }

    /**
     * Get the credit group with the idGroup as long as the credits are not revoked.
     *
     * @param idGroup
     * @return List<CredentialCredit>
     */
    private List<CredentialCredit> getCreditGroup(String idGroup) {
        List<CredentialState> activePendingStates = credentialStateRepository.findByStateNameIn(List.of(CredentialStatesCodes.CREDENTIAL_ACTIVE.getCode(), CredentialStatesCodes.PENDING_DIDI.getCode()));
        //get the group if it is not revoked
        List<CredentialCredit> creditsGroup = credentialCreditRepository.findByIdGroupAndCredentialStateIn(idGroup, activePendingStates);

        return creditsGroup;
    }

    /**
     * Revoke on DB and revoke on didi
     *
     * @param credentialToRevoke
     * @return boolean
     */
    //TODO tirar las exceptions, para que el loan quede para retomar ante un error
    public boolean revokeComplete(Credential credentialToRevoke, String reasonCode) {

        log.info("Starting complete revoking process for credential id: " + credentialToRevoke.getId() + " | credential type: " + credentialToRevoke.getCredentialDescription() + " holder " + credentialToRevoke.getCreditHolderDni() + " beneficiary " + credentialToRevoke.getBeneficiaryDni());
        //revoke on didi if credential was emitted
        if (credentialToRevoke.isEmitted()) {
            if (didiService.didiDeleteCertificate(credentialToRevoke.getIdDidiCredential())) {
                // if didi fail the credential need to know that is needed to be revoked (here think in the best resolution).
                // if this revoke came from the revocation business we will need to throw an error to rollback any change done before.
                return this.revokeCredentialOnlyOnSemillas(credentialToRevoke, reasonCode);
            } else {
                log.info("There was an error deleting credential id: " + credentialToRevoke.getId() + " on didi");
                return false;
            }
        } else
            return this.revokeCredentialOnlyOnSemillas(credentialToRevoke, reasonCode);

    }


    /**
     * Revoke only for internal usage. Only revokes the credential on the DB.
     *
     * @param credentialToRevoke
     * @return boolean
     */

    public boolean revokeCredentialOnlyOnSemillas(Credential credentialToRevoke, String reasonCode) {
        log.info("Revoking the credential " + credentialToRevoke.getId() + " with reason " + reasonCode);
        boolean haveRevoke = true;

        Optional<RevocationReason> reason = revocationReasonRepository.findByReason(reasonCode);
        if (reason.isPresent()) {
            //validate if the credential is in db
            Optional<Credential> opCredential = this.getCredentialById(credentialToRevoke.getId());
            if (opCredential.isEmpty()) {
                haveRevoke = false;
                log.error("The credential with id: " + credentialToRevoke.getId() + " is not in the database");
            } else {

                Credential credential = opCredential.get();
                //get revoke state
                Optional<CredentialState> opStateRevoke = credentialStateRepository.findByStateName(CredentialStatesCodes.CREDENTIAL_REVOKE.getCode());
                if (opStateRevoke.isPresent()) {
                    //revoke if the credential is not revoked yet
                    if (credential.getCredentialState().equals(opStateRevoke.get())) {
                        log.info("The credential " + credential.getId() + " has already been revoked");
                        haveRevoke = false;
                    } else {
                        //revoke
                        credentialToRevoke.setCredentialState(opStateRevoke.get());
                        credentialToRevoke.setRevocationReason(reason.get());
                        credentialToRevoke.setDateOfRevocation(DateUtil.getLocalDateTimeNow());
                        credentialRepository.save(credentialToRevoke);
                        log.info("Credential with id " + credentialToRevoke.getId() + " has been revoked!"); //then append also the reason
                    }
                }
            }

        } else {
            log.error("There is no reason with reason code: " + reasonCode);
            haveRevoke = false;
        }

        return haveRevoke;
    }


    /////

    //in pending state
    public List<Credential> getCredentialsBeingHolder(Long didDni, String did){
        Optional<CredentialState> pendingState = credentialStateRepository.findByStateName(CredentialStatesCodes.PENDING_DIDI.getCode());
        //get all credentials being holder (dniHolder & dniBenef equals)
        List<Credential> holderCredentials = credentialRepository.findByCreditHolderDniAndBeneficiaryDniAndCredentialStateIn(didDni, didDni, List.of(pendingState.get()));

        //add identities to emit being the holder of them
        List<Credential> credentialIdentitiesOfHolder = this.getIdentitiesFromSurveyBeingHolder(didDni, did);
        holderCredentials.addAll(credentialIdentitiesOfHolder);
        //remove the equals (for ex. the identity Titular)
        holderCredentials = holderCredentials.stream().distinct().collect(Collectors.toList());

        return holderCredentials;
    }


    /**
     * Get all the credential identities (Titular or Familiar) given the holderDni, and if they were set, the same did.
     * These identities are from the survey and need to be emmit with the holder DID.
     *
     * if the 3° credential identity is created but not emitted, filtering with the did in here, wont be catch
     * @param didDni
     * @param did
     * @return
     */
    private List<Credential> getIdentitiesFromSurveyBeingHolder(Long didDni, String did){
        //get all the credential identities of the holder created by survey, credentials could have the idReceptor or not (if emmit have failed)
        List<Credential> identitiesFromSurvey = credentialRepository.findCredentialIdentitiesFromSurvey(didDni, List.of(CredentialStatesCodes.PENDING_DIDI.getCode()),  did);
        return identitiesFromSurvey;
    }


    //in pending state
    public List<Credential> getCredentialsBeingBeneficiary(Long didDni, String did){
        List<Credential> credentialsBeingBeneficiary = new ArrayList<>();
        Optional<CredentialState> pendingState = credentialStateRepository.findByStateName(CredentialStatesCodes.PENDING_DIDI.getCode());

        //get identities of did user (in pending)
        List<CredentialIdentity> identitiesOfDidiUserPending = credentialIdentityRepository.findByIdDidiReceptorAndBeneficiaryDniAndCredentialDescriptionAndCredentialStateIn(did, didDni, CredentialTypesCodes.CREDENTIAL_IDENTITY_FAMILY.getCode(), List.of(pendingState.get()));
        credentialsBeingBeneficiary.addAll(identitiesOfDidiUserPending);

        //create new identities where the didUser does not have an identity familiar for each holder.
        credentialsBeingBeneficiary.addAll(this.checkToCreateNewIdentityFamiliar(didDni, did));

        //get the new benefits (in pending)
        List<CredentialBenefits> benefitsFamiliar = credentialBenefitsRepository.findByCreditHolderDniNotAndBeneficiaryDniAndCredentialStateIn(didDni, didDni, List.of(pendingState.get()));
        credentialsBeingBeneficiary.addAll(benefitsFamiliar);

        //get the new benefit to emit
        credentialsBeingBeneficiary.addAll(this.checkToCreateNewBenefitsFamiliar(didDni, did));

        return credentialsBeingBeneficiary;
    }


    /**
     * Create the 3° credential identity (the other 2 are the one created after survey has uploaded -- identidad titular, identidad familiar --)
     * For each different holder, the familiar with did must have a 3° identity with his did.
     * @param didDni
     * @return Optional<Credential>
     */
    private List<CredentialIdentity> checkToCreateNewIdentityFamiliar(Long didDni, String did) {
        List<CredentialIdentity> newIdentitiesToEmit = new ArrayList<>();

        List<CredentialState> pendingActiveState = credentialStateRepository.findByStateNameIn(List.of(CredentialStatesCodes.CREDENTIAL_ACTIVE.getCode(), CredentialStatesCodes.PENDING_DIDI.getCode()));
        //get all the identities of the did user
        List<CredentialIdentity> credentialsIdentitiesBeingFamiliar = credentialIdentityRepository.findByCreditHolderDniNotAndBeneficiaryDniAndCredentialDescriptionAndCredentialStateIn(
                didDni, didDni, CredentialTypesCodes.CREDENTIAL_IDENTITY_FAMILY.getCode(), pendingActiveState);

        if(credentialsIdentitiesBeingFamiliar.size() == 0)
            return Collections.emptyList();

        List<Long> differentHoldersDnis = credentialsIdentitiesBeingFamiliar.stream().map(CredentialIdentity::getCreditHolderDni).distinct().collect(Collectors.toList());

        //for each holder, filter the identities from both.
        for (Long holderDni : differentHoldersDnis) {
            List<CredentialIdentity> identityMatching = credentialsIdentitiesBeingFamiliar.stream().filter(aIdentity ->
                    aIdentity.getCreditHolderDni().equals(holderDni)).collect(Collectors.toList());

            //if there is only 1, its needed to create his own identity.
            if (identityMatching.size() == 1) {
                log.info("didiSync: Creating new Identity familiar for dni " + didDni);
                //here create the identity familiar with did of the beneficiary
                //use as a base credential, the same identity familiar. The difference will be what did it contains.
                CredentialIdentity newFamiliarIdentity = new CredentialIdentity(identityMatching.get(0));
                newFamiliarIdentity.setIdDidiCredential(null);
                newFamiliarIdentity.setDateOfIssue(DateUtil.getLocalDateTimeNow());
                newFamiliarIdentity.setIdDidiReceptor(did);
                setCredentialState(CredentialStatesCodes.PENDING_DIDI.getCode(), newFamiliarIdentity);

                credentialIdentityRepository.save(newFamiliarIdentity);
                log.info("Credential identity familiar has been created for dni " + didDni);

                newIdentitiesToEmit.add(newFamiliarIdentity);
            }
            else{
                log.info("did user "+ didDni + " has already his own credential identity");
            }
        }

        return newIdentitiesToEmit;
    }



    private List<CredentialBenefits> checkToCreateNewBenefitsFamiliar(Long didDni, String did){
        List<CredentialState> pendingActiveState = credentialStateRepository.findByStateNameIn(List.of(CredentialStatesCodes.CREDENTIAL_ACTIVE.getCode(), CredentialStatesCodes.PENDING_DIDI.getCode()));

        List<CredentialBenefits> newBenefitsToEmit = new ArrayList<>();
        Optional<Person> beneficiary = personRepository.findByDocumentNumber(didDni);

        //this person has not been loaded in the survey
        if(beneficiary.isEmpty())
            return Collections.emptyList();

        //get the existent benefits(familiar) to compare if its needed to create another.
        List<CredentialBenefits> benefitsFamiliar = credentialBenefitsRepository.findByCreditHolderDniNotAndBeneficiaryDniAndCredentialStateIn(didDni, didDni, pendingActiveState);
        List<Long> holderDniOfBenefits = benefitsFamiliar.stream().map(CredentialBenefits::getCreditHolderDni).collect(Collectors.toList());


        //get all the identities, to get the holders dni of each one.
        List<CredentialIdentity> identitiesFamiliarOfDidUser = credentialIdentityRepository.findByIdDidiReceptorAndBeneficiaryDniAndCredentialDescriptionAndCredentialStateIn(did, didDni,
                CredentialTypesCodes.CREDENTIAL_IDENTITY_FAMILY.getCode(), pendingActiveState);

        List<Long> holdersDnis = identitiesFamiliarOfDidUser.stream().map(Credential::getCreditHolderDni).collect(Collectors.toList());

        //check if the holder have any credential credit (active or pending)
        for (Long holderDni: holdersDnis) {
            if(this.hasHolderAnyCredentialCredit(holderDni) && !this.benefitsAlreadyExists(holderDni, holderDniOfBenefits)){

                //get person with holderSDni
                Optional<Person> holder = personRepository.findByDocumentNumber(holderDni);

                log.info("Creting new benefits familiar for " + didDni + " with holder " + holderDni);
                CredentialBenefits newFamiliarBenefits = this.buildBenefitsCredential(beneficiary.get(), holder.get());

                credentialBenefitsRepository.save(newFamiliarBenefits);
                log.info("Credential benefits familiar has been created for dni " + didDni);
                newBenefitsToEmit.add(newFamiliarBenefits);
            } else {
                log.info("Didi user " + didDni + " is not valid for new credentials benefits");
            }
        }

        return newBenefitsToEmit;
    }

    public List<Credential> getActiveCredentialsWithDid(String did){
        Optional<CredentialState> opActiveState = credentialStateRepository.findByStateName(CredentialStatesCodes.CREDENTIAL_ACTIVE.getCode());
        return credentialRepository.findByIdDidiReceptorAndCredentialState(did, opActiveState.get());
    }

    private void setCredentialState(String credentialStateString, Credential credential) {
        Optional<CredentialState> credentialState = credentialStateRepository.findByStateName(credentialStateString);
        credentialState.ifPresent(credential::setCredentialState);
    }

    private boolean hasHolderAnyCredentialCredit(Long holderDni){
        List<CredentialState> pendingActiveState = credentialStateRepository.findByStateNameIn(List.of(CredentialStatesCodes.CREDENTIAL_ACTIVE.getCode(), CredentialStatesCodes.PENDING_DIDI.getCode()));

        List<CredentialCredit> credentialCreditsOfHolder = credentialCreditRepository.findByCreditHolderDniAndCredentialStateIn(holderDni, pendingActiveState);
        return credentialCreditsOfHolder.size() > 0;
    }

    private boolean benefitsAlreadyExists(Long holderDni, List<Long> holderOfBenefits){
        return holderOfBenefits.stream().anyMatch(aHolderDNi -> aHolderDNi.equals(holderDni));
    }

}

