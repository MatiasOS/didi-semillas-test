package com.atixlabs.semillasmiddleware.app.model.credential;

import com.sun.xml.bind.v2.TODO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Transient;

@Getter
@Setter
@NoArgsConstructor
@Entity
@PrimaryKeyJoinColumn(referencedColumnName="id")
public class CredentialIdentity extends Credential {

    private Long dniBeneficiary;

    private String nameBeneficiary;

    private Long dniCreditHolder;

    private String nameCreditHolder;

    //GENDER ? BirthDate ?
    

}
