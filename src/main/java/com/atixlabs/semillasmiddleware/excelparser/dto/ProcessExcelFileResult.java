package com.atixlabs.semillasmiddleware.excelparser.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the final result of the processing of a file
 */
@NoArgsConstructor
@Getter
@Setter
public class ProcessExcelFileResult {

    private int totalReadRows = 0;
    private int totalValidRows = 0;
    private int totalErrorsRows = 0;
    private int totalEmptyRows = 0;
    private int totalProcessedForms = 0;
    private String fileName = null;
    private boolean fileValid = true;
    private String fileError = null;

    private List<ExcelErrorDetail> errorRows = new ArrayList<>();

    public void addRowError(Integer errorHeader, String errorBody){
        this.errorRows.add(new ExcelErrorDetail(String.valueOf(errorHeader), errorBody));
        this.totalErrorsRows++;
    }

    public void addRowError(String errorHeader, String errorBody){
        this.errorRows.add(new ExcelErrorDetail(errorHeader, errorBody));
        this.totalErrorsRows++;
    }

    public void addTotalReadRow(){
        this.totalReadRows++;
    }
    public void addTotalValidRows(){
        this.totalValidRows++;
    }
    public void addEmptyRow(){this.totalEmptyRows++;}
    public void addTotalProcessedForms(){ this.totalProcessedForms++;}


    @Override
    public String toString() {
        return "ProcessExcelFileResult{" +
                "totalReadRows=" + totalReadRows +
                ", totalValidRows=" + totalValidRows +
                ", totalErrorsRows=" + totalErrorsRows +
                ", totalEmptyRows=" + totalEmptyRows +
                ", totalProcessedForms=" + totalProcessedForms +
                ", fileName='" + fileName + '\'' +
                ", isFileValid=" + fileValid +
                ", fileError='" + fileError + '\'' +
                ", errorRows=" + errorRows +
                '}';
    }
}
