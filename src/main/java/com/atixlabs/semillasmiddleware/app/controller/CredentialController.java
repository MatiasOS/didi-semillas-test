package com.atixlabs.semillasmiddleware.app.controller;

import com.atixlabs.semillasmiddleware.app.dto.CredentialDto;
import com.atixlabs.semillasmiddleware.app.model.credential.Credential;
import com.atixlabs.semillasmiddleware.app.model.credential.constants.CredentialStatesCodes;
import com.atixlabs.semillasmiddleware.app.model.credential.constants.CredentialStatusCodes;
import com.atixlabs.semillasmiddleware.app.model.credential.constants.CredentialTypesCodes;
import com.atixlabs.semillasmiddleware.app.repository.CredentialCreditRepository;
import com.atixlabs.semillasmiddleware.app.repository.CredentialRepository;
import com.atixlabs.semillasmiddleware.app.repository.CredentialServiceCustom;
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

    public static final String URL_MAPPING_CREDENTIAL = "/credential";

    @Autowired
    private CredentialService credentialService;

    @Autowired
    private CredentialCreditRepository credentialCreditRepository;

    @Autowired
    CredentialServiceCustom credentialServiceCustom;

    @Autowired
    private CredentialRepository credentialRepository;

    @RequestMapping(value = "/createCredit", method = RequestMethod.GET)
    public void createCredit() {
        log.info(" createCredit ");
        credentialService.addCredentialCredit();
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CredentialDto> findCredentials(@RequestParam(required = false) String credentialType,
                                                        @RequestParam(required = false) String name,
                                                        @RequestParam(required = false) String dniBeneficiary,
                                                        @RequestParam(required = false) String idDidiCredential,
                                                        @RequestParam(required = false) String dateOfIssue,
                                                        @RequestParam(required = false) String dateOfExpiry,
                                                        @RequestParam(required = false) List<String> credentialState,
                                                        @RequestParam(required = false) String credentialStatus) {


        List<Credential> credentials;
        try {
            credentials = credentialServiceCustom.findCredentialsWithFilter(credentialType, name, dniBeneficiary, idDidiCredential, dateOfExpiry, dateOfIssue, credentialState, credentialStatus);
        }
        catch (Exception e){
            log.info("There has been an error searching for credentials " + e);
            return Collections.emptyList();
        }
       List<CredentialDto> credentialsDto = credentials.stream().map(aCredential -> new CredentialDto(aCredential)).collect(Collectors.toList());
       log.info("FIND CREDENTIALS -- " + credentialsDto.toString());
       return credentialsDto;
    }

    @GetMapping("/credentialStates")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, String> findCredentialStates() {
        Map<String, String> credentialStates = new HashMap<>();
        for (CredentialStatesCodes states: CredentialStatesCodes.values()) {
            credentialStates.put(states.name(), states.getCode());
        }
        log.info("find credential states ----> " + credentialStates);
        return credentialStates;
    }

    @GetMapping("/credentialTypes")
    @ResponseStatus(HttpStatus.OK)
    public List<String> findCredentialTypes() {
        //TODO: desmockear creando un enum con los tipos de credenciales como lso estados para utilizar en el searchbox
        List<String> credentialTypes =  Arrays.stream(CredentialTypesCodes.values()).map(state -> state.getCode()).collect(Collectors.toList());
        log.info("find credential types ----> " + credentialTypes);
        return credentialTypes;
    }

    @GetMapping("/credentialStatus")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, String> findCredentialStatus() {
        Map<String, String> credentialStatus = new HashMap<>();
        for (CredentialStatusCodes status: CredentialStatusCodes.values()) {
            credentialStatus.put(status.name(), status.getCode());
        }

       // Map<CredentialStatesCodes, String> credentialStatus = (Map<CredentialStatesCodes, String >) Arrays.stream(CredentialStatusCodes.values()).map(state ->Map.of(state,state.getCode())).collect(Collectors.toMap(e -> e, CredentialStatusCodes::getCode));
        log.info("find credential status ----> " + credentialStatus);
        return credentialStatus;
    }


}
