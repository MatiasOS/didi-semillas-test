package com.atixlabs.semillasmiddleware.credentialService;

import com.atixlabs.semillasmiddleware.app.bondarea.model.Loan;
import com.atixlabs.semillasmiddleware.app.bondarea.model.constants.LoanStateCodes;
import com.atixlabs.semillasmiddleware.app.bondarea.model.constants.LoanStatusCodes;
import com.atixlabs.semillasmiddleware.app.didi.service.DidiService;
import com.atixlabs.semillasmiddleware.app.exceptions.CredentialException;
import com.atixlabs.semillasmiddleware.app.model.beneficiary.Person;
import com.atixlabs.semillasmiddleware.app.model.configuration.ParameterConfiguration;
import com.atixlabs.semillasmiddleware.app.model.configuration.constants.ConfigurationCodes;
import com.atixlabs.semillasmiddleware.app.model.credential.CredentialBenefits;
import com.atixlabs.semillasmiddleware.app.model.credential.constants.CredentialCategoriesCodes;
import com.atixlabs.semillasmiddleware.app.model.credential.constants.CredentialStatesCodes;
import com.atixlabs.semillasmiddleware.app.model.credential.constants.CredentialTypesCodes;
import com.atixlabs.semillasmiddleware.app.model.credential.constants.PersonTypesCodes;
import com.atixlabs.semillasmiddleware.app.model.credentialState.CredentialState;
import com.atixlabs.semillasmiddleware.app.model.credentialState.RevocationReason;
import com.atixlabs.semillasmiddleware.app.model.credentialState.constants.RevocationReasonsCodes;
import com.atixlabs.semillasmiddleware.app.repository.*;
import com.atixlabs.semillasmiddleware.app.service.CredentialBenefitService;
import com.atixlabs.semillasmiddleware.app.service.CredentialStateService;
import com.atixlabs.semillasmiddleware.app.service.PersonService;
import com.atixlabs.semillasmiddleware.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
@Slf4j
public class CredentialBenefitsServiceTest {

    @InjectMocks
    private CredentialBenefitService credentialBenefitsService;

    @Mock
    private ParameterConfigurationRepository parameterConfigurationRepository;

    @Mock
    private CredentialStateService credentialStateService;

    @Mock
    private PersonService personService;

    @Mock
    private CredentialBenefitsRepository credentialBenefitsRepository;

    @Mock
    private RevocationReasonRepository revocationReasonRepository;

    @Mock
    private CredentialRepository credentialRepository;

    @Mock
    private DidiService didiService;

    @Mock
    private CredentialIdentityRepository credentialIdentityRepository;

    @Captor
    private ArgumentCaptor<CredentialBenefits> credentialBenefitCaptor;

    @Before
    public void setupMocks() {
        MockitoAnnotations.initMocks(this);
    }


    //BUILDS
    @Test
    public void buildCredentialBenefitsForHolder() throws CredentialException {

        when(parameterConfigurationRepository.findByConfigurationName(ConfigurationCodes.ID_DIDI_ISSUER.getCode())).thenReturn(getParameterConfigurationDidiIssuerMock());
        Optional<CredentialState>  StatePendingDidi = createCredentialStatePendingDidiMock();
        when(credentialStateService.getCredentialPendingDidiState()).thenReturn(StatePendingDidi.get());

        Loan loan = this.getMockLoan();

        Person holder = this.createPersonMock();

        try {
            CredentialBenefits credentialBenefits =  credentialBenefitsService.buildNewBenefitsCredential(holder,holder,PersonTypesCodes.HOLDER);

            Assertions.assertEquals(PersonTypesCodes.HOLDER.getCode(), credentialBenefits.getBeneficiaryType());
            Assertions.assertEquals(CredentialCategoriesCodes.BENEFIT.getCode(), credentialBenefits.getCredentialCategory());
            Assertions.assertEquals(StatePendingDidi.get(), credentialBenefits.getCredentialState());
            Assertions.assertEquals(CredentialTypesCodes.CREDENTIAL_BENEFITS.getCode(), credentialBenefits.getCredentialDescription());

            Assertions.assertEquals(getParameterConfigurationDidiIssuerMock().get().getValue(), credentialBenefits.getIdDidiIssuer());
            Assertions.assertNull( credentialBenefits.getIdDidiReceptor());
            Assertions.assertNull( credentialBenefits.getIdDidiCredential());

            Assertions.assertNull( credentialBenefits.getIdHistorical());

            Assertions.assertNull( credentialBenefits.getDateOfRevocation());
            Assertions.assertNull( credentialBenefits.getRevocationReason());

            Assertions.assertEquals(holder, credentialBenefits.getCreditHolder());
            Assertions.assertEquals(holder.getDocumentNumber(), credentialBenefits.getCreditHolderDni());
            Assertions.assertEquals(holder.getFirstName(), credentialBenefits.getCreditHolderFirstName());
            Assertions.assertEquals(holder.getLastName(), credentialBenefits.getCreditHolderLastName());

            Assertions.assertEquals(holder, credentialBenefits.getBeneficiary());
            Assertions.assertEquals(holder.getDocumentNumber(), credentialBenefits.getBeneficiaryDni());
            Assertions.assertEquals(holder.getFirstName(), credentialBenefits.getBeneficiaryFirstName());
            Assertions.assertEquals(holder.getLastName(), credentialBenefits.getBeneficiaryLastName());

//TODO            protected LocalDateTime dateOfIssue;



        } catch (CredentialException e) {
            Assertions.fail(e.getMessage());
        }



    }

    @Test
    public void buildCredentialBenefitsForFamily() throws CredentialException {

        when(parameterConfigurationRepository.findByConfigurationName(ConfigurationCodes.ID_DIDI_ISSUER.getCode())).thenReturn(getParameterConfigurationDidiIssuerMock());
        Optional<CredentialState> StatePendingDidi = createCredentialStatePendingDidiMock();
        when(credentialStateService.getCredentialPendingDidiState()).thenReturn(StatePendingDidi.get());

        Loan loan = this.getMockLoan();

        Person holder = this.createPersonMock();
        holder.setDocumentNumber(1111111L);
        Person beneficiary = this.createPersonMock();
        beneficiary.setDocumentNumber(222222L);
        beneficiary.setFirstName("Beneficiary Name");
        beneficiary.setLastName("Beneficiary Last Name");

        try {
            CredentialBenefits credentialBenefits =  credentialBenefitsService.buildNewBenefitsCredential(holder,beneficiary, PersonTypesCodes.FAMILY);

            Assertions.assertEquals(PersonTypesCodes.FAMILY.getCode(), credentialBenefits.getBeneficiaryType());
            Assertions.assertEquals(CredentialCategoriesCodes.BENEFIT.getCode(), credentialBenefits.getCredentialCategory());
            Assertions.assertEquals(StatePendingDidi.get(), credentialBenefits.getCredentialState());
            Assertions.assertEquals(CredentialTypesCodes.CREDENTIAL_BENEFITS_FAMILY.getCode(), credentialBenefits.getCredentialDescription());

            Assertions.assertEquals(getParameterConfigurationDidiIssuerMock().get().getValue(), credentialBenefits.getIdDidiIssuer());
            Assertions.assertNull( credentialBenefits.getIdDidiReceptor());
            Assertions.assertNull( credentialBenefits.getIdDidiCredential());

            Assertions.assertNull( credentialBenefits.getIdHistorical());

            Assertions.assertNull( credentialBenefits.getDateOfRevocation());
            Assertions.assertNull( credentialBenefits.getRevocationReason());

            Assertions.assertEquals(holder, credentialBenefits.getCreditHolder());
            Assertions.assertEquals(holder.getDocumentNumber(), credentialBenefits.getCreditHolderDni());
            Assertions.assertEquals(holder.getFirstName(), credentialBenefits.getCreditHolderFirstName());
            Assertions.assertEquals(holder.getLastName(), credentialBenefits.getCreditHolderLastName());

            Assertions.assertEquals(beneficiary, credentialBenefits.getBeneficiary());
            Assertions.assertEquals(beneficiary.getDocumentNumber(), credentialBenefits.getBeneficiaryDni());
            Assertions.assertEquals(beneficiary.getFirstName(), credentialBenefits.getBeneficiaryFirstName());
            Assertions.assertEquals(beneficiary.getLastName(), credentialBenefits.getBeneficiaryLastName());

//TODO            protected LocalDateTime dateOfIssue;



        } catch (CredentialException e) {
            Assertions.fail(e.getMessage());
        }

    }

    //CREDENTIAL BENEFITS
    @Test(expected = CredentialException.class)
    public void createNewCrendentialBenefitsForHolderWhenNotExists_LoanToReviewOk() throws CredentialException {

        when(personService.findByDocumentNumber(anyLong())).thenReturn(Optional.empty());

        Loan loan = this.getMockLoan();

        credentialBenefitsService.createCredentialsBenefitsHolderForNewLoan(loan);

    }

    @Test
    public void createNewCrendentialBenefitsForHolderWhenIsNotOnDefaultAndCredentialNotExist() throws CredentialException {

        Loan loan = this.getMockLoan();
        Optional<Person> opHolder = this.getPersonMockWithDid();
        Person holder = opHolder.get();
        // CredentialBenefits credentialBenefitsSaved = new CredentialBenefits();
        //  credentialBenefitsSaved.setIdHistorical(1L);


        Optional<CredentialState>  StatePendingDidi = createCredentialStatePendingDidiMock();
        when(credentialStateService.getCredentialPendingDidiState()).thenReturn(StatePendingDidi.get());
        when(parameterConfigurationRepository.findByConfigurationName(ConfigurationCodes.ID_DIDI_ISSUER.getCode())).thenReturn(getParameterConfigurationDidiIssuerMock());
        when(personService.findByDocumentNumber(anyLong())).thenReturn(opHolder);
        when(credentialIdentityRepository.findDistinctBeneficiaryFamilyByHolder(any(Person.class))).thenReturn(null);
        when(credentialBenefitsRepository.findTopByCreditHolderDniAndBeneficiaryDniAndBeneficiaryTypeOrderByIdDesc(holder.getDocumentNumber(), holder.getDocumentNumber(),PersonTypesCodes.HOLDER.getCode())).thenReturn(Optional.empty());
        when(credentialBenefitsRepository.save(any(CredentialBenefits.class))).thenAnswer(new Answer<CredentialBenefits>() {
            @Override
            public CredentialBenefits answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return (CredentialBenefits) args[0];
            }
        });


        try {
            credentialBenefitsService.createCredentialsBenefitsHolderForNewLoan(loan);
        } catch (CredentialException e) {
            e.printStackTrace();
            Assertions.fail(e.getMessage());
        }
        verify(credentialBenefitsRepository, times(2)).save(credentialBenefitCaptor.capture());
        CredentialBenefits credentialBenefits = credentialBenefitCaptor.getValue();

        Assertions.assertEquals(holder, credentialBenefits.getCreditHolder());
        Assertions.assertEquals(holder.getDocumentNumber(), credentialBenefits.getCreditHolderDni());
        Assertions.assertEquals(holder.getFirstName(), credentialBenefits.getCreditHolderFirstName());
        Assertions.assertEquals(holder.getLastName(), credentialBenefits.getCreditHolderLastName());

        Assertions.assertEquals(holder, credentialBenefits.getBeneficiary());
        Assertions.assertEquals(holder.getDocumentNumber(), credentialBenefits.getBeneficiaryDni());
        Assertions.assertEquals(holder.getFirstName(), credentialBenefits.getBeneficiaryFirstName());
        Assertions.assertEquals(holder.getLastName(), credentialBenefits.getBeneficiaryLastName());

        Assertions.assertEquals(StatePendingDidi.get(), credentialBenefits.getCredentialState());

    }

    @Test
    public void createNewCrendentialBenefitsForHolderWhenIsNotOnDefaultAndCredentialExistsAndIsRevoked() throws CredentialException {

        Loan loan = this.getMockLoan();
        Optional<Person> opHolder = this.getPersonMockWithDid();
        Person holder = opHolder.get();
        Optional<CredentialState>  StateRevoke = createCredentialStateRevokeMock();
        CredentialBenefits credentialBenefitsSaved = new CredentialBenefits();
        credentialBenefitsSaved.setCredentialState(StateRevoke.get());
        credentialBenefitsSaved.setIdHistorical(99L);


        Optional<CredentialState>  StatePendingDidi = createCredentialStatePendingDidiMock();
        when(credentialStateService.getCredentialPendingDidiState()).thenReturn(StatePendingDidi.get());
        when(credentialStateService.getCredentialRevokeState()).thenReturn(StateRevoke);
        when(parameterConfigurationRepository.findByConfigurationName(ConfigurationCodes.ID_DIDI_ISSUER.getCode())).thenReturn(getParameterConfigurationDidiIssuerMock());
        when(personService.findByDocumentNumber(anyLong())).thenReturn(opHolder);
        when(credentialBenefitsRepository.findTopByCreditHolderDniAndBeneficiaryDniAndBeneficiaryTypeOrderByIdDesc(holder.getDocumentNumber(), holder.getDocumentNumber(), PersonTypesCodes.HOLDER.getCode())).thenReturn(Optional.of(credentialBenefitsSaved));
        when(credentialBenefitsRepository.save(any(CredentialBenefits.class))).thenAnswer(new Answer<CredentialBenefits>() {
            @Override
            public CredentialBenefits answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return (CredentialBenefits) args[0];
            }
        });


        try {
            credentialBenefitsService.createCredentialsBenefitsHolderForNewLoan(loan);
        } catch (CredentialException e) {
            e.printStackTrace();
            Assertions.fail(e.getMessage());
        }

        verify(credentialBenefitsRepository, times(1)).save(credentialBenefitCaptor.capture());
        CredentialBenefits credentialBenefits = credentialBenefitCaptor.getValue();

        Assertions.assertEquals(holder, credentialBenefits.getCreditHolder());
        Assertions.assertEquals(holder.getDocumentNumber(), credentialBenefits.getCreditHolderDni());
        Assertions.assertEquals(holder.getFirstName(), credentialBenefits.getCreditHolderFirstName());
        Assertions.assertEquals(holder.getLastName(), credentialBenefits.getCreditHolderLastName());

        Assertions.assertEquals(holder, credentialBenefits.getBeneficiary());
        Assertions.assertEquals(holder.getDocumentNumber(), credentialBenefits.getBeneficiaryDni());
        Assertions.assertEquals(holder.getFirstName(), credentialBenefits.getBeneficiaryFirstName());
        Assertions.assertEquals(holder.getLastName(), credentialBenefits.getBeneficiaryLastName());

        Assertions.assertEquals(StatePendingDidi.get(), credentialBenefits.getCredentialState());
        Assertions.assertEquals(credentialBenefitsSaved.getIdHistorical(), credentialBenefits.getIdHistorical());

        //Credential saved is not modified
        Assertions.assertEquals(99L, credentialBenefitsSaved.getIdHistorical());
        Assertions.assertEquals(StateRevoke.get(), credentialBenefitsSaved.getCredentialState());


    }

    @Test
    public void revokeCrendentialBenefitsForHolderWhenCredentialNotExists() throws CredentialException {

        Optional<Person> opHolder = this.getPersonMockWithDid();
        Person holder = opHolder.get();

        when(credentialBenefitsRepository.findTopByCreditHolderDniAndBeneficiaryDniAndBeneficiaryTypeOrderByIdDesc(holder.getDocumentNumber(), holder.getDocumentNumber(), PersonTypesCodes.HOLDER.getCode())).thenReturn(Optional.empty());


        try {
            credentialBenefitsService.revokeHolderCredentialsBenefitsForLoan(holder);
        } catch (CredentialException e) {
            e.printStackTrace();
            Assertions.fail(e.getMessage());
        }

        verify(credentialBenefitsRepository, times(0)).save(credentialBenefitCaptor.capture());
        verify(credentialStateService, times(0)).getCredentialRevokeState();
    }

    @Test
    public void revokeCrendentialBenefitsForFamiliyWhenCredentialIsAlreadyRevokeThenDoNothing() throws CredentialException {

        Optional<Person> opHolder = this.getPersonMockWithDid();
        Person holder = opHolder.get();
        Optional<CredentialState>  StateRevoke = createCredentialStateRevokeMock();

        holder.setDocumentNumber(11111111L);

        Optional<Person> opBeneficiary = this.getPersonMockWithDid();
        Person beneficiary = opBeneficiary.get();
        beneficiary.setDocumentNumber(22222222L);

        List<Person> family = new ArrayList<Person>();
        family.add(beneficiary);

        CredentialBenefits credentialBenefitsSaved = new CredentialBenefits();
        credentialBenefitsSaved.setCredentialState(StateRevoke.get());
        credentialBenefitsSaved.setIdHistorical(99L);

        when(credentialStateService.getCredentialRevokeState()).thenReturn(StateRevoke);
        when(personService.findFamilyForHolder(holder)).thenReturn(Optional.of(family));
        when(credentialBenefitsRepository.findTopByCreditHolderDniAndBeneficiaryDniAndBeneficiaryTypeOrderByIdDesc(holder.getDocumentNumber(), beneficiary.getDocumentNumber(), PersonTypesCodes.FAMILY.getCode())).thenReturn(Optional.of(credentialBenefitsSaved));


        try {
            credentialBenefitsService.revokeFamilyCredentialsBenefitsForLoan(holder);
        } catch (CredentialException e) {
            e.printStackTrace();
            Assertions.fail(e.getMessage());
        }

        verify(credentialBenefitsRepository, times(0)).save(credentialBenefitCaptor.capture());
        verify(credentialStateService, times(1)).getCredentialRevokeState();
        Assertions.assertEquals(StateRevoke.get(), credentialBenefitsSaved.getCredentialState());

    }

    @Test
    public void revokeCrendentialBenefitsForHolderWhenCredentialIsAlreadyRevoke() throws CredentialException {

        Optional<Person> opHolder = this.getPersonMockWithDid();
        Person holder = opHolder.get();
        Optional<CredentialState>  StateRevoke = createCredentialStateRevokeMock();


        CredentialBenefits credentialBenefitsSaved = new CredentialBenefits();
        credentialBenefitsSaved.setCredentialState(StateRevoke.get());
        credentialBenefitsSaved.setIdHistorical(99L);

        when(credentialStateService.getCredentialRevokeState()).thenReturn(StateRevoke);

        when(credentialBenefitsRepository.findTopByCreditHolderDniAndBeneficiaryDniAndBeneficiaryTypeOrderByIdDesc(holder.getDocumentNumber(), holder.getDocumentNumber(), PersonTypesCodes.HOLDER.getCode())).thenReturn(Optional.of(credentialBenefitsSaved));


        try {
            credentialBenefitsService.revokeHolderCredentialsBenefitsForLoan(holder);
        } catch (CredentialException e) {
            e.printStackTrace();
            Assertions.fail(e.getMessage());
        }

        verify(credentialBenefitsRepository, times(0)).save(credentialBenefitCaptor.capture());
        verify(credentialStateService, times(1)).getCredentialRevokeState();
        Assertions.assertEquals(StateRevoke.get(), credentialBenefitsSaved.getCredentialState());

    }

    @Test
    public void revokeCrendentialBenefitsForHolderWhenCredentialIsPendingDidiThenRevokeLocaly() throws CredentialException {

        Optional<Person> opHolder = this.getPersonMockWithDid();
        Person holder = opHolder.get();
        Optional<CredentialState>  StateRevoke = createCredentialStateRevokeMock();
        Optional<CredentialState>  StatePendingDidi = createCredentialStatePendingDidiMock();
        CredentialBenefits credentialBenefitsSaved = new CredentialBenefits();
        credentialBenefitsSaved.setCredentialState(StatePendingDidi.get());
        credentialBenefitsSaved.setIdHistorical(99L);

        RevocationReason reasonDefault = this.getRevocationReasonDefaultMock();


        when(credentialStateService.getCredentialRevokeState()).thenReturn(StateRevoke);
        when(credentialStateService.getCredentialPendingDidiState()).thenReturn(StatePendingDidi.get());
        when(credentialBenefitsRepository.findTopByCreditHolderDniAndBeneficiaryDniAndBeneficiaryTypeOrderByIdDesc(holder.getDocumentNumber(), holder.getDocumentNumber(), PersonTypesCodes.HOLDER.getCode())).thenReturn(Optional.of(credentialBenefitsSaved));
        when(revocationReasonRepository.findByReason(RevocationReasonsCodes.DEFAULT.getCode())).thenReturn(Optional.of(reasonDefault));
        when(credentialRepository.findById(any())).thenReturn(Optional.of(credentialBenefitsSaved));

        try {
            credentialBenefitsService.revokeHolderCredentialsBenefitsForLoan(holder);
        } catch (CredentialException e) {
            e.printStackTrace();
            Assertions.fail(e.getMessage());
        }

        verify(credentialRepository, times(1)).save(credentialBenefitCaptor.capture());
        verify(didiService, times(0)).didiDeleteCertificate(anyString());
        CredentialBenefits credentialBenefits = credentialBenefitCaptor.getValue();
        Assertions.assertEquals(StateRevoke.get(), credentialBenefits.getCredentialState());
    }


    @Test
    public void revokeCrendentialBenefitsForHolderWhenCredentialIsActiveThenRevokeComplete() throws CredentialException {

        Optional<Person> opHolder = this.getPersonMockWithDid();
        Person holder = opHolder.get();
        Optional<CredentialState>  StateRevoke = createCredentialStateRevokeMock();
        Optional<CredentialState>  StateActiveDidi = createCredentialStateActiveMock();
        CredentialBenefits credentialBenefitsSaved = new CredentialBenefits();
        credentialBenefitsSaved.setCredentialState(StateActiveDidi.get());
        credentialBenefitsSaved.setIdHistorical(99L);
        credentialBenefitsSaved.setIdDidiCredential("iddidicred");
        RevocationReason reasonDefault = this.getRevocationReasonDefaultMock();


        when(credentialStateService.getCredentialRevokeState()).thenReturn(StateRevoke);
        when(credentialStateService.getCredentialActiveState()).thenReturn(StateActiveDidi);
        when(credentialBenefitsRepository.findTopByCreditHolderDniAndBeneficiaryDniAndBeneficiaryTypeOrderByIdDesc(holder.getDocumentNumber(), holder.getDocumentNumber(), PersonTypesCodes.HOLDER.getCode())).thenReturn(Optional.of(credentialBenefitsSaved));
        when(revocationReasonRepository.findByReason(RevocationReasonsCodes.DEFAULT.getCode())).thenReturn(Optional.of(reasonDefault));
        when(credentialRepository.findById(any())).thenReturn(Optional.of(credentialBenefitsSaved));
        when(didiService.didiDeleteCertificate(anyString())).thenReturn(true);

        try {
            credentialBenefitsService.revokeHolderCredentialsBenefitsForLoan(holder);
        } catch (CredentialException e) {
            e.printStackTrace();
            Assertions.fail(e.getMessage());
        }

        verify(credentialRepository, times(1)).save(credentialBenefitCaptor.capture());
        verify(didiService, times(1)).didiDeleteCertificate(anyString());
        CredentialBenefits credentialBenefits = credentialBenefitCaptor.getValue();
        Assertions.assertEquals(StateRevoke.get(), credentialBenefits.getCredentialState());
    }

    /*
     * *                 If exists, is active and emmited, do revoke,
     * *                */
    @Test
    public void revokeCrendentialBenefitsForFamilyWhenHolderHasNoFamiliy() {

        Optional<Person> opHolder = this.getPersonMockWithDid();
        Person holder = opHolder.get();

        when(credentialBenefitsRepository.findTopByCreditHolderDniAndBeneficiaryDniAndBeneficiaryTypeOrderByIdDesc(holder.getDocumentNumber(), holder.getDocumentNumber(), PersonTypesCodes.HOLDER.getCode())).thenReturn(Optional.empty());


        try {
            credentialBenefitsService.revokeFamilyCredentialsBenefitsForLoan(holder);
        } catch (CredentialException e) {
            e.printStackTrace();
            Assertions.fail(e.getMessage());
        }

        verify(credentialBenefitsRepository, times(0)).save(credentialBenefitCaptor.capture());
    //    verify(credentialStateRepository, times(0)).findByStateName(anyString());
    }


    @Test
    public void revokeCrendentialBenefitsForFamilyWhenBeneficiaryDontHasCredential() {

        Optional<Person> opHolder = this.getPersonMockWithDid();
        Person holder = opHolder.get();
        holder.setDocumentNumber(11111111L);

        Optional<Person> opBeneficiary = this.getPersonMockWithDid();
        Person beneficiary = opBeneficiary.get();
        beneficiary.setDocumentNumber(22222222L);

        List<Person> family = new ArrayList<Person>();
        family.add(beneficiary);


        when(credentialBenefitsRepository.findTopByCreditHolderDniAndBeneficiaryDniAndBeneficiaryTypeOrderByIdDesc(holder.getDocumentNumber(), holder.getDocumentNumber(), PersonTypesCodes.HOLDER.getCode())).thenReturn(Optional.empty());
        when(credentialIdentityRepository.findDistinctBeneficiaryFamilyByHolder(holder)).thenReturn(family);

        try {
            credentialBenefitsService.revokeFamilyCredentialsBenefitsForLoan(holder);
        } catch (CredentialException e) {
            e.printStackTrace();
            Assertions.fail(e.getMessage());
        }

        verify(credentialBenefitsRepository, times(0)).save(credentialBenefitCaptor.capture());
       // verify(credentialStateRepository, times(0)).findByStateName(anyString());
    }



    @Test
    public void revokeCrendentialBenefitsForFamilyWhenCredentialIsPendingDidiThenRevokeOnlyLocaly() throws CredentialException {

        Optional<Person> opHolder = this.getPersonMockWithDid();
        Person holder = opHolder.get();
        Optional<Person> opBeneficiary = this.getPersonMockWithDid();
        Person beneficiary = opBeneficiary.get();
        beneficiary.setDocumentNumber(22222222L);
        List<Person> family = new ArrayList<Person>();
        family.add(beneficiary);

        Optional<CredentialState>  StateRevoke = createCredentialStateRevokeMock();
        Optional<CredentialState>  StatePendingDidi = createCredentialStatePendingDidiMock();
        CredentialBenefits credentialBenefitsSaved = new CredentialBenefits();
        credentialBenefitsSaved.setCredentialState(StatePendingDidi.get());
        credentialBenefitsSaved.setIdHistorical(99L);

        RevocationReason reasonDefault = this.getRevocationReasonDefaultMock();


        when(credentialStateService.getCredentialRevokeState()).thenReturn(StateRevoke);
        when(credentialStateService.getCredentialPendingDidiState()).thenReturn(StatePendingDidi.get());
        when(credentialBenefitsRepository.findTopByCreditHolderDniAndBeneficiaryDniAndBeneficiaryTypeOrderByIdDesc(holder.getDocumentNumber(), beneficiary.getDocumentNumber(), PersonTypesCodes.FAMILY.getCode())).thenReturn(Optional.of(credentialBenefitsSaved));
        when(revocationReasonRepository.findByReason(RevocationReasonsCodes.DEFAULT.getCode())).thenReturn(Optional.of(reasonDefault));
        when(credentialRepository.findById(any())).thenReturn(Optional.of(credentialBenefitsSaved));
        when(personService.findFamilyForHolder(holder)).thenReturn(Optional.of(family));

        try {
            credentialBenefitsService.revokeFamilyCredentialsBenefitsForLoan(holder);
        } catch (CredentialException e) {
            e.printStackTrace();
            Assertions.fail(e.getMessage());
        }

        verify(credentialRepository, times(1)).save(credentialBenefitCaptor.capture());
        verify(didiService, times(0)).didiDeleteCertificate(anyString());
        CredentialBenefits credentialBenefits = credentialBenefitCaptor.getValue();
        Assertions.assertEquals(StateRevoke.get(), credentialBenefits.getCredentialState());
    }


    @Test
    public void revokeCrendentialBenefitsForFamilyWhenCredentialIsActiveThenRevokeComplete() throws CredentialException {

        Optional<Person> opHolder = this.getPersonMockWithDid();
        Person holder = opHolder.get();
        holder.setDocumentNumber(11111111L);
        Optional<Person> opBeneficiary = this.getPersonMockWithDid();
        Person beneficiary = opBeneficiary.get();
        beneficiary.setDocumentNumber(22222222L);
        List<Person> family = new ArrayList<Person>();
        family.add(beneficiary);

        Optional<CredentialState>  StateRevoke = createCredentialStateRevokeMock();
        Optional<CredentialState>  StateActiveDidi = createCredentialStateActiveMock();
        CredentialBenefits credentialBenefitsSaved = new CredentialBenefits();
        credentialBenefitsSaved.setCredentialState(StateActiveDidi.get());
        credentialBenefitsSaved.setIdHistorical(99L);
        credentialBenefitsSaved.setIdDidiCredential("iddidicred");
        RevocationReason reasonDefault = this.getRevocationReasonDefaultMock();


        when(credentialStateService.getCredentialRevokeState()).thenReturn(StateRevoke);
        when(credentialStateService.getCredentialActiveState()).thenReturn(StateActiveDidi);
        when(credentialBenefitsRepository.findTopByCreditHolderDniAndBeneficiaryDniAndBeneficiaryTypeOrderByIdDesc(holder.getDocumentNumber(), beneficiary.getDocumentNumber(), PersonTypesCodes.FAMILY.getCode())).thenReturn(Optional.of(credentialBenefitsSaved));
        when(revocationReasonRepository.findByReason(RevocationReasonsCodes.DEFAULT.getCode())).thenReturn(Optional.of(reasonDefault));
        when(credentialRepository.findById(any())).thenReturn(Optional.of(credentialBenefitsSaved));
        when(didiService.didiDeleteCertificate(anyString())).thenReturn(true);
        when(personService.findFamilyForHolder(holder)).thenReturn(Optional.of(family));

        try {
            credentialBenefitsService.revokeFamilyCredentialsBenefitsForLoan(holder);
        } catch (CredentialException e) {
            e.printStackTrace();
            Assertions.fail(e.getMessage());
        }

        verify(credentialRepository, times(1)).save(credentialBenefitCaptor.capture());
        verify(didiService, times(1)).didiDeleteCertificate(anyString());
        CredentialBenefits credentialBenefits = credentialBenefitCaptor.getValue();
        Assertions.assertEquals(StateRevoke.get(), credentialBenefits.getCredentialState());
    }


    @Test
    public void createNewCrendentialBenefitsForFamilyWhenIsNotOnDefaultAndCredentialNotExist() throws CredentialException {

        Loan loan = this.getMockLoan();
        Optional<Person> opHolder = this.getPersonMockWithDid();
        Person holder = opHolder.get();
        // CredentialBenefits credentialBenefitsSaved = new CredentialBenefits();
        //  credentialBenefitsSaved.setIdHistorical(1L);
        Person kinsman =  getPersonFamiliyMock();
        List<Person> family = new ArrayList<Person>();
        family.add(kinsman);

        Optional<CredentialState>  StatePendingDidi = createCredentialStatePendingDidiMock();
        when(credentialStateService.getCredentialPendingDidiState()).thenReturn(StatePendingDidi.get());
        when(parameterConfigurationRepository.findByConfigurationName(ConfigurationCodes.ID_DIDI_ISSUER.getCode())).thenReturn(getParameterConfigurationDidiIssuerMock());
        when(personService.findByDocumentNumber(anyLong())).thenReturn(opHolder);
        when(credentialIdentityRepository.findDistinctBeneficiaryFamilyByHolder(any(Person.class))).thenReturn(null);
        when(credentialBenefitsRepository.findTopByCreditHolderDniAndBeneficiaryDniAndBeneficiaryTypeOrderByIdDesc(holder.getDocumentNumber(), kinsman.getDocumentNumber(),PersonTypesCodes.FAMILY.getCode())).thenReturn(Optional.empty());
        when(personService.findFamilyForHolder(holder)).thenReturn(Optional.of(family));
        when(credentialBenefitsRepository.save(any(CredentialBenefits.class))).thenAnswer(new Answer<CredentialBenefits>() {
            @Override
            public CredentialBenefits answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return (CredentialBenefits) args[0];
            }
        });


        try {
            credentialBenefitsService.createCredentialsBenefitsFamilyForNewLoan(loan);
        } catch (CredentialException e) {
            e.printStackTrace();
            Assertions.fail(e.getMessage());
        }
        verify(credentialBenefitsRepository, times(2)).save(credentialBenefitCaptor.capture());
        CredentialBenefits credentialBenefits = credentialBenefitCaptor.getValue();

        Assertions.assertEquals(holder, credentialBenefits.getCreditHolder());
        Assertions.assertEquals(holder.getDocumentNumber(), credentialBenefits.getCreditHolderDni());
        Assertions.assertEquals(holder.getFirstName(), credentialBenefits.getCreditHolderFirstName());
        Assertions.assertEquals(holder.getLastName(), credentialBenefits.getCreditHolderLastName());

        Assertions.assertEquals(kinsman, credentialBenefits.getBeneficiary());
        Assertions.assertEquals(kinsman.getDocumentNumber(), credentialBenefits.getBeneficiaryDni());
        Assertions.assertEquals(kinsman.getFirstName(), credentialBenefits.getBeneficiaryFirstName());
        Assertions.assertEquals(kinsman.getLastName(), credentialBenefits.getBeneficiaryLastName());

        Assertions.assertEquals(StatePendingDidi.get(), credentialBenefits.getCredentialState());

    }


    @Test(expected = CredentialException.class)
    public void createNewCrendentialBenefitsForFamilyWhenNotExists_LoanToReviewOk() throws CredentialException {

        when(personService.findByDocumentNumber(anyLong())).thenReturn(Optional.empty());

        Loan loan = this.getMockLoan();

        credentialBenefitsService.createCredentialsBenefitsFamilyForNewLoan(loan);

    }


    @Test
    public void createNewCrendentialBenefitsForFamilyWhenIsNotOnDefaultAndCredentialExistsAndIsRevoked() throws CredentialException {

        Loan loan = this.getMockLoan();
        Optional<Person> opHolder = this.getPersonMockWithDid();
        Person holder = opHolder.get();
        Optional<CredentialState>  StateRevoke = createCredentialStateRevokeMock();
        CredentialBenefits credentialBenefitsSaved = new CredentialBenefits();
        credentialBenefitsSaved.setCredentialState(StateRevoke.get());
        credentialBenefitsSaved.setIdHistorical(99L);
        Person kinsman =  getPersonFamiliyMock();
        List<Person> family = new ArrayList<Person>();
        family.add(kinsman);


        Optional<CredentialState>  StatePendingDidi = createCredentialStatePendingDidiMock();
        when(credentialStateService.getCredentialPendingDidiState()).thenReturn(StatePendingDidi.get());
        when(credentialStateService.getCredentialRevokeState()).thenReturn(StateRevoke);
        when(parameterConfigurationRepository.findByConfigurationName(ConfigurationCodes.ID_DIDI_ISSUER.getCode())).thenReturn(getParameterConfigurationDidiIssuerMock());
        when(personService.findByDocumentNumber(anyLong())).thenReturn(opHolder);
        when(credentialBenefitsRepository.findTopByCreditHolderDniAndBeneficiaryDniAndBeneficiaryTypeOrderByIdDesc(holder.getDocumentNumber(), kinsman.getDocumentNumber(), PersonTypesCodes.FAMILY.getCode())).thenReturn(Optional.of(credentialBenefitsSaved));
        when(personService.findFamilyForHolder(holder)).thenReturn(Optional.of(family));

        when(credentialBenefitsRepository.save(any(CredentialBenefits.class))).thenAnswer(new Answer<CredentialBenefits>() {
            @Override
            public CredentialBenefits answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return (CredentialBenefits) args[0];
            }
        });


        try {
            credentialBenefitsService.createCredentialsBenefitsFamilyForNewLoan(loan);
        } catch (CredentialException e) {
            e.printStackTrace();
            Assertions.fail(e.getMessage());
        }

        verify(credentialBenefitsRepository, times(1)).save(credentialBenefitCaptor.capture());
        CredentialBenefits credentialBenefits = credentialBenefitCaptor.getValue();

        Assertions.assertEquals(holder, credentialBenefits.getCreditHolder());
        Assertions.assertEquals(holder.getDocumentNumber(), credentialBenefits.getCreditHolderDni());
        Assertions.assertEquals(holder.getFirstName(), credentialBenefits.getCreditHolderFirstName());
        Assertions.assertEquals(holder.getLastName(), credentialBenefits.getCreditHolderLastName());

        Assertions.assertEquals(kinsman, credentialBenefits.getBeneficiary());
        Assertions.assertEquals(kinsman.getDocumentNumber(), credentialBenefits.getBeneficiaryDni());
        Assertions.assertEquals(kinsman.getFirstName(), credentialBenefits.getBeneficiaryFirstName());
        Assertions.assertEquals(kinsman.getLastName(), credentialBenefits.getBeneficiaryLastName());

        Assertions.assertEquals(StatePendingDidi.get(), credentialBenefits.getCredentialState());
        Assertions.assertEquals(credentialBenefitsSaved.getIdHistorical(), credentialBenefits.getIdHistorical());

        //Credential saved is not modified
        Assertions.assertEquals(99L, credentialBenefitsSaved.getIdHistorical());
        Assertions.assertEquals(StateRevoke.get(), credentialBenefitsSaved.getCredentialState());


    }

    @Test
    public void whenFinalizeLoanAndIsTheOnlyActiveForHolder_thenRevokeCredentialsBenefits() throws CredentialException {

        Loan loan = this.getMockLoan();
        Optional<Person> opHolder = this.getPersonMockWithDid();

        when(personService.findByDocumentNumber(anyLong())).thenReturn(opHolder);

        CredentialBenefitService credentialBenefitServiceSpy = Mockito.spy(this.credentialBenefitsService);
        credentialBenefitServiceSpy.handleLoanFinalized(loan,null);

        verify(credentialBenefitServiceSpy, times(1)).revokeHolderCredentialsBenefitsForLoan(opHolder.get());
        verify(credentialBenefitServiceSpy, times(1)).revokeFamilyCredentialsBenefitsForLoan(opHolder.get());

    }

    @Test
    public void whenFinalizeLoanAndIsNotTheOnlyActiveForHolder_thenDonothing() throws CredentialException {

        Loan loan = this.getMockLoan();
        Optional<Person> opHolder = this.getPersonMockWithDid();

        loan.setState(LoanStateCodes.DEFAULT.getCode());

        List<Loan> otherLoans = new ArrayList<Loan>();
        otherLoans.add(this.getMockLoan());

        when(personService.findByDocumentNumber(anyLong())).thenReturn(opHolder);

        CredentialBenefitService credentialBenefitServiceSpy = Mockito.spy(this.credentialBenefitsService);
        List<Loan> result = credentialBenefitServiceSpy.handleLoanFinalized(loan,otherLoans);

        verify(credentialBenefitServiceSpy, times(0)).revokeHolderCredentialsBenefitsForLoan(opHolder.get());
        verify(credentialBenefitServiceSpy, times(0)).revokeFamilyCredentialsBenefitsForLoan(opHolder.get());

        Assert.assertEquals(otherLoans,result);
    }

    @Test
    public void whenFinalizeLoanAndIsNotTheOnlyActiveForHolderAndIsOnDefault_thenReturnLoansToVerify() throws CredentialException {

        Loan loan = this.getMockLoan();
        Optional<Person> opHolder = this.getPersonMockWithDid();


        List<Loan> otherLoans = new ArrayList<Loan>();
        otherLoans.add(this.getMockLoan());

        when(personService.findByDocumentNumber(anyLong())).thenReturn(opHolder);

        CredentialBenefitService credentialBenefitServiceSpy = Mockito.spy(this.credentialBenefitsService);
        List<Loan> result = credentialBenefitServiceSpy.handleLoanFinalized(loan,otherLoans);

        verify(credentialBenefitServiceSpy, times(0)).revokeHolderCredentialsBenefitsForLoan(opHolder.get());
        verify(credentialBenefitServiceSpy, times(0)).revokeFamilyCredentialsBenefitsForLoan(opHolder.get());

        Assert.assertTrue(result.isEmpty());
    }

    private Optional<ParameterConfiguration> getParameterConfigurationDidiIssuerMock(){
        ParameterConfiguration parameterConfiguration = new ParameterConfiguration();
        parameterConfiguration.setConfigurationName(ConfigurationCodes.ID_DIDI_ISSUER.getCode());
        parameterConfiguration.setValue("1234567890");
        return Optional.of(parameterConfiguration);
    }

    private Optional<CredentialState> createCredentialStatePendingDidiMock() {
        CredentialState credentialState = new CredentialState();
        credentialState.setId(2L);
        credentialState.setStateName(CredentialStatesCodes.PENDING_DIDI.getCode());
        return Optional.of(credentialState);
    }

    private Loan getMockLoan() {
        Loan loan = new Loan();
        loan.setDniPerson(123456L);
        loan.setIdBondareaLoan("1a");
        loan.setIdGroup("group1");
        loan.setIdProductLoan("idProduction");
        loan.setUserId("userId");
        loan.setCycleDescription("Ciclo 1");
        loan.setStatus(LoanStatusCodes.ACTIVE.getCode());
        loan.setState(LoanStateCodes.OK.getCode());
        loan.setHasCredential(true);
        loan.setExpiredAmount(BigDecimal.valueOf(0));
        loan.setCreationDate(DateUtil.getLocalDateTimeNow().toLocalDate());
        return loan;
    }

    private Person createPersonMock() {
        Person person = new Person();
        person.setId(1L);
        person.setFirstName("PepeMock");
        person.setLastName("GrilloMock");
        person.setDocumentNumber(99999999L);
        return person;
    }

    private Optional<Person> getPersonMockWithDid() {
        Person person = new Person();
        person.setId(1L);
        person.setDocumentNumber((long) 123456);
        person.setFirstName("Pepito");
        //person.setDIDIsHisotoric(List.of(getDIDHistoricMock()));
        return Optional.of(person);
    }

    private Person getPersonFamiliyMock() {
        Person person = new Person();
        person.setId(2L);
        person.setDocumentNumber((long) 234567);
        person.setFirstName("Pepito hijo");
        //person.setDIDIsHisotoric(List.of(getDIDHistoricMock()));
        return person;
    }

    private Optional<CredentialState> createCredentialStateRevokeMock() {
        CredentialState credentialState = new CredentialState();
        credentialState.setId(3L);
        credentialState.setStateName(CredentialStatesCodes.CREDENTIAL_REVOKE.getCode());
        return Optional.of(credentialState);
    }

    private RevocationReason getRevocationReasonDefaultMock() {
        RevocationReason reason = new RevocationReason();
        reason.setId(1L);
        reason.setReason(RevocationReasonsCodes.DEFAULT.getCode());
        return reason;
    }

    private Optional<CredentialState> createCredentialStateActiveMock() {
        CredentialState credentialState = new CredentialState();
        credentialState.setId(1L);
        credentialState.setStateName(CredentialStatesCodes.CREDENTIAL_ACTIVE.getCode());
        return Optional.of(credentialState);
    }
}
