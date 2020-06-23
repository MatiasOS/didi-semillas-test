package com.atixlabs.semillasmiddleware.app.processControl.service;

import com.atixlabs.semillasmiddleware.app.processControl.exception.InvalidProcessException;
import com.atixlabs.semillasmiddleware.app.processControl.model.ProcessControl;
import com.atixlabs.semillasmiddleware.app.processControl.model.constant.ProcessControlStatusCodes;
import com.atixlabs.semillasmiddleware.app.processControl.model.constant.ProcessNamesCodes;
import com.atixlabs.semillasmiddleware.app.processControl.repository.ProcessControlRepository;
import com.atixlabs.semillasmiddleware.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
public class ProcessControlService {

    private ProcessControlRepository processControlRepository;

    @Autowired
    public ProcessControlService(ProcessControlRepository processControlRepository) {
        this.processControlRepository = processControlRepository;
    }

    public ProcessControl setStatusToProcess(ProcessNamesCodes process, ProcessControlStatusCodes processStatus) throws InvalidProcessException {
        return this.setStatusToProcess(process.getCode(),processStatus.getCode() );
    }

    public ProcessControl setStatusToProcess(String processName, String processStatus) throws InvalidProcessException {
        ProcessControl processControl = this.findByProcessName(processName);
        //set status
        processControl.setStatus(processStatus);
        //the process is set to start: set start time
        if(processStatus.equals(ProcessControlStatusCodes.RUNNING.getCode()))
            processControl.setStartTime(DateUtil.getLocalDateTimeNowWithFormat("yyyy-MM-dd HH:mm:ss"));

        processControlRepository.save(processControl);
        log.info("Process " + processName + " is now set to " + processStatus);

        return processControl;
    }


    public void setProcessStartTimeManually(String processName, LocalDateTime time) throws InvalidProcessException{
        ProcessControl process = this.findByProcessName(processName);
        process.setStartTime(time);

        processControlRepository.save(process);
        log.info("Process " + processName + " is set the time manually to " + time);
    }

    public boolean isProcessRunning(ProcessNamesCodes process) throws InvalidProcessException {
        ProcessControl processControl = this.findByProcessName(process.getCode());
        return processControl.isRunning();
    }

    public boolean isProcessRunning(String processName) throws InvalidProcessException {
        ProcessControl processControl = this.findByProcessName(processName);
        return processControl.isRunning();
    }

    public LocalDateTime getProcessTimeByProcessCode(ProcessNamesCodes process) throws InvalidProcessException {
        return this.getProcessTimeByProcessCode(process.getCode());
    }

    public LocalDateTime getProcessTimeByProcessCode(String processName) throws InvalidProcessException {
        return this.findByProcessName(processName).getStartTime();
    }

    private ProcessControl findByProcessName(String processName) throws InvalidProcessException {
        Optional<ProcessControl> processControl = processControlRepository.findByProcessName(processName);
        if(processControl.isPresent())
            return processControl.get();
        else
            throw new InvalidProcessException("There is no process with name " + processName);
    }


}
