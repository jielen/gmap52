package com.anyi.gp.core.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.anyi.gp.Pub;
import com.anyi.gp.access.FileService;
import com.anyi.gp.core.bean.AsFile;
import com.anyi.gp.pub.SessionUtils;
import com.anyi.gp.util.StringTools;
import com.opensymphony.webwork.interceptor.ServletRequestAware;
import com.opensymphony.webwork.interceptor.ServletResponseAware;
import com.opensymphony.xwork.ActionSupport;

public class ResourceUploadAction extends ActionSupport implements
		ServletResponseAware , ServletRequestAware{

	private static final Logger log = Logger
			.getLogger(ResourceUploadAction.class);

	private static final long serialVersionUID = 6154973269813444328L;

	private File addfile;

	private String fileFileName;

	private String fileDesc;

	private Date fileUploadTime;

	private HttpServletResponse response;

	private FileService service;
	
	private HttpServletRequest request;

	public ResourceUploadAction() {

	}

	public File getAddfile() {
		return addfile;
	}

	public void setAddfile(File addfile) {
		this.addfile = addfile;
	}

	public String getFileDesc() {
		return fileDesc;
	}

	public void setFileDesc(String fileDesc) {
		this.fileDesc = fileDesc;
	}

	public void setServletResponse(HttpServletResponse response) {
		this.response = response;

	}

	public FileService getService() {
		return service;
	}

	public void setService(FileService service) {
		this.service = service;
	}

	public String execute() {
		String tempFileId = "";
    InputStream is = null;
		try {
			AsFile file = new AsFile();
			is = new FileInputStream(addfile);
			int len = (new Long(addfile.length()).intValue());
			byte[] filedata = new byte[len];
			is.read(filedata);
			
			fileUploadTime=   new   java.sql.Date((new   java.util.Date()).getTime());  
			file.setFileContent(filedata);
			file.setFileName(fileFileName);
			file.setFileDesc(fileDesc);
			file.setFileCreator((String)SessionUtils.getAttribute(request,"svUserID"));
			file.setFileUploadTime(fileUploadTime);
			tempFileId = Pub.getUID();
			file.setFileId(tempFileId);
			service.uploadResource(file);
			back(response, true, tempFileId);
      
		} catch (Exception ex) {
			back(response, false, ex.getMessage());
		} finally{
      try {
        is.close();
      } catch (IOException e) {
        LOG.error(e);
      }
    }
		return NONE;
	}

	private void back(HttpServletResponse response, boolean success,
			String message) {
		response.setContentType(StringTools.CONTENT_TYPE);
		try {
			PrintWriter out = new PrintWriter(response.getOutputStream());
			out.println("<html>");
			out.println("<body>");
			out.println("<xml id='result'");
			out.println(" success=\"" + success + "\">");
			out.println(message);
			out.println("</xml>");
			out.println("<script>");
			out.println("var doc = document.getElementById(\"result\");");
			out.println("parent.fileBack(doc);");
			out.println("</script>");
			out.println("</body></html>");
			out.close();
		} catch (IOException ex) {
			log.error(ex.toString(), ex);
		}
	}

	public String getAddfileFileName() {
		return fileFileName;
	}

	public void setAddfileFileName(String fileFileName) {
		this.fileFileName = fileFileName;
	}

	public void setServletRequest(HttpServletRequest request) {
		// TCJLODO Auto-generated method stub
		this.request = request;
		
	}

}
