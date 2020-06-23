package com.atixlabs.semillasmiddleware.app.bondarea.dto;

import com.atixlabs.semillasmiddleware.app.bondarea.model.LoanDto;
import com.atixlabs.semillasmiddleware.app.bondarea.model.constants.BondareaLoanStatusCodes;
import com.atixlabs.semillasmiddleware.app.bondarea.model.constants.LoanStatusCodes;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class BondareaLoanDto {

    @SerializedName("pp")
    private String idProductLoan; // ID de producto de préstamo (Ej. B26F5FKZ)

    @SerializedName("ppt")
    private String tagBondareaLoan; // Nombre del producto de préstamo (Ej. Recurrentes)

    @SerializedName("sta")
    private String statusDescription; // Estado del préstamo (Ej. Preparación, Activo, Finalizado)

    @SerializedName("staint")
    private int status; // Estado numérico del préstamo (Ej.0=Preparación, 55= Activo, 60=finalizado)

    @SerializedName("t")
    private String statusFullDescription; // Deccripcion de estado ?

    @SerializedName("id")
    private String idBondareaLoan; // ID del crédito individual. Para créditosgrupales representa el tramo del crédito grupal   (Ej. B26F5FKZ)

    @SerializedName("id_pg")
    private String idGroup; // ID del crédito grupal y su estado (Ej.55-B26F5FKZ)

    @SerializedName("pg")
    private String cycle; // Nombre asignado al crédito (Ej. Ciclo 2)

    @SerializedName("fOt")
    private String creationDate; // Fecha de otorgamiento cuentas

    @SerializedName("cuentasTag")
    private String personName; // Nombre del solicitante del tramo (Ej. Perez, Juan)

    @SerializedName("usr")
    private String userId; // ID del solicitante del tramo (Ej. B26F5FKZ)

    @SerializedName("dni")
    private Long dni; // Nro. de documento del solicitante del tramo (Ej. 99999999)

    @SerializedName("m")
    private BigDecimal amount; // Monto del crédito del tramo (Ej. 10000)

    @SerializedName("fPri")
    private String dateFirstInstalment; // Fecha de primera cuota

    @SerializedName("sv")
    private BigDecimal expiredAmount; // Saldo vencido del crédito individual, compuesto por capital, intereses, seguros y cargos (Ej. 1845.24)


    /**
     * for mock
     *
     */
    public BondareaLoanDto(LoanDto loan){
        this.idProductLoan = loan.getIdProductLoan();
        this.tagBondareaLoan = loan.getTagBondareaLoan();
        if(loan.getStatus().equals(LoanStatusCodes.ACTIVE.getCode()))
            this.status = Integer.parseInt(BondareaLoanStatusCodes.ACTIVE.getCode());
        else
            if(loan.getStatus().equals(LoanStatusCodes.FINALIZED.getCode()))
                this.status = Integer.parseInt(BondareaLoanStatusCodes.FINALIZED.getCode());
        this.idBondareaLoan = loan.getIdBondareaLoan();
        this.idGroup = loan.getIdGroup();
        this.cycle = loan.getCycleDescription();
        this.creationDate = loan.getCreationDate();
        this.personName = loan.getPersonName();
        this.userId = loan.getUserId();
        this.dni = loan.getDniPerson();
        this.amount = loan.getAmount();
        this.dateFirstInstalment = loan.getDateFirstInstalment();
        this.expiredAmount = loan.getExpiredAmount();
    }
}
