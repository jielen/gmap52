package com.anyi.gp.print.service.imp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.anyi.gp.context.ApplusContext;
import com.anyi.gp.core.dao.BaseDao;
import com.anyi.gp.print.data.DataConvertor;
import com.anyi.gp.print.service.PrintSetService;
import com.anyi.gp.print.util.PrintTPLLoader;
import com.anyi.gp.print.util.PrintTPLSaver;
import com.anyi.gp.print.util.PrintTPLVerifier;
import com.anyi.gp.pub.DAOFactory;
import com.anyi.gp.pub.DBHelper;

public class PrintSetServiceImp implements PrintSetService {
	private static final Log logger = LogFactory
			.getLog(PrintSetServiceImp.class);

	private BaseDao baseDAO;

	public BaseDao getBaseDAO() {
		return baseDAO;
	}

	public void setBaseDAO(BaseDao baseDAO) {
		this.baseDAO = baseDAO;
	}

	public String getPrintSetInfo(String setInfo) {
		String result = "";
		try {
			Map map = null;
			DataConvertor dataConvertor = new DataConvertor();
			List list = dataConvertor.convertData(setInfo, 0, null);
			if (list != null && list.size() > 0) {
				map = (Map) list.get(0);
			}
			if (map != null) {
				String userID = (String) map.get("UserID");
				String compoID = (String) map.get("CompoID");
				String pageType = (String) map.get("PageType");
				result = selectPrintSetInfo(userID, compoID, pageType);
			}
		} catch (Exception e) {
			logger.error(e);
			throw new RuntimeException(
					"Class PrintSetServiceImp , Method getPrintSetInfo(String setInfo) Exception : "
							+ e.toString() + "\n");
		}
		return result;
	}

	public String selectPrintSetInfo(String userID, String compoID,
			String pageType) {
		StringBuffer sf = new StringBuffer();
		Connection conn = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		String sql = "";
		try {
			conn = DAOFactory.getInstance().getConnection();
			sql = "SELECT USER_ID, COMPO_ID, PAGE_TYPE, TPL_CODE, EXPORT_TYPE, IS_PREVIEW FROM AS_PRINT_JASPERPRINTSET  WHERE USER_ID = ? AND COMPO_ID = ? AND PAGE_TYPE = ?";
			st = conn.prepareStatement(sql);
			st.setString(1, userID);
			st.setString(2, compoID);
			st.setString(3, pageType);
			rs = st.executeQuery();
			while (rs.next()) {
				sf.append("<delta>");
				sf.append("<entity name = \"head\">");
				sf.append("<field name = \"UserID\" value = \""
						+ rs.getString("USER_ID") + "\" />");
				sf.append("<field name = \"CompoID\" value = \""
						+ rs.getString("COMPO_ID") + "\" />");
				sf.append("<field name = \"PageType\" value = \""
						+ rs.getString("PAGE_TYPE") + "\" />");
				sf.append("<field name = \"TplCode\" value = \""
						+ rs.getString("TPL_CODE") + "\" />");
				sf.append("<field name = \"ExportType\" value = \""
						+ rs.getString("EXPORT_TYPE") + "\" />");
				sf.append("<field name = \"IsPreview\" value = \""
						+ rs.getString("IS_PREVIEW") + "\" />");
				sf.append("</entity>");
				sf.append("</delta>");
			}
		} catch (SQLException e) {
			logger.error(e);
			throw new RuntimeException(
					"Method getPrintSetInfo(String userID, String compoID, String pageType) Exception : "
							+ e.toString() + "\n" + sql + "\n");
		} finally {
			DBHelper.closeConnection(conn, st, rs);
		}
		return sf.toString();
	}

	public void setPrintSetInfo(String setInfo) {
		try {
			Map map = null;
			DataConvertor dataConvertor = new DataConvertor();
			List list = dataConvertor.convertData(setInfo, 0, null);
			if (list != null && list.size() > 0) {
				map = (Map) list.get(0);
			}
			if (map != null) {
				String userID = (String) map.get("UserID");
				String compoID = (String) map.get("CompoID");
				String pageType = (String) map.get("PageType");
				String tplCode = (String) map.get("TplCode");
				String exportType = (String) map.get("ExportType");
				String isPreview = (String) map.get("IsPreview");
				Object[] obj = {tplCode, exportType, isPreview, userID, compoID, pageType };
				String sql = "UPDATE AS_PRINT_JASPERPRINTSET SET TPL_CODE = ?, EXPORT_TYPE = ?, IS_PREVIEW = ? WHERE USER_ID = ? AND COMPO_ID = ? AND PAGE_TYPE = ?";
				
				int num = DBHelper.executeSQL(sql, obj);
				if(num == 0){
				  sql = "insert into AS_PRINT_JASPERPRINTSET(TPL_CODE, EXPORT_TYPE, IS_PREVIEW, USER_ID, COMPO_ID, PAGE_TYPE) values(?,?,?,?,?,?)";
				  DBHelper.executeSQL(sql, obj);
				}
			}
		} catch (Exception e) {
			logger.error(e);
			throw new RuntimeException(
					"Class PrintSetServiceImp , Method setPrintSetInfo(String setInfo) Exception : "
							+ e.toString() + "\n");
		}

	}

	public List getCompoTplInfo(String condition, String componame) {
		if (condition == null) {
			condition = "";
		}
		String sql = "SELECT * FROM AS_PRINT_JASPERTEMP WHERE PRN_COMPO_ID = ? "
				+ condition;
		Object[] obj = { componame };
		List result = this.getBaseDAO().queryForListBySql(sql, obj);
		return result;
	}

	public String getTextDataFromFile(String prn_tpl_code) {
		String filePath = PrintTPLLoader.getJasperReportPath();
		if (!filePath.endsWith(File.separator)) {
			filePath += File.separator;
		}
		String fileName = filePath + prn_tpl_code + ".html";
		File file = new File(fileName);
		String inTextData = new String();
		if (!file.exists()) {
			inTextData = "false:" + filePath;
		} else {
      InputStreamReader isr = null;
			try {
				String record = null;
				isr = new InputStreamReader(
						new FileInputStream(fileName), "GBK");
				BufferedReader br = new BufferedReader(isr);
				record = new String();
				while ((record = br.readLine()) != null) {
					inTextData += record;
				}
				br.close();
				isr.close();
			} catch (IOException e) {
				throw new RuntimeException(
						"Method getTextDataFromFile(String prn_tpl_code) Exception : "
								+ e.getMessage() + "\n");
			} finally{
        try {
          isr.close();
        } catch (IOException e) {
          logger.error(e);
        }
      }
			inTextData = inTextData.substring(12, (inTextData.length() - 14));
		}
		return inTextData;
	}

	public String setJasperTextData(String tableName, String fieldName,
			String condition, String textData, String rowCount) {
		String path = ApplusContext.getEnvironmentConfig().get("reportspath");
		if (!path.endsWith(File.separator)) {
			path += File.separator;
		}
		int index = condition.indexOf("=");
		String fileName = condition.substring(index + 3, condition.length() - 1);
		if (fieldName.endsWith("XML")) {
      if(textData.indexOf("com.anyi.erp.")>0){
        textData = textData.replaceAll("com.anyi.erp.", "com.anyi.gp.");
      }
      if(textData.indexOf("newDate")>0){
        textData = textData.replaceAll("newDate", "new Date");
      }
      saveToFile(path, fileName + ".xml", textData);
			String message = "";
			JasperReport jasperReport = null;
			List result = PrintTPLVerifier.verifyTPL(textData);
			if (result != null && result.size() > 0) {
				message = (String) result.get(0);
			}
			try {
				if (message != null && message.equals("valid")) {
					if (result != null && result.size() > 1) {
						jasperReport = (JasperReport) result.get(1);
					}
					if (jasperReport != null) {
						PrintTPLSaver.saveTemplate(jasperReport, fileName);
					}
				}
			} catch (JRException e) {
				message = e.getMessage();
			}
			return message;
		}
		StringBuffer content = new StringBuffer();
		content.append("<html>");
		content.append("<body>");
		content.append(textData);
		content.append("</body>");
		content.append("</html>");
		saveToFile(path, fileName + ".html", content.toString());
		updateRowCount(rowCount, condition);
		return "valid";
	}

	private void updateRowCount(String temp, String condition) {
		String sql = "update AS_PRINT_JASPERTEMP set PRN_TPL_FIXROWCOUNT = ? where " + condition;
		Object[] obj = {temp};
		this.getBaseDAO().executeBySql(sql, obj);
	}

	private void saveToFile(String filePath, String fileName, String textData) {
    OutputStreamWriter osw = null;
    try {
			createDirectory(filePath);
		    osw = new OutputStreamWriter(new FileOutputStream(filePath
		        + fileName), "GBK");
		    int i = 0, j = 8000;
		    while (i < textData.length()) {
		        if (j >= textData.length()) {
		        	j = textData.length();
		        }
		        String tmpText = textData.substring(i, j);
		        i += 8000;
		        j += 8000;
		        osw.write(tmpText);
		        osw.flush();
		    }
		} catch (IOException ioe) {
		      ioe.printStackTrace();
		      throw new RuntimeException(ioe);
		} finally{
      try {
        osw.close();
      } catch (IOException e) {
        logger.error(e);
      }
    }
		
	}
	/**
	 * 新建目录，只能建一层下级子目录，如果建两级子目录的话就会报错，并且不能建
	 * @param filePath 新建的子目录的路径
	 */
	private static void createDirectory(String filePath) {
	    File filepath = new File(filePath);
	    if (!filepath.exists()) {
	    	filepath.mkdir();
	    }
	}
}
