package com.atixlabs.semillasmiddleware.app.service;

import com.atixlabs.semillasmiddleware.app.model.beneficiary.Person;
import com.atixlabs.semillasmiddleware.app.model.credential.CredentialCredit;
import com.atixlabs.semillasmiddleware.app.repository.CredentialCreditRepository;
import com.atixlabs.semillasmiddleware.app.repository.PersonRepository;
import com.atixlabs.semillasmiddleware.excelparser.app.categories.PersonCategory;
import com.atixlabs.semillasmiddleware.excelparser.app.dto.SurveyForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
public class CredentialService {

    @Autowired
    private CredentialCreditRepository credentialCreditRepository;

    @Autowired
    private PersonRepository personRepository;

    public void buildAllCredentialsFromForm(SurveyForm surveyForm)
    {
        log.info("buildCredentials: "+this.toString());
        buildPerson(surveyForm);
        buildCreditCredential(surveyForm);
    }

    /**
     * The following are non-public methods, isolating functionality.
     * to make public methods easier to read.
     * @param surveyForm
     */
    private void buildPerson(SurveyForm surveyForm){
        log.info("  buildPerson");

        PersonCategory personCategory = (PersonCategory) surveyForm.getCategoryData(PersonCategory.class);
        if(personCategory != null) {
            Person person = new Person();
            person.setName(personCategory.getNameAndSurname());
            person.setDocumentType("DNI HARDCODE");
            person.setDocumentNumber(personCategory.getIdNumber());
            person.setBirthDate(personCategory.getBirthdate());
            log.info(person.toString());

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
            CredentialCredit credentialCredit = new CredentialCredit();
            credentialCredit.setBeneficiaryDocumentType("DNI HARDCODE");
            credentialCredit.setBeneficiaryDocumentNumber(personCategory.getIdNumber());
            credentialCredit.setDateOfExpiry(LocalDateTime.now());
            credentialCredit.setDateOfIssue(LocalDateTime.now());
            credentialCredit.setCurrentCycle("imported-from-excel");
            credentialCredit.setCreditState("pre-credential");
            log.info(credentialCredit.toString());

            Optional<CredentialCredit> credentialCreditOptional = credentialCreditRepository.findByBeneficiaryDocumentTypeAndBeneficiaryDocumentNumber(
                    credentialCredit.getBeneficiaryDocumentType(), credentialCredit.getBeneficiaryDocumentNumber()
            );

            if(credentialCreditOptional.isEmpty())
                credentialCreditRepository.save(credentialCredit);
            else
                log.info("Ya existe una credencial para el "+
                        credentialCredit.getBeneficiaryDocumentType()+" " +
                        credentialCredit.getBeneficiaryDocumentNumber());
        }
    }

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
        credentialCredit.setBeneficiaryDocumentNumber(29302594L);
        credentialCreditRepository.save(credentialCredit);
    }




}
