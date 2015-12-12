package com.anyi.gp.core.bean;
import java.sql.Date;
/**
 * @author shenjw
 *
 */
public class AsFile {
	
	private String fileId;
	
	private byte[] fileContent;
	private String fileName;
	private String fileDesc;
	private String fileCreator;
	private Date fileUploadTime;
	
	private String fileContentType;

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public byte[] getFileContent() {
		return fileContent;
	}

	public void setFileContent(byte[] fileContent) {
		this.fileContent = fileContent;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileContentType() {
		return fileContentType;
	}

	public void setFileContentType(String fileContentType) {
		this.fileContentType = fileContentType;
	}

	public String getFileCreator() {
		return fileCreator;
	}

	public void setFileCreator(String fileCreator) {
		this.fileCreator = fileCreator;
	}

	public String getFileDesc() {
		return fileDesc;
	}

	public void setFileDesc(String fileDesc) {
		this.fileDesc = fileDesc;
	}

	public Date getFileUploadTime() {
		return fileUploadTime;
	}

	public void setFileUploadTime(Date fileUploadTime) {
		this.fileUploadTime = fileUploadTime;
	}

}
