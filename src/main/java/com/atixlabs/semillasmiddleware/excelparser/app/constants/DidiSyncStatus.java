package com.atixlabs.semillasmiddleware.excelparser.app.constants;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum DidiSyncStatus implements CategoryQuestion {
    //Header
    DESCRIPTION("DESCRIPCION DE MATERIALES"),
    DWELLING_CONDITION("CONDICIONES DE VIVIENDA"),

    BRICK("LADRILLO"),
    LOCK("CHAPA"),
    WOOD("MADERA"),
    PAPERBOARD("CARTON"),
    DISTRICT("DISTRITO DE RESIDENCIA"){
        @Override
        public boolean isRequired() {
            return true;
        }
    },
    HOLDING_TYPE("TIPO DE TENENCIA"){
        @Override
        public boolean isRequired() {
            return true;
        }
    },
    DWELLING_TYPE("VIVIENDA"){
        @Override
        public boolean isRequired() {
            return true;
        }
    },
    LIGHT_INSTALLATION("INSTALACION DE LUZ"),
    GENERAL_CONDITIONS("CONDICIONES GRALES"),
    NEIGHBORHOOD_TYPE("TIPO DE BARRIO"),
    IS_MODIFICATION("¿ES MODIFICACION?") {
        @Override
        public boolean isRequired() { return true; }
    },
    BASIC_SERVICES("SERVICIOS BASICOS"),
    GAS("RED DE GAS"),
    CARAFE("GARRAFA"),
    WATER("RED DE AGUA"),
    WATTER_WELL("POZO/ BOMBA"),
    ANTIQUITY("¿DESDE QUE ANO VIVIS EN ESTA VIVIENDA?"){
        @Override
        public Class<?> getDataType() {
            return Long.class;
        }
    },
    NUMBER_OF_ENVIRONMENTS("CANTIDAD DE AMBIENTES"){
        @Override
        public Class<?> getDataType() {
            return Long.class;
        }
    },
    RENTAL("MONTO ALQUILER"){
        @Override
        public Class<?> getDataType() {
            return Long.class;
        }
    };

    private String questionName;
    static final Map<String, DidiSyncStatus> questionsMap = Arrays.stream(values()).collect(Collectors.toMap(DidiSyncStatus::getQuestionName, p->p));

    DidiSyncStatus(String questionName) {
        this.questionName=questionName;
    }

    public static DidiSyncStatus getEnumByStringValue(String questionName) {
        return questionsMap.get(questionName);
    }

    public String getQuestionName(){
        return this.questionName;
    }

    public boolean isRequired() {
        return false;
    }

    public Class<?> getDataType() { return String.class; }


}
