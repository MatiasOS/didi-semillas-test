package com.atixlabs.semillasmiddleware.app.model.credential;

import com.atixlabs.semillasmiddleware.excelparser.app.categories.PersonCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import java.time.LocalDateTime;


@NoArgsConstructor
@Getter
@Setter
@Entity
@PrimaryKeyJoinColumn(referencedColumnName="id")
public class CredentialCredit extends Credential {



    private Long idCredit;

    private String creditName;

    private Long idGroup;

    //presidente, secretario y tesorero, se obtiene a partir de la api de Bondarea
    private String groupName;

    private String rol;

    private String currentCycle;

    private String creditState;

    private Double amount; //TODO revisar el tipo para monto. Para dinero deberiamos usar BigDecimal

    private String beneficiaryDocumentType;

    private Long dniBeneficiary;


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
        this.setDniBeneficiary(personCategory.getIdNumber());

    }


    @Override
    public String toString() {
        return "CredentialCredit{" +
                "creditName='" + creditName + '\'' +
                ", idGroup=" + idGroup +
                ", groupName='" + groupName + '\'' +
                ", rol='" + rol + '\'' +
                ", currentCycle='" + currentCycle + '\'' +
                ", creditState='" + creditState + '\'' +
                ", amount=" + amount +
                ", beneficiaryDocumentType='" + beneficiaryDocumentType + '\'' +
                ", beneficiaryDocumentNumber=" + dniBeneficiary +
                '}';
    }
}
