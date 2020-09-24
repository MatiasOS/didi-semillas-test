package com.atixlabs.semillasmiddleware.app.didi.constant;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum DidiSyncStatus {
    SYNC_MISSING("sync-missing"),
    SYNC_OK("sync-ok"),
    SYNC_ERROR("sync-error"),
    SYNC_NEW("sync-new");

    private String code;
    DidiSyncStatus(String code) {this.code = code;}
    public String getCode(){return this.code;}

    static final Map<String, DidiSyncStatus> codeMap = Arrays.stream(values()).collect(Collectors.toMap(DidiSyncStatus::getCode, p->p));
    public static DidiSyncStatus getEnumByStringValue(String codeString) {
        return codeMap.get(codeString);
    }
}
