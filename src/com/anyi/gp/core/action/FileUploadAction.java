package com.anyi.gp.core.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.anyi.gp.Pub;
import com.anyi.gp.access.FileService;
import com.anyi.gp.core.bean.AsFile;
import com.anyi.gp.util.StringTools;
import com.opensymphony.webwork.interceptor.ServletResponseAware;
import com.opensymphony.xwork.ActionSupport;

/**
 * @author shenjw
 *
 */
public class FileUploadAction extends ActionSupport implements ServletResponseAware {

  private static final Logger log = Logger.getLogger(FileUploadAction.class);

  private static final long serialVersionUID = 6154973269813444328L;

  private File addfile;

  private String addfileContentType;

  private String addfileFileName;

  private String fileName;

  private HttpServletResponse response;

  private FileService service;

  public FileUploadAction() {

  }

  public FileService getService() {
    return service;
  }

  public void setService(FileService service) {
    this.service = service;
  }

  public void setServletResponse(HttpServletResponse response) {
    this.response = response;

  }

  public File getAddfile() {
    return addfile;
  }

  public void setAddfile(File addfile) {
    this.addfile = addfile;
  }

  public String getAddfileContentType() {
    return addfileContentType;
  }

  public void setAddfileContentType(String addfileContentType) {
    this.addfileContentType = addfileContentType;
  }

  public String getAddfileFileName() {
    return addfileFileName;
  }

  public void setAddfileFileName(String addfileFileName) {
    this.addfileFileName = addfileFileName;
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
      
      file.setFileContent(filedata);
      String fName = URLDecoder.decode(fileName, "UTF-8");
      file.setFileName(fName);
      file.setFileContentType(addfileContentType);

      tempFileId = Pub.getUID();
      file.setFileId(tempFileId);
      service.uploadFile(file);
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

  private void back(HttpServletResponse response, boolean success, String message) {
    response.setContentType(StringTools.CONTENT_TYPE);
    PrintWriter out = null;
    try {
      out = new PrintWriter(response.getOutputStream());
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
    } catch (IOException ex) {
      log.error(ex.toString(), ex);
    } finally{
      out.close();
    }
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

}
