package com.anyi.gp.print.action;

import java.util.Map;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import com.anyi.gp.core.action.AjaxAction;
import com.anyi.gp.print.service.PrintSetService;
import com.anyi.gp.pub.SessionUtils;
import com.opensymphony.webwork.ServletActionContext;

public class AjaxCompoTplInfoAction extends AjaxAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private PrintSetService printParameter;

	public PrintSetService getPrintParameter() {
		return printParameter;
	}

	public void setPrintParameter(PrintSetService printParameter) {
		this.printParameter = printParameter;
	}

	public String doExecute() throws Exception {
		/**
		 * Ajax«Î«Û¥¶¿Ì
		 */
		HttpServletRequest request = ServletActionContext.getRequest();
		String pageName = request.getParameter("pageName");
		String componame = request.getParameter("componame");
		String cocode = SessionUtils.getAttribute(request, "svCoCode");
		String condition = "";
		if (pageName != null && (pageName.lastIndexOf("_L") == (pageName.length() - 2))) {
			condition = " AND (PRN_TPL_REPORTTYPE LIKE '%L' OR PRN_TPL_REPORTTYPE LIKE '%C')";
		} else {
			condition = " AND (PRN_TPL_REPORTTYPE LIKE '%E' OR PRN_TPL_REPORTTYPE LIKE '%C')";
		}
		condition += " AND (CO_CODE = '*' OR CO_CODE = '" + cocode + "')";
		
		this.resultstring = "<response>";
		List list = this.getPrintParameter().getCompoTplInfo(condition, componame);
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				Map map = (Map) list.get(i);
				this.resultstring += "<CompoTplInfo>";
				this.resultstring += "<tplCode>" + map.get("PRN_TPL_JPCODE") + "</tplCode>";
				this.resultstring += "<tplName>" + map.get("PRN_TPL_NAME") + "</tplName>";
				this.resultstring += "<coCode>" + map.get("CO_CODE") + "</coCode>";
				this.resultstring += "</CompoTplInfo>";
			}
		}
		this.resultstring += "</response>";
		return SUCCESS;
	}
}
