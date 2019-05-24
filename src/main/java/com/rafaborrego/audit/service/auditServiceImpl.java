package com.rafaborrego.audit.service;

import com.rafaborrego.audit.dto.auditInputDto;
import com.rafaborrego.audit.repository.auditRepository;
import com.rafaborrego.audit.entity.audit;
import com.rafaborrego.audit.exception.auditNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class auditServiceImpl implements auditService {

    private final auditRepository auditRepository;

    public auditServiceImpl(auditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    @Override
    public audit getauditById (Long id) {

        return auditRepository.findById(id)
            .orElseThrow(() -> new auditNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<audit> getaudits() {

        return auditRepository.findNonDeletedauditsOrderedByCreationTimestamp();
    }

    @Override
    public audit createaudit(auditInputDto auditData) {

        LocalDateTime currentDateTime = LocalDateTime.now();

        audit audit = new audit();
        audit.setCreationTimestamp(currentDateTime);
        audit.setLastUpdateTimestamp(currentDateTime);

        // Add the content of auditData to the entity, for example:
        audit.setContent(auditData.getContent());

        auditRepository.save(audit);

        return audit;
    }

    @Override
    public audit modifyaudit(Long auditId, auditInputDto auditData) {

        audit audit = auditRepository.findById(auditId)
            .orElseThrow(() -> new auditNotFoundException(auditId));

        if (audit.isDeleted()) {
            throw new auditNotFoundException(auditId);
        }

        return modifyaudit(audit, auditData);
    }

    private audit modifyaudit(audit audit, auditInputDto auditData) {
        
        audit.setLastUpdateTimestamp(LocalDateTime.now());

        // Modify the entity with the content of auditData, for example:
        audit.setContent(auditData.getContent());

        return auditRepository.save(audit);
    }

    /**
    * It could throw a not found error if the audit is already deleted.
    * However I think that it is better to make the deletes idempotent
    */
    @Override
    public void deleteaudit(Long auditId) {

        audit audit = auditRepository.findById(auditId)
            .orElseThrow(() -> new auditNotFoundException(auditId));

        softDeleteaudit(audit);
    }

    private void softDeleteaudit(audit audit) {

        if (!audit.isDeleted()) {

            audit.setDeleted(true);
            audit.setLastUpdateTimestamp(LocalDateTime.now());
    
            auditRepository.save(audit);
        }
    }
}
