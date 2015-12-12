package com.anyi.gp.core.action;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.anyi.gp.BusinessException;
import com.anyi.gp.Delta;
import com.anyi.gp.Pub;
import com.anyi.gp.TableData;
import com.anyi.gp.access.CommonService;
import com.anyi.gp.util.StringTools;
import com.anyi.gp.util.XMLTools;

/**
 * @author shenjw
 * 
 */
public class GetRuleDeltaAction extends AjaxAction {

  private static final Logger log = Logger.getLogger(GetRuleDeltaAction.class);
  
  private static final long serialVersionUID = -8399089395445418770L;

  private static final String U_DEFAULT_TABLE = "U_DEFAULT_TABLE";

	private String param;

	private String ruleID;

	private String fieldsWithKilo;
	
  private CommonService service;
  
	private Delta getData() throws BusinessException {

		TableData tempParam = new TableData(XMLTools.stringToDocument(param)
				.getDocumentElement());
    
    Delta delta = service.getDBData(ruleID, tempParam);

		delta.setName(U_DEFAULT_TABLE);
		delta.setPageIndex(1);
		delta.setFromRow(1);
		delta.setPageSize(0);
		delta.setRowCountOfDB(delta.size());

		if (delta != null) {
			String[] vasField = StringTools.split2(fieldsWithKilo, ",");
			if (vasField != null) {
				List voKiloFields = new ArrayList();
				for (int i = 0; i < vasField.length; i++) {
					if (vasField[i] == null)
						continue;
					if ("".equals(vasField[i]))
						continue;
					voKiloFields.add(vasField[i]);
				}
				delta.setKiloFieldList(voKiloFields);
			}
		}
		return delta;
	}

	public String doExecute() throws Exception {

		resultstring = StringTools.XML_TYPE;
		resultstring += "\n" + Pub.makeRetString(true, this.getData().toString());
		
		return SUCCESS;
	}

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	public String getRuleID() {
		return ruleID;
	}

	public void setRuleID(String ruleID) {
		this.ruleID = ruleID;
	}

	public String getFieldsWithKilo() {
		return fieldsWithKilo;
	}

	public void setFieldsWithKilo(String fieldsWithKilo) {
		this.fieldsWithKilo = fieldsWithKilo;
	}

  public CommonService getService() {
    return service;
  }

  public void setService(CommonService service) {
    this.service = service;
  }

}
