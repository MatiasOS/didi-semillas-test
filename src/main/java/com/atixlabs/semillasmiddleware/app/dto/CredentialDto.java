package com.atixlabs.semillasmiddleware.app.dto;

import com.atixlabs.semillasmiddleware.app.model.credential.*;
import com.atixlabs.semillasmiddleware.app.model.credential.constants.CredentialCategoriesCodes;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bind.MethodDelegationBinder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Slf4j
@ToString
public class CredentialDto {

    //If we change a field name here, we must change it also in front !!!

    private Long id;

    private String idDidiCredential; //this is the didi of the person

    private String name;
    private String holderName;

    private Long creditHolderDni;
    private Long dniBeneficiary;

    private String credentialState;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime dateOfIssue;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime lastUpdate;

    private String credentialType;

    private Boolean isRevocable;

    private LocalDateTime dateOfRevocation;
    private String revocationReason;

    //beneficiary
    private String beneficiaryType;

    //credit
    private String idBondareaCredit;
    private LocalDate creationDate;
    private String creditType;
    private String idGroup;
    private String currentCycle;
    private Integer totalCycles;
    private Integer amountExpiredCycles;
    private String creditState;
    private LocalDate finishDate;
    private String expiredAmount;

    //dwelling
    private String dwellingType;
    private String dwellingAddress;
    private String possessionType;

    //entrepreneurship
    private String entrepreneurshipType;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startActivity;
    private String mainActivity;
    private String entrepreneurshipName;
    private String entrepreneurshipAddress;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endActivity;


    //identity
    private String relationWithCreditHolder;
    private String beneficiaryGender;
    private LocalDate beneficiaryBirthDate;

    public CredentialDto() {

    }

    public CredentialDto(Credential credential) {
        this.id = credential.getId();
        this.idDidiCredential = credential.getIdDidiReceptor();
        //this.dateOfExpiry = credential.getDateOfRevocation();
        this.name = credential.getBeneficiaryFirstName() +" "+ credential.getBeneficiaryLastName();
        this.dniBeneficiary = credential.getBeneficiaryDni();
        this.creditHolderDni = credential.getCreditHolderDni();
        this.credentialState = credential.getCredentialState().getStateName();
        this.lastUpdate = credential.getUpdated();
        this.credentialType = credential.getCredentialDescription();
        this.isRevocable = credential.isManuallyRevocable();
        this.dateOfIssue = credential.getDateOfIssue();
    }



    public void baseCredentialDto(Credential credential) {
        this.id = credential.getId();
        this.idDidiCredential = credential.getIdDidiReceptor();
        //this.dateOfExpiry = credential.getDateOfRevocation();
        this.name = credential.getBeneficiaryFirstName() +" "+ credential.getBeneficiaryLastName();
        this.dniBeneficiary = credential.getBeneficiaryDni();
        this.creditHolderDni = credential.getCreditHolderDni();
        this.credentialState = credential.getCredentialState().getStateName();
        this.lastUpdate = credential.getUpdated();
        this.credentialType = credential.getCredentialDescription();
        this.dateOfRevocation = credential.getDateOfRevocation();
        this.isRevocable = credential.isManuallyRevocable();
       // this.dateOfIssue = credential.getDateOfIssue();

        if(credential.getRevocationReason() !=null)
            this.revocationReason = credential.getRevocationReason().getReason();
    }

    public static CredentialDto constructBasedOnCredentialType(Credential credential){
        if (credential.getCredentialCategory() != null) {
            if (CredentialCategoriesCodes.BENEFIT.getCode().equals(credential.getCredentialCategory())) {
                return new CredentialDto((CredentialBenefits) credential);
            } else {
                if (CredentialCategoriesCodes.CREDIT.getCode().equals(credential.getCredentialCategory())) {
                    return new CredentialDto((CredentialCredit) credential);
                } else {
                    if (CredentialCategoriesCodes.IDENTITY.getCode().equals(credential.getCredentialCategory())) {
                        return new CredentialDto((CredentialIdentity) credential);
                    } else {
                        if (CredentialCategoriesCodes.DWELLING.getCode().equals(credential.getCredentialCategory())) {
                            return new CredentialDto((CredentialDwelling) credential);
                        } else {
                            if (CredentialCategoriesCodes.ENTREPRENEURSHIP.getCode().equals(credential.getCredentialCategory())) {
                                return new CredentialDto((CredentialEntrepreneurship) credential);
                            } else
                                return new CredentialDto();
                        }
                    }
                }
            }
        }
        return new CredentialDto();
    }


    public CredentialDto(CredentialBenefits benefits){
        this.baseCredentialDto(benefits);
        this.beneficiaryType = benefits.getBeneficiaryType();
    }

    public CredentialDto(CredentialCredit credit){
        this.baseCredentialDto(credit);
        this.idBondareaCredit = credit.getIdBondareaCredit();
        this.creationDate = credit.getCreationDate();
        this.creditType = credit.getCreditType();
        this.idGroup = credit.getIdGroup();
        this.currentCycle = credit.getCurrentCycle();
        this.totalCycles = credit.getTotalCycles();
        this.amountExpiredCycles = credit.getAmountExpiredCycles();
        this.creditState = credit.getCreditState();
        this.finishDate = credit.getFinishDate();
        this.expiredAmount = credit.getExpiredAmount().toString();
    }

    public CredentialDto(CredentialDwelling dwelling){
        this.baseCredentialDto(dwelling);
        this.dwellingType = dwelling.getDwellingType();
        this.dwellingAddress = dwelling.getDwellingAddress();
        this.possessionType = dwelling.getPossessionType();
    }

    public CredentialDto(CredentialEntrepreneurship entrepreneurship){
        this.baseCredentialDto(entrepreneurship);
        this.entrepreneurshipType = entrepreneurship.getEntrepreneurshipType();
        this.startActivity = entrepreneurship.getStartActivity();
        this.mainActivity = entrepreneurship.getMainActivity();
        this.entrepreneurshipName = entrepreneurship.getEntrepreneurshipName();
        this.entrepreneurshipAddress = entrepreneurship.getEntrepreneurshipAddress();
        this.endActivity = entrepreneurship.getEndActivity();
    }

    public CredentialDto(CredentialIdentity identity) {
        this.baseCredentialDto(identity);
        this.holderName = identity.getCreditHolderFirstName() + " " + identity.getCreditHolderLastName();
        this.relationWithCreditHolder = identity.getRelationWithCreditHolder();
        this.beneficiaryGender = identity.getBeneficiaryGender();
        this.beneficiaryBirthDate = identity.getBeneficiaryBirthDate();
    }

}
