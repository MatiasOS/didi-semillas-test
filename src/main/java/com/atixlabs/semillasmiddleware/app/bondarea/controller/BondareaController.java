package com.atixlabs.semillasmiddleware.app.bondarea.controller;

import com.atixlabs.semillasmiddleware.app.bondarea.dto.BondareaLoanDto;
import com.atixlabs.semillasmiddleware.app.bondarea.model.LoanDto;
import com.atixlabs.semillasmiddleware.app.bondarea.service.BondareaService;
import com.atixlabs.semillasmiddleware.app.exceptions.PersonDoesNotExistsException;
import com.atixlabs.semillasmiddleware.app.processControl.exception.InvalidProcessException;
import com.atixlabs.semillasmiddleware.app.service.CredentialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping(BondareaController.URL_MAPPING_CREDENTIAL)
@CrossOrigin(origins = "*", methods= {RequestMethod.GET,RequestMethod.POST})
public class BondareaController {

    public static final String URL_MAPPING_CREDENTIAL = "/bondarea";

    private BondareaService bondareaService;

    private CredentialService credentialService;

    @Autowired
    public BondareaController(BondareaService bondareaService, CredentialService credentialService) {
        this.bondareaService = bondareaService;
        this.credentialService = credentialService;
    }


    @PostMapping("/synchronize")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> synchronizeBondareaLoans() {
        boolean result = true;
        try {
            result = bondareaService.synchronizeLoans();
        } catch (InvalidProcessException ex) {
            log.error("Could not get the process ! " + ex.getMessage());
            return new ResponseEntity<>("Could not get the process !", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (result)
            return new ResponseEntity<>(HttpStatus.OK);
        else
            return new ResponseEntity<>("Error synchronizing and processing data from Bondarea !", HttpStatus.INTERNAL_SERVER_ERROR);
    }


    /**
     * MOCK PURPOSE:
     * Synchronize new loans mock. It can be used to create loans for the 1st time.
     * Then to test the creation of credit credential and benefits credentials
     */
    @PostMapping("/synchronizeMock")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> synchronizeBondareaLoansMock(@RequestBody List<LoanDto> loansJson) {
        log.info("BONDAREA - GET LOANS MOCK");
        List<BondareaLoanDto> loans;
        boolean result = true;

        loans = loansJson.stream().map(loanDto -> new BondareaLoanDto(loanDto)).collect(Collectors.toList());
        try {
            result = bondareaService.synchronizeMockLoans(loans);
        } catch (InvalidProcessException ex) {
            log.error("Could not get the process ! " + ex.getMessage());
            return new ResponseEntity<>("Could not get the process !", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (result)
            return new ResponseEntity<>(HttpStatus.OK);
        else
            return new ResponseEntity<>("Error synchronizing and processing data from Bondarea !", HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @PostMapping("/force-sync-generate")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> forceSyncAndGenerate() {
        boolean result = true;
        try {
            result = bondareaService.synchronizeLoans();
        } catch (InvalidProcessException ex) {
            log.error("Error getting or setting process Bondarea " + ex.getMessage());
            return new ResponseEntity<>("Error getting or setting process Bondarea !", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (!result)
            return new ResponseEntity<>("Error synchronizing and processing data from Bondarea !", HttpStatus.INTERNAL_SERVER_ERROR);

        try {
            credentialService.generateCredentials();
        } catch (InvalidProcessException | PersonDoesNotExistsException ex) {
            log.error("Error getting or setting process Generate-Credential !" + ex.getMessage());
            return new ResponseEntity<>("Error getting or setting process Generate-Credential !", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }


}
