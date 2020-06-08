package com.atixlabs.semillasmiddleware.credentialService;

import com.atixlabs.semillasmiddleware.app.bondarea.model.Loan;
import com.atixlabs.semillasmiddleware.app.bondarea.model.constants.LoanStatusCodes;
import com.atixlabs.semillasmiddleware.app.bondarea.repository.LoanRepository;
import com.atixlabs.semillasmiddleware.app.model.DIDHistoric.DIDHisotoric;
import com.atixlabs.semillasmiddleware.app.model.beneficiary.Person;
import com.atixlabs.semillasmiddleware.app.model.configuration.ParameterConfiguration;
import com.atixlabs.semillasmiddleware.app.model.configuration.constants.ConfigurationCodes;
import com.atixlabs.semillasmiddleware.app.model.credential.Credential;
import com.atixlabs.semillasmiddleware.app.model.credential.CredentialBenefits;
import com.atixlabs.semillasmiddleware.app.model.credential.CredentialCredit;
import com.atixlabs.semillasmiddleware.app.model.credential.CredentialIdentity;
import com.atixlabs.semillasmiddleware.app.model.credential.constants.CredentialStatesCodes;
import com.atixlabs.semillasmiddleware.app.model.credential.constants.CredentialTypesCodes;
import com.atixlabs.semillasmiddleware.app.model.credential.constants.PersonTypesCodes;
import com.atixlabs.semillasmiddleware.app.model.credentialState.CredentialState;
import com.atixlabs.semillasmiddleware.app.model.credentialState.RevocationReason;
import com.atixlabs.semillasmiddleware.app.repository.*;
import com.atixlabs.semillasmiddleware.app.service.CredentialService;
import com.atixlabs.semillasmiddleware.excelparser.app.categories.AnswerCategoryFactory;
import com.atixlabs.semillasmiddleware.excelparser.app.dto.AnswerRow;
import com.atixlabs.semillasmiddleware.excelparser.app.dto.SurveyForm;
import com.atixlabs.semillasmiddleware.excelparser.dto.ProcessExcelFileResult;
import com.atixlabs.semillasmiddleware.excelparser.exception.InvalidRowException;
import com.atixlabs.semillasmiddleware.util.DateUtil;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.junit.Before;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.mockito.Mockito.*;


@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class CredentialServiceTest {

    @InjectMocks
    private AnswerCategoryFactory answerCategoryFactory;

    @InjectMocks
    private CredentialService credentialService;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private CredentialRepository credentialRepository;
    
    @Mock
    private CredentialStateRepository credentialStateRepository;
    
    @Mock
    private CredentialIdentityRepository credentialIdentityRepository;
    
    @Mock
    private CredentialDwellingRepository credentialDwellingRepository;

    @Mock
    private CredentialEntrepreneurshipRepository credentialEntrepreneurshipRepository;

    @Mock
    private CredentialCreditRepository credentialCreditRepository;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private DIDHistoricRepository didHistoricRepository;

    @Mock
    private CredentialBenefitsRepository credentialBenefitsRepository;

    @Mock
    private ParameterConfigurationRepository parameterConfigurationRepository;

    @Mock
    private RevocationReasonRepository revocationReasonRepository;

    @Captor
    private ArgumentCaptor<CredentialCredit> credentialCreditCaptor;

    @Captor
    private ArgumentCaptor<Credential> credentialCaptor;

    @Captor
    private ArgumentCaptor<CredentialBenefits> credentialBenefitCaptor;

    @Captor
    private ArgumentCaptor<Loan> loanCaptor;


    @Before
    public void setupMocks() {
        MockitoAnnotations.initMocks(this);
    }


    private Person getBeneficiaryMockWithoutDID() {
        Person person = new Person();
        person.setId(1L);
        person.setDocumentNumber(123456L);
        person.setFirstName("Pepito");
        return person;
    }

    private Loan getMockLoan(){
        Loan loan = new Loan();
        loan.setDniPerson(123456L);
        loan.setIdBondareaLoan("1a");
        loan.setIdGroup("group1");
        loan.setCycleDescription("Ciclo 1");
        loan.setStatus(LoanStatusCodes.ACTIVE.getCode());
        loan.setHasCredential(true);
        loan.setExpiredAmount((float) 0);
        loan.setCreationDate(DateUtil.getLocalDateTimeNow().toLocalDate());
        return loan;
    }

    private Loan getLoanWithExpiredAmount(){
        Loan loan = getMockLoan();
        loan.setExpiredAmount((float) 10000);

        return loan;
    }

    private Loan getLoanWithFinishState(){
        Loan loan = getMockLoan();
        loan.setStatus(LoanStatusCodes.FINALIZED.getCode());

        return loan;
    }

    private Optional<CredentialBenefits> getCredentialHolderBenefitMock(Person beneficiary){
        CredentialBenefits benefits = new CredentialBenefits();
        benefits.setId(1L);
        benefits.setBeneficiaryType(PersonTypesCodes.HOLDER.getCode());
        benefits.setCredentialDescription(CredentialTypesCodes.CREDENTIAL_BENEFITS.getCode());
        benefits.setCredentialState(new CredentialState(CredentialStatesCodes.CREDENTIAL_ACTIVE.getCode()));
        benefits.setDateOfIssue(DateUtil.getLocalDateTimeNow());
        benefits.setBeneficiary(beneficiary);
        benefits.setBeneficiaryDni(beneficiary.getDocumentNumber());
        benefits.setIdDidiCredential("1234L");
        benefits.setIdDidiReceptor("1234L");

        return  Optional.of(benefits);
    }

    private Optional<CredentialBenefits> getCredentialHolderBenefitRevokeMock(Person beneficiary){
        CredentialBenefits benefits = new CredentialBenefits();
        benefits.setId(1L);
        benefits.setBeneficiaryType(PersonTypesCodes.HOLDER.getCode());
        benefits.setCredentialDescription(CredentialTypesCodes.CREDENTIAL_BENEFITS.getCode());
        benefits.setCredentialState(new CredentialState(CredentialStatesCodes.CREDENTIAL_REVOKE.getCode()));
        benefits.setDateOfIssue(DateUtil.getLocalDateTimeNow());
        benefits.setBeneficiary(beneficiary);
        benefits.setBeneficiaryDni(beneficiary.getDocumentNumber());
        benefits.setIdDidiCredential("1234L");
        benefits.setIdDidiReceptor("1234L");

        return  Optional.of(benefits);
    }

    private Optional<ParameterConfiguration> getParamConfiguration(){
        ParameterConfiguration config = new ParameterConfiguration();
        config.setConfigurationName(ConfigurationCodes.MAX_EXPIRED_AMOUNT.getCode());
        config.setExpiredAmountMax((float) 100);

        return Optional.of(config);
    }


    private CredentialBenefits getPendingCredentialHolderBenefitMock(Person beneficiary){
        CredentialBenefits benefits = new CredentialBenefits();
        benefits.setId(1L);
        benefits.setBeneficiaryType(PersonTypesCodes.HOLDER.getCode());
        benefits.setCredentialDescription(CredentialTypesCodes.CREDENTIAL_BENEFITS.getCode());
        benefits.setCredentialState(new CredentialState(CredentialStatesCodes.PENDING_DIDI.getCode()));
        benefits.setDateOfIssue(DateUtil.getLocalDateTimeNow());
        benefits.setBeneficiary(beneficiary);
        benefits.setBeneficiaryDni(beneficiary.getDocumentNumber());
        benefits.setCreditHolderDni(beneficiary.getDocumentNumber());
        benefits.setBeneficiaryDni(beneficiary.getDocumentNumber());
        return  benefits;
    }

    private CredentialBenefits getPendingCredentialFamiliarBenefitMock(Person beneficiary){
        CredentialBenefits benefits = getPendingCredentialHolderBenefitMock(beneficiary);
        benefits.setId(1 + beneficiary.getId());
        benefits.setCredentialDescription(CredentialTypesCodes.CREDENTIAL_BENEFITS_FAMILY.getCode());
        benefits.setBeneficiaryDni(beneficiary.getDocumentNumber());

        return benefits;
    }

    private CredentialBenefits getRevokedCredentialHolderBenefitMock(Person beneficiary){
        CredentialBenefits benefits = new CredentialBenefits();
        benefits.setId(1L);
        benefits.setBeneficiaryType(PersonTypesCodes.HOLDER.getCode());
        benefits.setCredentialDescription(CredentialTypesCodes.CREDENTIAL_BENEFITS.getCode());
        benefits.setCredentialState(new CredentialState(CredentialStatesCodes.CREDENTIAL_REVOKE.getCode()));
        benefits.setDateOfIssue(DateUtil.getLocalDateTimeNow());
        benefits.setBeneficiary(beneficiary);
        benefits.setBeneficiaryDni(beneficiary.getDocumentNumber());
        return  benefits;
    }

    private List<Credential> credentialsMock() {
        List<Credential> credentials = new ArrayList<>();

        Person beneficiary = getBeneficiaryMockWithoutDID();


        CredentialCredit credential1 = new CredentialCredit();
        credential1.setId(1L);
        credential1.setIdDidiCredential("2L");
        credential1.setCredentialDescription(CredentialTypesCodes.CREDENTIAL_CREDIT.getCode());
        credential1.setDateOfIssue(LocalDateTime.now());
        credential1.setDateOfRevocation(LocalDateTime.now().plusDays(14));
        credential1.setBeneficiaryDni(29302594L);
        credential1.setCreditState("Estado");
        credential1.setCreditHolder(beneficiary);
        credential1.setCredentialState(new CredentialState(CredentialStatesCodes.CREDENTIAL_ACTIVE.getCode()));
        credentials.add(credential1);

        CredentialIdentity credentialIdentity = new CredentialIdentity();
        credentialIdentity.setId(2L);
        credentialIdentity.setCreditHolderDni(34534534L);
        credentialIdentity.setCredentialDescription(CredentialTypesCodes.CREDENTIAL_IDENTITY.getCode());
        credentialIdentity.setBeneficiaryFirstName("Pepito");
        credentialIdentity.setBeneficiaryFirstName("Pepito Apellido");
        credentialIdentity.setDateOfRevocation(DateUtil.getLocalDateTimeNow());
        credentialIdentity.setDateOfIssue(DateUtil.getLocalDateTimeNow().minusDays(14));
        credentialIdentity.setBeneficiary(beneficiary);
        credentialIdentity.setCredentialState(new CredentialState(CredentialStatesCodes.CREDENTIAL_ACTIVE.getCode()));
        credentials.add(credentialIdentity);

        CredentialIdentity credentialIdentity2 = new CredentialIdentity();
        credentialIdentity2.setId(3L);
        credentialIdentity2.setCreditHolderDni(34534534L);
        credentialIdentity2.setCredentialDescription(CredentialTypesCodes.CREDENTIAL_IDENTITY.getCode());
        credentialIdentity2.setBeneficiaryFirstName("Pepito");
        credentialIdentity2.setBeneficiaryLastName("Pepito Apellido");
        credentialIdentity2.setDateOfRevocation(DateUtil.getLocalDateTimeNow());
        credentialIdentity2.setDateOfIssue(DateUtil.getLocalDateTimeNow().minusDays(14));
        credentialIdentity2.setBeneficiary(beneficiary);
        credentialIdentity2.setCredentialState(new CredentialState(CredentialStatesCodes.CREDENTIAL_REVOKE.getCode()));
        credentials.add(credentialIdentity2);


        return credentials;
    }

    private CredentialCredit getActiveCreditMock(Loan loan, Person personMock){
        CredentialCredit credential1 = new CredentialCredit();
        credential1.setId(1L);
        credential1.setCredentialDescription(CredentialTypesCodes.CREDENTIAL_CREDIT.getCode());
        credential1.setCredentialState(new CredentialState(CredentialStatesCodes.CREDENTIAL_ACTIVE.getCode()));

        credential1.setIdBondareaCredit(loan.getIdBondareaLoan());
        credential1.setIdGroup(loan.getIdGroup());
        credential1.setCurrentCycle(loan.getCycleDescription());
        credential1.setAmountExpiredCycles(0);
        credential1.setCreditState(loan.getStatus());
        credential1.setExpiredAmount(loan.getExpiredAmount());
        credential1.setCreationDate(loan.getCreationDate());
        credential1.setBeneficiaryDni(personMock.getDocumentNumber());
        credential1.setDateOfIssue(DateUtil.getLocalDateTimeNow());
        credential1.setBeneficiary(personMock);


        credential1.setIdDidiCredential("1234L");
        credential1.setIdDidiReceptor("1234L");

        credential1.setIdHistorical(1L);

        return credential1;
    }

    private CredentialCredit getPendingCreditMock(Loan loan, Person personMock){
        CredentialCredit credential1 = new CredentialCredit();
        credential1.setId(1L);
        credential1.setCredentialDescription(CredentialTypesCodes.CREDENTIAL_CREDIT.getCode());
        credential1.setCredentialState(new CredentialState(CredentialStatesCodes.PENDING_DIDI.getCode()));

        credential1.setIdBondareaCredit(loan.getIdBondareaLoan());
        credential1.setIdGroup(loan.getIdGroup());
        credential1.setCurrentCycle(loan.getCycleDescription());
        credential1.setAmountExpiredCycles(0);
        credential1.setCreditState(loan.getStatus());
        credential1.setExpiredAmount(loan.getExpiredAmount());
        credential1.setCreationDate(loan.getCreationDate());
        credential1.setBeneficiaryDni(personMock.getDocumentNumber());
        credential1.setDateOfIssue(DateUtil.getLocalDateTimeNow());
        credential1.setBeneficiary(personMock);
        credential1.setCreditHolder(personMock);
        credential1.setCreditHolderDni(personMock.getDocumentNumber());

        credential1.setIdHistorical(1L);

        return credential1;
    }

    private List<CredentialCredit> getCreditGroupExpiredMock() {
        List<CredentialCredit> creditsGroup = new ArrayList<>();
        //credit with expired amount
        CredentialCredit creditExpired = getPendingCreditMock(getLoanWithExpiredAmount(), getBeneficiaryMockWithoutDID());
        creditsGroup.add(creditExpired);
        //normal credit
        CredentialCredit creditOk = getPendingCreditMock(getMockLoan(), getBeneficiaryMockWithoutDID());
        creditsGroup.add(creditOk);

        return creditsGroup;
    }

    private DIDHisotoric getDIDHistoricMock() {
        DIDHisotoric didi = new DIDHisotoric();
        didi.setId(1L);
        didi.setActive(true);
        didi.setIdDidiReceptor("1234L");
        didi.setIdPerson(1245L);
        return didi;
    }

    private Optional<Person> getPersonMockWithDid() {
        Person person = new Person();
        person.setId(1L);
        person.setDocumentNumber((long) 123456);
        person.setFirstName("Pepito");
        //person.setDIDIsHisotoric(List.of(getDIDHistoricMock()));
        return Optional.of(person);
    }

    private Optional<CredentialState> getCredentialActiveState(){
        CredentialState state = new CredentialState();
        state.setId(1L);
        state.setStateName(CredentialStatesCodes.CREDENTIAL_ACTIVE.getCode());
        return Optional.of(state);
    }

    private Optional<CredentialState> getCredentialPendingState(){
        CredentialState state = new CredentialState();
        state.setId(2L);
        state.setStateName(CredentialStatesCodes.PENDING_DIDI.getCode());
        return Optional.of(state);
    }

    private Optional<CredentialState> getCredentialRevokeState(){
        CredentialState state = new CredentialState();
        state.setId(3L);
        state.setStateName(CredentialStatesCodes.CREDENTIAL_REVOKE.getCode());
        return Optional.of(state);
    }

    private List<CredentialState> getStateActivePending(){
        return List.of(getCredentialActiveState().get(), getCredentialPendingState().get());
    }

    private List<Credential> credentialsFilteredActiveMock() {
        return credentialsMock().stream().filter(credential -> credential.getCredentialState().getStateName().equals(CredentialStatesCodes.CREDENTIAL_ACTIVE.getCode())).collect(Collectors.toList());
    }

    private List<Credential> credentialsFilteredRevokedMock() {
        return credentialsMock().stream().filter(credential -> credential.getCredentialState().getStateName().equals(CredentialStatesCodes.CREDENTIAL_REVOKE.getCode())).collect(Collectors.toList());
    }

    private RevocationReason getRevocationReasonMock(){
        RevocationReason reason = new RevocationReason();
        reason.setId(1L);
        reason.setReason("Razon de revocacion");
        return  reason;
    }



    private Row createRowMock(String category, String question, String answer){
        Workbook wb;
        Sheet sheet;

        wb = new HSSFWorkbook();
        sheet = wb.createSheet();
        Row row = sheet.createRow(8);
        System.out.println(row.getRowNum());
        row.setRowNum(8);
        Cell surveyCell = row.createCell(7);
        surveyCell.setCellValue("SURVEY-1");
        Cell dateCell = row.createCell(9);
        dateCell.setCellValue("12/12/2019");
        Cell pdvCell = row.createCell(10);
        pdvCell.setCellValue(5456580);
        Cell categoryCell = row.createCell(14);
        categoryCell.setCellValue(category);
        Cell questionCell = row.createCell(15);
        questionCell.setCellValue(question);
        Cell answerCell = row.createCell(16);
        answerCell.setCellValue(answer);
        return row;
    }

    private ArrayList<AnswerRow> createAnswerRowListMock() throws InvalidRowException {
        ArrayList<AnswerRow> answerRowArrayList = new ArrayList<>();
        answerRowArrayList.add(new AnswerRow(createRowMock("EMPRENDIMIENTO", "FECHA DE INICIO / REINICIO","03/04/2020")));
        answerRowArrayList.add(new AnswerRow(createRowMock("EMPRENDIMIENTO", "ACTIVIDAD PRINCIPAL","Comercio")));
        answerRowArrayList.add(new AnswerRow(createRowMock("EMPRENDIMIENTO", "DIRECCION","Direccion 123")));
        answerRowArrayList.add(new AnswerRow(createRowMock("EMPRENDIMIENTO", "FIN DE LA ACTIVIDAD","03/04/2020")));
        answerRowArrayList.add(new AnswerRow(createRowMock("EMPRENDIMIENTO", "NOMBRE EMPRENDIMIENTO","Panaderia pepe")));
        answerRowArrayList.add(new AnswerRow(createRowMock("EMPRENDIMIENTO", "TIPO DE EMPRENDIMIENTO","Producto")));

        answerRowArrayList.add(new AnswerRow(createRowMock("DATOS DEL BENEFICIARIO", "NOMBRE","Pedro")));
        answerRowArrayList.add(new AnswerRow(createRowMock("DATOS DEL BENEFICIARIO", "APELLIDO","Picapiedra")));
        answerRowArrayList.add(new AnswerRow(createRowMock("DATOS DEL BENEFICIARIO", "TIPO DE DOCUMENTO","Dni")));
        answerRowArrayList.add(new AnswerRow(createRowMock("DATOS DEL BENEFICIARIO", "NUMERO DE DOCUMENTO","30697455")));
        answerRowArrayList.add(new AnswerRow(createRowMock("DATOS DEL BENEFICIARIO", "GENERO","Masculino")));
        answerRowArrayList.add(new AnswerRow(createRowMock("DATOS DEL BENEFICIARIO", "FECHA DE NACIMIENTO","03/04/2020")));

        answerRowArrayList.add(new AnswerRow(createRowMock("VIVIENDA", "VIVIENDA","Casa")));
        answerRowArrayList.add(new AnswerRow(createRowMock("VIVIENDA", "TIPO DE TENENCIA","Picapiedra")));
        answerRowArrayList.add(new AnswerRow(createRowMock("VIVIENDA", "DISTRITO DE RESIDENCIA","Barrio 31")));

        return answerRowArrayList;
    }

    private SurveyForm createSurveyFormMock(ArrayList<AnswerRow> answerRowArrayList, ProcessExcelFileResult processExcelFileResult){

        SurveyForm surveyForm = new SurveyForm();
        surveyForm.setCategoryList(answerCategoryFactory.getCategoryList());
        surveyForm.setSurveyFormCode("TEST-CREATE-CREDENTIALS");
        surveyForm.setSurveyDate(LocalDate.now());
        surveyForm.setPdv(1L);

        for (AnswerRow answerRow : answerRowArrayList) {
            surveyForm.setCategoryData(answerRow, processExcelFileResult);
        }
        //surveyForm.isValid(processExcelFileResult);
        //log.info(surveyForm.toString());
        //log.info(processExcelFileResult.toString());
        return surveyForm;
    }

    private Optional<CredentialState> createCredentialStateActiveMock(){
        CredentialState credentialState = new CredentialState();
        credentialState.setId(1L);
        credentialState.setStateName(CredentialStatesCodes.CREDENTIAL_ACTIVE.getCode());
        return Optional.of(credentialState);
    }

    private Person createPersonMock(){
        Person person = new Person();
        person.setId(1L);
        person.setFirstName("PepeMock");
        person.setLastName("GrilloMock");
        person.setDocumentNumber(99999999L);
        return person;
    }

    @Test
    public void getActiveCredentials() {
        when(credentialRepository.findCredentialsWithFilter(null, null, null, null, null, null, Arrays.asList("Vigente"))).thenReturn((List<Credential>) credentialsFilteredActiveMock());

        List<Credential> credentials = credentialService.findCredentials(null, null, null, null, null, null, Arrays.asList("Vigente"));

        verify(credentialRepository).findCredentialsWithFilter(null, null, null, null, null, null, Arrays.asList("Vigente"));

        //List<CredentialDto> credentialsDto = credentials.stream().map(aCredential -> new CredentialDto(aCredential)).collect(Collectors.toList());
        log.info("credenciales " + credentials.toString());


        Assertions.assertTrue(credentials.size() == credentialsFilteredActiveMock().size()); // check if the amount of credentials filtered in the service is the correct one
        Assertions.assertEquals(credentialsFilteredActiveMock().get(0).getId(), credentials.get(0).getId());
        Assertions.assertEquals(credentialsFilteredActiveMock().get(0).getCredentialState().getStateName(), credentials.get(0).getCredentialState().getStateName());
        Assertions.assertEquals(credentialsFilteredActiveMock().get(0).getCreditHolder().getDocumentNumber() ,credentials.get(0).getCreditHolder().getDocumentNumber());
        Assertions.assertEquals(credentialsFilteredActiveMock().get(0).getIdDidiCredential() ,credentials.get(0).getIdDidiCredential());
        Assertions.assertTrue(credentials.get(0).getDateOfRevocation() != null);
        Assertions.assertTrue(credentials.get(0).getDateOfIssue() != null);
        Assertions.assertEquals(credentialsFilteredActiveMock().get(0).getCreditHolder().getFirstName() ,credentials.get(0).getCreditHolder().getFirstName());
    }


    @Test
    public void getRevokedCredentials() {
        when(credentialRepository.findCredentialsWithFilter(null, null, null, null, null, null, Arrays.asList("Revocada"))).thenReturn((List<Credential>) credentialsFilteredRevokedMock());

        List<Credential> credentials = credentialService.findCredentials(null, null, null, null, null, null, Arrays.asList("Revocada"));

        verify(credentialRepository).findCredentialsWithFilter(null, null, null, null, null, null, Arrays.asList("Revocada"));

        log.info("credenciales " + credentials.toString());


        Assertions.assertTrue(credentials.size() == credentialsFilteredRevokedMock().size()); // check if the amount of credentials filtered in the service is the correct one
        Assertions.assertEquals(credentialsFilteredRevokedMock().get(0).getId(), credentials.get(0).getId());
        Assertions.assertEquals(credentialsFilteredRevokedMock().get(0).getCredentialState().getStateName(), credentials.get(0).getCredentialState().getStateName());
        //Assertions.assertEquals(credentialsFilteredRevokedMock().get(0).getCreditHolder().getDocumentNumber() ,credentials.get(0).getCreditHolder().getDocumentNumber());
        Assertions.assertEquals(credentialsFilteredRevokedMock().get(0).getIdDidiCredential() ,credentials.get(0).getIdDidiCredential());
        Assertions.assertTrue(credentials.get(0).getDateOfRevocation() != null);
        Assertions.assertTrue(credentials.get(0).getDateOfIssue() != null);
        //Assertions.assertEquals(credentialsFilteredRevokedMock().get(0).getCreditHolder().getFirstName() ,credentials.get(0).getCreditHolder().getFirstName());
    }

    @Test
    public void buildAllCredentialsFromFormOK() throws InvalidRowException {
        log.info("buildAllCredentialsFromFormOK");
        ProcessExcelFileResult processExcelFileResult = new ProcessExcelFileResult();
        SurveyForm surveyForm = createSurveyFormMock(createAnswerRowListMock(), processExcelFileResult);

        when(credentialStateRepository.findByStateName(CredentialStatesCodes.CREDENTIAL_ACTIVE.getCode())).thenReturn(createCredentialStateActiveMock());
        when(personRepository.findByDocumentNumber(any(Long.class))).thenReturn(Optional.of(new Person()));
        when(personRepository.save(any(Person.class))).thenReturn(createPersonMock());

        credentialService.buildAllCredentialsFromForm(surveyForm, processExcelFileResult);

        Assertions.assertEquals(processExcelFileResult.getErrorRows().size(), 0);
    }

    @Test
    @Ignore
    public void buildAllCredentialsDetectDuplicatedCredential() throws InvalidRowException {
        log.info("buildAllCredentialsDetectDuplicatedCredential");
        ProcessExcelFileResult processExcelFileResult = new ProcessExcelFileResult();
        SurveyForm surveyForm = createSurveyFormMock(createAnswerRowListMock(), processExcelFileResult);

        when(credentialStateRepository.findByStateName(CredentialStatesCodes.CREDENTIAL_ACTIVE.getCode())).thenReturn(createCredentialStateActiveMock());
        when(personRepository.findByDocumentNumber(any(Long.class))).thenReturn(Optional.of(new Person()));
        when(personRepository.save(any(Person.class))).thenReturn(createPersonMock());

        when(credentialRepository.findByBeneficiaryDniAndCredentialCategoryAndCredentialStateIn(
                anyLong(),//beneficiaryDni,
                anyString(),//credentialCategoryCode,
                any(ArrayList.class)//credentialStateActive
        )).thenReturn(Optional.of(credentialsFilteredActiveMock().get(1)));

        log.info(credentialsFilteredActiveMock().get(1).toString());

        credentialService.buildAllCredentialsFromForm(surveyForm, processExcelFileResult);

        log.info(processExcelFileResult.toString());

        //todo corregir estos errores que surgieron al modificar los mocks
        //Assertions.assertEquals(processExcelFileResult.getErrorRows().size(), 3);
        //Assertions.assertEquals(processExcelFileResult.getErrorRows().get(0).getErrorHeader(), "Warning CREDENCIAL DUPLICADA");
    }


    @Test
    public void createCredentialCreditAndBenefitWithActiveDID() throws Exception {
        when(credentialCreditRepository.findByIdBondareaCredit(anyString())).thenReturn(Optional.empty());
        when(personRepository.findByDocumentNumber(anyLong())).thenReturn(getPersonMockWithDid());
        when(didHistoricRepository.findByIdPersonAndIsActive(anyLong(), anyBoolean())).thenReturn(Optional.of(getDIDHistoricMock()));
        when(credentialStateRepository.findByStateName(anyString())).thenReturn(getCredentialActiveState());
        when(credentialStateRepository.findByStateNameIn(anyList())).thenReturn(getStateActivePending());
        when(credentialCreditRepository.save(any(CredentialCredit.class))).thenReturn(getActiveCreditMock(getMockLoan(), getPersonMockWithDid().get()));
        //credential benefits
        when(credentialBenefitsRepository.save(any(CredentialBenefits.class))).thenReturn(getCredentialHolderBenefitMock(getPersonMockWithDid().get()).get());
        when(credentialBenefitsRepository.findByBeneficiaryDniAndCredentialStateInAndBeneficiaryType(anyLong(), anyList(), anyString())).thenReturn(Optional.empty());

        Loan loan = getMockLoan();
        credentialService.createNewCreditCredentials(loan);

        verify(credentialCreditRepository, times(2)).save(credentialCreditCaptor.capture()); //because the id historic
        verify(loanRepository, times(1)).save(loanCaptor.capture());
        verify(credentialBenefitsRepository, times(2)).save(credentialBenefitCaptor.capture()); //because the id historic

        Loan savedLoan = loanCaptor.getValue();
        CredentialCredit creditSaved = credentialCreditCaptor.getAllValues().get(1);
        CredentialBenefits credentialBenefits = credentialBenefitCaptor.getAllValues().get(1);

        log.info("credit created " + creditSaved.toString());
        log.info("beneficiary created " + credentialBenefits.toString());

        Assertions.assertEquals(true, savedLoan.getHasCredential());
        Assertions.assertEquals(loan.getIdBondareaLoan(), creditSaved.getIdBondareaCredit());
        Assertions.assertEquals(loan.getDniPerson(), creditSaved.getBeneficiaryDni());
        Assertions.assertEquals(CredentialStatesCodes.CREDENTIAL_ACTIVE.getCode(), creditSaved.getCredentialState().getStateName());
        Assertions.assertEquals(0, creditSaved.getAmountExpiredCycles());
        Assertions.assertEquals(loan.getCreationDate(), creditSaved.getCreationDate());
        Assertions.assertNotNull(creditSaved.getDateOfIssue());
        Assertions.assertEquals(getDIDHistoricMock().getIdDidiReceptor(), creditSaved.getIdDidiCredential());
        Assertions.assertEquals(getDIDHistoricMock().getIdDidiReceptor(), creditSaved.getIdDidiReceptor());
        Assertions.assertEquals(loan.getStatus(), creditSaved.getCreditState());
        Assertions.assertTrue(creditSaved.getIdHistorical() == creditSaved.getId());

        //benefit
        Assertions.assertEquals(PersonTypesCodes.HOLDER.getCode(), credentialBenefits.getBeneficiaryType());
        Assertions.assertEquals(CredentialTypesCodes.CREDENTIAL_BENEFITS.getCode(), credentialBenefits.getCredentialDescription());
        Assertions.assertEquals(getPersonMockWithDid().get().getDocumentNumber(), credentialBenefits.getBeneficiary().getDocumentNumber());
        Assertions.assertEquals(getDIDHistoricMock().getIdDidiReceptor(), credentialBenefits.getIdDidiCredential());
        Assertions.assertEquals(CredentialStatesCodes.CREDENTIAL_ACTIVE.getCode(), credentialBenefits.getCredentialState().getStateName());
        Assertions.assertEquals(getDIDHistoricMock().getIdDidiReceptor(), credentialBenefits.getIdDidiReceptor());
    }

    @Test
    public void createCredentialCreditAndBenefitWithPendingDIDIState() throws Exception {
        when(credentialCreditRepository.findByIdBondareaCredit(anyString())).thenReturn(Optional.empty());
        when(personRepository.findByDocumentNumber(anyLong())).thenReturn(Optional.of(getBeneficiaryMockWithoutDID()));
        when(didHistoricRepository.findByIdPersonAndIsActive(anyLong(), anyBoolean())).thenReturn(Optional.empty());
        when(credentialStateRepository.findByStateName(anyString())).thenReturn(getCredentialPendingState());
        when(credentialStateRepository.findByStateNameIn(anyList())).thenReturn(getStateActivePending());
        when(credentialCreditRepository.save(any(CredentialCredit.class))).thenReturn(getPendingCreditMock(getMockLoan(), getBeneficiaryMockWithoutDID()));
        //credential benefits
        when(credentialBenefitsRepository.save(any(CredentialBenefits.class))).thenReturn(getPendingCredentialHolderBenefitMock(getPersonMockWithDid().get()));
        when(credentialBenefitsRepository.findByBeneficiaryDniAndCredentialStateInAndBeneficiaryType(anyLong(), anyList(), anyString())).thenReturn(Optional.empty());

        Loan loan = getMockLoan();
        credentialService.createNewCreditCredentials(loan);

        verify(credentialCreditRepository, times(2)).save(credentialCreditCaptor.capture());
        verify(loanRepository, times(1)).save(loanCaptor.capture());
        verify(credentialBenefitsRepository, times(2)).save(credentialBenefitCaptor.capture());

        Loan savedLoan = loanCaptor.getValue();
        CredentialCredit creditSaved = credentialCreditCaptor.getAllValues().get(1);
        CredentialBenefits credentialBenefits = credentialBenefitCaptor.getAllValues().get(1);

        log.info("credit created " + creditSaved.toString());
        log.info("beneficiary created " + credentialBenefits.toString());

        Assertions.assertEquals(true, savedLoan.getHasCredential());
        Assertions.assertEquals(loan.getIdBondareaLoan(), creditSaved.getIdBondareaCredit());
        Assertions.assertEquals(loan.getDniPerson(), creditSaved.getBeneficiaryDni());
        Assertions.assertEquals(CredentialStatesCodes.PENDING_DIDI.getCode(), creditSaved.getCredentialState().getStateName());
        Assertions.assertEquals(0, creditSaved.getAmountExpiredCycles());
        Assertions.assertEquals(loan.getCreationDate(), creditSaved.getCreationDate());
        Assertions.assertNotNull(creditSaved.getDateOfIssue());
        Assertions.assertEquals(null, creditSaved.getIdDidiCredential());
        Assertions.assertEquals(null, creditSaved.getIdDidiReceptor());
        Assertions.assertEquals(loan.getStatus(), creditSaved.getCreditState());

        //benefit
        Assertions.assertEquals(PersonTypesCodes.HOLDER.getCode(), credentialBenefits.getBeneficiaryType());
        Assertions.assertEquals(CredentialTypesCodes.CREDENTIAL_BENEFITS.getCode(), credentialBenefits.getCredentialDescription());
        Assertions.assertEquals(getBeneficiaryMockWithoutDID().getDocumentNumber(), credentialBenefits.getBeneficiaryDni());
        Assertions.assertEquals(getBeneficiaryMockWithoutDID().getDocumentNumber(), credentialBenefits.getBeneficiaryDni());
        Assertions.assertEquals(null, credentialBenefits.getIdDidiCredential());
        Assertions.assertEquals(null, credentialBenefits.getIdDidiReceptor());
        Assertions.assertEquals(CredentialStatesCodes.PENDING_DIDI.getCode(), credentialBenefits.getCredentialState().getStateName());
    }

    @Test
    public void createCredentialCreditAndHavingBenefitsCredential() throws Exception {
        when(credentialCreditRepository.findByIdBondareaCredit(anyString())).thenReturn(Optional.empty());
        when(personRepository.findByDocumentNumber(anyLong())).thenReturn(getPersonMockWithDid());
        when(didHistoricRepository.findByIdPersonAndIsActive(anyLong(), anyBoolean())).thenReturn(Optional.of(getDIDHistoricMock()));
        when(credentialStateRepository.findByStateName(anyString())).thenReturn(getCredentialActiveState());
        when(credentialStateRepository.findByStateNameIn(anyList())).thenReturn(getStateActivePending());
        when(credentialCreditRepository.save(any(CredentialCredit.class))).thenReturn(getActiveCreditMock(getMockLoan(), getPersonMockWithDid().get()));

        //credential benefits
        when(credentialBenefitsRepository.findByBeneficiaryDniAndCredentialStateInAndBeneficiaryType(anyLong(), anyList(), anyString())).thenReturn(getCredentialHolderBenefitMock(getPersonMockWithDid().get()));

        Loan loan = getMockLoan();
        credentialService.createNewCreditCredentials(loan);

        verify(credentialCreditRepository, times(2)).save(credentialCreditCaptor.capture());
        verify(loanRepository, times(1)).save(loanCaptor.capture());
        verify(credentialBenefitsRepository, times(0)).save(credentialBenefitCaptor.capture());

        Loan savedLoan = loanCaptor.getValue();
        CredentialCredit creditSaved = credentialCreditCaptor.getValue();

        log.info("credit created " + creditSaved.toString());

        Assertions.assertEquals(true, savedLoan.getHasCredential());
        Assertions.assertEquals(loan.getIdBondareaLoan(), creditSaved.getIdBondareaCredit());
        Assertions.assertEquals(loan.getDniPerson(), creditSaved.getBeneficiaryDni());
        Assertions.assertEquals(CredentialStatesCodes.CREDENTIAL_ACTIVE.getCode(), creditSaved.getCredentialState().getStateName());
        Assertions.assertEquals(0, creditSaved.getAmountExpiredCycles());
        Assertions.assertEquals(loan.getCreationDate(), creditSaved.getCreationDate());
        Assertions.assertNotNull(creditSaved.getDateOfIssue());
        Assertions.assertEquals(getDIDHistoricMock().getIdDidiReceptor(), creditSaved.getIdDidiCredential());
        Assertions.assertEquals(getDIDHistoricMock().getIdDidiReceptor(), creditSaved.getIdDidiReceptor());
        Assertions.assertEquals(loan.getStatus(), creditSaved.getCreditState());
    }

    @Test
    public void tryToCreateCredentialCreditButHasAlready() throws Exception {
        when(credentialCreditRepository.findByIdBondareaCredit(anyString())).thenReturn(Optional.of(getActiveCreditMock(getMockLoan(), getPersonMockWithDid().get())));
        when(personRepository.findByDocumentNumber(anyLong())).thenReturn(getPersonMockWithDid());

        Loan loan = getMockLoan();
        credentialService.createNewCreditCredentials(loan);

        verify(credentialCreditRepository, times(0)).save(credentialCreditCaptor.capture());
        verify(loanRepository, times(1)).save(loanCaptor.capture());
        verify(credentialBenefitsRepository, times(0)).save(credentialBenefitCaptor.capture());

        Loan savedLoan = loanCaptor.getValue();

        Assertions.assertEquals(true, savedLoan.getHasCredential());
    }


    @Test
    public void validateCredentialCreditToBeUpdateLoanHasChanged() {
        when(credentialCreditRepository.findFirstByIdBondareaCreditOrderByDateOfIssueDesc(anyString())).thenReturn(Optional.of(getPendingCreditMock(getMockLoan(),getBeneficiaryMockWithoutDID())));

        //loan has a different expired amount comparing with credential credit
        Loan loan = getLoanWithExpiredAmount();
        CredentialCredit creditToBeUpdate = credentialService.validateCredentialCreditToUpdate(loan);

        Assertions.assertNotNull(creditToBeUpdate);
    }

    @Test
    public void validateCredentialCreditToBeUpdateLoanIsTheSame() {
        when(credentialCreditRepository.findFirstByIdBondareaCreditOrderByDateOfIssueDesc(anyString())).thenReturn(Optional.of(getPendingCreditMock(getMockLoan(),getBeneficiaryMockWithoutDID())));

        //loan has not changed
        Loan loan = getMockLoan();
        CredentialCredit creditToBeUpdate = credentialService.validateCredentialCreditToUpdate(loan);

        Assertions.assertNull(creditToBeUpdate);
    }

    @Test
    public void validateCredentialCreditButThereIsNOCredentialCreditCreated() {
        when(credentialCreditRepository.findFirstByIdBondareaCreditOrderByDateOfIssueDesc(anyString())).thenReturn(Optional.empty());

        //loan has not changed
        Loan loan = getMockLoan();
        CredentialCredit creditToBeUpdate = credentialService.validateCredentialCreditToUpdate(loan);

        verify(loanRepository, times(1)).save(loanCaptor.capture());
        Loan loanUpdated = loanCaptor.getValue();

        Assertions.assertNull(creditToBeUpdate);
        Assertions.assertTrue(loanUpdated.getHasCredential() != getMockLoan().getHasCredential()); // the loan is set to not having credential
    }

    @Test
    public void updateCredentialCreditOK() throws Exception {
        CredentialCredit creditCreated = getPendingCreditMock(getMockLoan(), getBeneficiaryMockWithoutDID());

        when(credentialStateRepository.findByStateName(CredentialStatesCodes.CREDENTIAL_REVOKE.getCode())).thenReturn(getCredentialRevokeState());

        when(credentialCreditRepository.findByIdBondareaCredit(anyString())).thenReturn(Optional.empty());
        when(personRepository.findByDocumentNumber(anyLong())).thenReturn(getPersonMockWithDid());
        when(didHistoricRepository.findByIdPersonAndIsActive(anyLong(), anyBoolean())).thenReturn(Optional.empty());
        when(credentialStateRepository.findByStateName(CredentialStatesCodes.PENDING_DIDI.getCode())).thenReturn(getCredentialPendingState());
        when(credentialStateRepository.findByStateNameIn(anyList())).thenReturn(getStateActivePending());
        when(credentialCreditRepository.findByIdGroupAndCredentialStateIn(anyString(), anyList())).thenReturn(List.of(creditCreated));
        when(parameterConfigurationRepository.findByConfigurationName(anyString())).thenReturn(getParamConfiguration());
        when(credentialCreditRepository.save(any(CredentialCredit.class))).thenReturn(getPendingCreditMock(getMockLoan(), getBeneficiaryMockWithoutDID()));

        //credential benefits
        when(credentialBenefitsRepository.findByBeneficiaryDniAndCredentialStateInAndBeneficiaryType(anyLong(), anyList(), anyString())).thenReturn(getCredentialHolderBenefitMock(getBeneficiaryMockWithoutDID()));

        //revoke
        when(credentialRepository.findById(anyLong())).thenReturn(Optional.of(getPendingCreditMock(getMockLoan(),getBeneficiaryMockWithoutDID())));
        when(revocationReasonRepository.findByReason(anyString())).thenReturn(Optional.of(getRevocationReasonMock()));


        credentialService.updateCredentialCredit(getMockLoan(), creditCreated);

        verify(credentialCreditRepository, times(1)).save(credentialCreditCaptor.capture());
        verify(loanRepository, times(0)).save(loanCaptor.capture());
        verify(credentialBenefitsRepository, times(0)).save(credentialBenefitCaptor.capture());
        verify(credentialRepository, times(1)).save(credentialCaptor.capture());

        CredentialCredit creditUpdate = credentialCreditCaptor.getAllValues().get(0);
        Credential creditOld = credentialCaptor.getAllValues().get(0);

        log.info("credit updated " + creditUpdate.toString());

        Assertions.assertEquals(getMockLoan().getIdBondareaLoan(), creditUpdate.getIdBondareaCredit());
        Assertions.assertEquals(getMockLoan().getDniPerson(), creditUpdate.getBeneficiaryDni());
        Assertions.assertNotNull(creditUpdate.getCredentialState().getStateName());
        Assertions.assertEquals(0, creditUpdate.getAmountExpiredCycles());
        Assertions.assertEquals(getMockLoan().getCreationDate(), creditUpdate.getCreationDate());
        Assertions.assertNotNull(creditUpdate.getDateOfIssue());
        Assertions.assertEquals(creditCreated.getIdHistorical(), creditUpdate.getIdHistorical());
        Assertions.assertEquals(creditCreated.getAmountExpiredCycles(), creditUpdate.getAmountExpiredCycles());
        Assertions.assertEquals(creditCreated.getExpiredAmount(), creditUpdate.getExpiredAmount());
        Assertions.assertEquals(getMockLoan().getStatus(), creditUpdate.getCreditState());
        Assertions.assertEquals(CredentialStatesCodes.CREDENTIAL_REVOKE.getCode(), creditOld.getCredentialState().getStateName());
        Assertions.assertEquals(getRevocationReasonMock().getReason(), creditOld.getRevocationReason().getReason());
    }

    @Test
    public void updateCredentialCreditFinishLoan() throws Exception {
        CredentialCredit creditCreated = getPendingCreditMock(getLoanWithFinishState(), getBeneficiaryMockWithoutDID());
        CredentialBenefits benefits = getPendingCredentialHolderBenefitMock(getBeneficiaryMockWithoutDID());
        CredentialBenefits benefitFamiliar1 = getPendingCredentialFamiliarBenefitMock(getBeneficiaryMockWithoutDID());
        CredentialBenefits benefitFamiliar2 = getPendingCredentialFamiliarBenefitMock(getBeneficiaryMockWithoutDID());

        when(credentialStateRepository.findByStateName(CredentialStatesCodes.CREDENTIAL_REVOKE.getCode())).thenReturn(getCredentialRevokeState());

        when(credentialCreditRepository.findByIdBondareaCredit(anyString())).thenReturn(Optional.empty());
        when(personRepository.findByDocumentNumber(anyLong())).thenReturn(getPersonMockWithDid());
        when(didHistoricRepository.findByIdPersonAndIsActive(anyLong(), anyBoolean())).thenReturn(Optional.empty());
        when(credentialStateRepository.findByStateName(CredentialStatesCodes.PENDING_DIDI.getCode())).thenReturn(getCredentialPendingState());
        when(credentialStateRepository.findByStateNameIn(anyList())).thenReturn(getStateActivePending());
        when(credentialCreditRepository.findByIdGroupAndCredentialStateIn(anyString(), anyList())).thenReturn(List.of(creditCreated));
        when(parameterConfigurationRepository.findByConfigurationName(anyString())).thenReturn(getParamConfiguration());
        when(credentialCreditRepository.save(any(CredentialCredit.class))).thenReturn(getPendingCreditMock(getMockLoan(), getBeneficiaryMockWithoutDID()));

        //revoke process
        when(credentialBenefitsRepository.findByBeneficiaryDniAndCredentialStateInAndBeneficiaryType(anyLong(), anyList(), anyString())).thenReturn(Optional.of(benefits));
        when(credentialCreditRepository.findByCreditHolderDniAndCredentialStateIn(anyLong(),anyList())).thenReturn(Collections.emptyList()); // the holder dont have another credit
        when(credentialRepository.findById(anyLong())).thenReturn(Optional.of(getPendingCredentialHolderBenefitMock(getBeneficiaryMockWithoutDID())));
       //todo here is returning the same object, and when the first is revoked the second too
        when(credentialBenefitsRepository.findByCreditHolderDniAndCredentialStateInAndBeneficiaryType(anyLong(), anyList(), anyString())).thenReturn(List.of(benefitFamiliar1,benefitFamiliar2));
        when(revocationReasonRepository.findByReason(anyString())).thenReturn(Optional.of(getRevocationReasonMock()));

        Loan loan = getLoanWithFinishState();
        credentialService.updateCredentialCredit(loan, creditCreated);

        verify(credentialCreditRepository, times(2)).save(credentialCreditCaptor.capture());
        verify(loanRepository, times(0)).save(loanCaptor.capture());
        verify(credentialBenefitsRepository, times(0)).save(credentialBenefitCaptor.capture());
        //TODO must be 4
        verify(credentialRepository, times(2)).save(credentialCaptor.capture()); //1 for update credit and 2 because benefits holder and familiar have revoked

        //update process
        Credential creditOld = credentialCaptor.getAllValues().get(0);
        CredentialCredit creditUpdate = credentialCreditCaptor.getAllValues().get(1);

        //revoke process -> this is because the group we set have only 2 holders
        Credential credentialBenefitHolderRevoked = credentialCaptor.getAllValues().get(1);
        //Credential credentialBenefitsFamiliarRevoked = credentialCaptor.getAllValues().get(2);
        //Credential credentialBenefitsFamiliar2Revoked = credentialCaptor.getAllValues().get(3);

        log.info("credit updated " + creditUpdate.toString());

        Assertions.assertNotNull(creditUpdate.getFinishDate());
        Assertions.assertEquals(loan.getIdBondareaLoan(), creditUpdate.getIdBondareaCredit());
        Assertions.assertEquals(loan.getDniPerson(), creditUpdate.getBeneficiaryDni());
        Assertions.assertNotNull(creditUpdate.getCredentialState().getStateName());
        Assertions.assertEquals(0, creditUpdate.getAmountExpiredCycles());
        Assertions.assertEquals(loan.getCreationDate(), creditUpdate.getCreationDate());
        Assertions.assertNotNull(creditUpdate.getDateOfIssue());
        Assertions.assertEquals(creditOld.getIdHistorical(), creditUpdate.getIdHistorical());
        Assertions.assertEquals(creditCreated.getAmountExpiredCycles(), creditUpdate.getAmountExpiredCycles());
        Assertions.assertEquals(loan.getExpiredAmount(), creditUpdate.getExpiredAmount());
        //Assertions.assertEquals(loan.getStatus(), creditUpdate.getCreditState());
        Assertions.assertEquals(CredentialStatesCodes.CREDENTIAL_REVOKE.getCode(), creditOld.getCredentialState().getStateName());
        Assertions.assertEquals(getRevocationReasonMock().getReason(), creditOld.getRevocationReason().getReason());

        //revoke
        Assertions.assertNotNull(credentialBenefitHolderRevoked.getDateOfRevocation());
        Assertions.assertEquals(CredentialTypesCodes.CREDENTIAL_BENEFITS.getCode(),credentialBenefitHolderRevoked.getCredentialDescription());
        Assertions.assertEquals(CredentialStatesCodes.CREDENTIAL_REVOKE.getCode(), credentialBenefitHolderRevoked.getCredentialState().getStateName());
        Assertions.assertEquals(getRevocationReasonMock().getReason(), credentialBenefitHolderRevoked.getRevocationReason().getReason());
    }


    @Test
    public void updateCredentialCreditInDefault() throws Exception {
        CredentialCredit creditCreated = getPendingCreditMock(getMockLoan(), getBeneficiaryMockWithoutDID());
        CredentialBenefits benefits = getPendingCredentialHolderBenefitMock(getBeneficiaryMockWithoutDID());

        when(credentialStateRepository.findByStateName(CredentialStatesCodes.CREDENTIAL_REVOKE.getCode())).thenReturn(getCredentialRevokeState());

        when(credentialCreditRepository.findByIdBondareaCredit(anyString())).thenReturn(Optional.empty());
        when(personRepository.findByDocumentNumber(anyLong())).thenReturn(getPersonMockWithDid());
        when(didHistoricRepository.findByIdPersonAndIsActive(anyLong(), anyBoolean())).thenReturn(Optional.empty());
        when(credentialStateRepository.findByStateName(CredentialStatesCodes.PENDING_DIDI.getCode())).thenReturn(getCredentialPendingState());
        when(credentialStateRepository.findByStateNameIn(anyList())).thenReturn(getStateActivePending());
        when(credentialCreditRepository.findByIdGroupAndCredentialStateIn(anyString(), anyList())).thenReturn(getCreditGroupExpiredMock());
        when(parameterConfigurationRepository.findByConfigurationName(anyString())).thenReturn(getParamConfiguration());
        when(credentialCreditRepository.save(any(CredentialCredit.class))).thenReturn(getPendingCreditMock(getLoanWithExpiredAmount(), getBeneficiaryMockWithoutDID()));

        // revocation process
        when(credentialRepository.findById(anyLong())).thenReturn(Optional.of(getPendingCreditMock(getMockLoan(), getBeneficiaryMockWithoutDID())));
        when(credentialCreditRepository.findById(anyLong())).thenReturn(Optional.of(creditCreated));
        when(credentialBenefitsRepository.findByCreditHolderDniAndCredentialStateIn(anyLong(), anyList())).thenReturn(List.of(benefits)); //solamente tiene la suya de titular
        when(revocationReasonRepository.findByReason(anyString())).thenReturn(Optional.of(getRevocationReasonMock()));

        Loan loan = getLoanWithExpiredAmount();
        credentialService.updateCredentialCredit(loan, creditCreated);

        verify(credentialCreditRepository, times(2)).save(credentialCreditCaptor.capture());
        verify(loanRepository, times(0)).save(loanCaptor.capture());
        verify(credentialBenefitsRepository, times(0)).save(credentialBenefitCaptor.capture());
        verify(credentialRepository, times(5)).save(credentialCaptor.capture()); //1 for update credit and 4 because credits group is in defeault

        //update process
        Credential creditOld = credentialCaptor.getAllValues().get(0); //credit saved after revoke
        CredentialCredit creditUpdate = credentialCreditCaptor.getAllValues().get(1);

        //revoke process -> this is because the group we set have only 2 holders
        Credential credentialCreditRevoked = credentialCaptor.getAllValues().get(1);
        Credential credentialBenefitsRevoked = credentialCaptor.getAllValues().get(2);
        Credential credentialCreditRevoked2 = credentialCaptor.getAllValues().get(3);
        Credential credentialBenefitsRevoked2 = credentialCaptor.getAllValues().get(4);


        log.info("credit updated " + creditUpdate.toString());

        Assertions.assertEquals(loan.getIdBondareaLoan(), creditUpdate.getIdBondareaCredit());
        Assertions.assertEquals(loan.getDniPerson(), creditUpdate.getBeneficiaryDni());
        Assertions.assertNotNull(creditUpdate.getCredentialState().getStateName());
        Assertions.assertEquals(creditCreated.getAmountExpiredCycles() +1 , creditUpdate.getAmountExpiredCycles());
        Assertions.assertEquals(loan.getCreationDate(), creditUpdate.getCreationDate());
        Assertions.assertNotNull(creditUpdate.getDateOfIssue());
        Assertions.assertEquals(creditOld.getIdHistorical(), creditUpdate.getIdHistorical());
        Assertions.assertEquals(loan.getExpiredAmount(), creditUpdate.getExpiredAmount());
        Assertions.assertEquals(loan.getStatus(), creditUpdate.getCreditState());
        Assertions.assertEquals(CredentialStatesCodes.CREDENTIAL_REVOKE.getCode(), creditOld.getCredentialState().getStateName());
        Assertions.assertEquals(getRevocationReasonMock().getReason(), creditOld.getRevocationReason().getReason());

        //revocation asserts
        Assertions.assertNotNull(credentialCreditRevoked.getDateOfRevocation());
        Assertions.assertEquals(CredentialTypesCodes.CREDENTIAL_CREDIT.getCode(),credentialCreditRevoked.getCredentialDescription());
        Assertions.assertEquals(CredentialStatesCodes.CREDENTIAL_REVOKE.getCode(), credentialCreditRevoked.getCredentialState().getStateName());
        Assertions.assertEquals(getRevocationReasonMock().getReason(), credentialCreditRevoked.getRevocationReason().getReason());


        Assertions.assertNotNull(credentialBenefitsRevoked.getDateOfRevocation());
        Assertions.assertEquals(CredentialTypesCodes.CREDENTIAL_BENEFITS.getCode(),credentialBenefitsRevoked.getCredentialDescription());
        Assertions.assertEquals(CredentialStatesCodes.CREDENTIAL_REVOKE.getCode(), credentialBenefitsRevoked.getCredentialState().getStateName());
        Assertions.assertEquals(getRevocationReasonMock().getReason(), credentialBenefitsRevoked.getRevocationReason().getReason());

        Assertions.assertNotNull(credentialCreditRevoked2.getDateOfRevocation());
        Assertions.assertEquals(CredentialTypesCodes.CREDENTIAL_CREDIT.getCode(),credentialCreditRevoked2.getCredentialDescription());
        Assertions.assertEquals(CredentialStatesCodes.CREDENTIAL_REVOKE.getCode(), credentialCreditRevoked2.getCredentialState().getStateName());
        Assertions.assertEquals(getRevocationReasonMock().getReason(), credentialCreditRevoked2.getRevocationReason().getReason());

        Assertions.assertNotNull(credentialBenefitsRevoked2.getDateOfRevocation());
        Assertions.assertEquals(CredentialTypesCodes.CREDENTIAL_BENEFITS.getCode(),credentialBenefitsRevoked2.getCredentialDescription());
        Assertions.assertEquals(CredentialStatesCodes.CREDENTIAL_REVOKE.getCode(), credentialBenefitsRevoked2.getCredentialState().getStateName());
        Assertions.assertEquals(getRevocationReasonMock().getReason(), credentialBenefitsRevoked2.getRevocationReason().getReason());

    }
    
    


}
