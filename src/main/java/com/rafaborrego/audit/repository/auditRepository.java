package com.rafaborrego.audit.repository;

import com.rafaborrego.audit.entity.audit;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface auditRepository extends CrudRepository<audit, Long> {

    @Query("select audit " +
            "from #{#entityName} audit " +
            "where audit.deleted=false " +
            "order by audit.creationTimestamp desc")
    List<audit> findNonDeletedauditsOrderedByCreationTimestamp();
}
