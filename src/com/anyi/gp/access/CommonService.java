package com.anyi.gp.access;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.anyi.gp.BusinessException;
import com.anyi.gp.Datum;
import com.anyi.gp.Delta;
import com.anyi.gp.Guid;
import com.anyi.gp.Pub;
import com.anyi.gp.TableData;
import com.anyi.gp.core.bean.SearchSchema;
import com.anyi.gp.core.dao.BaseDao;
import com.anyi.gp.meta.Call;
import com.anyi.gp.meta.MetaManager;
import com.anyi.gp.meta.TableMeta;
import com.anyi.gp.pub.CommonSqlIdConst;
import com.anyi.gp.pub.DataTools;
import com.anyi.gp.pub.LangResource;
import com.anyi.gp.util.XMLTools;

public class CommonService {
  
  private static final Logger log = Logger.getLogger(CommonService.class);

  public static final String GET_SCHEMA_SQL_ID = "gmap-common.getSearchSchemaSataus";

  public static final String INSERT_SCHEMA_SQL_ID = "gmap-common.insertSearchSchema";
  
  public static final String DELETE_SCHEMA_SQL_ID = "gmap-common.deleteSearchSchema";
  
  public static final String SELECT_OPT_SQL_ID = "gmap-common.select_opt_val";

  public static final String UPDATE_OPT_SQL_ID = "gmap-common.update_opt_val";

  public static final String INSERT_OPT_SQL_ID = "gmap-common.insert_opt_option";
  
  public static final String SELECT_PSD_SQL_ID = "gmap-common.getPsdModiTime";
  
  public static final String UPDATA_PSD_SQL_ID = "gmap-common.updatePsdModiTime";

  public static final String UPDATA_LOGOUT = "gmap-common.updateLogout";

  public static final String LOG_USER_FUNC = "gmap-common.logUserFunc";
  
  private DBSupport support;
  
  private BaseDao dao;
 
  public CommonService(){
  }
  
  public CommonService(BaseDao dao, DBSupport support){
    this.dao = dao;
    this.support = support;
  }
  
  /**
   * ȡ�Ǽǲ��б�����
   * @param compoName
   * @param sqlid
   * @param condition
   * @return
   * @throws BusinessException
   */
  public Datum getEntities(String tableName, Map keys, int fromRow, int toRow)
    throws BusinessException {
    List newParams = new ArrayList();
    String sql = support.wrapSqlByTableName(tableName, keys, newParams);
    
//    Map params = new HashMap();
//    params.put("rownum", toRow + "");
//    params.put("rn", fromRow + "");
//    params.putAll(keys);
    newParams.add(toRow + "");
    newParams.add(fromRow + "");
    sql = support.wrapPaginationSql(sql);
    List data = dao.queryForListBySql(sql, newParams.toArray());
    
    Datum datum = new Datum();
    datum.setName(tableName);
    datum.setData(data);
    return datum;
  }
  
  public Datum getEntities(String tableName, Map keys)
    throws BusinessException {
    List newParams = new ArrayList();
    String sql = support.wrapSqlByTableName(tableName, keys, newParams);
    
    List data = dao.queryForListBySql(sql, newParams.toArray());
  
    Datum datum = new Datum();
    datum.setName(tableName);
    datum.setData(data);
    return datum;
}  
  /**
   * ȡ��������������
   * @param compoName
   * @param sqlid
   * @param condition
   * @param childSqlMaps
   * @return
   * @throws BusinessException
   */
  public Datum getEntity(String tableName, Map keys, boolean containChildTable)
    throws BusinessException{   
    Datum datum = getEntity(tableName, keys);
    
    if(!containChildTable){
      return datum;
    }else{
      TableMeta tableMeta = MetaManager.getTableMeta(tableName);
      List childTableNames = tableMeta.getChildTableNames();
      if(childTableNames.isEmpty()){
        return datum;
      }
      else{        
        for(int i = 0; i < childTableNames.size(); i++){
          datum.addChildDatum(getEntity(childTableNames.get(i).toString(), keys, containChildTable));
        }
      }
    }
    
    return datum;
  }
   
  /**
   * ����tablename��keys��ȡ������
   * @param tableName
   * @param keys
   * @return
   */
  public Datum getEntity(String tableName, Map keys){
    List newParams = new ArrayList();
    String sql = support.wrapSqlByTableName(tableName, keys, newParams);
    List data = dao.queryForListBySql(sql, newParams.toArray());
    Datum datum = new Datum();
    datum.setData(data);
    datum.setName(tableName);
    return datum;
  }

  /**
   * ���䷽������
   * @param className
   * @param callName
   * @param parameterTypes
   * @param parameterValues
   * @return
   * @throws BusinessException
   */
  public Object invokeMethod(String className, String callName
    , Class[] parameterTypes, Object[] parameterValues)throws BusinessException{
    Object result = null;
    Class objClass = null;
    Object object = null;
    Method call = null;

    try {
      objClass = Class.forName(className);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException("Error_060921: ת������ʱ����" + "û���ҵ� Java ��: "
        + className + "\n");
    }
    try {
      object = objClass.newInstance();
    } catch (InstantiationException e) {
      log.error(e);
      throw new RuntimeException("Error_060922: ת������ʱ����" + "�޷�ʵ���� Java ��: "
        + className + "\n");
    } catch (IllegalAccessException e) {
      log.error(e);
      throw new RuntimeException("Error_060923: ת������ʱ����" + "û��Ȩ��ʵ���� Java ��: "
        + className + "\n");
    }

    try {
      call = objClass.getDeclaredMethod(callName, parameterTypes);
    } catch (NoSuchMethodException e) {
      log.error(e);
      throw new RuntimeException("Error_060924: ת������ʱ����" + "�Ҳ��� Java ��: "
        + className + " �� " + callName + " ����" + "\n");
    } catch (SecurityException e) {
      log.error(e);
      throw new RuntimeException("Error_060925: ת������ʱ����" + "û��Ȩ��ִ�� Java ��: "
        + className + " �� " + callName + " ����" + "\n");
    }

    try {
      result = call.invoke(object, parameterValues);
    } catch (IllegalAccessException e) {
      log.error(e);
      throw new RuntimeException(e);
    } catch (IllegalArgumentException e) {
      log.error(e);
      throw new RuntimeException(e);
    } catch (InvocationTargetException e) {
      log.error(e);
      if (e.getTargetException() instanceof BusinessException) {
        throw (BusinessException) e.getTargetException();
      } else if (e.getTargetException() instanceof RuntimeException) {
        throw (RuntimeException) e.getTargetException();
      } else {
        throw new RuntimeException(e.getTargetException());
      }
    }
    return result;
  }
  
  /**
   * ����ȡ�������ȡ����
   * @param ruleID
   * @param param
   * @return 
   * @throws BusinessException
   * TODO �����ֶεĲ�ͬ���Ͷ����ݽ��и�ʽ��
   */
  public Delta getDBData(String ruleID, TableData entity) throws BusinessException{
    Map params = DataTools.TableData2Map(entity);
    
    List newParams = new ArrayList();
    String sql = null;
    try{
      sql = dao.getSql(ruleID, params, newParams);
    }catch(Exception e){
      sql = null;// sqlmap��û������
    }
    
    if(sql == null){
      sql = ruleID;
    }
    
    if(sql != null && sql.indexOf("{") >= 0){//ִ�д洢����,ibatis
      return getDBDataById(ruleID, params);
    }
    return getDBDataBySql(sql, newParams);//ibatis���ص�hashmap�������˳���䣬�ʲ���spring��jdbcTemplate����
    
  }

  /**
   * ����id�Ͳ�����ȡ����
   * @param ruleID
   * @param params
   * @return
   * @throws BusinessException
   */
  public Delta getDBDataById(String ruleID, Map params) throws BusinessException{
    Delta delta = new Delta();
    try {
    	List result = null;
    	if(params == null){
    		result = dao.queryForList(ruleID);
    	}else{
    		//result = dao.queryForList(ruleID, params);
    		List paramList = new ArrayList();
    		String sql = dao.getSql(ruleID, params, paramList);
        if(sql != null && sql.indexOf("{") >= 0){//ִ�д洢����,ibatis
          result = dao.queryForList(ruleID, params);
        }
        else{
          result = dao.queryForListBySql(sql, paramList.toArray());
        }
    	}
    	for(int i = 0; i < result.size(); i++){
	        Map map = (Map)result.get(i);
	        delta.add(DataTools.Map2TableData(map));
    	}
    } catch (SQLException e) {
      log.info(e);
      throw new BusinessException(e);
    }
    
    return delta;    
  }
  
  /**
   * ����sql���Ͳ�������sqlִ��
   * @param sql
   * @param params
   * @return
   * @throws BusinessException
   */
  private Delta getDBDataBySql(String sql, List params) throws BusinessException{
    Delta delta = new Delta();
    sql = sql.trim();
    if (sql.toUpperCase().startsWith("UPDATE")
      || sql.toUpperCase().startsWith("INSERT")
      || sql.toUpperCase().startsWith("DELETE")){
      dao.executeBySql(sql, params.toArray());   
    }
    else{
      List result = dao.queryForListBySql(sql, params.toArray());
      for(int i = 0; i < result.size(); i++){
        Map map = (Map)result.get(i);
        delta.add(DataTools.Map2TableData(map));
      }
    }
    return delta;
  } 
  /**
   * ����ȡ�������ȡ����
   * @param ruleID
   * @param entity
   * @return
   * @throws BusinessException
   * TODO �����ֶεĲ�ͬ���Ͷ����ݽ��и�ʽ��
   */
  public Datum getDatum(String ruleID, TableData entity)throws BusinessException{
    Map params = DataTools.TableData2Map(entity);
    Datum datum = new Datum();
    List result = null;
    try {
      List paramList = new ArrayList();
	  String sql = dao.getSql(ruleID, params, paramList);
      if(sql != null && sql.indexOf("{") >= 0){//ִ�д洢����,ibatis
        result = dao.queryForList(ruleID, params);
      }
      else{
        result = dao.queryForListBySql(sql, paramList.toArray());
      }
      datum.setData(result);
    } catch (SQLException e) {
      log.info(e);
      throw new BusinessException(e);
    }    
    return datum;
  }
 
  /**
   * ɾ���߼���������ķ���
   * @param schema
   * @return
   * @throws BusinessException
   */
  public int deleteSchema(SearchSchema schema) throws BusinessException{
    try {
      return dao.delete(DELETE_SCHEMA_SQL_ID, schema);
    } catch (SQLException e) {
      log.info(e);
      throw new BusinessException(e);
    }
  }
  
  /**
   * ��ȡ��������
   * @param schema
   * @return
   * @throws BusinessException
   */
  public SearchSchema loadSchema(SearchSchema schema) throws BusinessException{
    try {
      return (SearchSchema)dao.queryForObject(GET_SCHEMA_SQL_ID, schema);
    } catch (SQLException e) {
      log.info(e);
      throw new BusinessException(e);
    }    
  }
 
  /**
   * ɾ����������
   * @param schema
   * @throws BusinessException
   */
  public void deletSchema(SearchSchema schema) throws BusinessException{
    try {
      dao.delete(DELETE_SCHEMA_SQL_ID, schema);
    } catch (SQLException e) {
      log.info(e);
      throw new BusinessException(e);
    }    
  }

  /**
   * ����ϵͳѡ��
   * @param optionXml
   * @return
   * @throws BusinessException
   * @throws SQLException
   */
  public String saveOptions(String optionXml) throws BusinessException, SQLException{
   
    String msg = "";
    Document voDoc = XMLTools.stringToDocument(optionXml);
    if (voDoc == null) {
      return Pub.makeRetString(false, "���� Document ʧ�ܣ�");
    }
    
    List voRowList = XMLTools.getValidChildNodeList(voDoc.getDocumentElement());
    for (Iterator iter = voRowList.iterator(); iter.hasNext();) {
      Node voRow = (Node) iter.next();
      String value = XMLTools.getNodeAttr(voRow, "OPT_VAL", null);
      if (value == null)
        continue;

      String isSys = XMLTools.getNodeAttr(voRow, "IS_SYST_OPT",null);

      String optId = XMLTools.getNodeAttr(voRow, "OPT_ID", null);
      if (optId == null)
        continue;
      String coCode = XMLTools.getNodeAttr(voRow, "CO_CODE", null);
      if (coCode == null)
        continue;
      String compoId = XMLTools.getNodeAttr(voRow, "COMPO_ID", null);
      if (compoId == null)
        continue;
      String transType = XMLTools.getNodeAttr(voRow, "TRANS_TYPE", null);
      if (transType == null)
        continue;

      Map paramMap = new HashMap();
      paramMap.put("value", value);
      paramMap.put("isSys", isSys);
      paramMap.put("optId", optId);
      paramMap.put("coCode", coCode);
      paramMap.put("compoId", compoId);
      paramMap.put("transType", transType);
      
      List resultGet = (List)dao.queryForList(SELECT_OPT_SQL_ID, paramMap);
      //���optId�����ڣ�������
      if (resultGet.isEmpty()) {     
        dao.insert(INSERT_OPT_SQL_ID, paramMap);
        msg = "����ɹ�!";
      } else {
//        Map data = (Map)resultGet.get(0);
//        String isSystem = (String)data.get("IS_SYST_OPT");
//        if (isSystem != null && isSystem.length()!= 0 && isSystem.toLowerCase().equals("y")) {
//          msg = "ϵͳ�ڲ����ò��ɸ���!";
//        } else {
          dao.update(UPDATE_OPT_SQL_ID, paramMap);
          msg = "����ɹ�";
//        }
      }
    }
   return msg;  
  }
  
  /**
   * ������������
   * @param paramMap
   * @throws BusinessException
   */
  public void saveSchema(SearchSchema paramMap) throws BusinessException{    
    try {
      dao.delete(DELETE_SCHEMA_SQL_ID, paramMap);
      dao.insert(INSERT_SCHEMA_SQL_ID, paramMap);
    } catch (SQLException e) {
      log.info(e);
      throw new BusinessException(e);
    }
  }
  
  /**
   * ��ȡ�û��޸�����ʱ��
   * @param params
   * @return
   * @throws BusinessException
   * @throws SQLException
   */
  public Object getPsdModiTime(Map params) throws BusinessException, SQLException{
    return dao.queryForObject(SELECT_PSD_SQL_ID, params);
  }
  
  /**
   * ���������޸�ʱ��
   * @param params
   * @return
   * @throws BusinessException
   * @throws SQLException
   */
  public int updatePsdModiTime(Map params) throws BusinessException, SQLException{
    return dao.update(UPDATA_PSD_SQL_ID, params); 
  }

  /**
   * �˳�
   * @param params
   * @throws BusinessException
   */
  public void logout(Map params)throws BusinessException{
    try{
      dao.delete(CommonSqlIdConst.DELETE_TICKET, params);
      ////dao.update(UPDATA_LOGOUT, params);
    }catch(SQLException e){
      log.info(e);
      throw new BusinessException(e);
    }
  }
  
  /**
   * ɾ��ҵ��ʵ�壺����������ӱ�
   * @param tableName
   * @param keyFields
   */
  public void deleteEntity(String tableName, Map keyFields){
    String sql = null;
    
    TableMeta mainTableMeta = MetaManager.getTableMeta(tableName);//ȡ����������Ϣ
    List keyFieldNames = mainTableMeta.getKeyFieldNames();
     
    List childrenTableNames = mainTableMeta.getAllChildTableNames();
    childrenTableNames.add(tableName);//�����������
    for(int i = 0; i < childrenTableNames.size(); i++){
      String childName = childrenTableNames.get(i).toString();
      List params = new ArrayList();
      sql = " delete from " + childName + " where ";
      for(int m = 0; m < keyFieldNames.size(); m++){
        String st = keyFieldNames.get(m).toString();
        sql +=  st + " = ?,";
        params.add(keyFields.get(st));
      }
      sql = sql.substring(0, sql.length() - 1);
      dao.executeBySql(sql, params.toArray());
    }    
  }
  
  /**
   * �û�������־��¼
   * @param userId
   * @param userName
   * @param compoId
   * @param funcId
   * @param pkDesc
   * @param isSuccess
   * @throws BusinessException
   */
  public void logUserFunc(String userId, String userName, 
    String compoId, String funcId, String pkDesc, boolean isSuccess)
    throws BusinessException{
    if(compoId == null || compoId.length() == 0)
      return;
    
    if(!funcId.startsWith("f")&&funcId.startsWith(compoId))
      funcId="f" + funcId.substring(compoId.length()+1);
    else
      funcId = "f" + funcId;

    if(MetaManager.getCompoMeta(compoId) == null)
      return;
    
    Map calls = MetaManager.getCompoMeta(compoId).getCalls();
    Call call = (Call)calls.get(funcId);
    if(call == null || !call.isWrLog())
      return;
        
    Map params = new HashMap();
    params.put("GUID", new Guid().toString());
    params.put("OPER_TIME", new Timestamp(System.currentTimeMillis()).toString());
    params.put("USER_ID", userId);
    params.put("FUNC_ID", funcId);
    params.put("COMPO_ID", compoId);
    params.put("OPER_DESC", LangResource.getInstance().getLang(funcId));
    params.put("COMPO_NAME", LangResource.getInstance().getLang(compoId));
    params.put("IF_SUC", isSuccess? "Y" : "N");
    params.put("USER_NAME", userName);
    params.put("USER_IP", "");
    params.put("PK_DESC", pkDesc);
    
    try {
      dao.insert(LOG_USER_FUNC, params);
    } catch (SQLException e) {
      log.error(e);
      throw new BusinessException(e);
    }
  }
}





