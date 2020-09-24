package com.atixlabs.semillasmiddleware.excelparser.app.categories;

import com.atixlabs.semillasmiddleware.excelparser.app.constants.Categories;
import com.atixlabs.semillasmiddleware.excelparser.app.constants.FamiliarFinanceQuestion;
import com.atixlabs.semillasmiddleware.excelparser.app.dto.AnswerDto;
import com.atixlabs.semillasmiddleware.excelparser.app.dto.AnswerRow;
import com.atixlabs.semillasmiddleware.excelparser.dto.ProcessExcelFileResult;
import com.atixlabs.semillasmiddleware.util.StringUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Setter
@Getter

public class FamiliarFinanceCategory implements Category {
    private String categoryOriginalName;
    private Categories categoryName;

    private AnswerDto monthlyEntries;
    private AnswerDto monthlyExits;
    private AnswerDto entryEntrepreneurship;
    private AnswerDto entryApplicant;
    private AnswerDto entryFamily;
    private AnswerDto totalMonthlyEntry;
    private AnswerDto familiarSurplusFortnight;
    private AnswerDto exitFeeding;
    private AnswerDto exitGas;
    private AnswerDto exitEducation;
    private AnswerDto exitTransport;
    private AnswerDto exitWater;
    private AnswerDto exitElectricity;
    private AnswerDto exitPhone;
    private AnswerDto exitFit;
    private AnswerDto exitTaxes;
    private AnswerDto exitClothing;
    private AnswerDto exitRent;
    private AnswerDto exitOil;
    private AnswerDto exitCredits;
    private AnswerDto exitLeisure;
    private AnswerDto exitGambling;
    private AnswerDto exitTv;
    private AnswerDto exitInternet;
    private AnswerDto exitOthers;
    private AnswerDto totalMonthlyExit;
    private AnswerDto totalMonthlyEntry2;
    private AnswerDto totalMonthlyExit2;
    private AnswerDto totalMonthlyFamiliarSurplus;

    public FamiliarFinanceCategory(String categoryUniqueName, Categories category) {

        this.monthlyEntries = new AnswerDto(FamiliarFinanceQuestion.MONTHLY_ENTRIES);
        this.monthlyExits = new AnswerDto(FamiliarFinanceQuestion.MONTHLY_EXITS);
        this.entryEntrepreneurship = new AnswerDto(FamiliarFinanceQuestion.ENTRY_ENTREPRENEURSHIP);
        this.entryApplicant = new AnswerDto(FamiliarFinanceQuestion.ENTRY_APPLICANT);
        this.entryFamily = new AnswerDto(FamiliarFinanceQuestion.ENTRY_FAMILY);
        this.totalMonthlyEntry = new AnswerDto(FamiliarFinanceQuestion.TOTAL_MONTHLY_ENTRY);
        this.familiarSurplusFortnight = new AnswerDto(FamiliarFinanceQuestion.FAMILIAR_SURPLUS_FORTNIGHT);
        this.exitFeeding = new AnswerDto(FamiliarFinanceQuestion.EXIT_FEEDING);
        this.exitGas = new AnswerDto(FamiliarFinanceQuestion.EXIT_GAS);
        this.exitEducation = new AnswerDto(FamiliarFinanceQuestion.EXIT_EDUCATION);
        this.exitTransport = new AnswerDto(FamiliarFinanceQuestion.EXIT_TRANSPORT);
        this.exitWater = new AnswerDto(FamiliarFinanceQuestion.EXIT_WATER);
        this.exitElectricity = new AnswerDto(FamiliarFinanceQuestion.EXIT_ELECTRICITY);
        this.exitPhone = new AnswerDto(FamiliarFinanceQuestion.EXIT_PHONE);
        this.exitFit = new AnswerDto(FamiliarFinanceQuestion.EXIT_FIT);
        this.exitTaxes = new AnswerDto(FamiliarFinanceQuestion.EXIT_TAXES);
        this.exitClothing = new AnswerDto(FamiliarFinanceQuestion.EXIT_CLOTHING);
        this.exitRent = new AnswerDto(FamiliarFinanceQuestion.EXIT_RENT);;
        this.exitOil = new AnswerDto(FamiliarFinanceQuestion.EXIT_OIL);
        this.exitCredits = new AnswerDto(FamiliarFinanceQuestion.EXIT_CREDITS);
        this.exitLeisure = new AnswerDto(FamiliarFinanceQuestion.EXIT_LEISURE);
        this.exitGambling = new AnswerDto(FamiliarFinanceQuestion.EXIT_GAMBLING);
        this.exitTv = new AnswerDto(FamiliarFinanceQuestion.EXIT_TV);
        this.exitInternet = new AnswerDto(FamiliarFinanceQuestion.EXIT_INTERNET);
        this.exitOthers = new AnswerDto(FamiliarFinanceQuestion.EXIT_OTHERS);
        this.totalMonthlyExit = new AnswerDto(FamiliarFinanceQuestion.TOTAL_MONTHLY_EXIT);
        this.totalMonthlyEntry2 = new AnswerDto(FamiliarFinanceQuestion.TOTAL_MONTHLY_ENTRY_2);
        this.totalMonthlyExit2 = new AnswerDto(FamiliarFinanceQuestion.TOTAL_MONTHLY_EXIT_2);
        this.totalMonthlyFamiliarSurplus = new AnswerDto(FamiliarFinanceQuestion.TOTAL_MONTHLY_FAMILIAR_SURPLUS);

        this.categoryOriginalName = categoryUniqueName;
        this.categoryName = category;
    }

    public void loadData(AnswerRow answerRow, ProcessExcelFileResult processExcelFileResult) {
        String question = StringUtil.toUpperCaseTrimAndRemoveAccents(answerRow.getQuestion());
        FamiliarFinanceQuestion questionMatch = null;

        questionMatch = FamiliarFinanceQuestion.getEnumByStringValue(question);

        if (questionMatch == null)
            return;
        Optional<AnswerDto> optionalAnswer = getAnswerType(questionMatch, answerRow);
        optionalAnswer.ifPresent(param -> param.setAnswer(answerRow, processExcelFileResult));
    }

    private Optional<AnswerDto> getAnswerType(FamiliarFinanceQuestion questionMatch, AnswerRow answerRow) {
        switch (questionMatch) {
            case MONTHLY_ENTRIES:
                answerRow.setAnswer("SUBCATEGORY");
                return Optional.of(this.monthlyEntries);
            case MONTHLY_EXITS:
                answerRow.setAnswer("SUBCATEGORY");
                return Optional.of(this.monthlyExits);
            case ENTRY_ENTREPRENEURSHIP:
                return Optional.of(this.entryEntrepreneurship);
            case ENTRY_APPLICANT:
                return Optional.of(this.entryApplicant);
            case ENTRY_FAMILY:
                return Optional.of(this.entryFamily);
            case TOTAL_MONTHLY_ENTRY:
                return Optional.of(this.totalMonthlyEntry);
            case FAMILIAR_SURPLUS_FORTNIGHT:
                return Optional.of(this.familiarSurplusFortnight);
            case EXIT_FEEDING:
                return Optional.of(this.exitFeeding);
            case EXIT_GAS:
                return Optional.of(this.exitGas);
            case EXIT_EDUCATION:
                return Optional.of(this.exitEducation);
            case EXIT_TRANSPORT:
                return Optional.of(this.exitTransport);
            case EXIT_WATER:
                return Optional.of(this.exitWater);
            case EXIT_ELECTRICITY:
                return Optional.of(this.exitElectricity);
            case EXIT_PHONE:
                return Optional.of(this.exitPhone);
            case EXIT_FIT:
                return Optional.of(this.exitFit);
            case EXIT_TAXES:
                return Optional.of(this.exitTaxes);
            case EXIT_CLOTHING:
                return Optional.of(this.exitClothing);
            case EXIT_RENT:
                return Optional.of(this.exitRent);
            case EXIT_OIL:
                return Optional.of(this.exitOil);
            case EXIT_CREDITS:
                return Optional.of(this.exitCredits);
            case EXIT_LEISURE:
                return Optional.of(this.exitLeisure);
            case EXIT_GAMBLING:
                return Optional.of(this.exitGambling);
            case EXIT_TV:
                return Optional.of(this.exitTv);
            case EXIT_INTERNET:
                return Optional.of(this.exitInternet);
            case EXIT_OTHERS:
                return Optional.of(this.exitOthers);
            case TOTAL_MONTHLY_EXIT:
                return Optional.of(this.totalMonthlyExit);
            case TOTAL_MONTHLY_ENTRY_2:
                return Optional.of(this.totalMonthlyEntry2);
            case TOTAL_MONTHLY_EXIT_2:
                return Optional.of(this.totalMonthlyExit2);
            case TOTAL_MONTHLY_FAMILIAR_SURPLUS:
                return Optional.of(this.totalMonthlyFamiliarSurplus);
            default:
                return Optional.empty();
        }
    }

    @Override
    public boolean isValid(ProcessExcelFileResult processExcelFileResult) {
        return true;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean isRequired() {
        return false;
    }

    @Override
    public String getCategoryUniqueName() {
        return categoryOriginalName;
    }

    @Override
    public Categories getCategoryName() {
        return categoryName;
    }

    @Override
    public String toString() {
        return "FinancialSituationCategory{" +
                "categoryOriginalName='" + categoryOriginalName + '\'' +
                ", entryEntrepreneurship=" + entryEntrepreneurship +
                ", entryApplicant=" + entryApplicant +
                ", entryFamily=" + entryFamily +
                ", totalMonthlyEntry=" + totalMonthlyEntry +
                ", familiarSurplusFortnight=" + familiarSurplusFortnight +
                ", exitFeeding=" + exitFeeding +
                ", exitGas=" + exitGas +
                ", exitEducation=" + exitEducation +
                ", exitTransport=" + exitTransport +
                ", exitWater=" + exitWater +
                ", exitElectricity=" + exitElectricity +
                ", exitPhone=" + exitPhone +
                ", exitFit=" + exitFit +
                ", exitTaxes=" + exitTaxes +
                ", exitClothing=" + exitClothing +
                ", exitRent=" + exitRent +
                ", exitOil=" + exitOil +
                ", exitCredits=" + exitCredits +
                ", exitLeisure=" + exitLeisure +
                ", exitGambling=" + exitGambling +
                ", exitTv=" + exitTv +
                ", exitInternet=" + exitInternet +
                ", exitOthers=" + exitOthers +
                ", totalMonthlyExit=" + totalMonthlyExit +
                ", totalMonthlyEntry2=" + totalMonthlyEntry2 +
                ", totalMonthlyExit2=" + totalMonthlyExit2 +
                ", totalMonthlyFamiliarSurplus=" + totalMonthlyFamiliarSurplus +
                "}";
    }

    @Override
    public List<AnswerDto> getAnswersList() {
        return Arrays.asList(
                monthlyEntries, entryEntrepreneurship, entryApplicant, entryFamily, totalMonthlyEntry,
                monthlyExits, exitFeeding, exitGas, exitEducation, exitTransport, exitWater, exitElectricity, exitPhone, exitFit, exitTaxes, exitClothing, exitRent,
                exitOil, exitCredits, exitLeisure, exitGambling, exitTv, exitInternet, exitOthers, totalMonthlyExit,
                familiarSurplusFortnight,
                totalMonthlyEntry2, totalMonthlyExit2, totalMonthlyFamiliarSurplus
        );
    }
}