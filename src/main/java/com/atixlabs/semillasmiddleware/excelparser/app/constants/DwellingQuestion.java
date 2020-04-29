package com.atixlabs.semillasmiddleware.excelparser.app.constants;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum DwellingQuestion implements CategoryQuestion {
    DWELLING_TYPE("VIVIENDA"),
    HOLDING_TYPE("TIPO DE TENENCIA"),
    DISTRICT("DISTRITO DE RESIDENCIA");

    private String questionName;
    static final Map<String, DwellingQuestion> questionsMap = Arrays.stream(values()).collect(Collectors.toMap(DwellingQuestion::getQuestionName, p->p));

    DwellingQuestion(String questionName) {
        this.questionName=questionName;
    }

    public static DwellingQuestion get(String questionName) {
        return questionsMap.get(questionName);
    }

    public String getQuestionName(){
        return this.questionName;
    }

    public boolean isRequired() {
        return true;
    }

    public Class<?> getDataType() { return String.class; }


}
