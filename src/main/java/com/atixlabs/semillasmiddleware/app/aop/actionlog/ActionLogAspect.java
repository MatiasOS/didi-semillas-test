package com.atixlabs.semillasmiddleware.app.aop.actionlog;

import com.atixlabs.semillasmiddleware.app.didi.dto.DidiAppUserDto;
import com.atixlabs.semillasmiddleware.app.didi.model.constant.DidiAppUserOperationResult;
import com.atixlabs.semillasmiddleware.app.model.action.ActionLevelEnum;
import com.atixlabs.semillasmiddleware.app.model.action.ActionTypeEnum;
import com.atixlabs.semillasmiddleware.app.service.ActionLogService;
import com.atixlabs.semillasmiddleware.excelparser.app.dto.SurveyForm;
import com.atixlabs.semillasmiddleware.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * [Nueva Credencial] - INFO -Cuando una credencial fue creada.
 * [Credencial Revocada] - INFO - Cuando una credencial fue revocada.
 * [DIDI] - INFO - Cuando se realizó una sincronización con DIDI.
 * [DIDI] - ERROR - Error de conexión con DIDI.
 */
@Aspect
@Component
@Slf4j
public class ActionLogAspect {

    @Autowired
    private ActionLogService actionLogService;

    @Autowired
    public ActionLogAspect(ActionLogService actionLogService){
        this.actionLogService = actionLogService;
    }


    @Pointcut("execution(* com.atixlabs.semillasmiddleware.app.didi.service.DidiAppUserService.registerNewAppUser(..))")
    public void registerNewAppUser() {}

    @Pointcut("execution(* com.atixlabs.semillasmiddleware.app.bondarea.service.BondareaService.synchronizeLoans(..))")
    public void syncBondarea() {}

    @Pointcut("execution(* com.atixlabs.semillasmiddleware.app.service.CredentialService.buildAllCredentialsFromForm(..))")
    public void importSurvey(){}

    @AfterReturning(value = "registerNewAppUser()", returning = "returnValue")
    public void registerNewAppUserAfter(JoinPoint joinPoint,  Object returnValue)  {

        try {
            Object[] lArgs = joinPoint.getArgs();
            DidiAppUserDto didiAppUserDto = (DidiAppUserDto) lArgs[0];

            DidiAppUserOperationResult didiAppUserOperationResult = (DidiAppUserOperationResult)returnValue;

            if(didiAppUserOperationResult.equals(DidiAppUserOperationResult.NEW_USER_REGISTER_OK) ||
                    didiAppUserOperationResult.equals(DidiAppUserOperationResult.NEW_DID_REGISTERED_FOR_USER) ) {

                String message = String.format("Se registró el id Didi %s para el dni %d", didiAppUserDto.getDid(), didiAppUserDto.getDni());
                this.saveActionLog(ActionTypeEnum.DIDI_CREDENTIAL_REQUEST, ActionLevelEnum.INFO, message);

            }else{
                log.debug(String.format("AOP - registerNewAppUserAfter obviated, Operation %s",didiAppUserOperationResult.name()));
            }
        }catch (Exception ex){
            log.error("AOP ERROR - registerNewAppUserAfter ",ex);
        }

    }

    @Around(value = "syncBondarea()")
    public Object syncBondareaAround(final ProceedingJoinPoint joinPoint) throws Throwable{
        Object value;

        try {
            value = joinPoint.proceed();

            if(((Boolean) value)){
                this.registerSyncBondareaOk();
            }else{
                this.registerSyncBondareaError();
            }

        } catch (Throwable throwable) {
            this.registerSyncBondareaError();
            throw throwable;
        }

        return value;
    }

    private void registerSyncBondareaOk() {
            String message = "Sincronización con Bondarea finalizada Correctamente";
            this.saveActionLog(ActionTypeEnum.BONDAREA_SYNC, ActionLevelEnum.INFO, message);
    }

    private void registerSyncBondareaError() {
        String message = "Sincronización con Bondarea finalizada con errores";
        this.saveActionLog(ActionTypeEnum.BONDAREA_SYNC, ActionLevelEnum.ERROR, message);
    }

    @AfterReturning(value = "importSurvey()")
    public void importSurveyAfter(JoinPoint joinPoint)  {

        try {
            Object[] lArgs = joinPoint.getArgs();
            SurveyForm surveyForm = (SurveyForm) lArgs[0];

            String message = String.format("Se registró una nueva encuesta para el formulario %s de la fecha %s y pdv %d", surveyForm.getSurveyFormCode(), DateUtil.toString(surveyForm.getSurveyDate()), surveyForm.getPdv());
            this.saveActionLog(ActionTypeEnum.NEW_SURVEY, ActionLevelEnum.INFO, message);

        }catch (Exception e){
            log.error("AOP ERROR - importSurvey ",e);
        }

    }

    private void saveActionLog(ActionTypeEnum actionType, ActionLevelEnum actionLevel, String message){

        try {
            this.actionLogService.registerAction(actionType, actionLevel, message);
        } catch (Exception e) {
            log.error("ERROR when save Action Log {} {} {} ", actionType, actionLevel, message, e);
        }

    }


}
