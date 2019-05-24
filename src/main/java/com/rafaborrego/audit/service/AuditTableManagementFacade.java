package com.rafaborrego.audit.service;

import com.rafaborrego.audit.error.DatabaseManagementException;
import com.rafaborrego.audit.controller.AuditTableManagementController;

import java.util.List;

/**
 * Facade for orchestrating the services that provide functionality to {@link AuditTableManagementController} related
 * to creating audit tables
 */
public interface AuditTableManagementFacade {

	String generateAuditTablesScript(String databaseName, List<String> tablesNames) throws DatabaseManagementException;
}
