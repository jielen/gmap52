package com.anyi.gp.core.action;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Date;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.anyi.gp.access.FileExportService;
import com.anyi.gp.context.ApplusContext;
import com.anyi.gp.core.dao.BaseDao;
import com.anyi.gp.pub.SessionUtils;
import com.opensymphony.webwork.interceptor.ServletRequestAware;

public class ExportToReportAction extends AjaxAction implements
		ServletRequestAware {

	private static final long serialVersionUID = 5293179108488233769L;

	private static final Logger log = Logger
			.getLogger(ExportToReportAction.class);

	private static String EXPORT_TO_REPORT = "gmap-common.publishToReport";

	private String ruleID;

	private String condition;

	private String compoName;

	private String reportType;

	private Date createTime;

	private String creator;

	private String portletId;
	
	private String title;

	private HttpServletRequest request;

	private BaseDao baseDao;

	public String getCompoName() {
		return compoName;
	}

	public void setCompoName(String compoName) {
		this.compoName = compoName;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getReportType() {
		return reportType;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getRuleID() {
		return ruleID;
	}

	public void setRuleID(String ruleID) {
		this.ruleID = ruleID;
	}

	public String getPortletId() {
		return portletId;
	}

	public void setPortletId(String portletId) {
		this.portletId = portletId;
	}

	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setServletRequest(HttpServletRequest request) {
		// TCJLODO Auto-generated method stub
		this.request = request;
		this.creator = SessionUtils.getAttribute(request, "svUserID");
	}

	public BaseDao getBaseDao() {
		return baseDao;
	}

	public void setBaseDao(BaseDao baseDao) {
		this.baseDao = baseDao;
	}

	private void savePublishInfo(String filePath) throws SQLException {
		Map param = new HashMap();
		long time = System.currentTimeMillis();
		Date date = new Date(time);
		param.put("id", String.valueOf(System.currentTimeMillis()));
		param.put("compoName", this.compoName);
		param.put("reportType", this.reportType);
		param.put("createTime", date);
		param.put("creator", this.creator);
		param.put("title", this.title);
		param.put("filePath", filePath);
		baseDao.insert(EXPORT_TO_REPORT, param);
	}

	private String makeExcelPublish(HSSFWorkbook book) {
		String publishPath = ApplusContext.getEnvironmentConfig().get(
				"publishPath");
		File temp = new File(publishPath);
		if (!temp.exists()) {
			temp.mkdir();
		}
		String fileName = compoName + "_" + System.currentTimeMillis() + ".xls";
		OutputStream output = null;
    try {
      output = new FileOutputStream(publishPath + "/" + fileName);
      book.write(output);
      output.flush();
    } catch (FileNotFoundException e) {
      log.error(e);
    }catch (IOException e) {
      log.error(e);
    }finally{
  		try {
        output.close();
      } catch (IOException e) {
        log.error(e);
      }
    }
		return fileName;
	}

	public String doExecute() {
		String flag = "true";
		String dataStr = "success";
		try {
			if (this.reportType.equals("excel")) {
				HSSFWorkbook book = FileExportService.createExcelBySqlId(
						compoName, ruleID, condition);
				String filePath = makeExcelPublish(book);
				savePublishInfo(filePath);
			}
		} catch (Exception ex) {
			flag = "false";
			log.debug(ex);
			dataStr = ex.getMessage();
		}
		this.setResultstring(this.wrapResultStr(flag, dataStr));
		return this.SUCCESS;
	}
}
