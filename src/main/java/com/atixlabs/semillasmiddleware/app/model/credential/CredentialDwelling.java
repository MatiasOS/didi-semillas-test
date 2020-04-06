package com.atixlabs.semillasmiddleware.app.model.credential;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Transient;

@Getter
@Setter
@Entity
@PrimaryKeyJoinColumn(referencedColumnName="id")
public class CredentialDwelling extends Credential {

    private String dwellingType;
    private String dwellingAddress;
    private String possessionType;

    @Transient
    private String credentialType = "CredentialDwelling";


    public CredentialDwelling(Credential credential) {
        super(credential);
    }
}
