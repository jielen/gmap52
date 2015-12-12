package com.anyi.gp.core.action;

import com.anyi.gp.core.bean.SearchSchema;


public class DeleteSchemaAction  extends SchemaAction{

	private static final long serialVersionUID = 1002178637525480188L;
	
	public String doExecute() throws Exception{

		SearchSchema	schema = new 	SearchSchema();
		schema.setCompoId(compoId);
		schema.setSchemaName(schemaName);
		schema.setUserId(userId);
		schema.setSchemaValue(schemaValue);
		schema.setSystemSchema(systemSchema);
		
		int result = service.deleteSchema(schema);
		if(result > 0)
			resultString = "success";
		
		return SUCCESS;
	}
}
