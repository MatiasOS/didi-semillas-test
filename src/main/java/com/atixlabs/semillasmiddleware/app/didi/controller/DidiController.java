package com.atixlabs.semillasmiddleware.app.didi.controller;

import com.atixlabs.semillasmiddleware.app.didi.dto.DidiAppUserDto;
import com.atixlabs.semillasmiddleware.app.didi.dto.DidiCredential;
import com.atixlabs.semillasmiddleware.app.didi.dto.DidiEmmitCredentialResponse;
import com.atixlabs.semillasmiddleware.app.didi.dto.DidiGetAllCredentialResponse;
import com.atixlabs.semillasmiddleware.app.didi.service.DidiAppUserService;
import com.atixlabs.semillasmiddleware.app.didi.service.DidiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(DidiController.URL_MAPPING_CREDENTIAL)
@CrossOrigin(origins = "*", methods= {RequestMethod.GET,RequestMethod.POST})
@Slf4j
public class DidiController {

    public static final String URL_MAPPING_CREDENTIAL = "/credentials";

    private DidiAppUserService didiAppUserService;
    private DidiService didiService;

    @Autowired
    public DidiController(DidiAppUserService didiAppUserService, DidiService didiService) {
        this.didiAppUserService = didiAppUserService;
        this.didiService = didiService;

    }


    @PostMapping("/didi")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, String> registerNewDidiAppUser(@RequestBody DidiAppUserDto didiAppUserDto){
        Map<String, String> jsonMessage = new HashMap<>();

        String message = didiAppUserService.registerNewAppUser(didiAppUserDto);
        jsonMessage.put("message", message);
        return jsonMessage;
    }

    //ONLY FOR TESTING
    @GetMapping("/didi/login")
    @ResponseStatus(HttpStatus.OK)
    public String authDidi() {
        return didiService.getAuthToken();
    }

    @GetMapping("/didi/sync")
    @ResponseStatus(HttpStatus.OK)
    public String didiCredentialSync() {

        return didiService.didiCredentialSync();
    }



    //todo: delete on production environment, this endpoint is for testing purposes only, to keep clean didi-issuer

    @DeleteMapping("/didi/all/")
    @ResponseStatus(HttpStatus.OK)
    public String didiDeleteAllCredentials() {
        DidiGetAllCredentialResponse didiGetAllCredentialResponse = didiService.didiGetAllCredentials();

        for (DidiCredential credential : didiGetAllCredentialResponse.getData()) {
            didiService.didiDeleteCertificate(credential.get_id());
        }

        log.info("Finalizo el proceso de borrado");
        return "Finalizo el proceso de borrado";
    }

}
