package com.anyi.gp.core.action;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import com.anyi.gp.access.CommonService;
import com.opensymphony.xwork.ActionSupport;

public abstract class SchemaAction extends ActionSupport{
	
	private static final long serialVersionUID = -277909777739078767L;

	protected String compoId;

	protected String userId;

	protected String schemaName;

	protected String schemaValue;

	protected String systemSchema;

	protected InputStream inputStream;

	protected CommonService service;
	
	protected String resultString;
	
	public String getCompoId() {
		return compoId;
	}

	public void setCompoId(String compoId) {
		this.compoId = compoId;
	}

  public void setService(CommonService service) {
    this.service = service;
  }

  public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
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

	public String getSystemSchema() {
		return systemSchema;
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
	
	public String execute() throws Exception{
		before();
		String result = doExecute();
		after();
		return result;
	}
	
	public abstract String doExecute() throws Exception;
	
	public void before(){
		
	}
	
	public void after(){
		try {
			if(resultString != null)
				inputStream  = new ByteArrayInputStream(resultString.getBytes("UTF-8"));
			else
				inputStream = new ByteArrayInputStream("".getBytes());
		} 
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}
