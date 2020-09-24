package com.atixlabs.semillasmiddleware.excelparser.app.constants;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum PersonQuestion implements CategoryQuestion {
    // Headers
    FIRST_LAST_NAME("NOMBRE Y APELLIDO"),
    INSTITUTION_LEVEL("NIVEL DE INSTRUCCION"),
    ADDRESS_DATA_1("DATOS DOMICILIO 1"),
    PHONE_DATA("DATOS TELEFONO"),
    REFERENCE_CONTACT("CONTACTO DE REFERENCIA"),
    EDUCATION_LEVEL("NIVEL EDUCATIVO"),

    //Questions
    NAME("NOMBRE"){
        @Override
        public boolean isRequired() {
            return true;
        }
    },
    SURNAME("APELLIDO"){
        @Override
        public boolean isRequired() {
            return true;
        }
    },
    ID_TYPE("TIPO DOCUMENTO"){
        @Override
        public boolean isRequired() {
            return true;
        }
    },
    ID_NUMBER("NUMERO DE DOCUMENTO"){
        @Override
        public boolean isRequired() {
            return true;
        }
        @Override
        public Class<?> getDataType() {
            return Long.class;
        }
    },
    GENDER("GENERO"){
        @Override
        public boolean isRequired() {
            return true;
        }
    },
    BIRTH_DATE("FECHA DE NACIMIENTO"){
        @Override
        public boolean isRequired() {
            return true;
        }

        @Override
        public Class<?> getDataType() {
            return LocalDate.class;
        }
    },
    RELATION("PARENTESCO"),
    OCCUPATION("OCUPACION"),
    STUDIES("ESTUDIA"),
    WORKS("TRABAJA"),
    AGE("EDAD"){
        @Override
        public boolean isRequired() {
            return true;
        }
        @Override
        public Class<?> getDataType() {
            return Long.class;
        }
    },
    CHILDREN_QUANTITY("CANTIDAD DE HIJOS"),
    RESIDENCE_TIME_IN_COUNTRY("TIEMPO DE RESIDENCIA EN EL PAIS"),
    FACEBOOK("FACEBOOK"),
    ADDRESS("DIRECCION"),
    BETWEEN_STREETS("ENTRE CALLES"),
    NEIGHBORHOOD("BARRIO"),
    ZONE("PARTIDO"),
    LOCALITY("LOCALIDAD"),
    ADDRESS_2("DATOS DOMICILIO 2"),
    REFERENCE_CONTACT_NAME("NOMBRE DE CONTACTO"),
    REFERENCE_CONTACT_SURNAME("APELLIDO DE CONTACTO"),
    REFERENCE_CONTACT_PHONE("TELEFONO"),
    NATIONALITY("NACIONALIDAD"),
    PRIMARY("PRIMARIA"),
    HIGH_SCHOOL("SECUNDARIA"),
    TERTIARY("TERCIARIO"),
    UNIVERSITY("UNIVERSITARIO"),
    OTHERS("OTRO"),
    LAST_STUDY_YEAR("ULTIMO ANO DE ESTUDIOS"),
    WORKSHOPS("TALLERES"),
    COURSES("CURSOS"),
    LAND_LINE("TELEFONO FIJO"),
    CELLPHONE("TELEFONO CELULAR"),
    CIVIL_STATUS("ESTADO CIVIL"),
    EMAIL("CORREO ELECTRONICO");


    private String questionName;
    static final Map<String, PersonQuestion> questionsMap = Arrays.stream(values()).collect(Collectors.toMap(PersonQuestion::getQuestionName, q->q));

    PersonQuestion(String questionName){
        this.questionName = questionName;
    }

    public static PersonQuestion getEnumByStringValue(String questionName) {
        return questionsMap.get(questionName);
    }

    public String getQuestionName(){
        return this.questionName;
    }

    public boolean isRequired(){
        return false;
    }

    public Class<?> getDataType() { return String.class; }
}
