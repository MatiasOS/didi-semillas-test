package com.atixlabs.semillasmiddleware.app.model.credential;

import com.atixlabs.semillasmiddleware.excelparser.app.categories.PersonCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Transient;


@NoArgsConstructor
@Getter
@Setter
@Table(name = "credential_credit")
@Entity
@PrimaryKeyJoinColumn(name = "id")
//@PrimaryKeyJoinColumn(referencedColumnName="id")
public class CredentialCredit extends Credential {

        //    private Long idCredit;
    private String creditName;

    private Long idGroup;

    private String groupName;

    private String rol;

    private String currentCycle;

    private String creditState;

    private Double amount;

    private String beneficiaryDocumentType;

    private Long beneficiaryDocumentNumber;


    public CredentialCredit(PersonCategory personCategory){
        /*credentialCredit.setDateOfIssue(LocalDateTime.now());
        credentialCredit.setDateOfExpiry(LocalDateTime.now().plusDays(14L));
        credentialCredit.setCurrentCycle("imported-from-excel");
        credentialCredit.setCreditState("pre-credential");
        credentialCredit.setBeneficiaryDocumentType(personCategory.getIdType());
        credentialCredit.setBeneficiaryDocumentNumber(personCategory.getIdNumber());*/
        this.setDateOfIssue(LocalDateTime.now());
        this.setDateOfExpiry(LocalDateTime.now().plusDays(14L));
        this.setCurrentCycle("imported-from-excel");
        this.setCreditState("pre-credential");
        this.setBeneficiaryDocumentType(personCategory.getIdType());
        this.setBeneficiaryDocumentNumber(personCategory.getIdNumber());

    }

    @Transient
    private String credentialType = "CredentialCredit";


    @Override
    public String toString() {
        return "CredentialCredit{" +
                //"idCredit=" + idCredit +
                ", creditName='" + creditName + '\'' +
                ", idGroup=" + idGroup +
                ", groupName='" + groupName + '\'' +
                ", rol='" + rol + '\'' +
                ", currentCycle='" + currentCycle + '\'' +
                ", creditState='" + creditState + '\'' +
                ", amount=" + amount +
                ", dniBeneficiary=" + beneficiaryDocumentNumber +
                ", credentialType=" + credentialType +
                '}' + super.toString();
    }
}
