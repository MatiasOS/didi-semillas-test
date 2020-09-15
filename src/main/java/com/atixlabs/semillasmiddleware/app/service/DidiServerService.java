package com.atixlabs.semillasmiddleware.app.service;

import com.atixlabs.semillasmiddleware.app.didi.dto.DidiAppUserDto;
import com.atixlabs.semillasmiddleware.app.didi.dto.DidiUpdateIdentityResponse;
import com.atixlabs.semillasmiddleware.app.didi.service.DidiAppUserService;
import com.atixlabs.semillasmiddleware.app.model.RetrofitBuilder;
import com.atixlabs.semillasmiddleware.app.model.identityValidationRequest.constant.RequestState;
import com.atixlabs.semillasmiddleware.app.model.identityValidationRequest.model.IdentityValidationRequest;
import com.atixlabs.semillasmiddleware.enpoint.DidiServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;

@Service
@Slf4j
public class DidiServerService {

    private DidiAppUserService didiAppUserService;
    private DidiServerEndpoint endpointInterface;

    public DidiServerService(DidiAppUserService didiAppUserService, @Value("${didi.server.url}") String DIDI_SERVER_URL){
        this.didiAppUserService = didiAppUserService;
        this.endpointInterface = (DidiServerEndpoint) RetrofitBuilder.endpointInterfaceBuilder(DidiServerEndpoint.class, DIDI_SERVER_URL);
    }

    public void updateIdentityRequest(IdentityValidationRequest identityValidationRequest) {
        String did = identityValidationRequest.getDid();
        RequestState state = identityValidationRequest.getRequestState();

        if (state.equals(RequestState.IN_PROGRESS)) return;

        log.info("Update identity validation request on didi, id["+identityValidationRequest.getId()+"], did["+did+"], state["+state+"]");
        Call<DidiUpdateIdentityResponse> callSync = endpointInterface.updateIdentityValidationRequest(did, state.name());

        try {
            Response<DidiUpdateIdentityResponse> response = callSync.execute();
            log.info("Didi update identity response: "+response.body());
            if (HttpStatus.valueOf(response.raw().code()).is4xxClientError()){
                log.error("Impossible to update identity credential on didi, message: "+response.body());
                throw new RuntimeException("Could not update credential on didi");
            }

            if (state.equals(RequestState.SUCCESS))
                didiAppUserService.registerNewAppUser(new DidiAppUserDto(identityValidationRequest.getDni(), did));

        } catch (IOException ex) {
            log.error("Error updating identity request ["+ex.getMessage()+"]");
        }
    }
}
