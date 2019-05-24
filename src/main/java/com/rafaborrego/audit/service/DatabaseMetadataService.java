package com.rafaborrego.audit.service;

import com.rafaborrego.audit.error.DatabaseManagementException;
import com.rafaborrego.audit.model.DatabaseColumn;

import java.util.List;

/**
 * Service for obtaining information of the database like the names of the tables or their columns
 */
public interface DatabaseMetadataService {

	List<String> getDatabasesNames() throws DatabaseManagementException;

	List<String> getDataTablesNames(String databaseName) throws DatabaseManagementException;

	List<DatabaseColumn> getTableColumns(String databaseName, String tableName) throws DatabaseManagementException;
}
