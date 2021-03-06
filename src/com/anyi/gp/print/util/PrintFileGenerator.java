/* Generated by Together */
/* $Id: PrintFileGenerator.java,v 1.2 2009/07/10 08:36:56 liuxiaoyong Exp $ */

package com.anyi.gp.print.util;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.anyi.gp.print.bean.PrintParameter;
import com.anyi.gp.print.direct.PrintDirectManager;
import com.anyi.gp.print.exception.PrintingException;

/**
 *
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2002</p>
 *
 * <p>Company: </p>
 *
 * @author zhangyw
 * @version 1.0
 */
public class PrintFileGenerator {

  public static void generatePrintingFile(HttpServletResponse response,
    List jasperPrintList, PrintParameter printParameter) throws PrintingException {
    if (PrintDirectManager.isDirect()) {
      PrintDirectManager.direct(response, jasperPrintList, printParameter);
    } else {
      PrintExporter.exportFile(response, jasperPrintList, printParameter);
    }
  }
  
  public static void generatePrintingFile(HttpServletResponse response,
    List jasperPrintList, Map printParameter) throws PrintingException {
      PrintExporter.exportFile(response, jasperPrintList, new PrintParameter(printParameter));
  }
  

}
