package com.atixlabs.semillasmiddleware.app.repository;


import com.atixlabs.semillasmiddleware.app.model.beneficiary.Person;
import com.atixlabs.semillasmiddleware.app.model.credential.Credential;
import com.atixlabs.semillasmiddleware.app.model.credential.CredentialCredit;
import com.atixlabs.semillasmiddleware.app.model.credentialState.CredentialState;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Service
@Slf4j
public class CredentialCreditRepositoryCustomImpl implements CredentialCreditRepositoryCustom {

    @PersistenceContext
    protected EntityManager em;

    @Override
    public List<CredentialCredit> findLastCreditsDistinctIdHistoricalOrderByDateOfIssueDesc(Long dniHolder) {
        if(dniHolder != null) {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<CredentialCredit> cq = cb.createQuery(CredentialCredit.class);

            Root<CredentialCredit> credentialCredit = cq.from(CredentialCredit.class);
            List<Predicate> predicates = new ArrayList<>();


            predicates.add(cb.equal(credentialCredit.get("creditHolderDni"), dniHolder));

            //distinct id historical todo the distinct is necessary and is not working !
            ///cq.select(credentialCredit.get("idHistorical")).distinct(true);

            //ordered by date of issue desc
            cq.orderBy(cb.desc(credentialCredit.get("dateOfIssue")));

            cq.where(predicates.toArray(new Predicate[0]));

            TypedQuery<CredentialCredit> tq = em.createQuery(cq);
            return tq.getResultList();
        }
        return Collections.emptyList();
    }
}
