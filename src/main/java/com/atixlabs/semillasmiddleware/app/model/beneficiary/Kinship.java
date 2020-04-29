package com.atixlabs.semillasmiddleware.app.model.beneficiary;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table
public class Kinship {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "ID_PERSON")
    @OneToOne
    private Person mainBeneficiary;

    //private Person kinsman; //todo: eliminar esto, ya que se agrega en persona

    private String kindOfKinship ;

}
