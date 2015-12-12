/** $Id: StringTools.java,v 1.12 2008/09/24 07:03:23 dingyy Exp $ */
package com.anyi.gp.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;

public final class StringTools {
	private StringTools() {
	}

	private final static String ZERO_ZERO = "0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";

	private static final String maxMargin = "                                                                ";

	private static final String[] margins = new String[] { "", "  ", "    ",
			"      ", "        ", "          ", "            " };

	public static String getMargin(int i) {
		if (i < margins.length) {
			return margins[i];
		}
		return maxMargin.substring(0, i * 2);
	}

	/**
	 * 处理换行符，替换为 &#10;
	 */
	public static String escapeLineBreak(String source) {
		if (-1 == source.indexOf('\r') && -1 == source.indexOf('\n')) {
			return source;
		}
		StringBuffer result = new StringBuffer(source);
		for (int i = result.length() - 1; i >= 0; i--) {
			if ('\r' == result.charAt(i)) {
				result.delete(i, i + 1);
			}
			if ('\n' == result.charAt(i)) {
				result.replace(i, i + 1, "&#10;");
			}
		}
		return result.toString();
	}

	public static String doubleApos(String source) {
		String result = source;
		for (int i = 0, j = 0; i < result.length(); i = j + 2) {
			j = result.indexOf("'", i);
			if (j < 0) {
				break;
			}
			result = result.substring(0, j) + "'"
					+ result.substring(j, result.length());
		}
		return result;
	}

	public static String quot(String source) {
		String result = source;
		for (int i = 0, j = 0; i < result.length(); i = j + 2) {
			j = result.indexOf("\"", i);
			if (j < 0) {
				break;
			}
			result = result.substring(0, j) + "&quot;"
					+ result.substring(j + 1, result.length());
		}
		return result;
	}

	public static String formatDate(String value) {
		if (value == null)
			return "";
		String date = value.trim();
		int viPos = date.indexOf(" ");
		if (viPos >= 0) {
			date = value.substring(0, viPos);
		}
		if (date.length() > 10) {
			date = date.substring(0, 10);
		}
		return date;
	}

	/**
	 * 将数字转换成千分位格式.
	 * 
	 * @param s
	 *            要进行千分位转换的源字符串.
	 * @return 已经进行千分位转换的字符串
	 * @author leejianwei
	 */
	public static String kiloStyle(String s1) {
		if (s1 == null || s1.length() == 0)
			return "";
		StringBuffer result = new StringBuffer();
		boolean isMinus = false;
		if (s1.startsWith("-")) {
			s1 = s1.substring(1);
			isMinus = true;
		}
		int index = s1.indexOf(".");
		String s = null;
		String s2 = null;
		if (index != -1) {
			s = s1.substring(0, index);
			s2 = s1.substring(index);
		} else {
			s = s1;
			s2 = "";
		}
		// 千分位格式化后字符串中第一个逗号前字符个数.
		int head = s.length() % 3;
		// 需要加入逗号的个数
		int numOfComma = s.length() / 3;
		if ((head != 0) && (numOfComma != 0)) {
			result.append(s.substring(0, head));
			result.append(",");
			for (int i = 0; i < numOfComma; i++) {
				result.append(s.substring(i * 3 + head, (i + 1) * 3 + head));
				if (i != numOfComma - 1) {
					result.append(",");
				}
			}
		} else if ((head != 0) && (numOfComma == 0)) {
			result.append(s.substring(0, head));
		} else if ((head == 0) && (numOfComma != 0)) {
			for (int j = 0; j < numOfComma; j++) {
				result.append(s.substring(j * 3, (j + 1) * 3));
				if (j != numOfComma - 1) {
					result.append(",");
				}
			}
		} else {
			result.append("");
		}
		result.append(s2);
		String res = result.toString();
		if (isMinus) {
			res = "-" + res;
		}
		return res;
	}

	// 把&lt;变为< ,20040727,wtm
	public static String oplt(String source) {
		if (source == null || source.length() == 0) {
			return source;
		}
		Matcher matcherLt2 = patternLt2.matcher(source);
		return matcherLt2.replaceAll("<");
	}

	// 把&gt;变为> ,20040727,wtm
	public static String opgt(String source) {
		if (source == null || source.length() == 0) {
			return source;
		}
		Matcher matcherGt2 = patternGt2.matcher(source);
		return matcherGt2.replaceAll(">");
	}

	public static String nbsp(String source) {
		if (source == null || source.length() == 0) {
			return source;
		}
		Matcher matcherNbsp = patternNbsp.matcher(source);
		return matcherNbsp.replaceAll("&nbsp;");
	}

	public static String apos(String source) {
		if (source == null || source.length() == 0) {
			return source;
		}
		Matcher matcherApos = patternApos.matcher(source);
		return matcherApos.replaceAll("&apos;");
	}

	public static String lt(String source) {
		if (source == null || source.length() == 0) {
			return source;
		}
		Matcher matcherLt = patternLt.matcher(source);
		return matcherLt.replaceAll("&lt;");
	}

	public static String gt(String source) {
		if (source == null || source.length() == 0) {
			return source;
		}
		Matcher matcherGt = patternGt.matcher(source);
		return matcherGt.replaceAll("&gt;");
	}

	public static String amp(String source) {
		if (source == null || source.length() == 0) {
			return source;
		}
		Matcher matcherAmp = patternAmp.matcher(source);
		return matcherAmp.replaceAll("&amp;");
	}

	public final static Pattern patternNbsp = Pattern.compile(" ");

	public final static Pattern patternAmp = Pattern.compile("&");

  public final static Pattern patternAmp2 = Pattern.compile("&amp;");
  
	public final static Pattern patternQuot = Pattern.compile("\"");

  public final static Pattern patternQuot2 = Pattern.compile("&quot;");
  
	public final static Pattern patternApos = Pattern.compile("'");

	public final static Pattern patternApos2 = Pattern.compile("&apos;");

	public final static Pattern patternLt = Pattern.compile("<");

	public final static Pattern patternLt2 = Pattern.compile("&lt;");

	public final static Pattern patternGt = Pattern.compile(">");

	public final static Pattern patternGt2 = Pattern.compile("&gt;");

	public static String toXMLString(String s) {
		if (s == null || s.length() == 0) {
			return s;
		}
		String result = s;
		Matcher matcherAmp = patternAmp.matcher(result);
		result = matcherAmp.replaceAll("&amp;");
		Matcher matcherQuot = patternQuot.matcher(result);
		result = matcherQuot.replaceAll("&quot;");
		Matcher matcherApos = patternApos.matcher(result);
		result = matcherApos.replaceAll("&apos;");
		Matcher matcherLt = patternLt.matcher(result);
		result = matcherLt.replaceAll("&lt;");
		Matcher matcherGt = patternGt.matcher(result);
		result = matcherGt.replaceAll("&gt;");
		return result;
	}
	
  /**
   * 将xml字符串中的特殊字符进行转换，如&gt;换成<
   * @param xml
   * @return
   */
  public static String convertXML(String xml){
    if (xml == null || xml.length() == 0) {
      return xml;
    }
    String result = xml;
    Matcher matcherAmp = patternAmp2.matcher(result);
    result = matcherAmp.replaceAll("&");
    Matcher matcherQuot = patternQuot2.matcher(result);
    result = matcherQuot.replaceAll("\"");
    Matcher matcherApos = patternApos2.matcher(result);
    result = matcherApos.replaceAll("'");
    Matcher matcherLt = patternLt2.matcher(result);
    result = matcherLt.replaceAll("<");
    Matcher matcherGt = patternGt2.matcher(result);
    result = matcherGt.replaceAll(">");
    return result;  
  }
  
	public static String packSpecial(String s) {
		if (s == null || s.length() == 0) {
			return s;
		}
		String result = s;
		Matcher matcherAmp = patternAmp.matcher(result);
		result = matcherAmp.replaceAll("&amp;");
		Matcher matcherQuot = patternQuot.matcher(result);
		result = matcherQuot.replaceAll("&quot;");
		Matcher matcherLt = patternLt.matcher(result);
		result = matcherLt.replaceAll("&lt;");
		Matcher matcherGt = patternGt.matcher(result);
		result = matcherGt.replaceAll("&gt;");
		return result;
	}

	public static String addZero(String source, int z) {
		if (source == null || source.equals("")) {
			source = "0";
		}
		StringBuffer result = new StringBuffer(source);
		int i = source.indexOf(".");
		if (i == -1) {
			if (z > 0) {
				result.append(".");
			}
			while (z > 0) {
				result.append("0");
				z--;
			}
		} else {
			z = z - (source.length() - i - 1);
			while (z > 0) {
				result.append("0");
				z--;
			}
		}
		if (i == 0)
			result.insert(0, "0");
		return result.toString();
	}

	public static String aposConvert(String source) {
		if (source == null || source.length() == 0) {
			return source;
		}
		String result = source;
		Matcher matcherApos2 = patternApos2.matcher(result);
		return matcherApos2.replaceAll("'");
	}

	public static String replaceText(String strTmp, String strS, String strD) {
		return replaceAll(strTmp, strS, strD);
	}

	/**
	 * 本函数由刘明加入
	 * 
	 * @param s
	 * @param separator
	 * @return
	 */
	// 按照 separator 分离字符串 s 为字符串数组
	public static String[] split2(String s, String separator) {
		if (null == s || 0 == s.trim().length()) {
			return null;
		}
		String[] result = null;
		String tmp[] = new String[1000]; // TCJLODO:
		int k = 0;
		int j = 0;
		for (int i = 0; i < s.length(); i++) {
			if (separator.equals(s.substring(i, i + 1))) {
				tmp[k++] = s.substring(j, i);
				j = i + 1;
			}
		}
		tmp[k++] = s.substring(j, s.length());
		if (k == 0) {
			return result;
		}
		result = new String[k];
		for (int i = 0; i < k; i++) {
			result[i] = tmp[i];
		}
		return result;
	}

	/**
	 * @Title 将条件以逗号为分隔符分开，返回字符串的list，包括引号
	 * @param strS
	 *            源串
	 * @param strChar
	 *            分隔符
	 * @return
	 */
	public static List split(String strS, String strChar) {
		List list = new ArrayList();
		if (strS == null)
			return list;
		int index = strS.indexOf(strChar);
		String strTmp = strS;
		int len = strS.length(), charLen = strChar.length();
		if (index == -1) {
			list.add(strS); // 只有一个条件时的处理
		} else {
			while (index > -1) { // 两个以上条件的处理
				len = strTmp.length();
				list.add(strTmp.substring(0, index + charLen - 1));
				strTmp = strTmp.substring(index + strChar.length(), len);
				index = strTmp.indexOf(strChar);
				if (index == -1) { // 剩下最后一个条件时
					list.add(strTmp.substring(0, strTmp.length()));
				}
			}
		}
		return list;
	}

	public static List split(String strS, String strChar, boolean dropNull) {
		List list = new ArrayList();
		List list2 = split(strS, strChar);
		for (int i = 0; list2 != null && i < list2.size(); i++) {
			if (dropNull) {
				if (StringTools.isEmptyString(list2.get(i))) {
					// do nothing
				} else {
					list.add(list2.get(i));
				}
			} else {
				list.add(list2.get(i));
			}
		}
		return list;
	}

	/**
	 * @Title 处理成用户可以识别的条件，in、between and、>=、<=、=等条件 此条件为一个字段所对应的全部条件
	 * @param list
	 * @return
	 */
	public static List sortList(List list) {
		List result = new ArrayList();
		StringBuffer sbIn = new StringBuffer("in (");
		int nTmp = 0;
		for (int i = 0, j = list.size(); i < j; i++) {
			String tmp = (String) list.get(i);
			if (tmp.indexOf("..") == -1) { // 没有..的情况
				String strFirst = tmp.substring(0, 1);
				if (strFirst.equalsIgnoreCase("<")
						|| strFirst.equalsIgnoreCase(">")
						|| strFirst.equalsIgnoreCase("=")
						|| strFirst.equalsIgnoreCase("!")) {
					// 处理>、<、=、<>、>=、<=等的情况
					result.add(tmp);
				} else if ((tmp.length() > 5)
						&& (tmp.substring(0, 5).equalsIgnoreCase("like "))) {
					result.add(tmp);
				} else {
					// 处理in的情况
					if (nTmp != 0) {
						sbIn.append(",");
					}
					sbIn.append(tmp);
					nTmp = 1;
				}
			} else {
				// 处理between的情况
				int index = tmp.indexOf("..");
				String strBetween = "between " + tmp.substring(0, index)
						+ " and " + tmp.substring(index + 2, tmp.length());
				result.add(strBetween);
			}
		}
		sbIn.append(")");
		if (sbIn.toString().indexOf("in ()") == -1) {
			result.add(sbIn.toString());
		}
		return result;
	}

	public static String conv8859(String strS) {
		String result = null;
		try {
			byte[] byteTemp = strS.getBytes("8859_1");
			result = new String(byteTemp, "GBK");
		} catch (UnsupportedEncodingException ex) {
			throw new RuntimeException("StringTools类的conv8859方法：取值集时错误！"
					+ "Byte字符处理：" + ex.toString());
		}
		return result;
	}

	/** 把"yyyy-MM-dd"的日期形式转换为java.sql.Date类型 */
	public static Date getSQLDateFromString(String strDate) {
		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
		Date date;
		try {
			date = new Date(formater.parse(strDate).getTime());
		} catch (java.text.ParseException e) {
			return null;
		}
		return date;
	}

	/**
	 * 判断是否是空字符串
	 * 
	 * @param obj
	 *            要判断的对象
	 * @return 如果 obj 为 null 或者是长度为零的字符串，返回 true； 其它情况返回 false，如果 obj 不是 String
	 *         类型，返回 false。<code>
	 *   return (null == obj ||
	 *          (obj instanceof String && 0 == ((String)obj).length()));
	 * </code>
	 */
	public static boolean isEmptyString(Object obj) {
		return (null == obj || (obj instanceof String && 0 == ((String) obj)
				.length()));
	}

	private static final SimpleDateFormat df = new SimpleDateFormat(
			"yyyy-MM-dd");

	public static String getDateString(java.util.Date dt) {
		return df.format(dt);
	}

	public static Date toDate(String dateString) {
		if (dateString == null || dateString.length() < 10)
			return new Date(System.currentTimeMillis());
		else {
			return Date.valueOf(dateString.substring(0, 10));
		}
	}

	public static Timestamp toTimestamp(String dateString) {
		if (dateString == null || dateString.length() < 10)
			return new Timestamp(System.currentTimeMillis());
		else {
			String realTime = getRealTimestamp(dateString);
			return Timestamp.valueOf(realTime);
		}
	}

	private static String getRealTimestamp(String value) throws IllegalArgumentException {
		String result = "";
		String timePattern = "^(\\d\\d\\d\\d)-(\\d?\\d)-(\\d?\\d)\\s?(\\d?\\d)?(:\\d?\\d)?(:\\d?\\d)?$";
		Pattern pattern = Pattern.compile(timePattern);
		Matcher matcher = pattern.matcher(value);
		if (matcher.matches()) {
			result = matcher.group(1) + "-" + 
					 matcher.group(2) + "-" +
					 matcher.group(3);
			if (matcher.group(4) == null) {
				result += " 00:00:00";
				return result;
			} else {
				result += " " + matcher.group(4);
			}
			if (matcher.group(5) == null) {
				result += ":00:00";
				return result;
			} else {
				result += matcher.group(5);
			}
			if (matcher.group(6) == null) {
				result += ":00";
				return result;
			} else {
				result += matcher.group(6);
			}
			return result;
		} else {
			throw new IllegalArgumentException("错误的Timestamp格式!");
		}
	}
	public static String getDateString(java.util.Date dt, String formatString) {
		SimpleDateFormat df = new SimpleDateFormat(formatString);
		return df.format(dt);
	}

	public static int getMaxDay(int year, int month) {
		int result = 31;
		if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8
				|| month == 10 || month == 12) {
			result = 31;
		} else if (month == 4 || month == 6 || month == 9 || month == 11) {
			result = 30;
		} else {
			if (year % 4 == 0) {
				result = 29;
			} else {
				result = 28;
			}
		}
		return result;
	}

	public static String deleteAttribute(String source, String strBegin,
			String strEnd) {
		StringBuffer result = new StringBuffer();
		int searchBeginIndex = 0;
		while (true) {
			int beginIndex = source.indexOf(strBegin, searchBeginIndex);
			if (beginIndex < 0) {
				break;
			}
			int endIndex = source.indexOf(strEnd, beginIndex);
			result.append(source.substring(searchBeginIndex, beginIndex));
			result.append(strBegin + strEnd);
			searchBeginIndex = endIndex + strEnd.length();
		}
		result.append(source.substring(searchBeginIndex));
		return result.toString();
	}

	public static String deleteTag(String source, String strBegin, String strEnd) {
		StringBuffer result = new StringBuffer();
		int searchBeginIndex = 0;
		while (true) {
			int beginIndex = source.indexOf(strBegin, searchBeginIndex);
			if (beginIndex < 0) {
				break;
			}
			int endIndex = source.indexOf(strEnd, beginIndex);
			result.append(source.substring(searchBeginIndex, beginIndex));
			searchBeginIndex = endIndex + strEnd.length();
		}
		result.append(source.substring(searchBeginIndex));
		return result.toString();
	}

	/**
	 * 去除两边的字符,offset指定了要去除的字符数量
	 * 
	 * @param source
	 * @param offset
	 * @return
	 */
	public static String getMiddleString(String source, int offset) {
		return source.substring(offset, source.length() - offset);
	}

	public static String addDoubleQuot(String source, String strBegin,
			String strEnd) {
		StringBuffer result = new StringBuffer();
		int searchBeginIndex = 0;
		for (; true;) {
			int beginIndex = source.indexOf("id=", searchBeginIndex);
			if (beginIndex < 0) {
				break;
			}
			int endIndex = source.indexOf(" ", beginIndex);
			// 判断beginIndex,与endIndex之间是否有"号 ，如果有，说明id不是标记，而是属性值
			if (source.substring(beginIndex, endIndex).indexOf("\"") != -1)
				break;
			result.append(source.substring(searchBeginIndex, beginIndex
					+ strBegin.length()));
			result.append("\"");
			result.append(source.substring(beginIndex + strBegin.length(),
					endIndex));
			result.append("\"");
			searchBeginIndex = endIndex;
		}
		result.append(source.substring(searchBeginIndex));
		return result.toString();
	}

	public static String convXML(String source) {
		String strTd = deleteAttribute(source, "<TD", ">");
		String strTr = deleteAttribute(strTd, "<TR", ">");
		String strInput = deleteTag(strTr, "<TD><INPUT", "</TD>");
		String strChk = deleteTag(strInput, "<COL id=chkCol", "</COL>");
		String strId = addDoubleQuot(strChk, "id=", " ");
		return strId;
	}

	public static String deleteComma(String source1) {
		String result = "";
		if (source1.equalsIgnoreCase("")) {
			return "";
		}
		int index;
		String source = "";
		String source2 = "";
		boolean isMinus = false;
		String firstChar = source1.substring(0, 1);
		if (firstChar.equalsIgnoreCase("-")) {
			source1 = source1.substring(1);
			isMinus = true;
		}
		index = source1.indexOf(".");
		if (index != -1) {
			source = source1.substring(0, index);
			source2 = source1.substring(index);
		} else {
			source = source1;
			source2 = "";
		}
		int first = source.indexOf(",");
		if (first == -1) {
			if (isMinus) {
				source1 = "-" + source1;
			}
			return source1;
		}
		while (first != -1) {
			result = result + source.substring(0, first);
			source = source.substring(first + 1);
			first = source.indexOf(",");
		}
		result = result + source;
		result = result + source2;
		if (isMinus) {
			result = "-" + result;
		}
		return result;
	}

	/**
	 * 获得部件对应的产品（子系统）名称
	 * <p>
	 * 结果将自动转换为大写字母
	 * 
	 * @param compoId
	 * @return
	 */
	public static String getSubSys(String compoId) {
		String subSys = compoId;
		if (compoId != null) {
			subSys = subSys.toUpperCase();
			int index = subSys.indexOf("_");
			if (index > 0) {
				subSys = subSys.substring(0, index);
			}
		}
		return subSys;
	}

	/**
	 * 用一字符分隔字符串
	 * 
	 * @param var
	 *            -分割字符
	 * @param word
	 *            -字符串
	 * @return Vector
	 */
	public static Vector splitToVector(String var, String word) {
		Vector vec = new Vector();
		String tmpStr = "";
		int startIndex = 0;
		if (var != null && var.length() > 0 && word != null
				&& word.length() > 0) {
			for (; word.indexOf(var) > -1;) {
				startIndex = word.indexOf(var);
				tmpStr = word.substring(0, startIndex);
				word = word.substring(startIndex + 1, word.length());
				vec.addElement(tmpStr);
			}
			vec.addElement(word);
		}
		return vec;
	}

	/**
	 * <p>
	 * 将字符串用分割符分割(单一字符) 例如"aaa|bbb|ccc|ddd",分割符'|' 形成Vector类型
	 * 
	 * @param str
	 *            原字符串
	 * @param c
	 *            分割字符
	 * @return Vector
	 */
	public static final Vector split(String str, char c) {
		Vector vec = new Vector();
		if (str == null || str.length() == 0) {
			return vec;
		}
		if (!str.endsWith(String.valueOf(c))) {
			str = str + c;
		}
		int i = 0;
		int j = 0;
		while ((j = str.indexOf(c, i)) != -1) {
			// System.out.println(str.substring(i,j));
			vec.add(str.substring(i, j));
			i = j + 1;
		}
		return vec;
	}

	/**
	 * @Title 取得某月的起止日期，供部件的日期字段专用
	 * @return result[0]开始日期，result[1]终止日期
	 */
	public static String[] getBeginEndDate() {
		String[] result = new String[2];
		GregorianCalendar currentDate = new GregorianCalendar();
		int iyear = currentDate.get(Calendar.YEAR);
		int imonth = currentDate.get(Calendar.MONTH) + 1;
		int maxDay = StringTools.getMaxDay(iyear, imonth);
		String startDate = null;
		if (imonth < 10) {
			startDate = String.valueOf(iyear) + "-0" + String.valueOf(imonth)
					+ "-01";
		} else {
			startDate = String.valueOf(iyear) + "-" + String.valueOf(imonth)
					+ "-01";
		}
		result[0] = startDate;
		String endDate = null;
		if (imonth < 10) {
			endDate = String.valueOf(iyear) + "-0" + String.valueOf(imonth)
					+ "-" + String.valueOf(maxDay);
		} else {
			endDate = String.valueOf(iyear) + "-" + String.valueOf(imonth)
					+ "-" + String.valueOf(maxDay);
		}
		result[1] = endDate;
		return result;
	}

	/**
	 * @Title 截取起始字符串和终止字符串之间的子串 从config_xxxx.xml文件名截取子系统名专用
	 * @param source
	 * @param firstStr
	 * @param lastStr
	 * @return
	 */
	public static String getSubStrByTags(String source, String firstStr,
			String lastStr) {
		String result = null;
		int first, last;
		first = source.indexOf(firstStr);
		last = source.indexOf(lastStr);
		if (first > 0 && last > 0 && last > first) {
			result = source.substring(first + 1, last).toUpperCase();
		}
		return result;
	}

	/**
	 * 将数值变为 0 填充的字符串
	 * 
	 * @param index
	 *            整数
	 * @return 返回至少为五位数字的序列号，例如 00001, 01000, 1000000 等，不带引号
	 */
	public static String getTextSeq(int index) {
		StringBuffer result = new StringBuffer();
		java.util.Date dt = new java.util.Date();
		result.append(String.valueOf(dt.getTime()));
		if (index < 10) {
			result.append("0000");
		} else if ((index >= 10) && (index < 100)) {
			result.append("000");
		} else if ((index >= 100) && (index < 1000)) {
			result.append("00");
		} else {
			result.append("0");
		}
		result.append(String.valueOf(index));
		return result.toString();
	}

	public static String replaceAll(String strSource, String strOld,
			String strNew) {
		if (null == strSource || null == strOld || null == strNew
				|| 0 == strOld.length() || strOld.equals(strNew)) {
			return strSource;
		}
		// Pattern pattern = Pattern.compile(strOld);
		// Matcher matcher = pattern.matcher(strSource);
		// return matcher.replaceAll(strNew);
		String strDest = "";
		String strTemp;
		int iOldLength = strOld.length();
		int iStartIndex = strSource.indexOf(strOld);
		while (iStartIndex >= 0) {
			strTemp = strSource.substring(0, iStartIndex);
			strDest = strDest + strTemp + strNew;
			strSource = strSource.substring(iStartIndex + iOldLength, strSource
					.length());
			iStartIndex = strSource.indexOf(strOld);
		}
		strDest = strDest + strSource;

		return strDest;
	}

	public static final String CONTENT_TYPE = "text/html; charset=GBK";

	public static final String XML_TYPE = "<?xml version=\"1.0\" encoding=\"GBK\"?>";

	/**
	 * 获取有效的 XML tag 名称，将非法字符替换为下划线 _
	 * 
	 * @param name
	 * @return
	 */
	public static String getValidTagName(String name) {
		return replaceChars(name, " /", "__");
	}

	/**
	 * 一次性替换多个字符
	 */
	public static String replaceChars(String str, String searchChars,
			String replaceChars) {
		if (str == null || str.length() == 0 || searchChars == null
				|| searchChars.length() == 0) {
			return str;
		}
		char[] chars = str.toCharArray();
		int len = chars.length;
		boolean modified = false;
		for (int i = 0, isize = searchChars.length(); i < isize; i++) {
			char searchChar = searchChars.charAt(i);
			if (replaceChars == null || i >= replaceChars.length()) {
				// delete
				int pos = 0;
				for (int j = 0; j < len; j++) {
					if (chars[j] != searchChar) {
						chars[pos++] = chars[j];
					} else {
						modified = true;
					}
				}
				len = pos;
			} else {
				// replace
				for (int j = 0; j < len; j++) {
					if (chars[j] == searchChar) {
						chars[j] = replaceChars.charAt(i);
						modified = true;
					}
				}
			}
		}
		if (modified == false) {
			return str;
		}
		return new String(chars, 0, len);
	}

	public static String replaceOnce(String strSource, String strOld,
			String strNew) {
		if (null == strSource || null == strOld || null == strNew
				|| 0 == strOld.length()) {
			return strSource;
		}
		StringBuffer strDest = new StringBuffer();
		int iOldLength = strOld.length();
		int iStartIndex = strSource.indexOf(strOld);
		if (iStartIndex >= 0) {
			strDest.append(strSource.substring(0, iStartIndex));
			strDest.append(strNew);
			strDest.append(strSource.substring(iStartIndex + iOldLength));
		}
		return strDest.toString();
	}

	public static String[] splitA(String str, char separatorChar) {
		if (str == null) {
			return null;
		}
		int len = str.length();
		if (len == 0) {
			return new String[0];
		}
		List list = new ArrayList();
		int i = 0, start = 0;
		boolean match = false;
		while (i < len) {
			if (str.charAt(i) == separatorChar) {
				if (match) {
					list.add(str.substring(start, i));
					match = false;
				}
				start = ++i;
				continue;
			}
			match = true;
			i++;
		}
		if (match) {
			list.add(str.substring(start, i));
		}
		return (String[]) list.toArray(new String[list.size()]);
	}

	/** 如果字符串为 null，返回 ""，否则直接返回 */
	public static String toString(String s) {
		return (null == s) ? "" : s;
	}

	public static String cutLastDblQuot(String source) {
		String result = source;
		if (source == null)
			return null;
		int lastIndex = result.lastIndexOf("\"");
		if (lastIndex > 0)
			result = result.substring(0, lastIndex);
		return result;
	}

	/**
	 * 把形如yyyymmddhhmmss形式的时间转换为yyyy-mm-dd hh:mm:ss
	 * 
	 * @param String
	 *            inTime 输入时间
	 * @return String
	 */
	public static String toTimeString(String inTime) {
		if (inTime == null) {
			return "";
		}
		if (inTime.length() != 14) {
			return inTime;
		}
		StringBuffer outTime = new StringBuffer();
		try {
			outTime.append(inTime.substring(0, 4)).append("-");
			outTime.append(inTime.substring(4, 6)).append("-");
			outTime.append(inTime.substring(6, 8)).append(" ");
			outTime.append(inTime.substring(8, 10)).append(":");
			outTime.append(inTime.substring(10, 12)).append(":");
			outTime.append(inTime.substring(12));
		} catch (IndexOutOfBoundsException e) {
			return inTime;
		}
		return outTime.toString();
	}

	/**
	 * 将字符串转换成Integer
	 * 
	 * @param str
	 *            需要转换的字符串
	 * @return 返回转换后的Integer值。如果不能转换，返回null 如果str为null，也返回null
	 */
	/* by zhanggh 050623 */
	public static Integer parseStr2Int(String str) {
		Integer intResult = null;
		intResult = Integer.valueOf(str);
		return intResult;
	}

	/**
	 * 将字符串转换成Float
	 * 
	 * @param str
	 *            需要转换的字符串
	 * @return 返回转换后的Float值。如果不能转换，返回null 如果str为null，也返回null
	 */
	/* by zhanggh 050623 */
	public static Float parseStr2Float(String str) {
		Float floatResult = null;
		floatResult = Float.valueOf(str);
		return floatResult;
	}

	/**
	 * 填充任意位的0
	 * 
	 * @return 包含0的字符串
	 */
	public static String formatNum2Str(int number, int digit) {
		String[] strs = new String[digit];
		String result = "";
		int tempNum = 1;
		int temp;
		for (int i = 1; i <= digit; i++) {
			if (number >= tempNum) {
				temp = number % 10;
				strs[digit - i] = temp + "";
				number = (number - temp) / 10;
			} else {
				strs[digit - i] = "0";
			}
		}
		for (int j = 0; j < digit; j++) {
			result += strs[j];
		}
		return result;
	}

	/**
	 * 空值情况下的返回值;
	 * 
	 * @param sValue
	 * @param sRetValue
	 * @return String
	 */
	public static String ifNull(String sValue, String sRetValue) {
		if (sValue == null)
			return sRetValue;
		return sValue;
	}

	public static String ifNull(Object sValue, String sRetValue) {
		if (sValue == null)
			return sRetValue;
		return sValue.toString();
	}

	public static String createNbsp(int n) {
		StringBuffer result = new StringBuffer();
		for (int i = 1; i < n; i++) {
			result.append("&nbsp;&nbsp;&nbsp;");
		}
		return result.toString();
	}

	public static String getFieldXml(String fieldName, String fieldValue) {
		StringBuffer result = new StringBuffer();
		result.append("<field name=\"");
		result.append(fieldName);
		result.append("\" value=\"");
		result.append(fieldValue + "\"/>\n");
		return result.toString();
	}

	public static String getWraptXml(String wraper, String content) {
		StringBuffer result = new StringBuffer();
		result.append("<" + wraper + ">\n");
		result.append(content);
		result.append("</" + wraper + ">\n");
		return result.toString();
	}

	public static boolean isListEmpty(List list) {
		if (list == null || (list != null && list.size() == 0)) {
			return true;
		} else {
			return false;
		}
	}

	public static String wrapChars(String text, int[] indexs1, int[] indexs2) {
		char[] chars = text.toCharArray();
		for (int i = 0; i < indexs1.length; i++) {
			if (i >= indexs2.length)
				break;
			if (indexs1[i] < 0 || indexs1[i] >= chars.length)
				continue;
			if (indexs2[i] < 0 || indexs2[i] >= chars.length)
				continue;
			char char1 = chars[indexs1[i]];
			chars[indexs1[i]] = chars[indexs2[i]];
			chars[indexs2[i]] = char1;
		}
		return new String(chars);
	}

	public static String replicate(String str, int count) {
		if (str == null)
			return null;
		if (str.length() == 0)
			return "";
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < count; i++) {
			buf.append(str);
		}
		return buf.toString();
	}

	public static String replicateZero(int count) {
		if (count <= 0)
			return "";
		return ZERO_ZERO.substring(ZERO_ZERO.length() - count);
	}

	public static String doubleToString(double value, int decLen,
			boolean isForceDecLen) {
		if (decLen <= 0)
			return String.valueOf(value);
		long exponent = BigInteger.valueOf(10).pow(decLen).longValue();
		double dec = (value - Double.valueOf(String.valueOf(value)).longValue())
				* exponent;
		long decLong = Double.valueOf(String.valueOf(dec)).longValue();
		if (dec - decLong != 0 || !isForceDecLen)
			return String.valueOf(value);
		String decStr = String.valueOf(decLong);
		decStr = replicateZero(decLen - decStr.length()) + decStr;
		return String
				.valueOf(Double.valueOf(String.valueOf(value)).longValue())
				+ "." + decStr;
	}

	public static String numericToString(String num, int decLen,
			boolean isForceDecLen) {
		if (decLen <= 0)
			return num;
		String[] partArray = num.split(".");
		String intPart = "";
		String decStr = "";
		if (partArray.length < 3) {
			intPart = num;
			decStr = replicateZero(decLen);
		} else {
			if (partArray[2].length() > decLen && !isForceDecLen)
				return num;
			// //num = partArray[0];
			decStr = partArray[2]
					+ replicateZero(decLen - partArray[2].length());
		}
		String result = intPart + "." + decStr;
		return result;
	}

	public static boolean isMatched(String matchStr, String value) {
		if (null == matchStr || null == value)
			return false;
		try {
			RE re = new RE(matchStr);
			if (re.match(value)) {
				if (re.getParen(0).equals(value))
					return true;
			}
		} catch (RESyntaxException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return false;
	}

	public static boolean isIn(String matchStr, String value) {
		if (null == matchStr || null == value)
			return false;
		matchStr = matchStr + ",";
		int firstIndex = matchStr.indexOf(",");
		while (-1 != firstIndex) {
			try {
				String pattern = matchStr.substring(0, firstIndex);
				RE re = new RE(pattern);
				if (re.match(value)) {
					if (re.getParen(0).equals(value))
						return true;
				}
			} catch (RESyntaxException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
			matchStr = matchStr.substring(firstIndex + 1);
			firstIndex = matchStr.indexOf(",");
		}
		return false;
	}

	/**
	 * 获取 digest;
	 * 
	 * @param sText
	 * @return String digest / ""
	 */
	public static String getDigest(String sText) {
		if (sText == null)
			return "";
		String vsDigest = "";
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		md.update(sText.getBytes());
		byte[] m = md.digest();
		byte[] b = Base64.encode(m);
		vsDigest = new String(b);
		return vsDigest;
	}

	/**
	 * @param strFieldType
	 * @param fieldValue
	 * @return 根据字段的类型，格式化字段值，使得满足数据库查询的要求
	 */
	public static Object formatFieldValueByType(String strFieldType,
			String fieldValue) {
		Object result = new Object();
		if (strFieldType.equalsIgnoreCase("Num")) {
			result = fieldValue;
		} else if (strFieldType.equalsIgnoreCase("Date")) {
			Date date = Date.valueOf(fieldValue);
			result = date;
		} else if (strFieldType.equalsIgnoreCase("Datetime")) {
			Timestamp ts = Timestamp.valueOf(fieldValue);
			result = ts;
		} else {
			result = fieldValue;
		}
		return result;
	}

	public static String trimToEmpty(String s) {
		if (null == s)
			return "";
		return s.trim();
	}

	/**
	 * 将参数转换为布尔值，只有当参数不为空且等于 Y 或 y 时才返回真值
	 * 
	 * @param value
	 *            参数
	 * @return return (null != value && value.toString().equalsIgnoreCase("Y"))
	 */
	public static boolean toBooleanYN(Object value) {
		return (null != value && value.toString().equalsIgnoreCase("Y"));
	}

	/**
	 * 将list转换成string，以separator分割
	 * 
	 * @param fieldList
	 * @param separator
	 * @return
	 */
	public static String parseString(List fieldList, String separator) {
		StringBuffer voFieldBuf = new StringBuffer();
		if (fieldList != null) {
			for (int i = 0; i < fieldList.size(); i++) {
				if (i > 0)
					voFieldBuf.append(separator);
				voFieldBuf.append(fieldList.get(i));
			}
		}
		return voFieldBuf.toString();
	}

	public static String inToStr(InputStream in) {
		if (in == null)
			return null;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			out(in, out);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return out.toString();
	}

	public static String inToStr(InputStream in, String charset) {
		if (in == null)
			return null;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			out(in, out);
			return out.toString(charset);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static int out(InputStream in, OutputStream out) throws IOException {
		if (in == null)
			return 0;
		if (out == null)
			return 0;
		try {
			int len = -1;
			int totalLength = 0;
			byte data[] = new byte[8192];
			while ((len = in.read(data)) != -1) {
				totalLength += len;
				out.write(data, 0, len);
			}
			return totalLength;
		} catch (IOException e) {
			throw e;
		} finally {
			out.flush();
		}
	}

	public static List findWords(String src, String c) {
		List result = new ArrayList();
		int i = 0, j = 0;
		String mid = "";
		while (j < src.length() && src.indexOf(c, j + 1) > -1) {
			i = src.indexOf(c, j + 1);
			if (i == -1)
				break;
			j = src.indexOf(c, i + 1);
			if (j == -1)
				break;
			mid = src.substring(i + 1, j);
			if (null != mid && mid.length() > 0) {
				result.add(mid);
			}
		}
		return result;
	}
	
  public static String combCondition(String cond1, String cond2){
    if(cond1 != null && cond1.length() > 0) {
      if(cond2 != null && cond2.length() > 0)
        return cond1 + ";" + cond2;
      else
        return cond1;
    }
    else if(cond2 != null && cond2.length() > 0) 
      return cond2;
    else
      return "";
  }
}
