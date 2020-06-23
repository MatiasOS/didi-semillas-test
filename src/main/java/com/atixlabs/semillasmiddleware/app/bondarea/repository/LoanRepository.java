package com.atixlabs.semillasmiddleware.app.bondarea.repository;

import com.atixlabs.semillasmiddleware.app.bondarea.model.Loan;
import com.atixlabs.semillasmiddleware.app.bondarea.model.constants.LoanStatusCodes;
import com.atixlabs.semillasmiddleware.app.model.credential.Credential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findAllByStatus(String status);


    List<Loan> findAllByUpdateTimeAndStatus(LocalDateTime updateTime, String status);

    //get all loans that his updateTime > :dateToCompare AND status = :status (dateToCompare for ex. last time process run)
    List<Loan> findAllByUpdateTimeGreaterThanAndStatusIn(LocalDateTime dateToCompare, List<String> statuses);

    List<Loan> findAllByUpdateTimeGreaterThanAndStatusAndState(LocalDateTime dateToCompare, String status, String state);

    @Modifying()
    @Transactional
    @Query("UPDATE Loan set status = :status " + "WHERE synchroTime < :synchroTime AND status = :activeState")
    int updateStateBySynchroTimeLessThanAndActive(@Param("synchroTime") LocalDateTime synchroTime, @Param("status") String status, @Param("activeState") String activeState);

    Optional<Loan> findByIdBondareaLoan(String idBocs);

    List<Loan> findAllByHasCredentialFalse();

    List<Loan> findAllByIdGroup(String idGroup);

}
