package com.atixlabs.semillasmiddleware.bondareaService;


import com.atixlabs.semillasmiddleware.app.bondarea.dto.BondareaLoanDto;
import com.atixlabs.semillasmiddleware.app.bondarea.model.Loan;
import com.atixlabs.semillasmiddleware.app.bondarea.model.constants.LoanStatusCodes;
import com.atixlabs.semillasmiddleware.app.bondarea.repository.LoanRepository;
import com.atixlabs.semillasmiddleware.app.bondarea.service.BondareaService;
import com.atixlabs.semillasmiddleware.app.processControl.exception.InvalidProcessException;
import com.atixlabs.semillasmiddleware.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class BondareaServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private BondareaService bondareaService;

    @Captor
    private ArgumentCaptor<Loan> captor;


    @Before
    public void setupMocks(){
        MockitoAnnotations.initMocks(this);
    }

    public BondareaLoanDto getMockBondareaLoan(){
        BondareaLoanDto loan = new BondareaLoanDto();
        loan.setDni(123456L);
        loan.setIdBondareaLoan("1a");
        //loan.setStatus(LoanStatusCodes.ACTIVE.getCode());
        loan.setExpiredAmount(BigDecimal.valueOf(0));
        loan.setCreationDate(DateUtil.getLocalDateTimeNow().toLocalDate().toString());

        return loan;
    }

    public Loan getMockLoan(){
        Loan loan = new Loan();
        loan.setDniPerson(123456L);
        loan.setIdBondareaLoan("1a");
        //loan.setStatus(LoanStatusCodes.ACTIVE.getCode());
        loan.setExpiredAmount(BigDecimal.valueOf(0));
        loan.setCreationDate(DateUtil.getLocalDateTimeNow().toLocalDate());

        return loan;
    }

    private  List<BondareaLoanDto> firstBondareaLoansData(){
        List<BondareaLoanDto> loans = new ArrayList<>();

        BondareaLoanDto loan = getMockBondareaLoan();
        loans.add(loan);

        BondareaLoanDto loan2 = getMockBondareaLoan();
        loan2.setIdBondareaLoan("2a");
        loans.add(loan2);

        BondareaLoanDto loan3 = getMockBondareaLoan();
        loan3.setIdBondareaLoan("3a");
        loans.add(loan3);

        BondareaLoanDto loan4 = getMockBondareaLoan();
        loan4.setIdBondareaLoan("4a");
        loans.add(loan4);

        return loans;
    }

    private  List<Loan> firstLoansData(){
        List<Loan> loans = new ArrayList<>();

        Loan loan = getMockLoan();
        loans.add(loan);

        Loan loan2 = getMockLoan();
        loan2.setIdBondareaLoan("2a");
        loans.add(loan2);

        Loan loan3 = getMockLoan();
        loan3.setIdBondareaLoan("3a");
        loans.add(loan3);

        Loan loan4 = getMockLoan();
        loan4.setIdBondareaLoan("4a");
        loans.add(loan4);

        return loans;
    }

    private  List<BondareaLoanDto> secondLoansData(){
        List<BondareaLoanDto> loans = new ArrayList<>();

        //id 1 is deleted

        //loan 2 is the same
        BondareaLoanDto loan2 = getMockBondareaLoan();
        loan2.setIdBondareaLoan("2a");
        loans.add(loan2);

        //loan 3 modified tag
        BondareaLoanDto loan3 = getMockBondareaLoan();
        loan3.setIdBondareaLoan("3a");
        loan3.setTagBondareaLoan("nuevo tag");
        loans.add(loan3);

        //loan 4 is in default
        BondareaLoanDto loan4 = getMockBondareaLoan();
        loan4.setIdBondareaLoan("4a");
        loan4.setExpiredAmount(BigDecimal.valueOf(100));
        loans.add(loan4);

        //new loan
        BondareaLoanDto loan5 = getMockBondareaLoan();
        loan5.setIdBondareaLoan("5a");
        loans.add(loan5);

        return loans;
    }

    private  List<BondareaLoanDto> secondLoansDataAllNew(){
        List<BondareaLoanDto> loans = new ArrayList<>();

        //all new loans
        BondareaLoanDto loan2 = getMockBondareaLoan();
        loan2.setIdBondareaLoan("5a");
        loans.add(loan2);

        BondareaLoanDto loan3 = getMockBondareaLoan();
        loan3.setIdBondareaLoan("6a");
        loans.add(loan3);

        BondareaLoanDto loan4 = getMockBondareaLoan();
        loan4.setIdBondareaLoan("7a");
        loans.add(loan4);


        return loans;
    }

    /*private  List<Loan> secondLoansDataAllExpired(){
        List<Loan> loans = new ArrayList<>();

        Loan loan = getMockBondareaLoan();
        loan.setExpiredAmount((float) 100);
        loans.add(loan);

        Loan loan2 = getMockBondareaLoan();
        loan2.setIdBondareaLoan("2a");
        loan2.setExpiredAmount((float) 100);
        loans.add(loan2);

        Loan loan3 = getMockBondareaLoan();
        loan3.setIdBondareaLoan("3a");
        loan3.setExpiredAmount((float) 100);
        loans.add(loan3);

        Loan loan4 = getMockBondareaLoan();
        loan4.setIdBondareaLoan("4a");
        loan4.setExpiredAmount((float) 100);
        loans.add(loan4);

        return loans;
    }*/



    @Test
    public void saveNewDataOnLoan() throws InvalidProcessException {
        when(loanRepository.findAll()).thenReturn(Collections.emptyList());

        List<BondareaLoanDto> loans = firstBondareaLoansData();
        LocalDateTime processStart = DateUtil.getLocalDateTimeNow();
        bondareaService.createAndUpdateLoans(loans, processStart);

        verify(loanRepository,times(4)).save(captor.capture());

        List<Loan> loansSaves = captor.getAllValues();
        Loan firstLoan = loansSaves.get(0);

        Assertions.assertEquals(firstBondareaLoansData().get(0).getIdBondareaLoan(), firstLoan.getIdBondareaLoan());
        Assertions.assertTrue(firstLoan.getDniPerson() != null);

    }

    /**
     * This test will test that: the service takes an existing loans data from database compare with the new loans data and merge it.
     * Mix of all the different conditions
     */
    @Test
    public void updateAllLoans() throws InvalidProcessException {
        when(loanRepository.findByIdBondareaLoan("2a")).thenReturn(Optional.of(firstLoansData().get(1)));
        when(loanRepository.findByIdBondareaLoan("3a")).thenReturn(Optional.of(firstLoansData().get(2)));
        when(loanRepository.findByIdBondareaLoan("4a")).thenReturn(Optional.of(firstLoansData().get(3)));
        when(loanRepository.findByIdBondareaLoan("5a")).thenReturn(Optional.empty());

        when(loanRepository.updateStateBySynchroTimeLessThanAndActive(any(), LoanStatusCodes.PENDING.getCode(), LoanStatusCodes.ACTIVE.getCode())).thenReturn(0);

        List<BondareaLoanDto> loans = secondLoansData();
        LocalDateTime processStart = DateUtil.getLocalDateTimeNow();
        bondareaService.createAndUpdateLoans(loans, processStart);

        verify(loanRepository,times(5)).save(captor.capture());

        List<Loan> loansSaves = captor.getAllValues();

        Assertions.assertTrue(loansSaves.size() > firstBondareaLoansData().size());
        Assertions.assertEquals(LoanStatusCodes.PENDING.getCode(), loansSaves.get(4).getStatus());
        Assertions.assertFalse(loansSaves.get(1).getTagBondareaLoan().equals(firstBondareaLoansData().get(2).getTagBondareaLoan()));

        List<Loan> pendingLoans = loansSaves.stream().filter(loan -> loan.getStatus().equals(LoanStatusCodes.PENDING.getCode())).collect(Collectors.toList());
        Assertions.assertEquals(1, pendingLoans.size() );

    }

    @Test
    public void updateLoansOldToPending() throws InvalidProcessException {
        when(loanRepository.findByIdBondareaLoan("5a")).thenReturn(Optional.empty());
        when(loanRepository.findByIdBondareaLoan("6a")).thenReturn(Optional.empty());
        when(loanRepository.findByIdBondareaLoan("7a")).thenReturn(Optional.empty());

        when(loanRepository.updateStateBySynchroTimeLessThanAndActive(any(), LoanStatusCodes.PENDING.getCode(), LoanStatusCodes.ACTIVE.getCode())).thenReturn(0);

        List<BondareaLoanDto> loans = secondLoansDataAllNew();
        LocalDateTime processStart = DateUtil.getLocalDateTimeNow();
        bondareaService.createAndUpdateLoans(loans, processStart);

        verify(loanRepository,times(7)).save(captor.capture());

        List<Loan> loansSaves = captor.getAllValues();

        Assertions.assertTrue(loansSaves.size() > firstBondareaLoansData().size());

        //the active loans will be all. But some are pending -> 3
        List<Loan> activeLoans = loansSaves.stream().filter(loan -> loan.getStatus().equals(LoanStatusCodes.ACTIVE.getCode())).collect(Collectors.toList());
        Assertions.assertEquals(3, activeLoans.size() );

        // the pending loans will be the older loans in this case -> 4
        List<Loan> pendingLoans = loansSaves.stream().filter(loan -> loan.getStatus().equals(LoanStatusCodes.PENDING.getCode())).collect(Collectors.toList());
        Assertions.assertEquals(4, pendingLoans.size() );

    }

    @Test
    public void determinatePendingLoanToFinish(){
      //  when(loanRepository.findAllByStatus(LoanStatusCodes.PENDING.getCode())).thenReturn(pendientes);
        //when(bondareaService.getLoans(BondareaLoanStatusCodes.FINALIZED.getCode(), anyString(),"")).thenReturn(vuelve 1 l)
    //TODO


    }

    /*@Test
    public void updateLoansAllExpired() {
        when(loanRepository.findAll()).thenReturn(firstBondareaLoansData());

        List<Loan> loans = secondLoansDataAllExpired();
        bondareaService.createAndUpdateLoans(loans);

        verify(loanRepository,times(4)).save(captor.capture());

        List<Loan> loansSaves = captor.getAllValues();


        //the active loans will be any.
        List<Loan> activeLoans = loansSaves.stream().filter(loan -> loan.getIsActive() == true).collect(Collectors.toList());
        Assertions.assertEquals(0, activeLoans.size() );

        // the pending loans will be any.
        List<Loan> pendingLoans = loansSaves.stream().filter(loan -> loan.getPending() == true).collect(Collectors.toList());
        Assertions.assertEquals(0, pendingLoans.size() );

    }
    */

}
