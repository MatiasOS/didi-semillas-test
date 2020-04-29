package com.atixlabs.semillasmiddleware.app.dto;

import com.atixlabs.semillasmiddleware.app.model.credential.Credential;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bind.MethodDelegationBinder;

import java.time.LocalDateTime;

@Getter
@Setter
@Slf4j
public class CredentialDto {

    private Long id;

    private Long idDidiCredential;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime dateOfIssue;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime dateOfExpiry;

    private String name;

    private Long dniBeneficiary;

    private String credentialState;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime lastUpdate;

    private String credentialType;

    private String credentialStatus;


    public CredentialDto(Long id, Long idDidiCredential, LocalDateTime dateOfIssue, LocalDateTime dateOfExpiry, String name, Long dniBeneficiary, String creditState, String credentialType) {
        this.id = id;
        this.idDidiCredential = idDidiCredential;
        this.dateOfIssue = dateOfIssue;
        this.dateOfExpiry = dateOfExpiry;
        this.name = name;
        this.dniBeneficiary = dniBeneficiary;
        this.credentialState = creditState;
        this.credentialType = credentialType;

    }


    public CredentialDto(Credential credential) {
        this.id = credential.getId();
        this.idDidiCredential = credential.getIdDidiCredential();
        this.dateOfIssue = credential.getDateOfIssue();
        this.dateOfExpiry = credential.getDateOfExpiry();
        this.name = credential.getBeneficiary().getName();
        this.dniBeneficiary = credential.getBeneficiary().getDocumentNumber();
        this.credentialState = credential.getCredentialState().getStateName();
        this.lastUpdate = credential.getUpdated();
        this.credentialType = credential.getCredentialDescription();
        this.credentialStatus = credential.getCredentialStatus();
    }


    @Override
    public String toString() {
        return "CredentialDto{" +
                "id=" + id +
                ", idDidiCredential=" + idDidiCredential +
                ", dateOfIssue=" + dateOfIssue +
                ", dateOfExpiry=" + dateOfExpiry +
                ", name='" + name + '\'' +
                ", dniBeneficiary=" + dniBeneficiary +
                ", credentialState='" + credentialState + '\'' +
                ", lastUpdate=" + lastUpdate +
                ", credentialType='" + credentialType + '\'' +
                ", credentialStatus='" + credentialStatus + '\'' +
                '}';
    }
}
