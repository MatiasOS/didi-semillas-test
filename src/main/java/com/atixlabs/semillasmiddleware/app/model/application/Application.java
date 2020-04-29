package com.atixlabs.semillasmiddleware.app.model.application;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table
public class Application {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private Long documentNumber;

    private String applicationState; //TODO: enum or table?

    //Relationship with Person

    //TODO Data ?

   // id, dni, estado -> activo o inactivo
    //No se van a borrar, y si llega una nueva solicitud de un dni -> invalida la anterior (cambia su estado)

    //state: -> CREDENTIAL_PENDING_SURVEY("Pendiente-encuesta");
}
