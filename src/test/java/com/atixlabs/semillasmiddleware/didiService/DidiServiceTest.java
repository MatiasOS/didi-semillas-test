package com.atixlabs.semillasmiddleware.didiService;


import com.atixlabs.semillasmiddleware.app.didi.constant.DidiSyncStatus;
import com.atixlabs.semillasmiddleware.app.didi.dto.CreateCertificateResult;
import com.atixlabs.semillasmiddleware.app.didi.dto.DidiCreateCredentialResponse;
import com.atixlabs.semillasmiddleware.app.didi.dto.DidiCredential;
import com.atixlabs.semillasmiddleware.app.didi.dto.DidiCredentialData;
import com.atixlabs.semillasmiddleware.app.didi.model.DidiAppUser;
import com.atixlabs.semillasmiddleware.app.didi.repository.DidiAppUserRepository;
import com.atixlabs.semillasmiddleware.app.didi.service.DidiAppUserService;
import com.atixlabs.semillasmiddleware.app.didi.service.DidiService;
import com.atixlabs.semillasmiddleware.app.model.beneficiary.Person;
import com.atixlabs.semillasmiddleware.app.model.credential.Credential;
import com.atixlabs.semillasmiddleware.app.model.credential.CredentialDwelling;
import com.atixlabs.semillasmiddleware.app.model.credential.CredentialIdentity;
import com.atixlabs.semillasmiddleware.app.model.credential.constants.CredentialCategoriesCodes;
import com.atixlabs.semillasmiddleware.app.model.credential.constants.CredentialStatesCodes;
import com.atixlabs.semillasmiddleware.app.model.credentialState.CredentialState;
import com.atixlabs.semillasmiddleware.app.repository.CredentialRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class DidiServiceTest {

    @InjectMocks
    private DidiAppUserService didiAppUserService;

    @Autowired
    @InjectMocks
    private DidiService didiService;

    @Mock
    private DidiAppUserRepository didiAppUserRepository;
    @Mock
    private CredentialRepository credentialRepository;

    //@Captor
    //private ArgumentCaptor<Loan> captor;

    @Before
    public void setupMocks() {
      //  ReflectionTestUtils.setField(didiService, "didiBaseUrl", "https://test.com");
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(didiService, "didiBaseUrl", "https://test.com");
    }



    public ArrayList<DidiAppUser> getAppUserMockOneMissing(){
        ArrayList<DidiAppUser> didiAppUsersMock = new ArrayList<>();
        didiAppUsersMock.add(new DidiAppUser(10000000L, "did:ethr:0x73c47226d044af432829b60d0de38d657b0643dc", DidiSyncStatus.SYNC_OK.getCode()));
        didiAppUsersMock.add(new DidiAppUser(20000000L, "did:ethr:0x73c47226d044af432829b60d0de38d657b0643dc", DidiSyncStatus.SYNC_OK.getCode()));
        didiAppUsersMock.add(new DidiAppUser(30000000L, "did:ethr:0x73c47226d044af432829b60d0de38d657b0643dc", DidiSyncStatus.SYNC_MISSING.getCode()));
        return didiAppUsersMock;
    }

    public ArrayList<Credential> getCredentialsCreditHolder(){
        CredentialIdentity credential = new CredentialIdentity();
        credential.setId(1L);
        credential.setIdDidiIssuer("texto-fijo-semillas");
        credential.setIdDidiReceptor("");
        credential.setIdDidiCredential("");
        //credential.setIdHistorical(1L);
        credential.setDateOfIssue(LocalDateTime.now());
        //this.dateOfRevocation = credential.dateOfRevocation;

        Person creditHolder = createCreditHolderMock();

        credential.setCreditHolder(creditHolder);
        credential.setCreditHolderDni(creditHolder.getDocumentNumber());
        credential.setCreditHolderFirstName(creditHolder.getFirstName());
        credential.setCreditHolderLastName(creditHolder.getLastName());

        credential.setBeneficiary(creditHolder);
        credential.setBeneficiaryDni(creditHolder.getDocumentNumber());
        credential.setBeneficiaryFirstName(creditHolder.getFirstName());
        credential.setBeneficiaryLastName(creditHolder.getLastName());
        credential.setBeneficiaryBirthDate( LocalDate.now());

        credential.setCredentialState(createCredentialStateActiveMock());
        credential.setCredentialDescription(CredentialCategoriesCodes.IDENTITY.getCode());
        credential.setCredentialCategory(CredentialCategoriesCodes.IDENTITY.getCode());

        ArrayList<Credential> credentials = new ArrayList<>();
        credentials.add(credential);
        return credentials;
    }

    public CredentialIdentity getCredentialIdentity(){
        CredentialIdentity credential = new CredentialIdentity();
        credential.setId(1L);
        credential.setIdDidiIssuer(null);
        credential.setIdDidiReceptor("did:ethr:0x45b5bf83cc010c18739110d8d3397f1fa8a4d20a");
        credential.setIdDidiCredential("5f1752524acdb3002dbe48b1");
        //credential.setIdHistorical(1L);
        credential.setDateOfIssue(LocalDateTime.now());
        //this.dateOfRevocation = credential.dateOfRevocation;

        Person creditHolder = createCreditHolderMock();

        credential.setCreditHolder(creditHolder);
        credential.setCreditHolderDni(36637842L);
        credential.setCreditHolderFirstName("Florencc");
        credential.setCreditHolderLastName("Torielll");
        credential.setBeneficiaryGender("Femenino");
        credential.setRelationWithCreditHolder("titualar");
        credential.setBeneficiaryBirthDate(LocalDate.now());

        credential.setBeneficiary(creditHolder);
        credential.setBeneficiaryDni(36637842L);
        credential.setBeneficiaryFirstName("Floren");
        credential.setBeneficiaryLastName("Toriel");

        credential.setCredentialState(createCredentialStateActiveMock());
        credential.setCredentialDescription(CredentialCategoriesCodes.IDENTITY.getCode());
        credential.setCredentialCategory(CredentialCategoriesCodes.IDENTITY.getCode());

        return credential;
    }


    public CredentialDwelling getCredentialDwelling(){
        CredentialDwelling credential = new CredentialDwelling();
        credential.setId(1L);
        credential.setIdDidiIssuer("texto-fijo-semillas");
        credential.setIdDidiReceptor("");
        credential.setIdDidiCredential("");
        //credential.setIdHistorical(1L);
        credential.setDateOfIssue(LocalDateTime.now());
        //this.dateOfRevocation = credential.dateOfRevocation;

        Person creditHolder = createCreditHolderMock();

        credential.setCreditHolder(creditHolder);
        credential.setCreditHolderDni(creditHolder.getDocumentNumber());
        credential.setCreditHolderFirstName(creditHolder.getFirstName());
        credential.setCreditHolderLastName(creditHolder.getLastName());

        credential.setBeneficiary(creditHolder);
        credential.setBeneficiaryDni(creditHolder.getDocumentNumber());
        credential.setBeneficiaryFirstName(creditHolder.getFirstName());
        credential.setBeneficiaryLastName(creditHolder.getLastName());

        credential.setCredentialState(createCredentialStateActiveMock());
        credential.setCredentialDescription(CredentialCategoriesCodes.DWELLING.getCode());
        credential.setCredentialCategory(CredentialCategoriesCodes.DWELLING.getCode());

        return credential;
    }

    private Person createCreditHolderMock(){
        Person person = new Person();
        person.setId(1L);
        person.setFirstName("CreditHolder FirstName");
        person.setLastName("CreditHolder LastName");
        person.setDocumentNumber(30000000L);
        return person;
    }

    private CredentialState createCredentialStateActiveMock(){
        CredentialState credentialState = new CredentialState();
        credentialState.setId(1L);
        credentialState.setStateName(CredentialStatesCodes.CREDENTIAL_ACTIVE.getCode());
        return credentialState;
    }

    private DidiCreateCredentialResponse createDidiCredentialResponseOkMock(){
        DidiCreateCredentialResponse didiCreateCredentialResponse = new DidiCreateCredentialResponse();
        didiCreateCredentialResponse.setStatus("success");
        ArrayList<DidiCredential> didiCredentials = new ArrayList<>();
        DidiCredential didiCredential = new DidiCredential();
        didiCredential.set_id("ID-VALIDO-DIDI");
        didiCredentials.add(didiCredential);
        didiCreateCredentialResponse.setData(didiCredentials);
        return didiCreateCredentialResponse;
    }

    @Test
    @Ignore
    public void didiCredentialSyncEmpty() {
        ArrayList<DidiAppUser> didiAppUsers = new ArrayList<>();
        when(didiAppUserRepository.findBySyncStatusIn(any(ArrayList.class))).thenReturn(didiAppUsers);
        String response = didiService.didiCredentialSync();
        Assertions.assertEquals(response, "No existen credenciales pendientes para enviar hacia didi");
    }

    @Test
    @Ignore
    public void didiCredentialSyncAllOk() {
        when(didiAppUserRepository.findBySyncStatusIn(any(ArrayList.class))).thenReturn(getAppUserMockOneMissing());

        //ArrayList<Credential> creditHolders = credentialRepository.findByCreditHolderDniIn(dniList);
        when(credentialRepository.findByCreditHolderDniIn(any(ArrayList.class))).thenReturn(getCredentialsCreditHolder());

        ArrayList<Credential> beneficiaries = new ArrayList<>();
        when(credentialRepository.findByBeneficiaryDniIn(any(ArrayList.class))).thenReturn(beneficiaries);

        //tuve que hacer createCertificatyDidi public para poder mockear. (luego usaré spy)
        when(didiService.createCertificateDidiCall(anyString(), any(DidiCredentialData.class), anyBoolean())).thenReturn(createDidiCredentialResponseOkMock());

        String response = didiService.didiCredentialSync();

        Assertions.assertEquals(response, "La credencial fue actualizada con exito, se obtuvo el id de didi: ID-VALIDO-DIDI");
    }

    @Test
    @Ignore
    public void createCertificateDidiIdentityTest(){
        Credential credential = this.getCredentialIdentity();
        CreateCertificateResult createCertificateResult = didiService.createCertificateDidi(credential);
        Assert.assertNotNull(createCertificateResult.getCertificateId());
    }

    @Test
    @Ignore
    public void createCertificateDidiDwellingyTest(){
        Credential credential = this.getCredentialDwelling();
        CreateCertificateResult createCertificateResult = didiService.createCertificateDidi(credential);
        Assert.assertNotNull(createCertificateResult.getCertificateId());
    }

}
