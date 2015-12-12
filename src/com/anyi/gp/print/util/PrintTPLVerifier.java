/*
 * Created on 2005-6-13
 *
 */
package com.anyi.gp.print.util;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JRVerifier;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;


/**
 * @author zhangyingwei
 * 检查模板文件类
 */
public class PrintTPLVerifier extends JRVerifier{

	/**
	 *
	 */
	private static final String encoding = "GBK";

	/**
	 *
	 */
	private JasperDesign jasperDesign = null;

	/**
	 * @param jasperDesign
	 */
	protected PrintTPLVerifier(JasperDesign jasperDesign){
		super(jasperDesign);
		this.jasperDesign = jasperDesign;
	}

	/**
	 * @param textData
	 * @return
	 */
	public static List verifyTPL(String textData){
		List result = new ArrayList();
		StringBuffer message = new StringBuffer();
		try {
			ByteArrayInputStream stream = new ByteArrayInputStream(textData.getBytes(encoding));
			JasperDesign jasperDesign = JRXmlLoader.load(stream);
			result.addAll(verifyTPL(jasperDesign));
		} catch (UnsupportedEncodingException e) {
			message.append(e.getMessage());
		} catch (JRException e) {
			message.append(e.getMessage());
		}
		if(message.length() > 0){
			result.add(message.toString());
		}
		return result;
	}

	/**
	 * @param jasperDesign
	 * @return
	 */
	public static List verifyTPL(JasperDesign jasperDesign){
		PrintTPLVerifier printTPLVerifier = new PrintTPLVerifier(jasperDesign);
		return printTPLVerifier.verifyTPLDesign();
	}

	/**
	 * @return
	 */
	protected List verifyTPLDesign(){
		List result = new ArrayList();
		StringBuffer message = new StringBuffer();
		try {
			if (jasperDesign == null)
			{
				message.append("Report Design Empty!");
			}
			else{
				Collection col = JasperCompileManager.verifyDesign(jasperDesign);
				Iterator i = col.iterator();

				while (i.hasNext())
				{
					message.append(i.next().toString());
					message.append("\n");
				}

				if (message.length() == 0)
				{
					message.append("valid");
				}
			}
		} catch (Exception e) {
			message.append(e.getMessage());
		}
		if(message.toString().equals("valid")){
			result.addAll(this.verifyTPLReport());
		}else{
			result.add(message.toString());
		}
		return result;
	}

	/**
	 *
	 * @return
	 */
	protected List verifyTPLReport(){
		List result = new ArrayList();
		StringBuffer message = new StringBuffer();
		JasperReport jasperReport = null;
		try {
			jasperReport = JasperCompileManager.compileReport(jasperDesign);
		} catch (JRException e) {
			message.append(e.getMessage());
		}
		if(message.length() == 0){
			message.append("valid");
			result.add(message.toString());
			result.add(jasperReport);
		}else{
			result.add(message.toString());
		}
		return result;
	}
}
