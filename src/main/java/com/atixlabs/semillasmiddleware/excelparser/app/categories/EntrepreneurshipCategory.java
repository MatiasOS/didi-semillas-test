package com.atixlabs.semillasmiddleware.excelparser.app.categories;

import com.atixlabs.semillasmiddleware.excelparser.app.constants.Categories;
import com.atixlabs.semillasmiddleware.excelparser.app.constants.DidiSyncStatus;
import com.atixlabs.semillasmiddleware.excelparser.app.constants.EntrepreneurshipQuestion;
import com.atixlabs.semillasmiddleware.excelparser.app.dto.AnswerDto;
import com.atixlabs.semillasmiddleware.excelparser.app.dto.AnswerRow;
import com.atixlabs.semillasmiddleware.excelparser.dto.ProcessExcelFileResult;
import com.atixlabs.semillasmiddleware.util.StringUtil;
import lombok.Getter;
import lombok.Setter;

import javax.swing.text.html.Option;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.atixlabs.semillasmiddleware.excelparser.app.constants.DidiSyncStatus.IS_MODIFICATION;

@Setter
@Getter

public class EntrepreneurshipCategory implements Category {

    private String categoryOriginalName;
    private Categories categoryName;

    private AnswerDto entrepreneurshipData;
    private AnswerDto activityDev;
    private AnswerDto workTime;
    private AnswerDto entryPerFortnight;
    private AnswerDto entryPerWeek;
    private AnswerDto entries;
    private AnswerDto exits;
    private AnswerDto entriesExitsRelationship;

    private AnswerDto type;
    private AnswerDto activityStartDate;
    private AnswerDto mainActivity;
    private AnswerDto name;
    private AnswerDto address;
    private AnswerDto activityEndingDate;
    private AnswerDto phone;
    private AnswerDto reset;
    private AnswerDto seniority;

    private AnswerDto outpatient;
    private AnswerDto fair;
    private AnswerDto storeOrHome;

    private AnswerDto daysPerWeek;
    private AnswerDto hoursPerWeek;

    private AnswerDto firstFortnight;
    private AnswerDto secondFortnight;
    private AnswerDto totalMonthlyEntry;

    private AnswerDto entryWeek1;
    private AnswerDto entryWeek2;
    private AnswerDto entryWeek3;
    private AnswerDto entryWeek4;
    private AnswerDto entryPerMonth;

    private AnswerDto exitRent;
    private AnswerDto exitWater;
    private AnswerDto exitElectricity;
    private AnswerDto exitShopping;
    private AnswerDto exitPhone;
    private AnswerDto exitTaxes;
    private AnswerDto exitTransport;
    private AnswerDto exitMaintenance;
    private AnswerDto exitEmployees;
    private AnswerDto exitOthers;
    private AnswerDto totalExit;

    private AnswerDto totalEntry;
    private AnswerDto totalExitRel;
    private AnswerDto entryExitRelationship;
    private AnswerDto entryExitRelationshipFortnight;
    private AnswerDto projection;
    private AnswerDto facebook;
    private AnswerDto photo;
    private AnswerDto isModification;


    public EntrepreneurshipCategory(String categoryUniqueName, Categories category) {

        this.entrepreneurshipData = new AnswerDto(EntrepreneurshipQuestion.ENTREPRENEURSHIP_DATA);
        this.activityDev = new AnswerDto(EntrepreneurshipQuestion.ACTIVITY_DEV);
        this.workTime = new AnswerDto(EntrepreneurshipQuestion.WORK_TIME);
        this.entryPerFortnight = new AnswerDto(EntrepreneurshipQuestion.ENTRY_PER_FORTNIGHT);
        this.entryPerWeek = new AnswerDto(EntrepreneurshipQuestion.ENTRY_PER_WEEK);
        this.entries = new AnswerDto(EntrepreneurshipQuestion.ENTRIES);
        this.exits = new AnswerDto(EntrepreneurshipQuestion.EXITS);
        this.entriesExitsRelationship = new AnswerDto(EntrepreneurshipQuestion.ENTRIES_EXITS_RELATIONSHIP);

        this.type = new AnswerDto(EntrepreneurshipQuestion.TYPE);
        this.activityStartDate = new AnswerDto(EntrepreneurshipQuestion.ACTIVITY_START_DATE);
        this.mainActivity = new AnswerDto(EntrepreneurshipQuestion.MAIN_ACTIVITY);
        this.name = new AnswerDto(EntrepreneurshipQuestion.NAME);
        this.address = new AnswerDto(EntrepreneurshipQuestion.ADDRESS);
        this.activityEndingDate = new AnswerDto(EntrepreneurshipQuestion.ACTIVITY_ENDING_DATE);
        this.phone = new AnswerDto(EntrepreneurshipQuestion.PHONE);
        this.reset = new AnswerDto(EntrepreneurshipQuestion.RESET);
        this.seniority = new AnswerDto(EntrepreneurshipQuestion.SENIORITY);

        this.outpatient = new AnswerDto(EntrepreneurshipQuestion.OUTPATIENT);
        this.fair = new AnswerDto(EntrepreneurshipQuestion.FAIR);
        this.storeOrHome = new AnswerDto(EntrepreneurshipQuestion.STORE_OR_HOME);
        this.daysPerWeek = new AnswerDto(EntrepreneurshipQuestion.DAYS_PER_WEEK);
        this.hoursPerWeek = new AnswerDto(EntrepreneurshipQuestion.HOURS_PER_WEEK);
        this.firstFortnight = new AnswerDto(EntrepreneurshipQuestion.FIRST_FORTNIGHT);
        this.secondFortnight = new AnswerDto(EntrepreneurshipQuestion.SECOND_FORTNIGHT);

        this.totalMonthlyEntry = new AnswerDto(EntrepreneurshipQuestion.TOTAL_MONTHLY_ENTRY);
        this.entryWeek1 = new AnswerDto(EntrepreneurshipQuestion.ENTRY_WEEK_1);
        this.entryWeek2 = new AnswerDto(EntrepreneurshipQuestion.ENTRY_WEEK_2);
        this.entryWeek3 = new AnswerDto(EntrepreneurshipQuestion.ENTRY_WEEK_3);
        this.entryWeek4 = new AnswerDto(EntrepreneurshipQuestion.ENTRY_WEEK_4);
        this.entryPerMonth = new AnswerDto(EntrepreneurshipQuestion.ENTRY_PER_MONTH);
        this.exitRent = new AnswerDto(EntrepreneurshipQuestion.EXIT_RENT);
        this.exitWater = new AnswerDto(EntrepreneurshipQuestion.EXIT_WATER);
        this.exitElectricity = new AnswerDto(EntrepreneurshipQuestion.EXIT_ELECTRICITY);
        this.exitShopping = new AnswerDto(EntrepreneurshipQuestion.EXIT_SHOPPING);
        this.exitPhone = new AnswerDto(EntrepreneurshipQuestion.EXIT_PHONE);
        this.exitTaxes = new AnswerDto(EntrepreneurshipQuestion.EXIT_TAXES);
        this.exitTransport = new AnswerDto(EntrepreneurshipQuestion.EXIT_TRANSPORT);
        this.exitMaintenance = new AnswerDto(EntrepreneurshipQuestion.EXIT_MAINTENANCE);
        this.exitEmployees = new AnswerDto(EntrepreneurshipQuestion.EXIT_EMPLOYEES);
        this.exitOthers = new AnswerDto(EntrepreneurshipQuestion.EXIT_OTHERS);
        this.totalExit = new AnswerDto(EntrepreneurshipQuestion.TOTAL_EXIT);
        this.totalEntry = new AnswerDto(EntrepreneurshipQuestion.TOTAL_ENTRY);
        this.totalExitRel = new AnswerDto(EntrepreneurshipQuestion.TOTAL_EXIT_REL);
        this.entryExitRelationship = new AnswerDto(EntrepreneurshipQuestion.ENTRY_EXIT_RELATIONSHIP);
        this.entryExitRelationshipFortnight = new AnswerDto(EntrepreneurshipQuestion.ENTRY_EXIT_RELATIONSHIP_FORTNIGHT);
        this.projection = new AnswerDto(EntrepreneurshipQuestion.PROJECTION);
        this.facebook = new AnswerDto(EntrepreneurshipQuestion.FACEBOOK);
        this.photo = new AnswerDto(EntrepreneurshipQuestion.PHOTO);

        this.categoryOriginalName = categoryUniqueName;
        this.categoryName = category;
        this.isModification = new AnswerDto(EntrepreneurshipQuestion.IS_MODIFICATION);
    }

    public void loadData(AnswerRow answerRow, ProcessExcelFileResult processExcelFileResult){
        String question = StringUtil.toUpperCaseTrimAndRemoveAccents(answerRow.getQuestion());
        EntrepreneurshipQuestion questionMatch = null;

        questionMatch = EntrepreneurshipQuestion.getEnumByStringValue(question);

        if (questionMatch==null)
            return;
        Optional<AnswerDto> optionalAnswer = getAnswerType(questionMatch, answerRow);
        optionalAnswer.ifPresent(param -> param.setAnswer(answerRow, processExcelFileResult));
    }

    private Optional<AnswerDto> getAnswerType(EntrepreneurshipQuestion questionMatch, AnswerRow answerRow){
        switch (questionMatch){
            //headers
            case ENTREPRENEURSHIP_DATA:
                answerRow.setAnswer("SUBCATEGORY");
                return Optional.of(this.entrepreneurshipData);
            case ACTIVITY_DEV:
                answerRow.setAnswer("SUBCATEGORY");
                return Optional.of(this.activityDev);
            case WORK_TIME:
                answerRow.setAnswer("SUBCATEGORY");
                return Optional.of(this.workTime);
            case ENTRY_PER_FORTNIGHT:
                answerRow.setAnswer("SUBCATEGORY");
                return Optional.of(this.entryPerFortnight);
            case ENTRY_PER_WEEK:
                answerRow.setAnswer("SUBCATEGORY");
                return Optional.of(this.entryPerWeek);
            case ENTRIES:
                answerRow.setAnswer("SUBCATEGORY");
                return Optional.of(this.entries);
            case EXITS:
                answerRow.setAnswer("SUBCATEGORY");
                return Optional.of(this.exits);
            case ENTRIES_EXITS_RELATIONSHIP:
                answerRow.setAnswer("SUBCATEGORY");
                return Optional.of(this.entriesExitsRelationship);
            //Questions
            case TYPE:
                return Optional.of(this.type);
            case ACTIVITY_START_DATE:
                //possible data: dd/mm/yyyy or only date: 2020
                return Optional.of(this.activityStartDate);
            case MAIN_ACTIVITY:
                return Optional.of(this.mainActivity);
            case NAME:
                return Optional.of(this.name);
            case ADDRESS:
                return Optional.of(this.address);
            //check final form
            case ACTIVITY_ENDING_DATE:
                return Optional.of(this.activityEndingDate);
            case PHONE:
                return Optional.of(this.phone);
            case RESET:
                return Optional.of(this.reset);
            case SENIORITY:
                return Optional.of(this.seniority);
            case OUTPATIENT:
                return Optional.of(this.outpatient);
            case FAIR:
                return Optional.of(this.fair);
            case STORE_OR_HOME:
                return Optional.of(this.storeOrHome);
            case DAYS_PER_WEEK:
                return Optional.of(this.daysPerWeek);
            case HOURS_PER_WEEK:
                return Optional.of(this.hoursPerWeek);
            case FIRST_FORTNIGHT:
                return Optional.of(this.firstFortnight);
            case SECOND_FORTNIGHT:
                return Optional.of(this.secondFortnight);
            case TOTAL_MONTHLY_ENTRY:
                return Optional.of(this.totalMonthlyEntry);
            case ENTRY_WEEK_1:
                return Optional.of(this.entryWeek1);
            case ENTRY_WEEK_2:
                return Optional.of(this.entryWeek2);
            case ENTRY_WEEK_3:
                return Optional.of(this.entryWeek3);
            case ENTRY_WEEK_4:
                return Optional.of(this.entryWeek4);
            case ENTRY_PER_MONTH:
                return Optional.of(this.entryPerMonth);
            case EXIT_RENT:
                return Optional.of(this.exitRent);
            case EXIT_WATER:
                return Optional.of(this.exitWater);
            case EXIT_ELECTRICITY:
                return Optional.of(this.exitElectricity);
            case EXIT_SHOPPING:
                return Optional.of(this.exitShopping);
            case EXIT_PHONE:
                return Optional.of(this.exitPhone);
            case EXIT_TAXES:
                return Optional.of(this.exitTaxes);
            case EXIT_TRANSPORT:
                return Optional.of(this.exitTransport);
            case EXIT_MAINTENANCE:
                return Optional.of(this.exitMaintenance);
            case EXIT_EMPLOYEES:
                return Optional.of(this.exitEmployees);
            case EXIT_OTHERS:
                return Optional.of(this.exitOthers);
            case TOTAL_EXIT:
                return Optional.of(this.totalExit);
            case TOTAL_ENTRY:
                return Optional.of(this.totalEntry);
            case TOTAL_EXIT_REL:
                return Optional.of(this.totalExitRel);
            case ENTRY_EXIT_RELATIONSHIP:
                return Optional.of(this.entryExitRelationship);
            case ENTRY_EXIT_RELATIONSHIP_FORTNIGHT:
                return Optional.of(this.entryExitRelationshipFortnight);
            case PROJECTION:
                return Optional.of(this.projection);
            case FACEBOOK:
                return Optional.of(this.facebook);
            case PHOTO:
                return Optional.of(this.photo);
            case IS_MODIFICATION:
                return Optional.of(this.isModification);
            default:
                return Optional.empty();
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
    public Integer getActivityStartDate(){
        return (Integer) this.activityStartDate.getAnswer();
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
    public Boolean getIsModification() { return getBooleanFromAnswer(this.isModification); }

    @Override
    public String toString() {
        return "EntrepreneurshipCategory{" +
                "categoryOriginalName='" + categoryOriginalName + '\'' +
                "entrepreneurshipData='" + entrepreneurshipData + '\'' +
                "activityDev='" + activityDev + '\'' +
                "workTime='" + workTime + '\'' +
                "entryPerFortnight='" + entryPerFortnight + '\'' +
                "entryPerWeek='" + entryPerWeek + '\'' +
                "entries='" + entries + '\'' +
                "entries='" + exits + '\'' +
                "entries='" + entriesExitsRelationship + '\'' +
                ", type=" + type +
                ", activityStartDate=" + activityStartDate +
                ", mainActivity=" + mainActivity +
                ", name=" + name +
                ", address=" + address +
                ", activityEndingDate=" + activityEndingDate +
                ", phone=" + phone +
                ", reset=" + reset +
                ", seniority=" + seniority +
                ", outpatient=" + outpatient +
                ", fair=" + fair +
                ", storeOrHome=" + storeOrHome +
                ", daysPerWeek=" + daysPerWeek +
                ", hoursPerWeek=" + hoursPerWeek +
                ", firstFortnight=" + firstFortnight +
                ", secondFortnight=" + secondFortnight +
                ", totalMonthlyEntry=" + totalMonthlyEntry +
                ", entryWeek1=" + entryWeek1 +
                ", entryWeek2=" + entryWeek2 +
                ", entryWeek3=" + entryWeek3 +
                ", entryWeek4=" + entryWeek4 +
                ", entryPerMonth=" + entryPerMonth +
                ", exitRent=" + exitRent +
                ", exitWater=" + exitWater +
                ", exitElectricity=" + exitElectricity +
                ", exitShopping=" + exitShopping +
                ", exitPhone=" + exitPhone +
                ", exitTaxes=" + exitTaxes +
                ", exitTransport=" + exitTransport +
                ", exitMaintenance=" + exitMaintenance +
                ", exitEmployees=" + exitEmployees +
                ", exitOthers=" + exitOthers +
                ", totalExit=" + totalExit +
                ", totalEntry=" + totalEntry +
                ", totalExitRel=" + totalExitRel +
                ", entryExitRelationship=" + entryExitRelationship +
                ", entryExitRelationshipFortnight=" + entryExitRelationshipFortnight +
                ", projection=" + projection +
                ", facebook=" + facebook +
                ", photo=" + photo +
                ", isModification=" + isModification +
                '}';
    }

    @Override
    public String getHtmlFromTemplate(String rowTemplate, String subCategoryTemplate, String subcategoryParam, String questionParam, String answerParam) {
        String html="";

        List<AnswerDto> answerDtos = this.getAnswersList();

        for (AnswerDto answer : answerDtos) {

            if (answer.getQuestion() != null) {// && answer.getAnswer() != null) {
                String ans = String.valueOf(Optional.ofNullable(answer.getAnswer()).orElse(""));
                if (ans.equals("SUBCATEGORY")) {
                    html += subCategoryTemplate
                            .replace(subcategoryParam, answer.getQuestion().getQuestionName());
                } else html += rowTemplate
                        .replace(questionParam, answer.getQuestion().getQuestionName())
                        .replace(answerParam, ans);
            }
        }
        return html;
    }

    @Override
    public List<AnswerDto> getAnswersList(){
        return Arrays.asList(
                entrepreneurshipData, type, name, address, phone, mainActivity, activityStartDate, reset, seniority,
                activityDev, outpatient, fair, storeOrHome,
                workTime, daysPerWeek, hoursPerWeek,
                entryPerFortnight, firstFortnight, secondFortnight, totalMonthlyEntry,
                entryPerWeek, entryWeek1, entryWeek2, entryWeek3, entryWeek4, totalMonthlyEntry,
                entries, entryPerMonth,
                exits, exitRent, exitWater, exitElectricity, exitShopping, exitPhone, exitTaxes, exitTransport, exitMaintenance, exitEmployees, exitOthers, totalExit,
                entriesExitsRelationship, totalEntry, totalExit, entryExitRelationship, entryExitRelationshipFortnight,
                projection, facebook, photo, isModification
        );
    }
}
