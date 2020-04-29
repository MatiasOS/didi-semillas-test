package com.atixlabs.semillasmiddleware.excelparser.app.categories;

import com.atixlabs.semillasmiddleware.excelparser.app.constants.CategoryQuestion;
import com.atixlabs.semillasmiddleware.excelparser.app.dto.AnswerRow;
import com.atixlabs.semillasmiddleware.excelparser.dto.ProcessExcelFileResult;
import org.springframework.beans.factory.annotation.Autowired;


public interface Category {

     void loadData(AnswerRow answerRow, ProcessExcelFileResult processExcelFileResult);

     Category getData();

     boolean isValid(ProcessExcelFileResult processExcelFileResult);
}
