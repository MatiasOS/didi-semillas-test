package com.atixlabs.semillasmiddleware.security.repository;

import com.atixlabs.semillasmiddleware.security.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByCode(String role);

}
