package com.atixlabs.semillasmiddleware.security.repository;

import java.util.Optional;

import com.atixlabs.semillasmiddleware.security.model.Menu;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuRepository extends CrudRepository<Menu, Integer> {

  Optional<Menu> findByCode(String code);
}
