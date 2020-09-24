package com.atixlabs.semillasmiddleware.app.model.credential;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Transient;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@Entity
@PrimaryKeyJoinColumn(referencedColumnName="id")
public class CredentialDwelling extends Credential {

    private String dwellingType;
    private String dwellingAddress;
    private String possessionType;
    //TODO campo que faltaba respuesta: "Distrito de Residencia" String
    private Boolean brick; //ladrillo
    private Boolean lock; //chapa
    private Boolean wood; //madera
    private Boolean paperBoard; //carton
    private String district; //distrito de residencia
    private String lightInstallation; //instalacion de luz
    private String generalConditions; //condiciones generales
    private String neighborhoodType; //tipo de barrio
    private Boolean gas; //red de gas
    private Boolean carafe; //garrafa
    private Boolean water; //red de agua
    private Boolean watterWell; //pozo / bomba
    private Integer antiquity; //antiguedad
    private Integer numberOfEnvironments; //cantidad de ambientes
    private Long rental; //monto alquiler
    private String address;
    private String location;
    private String neighborhood;

    public CredentialDwelling(CredentialDwelling credentialDwelling){
        super(credentialDwelling);
        this.dwellingType = credentialDwelling.dwellingType;
        this.dwellingAddress = credentialDwelling.dwellingAddress;
        this.possessionType = credentialDwelling.possessionType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CredentialDwelling that = (CredentialDwelling) o;
        return Objects.equals(beneficiaryDni, that.beneficiaryDni) &&
                Objects.equals(credentialDescription, that.credentialDescription) &&
                Objects.equals(credentialCategory, that.credentialCategory) &&
                Objects.equals(dwellingAddress.trim().toUpperCase(), that.dwellingAddress.trim().toUpperCase());
    }

}
