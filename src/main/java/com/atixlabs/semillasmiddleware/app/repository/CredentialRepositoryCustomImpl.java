package com.atixlabs.semillasmiddleware.app.repository;


import com.atixlabs.semillasmiddleware.app.model.beneficiary.Person;
import com.atixlabs.semillasmiddleware.app.model.credential.Credential;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
public class CredentialRepositoryCustomImpl implements CredentialRepositoryCustom {

    @PersistenceContext
    protected EntityManager em;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")

    @Override
    public List<Credential> findCredentialsWithFilter(String credentialType, String name, String dniBeneficiary, String idDidiCredential, String dateOfExpiry, String dateOfIssue, List<String> credentialState) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Credential> cq = cb.createQuery(Credential.class);

        Root<Credential> credential = cq.from(Credential.class);
        List<Predicate> predicates = new ArrayList<>();

        Join<Credential, Person> children = credential.join("beneficiary", JoinType.LEFT);

        if (credentialType != null) {
            predicates.add(cb.equal(credential.get("credentialDescription"), credentialType));
        }

        if (name != null) {
            predicates.add(cb.like(cb.lower(children.get("name")), "%"+name.toLowerCase()+"%"));
        }

        if (dniBeneficiary != null) {
            predicates.add(cb.like(children.get("documentNumber").as(String.class), dniBeneficiary+"%"));
        }

        if (idDidiCredential != null) {
            predicates.add(cb.equal(credential.get("idDidiCredential"), idDidiCredential));
        }

        if (dateOfExpiry != null) {
            predicates.add(cb.like(credential.get("dateOfExpiry").as(String.class), dateOfExpiry+"%"));
        }

        if (dateOfIssue != null) {
            predicates.add(cb.like(credential.get("dateOfIssue").as(String.class), dateOfIssue+"%"));
        }

        if (credentialState != null) {
            predicates.add(cb.in(credential.get("credentialState")).value(credentialState));
        }

        cq.where(predicates.toArray(new Predicate[0]));

        return em.createQuery(cq).getResultList();
    }
}