package com.anyi.gp.access;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import com.anyi.gp.BusinessException;
import com.anyi.gp.Delta;
import com.anyi.gp.TableData;
import com.anyi.gp.pub.DAOFactory;
import com.anyi.gp.pub.DBHelper;
import com.anyi.gp.pub.DataTools;

/**
 * 存数过程
 * 此类仅从部门预算系统中移植过来，需要进一步的整理
 * @author liuxiaoyong
 */
public class StoredProcedure {
  
  public final static String BASE__FIELD_DATA_TYPE__INTEGER = "0"; //整形

  public final static String BASE__FIELD_DATA_TYPE__CHAR = "1"; //字符

  public final static String BASE__FIELD_DATA_TYPE__DECIMAL = "2"; //小数

  public final static String BASE__FIELD_DATA_TYPE__DATE = "3"; //日期  

  public StoredProcedure() {
  }

  /**
   * 获取输入参数
   * @param conn
   * @param strFunctionName
   * @return
   * @throws BusinessException
   */
  private Delta getParmDelta(Connection conn, String strFunctionName)
    throws BusinessException {
    Delta deltaRet = new Delta();
    
    ResultSet rs = null;
    try {
      strFunctionName = strFunctionName.toUpperCase();

      rs = conn.getMetaData().getProcedureColumns(null,
        conn.getMetaData().getUserName(), strFunctionName, "%");
      Delta deltaParm = new Delta(rs);

      int iParmSeq = 1;
      boolean bHasRet = false;
      for (int i = 0; i < deltaParm.size(); i++) {
        TableData td = (TableData) deltaParm.get(i);
        int iType = td.getFieldInt("COLUMN_TYPE");

        if (iType == DatabaseMetaData.procedureColumnReturn) {
          bHasRet = true;
          continue;
        } else if (iType != DatabaseMetaData.procedureColumnIn) {

        }

        TableData tdParmRet = new TableData();
        tdParmRet.setField("PARM_SEQ", "" + iParmSeq);
        iParmSeq++;
        tdParmRet.setField("PARM_NAME", td.getFieldValue("COLUMN_NAME"));
        tdParmRet.setField("DATA_TYPE", translateFieldDataTypeOfDB(td
          .getFieldInt("DATA_TYPE")));
        tdParmRet.setField("DATA_LENGTH", td.getFieldValue("LENGTH"));
        tdParmRet.setField("DATA_DECIMAL", td.getFieldValue("PRECISION"));
        if (td.getFieldInt("NULLABLE") == DatabaseMetaData.procedureNullable) {
          tdParmRet.setField("IS_NULL", "1");
        } else {
          tdParmRet.setField("IS_NULL", "0");
        }

        if (iType == DatabaseMetaData.procedureColumnOut) {
          tdParmRet.setField("IS_OUT", "1");
        } else {
          tdParmRet.setField("IS_OUT", "0");
        }

        deltaRet.add(tdParmRet);
      }

      if (false == bHasRet) {
        //        throw new BusinessException("存储过程必须定义为:function,且返回结果集",null);
      }

      return deltaRet;
    } catch (Exception ex) {
      throw new BusinessException(ex.toString(), null);
    } finally {
        DBHelper.closeConnection(null, null, rs);
    }
  }
  
  public Delta excuteSP(String strSPName, Map hmParmValue) throws BusinessException{
    Connection conn = null;
    try{
      conn = DAOFactory.getInstance().getConnection();
      return excuteSP(conn, strSPName, hmParmValue, true);
    }finally{
      DBHelper.closeConnection(conn);
    }
  }
  
  public void excuteSP(Connection conn, String strSPName, Map hmParmValue)
    throws BusinessException {
    excuteSP(conn, strSPName, hmParmValue, false);
  }
  /**
   * 执行存储过程
   * @param conn
   * @param strSPName
   * @param hmParmValue
   * @throws BusinessException
   */
  public Delta excuteSP(Connection conn, String strSPName, Map hmParmValue, boolean ret)
    throws BusinessException {
    Delta result = new Delta();
    CallableStatement pst = null;
    ResultSet rs = null;
    try {
      strSPName = strSPName.toUpperCase();

      Delta deltaParm = getParmDelta(conn, strSPName);

      //准备SQL
      StringBuffer strCall = new StringBuffer("{call " + strSPName + " (");
      for (int i = 0; i < deltaParm.size(); i++) {
        strCall.append("? ,");
      }
      if (deltaParm.size() > 0) {
        strCall.delete(strCall.length() - 2, strCall.length());
        strCall.append(") }");
      } else {
        strCall.delete(strCall.length() - 2, strCall.length());
        strCall.append(" }");
      }

      pst = conn.prepareCall(strCall.toString());

      int iHasOut = 0;

      //设置参数值
      for (int i = 0; i < deltaParm.size(); i++) {
        TableData td = (TableData) deltaParm.get(i);
        String strPARM_NAME = td.getFieldValue("PARM_NAME");
        String strDATA_TYPE = td.getFieldValue("DATA_TYPE");
        String strIS_OUT = td.getFieldValue("IS_OUT");

        //输出参数SYS__OUT__MESSAGE
        if ("1".equals(strIS_OUT)) {
          pst.registerOutParameter(i + 1, java.sql.Types.VARCHAR);
          iHasOut = i + 1;
        }

        //空值处理
        String strIS_NULL = td.getFieldValue("IS_NULL");
        String strValue = null;
        if (hmParmValue != null) {
          if (hmParmValue.containsKey(strPARM_NAME))
            strValue = (String) hmParmValue.get(strPARM_NAME);
          if (strValue == null)
            strValue = "";
        }

        if ("0".equals(strIS_NULL) && (strValue == null || strValue.length() == 0)) {
          throw new BusinessException("存储过程参数必须录入数值,存储过程参数名称=" + strPARM_NAME, null);
        }

        //设置参数
        setParameter(pst, i + 1, strDATA_TYPE, true, strValue);
      }
      
      if(ret){
        pst.registerOutParameter(deltaParm.size() + 1, oracle.jdbc.OracleTypes.CURSOR);
      }
      
      pst.execute();

      //获得返回结果
      if (iHasOut > 0) {
        String strOut = pst.getString(iHasOut);
        if (strOut != null && strOut.length() > 0) {
          throw new BusinessException(strOut, null);
        }
      }
      
      if(ret){
        rs = (ResultSet) pst.getObject(deltaParm.size() + 1);
        ResultSetMetaData rsmd = rs.getMetaData();
        int nCount = rsmd.getColumnCount();
        while (rs.next()) {
          TableData temp = DataTools.disposeEntity(rs, nCount, rsmd);
          result.add(temp);
        }
      }
      return result;
      
    }catch (SQLException ex) {
      //ex.printStackTrace();
      throw new BusinessException("执行存储过程" + strSPName + "错误:" + ex.toString(), null);
    }catch (Exception ex) {
      //ex.printStackTrace();
      throw new BusinessException("执行存储过程" + strSPName + "错误:" + ex.toString(), null);
    }
    finally {
      DBHelper.closeConnection(null, pst, rs);
    }
  }

  /**
   * 执行存储过程
   * @param conn
   * @param strSPName
   * @param hmParmValue
   * @throws BusinessException
   */
  public String excuteSPWithResult(String strSPName, Map hmParmValue)
    throws BusinessException {
    Connection conn=null;
    CallableStatement pst = null;
    ResultSet rs = null;
    try {
      strSPName = strSPName.toUpperCase();
      conn= DAOFactory.getInstance().getConnection();
      Delta deltaParm = getParmDelta(conn, strSPName);

      //准备SQL
      StringBuffer strCall = new StringBuffer("{call " + strSPName + " (");
      for (int i = 0; i < deltaParm.size(); i++) {
        strCall.append("? ,");
      }
      if (deltaParm.size() > 0) {
        strCall.delete(strCall.length() - 2, strCall.length());
        strCall.append(") }");
      } else {
        strCall.delete(strCall.length() - 2, strCall.length());
        strCall.append(" }");
      }

      pst = conn.prepareCall(strCall.toString());

      int iHasOut = 0;

      //设置参数值
      for (int i = 0; i < deltaParm.size(); i++) {
        TableData td = (TableData) deltaParm.get(i);
        String strPARM_NAME = td.getFieldValue("PARM_NAME");
        String strDATA_TYPE = td.getFieldValue("DATA_TYPE");
        String strIS_OUT = td.getFieldValue("IS_OUT");

        //输出参数SYS__OUT__MESSAGE
        if ("1".equals(strIS_OUT)) {
          pst.registerOutParameter(i + 1, java.sql.Types.VARCHAR);
          iHasOut = i + 1;
        }

        //空值处理
        String strIS_NULL = td.getFieldValue("IS_NULL");
        String strValue = null;
        if (hmParmValue != null) {
          if (hmParmValue.containsKey(strPARM_NAME))
            strValue = (String) hmParmValue.get(strPARM_NAME);
          if (strValue == null)
            strValue = "";
        }

        if ("0".equals(strIS_NULL) && (strValue == null || strValue.length() == 0)) {
          throw new BusinessException("存储过程参数必须录入数值,存储过程参数名称=" + strPARM_NAME, null);
        }
        //设置参数
        setParameter(pst, i + 1, strDATA_TYPE, true, strValue);
      }
      pst.execute();
      String result="";
      //获得返回结果
      if (iHasOut > 0) {
        String strOut = pst.getString(iHasOut);
        if (strOut != null && strOut.length() > 0) {
          result=strOut;
        }
      }
      return result;
    }catch (SQLException ex) {
      //ex.printStackTrace();
      throw new BusinessException("执行存储过程" + strSPName + "错误:" + ex.toString(), null);
    }catch (Exception ex) {
      //ex.printStackTrace();
      throw new BusinessException("执行存储过程" + strSPName + "错误:" + ex.toString(), null);
    }
    finally {
      DBHelper.closeConnection(conn, pst, rs);
    }
  }
  
  /**
   *设置PreparedStatement对象的参数
   * PreparedStatement pst  数据库PreparedStatement对象
   * int ii 参数序号
   * String intFieldType 参数类型
   * boolean bGetData 是否查询实际数据
   * String strData 数据
   * @throws Exception 标准异常
   */
  private void setParameter(CallableStatement pst, int ii, String intFieldType,
    boolean bGetData, String strData) throws Exception {
    //字符
    if (intFieldType.equals(BASE__FIELD_DATA_TYPE__CHAR)) {
      if (false == bGetData && (strData == null || strData.trim().length() == 0)) {
        pst.setString(ii, " ");
      } else {
        pst.setString(ii, strData);
      }
      return;
    }

    //小数
    if (intFieldType.equals(BASE__FIELD_DATA_TYPE__DECIMAL)) {
      if (false == bGetData && (strData == null || strData.trim().length() == 0)) {
        pst.setDouble(ii, 1);
        return;
      }
      if ((strData == null || strData.trim().length() == 0)) {
        pst.setDouble(ii, 0);
        return;
      }
      if ((strData == null || strData.trim().length() == 0)) {
        pst.setDouble(ii, 0);
        return;
      }

      java.math.BigDecimal dd = new java.math.BigDecimal(strData.trim());
      pst.setBigDecimal(ii, dd);
      return;
    }

    //整形
    if (intFieldType.equals(BASE__FIELD_DATA_TYPE__INTEGER)) {
      if (false == bGetData && (strData == null || strData.trim().length() == 0)) {
        pst.setInt(ii, 1);
        return;
      }
      if ((strData == null || strData.trim().length() == 0)) {
        pst.setInt(ii, 0);
        return;
      }
      if ((strData == null || strData.trim().length() == 0)) {
        pst.setInt(ii, 0);
        return;
      }
      Integer nn = null;
      nn = new Integer(strData.trim());
      pst.setInt(ii, nn.intValue());

      return;
    }

    //日期
    if (intFieldType.equals(BASE__FIELD_DATA_TYPE__DATE)) {
      if (false == bGetData && (strData == null || strData.trim().length() == 0)) {
        pst.setString(ii, "2000-12-12 12:12:12");
        return;
      }

      if ((strData == null || strData.trim().length() == 0)) {
        pst.setString(ii, null);
        return;
      }
      if ((strData == null || strData.trim().length() == 0)) {
        pst.setString(ii, null);
        return;
      }

      //转换
      String strDateTime = null;

      if (strData.trim().length() > 19) {
        strDateTime = strData.trim().substring(0, 19);
      } else if (strData.trim().length() == 10)
        strDateTime = strData.trim() + " 00:00:00";
      else if (strData.trim().length() == 13) {
        strDateTime = strData.trim() + ":00:00";
      } else if (strData.trim().length() == 16) {
        strDateTime = strData.trim() + ":00";
      } else {
        strDateTime = strData.trim();
      }

      pst.setString(ii, strDateTime);
      return;
    }
  }

  private static String translateFieldDataTypeOfDB(int iDBDataType) {
    switch (iDBDataType) {
    case java.sql.Types.BIGINT: //整形数据
    case java.sql.Types.INTEGER:
    case java.sql.Types.SMALLINT:
    case java.sql.Types.TINYINT:
      return BASE__FIELD_DATA_TYPE__INTEGER;

    case java.sql.Types.DATE: //日期数据
    case java.sql.Types.TIME:
    case java.sql.Types.TIMESTAMP:
      return BASE__FIELD_DATA_TYPE__DATE;

    case java.sql.Types.DECIMAL: //小数数据
    case java.sql.Types.DOUBLE:
    case java.sql.Types.FLOAT:
    case java.sql.Types.NUMERIC:
    case java.sql.Types.REAL:
      return BASE__FIELD_DATA_TYPE__DECIMAL;

    default: //字符数据
      return BASE__FIELD_DATA_TYPE__CHAR;
    }
  }
}
