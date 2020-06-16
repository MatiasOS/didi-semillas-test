package com.atixlabs.semillasmiddleware.didiService;


import com.atixlabs.semillasmiddleware.app.bondarea.model.Loan;
import com.atixlabs.semillasmiddleware.app.bondarea.repository.LoanRepository;
import com.atixlabs.semillasmiddleware.app.bondarea.service.BondareaService;
import com.atixlabs.semillasmiddleware.app.didi.constant.DidiSyncStatus;
import com.atixlabs.semillasmiddleware.app.didi.dto.DidiAppUserDto;
import com.atixlabs.semillasmiddleware.app.didi.model.DidiAppUser;
import com.atixlabs.semillasmiddleware.app.didi.repository.DidiAppUserRepository;
import com.atixlabs.semillasmiddleware.app.didi.service.DidiAppUserService;
import com.atixlabs.semillasmiddleware.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class DidiAppUserServiceTest {

    @InjectMocks
    private DidiAppUserService didiAppUserService;

    @Mock
    private DidiAppUserRepository didiAppUserRepository;


    @Before
    public void setupMocks(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void saveNewDidiAppUser() {
        DidiAppUserDto didiAppUserDto = new DidiAppUserDto(40000000L,"did:ethr:0x73c47226d044af432829b60d0de38d657b0643dc" );
        DidiAppUser didiAppUser = new DidiAppUser(didiAppUserDto);
        when(didiAppUserRepository.save(any(DidiAppUser.class))).thenReturn(didiAppUser);
        String response = didiAppUserService.registerNewAppUser(didiAppUserDto);
        Assertions.assertEquals(response, "El nuevo usuario se registro correctamente.");
    }

    @Test
    public void updateDidiAppUser(){
        DidiAppUser didiAppUserExisting = new DidiAppUser(new DidiAppUserDto(10000000L,"did:ethr:0x73c47226d044af432829b60d0de38d657b0643dc"));
        DidiAppUserDto didiAppUserDtoUpdate = new DidiAppUserDto(10000000L,"did:ethr:0x73c47226d044af432829b60d0de38d657b0643dcCAMBIO" );

        when(didiAppUserRepository.findByDniAndActiveTrue(anyLong())).thenReturn(Optional.of(didiAppUserExisting));

        when(didiAppUserRepository.save(any(DidiAppUser.class))).thenReturn(didiAppUserExisting);
        String response = didiAppUserService.registerNewAppUser(didiAppUserDtoUpdate);
        Assertions.assertEquals(response, "Se ha modificado el DID para un usuario que posee credenciales, se generarán nuevas credenciales.");
        Assertions.assertEquals(didiAppUserExisting.getSyncStatus(), DidiSyncStatus.SYNC_MISSING.getCode());
    }

}
