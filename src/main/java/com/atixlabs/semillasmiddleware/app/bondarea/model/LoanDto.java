package com.atixlabs.semillasmiddleware.app.bondarea.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class LoanDto implements Serializable {

    private Long id;

    private Long dniPerson;

    private String idBondareaLoan; // ID del crédito individual. Para créditosgrupales representa el tramo del crédito grupal   (Ej. B26F5FKZ)

    private String tagBondareaLoan; // Nombre del producto de préstamo (Ej. Recurrentes) //TODO credit type

    private String status;

    private String idProductLoan;  //ID de producto de préstamo (Ej.  B26F5FKZ)

    private String idGroup; // ID del   crédito grupal y su estado (Ej.55-B26F5FKZ)

    private String cycleDescription; // Nombre asignado al crédito (Ej. Ciclo 2)

    private String creationDate; // Fecha de otorgamiento cuentas

    private String personName; // Nombre del solicitante del tramo (Ej. Perez, Juan)

    private String userId; // ID del solicitante del tramo (Ej. B26F5FKZ)

    private BigDecimal amount; // Monto del crédito del tramo (Ej. 10000)

    private String dateFirstInstalment; // Fecha de primera cuota

    private BigDecimal expiredAmount; // Saldo vencido del crédito individual, compuesto por capital, intereses, seguros y cargos (Ej. 1845.24)

    private Boolean hasCredential;


}
