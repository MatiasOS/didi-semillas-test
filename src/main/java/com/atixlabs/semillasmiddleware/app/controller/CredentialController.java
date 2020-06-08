package com.atixlabs.semillasmiddleware.app.controller;

import com.atixlabs.semillasmiddleware.app.bondarea.model.Loan;
import com.atixlabs.semillasmiddleware.app.bondarea.service.LoanService;
import com.atixlabs.semillasmiddleware.app.dto.CredentialDto;
import com.atixlabs.semillasmiddleware.app.exceptions.NoExpiredConfigurationExists;
import com.atixlabs.semillasmiddleware.app.exceptions.PersonDoesNotExists;
import com.atixlabs.semillasmiddleware.app.model.credential.Credential;
import com.atixlabs.semillasmiddleware.app.model.credential.CredentialCredit;
import com.atixlabs.semillasmiddleware.app.model.credential.constants.CredentialStatesCodes;
import com.atixlabs.semillasmiddleware.app.model.credential.constants.CredentialTypesCodes;
import com.atixlabs.semillasmiddleware.app.service.CredentialService;
import com.atixlabs.semillasmiddleware.app.didi.service.DidiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(CredentialController.URL_MAPPING_CREDENTIAL)
@CrossOrigin(origins = "*", methods= {RequestMethod.GET,RequestMethod.POST, RequestMethod.PATCH})
@Slf4j
public class CredentialController {

    public static final String URL_MAPPING_CREDENTIAL = "/credentials";

    private CredentialService credentialService;

    private LoanService loanService;

    @Autowired
    public CredentialController(CredentialService credentialService, LoanService loanService, DidiService didiService) {
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
        } catch (Exception e) {
            log.info("There has been an error searching for credentials " + e);
            return Collections.emptyList();
        }

        return credentials.stream().map(aCredential -> CredentialDto.constructBasedOnCredentialType(aCredential)).collect(Collectors.toList());
    }

    @GetMapping("/states")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, String> findCredentialStates() {
        Map<String, String> credentialStates = new HashMap<>();
        for (CredentialStatesCodes states : CredentialStatesCodes.values()) {
            credentialStates.put(states.name(), states.getCode());
        }

        return credentialStates;
    }

    @GetMapping("/types")
    @ResponseStatus(HttpStatus.OK)
    public List<String> findCredentialTypes() {
        List<String> credentialTypes = Arrays.stream(CredentialTypesCodes.values()).map(state -> state.getCode()).collect(Collectors.toList());
        return credentialTypes;
    }

    @GetMapping("/revocation-reasons")
    @ResponseStatus(HttpStatus.OK)
    public Map<Long, String> findRevocationReasons() {
        //todo this is not the best option to obtain the reasons able by the user.
        return credentialService.getRevocationReasonsForUser();
    }



    @PatchMapping("/revoke/{idCredential}/reason/{idReason}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> revokeCredential(@PathVariable @NotNull @Min(1) Long idCredential, @PathVariable @NotNull @Min(1) Long idReason) {
        Optional<String> opRevocationReason = credentialService.getReasonFromId(idReason);
        if (opRevocationReason.isPresent()) {
            Optional<Credential> credentialToRevoke = credentialService.getCredentialById(idCredential);
            if (credentialToRevoke.isPresent()) {
                if (credentialToRevoke.get().isManuallyRevocable()) {
                    boolean revokeOk = credentialService.revokeCredential(idCredential, opRevocationReason.get());
                    if (revokeOk)
                        return ResponseEntity.status(HttpStatus.OK).body("Revoked successfully");
                    else
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error trying to revoke credential with id: " + idCredential);
                } else
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Credential with id: " + idCredential + " is not manually revocable");
            } else {
                log.error("There is no credential with id: " + idCredential);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There is no credential with id: " + idCredential);
            }
        } else
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Is not possible to revoke with reason " + idReason);
    }



    @PostMapping("/generate")
    @ResponseStatus(HttpStatus.CREATED)
    public void generateCredentialsCredit() {

        //update credentials
        List<Loan> loansWithCredentials = loanService.findLoansWithCredential();
        //if loan has been modified after the credential credit
        for (Loan loan : loansWithCredentials) {
            CredentialCredit creditToUpdate = credentialService.validateCredentialCreditToUpdate(loan);
            if (creditToUpdate != null) {
                try {
                    credentialService.updateCredentialCredit(loan, creditToUpdate);
                } catch (NoExpiredConfigurationExists | PersonDoesNotExists ex) {
                    log.error(ex.getMessage());
                } catch (Exception ex) {
                    log.error("Error ! " + ex.getMessage());
                }
            }
        }

        //create credentials
        List<Loan> newLoans = loanService.findLoansWithoutCredential();

        for (Loan newLoan : newLoans) {
            try {
                credentialService.createNewCreditCredentials(newLoan);
            } catch (PersonDoesNotExists ex) {
                log.error(ex.getMessage());
            }
        }
    }

}
