package com.atixlabs.semillasmiddleware.excelparser.app.categories;

import com.atixlabs.semillasmiddleware.excelparser.app.constants.Categories;
import com.atixlabs.semillasmiddleware.excelparser.app.constants.DidiSyncStatus;
import com.atixlabs.semillasmiddleware.excelparser.app.dto.AnswerDto;
import com.atixlabs.semillasmiddleware.excelparser.app.dto.AnswerRow;
import com.atixlabs.semillasmiddleware.excelparser.dto.ProcessExcelFileResult;
import com.atixlabs.semillasmiddleware.util.StringUtil;
import org.checkerframework.checker.units.qual.A;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DwellingCategory implements Category {

    String categoryOriginalName;
    private Categories categoryName = Categories.DWELLING_CATEGORY_NAME;

    AnswerDto description;
    AnswerDto brick; //ladrillo
    AnswerDto lock; //chapa
    AnswerDto wood; //madera
    AnswerDto paperBoard; //carton
    AnswerDto district; //distrito de residencia
    AnswerDto dwellingCondition; //condiciones de vivienda
    AnswerDto holdingType; //tipo de tenencia
    AnswerDto dwellingType; //vivienda
    AnswerDto lightInstallation; //instalacion de luz
    AnswerDto generalConditions; //condiciones generales
    AnswerDto neighborhoodType; //tipo de barrio
    AnswerDto basicServicies; //servicios basicos
    AnswerDto isModification; //es modificacion
    AnswerDto gas; //red de gas
    AnswerDto carafe; //garrafa
    AnswerDto water; //red de agua
    AnswerDto watterWell; //pozo / bomba
    AnswerDto antiquity; //antiguedad
    AnswerDto numberOfEnvironments; //cantidad de ambientes
    AnswerDto rental; //monto alquiler

    public DwellingCategory(String categoryUniqueName, Categories category){
        this.description = new AnswerDto(DidiSyncStatus.DESCRIPTION);
        this.brick= new AnswerDto(DidiSyncStatus.BRICK);
        this.lock= new AnswerDto(DidiSyncStatus.LOCK);
        this.wood= new AnswerDto(DidiSyncStatus.WOOD);
        this.paperBoard= new AnswerDto(DidiSyncStatus.PAPERBOARD);
        this.district= new AnswerDto(DidiSyncStatus.DISTRICT);
        this.dwellingCondition= new AnswerDto(DidiSyncStatus.DWELLING_CONDITION);
        this.holdingType= new AnswerDto(DidiSyncStatus.HOLDING_TYPE);
        this.dwellingType= new AnswerDto(DidiSyncStatus.DWELLING_TYPE);
        this.lightInstallation= new AnswerDto(DidiSyncStatus.LIGHT_INSTALLATION);
        this.generalConditions= new AnswerDto(DidiSyncStatus.GENERAL_CONDITIONS);
        this.neighborhoodType= new AnswerDto(DidiSyncStatus.NEIGHBORHOOD_TYPE);
        this.basicServicies= new AnswerDto(DidiSyncStatus.BASIC_SERVICES);
        this.isModification = new AnswerDto(DidiSyncStatus.IS_MODIFICATION);
        this.gas= new AnswerDto(DidiSyncStatus.GAS);
        this.carafe= new AnswerDto(DidiSyncStatus.CARAFE);
        this.water= new AnswerDto(DidiSyncStatus.WATER);
        this.watterWell= new AnswerDto(DidiSyncStatus.WATTER_WELL);
        this.antiquity= new AnswerDto(DidiSyncStatus.ANTIQUITY);
        this.numberOfEnvironments= new AnswerDto(DidiSyncStatus.NUMBER_OF_ENVIRONMENTS);
        this.rental= new AnswerDto(DidiSyncStatus.RENTAL);

        this.categoryOriginalName = categoryUniqueName;
    }

    public void loadData(AnswerRow answerRow, ProcessExcelFileResult processExcelFileResult) {
        String question = StringUtil.toUpperCaseTrimAndRemoveAccents(answerRow.getQuestion());

        DidiSyncStatus questionMatch = DidiSyncStatus.getEnumByStringValue(question);
        if(questionMatch==null)
            return;
        Optional<AnswerDto> optionalAnswer = getAnswerType(questionMatch, answerRow);
        optionalAnswer.ifPresent(param -> param.setAnswer(answerRow, processExcelFileResult));
    }

    public Optional<AnswerDto> getAnswerType(DidiSyncStatus questionMatch, AnswerRow answerRow) {
        switch (questionMatch) {
            case DESCRIPTION:
                answerRow.setAnswer("SUBCATEGORY");
                return Optional.of(this.description);
            case DWELLING_CONDITION:
                answerRow.setAnswer("SUBCATEGORY");
                return Optional.of(this.dwellingCondition);
            case BRICK:
                return Optional.of(this.brick);
            case LOCK:
                return Optional.of(this.lock);
            case WOOD:
                return Optional.of(this.wood);
            case PAPERBOARD:
                return Optional.of(this.paperBoard);
            case DISTRICT:
                return Optional.of(this.district);
            case HOLDING_TYPE:
                return Optional.of(this.holdingType);
            case DWELLING_TYPE:
                return Optional.of(this.dwellingType);
            case LIGHT_INSTALLATION:
                return Optional.of(this.lightInstallation);
            case GENERAL_CONDITIONS:
                return Optional.of(this.generalConditions);
            case NEIGHBORHOOD_TYPE:
                return Optional.of(this.neighborhoodType);
            case BASIC_SERVICES:
                return Optional.of(this.basicServicies);
            case IS_MODIFICATION:
                return Optional.of(this.isModification);
            case GAS:
                return Optional.of(this.gas);
            case CARAFE:
                return Optional.of(this.carafe);
            case WATER:
                return Optional.of(this.water);
            case WATTER_WELL:
                return Optional.of(this.watterWell);
            case ANTIQUITY:
                return Optional.of(this.antiquity);
            case NUMBER_OF_ENVIRONMENTS:
                return Optional.of(this.numberOfEnvironments);
            case RENTAL:
                return Optional.of(this.rental);
            default:
                return Optional.empty();

        }
    }

    @Override
    public  String getCategoryUniqueName(){
        return categoryOriginalName;
    }

    @Override
    public Categories getCategoryName(){return categoryName;}

    @Override
    public boolean isValid(ProcessExcelFileResult processExcelFileResult) {
        List<AnswerDto> answers = new LinkedList<>();
        answers.add(brick);
        answers.add(this.lock);
        answers.add(this.wood);
        answers.add(this.paperBoard);
        answers.add(this.district);
        answers.add(this.dwellingCondition);
        answers.add(this.holdingType);
        answers.add(this.dwellingType);
        answers.add(this.lightInstallation);
        answers.add(this.generalConditions);
        answers.add(this.neighborhoodType);
        answers.add(this.basicServicies);
        answers.add(this.isModification);
        answers.add(this.gas);
        answers.add(this.carafe);
        answers.add(this.water);
        answers.add(this.watterWell);
        answers.add(this.antiquity);
        answers.add(this.numberOfEnvironments);
        answers.add(this.rental);

        List<Boolean> validations = answers.stream().map(answerDto -> answerDto.isValid(processExcelFileResult, categoryOriginalName)).collect(Collectors.toList());
        return validations.stream().allMatch(v->v);
    }

    @Override
    public boolean isEmpty(){
        return brick.answerIsEmpty() && lock.answerIsEmpty() && wood.answerIsEmpty() && paperBoard.answerIsEmpty()
                && district.answerIsEmpty() && dwellingCondition.answerIsEmpty()
                && holdingType.answerIsEmpty() && dwellingType.answerIsEmpty()
                && lightInstallation.answerIsEmpty() && generalConditions.answerIsEmpty()
                && neighborhoodType.answerIsEmpty() && basicServicies.answerIsEmpty()
                && isModification.answerIsEmpty()
                && gas.answerIsEmpty() && carafe.answerIsEmpty() && water.answerIsEmpty()
                && watterWell.answerIsEmpty() && antiquity.answerIsEmpty()
                && numberOfEnvironments.answerIsEmpty() && rental.answerIsEmpty();
    }

    @Override
    public boolean isRequired() {
        return false;
    }

    public String getDwellingType(){
        return (String) this.dwellingType.getAnswer();
    }

    public String getHoldingType(){
        return (String) this.holdingType.getAnswer();
    }

    public String getDistrict(){
        return (String) this.district.getAnswer();
    }

    public Boolean getBrick() {
        return getBooleanFromAnswer(this.brick);
    }

    public Boolean getLock() {
        return getBooleanFromAnswer(this.lock);
    }

    public Boolean getWood() {
        return getBooleanFromAnswer(this.wood);
    }

    public Boolean getPaperBoard() {
        return getBooleanFromAnswer(this.paperBoard);
    }

    public String getLightInstallation() {
        return (String) this.lightInstallation.getAnswer();
    }

    public String getGeneralConditions() {
        return (String) this.generalConditions.getAnswer();
    }

    public String getNeighborhoodType() {
        return (String) this.neighborhoodType.getAnswer();
    }

    public Boolean getGas() {
        return getBooleanFromAnswer(this.gas);
    }

    public Boolean getCarafe() {
        return getBooleanFromAnswer(this.carafe);
    }

    public Boolean getWater() {
        return getBooleanFromAnswer(this.water);
    }

    public Boolean getWatterWell() {
        return getBooleanFromAnswer(this.watterWell);
    }

    public Boolean isModification() { return getBooleanFromAnswer(this.isModification); }

    public Integer getAntiquity() {
        if(this.antiquity.getAnswer() == null) return null;
        return ((Long) this.antiquity.getAnswer()).intValue();
    }

    public Integer getNumberOfEnvironments() {
        if(this.numberOfEnvironments.getAnswer() == null) return null;
        return ((Long) this.numberOfEnvironments.getAnswer()).intValue();
    }

    public Long getRental() {
        if(this.rental.getAnswer() == null) return null;
        return (Long) this.rental.getAnswer();
    }

    @Override
    public List<AnswerDto> getAnswersList(){
        return Arrays.asList( description, brick,  lock,  wood,  paperBoard,  district,  dwellingCondition,  holdingType,
                dwellingType,  lightInstallation,  generalConditions,  neighborhoodType,  basicServicies, isModification, gas,
                carafe,  water,  watterWell,  antiquity,  numberOfEnvironments,  rental);
    }

    @Override
    public String toString() {
        return "DwellingCategory{" +
                "categoryOriginalName='" + categoryOriginalName + '\'' +
                ", categoryName=" + categoryName +
                ", brick=" + brick +
                ", lock=" + lock +
                ", wood=" + wood +
                ", paperBoard=" + paperBoard +
                ", district=" + district +
                ", dwellingCondition=" + dwellingCondition +
                ", holdingType=" + holdingType +
                ", dwellingType=" + dwellingType +
                ", lightInstallation=" + lightInstallation +
                ", generalConditions=" + generalConditions +
                ", neighborhoodType=" + neighborhoodType +
                ", basicServicies=" + basicServicies +
                ", isModification=" + isModification +
                ", gas=" + gas +
                ", carafe=" + carafe +
                ", water=" + water +
                ", watterWell=" + watterWell +
                ", antiquity=" + antiquity +
                ", numberOfEnvironments=" + numberOfEnvironments +
                ", rental=" + rental +
                '}';
    }
}
