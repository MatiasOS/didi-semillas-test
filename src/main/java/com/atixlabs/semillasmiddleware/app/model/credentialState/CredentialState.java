package com.atixlabs.semillasmiddleware.app.model.credentialState;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table
@ToString
public class CredentialState {

    //LA DECISION DE QUE SEA UNA TABLA ES PORQUE SI EL DIA DE MAÑANA CAMBIA LA FORMA DE DECIRLE "VIGENTE" A UN ESTADO, NO PODRIAS MODIFICARLO EN CADA CREDENCIAL
    // EN CAMBIO ASI, SIMPLEMENTE MODIFICAS EL NOMBRE QUE TIENE EN AL TABLA
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;

    private String stateName;

    public CredentialState(String state){
        this.stateName = state;
    }

    public  CredentialState(){};


}
