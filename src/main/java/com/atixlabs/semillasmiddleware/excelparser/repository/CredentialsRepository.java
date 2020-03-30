package com.atixlabs.semillasmiddleware.excelparser.repository;

import com.atixlabs.semillasmiddleware.excelparser.model.Credentials;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CredentialsRepository extends CrudRepository<Credentials, Long> {


}
