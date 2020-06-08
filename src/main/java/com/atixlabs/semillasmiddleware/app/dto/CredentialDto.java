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

    private Long id;

    private String idDidiCredential;

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
    private Float expiredAmount;

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
        this.idDidiCredential = credential.getIdDidiCredential();
        //this.dateOfExpiry = credential.getDateOfRevocation();
        this.name = credential.getBeneficiaryFirstName() +" "+ credential.getBeneficiaryLastName();
        this.dniBeneficiary = credential.getBeneficiaryDni();
        this.credentialState = credential.getCredentialState().getStateName();
        this.lastUpdate = credential.getUpdated();
        this.credentialType = credential.getCredentialDescription();
        this.isRevocable = credential.isManuallyRevocable();
        this.dateOfIssue = credential.getDateOfIssue();
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


    private void baseConstructor(Credential credential) {
        this.id = credential.getId();
        this.idDidiCredential = credential.getIdDidiCredential();
        this.name = credential.getBeneficiaryFirstName() + " " + credential.getBeneficiaryLastName();
        this.creditHolderDni = credential.getCreditHolderDni();
        this.dniBeneficiary = credential.getBeneficiaryDni();
        this.credentialState = credential.getCredentialState().getStateName();
        this.lastUpdate = credential.getUpdated();
        this.credentialType = credential.getCredentialDescription();
        this.dateOfRevocation = credential.getDateOfRevocation();
        this.isRevocable = credential.isManuallyRevocable();
        if(credential.getRevocationReason() !=null)
            this.revocationReason = credential.getRevocationReason().getReason();
    }

    public CredentialDto(CredentialBenefits benefits){
        this.baseConstructor(benefits);
        this.beneficiaryType = benefits.getBeneficiaryType();
    }

    public CredentialDto(CredentialCredit credit){
        this.baseConstructor(credit);
        this.idBondareaCredit = credit.getIdBondareaCredit();
        this.creationDate = credit.getCreationDate();
        this.creditType = credit.getCreditType();
        this.idGroup = credit.getIdGroup();
        this.currentCycle = credit.getCurrentCycle();
        this.totalCycles = credit.getTotalCycles();
        this.amountExpiredCycles = credit.getAmountExpiredCycles();
        this.creditState = credit.getCreditState();
        this.finishDate = credit.getFinishDate();
        this.expiredAmount = credit.getExpiredAmount();
    }

    public CredentialDto(CredentialDwelling dwelling){
        this.baseConstructor(dwelling);
        this.dwellingType = dwelling.getDwellingType();
        this.dwellingAddress = dwelling.getDwellingAddress();
        this.possessionType = dwelling.getPossessionType();
    }

    public CredentialDto(CredentialEntrepreneurship entrepreneurship){
        this.baseConstructor(entrepreneurship);
        this.entrepreneurshipType = entrepreneurship.getEntrepreneurshipType();
        this.startActivity = entrepreneurship.getStartActivity();
        this.mainActivity = entrepreneurship.getMainActivity();
        this.entrepreneurshipName = entrepreneurship.getEntrepreneurshipName();
        this.entrepreneurshipAddress = entrepreneurship.getEntrepreneurshipAddress();
        this.endActivity = entrepreneurship.getEndActivity();
    }

    public CredentialDto(CredentialIdentity identity) {
        this.baseConstructor(identity);
        this.holderName = identity.getCreditHolderFirstName() + " " + identity.getCreditHolderLastName();
        this.relationWithCreditHolder = identity.getRelationWithCreditHolder();
        this.beneficiaryGender = identity.getBeneficiaryGender();
        this.beneficiaryBirthDate = identity.getBeneficiaryBirthDate();
    }

}
