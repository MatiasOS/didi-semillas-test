package com.atixlabs.semillasmiddleware.excelparser.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ExcelErrorDetail {

    private String errorHeader;
    private String errorBody;

    public ExcelErrorDetail(String errorHeader, String errorBody){
        this.errorHeader = errorHeader;
        this.errorBody = errorBody;
    }
    public ExcelErrorDetail(String errorBody){
        this.errorHeader = "";
        this.errorBody = errorBody;
    }

    @Override
    public String toString() {
        return "{" +
                "errorHeader='" + errorHeader + '\'' +
                ", errorBody='" + errorBody + '\'' +
                '}';
    }
}
