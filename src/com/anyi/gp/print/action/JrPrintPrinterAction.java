package com.anyi.gp.print.action;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.util.JRLoader;

import com.anyi.gp.BusinessException;
import com.anyi.gp.core.action.ServletAction;
import com.anyi.gp.print.util.PrintFileUtil;

public class JrPrintPrinterAction extends ServletAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	List jasperPrintList = new ArrayList();

	public String execute(HttpServletRequest request,
			HttpServletResponse response) throws BusinessException {
		try{
			String fileName = request.getParameter("fileName");
			File file = new File(fileName);
			try{
				if (file != null && file.exists() && file.canRead()) {
					jasperPrintList = (List)JRLoader.loadObject(file);
				}
			}catch(JRException e){
				e.printStackTrace();
			}
			if(jasperPrintList!=null){
				ServletOutputStream ouputStream = response.getOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(ouputStream);
				oos.writeObject(jasperPrintList);//将JasperPrint对象写入对象输出流中
				oos.flush();
				oos.close();
			}
			PrintFileUtil.deleteFile(fileName);//删除服务器上的暂存文件
		} catch(UnsupportedEncodingException e){
			e.printStackTrace();
		} catch(IOException e){
			e.printStackTrace();
		}
		return null;
	}

}
