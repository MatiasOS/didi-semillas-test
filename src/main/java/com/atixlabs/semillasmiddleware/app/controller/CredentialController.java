package com.atixlabs.semillasmiddleware.app.controller;

import com.atixlabs.semillasmiddleware.app.bondarea.model.Loan;
import com.atixlabs.semillasmiddleware.app.bondarea.service.LoanService;
import com.atixlabs.semillasmiddleware.app.dto.CredentialDto;
import com.atixlabs.semillasmiddleware.app.exceptions.NoExpiredConfigurationExists;
import com.atixlabs.semillasmiddleware.app.exceptions.PersonDoesNotExists;
import com.atixlabs.semillasmiddleware.app.model.beneficiary.Person;
import com.atixlabs.semillasmiddleware.app.model.credential.Credential;
import com.atixlabs.semillasmiddleware.app.model.credential.CredentialCredit;
import com.atixlabs.semillasmiddleware.app.model.credential.constants.CredentialStatesCodes;
import com.atixlabs.semillasmiddleware.app.model.credential.constants.CredentialTypesCodes;
import com.atixlabs.semillasmiddleware.app.service.CredentialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(CredentialController.URL_MAPPING_CREDENTIAL)
@CrossOrigin(origins = "*", methods= {RequestMethod.GET,RequestMethod.POST})
@Slf4j
public class CredentialController {

    public static final String URL_MAPPING_CREDENTIAL = "/credentials";

    private CredentialService credentialService;

    private LoanService loanService;

    @Autowired
    public CredentialController(CredentialService credentialService, LoanService loanService) {
        this.credentialService = credentialService;
        this.loanService = loanService;
    }


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CredentialDto> findCredentials(@RequestParam(required = false) String credentialType,
                                                        @RequestParam(required = false) String name,
                                                        @RequestParam(required = false) String dniBeneficiary,
                                                        @RequestParam(required = false) String idDidiCredential,
                                                        @RequestParam(required = false) String dateOfIssue,
                                                        @RequestParam(required = false) String dateOfExpiry,
                                                        @RequestParam(required = false) List<String> credentialState) {

        List<Credential> credentials;
        try {
            credentials = credentialService.findCredentials(credentialType, name, dniBeneficiary, idDidiCredential, dateOfExpiry, dateOfIssue, credentialState);
        }
        catch (Exception e){
            log.info("There has been an error searching for credentials " + e);
            return Collections.emptyList();
        }

       List<CredentialDto> credentialsDto = credentials.stream().map(aCredential -> new CredentialDto(aCredential)).collect(Collectors.toList());
       return credentialsDto;
    }

    @GetMapping("/states")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, String> findCredentialStates() {
        Map<String, String> credentialStates = new HashMap<>();
        for (CredentialStatesCodes states: CredentialStatesCodes.values()) {
            credentialStates.put(states.name(), states.getCode());
        }

        return credentialStates;
    }

    @GetMapping("/types")
    @ResponseStatus(HttpStatus.OK)
    public List<String> findCredentialTypes() {
        List<String> credentialTypes =  Arrays.stream(CredentialTypesCodes.values()).map(state -> state.getCode()).collect(Collectors.toList());
        return credentialTypes;
    }


    @PostMapping("/generate")
    @ResponseStatus(HttpStatus.CREATED)
    public void generateCredentialsCredit() {
        List<Loan> newLoans = loanService.findLoansWithoutCredential();

        for (Loan newLoan : newLoans) {
            try {
                credentialService.createNewCreditCredentials(newLoan);

            } catch (PersonDoesNotExists ex) {
                log.error(ex.getMessage());
            }
        }

            List<Loan> loansWithCredentials = loanService.findLoansWithCredential();
            //if loan has been modified after the credential credit
            for (Loan loan : loansWithCredentials) {
                CredentialCredit creditToUpdate = credentialService.validateCredentialCreditToUpdate(loan);
                if (creditToUpdate != null) {
                    try {
                        credentialService.updateCredentialCredit(loan, creditToUpdate);
                    } catch (NoExpiredConfigurationExists ex) {
                        log.error(ex.getMessage());
                    } catch (PersonDoesNotExists ex) {
                        log.error(ex.getMessage());
                    }
                    catch (Exception ex){
                        log.error(ex.getMessage());
                    }
                }
            }
        }




    }
