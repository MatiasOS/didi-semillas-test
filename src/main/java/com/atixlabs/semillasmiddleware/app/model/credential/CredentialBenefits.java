package com.atixlabs.semillasmiddleware.app.model.credential;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

@Getter
@Setter
@NoArgsConstructor
@Entity
@PrimaryKeyJoinColumn(referencedColumnName="id")
public class CredentialBenefits extends Credential{

    private String beneficiaryType;

    public CredentialBenefits(CredentialBenefits credentialBenefits) {
        super(credentialBenefits);
        this.beneficiaryType = credentialBenefits.beneficiaryType;
    }

    @Override
    public boolean isManuallyRevocable(){return false;}
}
