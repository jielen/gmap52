package com.anyi.gp.core.action;

import com.anyi.gp.core.bean.SearchSchema;

public class SaveSchemaAction extends SchemaAction {

	private static final long serialVersionUID = 7543085160984210384L;
	
	public String doExecute() throws Exception{
		
		SearchSchema	schema = new 	SearchSchema();
		schema.setCompoId(compoId);
		schema.setSchemaName(schemaName);
		schema.setUserId(userId);
		schema.setSchemaValue(schemaValue);
		schema.setSystemSchema(systemSchema);
		
//		service.deleteSchema(schema);
//		service.insertSchema(schema);
    service.saveSchema(schema);
		
		resultString = "success";
		
		return SUCCESS;
	}
}
