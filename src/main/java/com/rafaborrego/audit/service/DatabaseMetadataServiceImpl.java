package com.rafaborrego.audit.service;

import com.google.common.collect.Lists;
import com.rafaborrego.audit.dao.DatabaseMetadataDao;
import com.rafaborrego.audit.error.DatabaseManagementException;
import com.rafaborrego.audit.model.DatabaseColumn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DatabaseMetadataServiceImpl implements DatabaseMetadataService {

	@Autowired
	DatabaseMetadataDao databaseMetadataDao;

	@Override
	@Transactional(readOnly = true)
	public List<String> getDatabasesNames() throws DatabaseManagementException {
		String databaseNamePrefix = "PREFIX";

		return databaseMetadataDao.getDatabasesNames(databaseNamePrefix);
	}

	@Override
	@Transactional(readOnly = true)
	public List<String> getDataTablesNames(String databaseName) throws DatabaseManagementException {
		List<String> ignorePatterns = Lists.newArrayList(
			"AUDIT", "BATCH_JOB", "BATCH_STEP");

		return databaseMetadataDao.getTablesNames(databaseName, ignorePatterns);
	}

	@Override
	@Transactional(readOnly = true)
	public List<DatabaseColumn> getTableColumns(String databaseName, String tableName) throws DatabaseManagementException {
		return databaseMetadataDao.getTableColumns(databaseName, tableName);
	}
}
