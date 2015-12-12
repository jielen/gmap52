package com.anyi.gp.core.action;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import com.opensymphony.xwork.ActionSupport;

public abstract class AjaxAction extends ActionSupport{

	private static final long serialVersionUID = 389942129430342430L;
	
	protected InputStream inputStream;
	protected String resultstring;
	
	public InputStream getInputStream(){
		return inputStream;
	}
	
	public void setInputStream(InputStream inputStream){
		this.inputStream = inputStream;
	}
	
	public String getResultstring(){
		return resultstring;
	}
	
	public void setResultstring(String resultstring){
		this.resultstring = resultstring;
	}
	
	public void before(){};
	
	public abstract String doExecute() throws Exception ;
	
	public void after(){
		try {
			if(resultstring != null)
				inputStream  = new ByteArrayInputStream(resultstring.getBytes("UTF-8"));
			else
				inputStream = new ByteArrayInputStream("".getBytes());
		} 
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			//TODO: 
		}
	}
	
	public String execute() throws Exception{
		before();
		String result = doExecute();
		after();
		return result;
	}
	
	public String wrapResultStr(String flag, String dataStr) {
		StringBuffer buffer = new StringBuffer().append("<response success=\"" + flag + "\">\n");
		buffer.append("<![CDATA[");
		buffer.append(dataStr);
		buffer.append("]]>\n");
		buffer.append("</response>");
		return buffer.toString();
	}
	
	
}
