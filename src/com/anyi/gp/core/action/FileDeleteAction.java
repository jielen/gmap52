package com.anyi.gp.core.action;

import org.apache.log4j.Logger;

import com.anyi.gp.access.FileService;
import com.anyi.gp.core.bean.AsFile;

/**
 * @author shenjw
 *
 */
public class FileDeleteAction extends AjaxAction {

	private static final Logger log = Logger.getLogger(FileDeleteAction.class);

	private static final long serialVersionUID = 6154973269813444328L;

	private String fileid;

	private FileService service;
	
  public FileDeleteAction(){}
  
	public FileService getService() {
    return service;
  }

  public void setService(FileService service) {
    this.service = service;
  }

  public String getFileid() {
		return fileid;
	}

	public void setFileid(String fileid) {
		this.fileid = fileid;
	}

	public String doExecute() {
		
		resultstring = "false";
		try {
      AsFile file = new AsFile();
      file.setFileId(fileid);
      service.deleteFile(file);
			resultstring = "true";
			
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
    
		return SUCCESS;
	}

}
