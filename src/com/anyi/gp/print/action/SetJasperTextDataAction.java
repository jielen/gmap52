package com.anyi.gp.print.action;

import javax.servlet.http.HttpServletRequest;

import com.anyi.gp.core.action.AjaxAction;
import com.anyi.gp.print.service.PrintSetService;
import com.opensymphony.webwork.ServletActionContext;

public class SetJasperTextDataAction extends AjaxAction {

	private static final long serialVersionUID = 1L;
	private PrintSetService printParameter;
  	public PrintSetService getPrintParameter() {
  		return printParameter;
  	}
	public void setPrintParameter(PrintSetService printParameter) {
	    this.printParameter = printParameter;
	}
	public String doExecute() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		String tableName = request.getParameter("tableName");
		String fieldName = request.getParameter("fieldName");
		String condition = request.getParameter("condition");
		String textData = request.getParameter("textData");
		String rowCount = request.getParameter("rowCount");
		String result = this.getPrintParameter().setJasperTextData(tableName, fieldName, condition, textData, rowCount);
		
    if("valid".equals(result)){
      resultstring = this.wrapResultStr("true", result);
    }
    else{
      resultstring = this.wrapResultStr("false", result);
    }
    return SUCCESS;
	}

}
