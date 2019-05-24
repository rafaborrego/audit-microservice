package com.rafaborrego.audit.dto;


import java.util.List;

public class TablesNamesDTO extends ResultDTO {

	public List<String> tablesNames;

	public List<String> getTablesNames() {
		return tablesNames;
	}

	public void setTablesNames(List<String> tablesNames) {
		this.tablesNames = tablesNames;
	}
}
