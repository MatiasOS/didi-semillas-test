package com.atixlabs.semillasmiddleware.excelparser.row;

import com.atixlabs.semillasmiddleware.excelparser.dto.ProcessExcelFileResult;
import com.atixlabs.semillasmiddleware.excelparser.exception.InvalidRowException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Iterator;


/**
 * parent class of the parsers of each type of row
 */
@Getter
@Setter
@Slf4j
@NoArgsConstructor
public abstract class ExcelRow {

    protected int rowNum;
    //protected String errorMessage = "";
    //protected ArrayList<String> errorMessageList = new ArrayList<>();
    //protected boolean isValid = false;
    protected boolean exists = false;
    protected int cellIndex = 0;
    protected String cellIndexName = "None";
    protected String cellIndexDescription = "";


    public ExcelRow(Row row) throws InvalidRowException {
        try {
            this.rowNum = row.getRowNum()+1;//must +1 due to headers
            this.parseRow(row);
        } catch (Exception e) {
            log.error("ExcelRow: ", e);
            //processExcelFileResult.addRowError("", e.toString());
            throw new InvalidRowException(e.getMessage());
        }
    }

    protected String getCellStringValue(Row row, int cellIndex, String description) {
        this.saveCellData(cellIndex, description);
        if (row == null || row.getCell(cellIndex) == null)
            return null;
        this.cellIndexName = row.getCell(cellIndex).getAddress().formatAsString();
        if (row.getCell(cellIndex).getCellType() == CellType.BLANK)
            return null;
        row.getCell(cellIndex).setCellType(CellType.STRING);
        return row.getCell(cellIndex).getStringCellValue();
    }

    protected Long getCellLongValue(Row row, int cellindex, String descripcion) {
        this.saveCellData(cellindex, descripcion);
        if (row == null || row.getCell(cellindex) == null)
            return null;
        this.cellIndexName = row.getCell(cellindex).getAddress().formatAsString();
        if (row.getCell(cellindex).getCellType() == CellType.BLANK)
            return null;
        return (long) row.getCell(cellindex).getNumericCellValue();
    }

    Double getCellDoubleValue(Row row, int cellindex, String descripcion) {
        this.saveCellData(cellindex, descripcion);
        if (row == null || row.getCell(cellindex) == null)
            return null;
        this.cellIndexName = row.getCell(cellindex).getAddress().formatAsString();
        if (row.getCell(cellindex).getCellType() == CellType.BLANK)
            return null;
        return row.getCell(cellindex).getNumericCellValue();
    }

    Integer getCellIntValue(Row row, int cellindex, String descripcion) {
        this.saveCellData(cellindex, descripcion);
        if (row == null || row.getCell(cellindex) == null)
            return null;
        this.cellIndexName = row.getCell(cellindex).getAddress().formatAsString();
        if (row.getCell(cellindex).getCellType() == CellType.BLANK)
            return null;
        return (int) row.getCell(cellindex).getNumericCellValue();
    }

    Boolean getCellBooleanValue(Row row, int cellindex, String descripcion) {
        this.saveCellData(cellindex, descripcion);
        if (row == null || row.getCell(cellindex) == null)
            return null;
        this.cellIndexName = row.getCell(cellindex).getAddress().formatAsString();
        if (row.getCell(cellindex).getCellType() == CellType.BLANK)
            return null;
        return row.getCell(cellindex).getBooleanCellValue();
    }



    protected Long getCellStringToLongValue(Row row, int cellindex, String descripcion) {
        return Long.parseLong(getCellStringValue(row, cellindex, descripcion));
    }

    private void saveCellData(int cellindex, String cellIndexDescription) {
        this.cellIndex = cellindex;
        this.cellIndexDescription = cellIndexDescription;
    }
/*
    public String getStringError() {
        return "[" + cellIndexName + "]:"+cellIndexDescription + ": "+errorMessage;
    }

    public void setErrorDetailedMessage(String errorMessage){
        this.errorMessage += "[" + cellIndexName + "]:"+cellIndexDescription + ": "+errorMessage;
    }
*/

    protected abstract void parseRow(Row row);


    public String toString(Row row){
        Iterator<Cell> cellIterator = row.cellIterator();
        String cellString = "";

        while (cellIterator.hasNext()){
            cellString += " | " + cellIterator.next().toString();
        }

        return cellString;
    }


}

