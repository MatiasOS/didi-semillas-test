package com.atixlabs.semillasmiddleware.app.service;

import com.atixlabs.semillasmiddleware.app.dto.ActionDto;
import com.atixlabs.semillasmiddleware.app.dto.ActionFilterDto;
import com.atixlabs.semillasmiddleware.app.model.action.ActionLevelEnum;
import com.atixlabs.semillasmiddleware.app.model.action.ActionLog;
import com.atixlabs.semillasmiddleware.app.model.action.ActionTypeEnum;
import com.atixlabs.semillasmiddleware.app.repository.ActionLogRepository;
import com.atixlabs.semillasmiddleware.security.util.AuthUtil;
import com.atixlabs.semillasmiddleware.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import javax.swing.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Service
public class ActionLogService {

    private ActionLogRepository actionLogRepository;

    private AuthUtil authUtil;

    @Autowired
    public ActionLogService(ActionLogRepository actionLogRepository, AuthUtil authUtil){
        this.actionLogRepository = actionLogRepository;
        this.authUtil = authUtil;
    }

    private Specification<ActionLog> getActionSpecification (ActionFilterDto actionFilterDto) {
        return (Specification<ActionLog>) (root, query, cb) -> {
            Stream<Predicate> predicates = Stream.of(
                    actionFilterDto.getLevel().map(value -> cb.equal(root.get("level"), value)),
                    actionFilterDto.getActionType().map(value -> cb.equal(root.get("actionType"), value)),
                    actionFilterDto.getUsername().map(value -> {
                    String username = "%" + value + "%";
                        return cb.like(root.get("userName"), username);
                    }),
                    actionFilterDto.getMessage().map(value -> {
                        String message = "%" + value + "%";
                        return cb.like(cb.upper(root.get("message")), message.toUpperCase());
                    }),
                    actionFilterDto.getDateFrom().map(value -> cb.greaterThanOrEqualTo(root.get("executionDateTime"), value)),
                    actionFilterDto.getDateTo().map(value ->cb.lessThanOrEqualTo(root.get("executionDateTime"), value))
            ).flatMap(Optional::stream);
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }

    @Value("${app.pageSize}")
    private String size;

    public Page<ActionDto>  find(Integer page, ActionFilterDto actionFilterDto){
        Pageable pageable = PageRequest.of(page, Integer.parseInt(size), Sort.by("executionDateTime").descending());
        return actionLogRepository.findAll(getActionSpecification(actionFilterDto), pageable)
                .map(ActionDto::new);
    }

    private ActionLevelEnum getActionLevelEnumByValue(Integer value){
        if(value!=null) {
            Optional<ActionLevelEnum> actionLevelEnum = ActionLevelEnum.valueOf(value);
            if (actionLevelEnum.isPresent())
                return actionLevelEnum.get();
        }

        return null;
    }

    private ActionTypeEnum getActionTypeEnumByValue(Integer value){
        if(value!=null) {
            Optional<ActionTypeEnum> actionTypeEnum = ActionTypeEnum.valueOf(value);
            if (actionTypeEnum.isPresent())
                return actionTypeEnum.get();
        }

        return null;
    }

    //[DIDI] - ERROR - Error de conexión con DIDI.
    private List<ActionDto> getListMockActions(){
        ActionDto actionDto = new ActionDto();
        actionDto.setActionType("DIDI");
        actionDto.setExecutionDateTime(Instant.now());//DateUtil.getLocalDateTimeNow());
        actionDto.setLevel("ERROR");
        actionDto.setMessage("Error de conexión con Didi");
        actionDto.setUser("admin");

        ActionDto actionDto2 = new ActionDto();
        actionDto2.setActionType("DIDI");
        actionDto2.setExecutionDateTime(Instant.now());//DateUtil.getLocalDateTimeNow());
        actionDto2.setLevel("INFO");
        actionDto2.setMessage("Sincronización DIDI OK");
        actionDto2.setUser("admin");

        ArrayList<ActionDto> actionDtos = new ArrayList<ActionDto>();
        actionDtos.add(actionDto);
        actionDtos.add(actionDto2);

        return actionDtos;
    }

    public ActionLog save(ActionLog actionLog){

        return  this.actionLogRepository.save(actionLog);
    }


    public ActionLog registerAction(ActionTypeEnum type, ActionLevelEnum level, String message){
        ActionLog actionLog = new ActionLog();
        String username = this.getCurrentUsername();
        log.info(username);
        if(username==null) {
            username = this.getCurrentUsernameDefault();
        }
        actionLog.setUserName(username);
        log.info(actionLog.getUserName());
        actionLog.setActionType(type);
        actionLog.setLevel(level);
        actionLog.setMessage(message);
        actionLog.setExecutionDateTime(DateUtil.getInstantNow());//DateUtil.getLocalDateTimeNow());

        actionLog = actionLogRepository.save(actionLog);

        log.debug(String.format("new action log register type: %s, level: %s, message: %s",type.getDescription(), level.getDescription(), message));
        return actionLog;

    }

    private String getCurrentUsername(){
        String currentUsername = null;
        try{
            currentUsername = authUtil.getCurrentUsername();
        }catch (Exception e){
            log.error("Can't get username logged ",e);
        }

        return currentUsername;
    }

    private String getCurrentUsernameDefault(){
        return "desconocido"; //TODO unharcode
    }

}
