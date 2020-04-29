package com.atixlabs.semillasmiddleware.app.bondarea.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class BondareaLoanResponse {

    private String estado; // [correcto | error]
    private String error; // Descripción del tipo de error
    private String errorcode; // Código de error

    @SerializedName("prestamos")
    private List<BondareaLoanDto> loans;
}
