package com.anyi.gp.print.template;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRElement;
import net.sf.jasperreports.engine.JRGroup;
import net.sf.jasperreports.engine.JRTextElement;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignElement;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignImage;
import net.sf.jasperreports.engine.design.JRDesignStaticText;
import net.sf.jasperreports.engine.design.JasperDesign;

import org.apache.log4j.Logger;

import com.anyi.gp.context.ApplusContext;
import com.anyi.gp.license.LicenseManager;
import com.anyi.gp.print.bean.PrintConstants;
import com.anyi.gp.print.bean.PrintParameter;
import com.anyi.gp.print.data.PrintDataSourceFactory;
import com.anyi.gp.print.exception.PrintingException;
import com.anyi.gp.print.util.PrintCompiler;
import com.anyi.gp.print.util.PrintTPLLoader;
import com.anyi.gp.print.util.PrintTPLUtil;

/**
 * 
 * @author zhangyw
 * @version 1.0
 */
public class TemplateGenerator {

	private static Logger log = Logger.getLogger(TemplateGenerator.class);

	public TemplateGenerator() {
	}

	/**
	 * 
	 * @param printParameter
	 *            HashMap
	 * @return List
	 */
	public List generateWithTemplateList(Map printParameter) {
		List template = new ArrayList();
		try {
			String tplCode = (String) printParameter
					.get(PrintConstants.PRINT_PARAMETER_TPL_CODE);
			List tplCodeList = PrintTPLUtil.splitString(tplCode, ",");
			if (tplCodeList != null) {
				Iterator iterator = tplCodeList.iterator();
				while (iterator.hasNext()) {
					tplCode = (String) iterator.next();
					if (!tplCode.equals("")) {
						Map map = new HashMap();
						map.put(PrintConstants.PRINT_PARAMETER_TPL_CODE,
								tplCode);
						template.add(map);
						PrintDataSourceFactory.getFixRowCount(map,
								printParameter);
					}
				}
			}
		} catch (Exception e) {
			log.error(e);
			throw new RuntimeException(
					"Class TemplateGenerator, Method generateWithTemplateList(HashMap printParameter) Error :"
							+ e.getMessage() + "\n");
		}
		return template;
	}

	/**
	 * 
	 * @param printParameter
	 *            HashMap
	 * @return List
	 */
	public List generateNoTemplateList(Map printParameter) {
		List list = new ArrayList();
		try {
			JasperDesign design = (JasperDesign) printParameter
					.get(PrintConstants.PRINT_PARAMETER_NOTEMPLATE_DESIGN);
			if (design != null) {
				List template = new ArrayList();
				HttpServletRequest request = (HttpServletRequest) printParameter
						.get(PrintConstants.PRINT_PARAMETER_REQUEST);
				design = addNotRegisterFlag(design, request);
				JasperReport report = PrintCompiler.compileDesign(design);
				template.add(report);
				Map map = new HashMap();
				map.put(PrintConstants.PRINT_PARAMETER_JREPORT_LIST, template);
				list.add(map);
			}
		} catch (PrintingException e) {
			log.error(e);
			throw new RuntimeException(
					"Class TemplateGenerator, Method generateNoTemplateList(HashMap printParameter) Error :"
							+ e.getMessage() + "\n");
		}
		return list;
	}

	/**
	 * 
	 * @param tplCode
	 *            String
	 * @param printParameter
	 *            PrintParameter
	 * @return List
	 */
	public static List generateTemplate(String tplCode,
			PrintParameter printParameter) {
		if (printParameter != null) {
			return generateTemplate(tplCode, printParameter.getParameter());
		}
		return null;
	}

	/**
	 * 
	 * @param tplCode
	 *            String
	 * @param printParameter
	 *            HashMap
	 * @return List
	 */
	public static List generateTemplate(String tplCode, Map printParameter) {
		TemplateGenerator templateGenerator = new TemplateGenerator();
		return templateGenerator.generateTemplateList(tplCode, printParameter);
	}

	/**
	 * 
	 * @param tplCode
	 *            String
	 * @param printParameter
	 *            PrintParameter
	 * @return List
	 */
	public List generateTemplateList(String tplCode,
			PrintParameter printParameter) {
		if (printParameter != null) {
			return generateTemplateList(tplCode, printParameter.getParameter());
		}
		return null;
	}

	/**
	 * 
	 * @param tplCode
	 *            String
	 * @param printParameter
	 *            HashMap
	 * @return List
	 */
	public List generateTemplateList(String tplCode, Map printParameter) {
		String dynamicTpl = (String) printParameter
				.get(PrintConstants.PRINT_PARAMETER_DYNAMIC_TPL);
		if (PrintConstants.DYNAMIC_COLUMN_TPL.equalsIgnoreCase(dynamicTpl)) {
			return generateTemplateDynamicColumn(tplCode, printParameter);
		} else {
			return generateTemplateNoDynamicColumn(tplCode, printParameter);
		}
	}

	public List generateTemplateNoDynamicColumn(String tplCode,
			Map printParameter) {
		List template = new ArrayList();
		try {
			JasperReport jasperReport = PrintTPLLoader.loadTemplate(tplCode,
					printParameter);
			if (isNeedSplit(jasperReport)) {
				List jasperDesignList = splitTemplate(tplCode, printParameter);
				JasperDesign design = null;
				HttpServletRequest request = (HttpServletRequest) printParameter
						.get(PrintConstants.PRINT_PARAMETER_REQUEST);
				if (jasperDesignList != null) {
					for (int i = 0; i < jasperDesignList.size(); i++) {
						design = (JasperDesign) jasperDesignList.get(i);

						TemplateGenerator tg = new TemplateGenerator();
						design = tg.addNotRegisterFlag(design, request);
						jasperReport = PrintCompiler.compileDesign(design);
						template.add(jasperReport);
					}
				}
			} else {
				template.add(addNotRegisterFlag(tplCode, printParameter));
			}
		} catch (Exception e) {
			log.error(e);
			throw new RuntimeException(
					"Class TemplateGenerator, Method generateTemplateNoDynamicColumn(String tplCode, HashMap printParameter) Error :"
							+ e.getMessage() + "\n");
		}
		return template;
	}

	/**
	 * 
	 * @param tplCode
	 *            String
	 * @param printParameter
	 *            HashMap
	 * @return List
	 */
	public List generateTemplateDynamicColumn(String tplCode,
			Map printParameter) {
		Map parameters = (Map) printParameter
				.get(PrintConstants.PRINT_PARAMETER_OUT_PARAMETERS);
		JRDataSource ds = (JRDataSource) printParameter
				.get(PrintConstants.PRINT_PARAMETER_OUT_DATASOURCE);
		return generateTemplateDynamicColumn(tplCode, printParameter,
				parameters, ds);
	}

	/**
	 * 
	 * @param tplCode
	 *            String
	 * @param parameters
	 *            Map
	 * @param jrDS
	 *            JRDataSource
	 * @param printParameter
	 *            HashMap
	 * @return List
	 */
	public List generateTemplateDynamicColumn(String tplCode,
			Map printParameter, Map parameters, JRDataSource jrDS) {
		List template = new ArrayList();
		try {
			ChangeJspReport changeJspReport = new ChangeJspReport();
			JasperDesign jasperDesign = changeJspReport.DoJasperReport(tplCode,
					parameters, jrDS, printParameter);
			if (isNeedSplit(jasperDesign)) {
				List jasperDesignList = splitTemplate(jasperDesign,
						printParameter);
				JasperDesign design = null;
				// String hasRegister = (String) printParameter
				// .get("HAS_REGISTER");
				HttpServletRequest request = (HttpServletRequest) printParameter
						.get(PrintConstants.PRINT_PARAMETER_REQUEST);
				if (jasperDesignList != null) {
					for (int i = 0; i < jasperDesignList.size(); i++) {
						design = (JasperDesign) jasperDesignList.get(i);
						TemplateGenerator tg = new TemplateGenerator();
						design = tg.addNotRegisterFlag(design, request);
						JasperReport jasperReport = PrintCompiler
								.compileDesign(design);
						template.add(jasperReport);
					}
				}
			} else {
				template.add(addNotRegisterFlag(tplCode, printParameter));
			}
		} catch (Exception e) {
			log.error(e);
			throw new RuntimeException(
					"Class TemplateGenerator, Method generateTemplateDynamicColumn(String tplCode, HashMap printParameter, Map parameters, JRDataSource jrDS) Error :"
							+ e.getMessage() + "\n");
		}
		return template;
	}

	/**
	 * 
	 * @param tplCode
	 *            String
	 * @param printParameter
	 *            HashMap
	 * @return List
	 */
	public List splitTemplate(String tplCode, Map printParameter) {
		try {
			PrintTPLSplit printTPLSplit = new PrintTPLSplit();
			return printTPLSplit.getSplitJasperReport(tplCode, printParameter);
		} catch (PrintingException e) {
			log.error(e);
			throw new RuntimeException(
					"Class TemplateGenerator, Method splitTemplate(String tplCode, HashMap printParameter) Error :"
							+ e.getMessage() + "\n");
		}
	}

	/**
	 * 
	 * @param jasperDesign
	 *            JasperDesign
	 * @param printParameter
	 *            HashMap
	 * @return List
	 */
	public List splitTemplate(JasperDesign jasperDesign, Map printParameter) {
		try {
			PrintTPLSplit printTPLSplit = new PrintTPLSplit();
			return printTPLSplit.getSplitJasperReport(jasperDesign,
					printParameter);
		} catch (Exception e) {
			log.error(e);
			throw new RuntimeException(
					"Class TemplateGenerator, Method splitTemplate(JasperDesign jasperDesign, HashMap printParameter) Error :"
							+ e.getMessage() + "\n");
		}
	}

	/**
	 * 
	 * @param jasperDesign
	 *            JasperDesign
	 * @return boolean
	 */
	public boolean isNeedSplit(JasperDesign jasperDesign) {
		return PrintTPLSplit.isOverPageWidth(jasperDesign);
	}

	/**
	 * 
	 * @param jasperReport
	 *            JasperReport
	 * @return boolean
	 */
	public boolean isNeedSplit(JasperReport jasperReport) {
		return PrintTPLSplit.isOverPageWidth(jasperReport);
	}

	/**
	 * 
	 * @param tplCode
	 * @param printParameter
	 * @return
	 */
	public JasperReport addNotRegisterFlag(String tplCode,
			Map printParameter) {
		JasperReport jasperReport = null;
		HttpServletRequest request = (HttpServletRequest) printParameter
				.get(PrintConstants.PRINT_PARAMETER_REQUEST);
		try {
			JasperDesign jasperDesign = PrintTPLLoader.loadDesign(tplCode,
					printParameter);
			jasperDesign = addNotRegisterFlag(jasperDesign, request);
			jasperReport = PrintCompiler.compileDesign(jasperDesign);
		} catch (PrintingException e) {
			log.error(e);
		}
		return jasperReport;
	}

	/**
	 * 
	 * @param jasperDesign
	 */
	public JasperDesign addNotRegisterFlag(JasperDesign jasperDesign,
			HttpServletRequest request) {

		this.getJRDesignStaticText(jasperDesign);

		JRDesignBand designBand = (JRDesignBand) jasperDesign.getColumnHeader();
		if (designBand != null && designBand.getHeight() > 0) {
			this.getJRDesignImage(jasperDesign, designBand, request);
		}

		designBand = (JRDesignBand) jasperDesign.getDetail();
		if (designBand != null && designBand.getHeight() > 0) {
			this.getJRDesignImage(jasperDesign, designBand, request);
		}

		JRGroup[] group = jasperDesign.getGroups();
		if (group != null && group.length > 0) {
			for (int i = 0; i < group.length; i++) {
				JRGroup groupBand = group[i];
				designBand = (JRDesignBand) groupBand.getGroupHeader();
				if (designBand != null && designBand.getHeight() > 0) {
					this.getJRDesignImage(jasperDesign, designBand, request);
				}
			}
		}
		return jasperDesign;
	}

	/**
	 * 
	 * @param jasperDesign
	 */
	public void getJRDesignStaticText(JasperDesign jasperDesign) {
	  LicenseManager licenseManager = (LicenseManager) ApplusContext.getBean("licenseManager");
	  if(licenseManager.hasRegistered()){
	    return;
	  }
		JRDesignBand pageHead = (JRDesignBand) jasperDesign.getPageHeader();
		int width = jasperDesign.getPageWidth();
		int height = pageHead.getHeight();
		if (height < 20) {
			height = 20;
			pageHead.setHeight(height);
		}
		JRDesignStaticText staticText = new JRDesignStaticText();
		staticText.setX(0);
		staticText.setY(0);
		staticText.setWidth(width);
		staticText.setHeight(height);
		staticText.setForecolor(Color.red);
		staticText.setBackcolor(Color.WHITE);
		staticText.setFontSize(15);
		staticText.setFontName("ËÎÌו");
		staticText.setPdfFontName("STSong-Light");
		staticText.setPdfEncoding("UniGB-UCS2-H");
		staticText.setBold(Boolean.TRUE);
		staticText.setItalic(Boolean.TRUE);
		staticText.setUnderline(Boolean.TRUE);
		
		String message = licenseManager.getWaterMarkText();
		if (message.length() > 0) {
			staticText.setText("\"" + message + "\"");
		}
		staticText
				.setStretchType(JRElement.STRETCH_TYPE_RELATIVE_TO_BAND_HEIGHT);
		staticText.setHorizontalAlignment(JRTextElement.HORIZONTAL_ALIGN_LEFT);
		pageHead.addElement(staticText);
	}

	/**
	 * 
	 * @param jasperDesign
	 */
	public void getJRDesignImage(JasperDesign jasperDesign, JRDesignBand detail,
			HttpServletRequest request) {
	  LicenseManager licenseManager = (LicenseManager) ApplusContext.getBean("licenseManager");
	  if(licenseManager.hasRegistered()){
      return;
    }
		//JRDesignBand detail = (JRDesignBand) jasperDesign.getDetail();
		JRElement[] elementGroup = detail.getElements();
		for (int i = 0; i < elementGroup.length; i++) {
			detail.removeElement((JRDesignElement) elementGroup[i]);
		}
		int height = detail.getHeight();
		if (height < 20 && height > 0) {
			height = 20;
			detail.setHeight(height);
		}
		
		String rootPath = request.getScheme() + "://" + request.getServerName()
				+ ":" + request.getServerPort();
		String imageName = rootPath + "/style"
				+ licenseManager.getWaterMarkImage();
		ServletContext servletContext = request.getSession()
				.getServletContext().getContext("/style");

		JRDesignImage imageField = new JRDesignImage(jasperDesign);
		imageField.setX(0);
		imageField.setY(0);
		imageField.setWidth(400);
		imageField.setHeight(height);
		imageField.setScaleImage(JRDesignImage.SCALE_IMAGE_FILL_FRAME);
		JRDesignExpression expression = new JRDesignExpression();
		expression.setValueClass(java.lang.String.class);
		try {
      String waterMarkImg = licenseManager.getWaterMarkImage();
			if (waterMarkImg != null && waterMarkImg.length() > 0){
				if(servletContext.getResourceAsStream(waterMarkImg) != null) {
				  expression.setText("\"" + imageName + "\"");
        }
			}
		} catch (Exception e) {
			//log.error(e);
		}

		imageField.setExpression(expression);
		imageField.setMode(JRDesignImage.MODE_TRANSPARENT);
		imageField
				.setStretchType(JRDesignImage.STRETCH_TYPE_RELATIVE_TO_BAND_HEIGHT);
		imageField.setHorizontalAlignment(JRDesignImage.HORIZONTAL_ALIGN_LEFT);
		detail.addElement(imageField);
		for (int i = 0; i < elementGroup.length; i++) {
			JRDesignElement element = (JRDesignElement) elementGroup[i];
			element.setMode(JRDesignElement.MODE_TRANSPARENT);
			detail.addElement(element);
		}
	}
}
