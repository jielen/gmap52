package com.anyi.gp.core.action;

import com.anyi.gp.core.bean.SearchSchema;

/**
 * 
 * TODO 可以与LoadSchemaAction类进行合并
 */
public class GetSchemaStatusAction extends SchemaAction{

	private static final long serialVersionUID = -7627094103991421032L;
		
	public String doExecute() throws Exception{
		
		SearchSchema	schema = new 	SearchSchema();
		schema.setCompoId(compoId);
		schema.setSchemaName(schemaName);
		schema.setUserId(userId);
		SearchSchema result = (SearchSchema)service.loadSchema(schema);
		if(result == null){
			resultString = "noexit";
		}
		else if (!userId.equalsIgnoreCase("sa") && result.isSystemSchema()){
			resultString = "isSystemSche";
		}
		else{
			resultString = "exit";
		}
		
		return SUCCESS;
	}
}
