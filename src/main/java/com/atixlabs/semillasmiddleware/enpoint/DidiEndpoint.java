package com.atixlabs.semillasmiddleware.enpoint;

import com.atixlabs.semillasmiddleware.app.didi.dto.*;
import retrofit2.Call;
import retrofit2.http.*;

public interface DidiEndpoint {

    @POST("user/login")
    Call<DidiAuthResponse> getAuthToken(@Body DidiAuthRequestBody didiAuthRequestBody);

    @POST("Cert/")
    @FormUrlEncoded
    Call<DidiCreateCredentialResponse> createCertificate(
            @Header("token") String token,
            @Field("templateId") String templateId,
            @Field("split") boolean split,
            @Field("data") DidiCredentialData didiCredentialData);

    @POST("Cert/{credential_id}/emmit")
    Call<DidiEmmitCredentialResponse> emmitCertificate(
            @Header("token") String token,
            @Path("credential_id") String credential_id);

    //workaround para poder enviar DELETE con body
    //https://stackoverflow.com/questions/48768586/retrofit-delete-json
    //http://192.81.218.211:3500/api/1.0/didi_issuer/Cert/5ec5950dd9e6e10f342ba959
    //@DELETE("Cert/{credential_id}")
    @HTTP(method = "DELETE", path = "Cert/{credential_id}", hasBody = true)
    Call<DidiEmmitCredentialResponse> deleteCertificate(
            @Header("token") String token,
            @Path("credential_id") String credential_id,
            @Body String reason);


    @GET("Cert/all")
    Call<DidiGetAllCredentialResponse> getAllCertificates(
            @Header("token") String token);

    @GET("template/{template_id}")
    Call<DidiGetTemplateResponse> getTemplate(
            @Header("token") String token,
            @Path("template_id") String templateId);


/*
    @POST("banks/{bank_id}/accounts/{account_id}/{view_id}/wallet/cvu")
    Call<BindCVUResponse> createCVU(@Header("Authorization") String token,
                                    @Path("bank_id") Integer bank_id,
                                    @Path("account_id") String account_id,
                                    @Path("view_id") String view_id,
                                    @Body BindCVURequestBody bindCVURequestBody);
*/

}
