package com.atixlabs.semillasmiddleware.excelparser.app.constants;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum InterviewDataQuestion implements CategoryQuestion {
    //Headers
    GENERAL_DATA("DATOS GENERALES"),

    //Questions
    SOLIDARITY_GROUP("GRUPO SOLIDIARIO"),
    ADVISER("ASESOR/ASESORA");
    private String questionName;
    static final Map<String, InterviewDataQuestion> questionsMap = Arrays.stream(values()).collect(Collectors.toMap(InterviewDataQuestion::getQuestionName, p->p));

    InterviewDataQuestion(String questionName) { this.questionName = questionName; }

    public static InterviewDataQuestion getEnumByStringValue(String questionName){
        return questionsMap.get(questionName);
    }

    public String getQuestionName(){
        return this.questionName;
    }

    public boolean isRequired() {
        return true;
    }

    public Class<?> getDataType() {
        return String.class;
    }
}