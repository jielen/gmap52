package com.anyi.gp.print.action;

import javax.servlet.http.HttpServletRequest;

import com.anyi.gp.core.action.AjaxAction;
import com.anyi.gp.print.service.PrintSetService;
import com.opensymphony.webwork.ServletActionContext;

public class GetTextDataFromFileAction extends AjaxAction {

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
		String prn_tpl_code = request.getParameter("prn_tpl_code");
	    String textData = this.getPrintParameter().getTextDataFromFile(prn_tpl_code);
	    String flag = "true";
	    this.resultstring = this.wrapResultStr(flag, textData);
		return SUCCESS;
	}

}
