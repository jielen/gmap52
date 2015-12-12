/**
 * 
 */
package com.anyi.gp.print.direct;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.anyi.gp.Pub;
import com.anyi.gp.print.bean.PrintConstants;
import com.anyi.gp.print.bean.PrintParameter;
import com.anyi.gp.print.exception.PrintingException;
import com.anyi.gp.print.util.PrintFileUtil;
import com.anyi.gp.pub.DAOFactory;
import com.anyi.gp.pub.DBHelper;
import com.anyi.gp.pub.SessionUtils;

/**
 * @author Administrator
 * 
 */
public class PrintDirectManager {

	private final static Logger log = Logger.getLogger(PrintDirectManager.class);

	public final static String dir = "printdata";

	public static String server_address = null;

	public static String sdirect = null;

	public static boolean isDirect = false;

	/**
	 * 
	 */
	public PrintDirectManager() {

	}

	/**
	 * 
	 * @param response
	 * @param jasperPrintList
	 * @param printParameter
	 */
	public static void direct(HttpServletResponse response, List jasperPrintList, PrintParameter printParameter) {
		String fileName = getFileName(printParameter);
		save(response, jasperPrintList, printParameter, fileName);
		try {
			HttpServletRequest request = (HttpServletRequest)printParameter.get(PrintConstants.PRINT_PARAMETER_REQUEST);
			String webroot = Pub.getWebRoot(request);
			String url = "http://" + server_address + webroot + "/jrPrintDirect";
			RequestDispatcher dispatcher = request.getRequestDispatcher(url);
			request.setAttribute("fileName", fileName);
			dispatcher.forward(request, response);
		} catch (IOException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		} catch (ServletException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param response
	 * @param jasperPrintList
	 * @param printParameter
	 * @param fileName
	 */
	public static void save(HttpServletResponse response, List jasperPrintList,
			PrintParameter printParameter, String fileName) {
		try {
			printParameter.remove(PrintConstants.PRINT_PARAMETER_REQUEST);
			printParameter.remove(PrintConstants.PRINT_PARAMETER_RESPONSE);
			printParameter.addParameter("JasperReportPath", PrintFileUtil
					.getAbsoluteJasperReportPath());
			jasperPrintList.add(printParameter.getParameter());
			PrintSaver.saveObject(jasperPrintList, fileName);
			jasperPrintList.clear();
			jasperPrintList = null;
		} catch (PrintingException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * @param fileName
	 * @return
	 */
	public static Object load(String fileName) {
		try {
			return PrintLoader.loadObject(fileName);
		} catch (PrintingException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param printParameter
	 * @return
	 */
	public static String getFileName(PrintParameter printParameter) {
		HttpServletRequest request = (HttpServletRequest) printParameter.get(PrintConstants.PRINT_PARAMETER_REQUEST);
		String svUserId = SessionUtils.getAttribute(request.getSession(), "svUserID");
		long nowtime = System.currentTimeMillis();
		String fileName = String.valueOf(nowtime) + "_" + svUserId;
		String printDataPath = PrintFileUtil.getJasperReportPath() + dir;
		PrintFileUtil.createDirectory(printDataPath);
		fileName = printDataPath + File.separator + fileName + ".print";
		File file = new File(fileName);
		fileName = file.getAbsolutePath();
		return fileName;
	}

	/**
	 * 
	 * @return
	 */
	public static boolean isDirect() {
		if (sdirect != null) {
			return isDirect;
		}
		String address = null;
		String isUse = null;
		String sql = "SELECT ADDRESS, IS_USE FROM AS_PRINT_SERVER";
		Connection myConn = null;
		Statement sm = null;
		ResultSet rs = null;
		try {
			myConn = DAOFactory.getInstance().getConnection();
			sm = myConn.createStatement();
			rs = sm.executeQuery(sql);
			if (rs.next()) {
				address = rs.getString("ADDRESS");
				isUse = rs.getString("IS_USE");
			}
		} catch (SQLException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		} finally {
			DBHelper.closeConnection(myConn, sm, rs);
		}
		if ("Y".equalsIgnoreCase(isUse)) {
			isDirect = true;
			server_address = address;
			sdirect = "Y";
		} else {
			isDirect = false;
			server_address = address;
			sdirect = "N";
		}
		return isDirect;
	}

}
