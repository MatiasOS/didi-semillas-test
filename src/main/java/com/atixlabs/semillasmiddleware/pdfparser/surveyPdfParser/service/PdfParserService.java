package com.atixlabs.semillasmiddleware.pdfparser.surveyPdfParser.service;

import com.atixlabs.semillasmiddleware.excelparser.app.categories.Category;
import com.atixlabs.semillasmiddleware.excelparser.app.categories.EntrepreneurshipCategory;
import com.atixlabs.semillasmiddleware.excelparser.app.categories.PersonCategory;
import com.atixlabs.semillasmiddleware.excelparser.app.constants.Categories;
import com.atixlabs.semillasmiddleware.excelparser.app.dto.SurveyForm;
import com.atixlabs.semillasmiddleware.pdfparser.util.PDFUtil;
import com.atixlabs.semillasmiddleware.util.EmailTemplatesUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
@Slf4j
public class PdfParserService {

    private static final String TEMPLATE_NAME = "excell_survey_data.html";
    private static final String PDF_SUFFIX = "Encuesta_";
    private static final String CATEGORY_NAME_PARAM = "{{categoryName}}";
    private static final String TABLE_CONTENT_PARAM = "{{tableContent}}";
    private static final String QUESTION_PARAM = "{{question}}";
    private static final String ANSWER_PARAM = "{{answer}}";
    private static final String SUBCATEGORY_PARAM = "{{subCategory}}";

    private static final String rowStyle = "style=\"border: 1px solid #FFFFFF ; border-collapse: collapse; padding: 5px;\"\"";
    private static final String categoryStyle = "style=\"background-color: #A8A8A8; border: 1px solid #FFFFFF ; border-collapse: collapse; padding: 5px;\"\"";
    private String rowTemplate = "        <tr "+rowStyle+">\n" +
                                 "            <td style=\"width:100%\">{{question}}</td>\n" +
                                 "            <td style=\"width:100%\">{{answer}}</td>\n" +
                                 "        </tr>";

    private String subCategoryTemplate = "   <tr style=\"background-color:  #d8d8d8;\">\n" +
            "            <th colspan=\"2\">\n" +
            "              {{subCategory}}</th>\n" +
            "        </tr>";
    private String headerTemplate = "        <tr "+categoryStyle+">\n" +
                                    "            <th colspan=\"2\">\n" +
                                    "                <br />"+CATEGORY_NAME_PARAM+"\n" +
                                    "            </th>\n" +
                                    "        </tr>\n" +
                                    "        <th>PREGUNTA</th>\n" +
                                    "        <th>RESPUESTA</th>\n";

    public String generatePdfFromSurvey(SurveyForm surveyForm){
        log.info("Creating pdf from survey form");
        Stack<Category> categoriesStack = new Stack<>();
        //top down order
        fillStack(categoriesStack, Categories.getCodeList(), surveyForm);

        PersonCategory beneficiaryCategory = (PersonCategory) surveyForm.getCategoryByUniqueName(Categories.BENEFICIARY_CATEGORY_NAME.getCode(), null);

        String html = generateHtmlFromCategories(categoriesStack);
        String template = EmailTemplatesUtil.getTemplate(TEMPLATE_NAME).replace(TABLE_CONTENT_PARAM, html);

        return PDFUtil.createTemporaryPdf(PDF_SUFFIX+beneficiaryCategory.getFullName(), template);
    }

    private String generateHtmlFromCategories(Stack<Category> categoriesStack){
        String htmlStack = "";
        while (!categoriesStack.empty()){
            Category category = categoriesStack.pop();
            if (category == null) continue;
            log.info("Create html from category: "+category.getCategoryName());
            String header = headerTemplate.replace(CATEGORY_NAME_PARAM, category.getCategoryUniqueName());
            String rows = category.getHtmlFromTemplate(rowTemplate, subCategoryTemplate, SUBCATEGORY_PARAM, QUESTION_PARAM, ANSWER_PARAM);
            htmlStack += header+rows;
        }
        return htmlStack;
    }

    private void fillStack(Stack<Category> categoriesStack, List<Categories> categories, SurveyForm surveyForm){
        Collections.reverse(categories);
        categories.forEach( cat -> {
            for(int i = cat.getAmount(); i >= 1; i--) {
                String name = cat.getAmount() > 1 ? String.format("%s %d", cat.getCode(), i) : cat.getCode();
                Optional.ofNullable(surveyForm.getCategoryByUniqueName(name, null))
                        .ifPresent(categoriesStack::push);
            }
        });

    }
}
