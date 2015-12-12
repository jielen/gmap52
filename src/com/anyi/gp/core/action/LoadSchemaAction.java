package com.anyi.gp.core.action;

import com.anyi.gp.core.bean.SearchSchema;


public class LoadSchemaAction  extends SchemaAction{

	private static final long serialVersionUID = 7302242268640703004L;

	public String doExecute() throws Exception{
		
		SearchSchema	schema = new 	SearchSchema();
		schema.setCompoId(compoId);
		schema.setSchemaName(schemaName);
		schema.setUserId(userId);
		
		SearchSchema result = service.loadSchema(schema);
		
		resultString = result.getSchemaValue();
		if(resultString == null){
			resultString = "";
		}
		else if(result.isSystemSchema()){
      String temp = "<entity name=\"systemSche\">\n<field name=\"isSystemSche\" value=\"Y\"/>\n</entity>\n</delta>";
      resultString = resultString.replaceFirst("</delta>", temp);
		}
		
		return SUCCESS;
	}
	
}
