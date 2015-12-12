package com.anyi.gp.core.action;

import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import com.anyi.gp.access.FileService;
import com.anyi.gp.core.bean.AsFile;
import com.opensymphony.webwork.interceptor.ServletResponseAware;
import com.opensymphony.xwork.ActionSupport;

/**
 * @author shenjw
 *
 */
public class FileDownloadAction extends ActionSupport implements
		ServletResponseAware {

	////private final Logger log = Logger.getLogger(this.getClass());

	private static final long serialVersionUID = 6154973269813444328L;

	private String fileid;

	private HttpServletResponse response;

	private FileService service;
	
  public FileDownloadAction(){}
  
	public FileService getService() {
    return service;
  }

  public void setService(FileService service) {
    this.service = service;
  }

  public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}
  
  public String getFileid() {
    return fileid;
  }

  public void setFileid(String fileid) {
    this.fileid = fileid;
  }
	public String execute()throws Exception  {

		AsFile file = service.downloadFile(fileid);
	
		if(file == null) return NONE;
		
		response.setContentType(file.getFileContentType());
		OutputStream os = response.getOutputStream();
		os.write(file.getFileContent());
		os.flush();
		os.close();
		
		return NONE;
	}

}
