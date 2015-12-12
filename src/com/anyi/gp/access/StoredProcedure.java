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
 * ��������
 * ������Ӳ���Ԥ��ϵͳ����ֲ��������Ҫ��һ��������
 * @author liuxiaoyong
 */
public class StoredProcedure {
  
  public final static String BASE__FIELD_DATA_TYPE__INTEGER = "0"; //����

  public final static String BASE__FIELD_DATA_TYPE__CHAR = "1"; //�ַ�

  public final static String BASE__FIELD_DATA_TYPE__DECIMAL = "2"; //С��

  public final static String BASE__FIELD_DATA_TYPE__DATE = "3"; //����  

  public StoredProcedure() {
  }

  /**
   * ��ȡ�������
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
        //        throw new BusinessException("�洢���̱��붨��Ϊ:function,�ҷ��ؽ����",null);
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
   * ִ�д洢����
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

      //׼��SQL
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

      //���ò���ֵ
      for (int i = 0; i < deltaParm.size(); i++) {
        TableData td = (TableData) deltaParm.get(i);
        String strPARM_NAME = td.getFieldValue("PARM_NAME");
        String strDATA_TYPE = td.getFieldValue("DATA_TYPE");
        String strIS_OUT = td.getFieldValue("IS_OUT");

        //�������SYS__OUT__MESSAGE
        if ("1".equals(strIS_OUT)) {
          pst.registerOutParameter(i + 1, java.sql.Types.VARCHAR);
          iHasOut = i + 1;
        }

        //��ֵ����
        String strIS_NULL = td.getFieldValue("IS_NULL");
        String strValue = null;
        if (hmParmValue != null) {
          if (hmParmValue.containsKey(strPARM_NAME))
            strValue = (String) hmParmValue.get(strPARM_NAME);
          if (strValue == null)
            strValue = "";
        }

        if ("0".equals(strIS_NULL) && (strValue == null || strValue.length() == 0)) {
          throw new BusinessException("�洢���̲�������¼����ֵ,�洢���̲�������=" + strPARM_NAME, null);
        }

        //���ò���
        setParameter(pst, i + 1, strDATA_TYPE, true, strValue);
      }
      
      if(ret){
        pst.registerOutParameter(deltaParm.size() + 1, oracle.jdbc.OracleTypes.CURSOR);
      }
      
      pst.execute();

      //��÷��ؽ��
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
      throw new BusinessException("ִ�д洢����" + strSPName + "����:" + ex.toString(), null);
    }catch (Exception ex) {
      //ex.printStackTrace();
      throw new BusinessException("ִ�д洢����" + strSPName + "����:" + ex.toString(), null);
    }
    finally {
      DBHelper.closeConnection(null, pst, rs);
    }
  }

  /**
   * ִ�д洢����
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

      //׼��SQL
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

      //���ò���ֵ
      for (int i = 0; i < deltaParm.size(); i++) {
        TableData td = (TableData) deltaParm.get(i);
        String strPARM_NAME = td.getFieldValue("PARM_NAME");
        String strDATA_TYPE = td.getFieldValue("DATA_TYPE");
        String strIS_OUT = td.getFieldValue("IS_OUT");

        //�������SYS__OUT__MESSAGE
        if ("1".equals(strIS_OUT)) {
          pst.registerOutParameter(i + 1, java.sql.Types.VARCHAR);
          iHasOut = i + 1;
        }

        //��ֵ����
        String strIS_NULL = td.getFieldValue("IS_NULL");
        String strValue = null;
        if (hmParmValue != null) {
          if (hmParmValue.containsKey(strPARM_NAME))
            strValue = (String) hmParmValue.get(strPARM_NAME);
          if (strValue == null)
            strValue = "";
        }

        if ("0".equals(strIS_NULL) && (strValue == null || strValue.length() == 0)) {
          throw new BusinessException("�洢���̲�������¼����ֵ,�洢���̲�������=" + strPARM_NAME, null);
        }
        //���ò���
        setParameter(pst, i + 1, strDATA_TYPE, true, strValue);
      }
      pst.execute();
      String result="";
      //��÷��ؽ��
      if (iHasOut > 0) {
        String strOut = pst.getString(iHasOut);
        if (strOut != null && strOut.length() > 0) {
          result=strOut;
        }
      }
      return result;
    }catch (SQLException ex) {
      //ex.printStackTrace();
      throw new BusinessException("ִ�д洢����" + strSPName + "����:" + ex.toString(), null);
    }catch (Exception ex) {
      //ex.printStackTrace();
      throw new BusinessException("ִ�д洢����" + strSPName + "����:" + ex.toString(), null);
    }
    finally {
      DBHelper.closeConnection(conn, pst, rs);
    }
  }
  
  /**
   *����PreparedStatement����Ĳ���
   * PreparedStatement pst  ���ݿ�PreparedStatement����
   * int ii �������
   * String intFieldType ��������
   * boolean bGetData �Ƿ��ѯʵ������
   * String strData ����
   * @throws Exception ��׼�쳣
   */
  private void setParameter(CallableStatement pst, int ii, String intFieldType,
    boolean bGetData, String strData) throws Exception {
    //�ַ�
    if (intFieldType.equals(BASE__FIELD_DATA_TYPE__CHAR)) {
      if (false == bGetData && (strData == null || strData.trim().length() == 0)) {
        pst.setString(ii, " ");
      } else {
        pst.setString(ii, strData);
      }
      return;
    }

    //С��
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

    //����
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

    //����
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

      //ת��
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
    case java.sql.Types.BIGINT: //��������
    case java.sql.Types.INTEGER:
    case java.sql.Types.SMALLINT:
    case java.sql.Types.TINYINT:
      return BASE__FIELD_DATA_TYPE__INTEGER;

    case java.sql.Types.DATE: //��������
    case java.sql.Types.TIME:
    case java.sql.Types.TIMESTAMP:
      return BASE__FIELD_DATA_TYPE__DATE;

    case java.sql.Types.DECIMAL: //С������
    case java.sql.Types.DOUBLE:
    case java.sql.Types.FLOAT:
    case java.sql.Types.NUMERIC:
    case java.sql.Types.REAL:
      return BASE__FIELD_DATA_TYPE__DECIMAL;

    default: //�ַ�����
      return BASE__FIELD_DATA_TYPE__CHAR;
    }
  }
}
