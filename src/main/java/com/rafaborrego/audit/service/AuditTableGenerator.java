package com.rafaborrego.audit.service;

import com.rafaborrego.audit.model.DatabaseColumn;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AuditTableGenerator {
	
	public static final String COLUMN_TYPE_ENUM = "enum";
	public static final String COLUMN_TYPE_BOOLEAN = "bit(1)";
	public static final String COLUMN_TYPE_DATETIME = "datetime";
	public static final String COLUMN_TYPE_TIMESTAMP = "timestamp";


	public String generateAuditTableScript(String tableName, List<DatabaseColumn> columns, List<String> primaryKeys) {
		StringBuilder contentBuilder = new StringBuilder();
		
		generateCreateTableDefinition(tableName, columns, contentBuilder);
		generateIndexes(tableName, primaryKeys, contentBuilder);
		generateDropTriggersAndDelimiter(tableName, contentBuilder);
		generateInsertTrigger(tableName, columns, contentBuilder);
		generateDeleteTrigger(tableName, columns, contentBuilder);
		generateUpdateTrigger(tableName, columns, primaryKeys, contentBuilder);

		return contentBuilder.toString();
	}

	private void generateInsertTrigger(String tableName, List<DatabaseColumn> columns, StringBuilder contentBuilder) {
		generateCreateTriggerBeginning(tableName, contentBuilder);
		generateCreateTriggerInsert(tableName, columns, contentBuilder);
		generateCreateTriggerEnding(contentBuilder);
	}

	private void generateUpdateTrigger(String tableName, List<DatabaseColumn> columns, List<String> primaryKeys, StringBuilder contentBuilder) {
		generateUpdateTriggerBeginning(tableName, contentBuilder);
		generateFieldChangesComparisons(columns, contentBuilder);
		generateUpdateTriggerInsert(tableName, columns, primaryKeys, contentBuilder);
		generateUpdateTriggerEnding(contentBuilder);
	}

	private void generateDeleteTrigger(String tableName, List<DatabaseColumn> columns, StringBuilder contentBuilder) {
		generateDeleteTriggerBeginning(tableName, contentBuilder);
		generateDeleteTriggerInsert(tableName, columns, contentBuilder);
		generateCreateTriggerEnding(contentBuilder);
	}

	private void generateCreateTableDefinition(String tableName, List<DatabaseColumn> columns, StringBuilder contentBuilder) {
		contentBuilder.append("DROP TABLE IF EXISTS " + tableName + "_AUDIT;\n\n");

		contentBuilder.append("CREATE TABLE " + tableName + "_AUDIT (\n");
		addTab(contentBuilder);
		contentBuilder.append("AUD_ID bigint(20) NOT NULL AUTO_INCREMENT,\n");
		addTab(contentBuilder);
		contentBuilder.append("AUD_TIME timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,\n");
		addTab(contentBuilder);
		contentBuilder.append("AUD_ACTION char(1) NOT NULL,\n");
		addTab(contentBuilder);
		contentBuilder.append("AUD_COLUMNS varchar(255) DEFAULT NULL,\n");

		for(DatabaseColumn column : columns) {
			addTab(contentBuilder);
			contentBuilder.append(generateColumnDefinition(column));
		}

		addTab(contentBuilder);
		contentBuilder.append("PRIMARY KEY (AUD_ID)\n");
		contentBuilder.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ROW_FORMAT=COMPACT;\n\n");
	}

	private String generateColumnDefinition(DatabaseColumn column) {
		String columnDefinition = column.getName() + " " + getGeneratedColumnType(column);
		if(!StringUtils.isEmpty(column.getCharacterSetName())) {
			columnDefinition += " CHARACTER SET " + column.getCharacterSetName();
		}

		columnDefinition += " DEFAULT NULL,\n";

		return columnDefinition;
	}

	private String getGeneratedColumnType(DatabaseColumn column) {
		String columnType = column.getType();

		if(isDateOrTimeColumn(column) || isBooleanColumn(column)) {
			columnType += " NULL";
		} else if(isEnumColumn(column)){
			// enums are saved on the audit tables as chars as their possible values may change and affect the existing values
			columnType = "varchar(255)";
		}

		return columnType;
	}

	private boolean isDateOrTimeColumn(DatabaseColumn column) {
		return column.getType().equalsIgnoreCase(COLUMN_TYPE_DATETIME) ||
				column.getType().equalsIgnoreCase(COLUMN_TYPE_TIMESTAMP);
	}

	private boolean isBooleanColumn(DatabaseColumn column) {
		return column.getType().equalsIgnoreCase(COLUMN_TYPE_BOOLEAN);
	}

	private boolean isEnumColumn(DatabaseColumn column) {
		return column.getType().startsWith(COLUMN_TYPE_ENUM);
	}

	private void generateIndexes(String tableName, List<String> primaryKeys, StringBuilder contentBuilder) {
		contentBuilder.append("ALTER TABLE " + tableName + "_AUDIT ADD INDEX " + tableName + "_AUD_AUD_TIME_IDX (AUD_TIME);\n");

		for(String primaryKey : primaryKeys) {
			contentBuilder.append("ALTER TABLE " + tableName + "_AUDIT ADD INDEX " + tableName + "_AUD_" + primaryKey +
					"_IDX (" + primaryKey + ");\n\n");
		}
	}

	private void generateDropTriggersAndDelimiter(String tableName, StringBuilder contentBuilder) {
		contentBuilder.append("DROP TRIGGER IF EXISTS " + tableName + "_INSERT;\n");
		contentBuilder.append("DROP TRIGGER IF EXISTS " + tableName + "_UPDATE;\n\n");
		contentBuilder.append("DROP TRIGGER IF EXISTS " + tableName + "_DELETE;\n\n");
		contentBuilder.append("DELIMITER $$\n\n");
	}

	private void generateCreateTriggerBeginning(String tableName, StringBuilder contentBuilder) {
		contentBuilder.append("CREATE TRIGGER " + tableName + "_INSERT AFTER INSERT ON " + tableName + "\n");
		contentBuilder.append("FOR EACH ROW\n");

		addTab(contentBuilder);
		contentBuilder.append("BEGIN\n\n");
	}

	private void generateDeleteTriggerBeginning(String tableName, StringBuilder contentBuilder) {
		contentBuilder.append("CREATE TRIGGER " + tableName + "_DELETE AFTER DELETE ON " + tableName + "\n");
		contentBuilder.append("FOR EACH ROW\n");

		addTab(contentBuilder);
		contentBuilder.append("BEGIN\n\n");
	}

	private void generateCreateTriggerInsert(String tableName, List<DatabaseColumn> columns, StringBuilder contentBuilder) {
		addTabs(2, contentBuilder);
		contentBuilder.append("INSERT INTO " + tableName + "_AUDIT (\n");

		addTabs(3, contentBuilder);
		contentBuilder.append("AUD_ACTION\n");

		for(DatabaseColumn column : columns) {
			addTabs(3, contentBuilder);
			contentBuilder.append(", " + column.getName() + "\n");
		}

		addTabs(2, contentBuilder);
		contentBuilder.append(")VALUES(\n");

		addTabs(3, contentBuilder);
		contentBuilder.append("'I'\n");

		for(DatabaseColumn column : columns) {
			addTabs(3, contentBuilder);
			contentBuilder.append(", NEW." + column.getName() + "\n");
		}

		addTabs(2, contentBuilder);
		contentBuilder.append(");\n\n");
	}

	private void generateDeleteTriggerInsert(String tableName, List<DatabaseColumn> columns, StringBuilder contentBuilder) {
		addTabs(2, contentBuilder);
		contentBuilder.append("INSERT INTO " + tableName + "_AUDIT (\n");

		addTabs(3, contentBuilder);
		contentBuilder.append("AUD_ACTION\n");

		for(DatabaseColumn column : columns) {
			addTabs(3, contentBuilder);
			contentBuilder.append(", " + column.getName() + "\n");
		}

		addTabs(2, contentBuilder);
		contentBuilder.append(")VALUES(\n");

		addTabs(3, contentBuilder);
		contentBuilder.append("'D'\n");

		for(DatabaseColumn column : columns) {
			addTabs(3, contentBuilder);
			contentBuilder.append(", OLD." + column.getName() + "\n");
		}

		addTabs(2, contentBuilder);
		contentBuilder.append(");\n\n");
	}

	private void generateCreateTriggerEnding(StringBuilder contentBuilder) {
		addTab(contentBuilder);
		contentBuilder.append("END;\n\n$$\n\n");
	}

	private void generateUpdateTriggerBeginning(String tableName, StringBuilder contentBuilder) {
		contentBuilder.append("CREATE TRIGGER " + tableName + "_UPDATE AFTER UPDATE ON " + tableName + "\n");
		contentBuilder.append("FOR EACH ROW\n");

		addTab(contentBuilder);
		contentBuilder.append("BEGIN\n\n");

		addTabs(2, contentBuilder);
		contentBuilder.append("set @DO_UPDATE = false;\n\n");

		addTabs(2, contentBuilder);
		contentBuilder.append("set @UPDATED_COLUMNS = '';\n\n");
	}



	private void generateFieldChangesComparisons(List<DatabaseColumn> columns, StringBuilder contentBuilder) {
		String tabs ="\n\t\t";
		String template = "IF((OLD.? <> NEW.?)"
			+ tabs + "\tOR (OLD.? is null and NEW.? is not null)"
			+ tabs + "\tOR (OLD.? is not null and NEW.? is null))"
			+ tabs + "THEN"
			+ tabs + "\tset @? = NEW.?;"
			+ tabs + "\tset @DO_UPDATE = true;"
			+ tabs + "\tset @UPDATED_COLUMNS = CONCAT(@UPDATED_COLUMNS, '?, ');"
			+ tabs + "ELSE"
			+ tabs + "\tset @? = null;"
			+ tabs + "END IF;\n\n";

		for(DatabaseColumn column : columns) {
			addTabs(2, contentBuilder);
			contentBuilder.append(template.replaceAll("\\?", column.getName()));
		}
	}

	private void generateUpdateTriggerInsert(String tableName, List<DatabaseColumn> columns, List<String> primaryKeys,
	                                         StringBuilder contentBuilder) {
		addTabs(2, contentBuilder);
		contentBuilder.append("IF (@DO_UPDATE = true) THEN\n");

		addTabs(3, contentBuilder);
		contentBuilder.append("INSERT INTO " + tableName + "_AUDIT(\n");

		addTabs(4, contentBuilder);
		contentBuilder.append("AUD_ACTION \n");

		addTabs(4, contentBuilder);
		contentBuilder.append(", AUD_COLUMNS\n");

		for(String primaryKey : primaryKeys) {
			addTabs(4, contentBuilder);
			contentBuilder.append(", " + primaryKey + " \n");
		}

		for(DatabaseColumn column : columns) {
			if(!column.isPrimaryKey()) {
				addTabs(4, contentBuilder);
				contentBuilder.append(", " + column.getName()+ "\n");
			}
		}

		addTabs(3, contentBuilder);
		contentBuilder.append(") VALUES (\n");

		addTabs(4, contentBuilder);
		contentBuilder.append("'U'\n");

		addTabs(4, contentBuilder);
		contentBuilder.append(", @UPDATED_COLUMNS\n");

		for(String primaryKey : primaryKeys) {
			addTabs(4, contentBuilder);
			contentBuilder.append(", NEW." + primaryKey + "\n");
		}

		for(DatabaseColumn column : columns) {
			if(!column.isPrimaryKey()) {
				addTabs(4, contentBuilder);
				contentBuilder.append(", @" + column.getName()+ "\n");
			}
		}

		addTabs(2, contentBuilder);
		contentBuilder.append(");\n");

		addTabs(2, contentBuilder);
		contentBuilder.append("END IF;\n\n");
	}

	private void generateUpdateTriggerEnding(StringBuilder contentBuilder) {
		addTab(contentBuilder);
		contentBuilder.append("END;\n\n");
		contentBuilder.append("$$\n\n");
		contentBuilder.append("delimiter ;\n\n");
	}

	private void addTab(StringBuilder contentBuilder) {
		addTabs(1, contentBuilder);
	}

	private void addTabs(int numberTabs, StringBuilder contentBuilder) {
		for(int i = 0; i < numberTabs; i++)
		contentBuilder.append("\t");
	}
}
