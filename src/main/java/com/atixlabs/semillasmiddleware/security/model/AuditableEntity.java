package com.atixlabs.semillasmiddleware.security.model;

import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.LocalDateTime;

@MappedSuperclass
@Getter
public abstract class AuditableEntity implements Serializable {

        @Column(name = "CREATED", updatable = false)
        @CreationTimestamp
        private LocalDateTime created;

        @Column(name = "UPDATED")
        @UpdateTimestamp
        private LocalDateTime updated;
}



