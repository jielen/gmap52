package com.anyi.gp.core.action;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.anyi.gp.BusinessException;
import com.anyi.gp.context.ApplusContext;
import com.anyi.gp.core.dao.BaseDao;
import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.xwork.ActionSupport;

public class DownloadAction extends ActionSupport {
  
  private static final long serialVersionUID = 2184672654840593767L;

  private static final String GET_DOWNLOAD_SQL = "SELECT * FROM as_upload WHERE FILE_ID=?";

	private String fileId;

	private BaseDao dao;

	public BaseDao getDao() {
		return dao;
	}

	public void setDao(BaseDao dao) {
		this.dao = dao;
	}

	public String getId() {
		return fileId;
	}

	public void setId(String id) {
		this.fileId = id;
	}

	public String execute() throws BusinessException {
		Map data = dao.queryForMapBySql(GET_DOWNLOAD_SQL, new String[] { fileId });
		String type = (String) data.get("REPORTTYPE");
		String filePath = (String) data.get("FILE_ID");
		String fileName=(String) data.get("FILE_NAME");

		HttpServletResponse response = (HttpServletResponse) ServletActionContext
				.getResponse();
		String fullPath = ApplusContext.getEnvironmentConfig().get(
				"downloadPath")
				+ "/" + filePath;
		File file = null;
		InputStream input = null;
    OutputStream out = null;
    ByteArrayOutputStream baos = null;
		try {
			file = new File(fullPath);
			if (file != null && file.exists() && file.canRead()) {
				input = new FileInputStream(file);
				baos = new ByteArrayOutputStream();
				byte[] buff = new byte[1024];
				while ((input.read(buff)) != -1) {
					baos.write(buff);
				}
				response.reset();
				setHead(response, type, fileName);
				
        out = response.getOutputStream();
				out.write(baos.toByteArray());
				out.flush();
			}
		} catch (Exception ex) {
			throw new BusinessException(ex);
		} finally {
      try {
        baos.close();
      } catch (IOException e1) {
        LOG.error(e1);
      }
			try {
				input.close();
			} catch (Exception ex) {
        LOG.error(ex);
			}
      try {
        out.close();
      } catch (IOException e) {
        LOG.error(e);
      }
		}
		return NONE;
	}

	private void setHead(HttpServletResponse response, String type,
			String fileName) {
		try {
		  response.setContentType("application/download");
			response.setHeader("Content-Disposition", "attachment;filename="
					+ java.net.URLEncoder.encode(fileName, "UTF-8"));

		} catch (Exception ex) {
		}
	}
}
