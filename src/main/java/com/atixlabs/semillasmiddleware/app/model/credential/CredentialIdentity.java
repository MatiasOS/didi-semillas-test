package com.atixlabs.semillasmiddleware.app.model.credential;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import java.time.LocalDate;


@Getter
@Setter
@NoArgsConstructor
@Entity
@PrimaryKeyJoinColumn(referencedColumnName="id")
public class CredentialIdentity extends Credential {

    private String relationWithCreditHolder;
    private String beneficiaryGender;
    private LocalDate beneficiaryBirthDate;

    public CredentialIdentity(CredentialIdentity credentialIdentity){
        super(credentialIdentity);

        this.relationWithCreditHolder = credentialIdentity.relationWithCreditHolder;
        this.beneficiaryGender = credentialIdentity.beneficiaryGender;
        this.beneficiaryBirthDate = credentialIdentity.beneficiaryBirthDate;

    }

    public CredentialIdentity(CredentialIdentity credentialIdentity, String relationWithCreditHolder){
        super(credentialIdentity);

        this.relationWithCreditHolder = relationWithCreditHolder;
        this.beneficiaryGender = credentialIdentity.beneficiaryGender;
        this.beneficiaryBirthDate = credentialIdentity.beneficiaryBirthDate;

    }

}
