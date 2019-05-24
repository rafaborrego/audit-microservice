package com.rafaborrego.audit.dao;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.rafaborrego.audit.error.DatabaseManagementException;
import com.rafaborrego.audit.model.DatabaseColumn;
import com.rafaborrego.audit.error.DatabaseManagementException;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import javax.annotation.Nullable;
import java.util.List;

/**
 * {@inheritDoc}
 */
@Repository
public class DatabaseMetadataDaoMySqlImpl extends HibernateDao implements DatabaseMetadataDao {

	@Override
	public List<String> getDatabasesNames(String databaseNamePrefix) throws DatabaseManagementException {
		String sqlQuery = getSqlQueryGetsDatabasesNames(databaseNamePrefix);
		List<String> databasesNames = Lists.newLinkedList();
		try {
			List<Object []> results = executeSqlReadQuery(sqlQuery);
			if(results != null && !results.isEmpty()) {
				databasesNames = convertQueryResultsToStringList(results);
			}
		} catch (Exception e) {
			throw new DatabaseManagementException("There was an error obtaining the names of the databases. " +
					"Please contact the administrator", e);
		}

		return databasesNames;
	}

	@Override
	public List<String> getTablesNames(String databaseName, List<String> ignorePatterns)
			throws DatabaseManagementException {
		String sqlQuery = getSqlQueryGetsTablesNames(databaseName, ignorePatterns);
		List<String> tablesNames = Lists.newLinkedList();
		try {
			List<Object []> results = executeSqlReadQuery(sqlQuery);
			if(results != null && !results.isEmpty()) {
				tablesNames = convertQueryResultsToStringList(results);
			}
		} catch (Exception e) {
			throw new DatabaseManagementException("There was an error obtaining the tables names of the database " +
					databaseName +  ". Please consult the administrator", e);
		}

		return tablesNames;
	}

	@Override
	public List<DatabaseColumn> getTableColumns(String databaseName, String tableName) throws DatabaseManagementException {
		String sqlQuery = getSqlQueryGetsColumnsNames(databaseName, tableName);
		List<DatabaseColumn> columns = Lists.newLinkedList();
		try {
			List<Object []> results = executeSqlReadQuery(sqlQuery);
			if(results != null && !results.isEmpty()) {
				columns = convertQueryResultsToColumnList(results);
			}
		} catch (Exception e) {
			throw new DatabaseManagementException("There was an error obtaining the column names of the table " + tableName +
					" on the database " + databaseName + ". Please consult the administrator", e);
		}

		return columns;
	}

	private String getSqlQueryGetsDatabasesNames(String databaseNamePrefix) {
		String sqlQuery = "select distinct(TABLE_SCHEMA) from information_schema.tables " +
				"where TABLE_SCHEMA like '" + databaseNamePrefix + "%' " +
				"order by TABLE_SCHEMA asc";

		return sqlQuery;
	}

	private String getSqlQueryGetsTablesNames(String databaseName, List<String> ignorePatterns) {
		String sqlQuery = "select TABLE_NAME from information_schema.tables " +
				"where TABLE_TYPE='BASE TABLE' " +
				"and TABLE_SCHEMA = '" + databaseName + "'";

		if(ignorePatterns != null && !ignorePatterns.isEmpty()) {
			for(String ignorePattern : ignorePatterns) {
				sqlQuery += " and TABLE_NAME not like '%" + ignorePattern + "%'";
			}
		}

		return sqlQuery;
	}

	private String getSqlQueryGetsColumnsNames(String databaseName, String tableName) {
		String sqlQuery = "select COLUMN_NAME, COLUMN_TYPE, COLUMN_KEY, CHARACTER_SET_NAME FROM INFORMATION_SCHEMA.columns " +
				"where table_schema = '" + databaseName +  "'" +
				"and TABLE_NAME = '" + tableName + "'";

		return sqlQuery;
	}

	private List<Object []> executeSqlReadQuery(String sqlQuery) {
		Session session = getHibernateTpl().getSessionFactory().openSession();
		return session.createSQLQuery(sqlQuery).list();
	}

	private List<String> convertQueryResultsToStringList(List results) {
		List<String> list = Lists.newArrayListWithCapacity(results.size());
		for(Object result : results) {
			list.add((String)result);
		}

		return list;
	}

	private List<DatabaseColumn> convertQueryResultsToColumnList(List <Object []> results) {
		List<DatabaseColumn> columns = Lists.transform(results, new Function<Object[], DatabaseColumn>() {
					@Nullable
					@Override
					public DatabaseColumn apply(Object[] objects) {
						return convertRowDataToColumn(objects);
					}
				}
		);

		return columns;
	}

	private DatabaseColumn convertRowDataToColumn(Object[] objects) {
		DatabaseColumn column = new DatabaseColumn();
		column.setName((String) objects[0]);
		column.setType((String) objects[1]);
		column.setCharacterSetName((String) objects[3]);

		if(!StringUtils.isEmpty((String) objects[2])) {
			if((objects[2]).equals("PRI")) {
				column.setPrimaryKey(true);
			}
		}

		return column;
	}
}
