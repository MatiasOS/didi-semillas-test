package com.atixlabs.semillasmiddleware.app.service;

import com.atixlabs.semillasmiddleware.app.model.beneficiary.Person;
import com.atixlabs.semillasmiddleware.app.model.credential.CredentialCredit;
import com.atixlabs.semillasmiddleware.app.repository.CredentialCreditRepository;
import com.atixlabs.semillasmiddleware.app.repository.PersonRepository;
import com.atixlabs.semillasmiddleware.excelparser.app.categories.PersonCategory;
import com.atixlabs.semillasmiddleware.excelparser.app.dto.SurveyForm;
import com.atixlabs.semillasmiddleware.app.dto.CredentialDto;
import com.atixlabs.semillasmiddleware.app.model.credential.Credential;
import com.atixlabs.semillasmiddleware.app.repository.CredentialRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class CredentialService {

    private CredentialRepository credentialRepository;
    private CredentialCreditRepository credentialCreditRepository;

    @Autowired
    public CredentialService(CredentialCreditRepository credentialCreditRepository, CredentialRepository credentialRepository) {
        this.credentialCreditRepository = credentialCreditRepository;
        this.credentialRepository = credentialRepository;
    }

    @Autowired
    private PersonRepository personRepository;

    /*public void buildAllCredentialsFromForm(SurveyForm surveyForm)
    {
        log.info("buildAllCredentialsFromForm: "+this.toString());
        buildPerson(surveyForm);
        buildCreditCredential(surveyForm);
        buildIdentityOwnerCredential(surveyForm);
        buildIdentityRelativeCredential(surveyForm);
        buildEntrepreneurshipCredential(surveyForm);
        buildHomeCredential(surveyForm);
        buildHealthOwnerCredential(surveyForm);
        buildHeathRelativeCredential(surveyForm);
        buildKnowledgeOwnerCredential(surveyForm);
        buildKnowledgeRelativeCredential(surveyForm);
        buildScholarLoanCredential(surveyForm);
        buildCoursesOwnerCredential(surveyForm);
        buildCoursesRelativeCredential(surveyForm);
    }
    */


    /**
     * The following are non-public methods, isolating functionality.
     * to make public methods easier to read.
     * @param surveyForm
     */

    private void buildIdentityOwnerCredential(SurveyForm surveyForm) {
    }

    private void buildIdentityRelativeCredential(SurveyForm surveyForm) {
    }

    private void buildEntrepreneurshipCredential(SurveyForm surveyForm) {
    }

    private void buildHomeCredential(SurveyForm surveyForm) {
    }

    private void buildHealthOwnerCredential(SurveyForm surveyForm) {
    }

    private void buildHeathRelativeCredential(SurveyForm surveyForm) {
    }

    private void buildKnowledgeOwnerCredential(SurveyForm surveyForm) {
    }

    private void buildKnowledgeRelativeCredential(SurveyForm surveyForm) {
    }

    private void buildScholarLoanCredential(SurveyForm surveyForm) {
    }
    




    public List<Credential> findCredentials(String credentialType, String name, String dniBeneficiary, String idDidiCredential, String dateOfExpiry, String dateOfIssue, List<String> credentialState, String credentialStatus) {
        List<Credential> credentials;
        try {
         credentials = credentialRepository.findCredentialsWithFilter(credentialType, name, dniBeneficiary, idDidiCredential, dateOfExpiry, dateOfIssue, credentialState, credentialStatus);
        }
        catch (Exception e){
                log.info("There has been an error searching for credentials " + e);
                return Collections.emptyList();
            }
         return  credentials;
    }

    public void addCredentialCredit(){
        CredentialCredit credentialCredit = new CredentialCredit();
    }

    private void buildCoursesOwnerCredential(SurveyForm surveyForm) {
    }

    private void buildCoursesRelativeCredential(SurveyForm surveyForm) {
    }



    /*private void buildPerson(SurveyForm surveyForm){
        log.info("  buildPerson");

        PersonCategory personCategory = (PersonCategory) surveyForm.getCategoryData(PersonCategory.class);
        if(personCategory != null) {
            Person person = new Person(personCategory);

            Optional<Person> personOptional = personRepository.findByDocumentTypeAndDocumentNumber(person.getDocumentType(),person.getDocumentNumber());
            if(personOptional.isEmpty())
                personRepository.save(person);
            else
                log.info("Ya existe una persona con "+personOptional.get().getDocumentType()+" "+personOptional.get().getDocumentNumber());
        }
    }

    private void buildCreditCredential(SurveyForm surveyForm){
        log.info("  buildCreditCredential");

        PersonCategory personCategory = (PersonCategory) surveyForm.getCategoryData(PersonCategory.class);
        if(personCategory != null) {
            CredentialCredit credentialCredit = new CredentialCredit(personCategory);

            Optional<CredentialCredit> credentialCreditOptional = credentialCreditRepository.findByBeneficiaryDocumentTypeAndBeneficiaryDocumentNumber(
                    credentialCredit.getBeneficiaryDocumentType(), credentialCredit.getDniBeneficiary()
            );

            if(credentialCreditOptional.isEmpty())
                credentialCreditRepository.save(credentialCredit);
            else
                log.info("Ya existe una credencial para el "+
                        credentialCredit.getBeneficiaryDocumentType()+" " +
                        credentialCredit.getDniBeneficiary());
        }
    }
    */


    public void saveCredentialCreditMock(){
        CredentialCredit credentialCredit = new CredentialCredit();
        credentialCredit.setDateOfExpiry(LocalDateTime.now());

        credentialCredit.setDateOfIssue(LocalDateTime.now());

        //credentialCredit.setId(1L);//autogenerated
        //credentialCredit.setIdDidiCredential(456L);//null because must be completed by didi
        //credentialCredit.setIdDidiIssuer(123L);//null must be completed by didi
        //credentialCredit.setIdDidiReceptor(234L);//null must be completed by didi
        //credentialCredit.setIdHistorical(77L);//null must be completed by didi
        //credentialCredit.setIdRelatedCredential(534L);//tbd value


        credentialCredit.setCreditName("credit name");
        credentialCredit.setIdGroup(1111L);
        credentialCredit.setGroupName("GroupName");
        credentialCredit.setRol("rol");
        credentialCredit.setAmount(1d);
        credentialCredit.setCurrentCycle("Cycle");
        credentialCredit.setCreditState("state");
        credentialCredit.setDniBeneficiary(29302594L);
        credentialCreditRepository.save(credentialCredit);
    }



    public List<CredentialDto> findAllCredentialsMock(){
        List<CredentialDto> credentials = new ArrayList<>();


        CredentialDto credential1 = new CredentialDto(1L,2L,LocalDateTime.now(),LocalDateTime.now().plusDays(1),"Jorge Rodrigues",29302594L,"Estado", "CrdentialCredit");
        credentials.add(credential1);

        CredentialDto credential2 = new CredentialDto(2L,3L,LocalDateTime.now(),LocalDateTime.now().plusDays(1),"Uriel Brama",29302594L,"Estado", "CrdentialIdentity");
        credentials.add(credential2);

        CredentialDto credential3 = new CredentialDto(3L,4L,LocalDateTime.now(),LocalDateTime.now().plusDays(2),"Pepe Grillo",293025464L,"Estado", "CredentialCredit");
        credentials.add(credential3);

        CredentialDto credential4 = new CredentialDto(4L,5L,LocalDateTime.now(),LocalDateTime.now().plusDays(6),"Armando Thompson",29302594L,"Estado", "CredentialDwelling");
        credentials.add(credential4);

        return credentials;

    }

}
