package com.anyi.gp.print.service.imp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.data.JRMapArrayDataSource;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;

import com.anyi.gp.print.bean.PrintConstants;
import com.anyi.gp.print.data.PrintDataSourceFactory;
import com.anyi.gp.print.template.TemplateGenerator;
import com.anyi.gp.print.util.PrintTPLUtil;

public class JrPrintBaseService {
	
	public List buildJasperPrintList(List data, List template, Map map) {
		List jasperPrintList = new ArrayList();
		String continuePrint = (String) map.get(PrintConstants.PRINT_PARAMETER_CONTINUE_PRINT);
    	if (continuePrint == null && data.size() > 1 && template.size() > 1) {
    	    List jpList = null;
    	    Map dataMap = null;
    	    Map templateMap = null;
    	    String printData = null;
    	    Iterator dataIterator = data.iterator();
    	    Iterator templateIterator = template.iterator();
    	    while (dataIterator.hasNext()) {
    	    	dataMap = (Map) dataIterator.next();
    	    	templateMap = (Map) templateIterator.next();
    	    	printData = (String) dataMap.get(PrintConstants.PRINT_PARAMETER_DATA);
    	    	if (!templateIterator.hasNext()) {
    	    		templateIterator = template.iterator();
    	    	}
    	    	if (PrintTPLUtil.isEmptyDataSource(printData)) {
    	    		continue;
    	    	}
    	    	jpList = buildOneJasperPrint(dataMap, templateMap, map);
    	    	if (jpList != null) {
    	    		jasperPrintList.addAll(jpList);
    	    	}
    	    }
    	} else {
    	    List jpList = null;
    	    Map dataMap = null;
    	    Map templateMap = null;
    	    Iterator dataIterator = data.iterator();
    	    Iterator templateIterator = template.iterator();
    	    while (dataIterator.hasNext()) {
    	    	dataMap = (Map) dataIterator.next();
    	    	while (templateIterator.hasNext()) {
	    	        templateMap = (Map) templateIterator.next();
	    	        jpList = buildOneJasperPrint(dataMap, templateMap, map);
	    	        if (jpList != null) {
	    	        	jasperPrintList.addAll(jpList);
	    	        }
    	    	}
    	    	templateIterator = template.iterator();
    	    }
    	}
    	return jasperPrintList;
	}
	public List buildOneJasperPrint(Map data, Map template, Map printParameter) {
	    List dataSourceList = PrintDataSourceFactory.getPrintDataSource(data, template, printParameter);
	    Map parameters = (Map) dataSourceList.get(0);
	    JRDataSource jrDS = (JRDataSource) dataSourceList.get(1);
	    printParameter.put(PrintConstants.PRINT_PARAMETER_OUT_PARAMETERS, parameters);
	    printParameter.put(PrintConstants.PRINT_PARAMETER_OUT_DATASOURCE, jrDS);
	    String dynamicTpl = (String) printParameter.get(PrintConstants.PRINT_PARAMETER_DYNAMIC_TPL);
	    List jrList = (List) template.get(PrintConstants.PRINT_PARAMETER_JREPORT_LIST);
	    if (jrList == null || PrintConstants.DYNAMIC_COLUMN_TPL.equals(dynamicTpl)) {
	    	String tplCode = (String) template.get(PrintConstants.PRINT_PARAMETER_TPL_CODE);
	    	jrList = TemplateGenerator.generateTemplate(tplCode, printParameter);
	    	template.put(PrintConstants.PRINT_PARAMETER_JREPORT_LIST, jrList);
	    }
	    List jpList = fillTemplate(parameters, jrDS, jrList, printParameter);
	    return jpList;
	}
	public List fillTemplate(Map parameters, JRDataSource jrDS, List template, Map printParameter) {
		List jpList = new ArrayList();
		try {
			if (template != null) {
				JRMapArrayDataSource jraDS = null;
				JRMapCollectionDataSource jrcDS = null;
				JREmptyDataSource jrmDs = null;
				JRBeanCollectionDataSource jrbDs = null;
				if (jrDS instanceof JRMapArrayDataSource) {
					jraDS = (JRMapArrayDataSource) jrDS;
				} else if (jrDS instanceof JRMapCollectionDataSource) {
					jrcDS = (JRMapCollectionDataSource) jrDS;
				} else if (jrDS instanceof JRBeanCollectionDataSource) {
					jrbDs = (JRBeanCollectionDataSource) jrDS;
				}else if (jrDS instanceof JREmptyDataSource) {
					jrmDs = (JREmptyDataSource) jrDS;
				}
				JasperReport jasperReport = null;
				JasperPrint jasperPrint = null;
				Iterator jrIterator = template.iterator();
				while (jrIterator.hasNext()) {
					jasperReport = (JasperReport) jrIterator.next();
					if (jraDS != null) {
						jraDS.moveFirst();
						jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, jraDS);
					} else if (jrcDS != null) {
						jrcDS.moveFirst();
						jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, jrcDS);
					} else if (jrmDs != null) {
						jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, jrmDs);
					}else if (jrbDs != null) {
						jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, jrbDs);
					}
					jpList.add(jasperPrint);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		return jpList;
	}
}
