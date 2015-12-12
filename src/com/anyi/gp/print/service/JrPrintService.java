package com.anyi.gp.print.service;

import java.util.List;
import java.util.Map;

import com.anyi.gp.print.bean.PrintParameter;

public interface JrPrintService {
	/**
	 * 生成jasper打印数据List
	 * @param printParameter
	 * @return List
	 */
	public List getJasperPrint(PrintParameter printParameter);
	
	/**
	 * 生成打印数据List
	 * @param printParameter
	 * @return
	 */
	public List generatePrintDataList(Map printParameter);
	
	/**
	 * 生成模板List
	 * @param printParameter
	 * @return
	 */
	public List generateTemplateList(Map printParameter);
}
