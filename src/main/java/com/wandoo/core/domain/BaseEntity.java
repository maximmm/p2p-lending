package com.wandoo.core.domain;

import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OptimisticLocking;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.Version;
import java.util.Date;

import static javax.persistence.TemporalType.TIMESTAMP;

@MappedSuperclass
@Getter
@OptimisticLocking
public abstract class BaseEntity {

    @Temporal(TIMESTAMP)
    @CreationTimestamp
    @Column(name = "ENTITY_CREATED", updatable = false)
    private Date created;

    @Temporal(TIMESTAMP)
    @UpdateTimestamp
    @Column(name = "ENTITY_UPDATED")
    private Date updated;

    @Version
    @Column(name = "ENTITY_VERSION")
    private Long version;

}