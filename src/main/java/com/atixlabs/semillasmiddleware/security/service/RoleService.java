package com.atixlabs.semillasmiddleware.security.service;

import com.atixlabs.semillasmiddleware.security.model.Role;
import com.atixlabs.semillasmiddleware.security.repository.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RoleService {

    @Autowired
    RoleRepository roleRepository;

    public Role findByCode(String role){
        return roleRepository.findByCode(role);
    }

    public Role save(Role role){
        return roleRepository.save(role);
    }
}
