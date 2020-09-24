package com.atixlabs.semillasmiddleware.excelparser.app.categories;

import com.atixlabs.semillasmiddleware.excelparser.app.constants.Categories;
import com.atixlabs.semillasmiddleware.excelparser.app.constants.InterviewDataQuestion;
import com.atixlabs.semillasmiddleware.excelparser.app.constants.PatrimonialSituationQuestion;
import com.atixlabs.semillasmiddleware.excelparser.app.dto.AnswerDto;
import com.atixlabs.semillasmiddleware.excelparser.app.dto.AnswerRow;
import com.atixlabs.semillasmiddleware.excelparser.dto.ProcessExcelFileResult;
import com.atixlabs.semillasmiddleware.util.StringUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Setter
@Getter

public class InterviewDataCategory implements Category {
    private String categoryOriginalName;
    private Categories categoryName;

    private AnswerDto generalData;
    private AnswerDto solidarityGroup;
    private AnswerDto adviser;

    public InterviewDataCategory(String categoryUniqueName, Categories category) {
        this.generalData = new AnswerDto(InterviewDataQuestion.GENERAL_DATA);
        this.solidarityGroup = new AnswerDto(InterviewDataQuestion.SOLIDARITY_GROUP);
        this.adviser = new AnswerDto(InterviewDataQuestion.ADVISER);

        this.categoryOriginalName = categoryUniqueName;
        this.categoryName = category;
    }

    public void loadData(AnswerRow answerRow, ProcessExcelFileResult processExcelFileResult) {
        String question = StringUtil.toUpperCaseTrimAndRemoveAccents(answerRow.getQuestion());
        InterviewDataQuestion questionMatch = null;

        questionMatch = InterviewDataQuestion.getEnumByStringValue(question);

        if (questionMatch == null)
            return;
        Optional<AnswerDto> optionalAnswer = getAnswerType(questionMatch, answerRow);
        optionalAnswer.ifPresent(param -> param.setAnswer(answerRow, processExcelFileResult));
    }

    private Optional<AnswerDto> getAnswerType(InterviewDataQuestion questionMatch, AnswerRow answerRow){
        switch (questionMatch) {
            case GENERAL_DATA:
                answerRow.setAnswer("SUBCATEGORY");
                return Optional.of(this.generalData);
            case ADVISER:
                return Optional.of(this.adviser);
            case SOLIDARITY_GROUP:
                return Optional.of(this.solidarityGroup);
            default:
                return Optional.empty();
        }
    }

    @Override
    public boolean isValid(ProcessExcelFileResult processExcelFileResult) {
        List<AnswerDto> answers = new LinkedList<>();
        answers.add(this.solidarityGroup);
        answers.add(this.adviser);
        List<Boolean> validations = answers.stream().map(answerDto -> answerDto.isValid(processExcelFileResult, categoryOriginalName)).collect(Collectors.toList());
        return validations.stream().allMatch(v->v);
    }

    @Override
    public boolean isEmpty() { return false; }

    @Override
    public boolean isRequired() {
        return true;
    }

    @Override
    public String getCategoryUniqueName(){
        return categoryOriginalName;
    }

    @Override
    public Categories getCategoryName(){return categoryName;}

    @Override
    public String toString() {
        return "InterviewDataCategory{" +
                "categoryOriginalName='" + categoryOriginalName + '\'' +
                ", generalData=" + generalData +
                ", solidarityGroup=" + solidarityGroup +
                ", solidarityGroup=" + adviser;
    }
    @Override
    public List<AnswerDto> getAnswersList() {
        return Arrays.asList(generalData, solidarityGroup, adviser);
    }
}