package com.anyi.gp.print.action;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.anyi.gp.context.ApplusContext;
import com.anyi.gp.core.action.AjaxAction;
import com.anyi.gp.print.bean.PrintConstants;
import com.anyi.gp.print.bean.PrintParameter;
import com.anyi.gp.print.direct.PrintLoader;
import com.anyi.gp.print.service.imp.JrPrintServiceImp;
import com.anyi.gp.print.util.PrintFileGenerator;
import com.anyi.gp.print.util.PrintFileUtil;
import com.opensymphony.webwork.ServletActionContext;

public class JrPrintDirectAction extends AjaxAction {

	private static final long serialVersionUID = 4976950721298048632L;

	public String doExecute() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		PrintParameter printParameter = new PrintParameter();
		String fileName = (String)request.getAttribute("fileName");
		List list = (List)PrintLoader.loadObject(fileName);
		List data = (List) list.get(0);
		List template = (List) list.get(1);
		Map parameter = (Map) list.get(2);
		printParameter.setParameter(parameter);
		JrPrintServiceImp jrPrintService = (JrPrintServiceImp) ApplusContext.getBean("jrPrintsService");
		List jasperPrintList = jrPrintService.buildJasperPrintList(data, template, parameter);
		PrintFileGenerator.generatePrintingFile(response, jasperPrintList, printParameter);
		PrintFileUtil.deleteFile(fileName);
		String printFileName = printParameter.getParameter(PrintConstants.PRINT_EXPORT_DESTFILE_NAME);
		String isPreview = request.getParameter("IsPreview");
		if(printFileName != null && isPreview.equalsIgnoreCase("N")){
			String flag = "true";
			this.setResultstring(this.wrapResultStr(flag, printFileName));
		}
		return SUCCESS;
	}

}
