package com.anyi.gp.print.service;

import java.util.List;
import java.util.Map;

import com.anyi.gp.print.bean.PrintParameter;

public interface JrPrintService {
	/**
	 * ����jasper��ӡ����List
	 * @param printParameter
	 * @return List
	 */
	public List getJasperPrint(PrintParameter printParameter);
	
	/**
	 * ���ɴ�ӡ����List
	 * @param printParameter
	 * @return
	 */
	public List generatePrintDataList(Map printParameter);
	
	/**
	 * ����ģ��List
	 * @param printParameter
	 * @return
	 */
	public List generateTemplateList(Map printParameter);
}
