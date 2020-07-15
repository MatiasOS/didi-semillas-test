package com.atixlabs.semillasmiddleware.didiService;

import com.atixlabs.semillasmiddleware.app.didi.constant.DidiSyncStatus;
import com.atixlabs.semillasmiddleware.app.didi.model.DidiAppUser;
import com.atixlabs.semillasmiddleware.app.didi.service.DidiAppUserService;
import com.atixlabs.semillasmiddleware.app.didi.service.DidiService;
import com.atixlabs.semillasmiddleware.app.didi.service.SyncDidiProcessService;
import com.atixlabs.semillasmiddleware.app.exceptions.CredentialException;
import com.atixlabs.semillasmiddleware.app.model.credential.CredentialCredit;
import com.atixlabs.semillasmiddleware.app.service.CredentialCreditService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
@Slf4j
public class SyncDidiProcessServiceTest {

    @Mock
    private CredentialCreditService credentialCreditService;

    @Mock
    private DidiAppUserService didiAppUserService;

    @Mock
    private DidiService didiService;

    @InjectMocks
    private SyncDidiProcessService syncDidiProcessService;

    @Before
    public void setupMocks() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void whenEmmitCredentialCreditsAndCredentialCreditsToEmmitIsEmpty_thenDoNothing() throws CredentialException {

        when(credentialCreditService.getCredentialCreditsOnPendindDidiState()).thenReturn(new ArrayList<CredentialCredit>());

        syncDidiProcessService.emmitCredentialCredits();


    }

    @Test
    public void whenHolderNotHaveDIDRegister_thenCredentialCreditNotEmmit(){

        when(didiAppUserService.getDidiAppUserByDni(anyLong())).thenReturn(null);

        CredentialCredit credentialCredit = this.getCredentialCreditMock();
        credentialCredit.setIdDidiReceptor(null);

        syncDidiProcessService.emmitCredentialCredit(credentialCredit);

        Assert.assertNull(credentialCredit.getIdDidiReceptor());

    }


    @Test
    public void whenHolderHaveDIDRegister_thenEmmitCredentialCredit(){


        CredentialCredit credentialCredit = this.getCredentialCreditMock();
        credentialCredit.setIdDidiReceptor(null);

        DidiAppUser didiAppUser = this.getDidiAppUserMock();

        when(didiAppUserService.getDidiAppUserByDni(credentialCredit.getCreditHolderDni())).thenReturn(didiAppUser);

        syncDidiProcessService.emmitCredentialCredit(credentialCredit);

        verify(didiService, times(1)).createAndEmmitCertificateDidi(credentialCredit);


    }


    private CredentialCredit getCredentialCreditMock(){
        CredentialCredit credentialCredit = new CredentialCredit();
        credentialCredit.setCreditHolderDni(36637842L);
        credentialCredit.setIdDidiReceptor(null);
        credentialCredit.setId(1L);
        credentialCredit.setCreditHolder(null);
        credentialCredit.setCreditType("CONAMI 31");
        credentialCredit.setCurrentCycle("1");
        credentialCredit.setCreditState("Vigente");
        credentialCredit.setExpiredAmount(new BigDecimal(9L));
        credentialCredit.setIdBondareaCredit("987654");
        credentialCredit.setIdGroup("idGroup");
        credentialCredit.setTotalCycles(12);
        credentialCredit.setBeneficiaryDni(36637842L);
        credentialCredit.setBeneficiaryFirstName("Flor");
        credentialCredit.setBeneficiaryLastName("Tior");
        credentialCredit.setCreditHolderFirstName("Flor");
        credentialCredit.setCreditHolderLastName("Tiore");

        return credentialCredit;
    }

    private DidiAppUser getDidiAppUserMock(){
        DidiAppUser didiAppUser = new DidiAppUser();
        didiAppUser.setDni(36637842L);
        didiAppUser.setDid("did:ethr:0x45b5bf83cc010c18739110d8d3397f1fa8a4d20a");
        didiAppUser.setSyncStatus(DidiSyncStatus.SYNC_OK.getCode());

        return didiAppUser;
    }

}