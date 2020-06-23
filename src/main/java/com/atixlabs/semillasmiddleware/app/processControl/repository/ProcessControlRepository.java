package com.atixlabs.semillasmiddleware.app.processControl.repository;

import com.atixlabs.semillasmiddleware.app.processControl.model.ProcessControl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProcessControlRepository extends JpaRepository<ProcessControl, Long> {

    Optional<ProcessControl> findByProcessName(String processName);
}
