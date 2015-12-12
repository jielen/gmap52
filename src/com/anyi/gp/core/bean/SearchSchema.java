package com.anyi.gp.core.bean;

public class SearchSchema {

	private String compoId;

	private String userId;

	private String schemaName;

	private String schemaValue;

	private String systemSchema;

	public String getCompoId() {
		return compoId;
	}

	public void setCompoId(String compoId) {
		this.compoId = compoId;
	}

	public String getSchemaName() {
		return schemaName;
	}

	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	public String getSchemaValue() {
		return schemaValue;
	}

	public void setSchemaValue(String schemaValue) {
		this.schemaValue = schemaValue;
	}

	public boolean isSystemSchema() {
		return "y".equals(systemSchema);
	}

	public void setSystemSchema(String systemSchema) {
		this.systemSchema = systemSchema;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

}
