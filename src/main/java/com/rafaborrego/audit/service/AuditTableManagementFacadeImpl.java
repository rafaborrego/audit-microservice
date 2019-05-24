package com.rafaborrego.audit.service;

import com.rafaborrego.audit.error.DatabaseManagementException;
import com.rafaborrego.audit.model.DatabaseColumn;
import com.rafaborrego.audit.model.DatabaseTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AuditTableManagementFacadeImpl implements AuditTableManagementFacade {

	@Autowired
	private AuditTableService auditTableService;

	@Autowired
	private DatabaseMetadataService databaseMetadataService;

	@Override
	@Transactional(readOnly = true)
	public String generateAuditTablesScript(String databaseName, List<String> tablesNames) throws DatabaseManagementException {
		StringBuilder scriptContentBuilder = new StringBuilder();
		for(String tableName : tablesNames) {
			List<DatabaseColumn> columns = databaseMetadataService.getTableColumns(databaseName, tableName);

			if(columns != null && !columns.isEmpty()){
				DatabaseTable table = new DatabaseTable(tableName, columns);
				String tableScript = auditTableService.createAuditTableScript(table);
				scriptContentBuilder.append(tableScript);
			}
		}

		return scriptContentBuilder.toString();
	}
}
