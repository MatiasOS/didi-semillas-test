package com.atixlabs.semillasmiddleware.app.model.credential;

import com.atixlabs.semillasmiddleware.excelparser.app.categories.PersonCategory;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


@NoArgsConstructor
@Getter
@Setter
@Entity
@ToString
@PrimaryKeyJoinColumn(referencedColumnName="id")
public class CredentialCredit extends Credential {

    private String idBondareaCredit;

    //private BigDecimal creditAmmount; TO VALIDATE

    private LocalDate creationDate;

    private String creditType; //TODO  Credito semilla / escolar / estacional / individual / de motos/de min. desarrolo social

    private String idGroup;

    private String currentCycle;

    private int totalCycles;

    private Integer currentCycleNumber;

    private int amountExpiredCycles;

    private String creditState;

    private LocalDate finishDate;

    private BigDecimal expiredAmount;

    @Override
    public boolean isManuallyRevocable(){return false;}

    public CredentialCredit(CredentialCredit credentialCredit) {
        super(credentialCredit);
        this.idBondareaCredit = credentialCredit.idBondareaCredit;
        this.creationDate = credentialCredit.creationDate;
        this.creditType = credentialCredit.creditType;
        this.idGroup = credentialCredit.idGroup;
        this.currentCycle = credentialCredit.currentCycle;
        this.totalCycles = credentialCredit.totalCycles;
        this.amountExpiredCycles = credentialCredit.amountExpiredCycles;
        this.creditState = credentialCredit.creditState;
        this.finishDate = credentialCredit.finishDate;
        this.expiredAmount = credentialCredit.expiredAmount;
        this.currentCycleNumber = credentialCredit.currentCycleNumber;
        //this.creditAmmount = credentialCredit.creditAmmount; TO VALIDATE
    }

}
