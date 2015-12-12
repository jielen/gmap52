package com.anyi.gp.access;

import com.anyi.gp.print.service.JrPrintService;
import com.anyi.gp.print.service.PrintSetService;

public class PrintService {

	private DBSupport support;

	private JrPrintService printService;

	private PrintSetService printSetService;

	public JrPrintService getPrintService() {
		return printService;
	}

	public void setPrintService(JrPrintService printService) {
		this.printService = printService;
	}

	public PrintSetService getPrintSetService() {
		return printSetService;
	}

	public void setPrintSetService(PrintSetService printSetService) {
		this.printSetService = printSetService;
	}

	public DBSupport getSupport() {
		return support;
	}

	public void setSupport(DBSupport support) {
		this.support = support;
	}

}
