package com.rafaborrego.audit.error;

public class DatabaseManagementException extends Exception {

	public DatabaseManagementException(String message) {
		super(message);
	}

	public DatabaseManagementException(String message, Throwable cause) {
		super(message, cause);
	}
}
