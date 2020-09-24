package com.atixlabs.semillasmiddleware.app.bondarea.model;

import com.atixlabs.semillasmiddleware.app.bondarea.dto.BondareaLoanDto;
import com.atixlabs.semillasmiddleware.app.bondarea.model.constants.BondareaLoanStatusCodes;
import com.atixlabs.semillasmiddleware.app.bondarea.model.constants.LoanStateCodes;
import com.atixlabs.semillasmiddleware.app.bondarea.model.constants.LoanStatusCodes;
import com.atixlabs.semillasmiddleware.security.model.AuditableEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@Entity
@Table
@ToString
@Slf4j
public class Loan extends AuditableEntity {


    @PrePersist
    private void preSetValues(){
        if(this.hasCredential == null)
            this.hasCredential = false;
    }


    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;

    private Long dniPerson;

    private String idBondareaLoan; // ID del crédito individual. Para créditosgrupales representa el tramo del crédito grupal   (Ej. B26F5FKZ)

    private String tagBondareaLoan; // Nombre del producto de préstamo (Ej. Recurrentes) //TODO credit type

    private String status;

    private String state; //ok OR default

    private String idProductLoan;  //ID de producto de préstamo (Ej.  B26F5FKZ)

    private String idGroup; // ID del   crédito grupal y su estado (Ej.55-B26F5FKZ)

    private String cycleDescription; // Nombre asignado al crédito (Ej. Ciclo 2)

    private LocalDate creationDate; // Fecha de otorgamiento cuentas FIX

    private String personName; // Nombre del solicitante del tramo (Ej. Perez, Juan)

    private String userId; // ID del solicitante del tramo (Ej. B26F5FKZ)

    private BigDecimal amount; // Monto del crédito del tramo (Ej. 10000)

    private LocalDate dateFirstInstalment; // Fecha de primera cuota FIX

    private Integer currentInstalmentNumber;

    private Integer InstalmentTotalQuantity;

    private String InstalmentType;

    @Column(scale = 2)
    private BigDecimal expiredAmount; // Saldo vencido del crédito individual, compuesto por capital, intereses, seguros y cargos (Ej. 1845.24)

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime synchroTime;

    private Boolean hasCredential;



    public Loan(BondareaLoanDto loanDto, Integer currentFeeNumber) {
        this.dniPerson = loanDto.getDni();

        this.idBondareaLoan = loanDto.getIdBondareaLoan();

        this.tagBondareaLoan = loanDto.getTagBondareaLoan();

        //Bondarea has only 2 states, FINALIZED or ACTIVE, If state is not FINALIZED SET ACTIVE COMO DEFAULT
        if(String.valueOf(loanDto.getStatus()).equals(BondareaLoanStatusCodes.FINALIZED.getCode()))
            this.status = LoanStatusCodes.FINALIZED.getCode();
        else
            this.status = LoanStatusCodes.ACTIVE.getCode();

        //start in state ok
        this.setState(LoanStateCodes.OK.getCode());

        this.idProductLoan =   loanDto.getIdProductLoan();

        this.idGroup =   loanDto.getIdGroup();

        this.cycleDescription =   loanDto.getCycle();

        this.personName =   loanDto.getPersonName();

        this.userId =   loanDto.getUserId();

        this.amount =  loanDto.getAmount();

        if(loanDto.getExpiredAmount() == null)
            this.expiredAmount = BigDecimal.ZERO;
        else
            this.expiredAmount =   loanDto.getExpiredAmount();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        try {
            if (loanDto.getDateFirstInstalment() != null) {
                this.dateFirstInstalment = LocalDate.parse(loanDto.getDateFirstInstalment(), formatter);
            }

            if (loanDto.getCreationDate() != null) {
                this.creationDate = LocalDate.parse(loanDto.getCreationDate(), formatter);
            }
        }
        catch (Exception ex){
            log.error("Error trying to format BondareaLoanDto to Loan, using format dd/MM/yyyy. The format coming is " + loanDto.getCreationDate());
        }

        this.currentInstalmentNumber = currentFeeNumber;

        this.InstalmentTotalQuantity = loanDto.getFeeTotalQuantity();

        this.InstalmentType = loanDto.getFeeDuration();
    }


    @Override
    public boolean equals(Object object){
        if(object == null)
            return false;

        if(!object.getClass().equals(this.getClass()))
            return super.equals(object);

        Loan newLoan = (Loan) object;

        return this.getHash().equals(newLoan.getHash());
        //return (this.status.equals(newLoan.getStatus()) && this.cycleDescription.equals(newLoan.getCycleDescription()) && (this.expiredAmount.compareTo(newLoan.getExpiredAmount()) == 0));
    }

    public String getHash(){
        StringBuilder hashBuilder = new StringBuilder();
        //Fix part
        hashBuilder.append(this.dniPerson);
        hashBuilder.append(this.idBondareaLoan.trim());
        hashBuilder.append(this.idGroup.trim());
        hashBuilder.append(this.creationDate.hashCode());
        //control change part
        hashBuilder.append(this.status.trim());
        hashBuilder.append(this.cycleDescription.trim());
        hashBuilder.append(this.expiredAmount.stripTrailingZeros());
        hashBuilder.append(this.getCurrentInstalmentNumber());

        String hash =  hashBuilder.toString();

        return hash !=null ? hash : "";
    }

    public void merge(Loan loanToUpdate){
        this.status = loanToUpdate.getStatus();
        this.cycleDescription = loanToUpdate.getCycleDescription();
        this.expiredAmount = loanToUpdate.getExpiredAmount();
        this.personName =   loanToUpdate.getPersonName();
        this.currentInstalmentNumber = loanToUpdate.getCurrentInstalmentNumber();
        this.InstalmentTotalQuantity = loanToUpdate.getInstalmentTotalQuantity();
        this.InstalmentType = loanToUpdate.getInstalmentType();
    }


    public boolean isDefault(){
        return (this.state!=null ? this.state.equals(LoanStateCodes.DEFAULT.getCode()) : false);
    }

    public Loan() {}
}
