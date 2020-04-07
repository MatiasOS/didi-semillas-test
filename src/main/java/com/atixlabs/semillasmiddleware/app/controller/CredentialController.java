package com.atixlabs.semillasmiddleware.app.controller;

import com.atixlabs.semillasmiddleware.app.dto.CredentialDto;
import com.atixlabs.semillasmiddleware.app.model.credential.Credential;
import com.atixlabs.semillasmiddleware.app.model.credential.constants.CredentialStatesCodes;
import com.atixlabs.semillasmiddleware.app.repository.CredentialCreditRepository;
import com.atixlabs.semillasmiddleware.app.repository.CredentialRepository;
import com.atixlabs.semillasmiddleware.app.repository.CredentialServiceCustom;
import com.atixlabs.semillasmiddleware.app.service.CredentialService;
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
                                                        @RequestParam(required = false) String credentialState) {


        List<Credential> credentials;
        try {
            credentials = credentialServiceCustom.findCredentialsWithFilter(credentialType, name, dniBeneficiary, idDidiCredential, dateOfExpiry, dateOfIssue, credentialState);
        }
        catch (Exception e){
            log.info("There has been an error searching for credentials " + e);
            return Collections.emptyList();
        }
       List<CredentialDto> credentialsDto = credentials.stream().map(aCredential -> new CredentialDto(aCredential)).collect(Collectors.toList());
       log.info("FIND CREDENTIALS -- " + credentialsDto.toString());
       return credentialsDto;
    }


    @PatchMapping("/revoke/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> revokeCredential(@PathVariable @NotNull @Min(1) Long id){
        Optional<Credential> oPCredentialToRevoke = credentialRepository.findById(id);
        if(oPCredentialToRevoke.isPresent()){
            credentialService.revokeCredential(oPCredentialToRevoke.get());
            return  ResponseEntity.status(HttpStatus.OK).body("Revoked succesfully");
        }
        else
        {
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error there is no credential with id " + id);
        }

    }


    @GetMapping("/credentialStates")
    @ResponseStatus(HttpStatus.OK)
    public List<String> findCredentialStates() {
        log.info("find credential states ----> " + Arrays.stream(CredentialStatesCodes.values()).map(state -> state.getCode()).collect(Collectors.toList()));
        return Arrays.stream(CredentialStatesCodes.values()).map(state -> state.getCode()).collect(Collectors.toList());
    }


    @GetMapping("/credentialTypes")
    @ResponseStatus(HttpStatus.OK)
    public List<String> findCredentialTypes() {
       // log.info("find credential types ----> " + Arrays.stream(CredentialStatesCodes.values()).map(state -> state.getCode()).collect(Collectors.toList()));
        //return Arrays.stream(CredentialStatesCodes.values()).map(state -> state.getCode()).collect(Collectors.toList());
        //TODO: desmockear creando un enum con los tipos de credenciales como lso estados para utilizar en el searchbox
        List<String> crdentialTypes= new ArrayList<>();
        crdentialTypes.add("CredentialCredit");
        crdentialTypes.add("CredentialDwelling");
        crdentialTypes.add("CredentialIdentity");
        crdentialTypes.add("CredentialEntrepreneurship");
        return crdentialTypes;
    }


}
