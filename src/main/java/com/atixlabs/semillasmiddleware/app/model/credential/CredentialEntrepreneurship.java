package com.atixlabs.semillasmiddleware.app.model.credential;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Transient;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@PrimaryKeyJoinColumn(referencedColumnName="id")
public class CredentialEntrepreneurship extends Credential {

    // Comercio, Producción,Servicio
    private String entrepreneurshipType; //TODO enum or new class?

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime startActivity;

    private String mainActivity;

    private String entrepreneurshipName;

    private String entrepreneurshipAddress;

    //TODO
    // private String creditState; //TODO is into super class?? Este campo estaria en creditState, asociada el beneficiary

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime endActivity;

}
