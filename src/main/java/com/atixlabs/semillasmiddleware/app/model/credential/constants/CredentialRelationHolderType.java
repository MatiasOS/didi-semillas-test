package com.atixlabs.semillasmiddleware.app.model.credential.constants;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum CredentialRelationHolderType {

    HOLDER("Titular"),
    KINSMAN("Familiar");

    private String code;

    CredentialRelationHolderType(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

    static final Map<String, CredentialRelationHolderType> codeMap = Arrays.stream(values()).collect(Collectors.toMap(CredentialRelationHolderType::getCode, p->p));
    public static CredentialRelationHolderType getEnumByStringValue(String codeString) {
        return codeMap.get(codeString);
    }

}
