package com.atixlabs.semillasmiddleware.app.model.credential;

import com.atixlabs.semillasmiddleware.app.model.beneficiary.Person;
import com.atixlabs.semillasmiddleware.app.model.credentialState.CredentialState;
import com.atixlabs.semillasmiddleware.app.model.credentialState.RevocationReason;
import com.atixlabs.semillasmiddleware.security.model.AuditableEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@Table(name = "credential")
@Entity
@Inheritance( strategy = InheritanceType.JOINED )
@ToString
public abstract class Credential extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    protected String idDidiIssuer;//fixed value given by semillas

    protected String idDidiReceptor;//value generated from user when downloading the app.

    @Column(unique = true)
    protected String idDidiCredential;//value returned from didi when issuing the credential.

    protected Long idHistorical;

    protected LocalDateTime dateOfIssue;

    protected LocalDateTime dateOfRevocation;

    @ManyToOne
    protected RevocationReason revocationReason;


    @ManyToOne
    protected Person creditHolder;
    protected Long creditHolderDni;
    protected String creditHolderFirstName;
    protected String creditHolderLastName;

    @ManyToOne
    protected Person beneficiary;
    protected Long beneficiaryDni;
    protected String beneficiaryFirstName;
    protected String beneficiaryLastName;

    @ManyToOne
    protected CredentialState credentialState;

    protected String credentialDescription;

    protected String credentialCategory;

    public boolean isManuallyRevocable(){return true;}

    public Boolean isEmitted(){ return idDidiCredential != null;}

    public Credential(Credential credential){
        //this.id = credential.id;//id is not copied to save as new Credential.
        this.id = null;
        this.idDidiIssuer = credential.idDidiIssuer;
        this.idDidiReceptor = credential.idDidiReceptor;
        this.idDidiCredential = credential.idDidiCredential;
        this.idHistorical = credential.idHistorical;
        this.dateOfIssue = credential.dateOfIssue;
        this.dateOfRevocation = credential.dateOfRevocation;

        this.creditHolder = credential.creditHolder;
        this.creditHolderDni = credential.creditHolderDni;
        this.creditHolderFirstName = credential.creditHolderFirstName;
        this.creditHolderLastName = credential.creditHolderLastName;

        this.beneficiary = credential.beneficiary;
        this.beneficiaryDni = credential.beneficiaryDni;
        this.beneficiaryFirstName = credential.beneficiaryFirstName;
        this.beneficiaryLastName = credential.beneficiaryLastName;

        this.credentialState = credential.credentialState;
        this.credentialDescription = credential.credentialDescription;
        this.credentialCategory = credential.credentialCategory;
    }


}
