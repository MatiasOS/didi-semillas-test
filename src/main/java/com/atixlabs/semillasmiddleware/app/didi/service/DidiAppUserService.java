package com.atixlabs.semillasmiddleware.app.didi.service;

import com.atixlabs.semillasmiddleware.app.didi.constant.DidiSyncStatus;
import com.atixlabs.semillasmiddleware.app.didi.dto.*;
import com.atixlabs.semillasmiddleware.app.didi.model.DidiAppUser;
import com.atixlabs.semillasmiddleware.app.didi.repository.DidiAppUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;


@Slf4j
@Service
public class DidiAppUserService {

    private DidiAppUserRepository didiAppUserRepository;

    @Autowired
    public DidiAppUserService(DidiAppUserRepository didiAppUserRepository) {
        this.didiAppUserRepository = didiAppUserRepository;
    }

    public String registerNewAppUser(DidiAppUserDto didiAppUserDto) {

        Optional<DidiAppUser> opDidiAppUser  = didiAppUserRepository.findByDniAndActiveTrue(didiAppUserDto.getDni());

        //if DNI is new.
        if (opDidiAppUser.isEmpty()) {
            DidiAppUser didiAppUser = new DidiAppUser(didiAppUserDto);
            didiAppUserRepository.save(didiAppUser);
            return "El nuevo usuario se registro correctamente.";
        }

        DidiAppUser didiAppUser = opDidiAppUser.get();
        if (didiAppUser.getDid().equals(didiAppUserDto.getDid())) {
            //if DID is the same:
            switch (DidiSyncStatus.getEnumByStringValue(didiAppUser.getSyncStatus())) {
                case SYNC_OK:
                case SYNC_MISSING:
                    return "El usuario con Dni: " + didiAppUser.getDni() + " ya posee sus credenciales validadas o en espera con Didi, no se realizó ninguna operación";

                case SYNC_ERROR:
                    didiAppUser.setSyncStatus(DidiSyncStatus.SYNC_MISSING.getCode());
                    didiAppUserRepository.save(didiAppUser);
                    return "Se ha registrado una nueva solucitud de vinculacion de usuario con DID";
            }
        }
        else {
            //if DID is different requires sync:
            //set the last did to no active, and set the new one.
            didiAppUser.setActive(false);
            didiAppUserRepository.save(didiAppUser);

            DidiAppUser didiAppUserNew = new DidiAppUser(didiAppUserDto);
            didiAppUser.setSyncStatus(DidiSyncStatus.SYNC_MISSING.getCode());
            didiAppUserRepository.save(didiAppUserNew);
            return "Se ha modificado el DID para un usuario que posee credenciales, se generarán nuevas credenciales.";
        }
    return "Ocurrio un error procesando la solicitud, intente nuevamente";
    }


    public boolean updateAppUserStatusByCode(Long creditHolderDni, String syncStatusCode) {

        Optional<DidiAppUser> opDidiAppUser = didiAppUserRepository.findByDniAndActiveTrue(creditHolderDni);

        if (opDidiAppUser.isPresent()){
            DidiAppUser didiAppUser = opDidiAppUser.get();

            didiAppUser.setSyncStatus(syncStatusCode);
            didiAppUserRepository.save(didiAppUser);
            return true;
        }
        return false;
    }

}