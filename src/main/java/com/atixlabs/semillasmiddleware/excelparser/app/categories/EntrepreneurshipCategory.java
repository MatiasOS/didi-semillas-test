package com.atixlabs.semillasmiddleware.excelparser.app.categories;

import com.atixlabs.semillasmiddleware.excelparser.app.constants.Categories;
import com.atixlabs.semillasmiddleware.excelparser.app.constants.EntrepreneurshipQuestion;
import com.atixlabs.semillasmiddleware.excelparser.app.dto.AnswerDto;
import com.atixlabs.semillasmiddleware.excelparser.app.dto.AnswerRow;
import com.atixlabs.semillasmiddleware.excelparser.dto.ProcessExcelFileResult;
import com.atixlabs.semillasmiddleware.util.StringUtil;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter

public class EntrepreneurshipCategory implements Category {

    private String categoryOriginalName;
    private Categories categoryName;

    private AnswerDto type;
    private AnswerDto activityStartDate;
    private AnswerDto mainActivity;
    private AnswerDto name;
    private AnswerDto address;
    private AnswerDto activityEndingDate;

    public EntrepreneurshipCategory(String categoryOriginalName) {
        this.type = new AnswerDto(EntrepreneurshipQuestion.TYPE);
        this.activityStartDate = new AnswerDto(EntrepreneurshipQuestion.ACTIVITY_START_DATE);
        this.mainActivity = new AnswerDto(EntrepreneurshipQuestion.MAIN_ACTIVITY);
        this.name = new AnswerDto(EntrepreneurshipQuestion.NAME);
        this.address = new AnswerDto(EntrepreneurshipQuestion.ADDRESS);
        this.activityEndingDate = new AnswerDto(EntrepreneurshipQuestion.ACTIVITY_ENDING_DATE);

        this.categoryOriginalName = categoryOriginalName;
        this.categoryName = Categories.ENTREPRENEURSHIP_CATEGORY_NAME;
    }

    public void loadData(AnswerRow answerRow, ProcessExcelFileResult processExcelFileResult){
        String question = StringUtil.toUpperCaseTrimAndRemoveAccents(answerRow.getQuestion());
        EntrepreneurshipQuestion questionMatch = null;

        questionMatch = EntrepreneurshipQuestion.getEnumByStringValue(question);

        if (questionMatch==null)
            return;

        switch (questionMatch){
            case TYPE:
                this.type.setAnswer(answerRow, processExcelFileResult);
                break;
            case ACTIVITY_START_DATE:
                this.activityStartDate.setAnswer(answerRow, processExcelFileResult);
                break;
            case MAIN_ACTIVITY:
                this.mainActivity.setAnswer(answerRow, processExcelFileResult);
                break;
            case NAME:
                this.name.setAnswer(answerRow, processExcelFileResult);
                break;
            case ADDRESS:
                this.address.setAnswer(answerRow, processExcelFileResult);
                break;
            //check final form
            case ACTIVITY_ENDING_DATE:
                this.activityEndingDate.setAnswer(answerRow, processExcelFileResult);
                break;
        }
    }

    @Override
    public String getCategoryUniqueName(){
        return categoryOriginalName;
    }

    @Override
    public Categories getCategoryName(){return categoryName;}

    @Override
    public boolean isValid(ProcessExcelFileResult processExcelFileResult) {
        List<AnswerDto> answers = new LinkedList<>();
        answers.add(this.type);
        answers.add(this.activityStartDate);
        answers.add(this.mainActivity);
        answers.add(this.name);
        answers.add(this.address);
        answers.add(this.activityEndingDate);

        List<Boolean> validations = answers.stream().map(answerDto -> answerDto.isValid(processExcelFileResult, categoryOriginalName)).collect(Collectors.toList());
        return validations.stream().allMatch(v->v);
    }

    @Override
    public boolean isEmpty() {
        return type.answerIsEmpty() && activityStartDate.answerIsEmpty() && mainActivity.answerIsEmpty() && name.answerIsEmpty() && address.answerIsEmpty() && activityEndingDate.answerIsEmpty();
    }

    @Override
    public boolean isRequired() {
        return false;
    }

    public String getType(){
        return (String) this.type.getAnswer();
    }
    public LocalDate getActivityStartDate(){
        return (LocalDate) this.activityStartDate.getAnswer();
    }
    public String getMainActivity(){
        return (String) this.mainActivity.getAnswer();
    }
    public String getName(){
        return (String) this.name.getAnswer();
    }
    public String getAddress(){
        return (String) this.address.getAnswer();
    }
    public LocalDate getActivityEndingDate(){
        return (LocalDate) this.activityEndingDate.getAnswer();
    }

    @Override
    public String toString() {
        return "EntrepreneurshipCategory{" +
                "categoryOriginalName='" + categoryOriginalName + '\'' +
                ", type=" + type +
                ", activityStartDate=" + activityStartDate +
                ", mainActivity=" + mainActivity +
                ", name=" + name +
                ", address=" + address +
                ", activityEndingDate=" + activityEndingDate +
                '}';
    }
}
