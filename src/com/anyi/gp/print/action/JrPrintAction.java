package com.anyi.gp.print.action;
/**
 * @author hmgkevin
 * @date 08-03-20
 */
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.design.JasperDesign;

import com.anyi.gp.context.ApplusContext;
import com.anyi.gp.core.action.AjaxAction;
import com.anyi.gp.license.LicenseManager;
import com.anyi.gp.print.bean.PrintConstants;
import com.anyi.gp.print.bean.PrintParameter;
import com.anyi.gp.print.service.JrPrintService;
import com.anyi.gp.print.template.DynamicJD;
import com.anyi.gp.print.template.ParamConverter;
import com.anyi.gp.print.template.ParamFieldInfor;
import com.anyi.gp.print.template.StaticTextInfor;
import com.anyi.gp.print.template.TextElement;
import com.anyi.gp.print.util.PrintFileGenerator;
import com.anyi.gp.print.util.PrintTPLLoader;
import com.anyi.gp.print.util.PrintTPLUtil;
import com.anyi.gp.pub.SessionUtils;
import com.opensymphony.webwork.ServletActionContext;

public class JrPrintAction extends AjaxAction {

	private static final long serialVersionUID = 1L;
	private JrPrintService jrPrintService;
	public JrPrintService getJrPrintService() {
		return jrPrintService;
	}
	public void setJrPrintService(JrPrintService printService) {
		this.jrPrintService = printService;
	}

	public String doExecute() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		PrintParameter printParameter = new PrintParameter();
		/*******************获取页面传递的参数***********************/
		printParameter.addAllParameter(this.resolvePrintParameter(request, response));
		
		/*******************获取session参数******************************/
		printParameter.addAllParameter(this.getSessionParameter(request, response));
		
		/*********如果为无模板类型，则从页面获取模板数据，创建模板***********************/
		if (printParameter.getParameter(PrintConstants.PRINT_PARAMETER_TPL_CODE) == null) {
			printParameter.addAllParameter(resolveTemplateParameter(request, response));
		}
		
		/*****************根据页面传递的参数判断并设置打印类型******************/
		String printType = request.getParameter("printType");
		if(printType == null){
			printType = (String)request.getAttribute("printType");
		}
		if(printType.equalsIgnoreCase("EDIT_NOTPL")){
			printParameter.addParameter(PrintConstants.PRINT_PARAMETER_PRINT_TYPE,
                    PrintConstants.PRINT_TYPE_EDITPAGE_NOTEMPLATE);
		}else if(printType.equalsIgnoreCase("EDIT_TPL")){
			printParameter.addParameter(PrintConstants.PRINT_PARAMETER_PRINT_TYPE,
                    PrintConstants.PRINT_TYPE_EDITPAGE_TEMPLATE);
		}else if(printType.equalsIgnoreCase("LIST_TPL")){
			printParameter.addParameter(PrintConstants.PRINT_PARAMETER_PRINT_TYPE,
                    PrintConstants.PRINT_TYPE_LISTPAGE_TEMPLATE);
		}else if(printType.equalsIgnoreCase("LIST_NOTPL")){
			printParameter.addParameter(PrintConstants.PRINT_PARAMETER_PRINT_TYPE,
                    PrintConstants.PRINT_TYPE_LISTPAGE_NOTEMPLATE);
		}else if(printType.equalsIgnoreCase("REPORT_TPL")){
			printParameter.addParameter(PrintConstants.PRINT_PARAMETER_PRINT_TYPE,
                    PrintConstants.PRINT_TYPE_REPORTPAGE_TEMPLATE);
		}else if(printType.equalsIgnoreCase("REPORT_NOTPL")){
			printParameter.addParameter(PrintConstants.PRINT_PARAMETER_PRINT_TYPE,
                    PrintConstants.PRINT_TYPE_REPORTPAGE_NOTEMPLATE);
		}
		
		/************************生成打印数据流***********************************/
		List jasperPrintList = this.getJrPrintService().getJasperPrint(printParameter);
		
		/*******************生成打印文件并输出************************/
		PrintFileGenerator.generatePrintingFile(response, jasperPrintList, printParameter);
		
		String fileName = printParameter.getParameter(PrintConstants.PRINT_EXPORT_DESTFILE_NAME);
		String isPreview = request.getParameter("IsPreview");
		if(fileName != null && isPreview.equalsIgnoreCase("N")){
			String flag = "true";
			this.setResultstring(this.wrapResultStr(flag, fileName));
		}
		return SUCCESS;
	}

	/**
	 * 获取页面传递的参数，并保存到参数bean中（PrintParameter）
	 * @param request
	 * @param response
	 */
	public PrintParameter resolvePrintParameter(HttpServletRequest request, HttpServletResponse response) {
		PrintParameter printParameter = new PrintParameter();
		try {
			Map map = new HashMap();
			String tplCode = request.getParameter("TPL_CODE");
			if(tplCode == null){
				tplCode = (String)request.getAttribute("TPL_CODE");
			}
			if (tplCode != null) {
				tplCode = this.getCoCodeTemplate(request, tplCode);
				map.put(PrintConstants.PRINT_PARAMETER_TPL_CODE, tplCode);
			}
			String pageName = request.getParameter("pageName");
			if(pageName == null){
				pageName = (String)request.getAttribute("pageName");
			}
			if (pageName != null) {
				map.put(PrintConstants.PRINT_PARAMETER_PAGE_NAME,pageName);
			}
			String areaName = request.getParameter("AREA_NAME");
			if(areaName == null){
				areaName = (String)request.getAttribute("AREA_NAME");
			}
			if (areaName != null) {
				map.put(PrintConstants.PRINT_PARAMETER_AREA_NAME,areaName);
			}
			String gridId = request.getParameter("gridID");
			if(gridId == null){
				gridId = (String)request.getAttribute("gridID");
			}
			if (gridId != null) {
				map.put(PrintConstants.PRINT_PARAMETER_GRID_ID,gridId);
			}
			String exportType = request.getParameter("exportType");
			if(exportType == null){
				exportType = (String)request.getAttribute("exportType");
			}
			if (exportType != null) {
				map.put(PrintConstants.PRINT_PARAMETER_EXPORT_TYPE, exportType);
			}else{
				map.put(PrintConstants.PRINT_PARAMETER_EXPORT_TYPE,"0");
			}
			String compoName = request.getParameter("compoName");
			if(compoName == null){
				compoName = (String)request.getAttribute("compoName");
			}
			if (compoName != null) {
				map.put(PrintConstants.PRINT_PARAMETER_COMPO_NAME,compoName);
			}
			String dynamicTpl = request.getParameter("DynamicTpl"); // 动态模版sDynamicTpl="1";按原有模版动态缩列
			if(dynamicTpl == null){
				dynamicTpl = (String)request.getAttribute("DynamicTpl");
			}
			if (dynamicTpl != null) {
				map.put(PrintConstants.PRINT_PARAMETER_DYNAMIC_TPL,dynamicTpl);
			}
			String isPreview = request.getParameter("IsPreview");
			if(isPreview == null){
				isPreview = (String)request.getAttribute("IsPreview");
			}
			if (PrintConstants.IS_NOT_PREVIEW.equalsIgnoreCase(isPreview) && PrintConstants.EXPORT_PDF.equals(exportType)) {
				map.put(PrintConstants.PRINT_PARAMETER_PRINTTO_PRINTER,	PrintConstants.PRINT_TO_PRINTER);
			}
			if (request.getParameter("xmlhttp") != null) {
				if (request.getParameter("xmlhttp").equals("true")) {
					map.put(PrintConstants.PRINT_PARAMETER_REQUEST_TYPE,PrintConstants.REQUEST_TYPE_XMLHTTP);
				}
			}
			String printData = request.getParameter("PRINT_DATA");
			if(printData == null){
				printData = (String)request.getAttribute("PRINT_DATA");
			}
			if (printData != null) {
				String printServletPath = request.getServletPath();
				map.put(PrintConstants.PRINT_SERVLET_PATH,printServletPath);
				if (PrintConstants.PRINT_SERVLET_PATH_SPLITPRINT.equalsIgnoreCase(printServletPath)
						|| PrintConstants.PRINT_SERVLET_PATH_SPLITPRINTNOTPL.equalsIgnoreCase(printServletPath)) {
					map.put(
							PrintConstants.PRINT_PARAMETER_PRINT_HEADDATA,printData);
				} else {
					map.put(PrintConstants.PRINT_PARAMETER_PRINT_DATA,printData);
				}
			}
			String valueSet = request.getParameter("valueSet");
			if(valueSet == null){
				valueSet = (String)request.getAttribute("valueSet");
			}
			if(valueSet != null){
				map.put(PrintConstants.PRINT_PARAMETER_VALUE_SET, valueSet);
			}
			String childTableName = request.getParameter("ChildTableName");
			if(childTableName == null){
				childTableName = (String)request.getAttribute("ChildTableName");
			}
			if (childTableName != null) {
				map.put(PrintConstants.PRINT_PARAMETER_CHILDTABLE_NAME,	childTableName);
			}
			String continueCondition = request.getParameter("CONTINUE_CONDITION");
			if(continueCondition == null){
				continueCondition = (String)request.getAttribute("CONTINUE_CONDITION");
			}
			if (continueCondition != null) {
				map.put(PrintConstants.PRINT_PARAMETER_CONTINUE_CONDITION,continueCondition);
			}
			String continueRuleID = request.getParameter("CONTINUE_RULEID");
			if(continueRuleID == null){
				continueRuleID = (String)request.getAttribute("CONTINUE_RULEID");
			}
			if (continueRuleID != null) {
				map.put(PrintConstants.PRINT_PARAMETER_CONTINUE_RULEID,continueRuleID);
			}
			String isSelectRows = request.getParameter("isSelectRows");
			if(isSelectRows == null){
				isSelectRows = (String)request.getAttribute("isSelectRows");
			}
			if (isSelectRows != null) {
				map.put(PrintConstants.PRINT_PARAMETER_IS_SELECT_ROWS, isSelectRows);
			}
			String listPageCondition = request.getParameter("condition");
			if(listPageCondition == null){
				listPageCondition = (String)request.getAttribute("condition");
			}
			if (listPageCondition != null) {
				map.put(PrintConstants.PRINT_PARAMETER_LISTPAGE_CONDITION,listPageCondition);
			}else{
				map.put(PrintConstants.PRINT_PARAMETER_LISTPAGE_CONDITION,"");
			}
			String listPageKeyCondition = request.getParameter("keyCondition");
      if(listPageKeyCondition == null){
        listPageKeyCondition = (String)request.getAttribute("keyCondition");
      }
      if (listPageKeyCondition != null) {
        map.put(PrintConstants.PRINT_PARAMETER_LISTPAGE_KEY_CONDITION,listPageKeyCondition);
      }else{
        map.put(PrintConstants.PRINT_PARAMETER_LISTPAGE_KEY_CONDITION,"");
      }
			String ruleID = request.getParameter("ruleID");
			if(ruleID == null){
				ruleID = (String)request.getAttribute("ruleID");
			}
			if(ruleID != null && !ruleID.equals("undefined")){
				map.put(PrintConstants.PRINT_PARAMETER_LISTPAGE_RULEID, ruleID);
			}else{
				map.put(PrintConstants.PRINT_PARAMETER_LISTPAGE_RULEID, "");
			}
			String continuePrint = request.getParameter("ContinuePrint");
			if (continuePrint != null) {
				map.put(PrintConstants.PRINT_PARAMETER_CONTINUE_PRINT,	continuePrint);
			}
			String parameterStr = request.getParameter("parameters");
			if(parameterStr == null){
				parameterStr = (String)request.getAttribute("parameterStr");
			}
			if (parameterStr != null) {
				Map head = ParamConverter.splitParam(parameterStr);
				map.put(PrintConstants.PRINT_PARAMETER_LISTPAGE_HEADDATA, head);
			}
			map.put(PrintConstants.PRINT_PARAMETER_REQUEST, request);
			map.put(PrintConstants.PRINT_PARAMETER_RESPONSE,response);

			LicenseManager licenseManager = (LicenseManager) ApplusContext.getBean("licenseManager");
			map.put("HAS_REGISTER", licenseManager.hasRegistered() + "");
			
			printParameter.setParameter(map);
      
		} catch (Exception e) {
			throw new RuntimeException(
					"Method resolvePrintParameter(HttpServletRequest request,HttpServletResponse response) Error :"
							+ e.getMessage() + "\n");
		}
		return printParameter;
	}

	/**
	 * 当TPL_CODE==null时，从页面获取参数创建打印模板
	 * @param request
	 * @param response
	 */
	public PrintParameter resolveTemplateParameter(HttpServletRequest request, HttpServletResponse response) {
		PrintParameter printParameter = new PrintParameter();
		try {
			Map map = new HashMap();
			String name = request.getParameter("compoName");
			if(name == null){
				name = (String)request.getAttribute("compoName");
			}
			String cnName = request.getParameter("cnName");
			if(cnName == null){
				cnName = (String)request.getAttribute("cnName");
			}
			String pageWidthStr = request.getParameter("pageWidth");
			if(pageWidthStr == null){
				pageWidthStr = (String)request.getAttribute("pageWidth");
			}
			int pageWidth = PrintTPLUtil.str2Int(pageWidthStr);
			String pageHeightStr = request.getParameter("pageHeight");
			if(pageHeightStr == null){
				pageHeightStr = (String)request.getAttribute("pageHeight");
			}
			int pageHeight = PrintTPLUtil.str2Int(pageHeightStr);
			if (pageHeight == -1 || pageHeight == 0) {
				pageHeight = 842;
			}
			String rowHeightStr = request.getParameter("rowHeaderHeight");
			if(rowHeightStr == null){
				rowHeightStr = (String)request.getAttribute("rowHeaderHeight");
			}
			int rowHeight = PrintTPLUtil.str2Int(rowHeightStr);
			String rowDetailHeightStr = request.getParameter("rowDetailHeight");
			if(rowDetailHeightStr == null){
				rowDetailHeightStr = (String)request.getAttribute("rowDetailHeight");
			}
			int rowDetailHeight = PrintTPLUtil.str2Int(rowDetailHeightStr);
			String pageHeaderHStr = request.getParameter("pageHeaderH");
			if(pageHeaderHStr == null){
				pageHeaderHStr = (String)request.getAttribute("pageHeaderH");
			}
			int pageHeaderH = PrintTPLUtil.str2Int(pageHeaderHStr);
			String pageFooterHStr = request.getParameter("pageFooterH");
			if(pageFooterHStr == null){
				pageFooterHStr = (String)request.getAttribute("pageFooterH");
			}
			int pageFooterH = PrintTPLUtil.str2Int(pageFooterHStr);
			String labelsStr = request.getParameter("labels");
			if(labelsStr == null){
				labelsStr = (String)request.getAttribute("labels");
			}
			StaticTextInfor[] labels = ParamConverter.splitLabel(ParamConverter.extractLabel(labelsStr));
			String fieldsStr = request.getParameter("fields");
			if(fieldsStr == null){
				fieldsStr = (String)request.getAttribute("fields");
			}
			TextElement[] fields = ParamConverter.split(ParamConverter.extractElement(fieldsStr));
			String paramsStr = request.getParameter("params");
			if(paramsStr == null){
				paramsStr = (String)request.getAttribute("params");
			}
			Object[] temp = ParamConverter.extractParam(paramsStr);
			ParamFieldInfor[] params = ParamConverter.splitParam((List) temp[0]);
			Map paramValues = (Map) temp[1];
			DynamicJD djd = new DynamicJD();
			JasperDesign design = djd.getJasperDesign(name, cnName, pageWidth,
					pageHeight, rowHeight, rowDetailHeight, pageHeaderH,
					pageFooterH, params, labels, fields);
			map.put(PrintConstants.PRINT_PARAMETER_NOTEMPLATE_DESIGN, design);
			map.put(PrintConstants.PRINT_PARAMETER_NOTEMPLATE_PARAMVALUES,paramValues);
			map.put(PrintConstants.PRINT_PARAMETER_REQUEST, request);
			map.put(PrintConstants.PRINT_PARAMETER_RESPONSE,response);
			printParameter.setParameter(map);
		} catch (JRException e) {
			throw new RuntimeException(
				"Method resolveTemplateParameter(HttpServletRequest request,HttpServletResponse response) Error :"
							+ e.getMessage() + "\n");
		}
		return printParameter;
	}

	public String getCoCodeTemplate(HttpServletRequest request, String tplCode) {
		if (tplCode == null || tplCode.trim().length() == 0) return "";
		String coCodeTpl = null;
		HttpSession session = request.getSession();
		String coCode = SessionUtils.getAttribute(session,"svCoCode");
		String svAccountId = SessionUtils.getAttribute(session,"svAccountId");
		if (svAccountId != null && svAccountId.trim().length() > 0) {
			coCodeTpl = getSelfTemplate(tplCode, coCode + "_" + svAccountId);
		}
		if (coCodeTpl == null || tplCode.equals(coCodeTpl)) {
			coCodeTpl = getSelfTemplate(tplCode, coCode);
		}
		return coCodeTpl;
	}

	public String getSelfTemplate(String tplCode, String coCode) {
		String coCodeTpl = "";
		if (tplCode != null && !tplCode.equals("")) {
			if (coCode != null && !coCode.equals("")
					&& !coCode.equals("undefined") && !coCode.equals("null")) {
				tplCode += ",";
				int index = tplCode.indexOf(",");
				while (index > -1) {
					String sTplCode = tplCode.substring(0, index);
					int length = sTplCode.length() - coCode.length();
					if (length > 0 && coCode.equals(sTplCode.substring(length))) {
						coCodeTpl += sTplCode;
					} else {
						String newTplCode = sTplCode + "_" + coCode;
						if (this.isExistCoCodeTemplate(newTplCode)) {
							coCodeTpl += newTplCode;
						} else {
							coCodeTpl += sTplCode;
						}
					}
					coCodeTpl += ",";
					tplCode = tplCode.substring(index + 1);
					index = tplCode.indexOf(",");
				}
				coCodeTpl = coCodeTpl.substring(0, coCodeTpl.length() - 1);
			} else {
				coCodeTpl = tplCode;
			}
		}
		return coCodeTpl;
	}

	private boolean isExistCoCodeTemplate(String tplCode) {
		boolean isExist = false;
		try {
			if (tplCode != null && !tplCode.equals("")) {
				File f = new File(PrintTPLLoader.getJasperReportPath() + tplCode + ".jasper");
				if (f != null && f.exists()) {
					isExist = true;
				}
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return isExist;
	}
	
	public PrintParameter getSessionParameter(HttpServletRequest request, HttpServletResponse response) {
		PrintParameter printParameter = new PrintParameter();
		try {
			Map session = new HashMap();
			Map map = new HashMap();
			map.put("svCoCode", SessionUtils.getAttribute(request,"svCoCode"));
			map.put("svCoName", SessionUtils.getAttribute(request,"svCoName"));
			map.put("svEmpCode", SessionUtils.getAttribute(request,"svEmpCode"));
			map.put("svNd", SessionUtils.getAttribute(request,"svNd"));
			map.put("svPoCode", SessionUtils.getAttribute(request,"svPoCode"));
			map.put("svPoName", SessionUtils.getAttribute(request,"svPoName"));
			map.put("svOrgCode", SessionUtils.getAttribute(request,"svOrgCode"));
			map.put("svOrgName", SessionUtils.getAttribute(request,"svOrgName"));
			map.put("svSysDate", SessionUtils.getAttribute(request,"svSysDate"));
			map.put("svTransDate", SessionUtils.getAttribute(request,"svTransDate"));
			map.put("svUserID", SessionUtils.getAttribute(request,"svUserID"));
			map.put("svUserName", SessionUtils.getAttribute(request,"svUserName"));
			map.put("svRealUserID", SessionUtils.getAttribute(request,"svRealUserID"));
			map.put("svRealUserName", SessionUtils.getAttribute(request,"svRealUserName"));
			map.put("svFiscalYear", SessionUtils.getAttribute(request,"svFiscalYear"));
			session.put(PrintConstants.PRINT_PARAMETER_SESSION, map);
			printParameter.setParameter(session);
		}catch (RuntimeException e) {
			throw new RuntimeException(
					"Method getSessionParameter(HttpServletRequest request,HttpServletResponse response) Error :"
								+ e.getMessage() + "\n");
		}
		return printParameter;
	}

}
