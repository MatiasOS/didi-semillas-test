package com.atixlabs.semillasmiddleware.app.didi.service;

import com.atixlabs.semillasmiddleware.app.didi.constant.DidiSyncStatus;
import com.atixlabs.semillasmiddleware.app.didi.model.DidiAppUser;
import com.atixlabs.semillasmiddleware.app.exceptions.CredentialException;
import com.atixlabs.semillasmiddleware.app.model.credential.*;
import com.atixlabs.semillasmiddleware.app.model.credential.constants.CredentialCategoriesCodes;
import com.atixlabs.semillasmiddleware.app.model.credential.constants.CredentialRelationHolderType;
import com.atixlabs.semillasmiddleware.app.model.credentialState.constants.RevocationReasonsCodes;
import com.atixlabs.semillasmiddleware.app.sancor.model.SancorPolicy;
import com.atixlabs.semillasmiddleware.app.sancor.service.SancorPolicyService;
import com.atixlabs.semillasmiddleware.app.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class SyncDidiProcessService {

    private CredentialCreditService credentialCreditService;

    private CredentialBenefitService credentialBenefitService;

    private CredentialIdentityService credentialIdentityService;

    private CredentialDwellingService credentialDwellingService;

    private CredentialEntrepreneurshipService credentialEntrepreneurshipService;

    private CredentialBenefitSancorService credentialBenefitSancorService;

    private DidiAppUserService didiAppUserService;

    private DidiService didiService;

    private SancorPolicyService sancorPolicyService;

    @Autowired
    public SyncDidiProcessService(CredentialCreditService credentialCreditService, DidiAppUserService didiAppUserService, DidiService didiService, CredentialBenefitService credentialBenefitService, CredentialIdentityService credentialIdentityService, CredentialDwellingService credentialDwellingService, CredentialEntrepreneurshipService credentialEntrepreneurshipService, CredentialBenefitSancorService credentialBenefitSancorService, SancorPolicyService sancorPolicyService){
        this.credentialCreditService = credentialCreditService;
        this.didiAppUserService = didiAppUserService;
        this.didiService = didiService;
        this.credentialBenefitService = credentialBenefitService;
        this.credentialIdentityService = credentialIdentityService;
        this.credentialDwellingService = credentialDwellingService;
        this.credentialEntrepreneurshipService = credentialEntrepreneurshipService;
        this.credentialBenefitSancorService = credentialBenefitSancorService;
        this.sancorPolicyService = sancorPolicyService;
    }


    public Map<CredentialCategoriesCodes, String> emmitAllCredentialsOnPendindDidiState(){

        Map<CredentialCategoriesCodes, String> response = new HashMap<CredentialCategoriesCodes, String>();

        try {
            this.emmitCredentialsIdentity();
        } catch (CredentialException e) {
            log.error("Error emmiting Identity credentials : {} ",e.getMessage(), e);
            response.put(CredentialCategoriesCodes.IDENTITY,e.getMessage());
        }

        try {
            this.emmitCredentialsDwelling();
        } catch (CredentialException e) {
            log.error("Error emmiting Dwelling credentials : {} ",e.getMessage(), e);
            response.put(CredentialCategoriesCodes.DWELLING,e.getMessage());
        }

        try {
            this.emmitCredentialsEntrepreneurship();
        } catch (CredentialException e) {
            log.error("Error emmiting Entrepreneurship credentials : {} ",e.getMessage(), e);
            response.put(CredentialCategoriesCodes.ENTREPRENEURSHIP,e.getMessage());
        }

        try {
            this.emmitCredentialsCredit();
        } catch (CredentialException e) {
            log.error("Error emmiting Credit credentials : {} ",e.getMessage(), e);
            response.put(CredentialCategoriesCodes.CREDIT,e.getMessage());
        }

        try {
            this.emmitCredentialsBenefit();
        } catch (CredentialException e) {
            log.error("Error emmiting Benefit credentials : {} ",e.getMessage(), e);
            response.put(CredentialCategoriesCodes.BENEFIT,e.getMessage());
        }

        try {
            this.emmitCredentialsBenefitSancor();
        } catch (CredentialException e) {
            log.error("Error emmiting Benefit Sancor credentials : {} ",e.getMessage(), e);
            response.put(CredentialCategoriesCodes.BENEFIT_SANCOR,e.getMessage());
        }

        return response;
    }

    public void emmitCredentialsCredit() throws CredentialException {

        List<CredentialCredit> credentialCreditsToEmmit = this.credentialCreditService.getCredentialCreditsOnPendindDidiState();

        if(credentialCreditsToEmmit==null || credentialCreditsToEmmit.isEmpty()){
            log.info("No credit credentials to emmit were found");
        }else{

            log.info(" {} Credential Credits to emmit", credentialCreditsToEmmit.size());

            for(CredentialCredit credentialCredit : credentialCreditsToEmmit){
                this.emmitCredentialCredit(credentialCredit);
            }

        }
    }

    public void emmitCredentialsBenefit() throws CredentialException {

        List<CredentialBenefits> credentialBenefitsToEmmit = this.credentialBenefitService.getCredentialBenefitsOnPendindDidiState();

        if(credentialBenefitsToEmmit==null || credentialBenefitsToEmmit.isEmpty()){
            log.info("No benefits credentials to emmit were found");
        }else{

            log.info(" {} Credential Benefits to emmit", credentialBenefitsToEmmit.size());

            for(CredentialBenefits credentialBenefits : credentialBenefitsToEmmit){
                this.emmitCredentialBenefit(credentialBenefits);
            }

        }
    }


    public void emmitCredentialsIdentity() throws CredentialException {

        List<CredentialIdentity> credentialsIdentityToEmmit = this.credentialIdentityService.getCredentialIdentityOnPendindDidiState();

        if(credentialsIdentityToEmmit==null || credentialsIdentityToEmmit.isEmpty()){
            log.info("No Identity credentials to emmit were found");
        }else{

            log.info(" {} Credential Identity to emmit", credentialsIdentityToEmmit.size());

            for(CredentialIdentity credentialIdentity : credentialsIdentityToEmmit){
                this.emmitCredentialIdentity(credentialIdentity);
            }

        }
    }

    public void emmitCredentialsDwelling() throws CredentialException {

        List<CredentialDwelling> credentialDwellingToEmmit = this.credentialDwellingService.getCredentialDwellingOnPendindDidiState();

        if(credentialDwellingToEmmit==null || credentialDwellingToEmmit.isEmpty()){
            log.info("No Dwelling credentials to emmit were found");
        }else{

            log.info(" {} Credential Dwellingtity to emmit", credentialDwellingToEmmit.size());

            for(CredentialDwelling credentialDwelling : credentialDwellingToEmmit){
                this.emmitCredentialDwelling(credentialDwelling);
            }

        }
    }

    public void emmitCredentialsEntrepreneurship() throws CredentialException {

        List<CredentialEntrepreneurship> credentialsEntrepreneurshipToEmmit = this.credentialEntrepreneurshipService.getCredentialEntrepreneurshipOnPendindDidiState();

        if(credentialsEntrepreneurshipToEmmit==null || credentialsEntrepreneurshipToEmmit.isEmpty()){
            log.info("No Entrepreneurship credentials to emmit were found");
        }else{

            log.info(" {} Credential Entrepreneurship to emmit", credentialsEntrepreneurshipToEmmit.size());

            for(CredentialEntrepreneurship credentialEntrepreneurship : credentialsEntrepreneurshipToEmmit){
                this.emmitCredentialEntrepreneurship(credentialEntrepreneurship);
            }

        }
    }

    public void emmitCredentialsBenefitSancor() throws CredentialException {

        List<CredentialBenefitSancor> credentialsBenefitSancorToEmmit = this.credentialBenefitSancorService.getCredentialBenefitSancorsOnPendindDidiState();

        if(credentialsBenefitSancorToEmmit==null || credentialsBenefitSancorToEmmit.isEmpty()){
            log.info("No Benefit Sancor credentials to emmit were found");
        }else{

            log.info(" {} Benefit Sancor to emmit", credentialsBenefitSancorToEmmit.size());

            for(CredentialBenefitSancor credentialBenefitSancorToEmmit : credentialsBenefitSancorToEmmit){
                this.emmitCredentialBenefitSancor(credentialBenefitSancorToEmmit);
            }

        }
    }


    /**
     * get current Did for holder and emmit credential
     * @param credentialCredit
     */
    public void emmitCredentialCredit(CredentialCredit credentialCredit){

        log.info("Emmiting Credential Credit id {} idBondarea {} holder {}",credentialCredit.getId(), credentialCredit.getIdBondareaCredit(), credentialCredit.getCreditHolderDni());

        Optional<DidiAppUser> didiAppUser = this.didiAppUserService.getDidiAppUserByDni(credentialCredit.getCreditHolderDni());

        if(didiAppUser.isPresent()) {
            credentialCredit.setIdDidiReceptor(didiAppUser.get().getDid());
            credentialCredit = credentialCreditService.save(credentialCredit);

            didiService.createAndEmmitCertificateDidi(credentialCredit);

        }else{
            log.info("Id Didi for Holder {} not exist, Credential Credit for loan {} not emmited", credentialCredit.getCreditHolderDni(), credentialCredit.getIdBondareaCredit());
        }

    }

    public void emmitCredentialBenefit(CredentialBenefits credentialBenefit){

        log.info("Emmiting Credential Benefit id {} holder {} beneficiary {}",credentialBenefit.getId(), credentialBenefit.getCreditHolderDni(), credentialBenefit.getBeneficiaryDni());

        Optional<DidiAppUser> didiAppUser = this.didiAppUserService.getDidiAppUserByDni(credentialBenefit.getCreditHolderDni());

        if(didiAppUser.isPresent()) {
            credentialBenefit.setIdDidiReceptor(didiAppUser.get().getDid());
            credentialBenefit = credentialBenefitService.save(credentialBenefit);

            didiService.createAndEmmitCertificateDidi(credentialBenefit);

        }else{
            log.info("Id Didi for Benefociary {} not exist, Credential Benefit {} not emmited", credentialBenefit.getCreditHolderDni(), credentialBenefit.getId());
        }

    }

    public void emmitCredentialIdentity(CredentialIdentity credentialIdentity){

        log.info("Emmiting Credential identity id {} holder {} beneficiary {}",credentialIdentity.getId(), credentialIdentity.getCreditHolderDni(), credentialIdentity.getBeneficiaryDni());

        Optional<DidiAppUser> didiAppUser = this.didiAppUserService.getDidiAppUserByDni(credentialIdentity.getCreditHolderDni());

        if(didiAppUser.isPresent()) {
            credentialIdentity.setIdDidiReceptor(didiAppUser.get().getDid());
            credentialIdentity = credentialIdentityService.save(credentialIdentity);

            didiService.createAndEmmitCertificateDidi(credentialIdentity);

        }else{
            log.info("Id Didi for Beneficiary {} not exist, Credential Identity {} not emmited", credentialIdentity.getCreditHolderDni(), credentialIdentity.getId());
        }

    }

    public void emmitCredentialDwelling(CredentialDwelling credentialDwelling){

        log.info("Emmiting Credential Dwelling id {} holder {} beneficiary {}",credentialDwelling.getId(), credentialDwelling.getCreditHolderDni(), credentialDwelling.getBeneficiaryDni());

        Optional<DidiAppUser> didiAppUser  = this.didiAppUserService.getDidiAppUserByDni(credentialDwelling.getCreditHolderDni());

        if(didiAppUser.isPresent()) {
            credentialDwelling.setIdDidiReceptor(didiAppUser.get().getDid());
            credentialDwelling = credentialDwellingService.save(credentialDwelling);

            didiService.createAndEmmitCertificateDidi(credentialDwelling);

        }else{
            log.info("Id Didi for Beneficiary {} not exist, Credential Dwelling {} not emmited", credentialDwelling.getCreditHolderDni(), credentialDwelling.getId());
        }

    }


    public void emmitCredentialEntrepreneurship(CredentialEntrepreneurship credentialEntrepreneurship){

        log.info("Emmiting Credential Entrepreneurship id {} holder {} beneficiary {}",credentialEntrepreneurship.getId(), credentialEntrepreneurship.getCreditHolderDni(), credentialEntrepreneurship.getBeneficiaryDni());

        Optional<DidiAppUser> didiAppUser = this.didiAppUserService.getDidiAppUserByDni(credentialEntrepreneurship.getCreditHolderDni());

        if(didiAppUser.isPresent()) {
            credentialEntrepreneurship.setIdDidiReceptor(didiAppUser.get().getDid());
            credentialEntrepreneurship = credentialEntrepreneurshipService.save(credentialEntrepreneurship);

            didiService.createAndEmmitCertificateDidi(credentialEntrepreneurship);

        }else{
            log.info("Id Didi for Beneficiary {} not exist, Credential Entrepreneurship {} not emmited", credentialEntrepreneurship.getCreditHolderDni(), credentialEntrepreneurship.getId());
        }

    }

    public void emmitCredentialBenefitSancor(CredentialBenefitSancor credentialBenefitSancor){

        log.info("Emmiting Credential Benefit Sancor id {} holder {} beneficiary {}",credentialBenefitSancor.getId(), credentialBenefitSancor.getCreditHolderDni(), credentialBenefitSancor.getBeneficiaryDni());

        Optional<DidiAppUser> didiAppUser = this.didiAppUserService.getDidiAppUserByDni(credentialBenefitSancor.getCreditHolderDni());


        if(didiAppUser.isPresent()) {

            Optional<SancorPolicy>  opSancorPolicy = this.sancorPolicyService.findByCertificateClientDni(credentialBenefitSancor.getBeneficiaryDni());

            if(opSancorPolicy.isPresent()) {
                credentialBenefitSancor.setIdDidiReceptor(didiAppUser.get().getDid());

                credentialBenefitSancor.addPolicyData(opSancorPolicy.get());

                credentialBenefitSancor = credentialBenefitSancorService.save(credentialBenefitSancor);

                didiService.createAndEmmitCertificateDidi(credentialBenefitSancor);
            }else{
                log.info("Sancor policy for Beneficiary {} i not avaiable, Credential Benefit Sancor {} not emmited", credentialBenefitSancor.getCreditHolderDni(), credentialBenefitSancor.getId());

            }

        }else{
            log.info("Id Didi for Beneficiary {} not exist, Credential Benefit Sancor {} not emmited", credentialBenefitSancor.getCreditHolderDni(), credentialBenefitSancor.getId());
        }
    }

    //TODO improve return information
  public Boolean processNewsAppDidiUsers(){

        Boolean finalizedOk = true;

        List<DidiAppUser> didiAppUsersToProcces = didiAppUserService.getDidiAppUsersNeedProcess();

        if(didiAppUsersToProcces!=null && !didiAppUsersToProcces.isEmpty()){

            for(DidiAppUser didiAppUser : didiAppUsersToProcces) {
                try {
                    this.verifyCredentialIdentityForDidiAppUser(didiAppUser);
                    this.verifyCredentialCreditForDidiAppUser(didiAppUser);
                    this.verifyCredentialBenefitForDidiAppUser(didiAppUser);
                    this.verifyCredentialDwellingForDidiAppUser(didiAppUser);
                    this.verifyCredentialEntrepreneurshipForDidiAppUser(didiAppUser);
                    this.verifyCredentialBenefitSancorForDidiAppUser(didiAppUser);

                    didiAppUser.setSyncStatus(DidiSyncStatus.SYNC_OK.getCode());
                    didiAppUserService.save(didiAppUser);
                } catch (CredentialException e) {
                    log.error("Error updating info new id didi for dni {}",didiAppUser.getDni());
                    finalizedOk = false;
                }

            }

        }else{
            log.info("No new didi app user to process");
        }

        return finalizedOk;

    }

    //verify if credential identity is active and id didi is valid
    public void verifyCredentialIdentityForDidiAppUser(DidiAppUser didiAppUser) throws CredentialException {

        //credential with principal dni is a holder
        List<CredentialIdentity> credentialsIdentityToVerify = this.credentialIdentityService.getAllCredentialIdentityActivesForDni(didiAppUser.getDni());

        if(credentialsIdentityToVerify==null || credentialsIdentityToVerify.isEmpty()){
            log.info("Credential identity for dni {} is not present", didiAppUser.getDni());
        }

        for(CredentialIdentity credentialIdentityToVerify : credentialsIdentityToVerify) {

                if (!credentialIdentityToVerify.getIdDidiReceptor().equals(didiAppUser.getDid())) {
                    credentialIdentityService.revokeComplete(credentialIdentityToVerify, RevocationReasonsCodes.UPDATE_INTERNAL);
                    CredentialIdentity newCredentialIdentity = credentialIdentityService.buildNewOnPendidgDidi(credentialIdentityToVerify, didiAppUser);
                    credentialIdentityService.save(newCredentialIdentity);
                    log.info("Credential Identity for holder {} and beneficiary {} type {} updated to id didi {} and set on pending didi state", newCredentialIdentity.getCreditHolderDni(),newCredentialIdentity.getBeneficiaryDni(), newCredentialIdentity.getRelationWithCreditHolder(), didiAppUser.getDid());
                }
                log.info("Credential Identity for holder {} and beneficiary {} and type {} dont need be updated", credentialIdentityToVerify.getCreditHolderDni(), credentialIdentityToVerify.getBeneficiaryDni(), credentialIdentityToVerify.getRelationWithCreditHolder());

        }

        if(didiAppUser.getSyncStatus().equals(DidiSyncStatus.SYNC_NEW.getCode())){
            log.info("Id didi {} is new for dni {}, create if is necesary Credentials Identity Kinsman type", didiAppUser.getId(), didiAppUser.getDni());
            this.createCredentialIdentityKinsmanType(didiAppUser);
        }
    }

    /**
     *  for didi app user dni beneficiary
     * for each credential identity
     * id didi holder, dni holder, dni beneficiary, type holder
     * must have
     * id didi beneficary, dni holder, dni beneficiary, type kinsman
     *
     *
     * @param didiAppUserNew
     */
    public void createCredentialIdentityKinsmanType(DidiAppUser didiAppUserNew) throws CredentialException {

        List<CredentialIdentity> credentialsIdentityHolderToCompare = this.credentialIdentityService.getAllCredentialIdentityActivesOrPendingDidiForBeneficiaryDniAsFamilyAndTypeHolder(didiAppUserNew.getDni());

        for (CredentialIdentity credentialIdentityHolderType : credentialsIdentityHolderToCompare){
            if(!this.credentialIdentityService.existsCredentialIdentityActivesOrPendingDidiForBeneficiaryDniAsFamilyAndTypeKinsman(credentialIdentityHolderType.getCreditHolderDni(), credentialIdentityHolderType.getBeneficiaryDni())) {
                CredentialIdentity newCredentialidentityAsKinsmanType = this.credentialIdentityService.buildNewOnPendidgDidiAsKinsmanType(credentialIdentityHolderType, didiAppUserNew);
                this.credentialIdentityService.save(newCredentialidentityAsKinsmanType);
                log.info("Credential Identity for holder {} and beneficiary {} type {} generated for id didi {} and set on pending didi state", newCredentialidentityAsKinsmanType.getCreditHolderDni(),newCredentialidentityAsKinsmanType.getBeneficiaryDni(), newCredentialidentityAsKinsmanType.getRelationWithCreditHolder(), newCredentialidentityAsKinsmanType.getIdDidiReceptor());

            }else{
                log.info("Credential Identity for holder {} and beneficiary {} type {} generated for id didi {} and set on pending didi state", credentialIdentityHolderType.getCreditHolderDni(),credentialIdentityHolderType.getBeneficiaryDni(), CredentialRelationHolderType.KINSMAN.getCode(), didiAppUserNew.getDid());

            }
        }

    }

    //verify if credential identity is active and id didi is valid
    public void verifyCredentialCreditForDidiAppUser(DidiAppUser didiAppUser) throws CredentialException {

        List<CredentialCredit> credentialsCreditToVerify = this.credentialCreditService.getCredentialsCreditActiveForDni(didiAppUser.getDni());
        if(credentialsCreditToVerify!=null && !credentialsCreditToVerify.isEmpty()) {
            for(CredentialCredit credentialCredit : credentialsCreditToVerify) {
                if (!credentialCredit.getIdDidiReceptor().equals(didiAppUser.getDid())) {
                    this.credentialCreditService.revokeComplete(credentialCredit, RevocationReasonsCodes.UPDATE_INTERNAL);
                    CredentialCredit newCredentialCredit = credentialCreditService.buildNewOnPendidgDidi(credentialCredit, didiAppUser);
                    this.credentialCreditService.save(newCredentialCredit);
                    log.info("Credential Credit for dni {} and id credit {} updated to id didi {} and set on pending didi state", credentialCredit.getBeneficiaryDni(), credentialCredit.getIdBondareaCredit(), didiAppUser.getDid());
                }
                log.info("Credential Identity for dni {} and id credit {} dont need be updated", credentialCredit.getBeneficiaryDni(), credentialCredit.getIdBondareaCredit());
            }
        }
        else
            log.info("dni {} not have credentials credit active", didiAppUser.getDni());

    }


    public void verifyCredentialBenefitForDidiAppUser(DidiAppUser didiAppUser) throws CredentialException {

        List<CredentialBenefits> credentialsBenefitToVerify = this.credentialBenefitService.getCredentialBenefitsActiveForDni(didiAppUser.getDni());
        if(credentialsBenefitToVerify!=null && !credentialsBenefitToVerify.isEmpty()) {
            for(CredentialBenefits credentialBenefits : credentialsBenefitToVerify) {
                if (!credentialBenefits.getIdDidiReceptor().equals(didiAppUser.getDid())) {
                    this.credentialBenefitService.revokeComplete(credentialBenefits, RevocationReasonsCodes.UPDATE_INTERNAL);
                    CredentialBenefits newcCedentialBenefits = credentialBenefitService.buildNewOnPendidgDidi(credentialBenefits, didiAppUser);
                    this.credentialBenefitService.save(newcCedentialBenefits);
                    log.info("Credential Benefits for dni holder {} and dni beneficiary {} updated to id didi {} and set on pending didi state", newcCedentialBenefits.getCreditHolderDni(), newcCedentialBenefits.getBeneficiaryDni(), didiAppUser.getDid());
                }
                log.info("Credential Benefits for dni holder {} and dni beneficiary {} dont need be updated", credentialBenefits.getCreditHolderDni(), credentialBenefits.getBeneficiaryDni());
            }
        }
        else
            log.info("dni {} not have credentials Benefits active", didiAppUser.getDni());

    }

    public void verifyCredentialDwellingForDidiAppUser(DidiAppUser didiAppUser) throws CredentialException {

        List<CredentialDwelling> credentialsDwellingToVerify = this.credentialDwellingService.getCredentialDwellingActiveForDni(didiAppUser.getDni());
        if(credentialsDwellingToVerify!=null && !credentialsDwellingToVerify.isEmpty()) {
            for(CredentialDwelling credentialDwelling : credentialsDwellingToVerify) {
                if (!credentialDwelling.getIdDidiReceptor().equals(didiAppUser.getDid())) {
                    this.credentialDwellingService.revokeComplete(credentialDwelling, RevocationReasonsCodes.UPDATE_INTERNAL);
                    CredentialDwelling newCredentialDwelling = credentialDwellingService.buildNewOnPendidgDidi(credentialDwelling, didiAppUser);
                    this.credentialDwellingService.save(newCredentialDwelling);
                    log.info("Credential Dwelling for dni holder {} and dni beneficiary {} updated to id didi {} and set on pending didi state", newCredentialDwelling.getCreditHolderDni(), newCredentialDwelling.getBeneficiaryDni(), didiAppUser.getDid());
                }
                log.info("Credential Dwelling for dni holder {} and dni beneficiary {} dont need be updated", credentialDwelling.getCreditHolderDni(), credentialDwelling.getBeneficiaryDni());
            }
        }
        else
            log.info("dni {} not have credentials Dwelling active", didiAppUser.getDni());

    }

    public void verifyCredentialEntrepreneurshipForDidiAppUser(DidiAppUser didiAppUser) throws CredentialException {

        List<CredentialEntrepreneurship> credentialsEntrepreneurshipToVerify = this.credentialEntrepreneurshipService.getCredentialEntrepreneurshipActiveForDni(didiAppUser.getDni());
        if(credentialsEntrepreneurshipToVerify!=null && !credentialsEntrepreneurshipToVerify.isEmpty()) {
            for(CredentialEntrepreneurship credentialEntrepreneurship : credentialsEntrepreneurshipToVerify) {
                if (!credentialEntrepreneurship.getIdDidiReceptor().equals(didiAppUser.getDid())) {
                    this.credentialEntrepreneurshipService.revokeComplete(credentialEntrepreneurship, RevocationReasonsCodes.UPDATE_INTERNAL);
                    CredentialEntrepreneurship newCredentialEntrepreneurship = credentialEntrepreneurshipService.buildNewOnPendidgDidi(credentialEntrepreneurship, didiAppUser);
                    this.credentialEntrepreneurshipService.save(newCredentialEntrepreneurship);
                    log.info("Credential Entrepreneurship for dni holder {} and dni beneficiary {} updated to id didi {} and set on pending didi state", newCredentialEntrepreneurship.getCreditHolderDni(), newCredentialEntrepreneurship.getBeneficiaryDni(), didiAppUser.getDid());
                }
                log.info("Credential Entrepreneurship for dni holder {} and dni beneficiary {} dont need be updated", credentialEntrepreneurship.getCreditHolderDni(), credentialEntrepreneurship.getBeneficiaryDni());
            }
        }
        else
            log.info("dni {} not have credentials Entrepreneurship active", didiAppUser.getDni());

    }


    public void verifyCredentialBenefitSancorForDidiAppUser(DidiAppUser didiAppUser) throws CredentialException {

        List<CredentialBenefitSancor> credentialsBenefitSancorToVerify = this.credentialBenefitSancorService.getCredentialBenefitSancorActiveForDni(didiAppUser.getDni());
        if(credentialsBenefitSancorToVerify!=null && !credentialsBenefitSancorToVerify.isEmpty()) {
            for(CredentialBenefitSancor credentialBenefitSancor : credentialsBenefitSancorToVerify) {
                if (!credentialBenefitSancor.getIdDidiReceptor().equals(didiAppUser.getDid())) {
                    this.credentialBenefitSancorService.revokeComplete(credentialBenefitSancor, RevocationReasonsCodes.UPDATE_INTERNAL);
                    CredentialBenefitSancor newCredentialBenefitSancor = credentialBenefitSancorService.buildNewOnPendidgDidi(credentialBenefitSancor, didiAppUser);
                    this.credentialBenefitSancorService.save(newCredentialBenefitSancor);
                    log.info("Credential Sancor for dni holder {} and dni beneficiary {} updated to id didi {} and set on pending didi state", newCredentialBenefitSancor.getCreditHolderDni(), newCredentialBenefitSancor.getBeneficiaryDni(), didiAppUser.getDid());
                }
                log.info("Credential Sancor for dni holder {} and dni beneficiary {} dont need be updated", credentialBenefitSancor.getCreditHolderDni(), credentialBenefitSancor.getBeneficiaryDni());
            }
        }
        else
            log.info("dni {} not have credentials Sancor active", didiAppUser.getDni());

    }


}
