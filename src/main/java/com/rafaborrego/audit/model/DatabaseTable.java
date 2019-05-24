package com.rafaborrego.audit.model;

import java.util.List;

public class DatabaseTable {

	private String name;

	private List<DatabaseColumn> columns;


	public DatabaseTable(String name, List<DatabaseColumn> columns) {
		this.name = name;
		this.columns = columns;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<DatabaseColumn> getColumns() {
		return columns;
	}

	public void setColumns(List<DatabaseColumn> columns) {
		this.columns = columns;
	}
}
