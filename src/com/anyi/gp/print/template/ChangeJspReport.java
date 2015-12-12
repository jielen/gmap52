/* $Id: ChangeJspReport.java,v 1.7 2009/07/10 08:36:56 liuxiaoyong Exp $ */

package com.anyi.gp.print.template;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import net.sf.jasperreports.engine.JRBand;
import net.sf.jasperreports.engine.JRBox;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRElement;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRGroup;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRRectangle;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JRStaticText;
import net.sf.jasperreports.engine.JRTextField;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignElement;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignGroup;
import net.sf.jasperreports.engine.design.JasperDesign;

import com.anyi.gp.print.util.PrintTPLLoader;
import com.anyi.gp.util.StringTools;

/**
 * <p>
 * Title: ������ģ��Ļ����ϸı�ģ��
 * </p>
 * <p>
 * Description: ������ģ��Ļ����ϸı�ģ�棬Ŀǰֻ�ܴ������У�
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company: UFGOV
 * </p>
 * 
 * @author zuodf
 * @version 1.0
 */

public class ChangeJspReport {
	public ChangeJspReport() {
	}

	public JasperDesign DoJasperReport(String tplCode, Map parameters, JRDataSource jrDS, Map printParameter) {
		JasperDesign jasperdesign = null;
		Collection noField = new ArrayList();
		Collection noparameter = new ArrayList();
		try {
			jasperdesign = PrintTPLLoader.loadDesign(tplCode, printParameter);
			/* ������ģ������ϵĶ�̬��ӡ */
			// ��ģ��jasperdesign�ж���Ĳ���ɾ��
			noparameter = DoParameter(parameters, jasperdesign);
			// ��ģ��jasperdesign�ж�����ֶ�ɾ���������ض�����ֶ�
			noField = DoField(jrDS, jasperdesign);
			if (noField.size() > 0) {
				// ��ģ��jasperdesign��detail��������ֶ�ɾ��������¼ɾ�����ο�λ����Ϣ��x,y,width,height��,����ɾ��Ԫ�غ��Ԫ��λ��
				DoDetailTextField(jasperdesign, noField);
				// ��̬����ȥ�����ֶΣ�����ڳ�detail����������������У�Ҳ����ɾ��
				DoOtherBandField(jasperdesign, noField);
				// ��̬����ȥ�����ֶΣ������GroupExpression���У�Ҳ����ɾ��
				DoGroupExpression(jasperdesign, noField);
			}
			if(noparameter.size() > 0){
				// ��ģ��jasperdesign��tablegroupHeader��ColumnHeader��������ֶ�ɾ�������ο�ɾ��������¼ɾ�����ο�λ����Ϣ��x,y,width,height��,����ɾ��Ԫ�غ��Ԫ��λ��
				DoColumnHeaderTextField(jasperdesign, noparameter);
				DoGroupHeaderTextField(jasperdesign, noparameter);
			}
			/* ������ģ������ϵĶ�̬��ӡ */
		} catch (JRException e) {
			throw new RuntimeException("ChangeJspReport���DoJasperReport��������"
					+ e.getMessage() + "\n");
		} catch (Exception e) {
			throw new RuntimeException("ChangeJspReport���DoJasperReport��������"
					+ e.getMessage() + "\n");
		}
		return jasperdesign;
	}

	/**
	 * ����GroupHeader��JRTextField���ֶ�Ԫ�أ�������ǵĲ�����ɾ��������Ҫɾ�����ο���뼯��
	 * 
	 * @param jasperdesign
	 *            JasperDesign ��ӡ���ģ��
	 * @param noparameter
	 *            Collection Ҫɾ���Ĳ�������
	 * @param noparameterElement
	 *            Collection ɾ����JRTextField���ֶ�Ԫ�أ��򼯺�
	 * @return noparameterElement Collection ����Ҫɾ����JRTextField���ֶ�Ԫ�أ��򼯺�
	 */
	private void DoGroupHeaderTextField(JasperDesign jasperdesign,
			Collection noparameter) {
		JRGroup[] jrgroups = jasperdesign.getGroups();
		if (jrgroups != null && jrgroups.length > 0) {
			for (int j = 0, n = jrgroups.length; j < n; j++) {
				JRDesignGroup jrgroup = (JRDesignGroup) jrgroups[j];
				JRDesignBand band = (JRDesignBand) jrgroup.getGroupHeader();
				JRElement[] elements = band.getElements();
				if (elements != null && elements.length > 0) {
					for (int i = 0, m = elements.length; i < m; i++) {
						JRDesignElement jrdelement = (JRDesignElement) elements[i];
						if (jrdelement == null) {
							continue;
						}
						if (jrdelement instanceof JRRectangle) {
						} else if (jrdelement instanceof JRTextField) {
							JRTextField elementField = (JRTextField) jrdelement;
							String fieldText = elementField.getExpression().getText();
							if (fieldText != null && fieldText.indexOf("com.anyi.gp.print.util.") == 0) {
								int bindex1 = fieldText.indexOf("{");
								int bindex2 = fieldText.indexOf("}");
								if (bindex1 != -1 && bindex2 != -1) {
									fieldText = fieldText.substring(
											bindex1 - 2, bindex2 + 1);
								}
							}
							for (Iterator iterbody = noparameter.iterator(); iterbody
									.hasNext();) {
								String tempfield = (String) iterbody.next();
								if (fieldText != null
										&& fieldText.equals("$P{" + tempfield + "}")) {
									DoGroupHeaderMoveTextField(jrdelement, band);
									band.removeElement(jrdelement);

								}
							}
						} else if (jrdelement instanceof JRStaticText) {

						}
					}
				}
				jrgroup.setGroupHeader(band);
			}
		}
	}

	private void DoGroupHeaderMoveTextField(JRDesignElement jrdelement,
			JRDesignBand band) {
		if (jrdelement == null || band == null) {
			return;
		}
		int ibandHeight = band.getHeight();
		int iwidth = jrdelement.getWidth();
		int ieHeight = jrdelement.getHeight();
		int iex = jrdelement.getX();
		int iey = jrdelement.getY();
		int x;
		int y;
		int w;
		JRElement[] elements = band.getElements();
		if (elements != null && elements.length > 0) {
			if (ieHeight == ibandHeight) {
				for (int i = 0; i < elements.length; i++) {
					JRDesignElement jrmvelement = (JRDesignElement) elements[i];
					if (jrmvelement == null) {
						continue;
					}
					if (jrmvelement instanceof JRTextField
							|| jrmvelement instanceof JRStaticText) {
						x = jrmvelement.getX();
						if (x > iex) {
							jrmvelement.setX(x - iwidth);
						}
					}
				}
			} else {
				for (int i = 0; i < elements.length; i++) {
					JRDesignElement jrmvelement = (JRDesignElement) elements[i];
					if (jrmvelement == null) {
						continue;
					}
					if (jrmvelement instanceof JRTextField
							|| jrmvelement instanceof JRStaticText) {
						x = jrmvelement.getX();
						y = jrmvelement.getY();
						w = jrmvelement.getWidth();
						if (y == iey) {
							if (x > iex) {
								jrmvelement.setX(x - iwidth);
							}
						} else if (y < iey) {
							if (x <= iex && (x + w) >= (iex + iwidth)) {
								jrmvelement.setWidth(w - iwidth);
							} else if (x > iex) {
								jrmvelement.setX(x - iwidth);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * ����ColumnHeader��JRTextField���ֶ�Ԫ�أ�������ǵĲ�������Ҫɾ�������뼯��
	 * 
	 * @param jasperdesign
	 *            JasperDesign ��ӡ���ģ��
	 * @param noparameter
	 *            Collection Ҫɾ���Ĳ�������
	 * @param noparameterElement
	 *            Collection Ҫɾ����JRTextField���ֶ�Ԫ�أ�����
	 * @return noparameterElement Collection ����Ҫɾ����JRTextField���ֶ�Ԫ�أ�����
	 */
	private void DoColumnHeaderTextField(JasperDesign jasperdesign,
			Collection noparameter) {
		JRDesignBand band = (JRDesignBand) jasperdesign.getColumnHeader();
		JRElement[] elements = band.getElements();
		if (elements != null && elements.length > 0) {
			for (int i = 0, m = elements.length; i < m; i++) {
				JRDesignElement jrdelement = (JRDesignElement) elements[i];
				if (jrdelement == null) {
					continue;
				}
				if (jrdelement instanceof JRRectangle) {
				} else if (jrdelement instanceof JRTextField) {
					JRTextField elementField = (JRTextField) jrdelement;
					String fieldText = elementField.getExpression().getText();
					if (fieldText != null && fieldText.indexOf("com.anyi.gp.print.util.") == 0) {
						int bindex1 = fieldText.indexOf("{");
						int bindex2 = fieldText.indexOf("}");
						if (bindex1 != -1 && bindex2 != -1) {
							fieldText = fieldText.substring(bindex1 - 2,
									bindex2 + 1);
						}
					}
					for (Iterator iterbody = noparameter.iterator(); iterbody
							.hasNext();) {
						String tempfield = (String) iterbody.next();
						if (fieldText != null
								&& fieldText.equals("$P{" + tempfield + "}")) {
							JRBox jrbox = elementField.getBox();
							if (jrbox != null && jrbox.getLeftBorder() != 0) {
								DoMoveTextField(jrdelement, elements, 0);
							}
							band.removeElement(jrdelement);
						}
					}
				} else if (jrdelement instanceof JRStaticText) {

				}
			}
		}
		jasperdesign.setColumnHeader(band);
	}

	/**
	 * ����Detail��JRTextField���ֶ�Ԫ�أ���Ԫ�أ�����ǵ��ֶμ����뼯��
	 * 
	 * @param jasperdesign
	 *            JasperDesign ��ӡ���ģ��
	 * @param noField
	 *            Collection Ҫɾ�����ֶμ���
	 * @param DoDetailTextField
	 *            Collection ɾ����JRTextField�ֶ�Ԫ�ؿ򼯺�
	 * @return DoDetailTextField Collection ����Ҫɾ����JRTextField�ֶ�Ԫ�ؿ򼯺�
	 */
	private void DoDetailTextField(JasperDesign jasperdesign, Collection noField) {
		JRDesignBand band = (JRDesignBand) jasperdesign.getDetail();
		JRElement[] elements = band.getElements();
		if (elements != null && elements.length > 0) {
			for (int i = 0, m = elements.length; i < m; i++) {
				JRDesignElement jrdelement = (JRDesignElement) elements[i];
				if (jrdelement == null) {
					continue;
				}
				if (jrdelement instanceof JRRectangle) {

				} else if (jrdelement instanceof JRTextField) {
					JRTextField elementField = (JRTextField) jrdelement;
					String fieldText = elementField.getExpression().getText();
					if (fieldText != null && fieldText.indexOf("com.anyi.gp.print.util.") == 0) {
						int bindex1 = fieldText.indexOf("{");
						int bindex2 = fieldText.indexOf("}");
						if (bindex1 != -1 && bindex2 != -1) {
							fieldText = fieldText.substring(bindex1 - 2,
									bindex2 + 1);
						}
					}
					for (Iterator iterbody = noField.iterator(); iterbody.hasNext();) {
						String tempfield = "$F{" + (String) iterbody.next()	+ "}";
						if (fieldText != null && fieldText.equals(tempfield)) {
							DoMoveTextField(jrdelement, elements, i);
							band.removeElement(jrdelement);
							DoFooterLine(jasperdesign, jrdelement.getWidth());
						}
					}
				} else if (jrdelement instanceof JRStaticText) {

				}
			}
		}
		jasperdesign.setDetail(band);
	}

	private void DoFooterLine(JasperDesign jasperdesign, int iwith) {
		JRGroup[] jrgroups = jasperdesign.getGroups();
		if (jrgroups != null && jrgroups.length > 0) {
			for (int j = 0, n = jrgroups.length; j < n; j++) {
				JRDesignGroup jrgroup = (JRDesignGroup) jrgroups[j];
				JRDesignBand band = (JRDesignBand) jrgroup.getGroupFooter();
				JRDesignElement elements = (JRDesignElement) band
						.getElementByKey("keySpecialLine");
				if (elements != null) {
					elements.setWidth(elements.getWidth() - iwith);
				}
			}
		}
	}

	private void DoMoveTextField(JRDesignElement jrdelementRec,
			JRElement[] elements, int j) {
		int ix = jrdelementRec.getX(), iwidth = jrdelementRec.getWidth(), iy = jrdelementRec
				.getY(), ih = jrdelementRec.getHeight(), iey, ieh, iex, iew, iwx;
		for (int i = j, m = elements.length; i < m; i++) {
			JRDesignElement jrdelement = (JRDesignElement) elements[i];
			if (jrdelement == null) {
				continue;
			}

			iey = jrdelement.getY();
			ieh = jrdelement.getHeight();
			iex = jrdelement.getX();
			iew = jrdelement.getWidth();
			if (jrdelement instanceof JRRectangle) {

			} else if (jrdelement instanceof JRTextField) {
				// JRBox jrbox = ((JRTextField) jrdelement).getBox();
				// if (jrbox != null && jrbox.getLeftBorder() != 0) {
				// if (jrbox != null) {
				if (((iy == iey) || ((iy + ih >= iey + ieh) && iy < iey))
						&& (ix < iex)) { // ��ɾ��Ԫ��ͬһ�е�Ԫ���ƶ�
					iwx = jrdelement.getX() - iwidth;
					jrdelement.setX(iwx);
				} else if (iy > iey) { // ��ɾ��Ԫ�ز�ͬ�У�С��Ԫ�ص�����,ֻ����ǰԪ����һ��Ԫ�ص��ƶ�����С��ȣ�
					if ((ix + iwidth) <= (iex + iew)) {
						if (iex < ix) {
							iwx = jrdelement.getWidth() - iwidth;
							jrdelement.setWidth(iwx);
						} else {
							iwx = jrdelement.getX() - iwidth;
							jrdelement.setX(iwx);
						}
					}
				}
				// }
			} else if (jrdelement instanceof JRStaticText) {
				// JRBox jrbox = ((JRStaticText) jrdelement).getBox();
				// if (jrbox != null && jrbox.getLeftBorder() != 0) {
				// if (jrbox != null) {
				if (((iy == iey) || ((iy + ih >= iey + ieh) && iy < iey))
						&& (ix < iex)) { // ��ɾ��Ԫ��ͬһ�е�Ԫ���ƶ�
					iwx = jrdelement.getX() - iwidth;
					jrdelement.setX(iwx);
				} else if (iy > iey) { // ��ɾ��Ԫ�ز�ͬ�У�С��Ԫ�ص�����,ֻ����ǰԪ����һ��Ԫ�ص��ƶ�����С��ȣ�
					if ((ix + iwidth) <= (iex + iew)) {
						if (iex < ix) {
							iwx = jrdelement.getWidth() - iwidth;
							jrdelement.setWidth(iwx);
						} else {
							iwx = jrdelement.getX() - iwidth;
							jrdelement.setX(iwx);
						}
					}
				}
				// }
			}
		}
	}

	private Collection DoField(JRDataSource jrDS, JasperDesign jasperdesign) throws JRException {
		Collection noField = new ArrayList();
		JRField[] jrFields = jasperdesign.getFields();
		JRMapCollectionDataSource jrmDS = null;
		JRResultSetDataSource jrrDS = null;
		JREmptyDataSource jreDS = null;
		if (jrDS instanceof JRMapCollectionDataSource) {
			jrmDS = (JRMapCollectionDataSource) jrDS;
		} else if (jrDS instanceof JRResultSetDataSource) {
			jrrDS = (JRResultSetDataSource) jrDS;
		} else {
			jreDS = (JREmptyDataSource) jrDS;
		}
		if (jreDS != null) {
			return noField;
		}
		if (jrmDS != null) {
			jrmDS.next();
			for (int i = 0, m = jrFields.length; i < m; i++) {
				if (jrmDS.getFieldValue(jrFields[i]) == null) {
					noField.add(jrFields[i].getName());
					jasperdesign.removeField(jrFields[i]);
				}
			}
			jrmDS.moveFirst();
			return noField;
		} 
		if (jrrDS != null) {
			jrrDS.next();
			for (int i = 0, m = jrFields.length; i < m; i++) {
				try {
					if (jrrDS.getFieldValue(jrFields[i]) == null) {
						noField.add(jrFields[i].getName());
						jasperdesign.removeField(jrFields[i]);
					}
				} catch (JRException ex) {
					noField.add(jrFields[i].getName());
					jasperdesign.removeField(jrFields[i]);
				}
			}
		}
		return noField;
	}

	private Collection DoParameter(Map parameters, JasperDesign jasperdesign) {
		Collection noparameter = new ArrayList();
		JRParameter[] jrparameters = jasperdesign.getParameters();
		for (int i = 0, m = jrparameters.length; i < m; i++) {
			if (jrparameters[i].getName().toString().length() < 7
					|| !jrparameters[i].getName().toString().substring(0, 7)
							.equals("REPORT_")) {
				if (!isReportParameter(parameters, jrparameters[i].getName()
						.toString())) {
					noparameter.add(jrparameters[i].getName());
					jasperdesign.removeParameter(jrparameters[i]);
				}
			}
		}
		return noparameter;
	}

	private boolean isReportParameter(Map parameter, String parameterName) {
		boolean bparameter = false;
		if(parameter == null)return false;
		if (parameter.containsKey(parameterName)) {
			bparameter = true;
		} else {
			bparameter = false;
		}
		return bparameter;
	}

	/**
	 * ��̬����ȥ�����ֶΣ������GroupExpression���У�Ҳ����ɾ��
	 * 
	 * @param jasperdesign
	 * @param nofield
	 */
	private void DoGroupExpression(JasperDesign jasperdesign, Collection nofield) {
		if (nofield == null || nofield.isEmpty()) {
			return;
		}
		JRGroup[] jrgroups = jasperdesign.getGroups();
		if (jrgroups != null && jrgroups.length > 0) {
			for (int j = 0, n = jrgroups.length; j < n; j++) {
				JRDesignGroup jrgroup = (JRDesignGroup) jrgroups[j];
				JRDesignExpression jrExpression = (JRDesignExpression) jrgroup
						.getExpression();
				String expressionText = "";
				if (jrExpression != null) {
					expressionText = jrExpression.getText();
				}
				if (expressionText != null && expressionText != "") {
					String field;
					Iterator iterator = nofield.iterator();
					while (iterator.hasNext()) {
						field = (String) iterator.next();
						if (field != null
								&& expressionText.indexOf(field) != -1) {
							field = "$F{" + field + "}";
							expressionText = StringTools.replaceAll(
									expressionText, field, "\"\"");
						}
					}
					jrExpression.setText(expressionText);
					jrgroup.setExpression(jrExpression);
				}
			}
		}
	}

	/**
	 * ��̬����ȥ�����ֶΣ�����ڳ�detail����������������У�Ҳ����ɾ��
	 * 
	 * @param jasperdesign
	 * @param nofield
	 */
	private void DoOtherBandField(JasperDesign jasperdesign, Collection nofield) {
		if (jasperdesign == null) {
			return;
		}
		if (nofield == null || nofield.isEmpty()) {
			return;
		}
		DoBandField(jasperdesign.getPageHeader(), nofield);
		DoBandField(jasperdesign.getPageFooter(), nofield);
		DoBandField(jasperdesign.getColumnHeader(), nofield);
		DoBandField(jasperdesign.getColumnHeader(), nofield);
	}

	/**
	 * ��̬����ȥ�����ֶΣ�����ڳ�detail����������������У�Ҳ����ɾ��
	 * 
	 * @param jrBand
	 * @param nofield
	 */
	private void DoBandField(JRBand jrBand, Collection nofield) {
		if (jrBand == null) {
			return;
		}
		if (nofield == null || nofield.isEmpty()) {
			return;
		}
		JRDesignBand band = (JRDesignBand) jrBand;
		JRElement[] elements = band.getElements();
		if (elements != null && elements.length > 0) {
			for (int i = 0, m = elements.length; i < m; i++) {
				JRDesignElement jrdelement = (JRDesignElement) elements[i];
				if (jrdelement == null) {
					continue;
				}
				if (jrdelement instanceof JRTextField) {
					JRTextField elementField = (JRTextField) jrdelement;
					String fieldText = elementField.getExpression().getText();
					if (fieldText != null) {
						if (fieldText.indexOf("com.anyi.gp.print.util.") == 0) {
							int bindex1 = fieldText.indexOf("{");
							int bindex2 = fieldText.indexOf("}");
							if (bindex1 != -1 && bindex2 != -1) {
								fieldText = fieldText.substring(bindex1 - 2,
										bindex2 + 1);
							}
						}
						String field;
						Iterator iterator = nofield.iterator();
						while (iterator.hasNext()) {
							field = (String) iterator.next();
							field = "$F{" + field + "}";
							if (fieldText.equals(field)) {
								band.removeElement(jrdelement);
							}
						}
					}
				}
			}
		}
	}
}