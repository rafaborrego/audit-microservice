package com.rafaborrego.audit.service;

import com.rafaborrego.audit.error.DatabaseManagementException;
import com.rafaborrego.audit.model.DatabaseTable;

public interface AuditTableService {

	String createAuditTableScript(DatabaseTable table) throws DatabaseManagementException;
}
