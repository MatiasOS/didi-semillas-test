package com.atixlabs.semillasmiddleware.app.didi.service;

import com.atixlabs.semillasmiddleware.app.didi.constant.DidiSyncStatus;
import com.atixlabs.semillasmiddleware.app.didi.dto.*;
import com.atixlabs.semillasmiddleware.app.didi.model.DidiAppUser;
import com.atixlabs.semillasmiddleware.app.didi.model.constant.DidiAppUserOperationResult;
import com.atixlabs.semillasmiddleware.app.didi.repository.DidiAppUserRepository;
import com.atixlabs.semillasmiddleware.app.model.action.ActionLevelEnum;
import com.atixlabs.semillasmiddleware.app.model.action.ActionTypeEnum;
import com.atixlabs.semillasmiddleware.app.service.ActionLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
public class DidiAppUserService {


    private DidiAppUserRepository didiAppUserRepository;

    @Autowired
    public DidiAppUserService(DidiAppUserRepository didiAppUserRepository) {
        this.didiAppUserRepository = didiAppUserRepository;
    }

    public DidiAppUserOperationResult addNewDidiAppUser(DidiAppUserDto didiAppUserDto) {

        log.info("addNewDidiAppUser");

        DidiAppUser didiAppUser = new DidiAppUser(didiAppUserDto);
        didiAppUserRepository.save(didiAppUser);
        //TODO to aop
        // this.registerNewAppDidiUserAction(didiAppUserDto);
        return DidiAppUserOperationResult.NEW_USER_REGISTER_OK;
    }

    public DidiAppUserOperationResult registerNewAppUser(DidiAppUserDto didiAppUserDto) {
        log.info("Register new app user: "+didiAppUserDto.toString());
        Optional<DidiAppUser> opDidiAppUser = didiAppUserRepository.findByDniAndActiveTrue(didiAppUserDto.getDni());

        //if DNI is new.
        if (opDidiAppUser.isEmpty()) {
            return this.addNewDidiAppUser(didiAppUserDto);
        } else {
            DidiAppUser didiAppUser = new DidiAppUser(didiAppUserDto);
            if (opDidiAppUser.get().getDid().equals(didiAppUserDto.getDid())) {
                //if DID is the same:
                log.info("DidiSyncStatus: "+ DidiSyncStatus.getEnumByStringValue(didiAppUser.getSyncStatus()));
                switch (DidiSyncStatus.getEnumByStringValue(didiAppUser.getSyncStatus())) {
                    case SYNC_OK:
                    case SYNC_MISSING:
                        return DidiAppUserOperationResult.USER_ALREADY_EXIST_NO_CHANGES;

                    case SYNC_ERROR:
                        opDidiAppUser.get().setSyncStatus(DidiSyncStatus.SYNC_MISSING.getCode());
                        didiAppUserRepository.save(opDidiAppUser.get());
                        return DidiAppUserOperationResult.NEW_REQUEST_REGISTERED;
                }
            } else {
                //if DID is different requires sync:

                opDidiAppUser.get().setDid(didiAppUserDto.getDid());
                opDidiAppUser.get().setSyncStatus(DidiSyncStatus.SYNC_MISSING.getCode());
                didiAppUserRepository.save(opDidiAppUser.get());
                return DidiAppUserOperationResult.NEW_DID_REGISTERED_FOR_USER;
            }
        }
        return DidiAppUserOperationResult.ERROR;
    }


    public boolean updateAppUserStatusByCode(Long creditHolderDni, String syncStatusCode) {

        Optional<DidiAppUser> didiAppUser = didiAppUserRepository.findByDniAndActiveTrue(creditHolderDni);

        if (didiAppUser.isPresent()) {
            didiAppUser.get().setSyncStatus(syncStatusCode);
            didiAppUserRepository.save(didiAppUser.get());
            return true;
        }
        return false;
    }

    public Optional<DidiAppUser> getDidiAppUserByDni(Long dni) {
        return didiAppUserRepository.findByDniAndActiveTrue(dni);

    }

    /**
     * return all didi app user news an with errors process
     *
     * @return
     */
    public List<DidiAppUser> getDidiAppUsersNeedProcess() {

        return this.didiAppUserRepository.findByActiveAndSyncStatusIn(true,this.getDidiSyncStatusNeedProcess());
    }

    private ArrayList<String> getDidiSyncStatusNeedProcess() {

    ArrayList<String> didiSyncStatus = new ArrayList<>();
        didiSyncStatus.add(DidiSyncStatus.SYNC_MISSING.getCode());
        didiSyncStatus.add(DidiSyncStatus.SYNC_ERROR.getCode());
        didiSyncStatus.add(DidiSyncStatus.SYNC_NEW.getCode());

        return didiSyncStatus;
    }

    public DidiAppUser save(DidiAppUser didiAppUser){
        return didiAppUserRepository.save(didiAppUser);
    }
}