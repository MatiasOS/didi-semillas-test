package com.atixlabs.semillasmiddleware.app.repository;


import com.atixlabs.semillasmiddleware.app.model.beneficiary.Person;
import com.atixlabs.semillasmiddleware.app.model.credential.Credential;
import com.atixlabs.semillasmiddleware.app.model.credentialState.CredentialState;
import com.atixlabs.semillasmiddleware.util.DateUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
public class CredentialRepositoryCustomImpl implements CredentialRepositoryCustom {

    @PersistenceContext
    protected EntityManager em;

    @Override
    public Page<Credential> findCredentialsWithFilter(String credentialType, String name, String surname, String dniBeneficiary, String dniHolder, String idDidiCredential, String lastUpdate, List<String> credentialStates,  Pageable page) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Credential> cq = cb.createQuery(Credential.class);

        Root<Credential> credential = cq.from(Credential.class);
        List<Predicate> predicates = new ArrayList<>();

        Join<Credential, Person> beneficiary = credential.join("beneficiary", JoinType.LEFT);
        Join<Credential, CredentialState> credentialStateEntity = credential.join("credentialState", JoinType.LEFT);

        if (credentialType != null) {
            predicates.add(cb.equal(credential.get("credentialDescription"), credentialType));
        }

        if(name != null) {
            predicates.add(cb.like(cb.lower(beneficiary.get("firstName")), "%" + name.toLowerCase() + "%"));
        }
        if(surname != null) {
            predicates.add(cb.like(cb.lower(beneficiary.get("lastName")), "%" + surname.toLowerCase() + "%"));
        }
        if (dniBeneficiary != null) {
            predicates.add(cb.like(credential.get("beneficiaryDni").as(String.class), dniBeneficiary+"%"));
        }

        if (dniHolder != null) {
            predicates.add(cb.like(credential.get("creditHolderDni").as(String.class), dniHolder+"%"));
        }

        if (idDidiCredential != null) {
            //this has been changed the value of id didi credential for credentialDto but not changed the name because front have mapped this name
            predicates.add(cb.equal(credential.get("idDidiReceptor"), idDidiCredential));
        }

        if (lastUpdate != null) {
            predicates.add(cb.like(credential.get("updated").as(String.class), lastUpdate+"%"));
        }

        if (credentialStates != null) {
            predicates.add(cb.in(credentialStateEntity.get("stateName")).value(credentialStates));
        }


        Long quantityResults =  this.getTotalCountWithFilters(credentialType,name, dniBeneficiary, dniHolder,idDidiCredential,  lastUpdate, credentialStates);

        log.info("cant result "+quantityResults);

        cq.where(predicates.toArray(new Predicate[0]));

        //order by updated field ASC
        Order lastUpdateOrder = cb.desc(credential.get("updated"));
        cq.orderBy(lastUpdateOrder);

        log.info("cq armada ");

        if(page != null) {
            TypedQuery<Credential> typedQuery = em.createQuery(cq);
            typedQuery.setFirstResult(Math.toIntExact((page.getPageNumber()) * page.getPageSize()));
            typedQuery.setMaxResults(page.getPageSize());
            return new PageImpl<>(typedQuery.getResultList(), page, quantityResults);
        }else
            return Page.empty();

    }


    @Override
    public Long getTotalCountWithFilters(String credentialType, String name, String dniBeneficiary, String dniHolder, String idDidiCredential, String lastUpdate, List<String> credentialStates) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);

        Root<Credential> credential = cq.from(Credential.class);
        List<Predicate> predicates = new ArrayList<>();

        Join<Credential, Person> beneficiary = credential.join("beneficiary", JoinType.LEFT);
        Join<Credential, CredentialState> credentialStateEntity = credential.join("credentialState", JoinType.LEFT);

        if (credentialType != null) {
            predicates.add(cb.equal(credential.get("credentialDescription"), credentialType));
        }

        if(name != null) {
            predicates.add(cb.or((cb.like(cb.lower(beneficiary.get("firstName")), "%" + name.toLowerCase() + "%")),
                    (cb.like(cb.lower(beneficiary.get("lastName")), "%" + name.toLowerCase() + "%"))));
        }


        if (dniBeneficiary != null) {
            predicates.add(cb.like(beneficiary.get("documentNumber").as(String.class), dniBeneficiary+"%"));
        }

        if (dniHolder != null) {
            predicates.add(cb.like(credential.get("creditHolderDni").as(String.class), dniHolder+"%"));
        }

        if (idDidiCredential != null) {
            //this has been changed the value of id didi credential for credentialDto but not changed the name because front have mapped this name
            predicates.add(cb.equal(credential.get("idDidiReceptor"), idDidiCredential));
        }

        if (lastUpdate != null) {
            predicates.add(cb.like(credential.get("updated").as(String.class), lastUpdate+"%"));
        }

        if (credentialStates != null) {
            predicates.add(cb.in(credentialStateEntity.get("stateName")).value(credentialStates));
        }

        cq.where(predicates.toArray(new Predicate[0]));

        cq.select(cb.count(credential));

        return em.createQuery(cq).getSingleResult();
    }
}
