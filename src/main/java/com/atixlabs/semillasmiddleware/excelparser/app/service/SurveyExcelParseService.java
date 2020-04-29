package com.atixlabs.semillasmiddleware.excelparser.app.service;

import com.atixlabs.semillasmiddleware.app.service.CredentialService;
import com.atixlabs.semillasmiddleware.excelparser.app.categories.AnswerCategoryFactory;
import com.atixlabs.semillasmiddleware.excelparser.app.categories.Category;
import com.atixlabs.semillasmiddleware.excelparser.app.dto.AnswerRow;
import com.atixlabs.semillasmiddleware.excelparser.app.dto.SurveyForm;
import com.atixlabs.semillasmiddleware.excelparser.dto.ProcessExcelFileResult;
import com.atixlabs.semillasmiddleware.excelparser.app.exception.InvalidCategoryException;
import com.atixlabs.semillasmiddleware.excelparser.exception.InvalidRowException;
import com.atixlabs.semillasmiddleware.excelparser.service.ExcelParseService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class SurveyExcelParseService extends ExcelParseService {

    /**
     * Levanta el archivo y lee linea por linea
     *
     * @param row
     * @return
     * @throws FileNotFoundException
     */

    @Autowired
    private CredentialService credentialService;

    private AnswerRow answerRow;
    private AnswerCategoryFactory answerCategoryFactory;
    private SurveyForm currentForm;
    private List<SurveyForm> surveyFormList;


    public SurveyExcelParseService(){
        resetFormRelatedVariables();
    }

    public void resetFormRelatedVariables(){
        if (answerCategoryFactory == null)
            answerCategoryFactory = new AnswerCategoryFactory();
        if (currentForm == null)
            currentForm = new SurveyForm();
        if (surveyFormList == null)
            surveyFormList = new ArrayList<>();
        answerRow = null;
    }

    public void clearFormRelatedVariables(){
        currentForm = null;
        answerCategoryFactory = null;
        surveyFormList.clear();
        surveyFormList = null;
    }

    @Override
    public ProcessExcelFileResult processRow(Row currentRow, boolean hasNext, ProcessExcelFileResult processExcelFileResult){

        resetFormRelatedVariables();

        try {
            answerRow = new AnswerRow(currentRow);
        } catch (InvalidRowException e) {
            processExcelFileResult.addRowError(currentRow.getRowNum(), e.toString());
        }



        if (answerRow != null) {
            if(!answerRow.isEmpty(currentRow)){

                processExcelFileResult.addTotalReadRow();
                if (answerRow.hasFormKeyValues()) {
                    //processExcelFileResult.addTotalValidRows();

                    if (!currentForm.isRowFromSameForm(answerRow)) {
                        endOfFormHandler(processExcelFileResult);
                        currentForm = new SurveyForm(answerRow);
                        answerCategoryFactory = new AnswerCategoryFactory();
                    }

                    addCategoryDataIntoForm(answerRow, processExcelFileResult);
                    log.info("OK:" + answerRow.toString());
                }
                else
                    processExcelFileResult.addEmptyRow();
            }

        }
        if(!hasNext)
            endOfFileHandler(processExcelFileResult);

        return processExcelFileResult;
    }

    private void addCategoryDataIntoForm(AnswerRow answerRow, ProcessExcelFileResult processExcelFileResult){

        try {
            Category category = answerCategoryFactory.get(answerRow.getCategory());
            category.loadData(answerRow, processExcelFileResult);
            currentForm.addCategory(category);
        }
        catch ( InvalidCategoryException e){
            return;
        }
        catch (Exception e) {
            processExcelFileResult.addRowError(answerRow.getRowNum(), e.toString());
        }
    }

    private void endOfFormHandler(ProcessExcelFileResult processExcelFileResult){
        log.info("endOfFormHandler -> add form to surveyFormList");
        processExcelFileResult.addTotalProcessedForms();
        surveyFormList.add(currentForm);
    }
    private void endOfFileHandler(ProcessExcelFileResult processExcelFileResult){
        this.endOfFormHandler(processExcelFileResult);
        log.info("endOfFileHandler -> checking errors and building credentials");

        boolean allFormValid = true;

        for (SurveyForm surveyForm : surveyFormList) {
            if (!surveyForm.isValid(processExcelFileResult))
                allFormValid = false;
        }

        if(allFormValid) {
            log.info("endOfFileHandler -> all forms are ok: building credentials");
            //for (SurveyForm surveyForm : surveyFormList) {
            //        credentialService.buildAllCredentialsFromForm(surveyForm);
            //}
        }
        else
            log.info("endOfFileHandler -> there are forms with errors: stopping import");

        //todo: rows with multiple errors must be considered in next sprint
        processExcelFileResult.setTotalValidRows(processExcelFileResult.getTotalReadRows() - processExcelFileResult.getTotalErrorsRows());
        clearFormRelatedVariables();
    }
}
