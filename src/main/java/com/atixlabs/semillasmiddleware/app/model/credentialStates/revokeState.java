package com.atixlabs.semillasmiddleware.app.model.credentialStates;

import com.atixlabs.semillasmiddleware.app.model.credential.constants.CredentialStatesCodes;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.Transient;

import javax.persistence.*;


@Entity
@Table
@Getter
@Setter
public class revokeState {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;

    private Long idCredential;

    private String stateName = CredentialStatesCodes.CREDENTIAL_REVOKE.getCode();

    private String reason;
}
