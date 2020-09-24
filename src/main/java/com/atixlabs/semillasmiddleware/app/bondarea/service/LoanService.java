package com.atixlabs.semillasmiddleware.app.bondarea.service;

import com.atixlabs.semillasmiddleware.app.bondarea.model.Loan;
import com.atixlabs.semillasmiddleware.app.bondarea.model.constants.LoanStateCodes;
import com.atixlabs.semillasmiddleware.app.bondarea.model.constants.LoanStatusCodes;
import com.atixlabs.semillasmiddleware.app.bondarea.repository.LoanRepository;
import com.atixlabs.semillasmiddleware.app.processControl.exception.InvalidProcessException;
import com.atixlabs.semillasmiddleware.app.processControl.model.constant.ProcessNamesCodes;
import com.atixlabs.semillasmiddleware.app.processControl.service.ProcessControlService;
import com.atixlabs.semillasmiddleware.app.repository.PersonRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class LoanService {


    private LoanRepository loanRepository;

    @Autowired
    public LoanService(LoanRepository loanRepository){
        this.loanRepository = loanRepository;
    }


    public List<Loan> findActiveAndOkLoansWithoutCredential(){
        List<Loan> newLoans = loanRepository.findAllByHasCredentialAndStatusAndState(false, LoanStatusCodes.ACTIVE.getCode(), LoanStateCodes.OK.getCode());

        return newLoans;
    }


    public List<Loan> findLastLoansModified(LocalDateTime credentialProcessLastTime, List<String> statuses) {
        //get the modified credits: the credits that modified in the last sync and (if exits) the credits that had been modified before that last sync.
        List<Loan> modifiedCredits = loanRepository.findAllByUpdateTimeGreaterThanAndStatusIn(credentialProcessLastTime, statuses);
        return modifiedCredits;
    }

    public List<Loan> findLastLoansModified(LocalDateTime credentialProcessLastTime, String status) {
        //get the modified credits: the credits that modified in the last sync and (if exits) the credits that had been modified before that last sync.
        List<Loan> modifiedCredits = loanRepository.findAllByUpdateTimeGreaterThanAndStatus(credentialProcessLastTime, status);
        return modifiedCredits;
    }

    public List<Loan> findLastLoansModifiedInDefault(LocalDateTime credentialProcessLastTime) {
        //get the modified credits: the credits that modified in the last sync and (if exits) the credits that had been modified before that last sync.
        return loanRepository.findAllByUpdateTimeGreaterThanAndStatusAndState(credentialProcessLastTime, LoanStatusCodes.ACTIVE.getCode(), LoanStateCodes.DEFAULT.getCode());
    }

    public List<Loan> findLastLoansModifiedActive(LocalDateTime credentialProcessLastTime) {
        //get the modified credits: the credits that modified in the last sync and (if exits) the credits that had been modified before that last sync.
        List<Loan> defaultCredits = loanRepository.findAllByUpdateTimeGreaterThanAndStatusAndState(credentialProcessLastTime, LoanStatusCodes.ACTIVE.getCode(), LoanStateCodes.OK.getCode());
        return defaultCredits;
    }

    public List<Loan> findLastLoansModifiedActiveWithCredential(LocalDateTime credentialProcessLastTime) {
        //get the modified credits: the credits that modified in the last sync and (if exits) the credits that had been modified before that last sync.
        List<Loan> defaultCredits = loanRepository.findAllByUpdateTimeGreaterThanAndStatusAndStateAndHasCredential(credentialProcessLastTime, LoanStatusCodes.ACTIVE.getCode(), LoanStateCodes.OK.getCode(), true);
        return defaultCredits;
    }

    public List<Loan> findLastLoansModifiedFinalized(LocalDateTime credentialProcessLastTime) {
        //get the modified credits: the credits that modified in the last sync and (if exits) the credits that had been modified before that last sync.
        List<Loan> finalizedCredits = loanRepository.findAllByUpdateTimeGreaterThanAndStatusAndState(credentialProcessLastTime, LoanStatusCodes.FINALIZED.getCode(), LoanStateCodes.OK.getCode());
        return finalizedCredits;
    }

    public List<Loan> findLastLoansModifiedCancelled(LocalDateTime credentialProcessLastTime) {
        //get the modified credits: the credits that modified in the last sync and (if exits) the credits that had been modified before that last sync.
        List<Loan> finalizedCredits = loanRepository.findAllByUpdateTimeGreaterThanAndStatusAndState(credentialProcessLastTime, LoanStatusCodes.CANCELLED.getCode(), LoanStateCodes.OK.getCode());
        return finalizedCredits;
    }

    public List<Loan> findOthersLoansActivesForHolder(Loan loan){
        return loanRepository.findAllByDniPersonAndStatusAndIdBondareaLoanNot(loan.getDniPerson(), LoanStatusCodes.ACTIVE.getCode(),loan.getIdBondareaLoan());
    }

}
