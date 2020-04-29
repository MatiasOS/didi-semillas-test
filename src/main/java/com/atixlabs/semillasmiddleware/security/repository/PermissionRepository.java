package com.atixlabs.semillasmiddleware.security.repository;

import com.atixlabs.semillasmiddleware.security.model.Permission;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends CrudRepository<Permission, String> {}
