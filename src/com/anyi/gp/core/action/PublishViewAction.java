package com.anyi.gp.core.action;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.anyi.gp.context.ApplusContext;
import com.anyi.gp.core.dao.BaseDao;
import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.xwork.ActionSupport;

public class PublishViewAction extends ActionSupport {

  private static final long serialVersionUID = -7290438471420829707L;

  private static final String GET_PUBLISH_SQL = "SELECT * FROM ap_publish WHERE ID = ? ";

	private String id;

	private BaseDao dao;

	public BaseDao getDao() {
		return dao;
	}

	public void setDao(BaseDao dao) {
		this.dao = dao;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String execute() throws Exception {
		Map data = dao.queryForMapBySql(GET_PUBLISH_SQL, new String[] { id });
		String type = (String) data.get("REPORTTYPE");
		String filePath = (String) data.get("FILEPATH");
		
		HttpServletResponse response = (HttpServletResponse) ServletActionContext.getResponse();
		String fullPath = ApplusContext.getEnvironmentConfig().get("publishPath") + "/" + filePath;
		File file = null;
		FileInputStream input = null;
		try {
			file = new File(fullPath);
			if (file != null && file.exists() && file.canRead()) {
				input = new FileInputStream(file);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				byte[] buff = new byte[1024];
				while ((input.read(buff)) != -1) {
					baos.write(buff);
				}
				response.reset();
				setHead(response, type, filePath);
				OutputStream out = response.getOutputStream();
				out.write(baos.toByteArray());
				out.flush();
				out.close();	
			}
		} finally {
			try {
				input.close();
			} catch (Exception ex) {
			}
		}
		return NONE;
	}
	
	private void setHead(HttpServletResponse response, String type, String fileName) {
		if (type.equals("excel")) {
		    response.setContentType("application/vnd.ms-excel"); 
		    response.setHeader("Content-Disposition","inline; filename=" + fileName + ".xls");			
		}
		if (type.equals("pdf")) {
		    response.setContentType("application/pdf"); 
		    response.setHeader("Content-Disposition","inline; filename=" + fileName + ".pdf");				
		}
	}

}
