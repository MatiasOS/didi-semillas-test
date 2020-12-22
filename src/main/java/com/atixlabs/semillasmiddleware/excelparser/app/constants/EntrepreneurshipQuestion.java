package com.atixlabs.semillasmiddleware.excelparser.app.constants;

import org.apache.tomcat.jni.Local;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum EntrepreneurshipQuestion implements CategoryQuestion {
    // headers
    ENTREPRENEURSHIP_DATA("DATOS EMPRENDIMIENTO"),
    ACTIVITY_DEV("DESARROLLO DE LA ACTIVIDAD"),
    WORK_TIME("TIEMPO DE TRABAJO"),
    ENTRY_PER_FORTNIGHT("INGRESOS POR QUINCENA"),
    ENTRY_PER_WEEK("INGRESOS POR SEMANA"),
    ENTRIES("INGRESOS"),
    EXITS("EGRESOS"),
    ENTRIES_EXITS_RELATIONSHIP("RELACION - IGRESOS / EGRESOS MES"),

    // questions
    TYPE("TIPO DE EMPRENDIMIENTO"),
    ACTIVITY_START_DATE("FECHA DE INICIO"){
        @Override
        public Class<?> getDataType() {
            return Integer.class;
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
    },
    PHONE("TELEFONO"),
    RESET("REINICIO"),
    SENIORITY("ANTIGUEDAD"),
    // Activity development
    OUTPATIENT("AMBULANTE"),
    FAIR("FERIA"),
    STORE_OR_HOME("LOCAL O CASA"),
    // Work time
    DAYS_PER_WEEK("DIAS POR SEMANA"),
    HOURS_PER_WEEK("HORAS POR SEMANA"),
    // Entry per fortnight
    FIRST_FORTNIGHT("QUINCENA 1"),
    SECOND_FORTNIGHT("QUINCENA 2"),
    TOTAL_MONTHLY_ENTRY("+TOTAL INGRESO MENSUAL"),
    // Entry per week
    ENTRY_WEEK_1("INGRESO SEMANA 1"),
    ENTRY_WEEK_2("INGRESO SEMANA 2"),
    ENTRY_WEEK_3("INGRESO SEMANA 3"),
    ENTRY_WEEK_4("INGRESO SEMANA 4"),
    ENTRY_PER_MONTH("INGRESOS - POR MES"),
    // Exit
    EXIT_RENT("EGRESOS- ALQUILER"),
    EXIT_WATER("EGRESOS- AGUA"),
    EXIT_ELECTRICITY("EGRESOS- LUZ"),
    EXIT_SHOPPING("EGRESOS- COMPRAS"),
    EXIT_PHONE("EGRESOS- TELEFONO"),
    EXIT_TAXES("EGRESOS- IMPUESTOS"),
    EXIT_TRANSPORT("EGRESOS- TRANSPORTE"),
    EXIT_MAINTENANCE("EGRESOS- MANTENIMIENTO"),
    EXIT_EMPLOYEES("EGRESOS- EMPLEADOS"),
    EXIT_OTHERS("EGRESOS- OTROS"),
    TOTAL_EXIT("+ TOTAL EGRESOS"),
    // Monthly entry / exit
    TOTAL_ENTRY("TOTAL INGRESOS"),
    TOTAL_EXIT_REL("TOTAL EGRESOS (-)"),
    ENTRY_EXIT_RELATIONSHIP("+ TOTAL RELACION - IGRESOS / EGRESOS MENSUAL"),
    ENTRY_EXIT_RELATIONSHIP_FORTNIGHT("RELACION - IGRESOS / EGRESOS QUINCENA"),
    // Others
    PROJECTION("¿PROYECCION?"),
    FACEBOOK("FACEBOOK DEL EMPRENDIMIENTO"),
    IS_MODIFICATION("¿ES MODIFICACION?") {
        @Override
        public boolean isRequired() { return true; }
    },
    PHOTO("FOTO DEL EMPRENDIMIENTO");


    private String questionName;
    static final Map<String, EntrepreneurshipQuestion> questionsMap = Arrays.stream(values()).collect(Collectors.toMap(EntrepreneurshipQuestion::getQuestionName, p->p));

    EntrepreneurshipQuestion(String questionName) {
        this.questionName = questionName;
    }

    public static EntrepreneurshipQuestion getEnumByStringValue(String questionName){
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
