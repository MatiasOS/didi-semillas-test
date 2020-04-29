package com.atixlabs.semillasmiddleware.excelparser.app.constants;

import org.apache.tomcat.jni.Local;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum EntrepreneurshipQuestion implements CategoryQuestion {
    TYPE("TIPO DE EMPRENDIMIENTO"),
    ACTIVITY_START_DATE("FECHA DE INICIO / REINICIO"){
        @Override
        public Class<?> getDataType() {
            return LocalDate.class;
        }
    },
    MAIN_ACTIVITY("ACTIVIDAD PRINCIPAL"),
    NAME("NOMBRE EMPRENDIMIENTO"),
    ADDRESS("DIRECCION"),
    ACTIVITY_ENDING_DATE("FIN DE LA ACTIVIDAD") {
        @Override
        public Class<?> getDataType() {
            return LocalDate.class;
        }

        @Override
        public boolean isRequired() {
            return false;
        }
    };

    private String questionName;
    static final Map<String, EntrepreneurshipQuestion> questionsMap = Arrays.stream(values()).collect(Collectors.toMap(EntrepreneurshipQuestion::getQuestionName, p->p));

    EntrepreneurshipQuestion(String questionName) {
        this.questionName = questionName;
    }

    public static EntrepreneurshipQuestion get(String questionName){
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
