package com.atixlabs.semillasmiddleware.excelparser.app.constants;

import com.atixlabs.semillasmiddleware.app.model.beneficiary.Person;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum PersonType {
    BENEFICIARY("BENEFICIARIO"),
    CHILD("HIJO"),
    SPOUSE("CONYUGE"),
    OTHER_KINSMAN("OTRO MIEMBRO DE LA FAMILIA");

    private String type;
    PersonType(String type) {
        this.type = type;
    }

    public String getType(){
        return this.type;
    }

    static final Map<String, PersonType> elementMap = Arrays.stream(values()).collect(Collectors.toMap(PersonType::getType, p->p));

    public static PersonType get(String type) {
        return elementMap.get(type);
    }
}
