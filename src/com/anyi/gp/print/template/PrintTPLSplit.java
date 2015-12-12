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
 * ���ֶ�ģ����
 * <p>
 * Title: PrintTPLSplit
 * </p>
 * <p>
 * Description: ��ģ����Ԫ�س���ģ����ʱ������ģ��ת���ɶ����ģ��
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
	 * ���캯��������ģ���JasperDesign����ʼ������
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
	 * ���캯��������ģ���ļ�����ȡģ���.xml�ļ�����ʼ������
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
				throw new PrintingException("PrintTPLSplit�����.xmlģ���ļ�������");
			}
		} catch (PrintingException ex) {
			throw new RuntimeException(".xmlģ���ļ�������" + ex.getMessage());
		}
	}

	/**
	 * ��ʼ������
	 */
	public void init() {
		try {
			pageWidth = design.getPageWidth();
			pageLeftMargin = design.getLeftMargin();
			pageLeftOffset = getPageLeftOffSet(design);
			border = getElementBorder(design);
		} catch (Exception ex) {
			throw new RuntimeException("PrintTPLSplit���init��������"
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
				throw new PrintingException("PrintTPLSplit�����.xmlģ���ļ�������");
			}
		} catch (PrintingException e) {
			throw new RuntimeException("PrintTPLSplit�����PrintingException"
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
	 * ����һ����ģ��Ϊ�����ģ��JasperDesign
	 * 
	 * @return List �����ģ��JasperDesign
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
			throw new RuntimeException("PrintTPLSplit���getSplitTpls��������"
					+ ex.getMessage());
		}
		return jasperDesignList;
	}

	/**
	 * ����ģ��ת��Ϊ��ģ��
	 * 
	 * @param design
	 *            JasperDesign ��ģ���Design����
	 * @param pageLeft
	 *            int ������ģ�����߽�
	 * @param pageRight
	 *            int ������ģ����ұ߽�
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
				changSpecialLine(design, groupHeader, pageLeft, pageRight); // ���������ڵ�����ֱ�ߣ�����ֱ���Ǹ���ʵ�ֱ����Χ�߿�
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
	 * �ƶ���ģ��ÿ������band�ڵ�Ԫ�أ��γ��µ���ģ��
	 * 
	 * @param band
	 *            JRDesignBand ��ģ�������band����
	 * @param pageLeft
	 *            int ������ģ�����߽�
	 * @param pageRight
	 *            int ������ģ����ұ߽�
	 * @param pageLeftOffset
	 *            int ģ��������ƫ����
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
			changeElementBorder(band, pageLeft, pageRight, pageLeftOffset); // ��������Χ�߿���
		}
	}

	/**
	 * �ƶ���ģ��GroupHeader��GroupFooter�����ڵ�Ԫ�أ��γ��µ���ģ��
	 * 
	 * @param band
	 *            JRDesignBand ��ģ�������band����
	 * @param pageLeft
	 *            int ������ģ�����߽�
	 * @param pageRight
	 *            int ������ģ����ұ߽�
	 * @param pageLeftOffset
	 *            int ģ��������ƫ����
	 */
	protected void moveTemplateGroupEles(JRDesignBand band, int pageLeft,
			int pageRight, int pageLeftOffset) {
		JRElement[] eles = band.getElements();
		if (eles != null && eles.length > 0) {
			if (pageLeft == pageRight) { // ��һ��ģ��
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
			changeElementBorder(band, pageLeft, pageRight, pageLeftOffset); // ��������Χ�߿���
		}
	}

	/**
	 * ȡ�ñ����������ƫ��������û�б�񣬷���0��
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
	 * ����ģ��������ΪԪ���ṩ�˱߿����ԣ��������Ϳ���ȥ��������ֶ�Ԫ����Χ�ľ��ο򣬶���
	 * �߿����Դ��档��֮��������������ģ��ָ�ʱ��Ҫ�������ñ����Χ�߿�Ĵ�ϸ��
	 * �˺������ڻ�ñ����Χ�ı߿��ϸ������������Ԫ�ر߿�ʱʹ�����ֵ�����ֵȡ���Ǳ���� ��Ԫ�ص���߿��ֵ����û�б�񣬷��ء�1Point����
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
	 * ʵ������������Ԫ�صı߿�����
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
						if (pageRight == pageLeft) { // ��һ��design
							if (x + width == pageRight) { // ���һ��Ԫ��
								if (jrBox != null) {
									jrBox.setRightBorder(border);
								}
							}
						} else {
							if (x + width == (pageRight - pageLeft + pageLeftOffset)) { // ���һ��Ԫ��
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
	 * ���������ں��е�����ֱ�ߣ�����ֱ������Ϊ�����Χ�ӱ߿�
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
	 * ȡ��band������Ԫ�ص�x�� widthֵ
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
	 * ȡ�û��ֶ����ģ���Xֵ����
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
	 * ȡ�û���ÿ����ģ���Xֵ
	 * 
	 * @param pageWidth
	 *            int ������ģ��ı�׼X
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
	 * �ж�ģ����Ԫ���Ƿ񳬹�ģ���ȣ�����˳����detail��groupHeader��groupFooter��columnHeader��
	 * 
	 * @param report
	 *            JasperReport ģ���JasperReport
	 * @return boolean ����Ԫ�س���ģ����Ϊtrue������false��
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
	 * �ж�ģ����Ԫ���Ƿ񳬹�ģ���ȣ�����˳����detail��groupHeader��groupFooter��columnHeader��
	 * 
	 * @param design
	 *            JasperDesign ģ���JasperDesign
	 * @return boolean ����Ԫ�س���ģ����Ϊtrue������false��
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
	 * ��ȡָ�����зָ�ģ��Ҫ�ظ���ӡ��Ԫ��
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
	 * ��ȡָ�����зָ�ģ��Ҫ�ظ���ӡ��Ԫ�ص��ܿ��
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
