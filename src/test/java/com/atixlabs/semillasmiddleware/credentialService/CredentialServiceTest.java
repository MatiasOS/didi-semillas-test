package com.atixlabs.semillasmiddleware.credentialService;

import com.atixlabs.semillasmiddleware.app.model.beneficiary.Person;
import com.atixlabs.semillasmiddleware.app.model.credential.Credential;
import com.atixlabs.semillasmiddleware.app.model.credential.CredentialCredit;
import com.atixlabs.semillasmiddleware.app.model.credential.CredentialIdentity;
import com.atixlabs.semillasmiddleware.app.model.credential.constants.CredentialStatesCodes;
import com.atixlabs.semillasmiddleware.app.model.credential.constants.CredentialTypesCodes;
import com.atixlabs.semillasmiddleware.app.repository.CredentialRepository;
import com.atixlabs.semillasmiddleware.app.repository.CredentialServiceCustom;
import com.atixlabs.semillasmiddleware.app.service.CredentialService;
import com.atixlabs.semillasmiddleware.util.DateUtil;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class CredentialServiceTest {

    @Mock
    CredentialServiceCustom credentialServiceCustom;

    @Mock
    CredentialRepository credentialRepository;

    @InjectMocks
    CredentialService credentialService;

    @Autowired
    DateUtil util;

    private Person getBeneficiaryMock(){
        Person person = new Person();
        person.setId(1L);
        person.setDocumentNumber(29302594L);
        person.setName("Pepito");
        return person;
    }

    private Optional<Credential> getACredentialMock(){
        Credential credential1 = new CredentialCredit();
        credential1.setId(1L);
        credential1.setIdDidiCredential(2L);
        credential1.setCredentialType(CredentialTypesCodes.CREDENTIAL_CREDIT.getCode());
        credential1.setDateOfIssue(LocalDateTime.now());
        credential1.setDateOfExpiry(LocalDateTime.now().plusDays(1));
        credential1.setBeneficiary(getBeneficiaryMock());
        credential1.setCredentialState(CredentialStatesCodes.CREDENTIAL_ACTIVE.getCode());

        return Optional.of(credential1);
    }
    private List<Credential> credentialsMock(){
        List<Credential> credentials = new ArrayList<>();

        Person beneficiary = getBeneficiaryMock();
        Optional<Credential> c = getACredentialMock();
        CredentialCredit credential1 = new CredentialCredit(c.get());
        credential1.setDniBeneficiary(29302594L);
        credential1.setCreditState("Estado");
        credentials.add(credential1);

        CredentialIdentity credentialIdentity = new CredentialIdentity();
        credentialIdentity.setId(2L);
        credentialIdentity.setDniCreditHolder(34534534L);
        credential1.setCredentialType(CredentialTypesCodes.CREDENTIAL_IDENTITY.getCode());
        credentialIdentity.setNameBeneficiary("Pepito");
        credentialIdentity.setDateOfExpiry(util.getLocalDateTimeNow());
        credentialIdentity.setDateOfIssue(util.getLocalDateTimeNow().minusDays(1));
        credentialIdentity.setBeneficiary(beneficiary);
        credentialIdentity.setCredentialState(CredentialStatesCodes.CREDENTIAL_ACTIVE.getCode());
        credentials.add(credentialIdentity);



        return credentials;
    }

    @Test
    public void getActiveCredentials() {
        when(credentialServiceCustom.findCredentialsWithFilter(null,null,null, null, null, null,"Vigente")).thenReturn((List<Credential>) credentialsMock());

        List<Credential> credentials = credentialServiceCustom.findCredentialsWithFilter(null,null,null, null, null, null,"Vigente");

        verify(credentialServiceCustom).findCredentialsWithFilter(null,null,null, null, null, null,"Vigente");

        //List<CredentialDto> credentialsDto = credentials.stream().map(aCredential -> new CredentialDto(aCredential)).collect(Collectors.toList());
        log.info("credenciales " +credentials.toString());


        Assertions.assertTrue(credentials.size() > 0);
        Assertions.assertEquals(credentialsMock().get(0).getId() ,credentials.get(0).getId());
        Assertions.assertEquals(credentialsMock().get(0).getCredentialState(), credentials.get(0).getCredentialState());
        Assertions.assertEquals(credentialsMock().get(0).getBeneficiary().getDocumentNumber() ,credentials.get(0).getBeneficiary().getDocumentNumber());
        Assertions.assertEquals(credentialsMock().get(0).getIdDidiCredential() ,credentials.get(0).getIdDidiCredential());
        Assertions.assertTrue(credentials.get(0).getDateOfExpiry() != null);
        Assertions.assertTrue(credentials.get(0).getDateOfIssue() != null);
        Assertions.assertEquals(credentialsMock().get(0).getBeneficiary().getName() ,credentials.get(0).getBeneficiary().getName());
    }

    @Test
    public void revokeCredential(){
        when(credentialRepository.findById(anyLong())).thenReturn(getACredentialMock());

        Optional<Credential> opCredential = credentialRepository.findById(1L);
        Credential cred = credentialService.revokeCredential(opCredential.get());
        log.info("Credential obtained " + cred.toString());

        Assertions.assertEquals(CredentialStatesCodes.CREDENTIAL_REVOKE.getCode(), cred.getCredentialState());
    }

}
