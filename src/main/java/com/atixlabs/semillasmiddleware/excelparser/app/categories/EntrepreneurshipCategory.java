package com.atixlabs.semillasmiddleware.excelparser.app.categories;

import com.atixlabs.semillasmiddleware.excelparser.app.constants.DwellingQuestion;
import com.atixlabs.semillasmiddleware.excelparser.app.constants.EntrepreneurshipQuestion;
import com.atixlabs.semillasmiddleware.excelparser.app.dto.AnswerDto;
import com.atixlabs.semillasmiddleware.excelparser.app.dto.AnswerRow;
import com.atixlabs.semillasmiddleware.excelparser.dto.ProcessExcelFileResult;
import com.atixlabs.semillasmiddleware.util.StringUtil;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
public class EntrepreneurshipCategory implements Category {

    AnswerDto type;
    AnswerDto activityStartDate;
    AnswerDto mainActivity;
    AnswerDto name;
    AnswerDto address;
    AnswerDto activityEndingDate;

    public EntrepreneurshipCategory() {
        this.type = new AnswerDto(EntrepreneurshipQuestion.TYPE);
        this.activityStartDate = new AnswerDto(EntrepreneurshipQuestion.ACTIVITY_START_DATE);
        this.mainActivity = new AnswerDto(EntrepreneurshipQuestion.MAIN_ACTIVITY);
        this.name = new AnswerDto(EntrepreneurshipQuestion.NAME);
        this.address = new AnswerDto(EntrepreneurshipQuestion.ADDRESS);
        this.activityEndingDate = new AnswerDto(EntrepreneurshipQuestion.ACTIVITY_ENDING_DATE);
    }

    public void loadData(AnswerRow answerRow, ProcessExcelFileResult processExcelFileResult){
        String question = StringUtil.toUpperCaseTrimAndRemoveAccents(answerRow.getQuestion());

        EntrepreneurshipQuestion questionMatch = EntrepreneurshipQuestion.get(question);

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
    public Category getData() {
        return this;
    }

    ;

    @Override
    public boolean isValid(ProcessExcelFileResult processExcelFileResult) {
        List<AnswerDto> answers = new LinkedList<>();
        answers.add(this.type);
        answers.add(this.activityStartDate);
        answers.add(this.mainActivity);
        answers.add(this.name);
        answers.add(this.address);
        answers.add(this.activityEndingDate);

        List<Boolean> validations = answers.stream().map(answerDto -> answerDto.isValid(processExcelFileResult, "Emprendimiento")).collect(Collectors.toList());
        return validations.stream().allMatch(v->v);
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
}
