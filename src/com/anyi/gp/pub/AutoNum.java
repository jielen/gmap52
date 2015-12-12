/*$Id: AutoNum.java,v 1.6 2010/02/08 05:57:15 zhuyulong Exp $*/
package com.anyi.gp.pub;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.anyi.gp.TableData;
import com.anyi.gp.meta.Field;
import com.anyi.gp.meta.MetaManager;
import com.anyi.gp.meta.TableMeta;
import com.anyi.gp.util.StringTools;
import com.opensymphony.webwork.ServletActionContext;

public class AutoNum{

	private String compoId = null;

	private String prefixCompo = null;

	private String masterTableId = null;

	private String noFieldName = null;

	private String ruleCode = null;

	private boolean isCont = false;

	private String preFix = null;

	private int indexLen = 0;

  private String isFillZero = null;

	private final Logger log = Logger.getLogger(AutoNum.class);

	public AutoNum(){
	}

  public String getNum(String compoId, TableData entity){
		initialize(compoId);

		String segPreFix = getSegPreFix(entity);
		String counterPreFix = getCounterPreFix(entity, segPreFix);

		lockCounter(counterPreFix);
		String serNum = String.valueOf(getSerialNumber(counterPreFix));
    if("Y".equalsIgnoreCase(this.isFillZero)){
		int serNumLen = serNum.length();
		if(indexLen > serNum.length()){
			for(int i = 0; i < indexLen - serNumLen; i++){
				serNum = "0" + serNum;
  			}
			}
		}
		return preFix + segPreFix + serNum;
	}

	/**
	 * 由部件ID，数据包获得自动编号
	 *
	 * @param compoId
	 *          部件ID
	 * @param typeField -
	 * @param typeTable -
	 * @param entity
	 *          TableData包
	 * @return num 编号
	 * @deprecated {@link #getNum(String, String, String, TableData)}
	 */
	public String getNum(String compoId, String typeField, String typeTable, TableData entity){
		return getNum(compoId, entity);
	}

	/**
	 * 返回数据库里的计数器前缀
	 *
	 * @param entity
	 * @param segNum
	 * @return
	 */
	private String getCounterPreFix(TableData entity, String segNum){
		String counterPreFix = null;
		if(isCont){
			counterPreFix = "noPreFix";
		}else{
			counterPreFix = getAppPreFix(entity) + preFix + segNum;
			if(counterPreFix == null || counterPreFix.equals("")){
				counterPreFix = "noPreFix";
			}
		}
		return counterPreFix;
	}

	private void initialize(String compoId){
		this.compoId = compoId;
		initTableAndNoField();
		initRule();
	}

	private void initRule(){
		Connection conn = DAOFactory.getInstance().getConnection();
    PreparedStatement st = null;
		ResultSet rs = null;

		try{
			String sqlStr = "select RULE_CODE,IS_CONT,NO_INDEX_LEN,NO_PREFIX,IS_FILL_ZERO from AS_NO_RULE where COMPO_ID=? and NUM_TOOL_ID is null";
      st = conn.prepareStatement(sqlStr);

      int i = 1;
      st.setString(i++, compoId);

			rs = st.executeQuery();
			if(rs.next()){
				this.ruleCode = rs.getString("RULE_CODE");
				String isContS = rs.getString("IS_CONT");
				this.isCont = (isContS != null && isContS.equalsIgnoreCase("Y"));
				this.indexLen = rs.getInt("NO_INDEX_LEN");
				this.preFix = rs.getString("NO_PREFIX");
        this.isFillZero=rs.getString("IS_FILL_ZERO");
				if(this.preFix == null){
					this.preFix = "";
				}
			}
		}catch(SQLException se){
			log.error(se);
			throw new RuntimeException(se);
		}finally{
			DBHelper.closeConnection(conn, st, rs);
		}
	}

	/**
	 * 获得中间段
	 *
	 * @param compoId
	 *          部件编码
	 * @param ruleCode
	 *          编码规则
	 * @param entity
	 *          TableData数据包
	 * @return segmentNumber
	 */
	private String getSegPreFix(TableData entity){
		StringBuffer result = new StringBuffer();
		Connection conn = DAOFactory.getInstance().getConnection();
    PreparedStatement st = null;
		ResultSet rs = null;

		String sqlStr = "select SEG_DELI,SEG_FIELD,DATE_FMT,seg_len,seg_fill_posi,seg_fill,SEG_SV,SEG_CONST from AS_NO_RULE_SEG where COMPO_ID = ? and RULE_CODE=? order by ORD_INDEX";
		try{
			st = conn.prepareStatement(sqlStr);

      int j = 1;
      st.setString(j++, compoId);
      st.setString(j++, ruleCode);

			rs = st.executeQuery();
			while(rs.next()){
				String segDeli = rs.getString(1);
				if(segDeli == null){
					segDeli = "";
				}

				String segField = rs.getString(2);
				String datFmt = rs.getString(3);
        int segLen = rs.getInt(4);
        String segPosi = rs.getString(5);
        String segFill = rs.getString(6);
        String segSv = rs.getString(7);
        String segConst = rs.getString(8);

        boolean isDateField = false;

				String fieldValue = null;
				if(segField != null && segField.length() > 0){
					fieldValue = entity.getField(segField).toString();
					if(isDateType(segField)){
	          fieldValue = transDate(fieldValue, datFmt);
	          isDateField = true;
	        }
				}else if(segSv != null && segSv.length() > 0){//增加对环境变量的支持
				  HttpServletRequest request = ServletActionContext.getRequest();
				  fieldValue = SessionUtils.getAttribute(request, segSv);
				  if(segSv.endsWith("Date")){
            fieldValue = transDate(fieldValue, datFmt);
            isDateField = true;
          }
				}else{
				  fieldValue = segConst;
				}

				if (fieldValue == null) {
          fieldValue = "";
        }

        if (!isDateField && fieldValue.length() < segLen) {
          String sf = "";
          for (int i = 0; i < segLen - fieldValue.length(); i++) {
            sf = sf + (null == segFill ? "" : segFill);
          }
          if ("1".equals(segPosi)) {
            fieldValue = sf + fieldValue;
          } else {
            fieldValue = fieldValue + sf;
          }
        }

				result.append(fieldValue + segDeli);
			}

		}catch(SQLException se){
			log.error(sqlStr, se);
			throw new RuntimeException(se);
		}finally{
			DBHelper.closeConnection(conn, st, rs);
		}
		return result.toString();
	}

	private boolean isDateType(String fieldName){
		TableMeta meta = MetaManager.getTableMetaByCompoName(compoId);
		Field field = meta.getField(fieldName);
		return field.getType().equalsIgnoreCase("Date");
	}

	private void lockCounter(String prefix){
		String sql = "update as_no set cur_index = cur_index + 1 where compo_id = ? and fix_segs = ?";
		Object[] params = new Object[]{prefixCompo, prefix};

    int rows = DBHelper.executeUpdate(DAOFactory.getDataSource(), sql, params);
		if(rows == 0){
			String insertSql = "insert into as_no(compo_id,fix_segs,cur_index) values(?, ?, 1)";
			DBHelper.executeUpdate(DAOFactory.getDataSource(), insertSql, params);
		}
	}

	/**
	 * 获得自动编号字段的最大号
	 *
	 * @param compoId
	 *          String
	 * @param fixSegs
	 *          String
	 * @return long
	 */
	private long getSerialNumber(String fixSegs){
		long serialNumber = 0;
		Connection conn = DAOFactory.getInstance().getConnection();
    PreparedStatement st = null;
		ResultSet rs = null;
		String sqlStr = "select CUR_INDEX from AS_NO where COMPO_ID = ? and FIX_SEGS = ?";

    try{
			st = conn.prepareStatement(sqlStr);

      int i = 1;
      st.setString(i++, prefixCompo);
      st.setString(i++, fixSegs);

			rs = st.executeQuery();
			if(rs.next()){
				serialNumber = rs.getInt(1);
			}
		}catch(SQLException se){
			log.error(sqlStr, se);
			throw new RuntimeException(se);
		}finally{
			DBHelper.closeConnection(conn, st, rs);
		}
		return serialNumber;
	}

	/**
	 * 把日期转换成指定格式的字符串
	 *
	 * @param d
	 *          日期
	 * @param dateStr
	 *          日期格式
	 * @return dateStr 日期字符串
	 */
	private String transDate(String d, String dateStr){
		if(d != null && dateStr != null){

			String yearStr = "";
			String simpleYearStr = "";

			String monthStr = "";
			String simpleMonthStr = "";

			String dayStr = "";
			String simpleDayStr = "";

			Vector vecDate = StringTools.splitToVector("-", d);//cp.split("-", d);
			if(vecDate != null && vecDate.size() > 0){
				yearStr = vecDate.elementAt(0).toString();
				simpleMonthStr = vecDate.elementAt(1).toString();
				simpleDayStr = vecDate.elementAt(2).toString();
			}

			if(yearStr.length() > 3){
				simpleYearStr = yearStr.substring(2);
			}else{
				simpleYearStr = yearStr;
			}

			if(simpleMonthStr.length() < 2){
				monthStr = "0" + simpleMonthStr;
			}else{
				monthStr = simpleMonthStr;
			}

			if(simpleDayStr.length() < 2){
				dayStr = "0" + simpleDayStr;
			}else{
				dayStr = simpleDayStr;
			}

			if(dateStr.indexOf("YYYY") > -1){
				dateStr = StringTools.replaceText(dateStr, "YYYY", yearStr);
			}else if(dateStr.indexOf("YY") > -1){
				dateStr = StringTools.replaceText(dateStr, "YY", simpleYearStr);
			}

			if(dateStr.indexOf("MM") > -1){
				dateStr = StringTools.replaceText(dateStr, "MM", monthStr);
			}else if(dateStr.indexOf("M") > -1){
				dateStr = StringTools.replaceText(dateStr, "M", simpleMonthStr);
			}

			if(dateStr.indexOf("DD") > -1){
				dateStr = StringTools.replaceText(dateStr, "DD", dayStr);
			}else if(dateStr.indexOf("D") > -1){
				dateStr = StringTools.replaceText(dateStr, "DD", simpleDayStr);
			}
		}
		if(dateStr == null){
			dateStr = d;
		}
		return dateStr;
	}

	/**
	 * 如果是在部件表里描述的自动编号字段，把除编号字段之外的其它主键值串联起来在 加上“_APP_”作为前缀
	 *
	 * @param compoId
	 * @param entity
	 * @return
	 */
	private String getAppPreFix(TableData entity){
		StringBuffer result = new StringBuffer();
		Connection conn = DAOFactory.getInstance().getConnection();
    PreparedStatement st = null;
		ResultSet rs = null;
		try{
			String sqlStr = "select DATA_ITEM from AS_TAB_COL where TAB_ID = ? and upper(IS_PK) = 'Y' and DATA_ITEM != ? order by ORD_INDEX";
			st = conn.prepareStatement(sqlStr);

      int i = 1;
      st.setString(i++, masterTableId);
      st.setString(i++, noFieldName);

			rs = st.executeQuery();
			while(rs.next()){
				String tStr = rs.getString(1);
				if(tStr != null){
					result.append(entity.getFieldValue(tStr));
				}
			}
			if(result.length() > 0){
				result.append("_APP_");
			}
		}catch(SQLException se){
			log.error(se);
			throw new RuntimeException(se);
		}finally{
			DBHelper.closeConnection(conn, st, rs);
		}
		return result.toString();
	}

	private void initTableAndNoField(){
		Connection conn = DAOFactory.getInstance().getConnection();
    PreparedStatement st = null;
		ResultSet rs = null;

		try{
			String sqlStr = "select MASTER_TAB_ID,NO_FIELD from AS_COMPO where COMPO_ID = ? ";
			st = conn.prepareStatement(sqlStr);

      int i = 1;
      st.setString(i++, compoId);

			rs = st.executeQuery();
			if(!rs.next()){
				return;
			}

      prefixCompo = rs.getString(1) + rs.getString(2);
			masterTableId = rs.getString(1);
			noFieldName = rs.getString(2);

		}catch(SQLException e){
			log.error(e);
			throw new RuntimeException(e);
		}finally{
			DBHelper.closeConnection(conn, st, rs);
		}
	}

	private String newGetSNstr(String compoId, String fixSegs){
		prefixCompo = compoId + "_SN";
		if(StringTools.isEmptyString(fixSegs)){
			fixSegs = "noPreFix";
		}
		lockCounter(fixSegs);
		return String.valueOf(getSerialNumber(fixSegs));
	}

	public String newGetSNfix(String compoId, String fixSegs){
		StringBuffer result = new StringBuffer(newGetSNstr(compoId, fixSegs));
		String sqlStr = "select NO_INDEX_LEN from AS_NO_RULE where COMPO_ID = ?";
    Object[] params = new Object[]{compoId};

		BigDecimal temp = (BigDecimal) DBHelper.queryOneValue(DAOFactory.getDataSource(), sqlStr, params);
		if(temp != null){
			int fixL = temp.intValue();
			int tmpL = result.length();
			if(fixL > tmpL){
				for(int i = 0; i < fixL - tmpL; i++){
					result.insert(0, "0");
				}
			}
		}
		return result.toString();
	}

}
