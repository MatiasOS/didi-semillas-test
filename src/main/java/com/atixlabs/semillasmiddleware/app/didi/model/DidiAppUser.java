package com.atixlabs.semillasmiddleware.app.didi.model;

import com.atixlabs.semillasmiddleware.app.didi.constant.DidiSyncStatus;
import com.atixlabs.semillasmiddleware.app.didi.dto.DidiAppUserDto;
import com.atixlabs.semillasmiddleware.app.model.beneficiary.Person;
import com.atixlabs.semillasmiddleware.app.model.credentialState.CredentialState;
import com.atixlabs.semillasmiddleware.security.model.AuditableEntity;
import com.atixlabs.semillasmiddleware.util.DateUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Getter
@Setter
@Table(name = "didi_app_user")
@Entity
@ToString
public class DidiAppUser extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    protected Long dni;
    protected String did;

    protected String syncStatus;

    protected boolean active;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    protected LocalDateTime dateOfRegistration;

    public DidiAppUser() {
        this.syncStatus = DidiSyncStatus.SYNC_MISSING.getCode();
    }

    public DidiAppUser(Long dni, String did, String syncStatus) {
        this.dni = dni;
        this.did = did;
        this.syncStatus = syncStatus;
    }

    public DidiAppUser(DidiAppUserDto didiAppUserDto) {
        this.dni = didiAppUserDto.getDni();
        this.did = didiAppUserDto.getDid();
        this.syncStatus = DidiSyncStatus.SYNC_MISSING.getCode();
        this.active = true;
        this.dateOfRegistration = DateUtil.getLocalDateTimeNow();
    }


}
