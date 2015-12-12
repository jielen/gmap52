package com.anyi.gp.print.action;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRMapArrayDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import com.anyi.gp.core.action.AjaxAction;
import com.anyi.gp.print.bean.PrintConstants;
import com.anyi.gp.print.bean.PrintParameter;
import com.anyi.gp.print.service.imp.JrPrintBaseService;
import com.anyi.gp.print.template.TemplateGenerator;
import com.anyi.gp.print.util.PrintCompiler;
import com.anyi.gp.print.util.PrintFileGenerator;
import com.anyi.gp.print.util.PrintTPLUtil;
import com.opensymphony.webwork.ServletActionContext;

public class JrPrintPreviewAction extends AjaxAction {

	private static final long serialVersionUID = 1L;
	private final String exportType = "0";
	private int length = 20;

	public String doExecute() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		String prnTplXml = request.getParameter("prnTplXml");
		//System.out.println(prnTplXml);
		if(prnTplXml.indexOf("com.anyi.erp.")>0){
			prnTplXml = prnTplXml.replaceAll("com.anyi.erp.", "com.anyi.gp.");
		}
		//System.out.println(prnTplXml);
		String prnTplParameters = request.getParameter("prnTplParameters");
		String prnTplFields = request.getParameter("prnTplFields");
		String prnFixRowCount = request.getParameter("prnFIXROWCOUNT");
		String fieldsDispFlag = request.getParameter("fieldsDispFlag");
		int fixrowcount = PrintTPLUtil.str2Int(prnFixRowCount);
		if (fixrowcount == 0) {
			fixrowcount = length;
		}
		int rowGroup = (length % fixrowcount == 0) ? length / fixrowcount
				: (length / fixrowcount + 1);
		if (rowGroup * fixrowcount > length) {
			length = rowGroup * fixrowcount;
		}
		Map params = getParams(prnTplParameters, fieldsDispFlag);
		Map fields = getParams(prnTplFields, fieldsDispFlag);
		Object[] jrobject = getFields(fields, fixrowcount);
		JRDataSource dataSource = null;
		if (jrobject != null) {
			dataSource = new JRMapArrayDataSource(jrobject);
		} else {
			dataSource = new JREmptyDataSource();
		}
		List template = getJasperReport(prnTplXml);
		JrPrintBaseService jp = new JrPrintBaseService();
		List jpList = jp.fillTemplate(params, dataSource, template, null);
		PrintParameter printParamter = new PrintParameter();
		printParamter.addParameter(PrintConstants.PRINT_PARAMETER_EXPORT_TYPE, exportType);
		PrintFileGenerator.generatePrintingFile(response, jpList, printParamter);
		return null;
	}

	public List getJasperReport(String prnTplXml) {
		List template = new ArrayList();
		try {
			JasperDesign jasperDesign = null;
			byte[] tmpByte = prnTplXml.getBytes("GBK");
			ByteArrayInputStream bain = new ByteArrayInputStream(tmpByte);
			jasperDesign = JRXmlLoader.load(bain);
			TemplateGenerator templateGenerator = new TemplateGenerator();
			if (templateGenerator.isNeedSplit(jasperDesign)) {
				List splitTemplate = templateGenerator.splitTemplate(jasperDesign, null);
				template.addAll(splitTemplate);
			} else {
				JasperReport jasperReport = PrintCompiler.compileDesign(jasperDesign);
				template.add(jasperReport);
			}

		} catch (Exception e) {
			throw new RuntimeException(
					"Class JrPrintPreviewAction, Method getJasperReport(String prnTplXml) Error :"
							+ e.getMessage() + "\n");
		}
		return template;
	}

	public Map getParams(String prnTplParameters, String fieldsDispFlag) {
		Map params = new HashMap();
		if (prnTplParameters != null && !prnTplParameters.equals("")) {
			String tempParas = null;
			int i = prnTplParameters.indexOf(',');
			if (i == -1) {
				tempParas = prnTplParameters;
				if (!tempParas.equals("FIXROWCOUNT")) {
					if (fieldsDispFlag.equals("true")) {
						params.put(tempParas, "0000000000");
					}
					if (fieldsDispFlag.equals("false")) {
						params.put(tempParas, "");
					}
				}
			} else {
				int j = prnTplParameters.lastIndexOf(',');
				for (int k = 0; k < j;) {
					tempParas = (k == 0) ? prnTplParameters.substring(k, i)
							: prnTplParameters.substring(k + 1, i);
					if (!tempParas.equals("FIXROWCOUNT")) {
						if (fieldsDispFlag.equals("true")) {
							params.put(tempParas, "0000000000");
						}
						if (fieldsDispFlag.equals("false")) {
							params.put(tempParas, "");
						}
					}
					k = i;
					i = prnTplParameters.indexOf(',', k + 1);
				}
				tempParas = prnTplParameters.substring(j + 1, prnTplParameters
						.length());
				if (!tempParas.equals("FIXROWCOUNT")) {
					if (fieldsDispFlag.equals("true")) {
						params.put(tempParas, "0000000000");
					}
					if (fieldsDispFlag.equals("false")) {
						params.put(tempParas, "");
					}
				}
			}
		} else {
			params = null;
		}
		return params;
	}

	/**
	 * 取得预览的fields数据
	 * 
	 * @param field
	 *            Map
	 * @param fixrowcount
	 *            int 整型数据
	 * @return fields 对象数组
	 */
	public Object[] getFields(Map field, int fixrowcount) {
		Object[] fields = new Object[length];
		if (field != null) {
			int rowGroup = (length % fixrowcount == 0) ? length / fixrowcount
					: (length / fixrowcount + 1);
			for (int j = 0; j < rowGroup; j++) {
				Map temp = new java.util.HashMap();
				temp.putAll(field);
				for (int k = 0; k < fixrowcount; k++) {
					temp.put("FIXROWCOUNT", String.valueOf(j));
					fields[j * fixrowcount + k] = temp;
				}
			}
		} else {
			fields = null;
		}
		return fields;
	}

}
