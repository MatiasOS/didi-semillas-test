package com.atixlabs.semillasmiddleware.app.bondarea.repository;

import com.atixlabs.semillasmiddleware.app.bondarea.model.Loan;
import com.atixlabs.semillasmiddleware.app.model.credential.Credential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findAllByPending(boolean pending);

    List<Loan> findAllByModifiedTimeNotAndModifiedTimeNotNull(LocalDateTime updateTime);

    Optional<Loan> findByIdBondareaLoan(String idBocs);
}
