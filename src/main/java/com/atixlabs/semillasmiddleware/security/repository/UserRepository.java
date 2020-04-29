package com.atixlabs.semillasmiddleware.security.repository;

import com.atixlabs.semillasmiddleware.security.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User>,
        PagingAndSortingRepository<User, Long> {

    Optional<User> findByUsername(String username);

    @Override
    Page<User> findAll(Pageable pageable);
}
