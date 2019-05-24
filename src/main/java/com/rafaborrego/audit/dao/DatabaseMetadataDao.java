package com.rafaborrego.audit.dao;

import com.rafaborrego.audit.error.DatabaseManagementException;
import com.rafaborrego.audit.model.DatabaseColumn;
import com.rafaborrego.audit.error.DatabaseManagementException;
import com.rafaborrego.audit.model.DatabaseColumn;

import java.util.List;

/**
 * DAO for obtaining information of the database like the names of the tables or their columns
 */
public interface DatabaseMetadataDao {

	List<String> getDatabasesNames(String databaseNamePrefix) throws DatabaseManagementException;

	List<String> getTablesNames(String databaseName, List<String> ignorePatterns) throws DatabaseManagementException;

	List<DatabaseColumn> getTableColumns(String databaseName, String tableName) throws DatabaseManagementException;
}
