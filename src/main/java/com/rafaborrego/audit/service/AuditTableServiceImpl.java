package com.rafaborrego.audit.service;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.rafaborrego.audit.error.DatabaseManagementException;
import com.rafaborrego.audit.model.DatabaseColumn;
import com.rafaborrego.audit.model.DatabaseTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditTableServiceImpl implements AuditTableService {

	@Autowired
	private AuditTableGenerator auditTableGenerator;

	@Override
	public String createAuditTableScript(DatabaseTable table) throws DatabaseManagementException {
		String tableName = table.getName();
		List<DatabaseColumn> columns = table.getColumns();
		Optional<List<String>> primaryKeys = getPrimaryKeys(table);

		String auditTableScript;
		if(primaryKeys.isPresent()) {
			auditTableScript = auditTableGenerator.generateAuditTableScript(tableName, columns, primaryKeys.get());
		} else {
			throw new DatabaseManagementException("The primary key of the table " +
					table.getName() + " was not found. Please consult the administrator");
		}

		return auditTableScript;
	}

	private Optional<List<String>> getPrimaryKeys(DatabaseTable table) throws DatabaseManagementException {
		Optional<List<String>> primaryKeys;
		if(table.getColumns() != null && !table.getColumns().isEmpty()) {
			Iterable<DatabaseColumn> columnsIterable = Iterables.filter(table.getColumns(), new Predicate<DatabaseColumn>() {
				@Override
				public boolean apply(DatabaseColumn column) {
					return column.isPrimaryKey();
				}
			});

			primaryKeys = convertColumnIterableToStringList(columnsIterable);
		} else {
			throw new DatabaseManagementException("There was an error getting the primary keys of the table " +
					table.getName() + ". Please consult the administrator");
		}

		return primaryKeys;
	}

	private Optional<List<String>> convertColumnIterableToStringList(Iterable<DatabaseColumn> columnsIterable) {
		Optional<List<String>> primaryKeys;
		if(columnsIterable != null) {
			List<String> columnsNames = Lists.newLinkedList();
			for (DatabaseColumn column : columnsIterable) {
				String columnName = column.getName();
				columnsNames.add(columnName);
			}

			if (columnsNames.isEmpty()) {
				primaryKeys = Optional.absent();
			} else {
				primaryKeys = Optional.of(columnsNames);
			}
		} else {
			primaryKeys = Optional.absent();
		}

		return primaryKeys;
	}
}
