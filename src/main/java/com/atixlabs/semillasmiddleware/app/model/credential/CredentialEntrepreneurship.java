package com.atixlabs.semillasmiddleware.app.model.credential;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@PrimaryKeyJoinColumn(referencedColumnName="id")
public class CredentialEntrepreneurship extends Credential {

    private String entrepreneurshipType; //TODO enum or new class?

    private LocalDateTime startActivity;

    private String mainActivity;

    private String nameEntrepreneurship;

    private String addressEntrepreneurship;

    private String creditState; //TODO is into super class??

    private LocalDateTime endActivity;

}