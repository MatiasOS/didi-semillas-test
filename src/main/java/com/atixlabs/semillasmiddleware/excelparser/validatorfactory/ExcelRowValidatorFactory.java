package com.atixlabs.semillasmiddleware.excelparser.validatorfactory;

import com.atixlabs.semillasmiddleware.excelparser.row.ExcelRow;
import com.atixlabs.semillasmiddleware.excelparser.validator.RowValidator;
import com.atixlabs.semillasmiddleware.excelparser.validator.Validator;

import java.util.ArrayList;
import java.util.List;


/**
 * Create the chain of validators for each type of row
 */
public class ExcelRowValidatorFactory {

    public RowValidator<ExcelRow> getExcelRowValidator() {

        List<Validator<ExcelRow>> validators = new ArrayList<>();
        return new RowValidator<>(validators);
    }
}
