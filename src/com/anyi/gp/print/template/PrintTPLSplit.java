package com.anyi.gp.print.template;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRBand;
import net.sf.jasperreports.engine.JRBox;
import net.sf.jasperreports.engine.JRElement;
import net.sf.jasperreports.engine.JRGraphicElement;
import net.sf.jasperreports.engine.JRGroup;
import net.sf.jasperreports.engine.JRTextElement;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignElement;
import net.sf.jasperreports.engine.design.JRDesignLine;
import net.sf.jasperreports.engine.design.JRDesignStaticText;
import net.sf.jasperreports.engine.design.JRDesignTextField;
import net.sf.jasperreports.engine.design.JasperDesign;

import com.anyi.gp.print.exception.PrintingException;
import com.anyi.gp.print.util.ObjectClone;
import com.anyi.gp.print.util.PrintTPLLoader;

/**
 * 划分多模板类
 * <p>
 * Title: PrintTPLSplit
 * </p>
 * <p>
 * Description: 父模板内元素超过模板宽度时，将父模板转换成多个子模板
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company: UFGOV
 * </p>
 * 
 * @author zhangyw
 * @version 1.0
 */

public class PrintTPLSplit {
	private int pageWidth;

	private int pageLeftMargin;

	private int pageLeftOffset;

	private byte border;

	private JasperDesign design;

	public PrintTPLSplit() {
	}

	/**
	 * 构造函数，传递模板的JasperDesign，初始化参数
	 * 
	 * @param design
	 *            JasperDesign
	 */
	public PrintTPLSplit(JasperDesign design) {
		this.design = new JasperDesign();
		this.design = design;
		init();
	}

	/**
	 * 构造函数，根据模板文件名读取模板的.xml文件，初始化参数
	 * 
	 * @param tplName
	 *            String
	 */
	public PrintTPLSplit(String tplName) {
		try {
			design = new JasperDesign();
			design = PrintTPLLoader.loadDesign(tplName);
			if (design != null) {
				init();
			} else {
				throw new PrintingException("PrintTPLSplit类出错，.xml模板文件不存在");
			}
		} catch (PrintingException ex) {
			throw new RuntimeException(".xml模板文件不存在" + ex.getMessage());
		}
	}

	/**
	 * 初始化参数
	 */
	public void init() {
		try {
			pageWidth = design.getPageWidth();
			pageLeftMargin = design.getLeftMargin();
			pageLeftOffset = getPageLeftOffSet(design);
			border = getElementBorder(design);
		} catch (Exception ex) {
			throw new RuntimeException("PrintTPLSplit类的init方法出错"
					+ ex.getMessage());
		}
	}

	/**
	 * 
	 * @param tplName
	 *            String
	 * @param printParameter
	 *            HashMap
	 * @return List
	 * @throws PrintingException
	 */
	public List getSplitJasperReport(String tplName, Map printParameter)
			throws PrintingException {
		List jasperDesignList = new ArrayList();
		try {
			JasperDesign design = PrintTPLLoader.loadDesign(tplName,
					printParameter);
			if (design != null) {
				jasperDesignList = this.getSplitJasperReport(design,
						printParameter);
			} else {
				throw new PrintingException("PrintTPLSplit类出错，.xml模板文件不存在");
			}
		} catch (PrintingException e) {
			throw new RuntimeException("PrintTPLSplit类出错PrintingException"
					+ e.getMessage() + "\n");
		}
		return jasperDesignList;
	}

	public List getSplitJasperReport(JasperDesign design, Map printParameter){
		this.design = design;
		init();
		List jasperDesignList = new ArrayList();
		jasperDesignList = this.getSplitTpls();
		return jasperDesignList;
	}

	/**
	 * 划分一个父模板为多个子模板JasperDesign
	 * 
	 * @return List 多个子模板JasperDesign
	 */
	public List getSplitTpls() {
		List jasperDesignList = new ArrayList();
		try {
			int[] splitx = getSplitX();
			for (int i = 0, j = splitx.length; i < j; i++) {
				if (splitx[i] == 0)
					continue;
				JasperDesign designc = null;
				ObjectClone oc = new ObjectClone();
				designc = (JasperDesign) oc.clone(design);
				if (i == 0) {
					splitTemplate(designc, splitx[i], splitx[i]);
				} else {
					designc.setName(designc.getName() + "_continue");
					splitTemplate(designc, splitx[i - 1], splitx[i]);
				}
				jasperDesignList.add(designc);
			}

		} catch (Exception ex) {
			throw new RuntimeException("PrintTPLSplit类的getSplitTpls方法出错"
					+ ex.getMessage());
		}
		return jasperDesignList;
	}

	/**
	 * 将父模板转换为子模板
	 * 
	 * @param design
	 *            JasperDesign 父模板的Design对象
	 * @param pageLeft
	 *            int 划分子模板的左边界
	 * @param pageRight
	 *            int 划分子模板的右边界
	 */
	protected void splitTemplate(JasperDesign design, int pageLeft,
			int pageRight) {
		JRDesignBand detail = (JRDesignBand) design.getDetail();
		moveTemplateEles(detail, pageLeft, pageRight, pageLeftOffset);
		JRGroup[] groups = design.getGroups();
		if (groups != null) {
			for (int i = 0; i < groups.length; i++) {
				JRDesignBand groupHeader = (JRDesignBand) groups[i]
						.getGroupHeader();
				moveTemplateGroupEles(groupHeader, pageLeft, pageRight,
						pageLeftOffset);
				changSpecialLine(design, groupHeader, pageLeft, pageRight); // 处理区域内的特殊直线，这条直线是辅助实现表格外围边框。
				JRDesignBand groupFooter = (JRDesignBand) groups[i]
						.getGroupFooter();
				moveTemplateGroupEles(groupFooter, pageLeft, pageRight,
						pageLeftOffset);
				changSpecialLine(design, groupFooter, pageLeft, pageRight);
			}
		}
		JRDesignBand pageHeader = (JRDesignBand) design.getPageHeader();
		moveTemplateEles(pageHeader, pageLeft, pageRight, pageLeftOffset);
		JRDesignBand columnHeader = (JRDesignBand) design.getColumnHeader();
		moveTemplateEles(columnHeader, pageLeft, pageRight, pageLeftOffset);
		JRDesignBand pageFooter = (JRDesignBand) design.getPageFooter();
		moveTemplateEles(pageFooter, pageLeft, pageRight, pageLeftOffset);
		JRDesignBand columnFooter = (JRDesignBand) design.getColumnFooter();
		moveTemplateEles(columnFooter, pageLeft, pageRight, pageLeftOffset);
	}

	/**
	 * 移动父模板每个区域band内的元素，形成新的子模板
	 * 
	 * @param band
	 *            JRDesignBand 父模板的区域band对象
	 * @param pageLeft
	 *            int 划分子模板的左边界
	 * @param pageRight
	 *            int 划分子模板的右边界
	 * @param pageLeftOffset
	 *            int 模板区域左偏移量
	 */
	protected void moveTemplateEles(JRDesignBand band, int pageLeft,
			int pageRight, int pageLeftOffset) {
		JRElement[] eles = band.getElements();
		if (eles != null && eles.length > 0) {
			if (pageLeft == pageRight) {
				for (int i = 0; i < eles.length; i++) {
					if (eles[i] != null) {
						int x = eles[i].getX();
						if (x >= pageRight) {
							String key = eles[i].getKey();
							JRDesignElement jrElement = (JRDesignElement) band
									.getElementByKey(key);
							if (jrElement instanceof JRTextElement) {
								band.removeElement(jrElement);
							}
						}
					}
				}
			} else {
				for (int i = 0; i < eles.length; i++) {
					if (eles[i] != null) {
						int x = eles[i].getX();
						String key = eles[i].getKey();
						JRDesignElement jrElement = (JRDesignElement) band
								.getElementByKey(key);
						if (x < pageLeft || x >= pageRight) {
							if (jrElement instanceof JRTextElement) {
								if (!jrElement.isPrintInSplitedTemplate()) {
									band.removeElement(jrElement);
								}
							}
						} else {
							jrElement
									.setX(x
											- pageLeft
											+ pageLeftOffset
											+ this
													.getPrintedEleWidthInSplitedTplInBand(design
															.getDetail()));
						}
					}
				}
			}
			changeElementBorder(band, pageLeft, pageRight, pageLeftOffset); // 处理表格外围边框宽度
		}
	}

	/**
	 * 移动父模板GroupHeader和GroupFooter区域内的元素，形成新的子模板
	 * 
	 * @param band
	 *            JRDesignBand 父模板的区域band对象
	 * @param pageLeft
	 *            int 划分子模板的左边界
	 * @param pageRight
	 *            int 划分子模板的右边界
	 * @param pageLeftOffset
	 *            int 模板区域左偏移量
	 */
	protected void moveTemplateGroupEles(JRDesignBand band, int pageLeft,
			int pageRight, int pageLeftOffset) {
		JRElement[] eles = band.getElements();
		if (eles != null && eles.length > 0) {
			if (pageLeft == pageRight) { // 第一个模板
				for (int i = 0; i < eles.length; i++) {
					if (eles[i] != null) {
						int x = eles[i].getX();
						String key = eles[i].getKey();
						JRDesignElement jrElement = (JRDesignElement) band
								.getElementByKey(key);
						if (x >= pageRight) {
							if (jrElement instanceof JRTextElement) {
								band.removeElement(jrElement);
							}
						} else {
							int width = eles[i].getWidth();
							if (x + width > pageRight) {
								if (jrElement instanceof JRTextElement) {
									jrElement.setWidth(pageRight - x);
								}
							}
						}
					}
				}
			} else {
				int repeatedColWidth = this
						.getPrintedEleWidthInSplitedTplInBand(design
								.getDetail());
				for (int i = 0; i < eles.length; i++) {
					if (eles[i] != null) {
						int x = eles[i].getX();
						String key = eles[i].getKey();
						JRDesignElement jrElement = (JRDesignElement) band
								.getElementByKey(key);
						if (x >= pageRight) {
							if (jrElement instanceof JRTextElement) {
								if (!jrElement.isPrintInSplitedTemplate()) {
									band.removeElement(jrElement);
								}
							}
						} else if (x < pageLeft) {
							int width = eles[i].getWidth();
							if (x + width > pageLeft) {
								if (jrElement instanceof JRTextElement) {
									if (x + width > pageRight) {
										jrElement
												.setWidth(pageRight - pageLeft);
									} else {
										jrElement
												.setWidth(x + width - pageLeft);
									}
									jrElement.setX(pageLeftOffset
											+ repeatedColWidth);
								}
							} else {
								if (jrElement instanceof JRTextElement) {
									if (!jrElement.isPrintInSplitedTemplate()) {
										band.removeElement(jrElement);
									}
								}
							}
						} else {
							int width = eles[i].getWidth();
							if (x + width > pageRight) {
								if (jrElement instanceof JRTextElement) {
									jrElement.setWidth(pageRight - x);
								}
							}
							jrElement.setX(x - pageLeft + pageLeftOffset
									+ repeatedColWidth);
						}
					}
				}
			}
			changeElementBorder(band, pageLeft, pageRight, pageLeftOffset); // 处理表格外围边框宽度
		}
	}

	/**
	 * 取得表格在区域内偏移量，若没有表格，返回0。
	 * 
	 * @param design
	 *            JasperDesign
	 * @return int
	 */
	protected int getPageLeftOffSet(JasperDesign design) {
		int pageLeftOffSet = 0;
		JRDesignBand band = (JRDesignBand) design.getDetail();
		JRElement[] eles = band.getElements();
		if (eles == null || eles.length == 0) {
			JRGroup[] groups = design.getGroups();
			if (groups != null) {
				for (int i = 0; i < groups.length; i++) {
					JRGroup group = groups[i];
					eles = group.getGroupHeader().getElements();
					if (eles == null || eles.length == 0) {
						eles = group.getGroupFooter().getElements();
					}
				}
			}
		}
		if (eles != null && eles.length > 0) {
			pageLeftOffSet = eles[0].getX();
		}
		return pageLeftOffSet;
	}

	/**
	 * 对于模板升级，为元素提供了边框属性，这样，就可以去掉表格中字段元素外围的矩形框，而用
	 * 边框属性代替。随之而来的问题是在模板分隔时需要重新设置表格外围边框的粗细。
	 * 此函数用于获得表格外围的边框粗细，在设置其他元素边框时使用这个值。这个值取的是表格最 左元素的左边框的值，若没有表格，返回“1Point”。
	 * 
	 * @param design
	 *            JasperDesign
	 * @return byte
	 */
	protected byte getElementBorder(JasperDesign design) {
		byte leftBorder = JRGraphicElement.PEN_1_POINT;
		JRDesignBand band = (JRDesignBand) design.getDetail();
		JRElement[] eles = band.getElements();
		if (eles == null || eles.length == 0) {
			JRGroup[] groups = design.getGroups();
			if (groups != null) {
				for (int i = 0; i < groups.length; i++) {
					JRGroup group = groups[i];
					eles = group.getGroupHeader().getElements();
					if (eles == null || eles.length == 0) {
						eles = group.getGroupFooter().getElements();
					}
				}
			}
		}
		if (eles != null && eles.length > 0) {
			String key = eles[0].getKey();
			JRDesignElement jrElement = (JRDesignElement) band
					.getElementByKey(key);
			JRTextElement jrTextElement;
			if (jrElement instanceof JRTextElement) {
				jrTextElement = (JRTextElement) jrElement;
				JRBox jrBox = jrTextElement.getBox();
				if (jrBox != null) {
					leftBorder = jrBox.getLeftBorder();
				}
			}
		}
		return leftBorder;
	}

	/**
	 * 实现区域内两端元素的边框设置
	 * 
	 * @param band
	 *            JRDesignBand
	 * @param pageLeft
	 *            int
	 * @param pageRight
	 *            int
	 * @param pageLeftOffset
	 *            int
	 */
	protected void changeElementBorder(JRDesignBand band, int pageLeft,
			int pageRight, int pageLeftOffset) {
		JRElement[] eles = band.getElements();
		if (eles != null && eles.length > 0) {
			for (int i = 0; i < eles.length; i++) {
				if (eles[i] != null) {
					String key = eles[i].getKey();
					JRDesignElement jrElement = (JRDesignElement) band
							.getElementByKey(key);
					JRTextElement jrTextElement;
					if (jrElement instanceof JRTextElement) {
						jrTextElement = (JRTextElement) jrElement;
						JRBox jrBox = jrTextElement.getBox();
						int x = jrTextElement.getX();
						int width = jrTextElement.getWidth();
						if (x == pageLeftOffset) { // the first element of
							// band,set
							// leftBorder border
							if (jrBox != null) {
								jrBox.setLeftBorder(border);
							}
						}
						if (pageRight == pageLeft) { // 第一个design
							if (x + width == pageRight) { // 最后一个元素
								if (jrBox != null) {
									jrBox.setRightBorder(border);
								}
							}
						} else {
							if (x + width == (pageRight - pageLeft + pageLeftOffset)) { // 最后一个元素
								if (jrBox != null) {
									jrBox.setRightBorder(border);
								}
							}
						}
						if (jrElement instanceof JRDesignStaticText) {
							JRDesignStaticText jrStaticText = (JRDesignStaticText) jrTextElement;
							if (jrBox != null) {
								jrStaticText.setBox(jrBox);
							}
						}
						if (jrElement instanceof JRDesignTextField) {
							JRDesignTextField jrTextField = (JRDesignTextField) jrTextElement;
							if (jrBox != null) {
								jrTextField.setBox(jrBox);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 处理区域内含有的特殊直线，这条直线用来为表格外围加边框
	 * 
	 * @param band
	 *            JRDesignBand
	 * @param pageLeft
	 *            int
	 * @param pageRight
	 *            int
	 */
	protected void changSpecialLine(JasperDesign design, JRDesignBand band,
			int pageLeft, int pageRight) {
		JRDesignLine specialLine = new JRDesignLine();
		specialLine = (JRDesignLine) band.getElementByKey("keySpecialLine");
		if (specialLine != null) {
			if (pageLeft == pageRight) {
				specialLine.setWidth(pageRight - pageLeftOffset);
			} else {
				int maxPos = getBandMaxElePos(design);
				if (maxPos == (pageRight - pageLeft + pageLeftOffset)) {
					specialLine.setWidth(pageRight - pageLeft);
				} else {
					specialLine.setWidth(maxPos - pageLeftOffset);
				}
			}
		}
	}

	/**
	 * 取得band内最右元素的x＋ width值
	 * 
	 * @param band
	 *            JRDesignBand
	 * @return int
	 */
	protected int getBandMaxElePos(JasperDesign design) {
		int maxPos = 0;
		JRDesignBand band = (JRDesignBand) design.getDetail();
		JRElement[] eles = band.getElements();
		if (eles == null || eles.length == 0) {
			JRGroup[] groups = design.getGroups();
			if (groups != null) {
				for (int i = 0; i < groups.length; i++) {
					JRGroup group = groups[i];
					eles = group.getGroupHeader().getElements();
					if (eles == null || eles.length == 0) {
						eles = group.getGroupFooter().getElements();
					}
				}
			}
		}
		if (eles != null && eles.length > 0) {
			for (int i = 0; i < eles.length; i++) {
				if (eles[i] != null) {
					String key = eles[i].getKey();
					JRDesignElement jrElement = (JRDesignElement) band
							.getElementByKey(key);
					if (jrElement instanceof JRTextElement) {
						int x = eles[i].getX();
						int width = eles[i].getWidth();
						if (x + width > maxPos) {
							maxPos = x + width;
						}
					}
				}
			}
		}
		return maxPos;
	}

	/**
	 * 取得划分多个子模板的X值数组
	 * 
	 * @return int[]
	 */
	protected int[] getSplitX() {
		JRDesignBand band = (JRDesignBand) design.getDetail();
		JRElement[] eles = band.getElements();
		if (eles == null || eles.length == 0) {
			JRGroup[] groups = design.getGroups();
			if (groups != null) {
				for (int i = 0, j = groups.length; i < j; i++) {
					JRGroup group = groups[i];
					eles = group.getGroupHeader().getElements();
					if (eles == null || eles.length == 0) {
						eles = group.getGroupFooter().getElements();
					}
				}
			}
			if (eles == null || eles.length == 0) {
				eles = design.getColumnHeader().getElements();
			}
		}
		int dataWidth = pageWidth - pageLeftMargin - pageLeftOffset;
		pageWidth -= pageLeftMargin;
		int[] splitx = new int[50];
		int size = 0;
		int minX = 1;
		boolean isFirstCol = true;
		while (minX != 0) {
			minX = getMinX(pageWidth, isFirstCol, eles);
			isFirstCol = false;
			if (minX != 0) {
				splitx[size++] = minX;
			} else {
				splitx[size++] = pageWidth;
			}
			pageWidth = minX + dataWidth;
			if (size == 50)
				break;
		}
		return splitx;
	}

	/**
	 * 取得划分每个子模板的X值
	 * 
	 * @param pageWidth
	 *            int 划分子模板的标准X
	 * @return int
	 */
	protected int getMinX(int pageWidth, boolean isFirstCol, JRElement[] eles) {
		if (eles == null) {
			return 0;
		}
		int minX = 0;
		for (int i = 0, j = eles.length; i < j; i++) {
			if (eles[i] != null) {
				int x = eles[i].getX();
				int width = eles[i].getWidth();
				int pos = x + width;
				if (!isFirstCol) {
					pos += this.getPrintedEleWidthInSplitedTplInBand(design
							.getDetail());
				}
				if (pos > pageWidth) {
					minX = x;
					break;
				}
			}
		}
		return minX;
	}

	/**
	 * 判断模板内元素是否超过模板宽度，检验顺序是detail、groupHeader、groupFooter、columnHeader区
	 * 
	 * @param report
	 *            JasperReport 模板的JasperReport
	 * @return boolean 存在元素超过模板宽度为true；否则false。
	 */
	public static boolean isOverPageWidth(JasperReport report) {
		int pageWidth = report.getPageWidth();
		JRElement[] eles = report.getDetail().getElements();
		if (eles == null || eles.length == 0) {
			JRGroup[] groups = report.getGroups();
			if (groups != null) {
				for (int i = 0; i < groups.length; i++) {
					JRGroup group = groups[i];
					eles = group.getGroupHeader().getElements();
					if (eles == null || eles.length == 0) {
						eles = group.getGroupFooter().getElements();
					}
				}
			}
			if (eles == null || eles.length == 0) {
				eles = report.getColumnHeader().getElements();
			}
		}
		if (eles != null && eles.length > 0) {
			int i;
			for (i = 0; i < eles.length; i++) {
				int x = eles[i].getX();
				int width = eles[i].getWidth();
				int pos = x + width;
				if (pos > pageWidth) {
					break;
				}
			}
			if (i < eles.length) {
				return true;
			}
			return false;
		}
		return false;
	}

	/**
	 * 判断模板内元素是否超过模板宽度，检验顺序是detail、groupHeader、groupFooter、columnHeader区
	 * 
	 * @param design
	 *            JasperDesign 模板的JasperDesign
	 * @return boolean 存在元素超过模板宽度为true；否则false。
	 */
	public static boolean isOverPageWidth(JasperDesign design) {
		int pageWidth = design.getPageWidth();
		JRElement[] eles = design.getDetail().getElements();
		if (eles == null || eles.length == 0) {
			JRGroup[] groups = design.getGroups();
			if (groups != null) {
				for (int i = 0; i < groups.length; i++) {
					JRGroup group = groups[i];
					eles = group.getGroupHeader().getElements();
					if (eles == null || eles.length == 0) {
						eles = group.getGroupFooter().getElements();
					}
				}
			}
			if (eles == null || eles.length == 0) {
				eles = design.getColumnHeader().getElements();
			}
		}
		if (eles != null && eles.length > 0) {
			int i;
			for (i = 0; i < eles.length; i++) {
				int x = eles[i].getX();
				int width = eles[i].getWidth();
				int pos = x + width;
				if (pos > pageWidth) {
					break;
				}
			}
			if (i < eles.length) {
				return true;
			}
			return false;
		}
		return false;
	}

	/**
	 * 获取指定区中分割模板要重复打印的元素
	 * 
	 * @return
	 */
	private JRElement[] getPrintedEleInSplitedTplInBand(JRBand band) {
		JRElement[] result = null;
		JRElement[] allElements = band.getElements();
		JRElement element = null;
		for (int i = 0, j = allElements.length; i < j; i++) {
			element = allElements[i];
			if (element.isPrintInSplitedTemplate()) {
				if (result == null) {
					result = new JRElement[1];
					result[0] = element;
				} else {
					JRElement[] old = result;
					result = new JRElement[old.length + 1];
					System.arraycopy(old, 0, result, 0, old.length);
					result[old.length] = element;
					old = null;
				}

			}
		}
		return result;
	}

	/**
	 * 获取指定区中分割模板要重复打印的元素的总宽度
	 * 
	 * @return
	 */
	private int getPrintedEleWidthInSplitedTplInBand(JRBand band) {
		JRElement[] eles = this.getPrintedEleInSplitedTplInBand(band);
		int result = 0;
		if (eles == null) {
			return 0;
		}
		for (int i = 0, j = eles.length; i < j; i++) {
			result += eles[i].getWidth();
		}
		return result;
	}
}
