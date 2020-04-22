package com.atixlabs.semillasmiddleware.app.model.credential;

import com.atixlabs.semillasmiddleware.app.model.beneficiary.Person;
import com.atixlabs.semillasmiddleware.app.model.credentialState.CredentialState;
import com.atixlabs.semillasmiddleware.security.model.AuditableEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Table(name = "credential")
@Entity
@Inheritance( strategy = InheritanceType.JOINED )
public abstract class Credential extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long idDidiIssuer;

    private Long idDidiReceptor;

    @Column(unique = true)
    private Long idDidiCredential;

    private Long idHistorical;

    private LocalDateTime dateOfIssue;

    private LocalDateTime dateOfExpiry;

    //private Long idRelatedCredential; //TODO: Cual es la finalidad ? Si se asocia las credenciales del titular deberia estar en credentialCredit ya
                                      // ya que es la credencial principal. Las credcenciales familiares se pueden encontrar filtrando a las personas que
                                      // asociado el dni del titular

    @ManyToOne
    private Person beneficiary;

    @ManyToOne
    private CredentialState credentialState;

    private String credentialStatus; //pending -> bondarea/didi || Active -> null

    private String credentialDescription;

    private String credentialCategory;




    @Override
    public String toString() {
        return "Credential{" +
                "id=" + id +
                ", idDidiIssuer=" + idDidiIssuer +

                ", idDidiReceptor=" + idDidiReceptor +
                ", idDidiCredential=" + idDidiCredential +
                ", idHistorical=" + idHistorical +
                ", dateOfIssue=" + dateOfIssue +
                ", dateOfExpiry=" + dateOfExpiry +
               // ", idRelatedCredential=" + idRelatedCredential +
                ", beneficiary=" + beneficiary +
                ", credentialState='" + credentialState + '\'' +
                ", credentialType='" + credentialDescription + '\'' +
                '}';
    }
}
