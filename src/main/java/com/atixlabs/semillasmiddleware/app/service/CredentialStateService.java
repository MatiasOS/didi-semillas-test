package com.atixlabs.semillasmiddleware.app.service;

import com.atixlabs.semillasmiddleware.app.exceptions.CredentialException;
import com.atixlabs.semillasmiddleware.app.model.credential.constants.CredentialStatesCodes;
import com.atixlabs.semillasmiddleware.app.model.credentialState.CredentialState;
import com.atixlabs.semillasmiddleware.app.repository.CredentialStateRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class CredentialStateService {

    private CredentialStateRepository credentialStateRepository;

    @Autowired
    public CredentialStateService(CredentialStateRepository credentialStateRepository){
        this.credentialStateRepository = credentialStateRepository;
    }

    public Optional<CredentialState> getCredentialRevokeState() throws CredentialException {

        Optional<CredentialState> opStateRevoke = credentialStateRepository.findByStateName(CredentialStatesCodes.CREDENTIAL_REVOKE.getCode());
        if (!opStateRevoke.isPresent()) {
            throw new CredentialException(String.format("Cant't obtain Credential State 'REVOKE' (code:%s)", CredentialStatesCodes.CREDENTIAL_REVOKE.getCode()));
        }
        return opStateRevoke;
    }

    public Optional<CredentialState> getCredentialActiveState() throws CredentialException {

        Optional<CredentialState> opStateActive = credentialStateRepository.findByStateName(CredentialStatesCodes.CREDENTIAL_ACTIVE.getCode());
        if (!opStateActive.isPresent()) {
            throw new CredentialException(String.format("Cant't obtain Credential State 'ACTIVE' (code:%s)", CredentialStatesCodes.CREDENTIAL_ACTIVE.getCode()));
        }
        return opStateActive;
    }

    public CredentialState getCredentialPendingDidiState() throws CredentialException {

        Optional<CredentialState> opStateActive = credentialStateRepository.findByStateName(CredentialStatesCodes.PENDING_DIDI.getCode());
        if (!opStateActive.isPresent()) {
            throw new CredentialException(String.format("Cant't obtain Credential State 'PENDING_DIDI' (code:%s)", CredentialStatesCodes.PENDING_DIDI.getCode()));
        }
        return opStateActive.get();
    }

    public CredentialState getCredentialState(String code){
        Optional<CredentialState> opStateActive = credentialStateRepository.findByStateName(code);
        if (!opStateActive.isPresent()) {
            throw new CredentialException(String.format("Cant't obtain Credential State (code:%s)", code));
        }
        return opStateActive.get();
    }

}
