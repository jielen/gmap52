package com.anyi.gp.print.util;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.w3c.dom.Document;

import com.anyi.gp.TableData;
import com.anyi.gp.util.XMLTools;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2002
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author zhangyw
 * @version 1.0
 */
public class PrintTPLUtil {
	public PrintTPLUtil() {
	}

	/**
	 * 字符串变成整型
	 * 
	 * @param s
	 *            String
	 * @return int
	 */
	public static int str2Int(String s) {
		int result = 0;
		try {
			if ((s != null) && (!s.equals(""))) {
				result = Integer.parseInt(s);
			}
		} catch (NumberFormatException ignored) {
			//
		}
		return result;
	}

	/**
	 * 
	 * @param sStr
	 *            String
	 * @param split
	 *            String
	 * @return List
	 */
	public static List splitString(String sStr, String split) {
		List sStrList = new ArrayList();
		try {
			if (sStr != null && !sStr.equals("")) {
				if (!(sStr.indexOf(split) > -1)) {
					sStr += split;
				}
				String tplCodes = null;
				StringTokenizer stz = new StringTokenizer(sStr, split);
				while (stz.hasMoreTokens()) {
					tplCodes = stz.nextToken();
					sStrList.add(tplCodes);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(
					"Class PrintTPLUtil, Method separateTplCode Error"
							+ e.getMessage() + "\n");
		}
		return sStrList;
	}

	/**
	 * 
	 * @param tStr
	 *            String
	 * @return TableData
	 */
	public static TableData stringToTableData(String tStr) {
		TableData data = new TableData();
		try {
			if (tStr != null && !tStr.equals("")) {
				Document xmlData = XMLTools.stringToDocument(tStr);
				data = new TableData(xmlData.getDocumentElement());
			}
		} catch (Exception e) {
			throw new IllegalArgumentException(
					"StringToTableData不符合TableData格式规范: " + tStr);
		}
		return data;
	}

	/**
	 * 
	 * @param sStr
	 *            String
	 * @param firstStr
	 *            String
	 * @param lastStr
	 *            String
	 * @param isRemove
	 *            boolean
	 * @return List
	 */
	public static List splitString(String sStr, String firstStr, String lastStr, boolean isRemove) {
		List list = new ArrayList();
		try {
			if (sStr != null && firstStr != null && lastStr != null) {
				int index = sStr.indexOf(lastStr);
				int firstStrLen = firstStr.length();
				int lastStrLen = lastStr.length();
				String temp = "";
				while (index > -1) {
					if (isRemove) {
						temp = sStr.substring(sStr.indexOf(firstStr)
								+ firstStrLen, index);
					} else {
						temp = sStr.substring(sStr.indexOf(firstStr), index
								+ lastStrLen);
					}
					temp = packSpecial(temp);
					list.add(temp);
					sStr = sStr.substring(index + lastStrLen);
					index = sStr.indexOf(lastStr);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(
					"Class PrintTPLUtil, Method splitString Error"
							+ e.getMessage() + "\n");
		}
		return list;
	}

	/**
	 * 
	 * @param source
	 *            String
	 * @return String
	 */
	public static String packSpecial(String source) {
		try {
			if (source != null) {
				if (source.indexOf("<") != -1) {
					source = source.substring(source.indexOf("<"));
				}
				if (source.lastIndexOf(">") != -1) {
					source = source.substring(0, source.lastIndexOf(">") + 1);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(
					"Class PrintTPLUtil, Method packSpecial(String source) Error"
							+ e.getMessage() + "\n");
		}
		return source;
	}

	public static boolean isEmptyDataSource(String source) {
		boolean isEmpty = false;
		try {
			source = packSpecial(source);
			if (source == null) {
				isEmpty = true;
			}
			if ("<delta></delta>".equals(source)) {
				isEmpty = true;
			}
			if ("<delta>\n</delta>".equals(source)) {
				isEmpty = true;
			}
		} catch (Exception e) {
			throw new RuntimeException(
					"Class PrintTPLUtil, Method isEmptyDataSource(String source) Error"
							+ e.getMessage() + "\n");
		}
		return isEmpty;
	}

}
