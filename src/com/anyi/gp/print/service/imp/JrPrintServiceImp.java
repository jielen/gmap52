package com.anyi.gp.print.service.imp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.anyi.gp.access.CommonService;
import com.anyi.gp.print.bean.PrintConstants;
import com.anyi.gp.print.bean.PrintParameter;
import com.anyi.gp.print.data.DataGenerator;
import com.anyi.gp.print.direct.PrintDirectManager;
import com.anyi.gp.print.service.JrPrintService;
import com.anyi.gp.print.template.TemplateGenerator;

public class JrPrintServiceImp extends JrPrintBaseService implements JrPrintService {
	
	private static final Log logger = LogFactory.getLog(JrPrintServiceImp.class);
	private DataGenerator dataGen = new DataGenerator();
	private TemplateGenerator temp = new TemplateGenerator();
	private CommonService commonService;
	public CommonService getCommonService() {
		return commonService;
	}
	public void setCommonService(CommonService commonService) {
		this.commonService = commonService;
	}
	public List getJasperPrint(PrintParameter printParameter) {
		try{
			List jasperPrintList = new ArrayList();
			if (printParameter != null) {
				  Map map = printParameter.getParameter();
			    List data = this.generatePrintDataList(map);
			    List template = this.generateTemplateList(map);
			    if (PrintDirectManager.isDirect()) {
			    	jasperPrintList.add(data);
			    	jasperPrintList.add(template);
			    } else {
			    	jasperPrintList = buildJasperPrintList(data, template, map);
			    }
			}
			return jasperPrintList;
		}catch(Exception e){
			logger.error(e);
			return null;
		}
	}
	public List generatePrintDataList(Map printParameter) {
		try {
			String printType = (String) printParameter.get(PrintConstants.PRINT_PARAMETER_PRINT_TYPE);
			if (PrintConstants.PRINT_TYPE_EDITPAGE_TEMPLATE.equalsIgnoreCase(printType)
					|| PrintConstants.PRINT_TYPE_EDITPAGE_NOTEMPLATE.equalsIgnoreCase(printType)) {
				return dataGen.generateEditPageData(printParameter);
			} else if (PrintConstants.PRINT_TYPE_LISTPAGE_TEMPLATE.equalsIgnoreCase(printType)
					|| PrintConstants.PRINT_TYPE_LISTPAGE_NOTEMPLATE.equalsIgnoreCase(printType)) {
				return dataGen.generateListPageData(printParameter);
			} else if (PrintConstants.PRINT_TYPE_REPORTPAGE_TEMPLATE.equalsIgnoreCase(printType)
					|| PrintConstants.PRINT_TYPE_REPORTPAGE_NOTEMPLATE.equalsIgnoreCase(printType)) {
				return dataGen.generateReportPageData(printParameter);
			} else {
				return dataGen.generateEditPageData(printParameter);
			}
		} catch (Exception e) {
			logger.error(e);
			throw new RuntimeException("Method generatePrintDataList(HashMap printParameter) Error :"
							+ e.getMessage() + "\n");
		}
	}
	public List generateTemplateList(Map printParameter) {
		List template = new ArrayList();
		try {
			String tplCode = (String) printParameter.get(PrintConstants.PRINT_PARAMETER_TPL_CODE);
			if (tplCode == null || tplCode.equals("")) {
				template.addAll(temp.generateNoTemplateList(printParameter));
			} else {
				template.addAll(temp.generateWithTemplateList(printParameter));
			}
		} catch (Exception e) {
			logger.error(e);
			throw new RuntimeException("Class TemplateGenerator, Method generateTemplateList(HashMap printParameter) Error :"
							+ e.getMessage() + "\n");
		}
		return template;
	}

}
