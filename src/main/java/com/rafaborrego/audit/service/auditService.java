package com.rafaborrego.audit.service;

import com.rafaborrego.audit.dto.auditInputDto;
import com.rafaborrego.audit.entity.audit;

public interface auditService {

    audit getauditById(Long id);

    Iterable<audit> getaudits();

    audit createaudit(auditInputDto auditData);

    audit modifyaudit(Long auditId, auditInputDto auditData);

    void deleteaudit(Long auditId);
}
