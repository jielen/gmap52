package com.anyi.gp.pub;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.anyi.gp.context.ApplusContext;
import com.anyi.gp.core.dao.BaseDao;

/**
 * <p>
 * ������Դ��
 * </p>
 * <p>
 * ������Դ���¼�������ַ����ڲ�ͬ���ֵķ��롣 ���������͵���Դ��һ���Ǽ���Դ���硰��ⵥ���ȣ���һ����ģʽ��Դ��
 * �硰��û�����{0}Ԫ������Ȩ�������������ģʽ��Ϣ�μ�{@link java.text.MessageFormat MessageFormat}
 * </p>
 * 
 */
public class LangResource {

  /**
   * ������Ӧ��Դ�������ض������еķ����ַ���
   * 
   * @param lang
   *          ���ִ��룬����Ϊ"C"��Ӣ��Ϊ"E"
   * @param sourceStr
   *          ��Դ����
   * @return ����֮�����Դ�ַ���
   * @throws IllegalStateException
   *           ������Դ����û����Ӧ����Դ
   * @deprecated 
   */
  public final String getLang(String lang, String sourceStr) {
    if (lang == null)
      lang = "C";
    
    if (sourceStr == null)
      throw new IllegalStateException(
          "LangResource���getLang�����У���ԴID����sourceStrΪ��");
    
    String result = (String) resource.get((lang + sourceStr).toUpperCase());
    if (result == null) {
      return sourceStr;
    }
    return result;
  }

  public final String getLang(String source){
    if (source == null)
      throw new IllegalStateException(
          "LangResource���getLang�����У���ԴID����sourceStrΪ��");
    
    String result = (String) resource.get(("C" + source).toUpperCase());
    if (result == null) {
      return source;
    }
    return result;
  }
  
  /**
   * ��ö�������Դ������� 
   * @return ������Դ��
   */
  public static LangResource getInstance() {
    if (instance == null) {
        instance = new LangResource();        
    }
    return instance;
  }
  
  /**
   * �����ݿ���ļ���ȡ��������Դ������������Դ��
   */
  protected LangResource() {
    BaseDao dao = (BaseDao)ApplusContext.getBean("baseDao");
    resource = new HashMap();
    try {
      List resultList = dao.queryForList(GET_TRANS_TABLE);
      Map itemMap = null;
      for(int i=0;i < resultList.size();i++) {
        itemMap = (Map)resultList.get(i);
        String resName = (String) itemMap.get("RES_NA");
        String resId = (String) itemMap.get("RES_ID");
        resource.put(((String)itemMap.get("LANG_ID") + resId).toUpperCase(), resName);
      }
    } catch (SQLException ex) {
      throw new RuntimeException("������-��Դ��ʱ��SQL���ִ�д���" + ex.toString());
    } 
  }

  public void changeTrans(String resId, String resName) throws SQLException {	
    if ((resId == null) || (resId.length() == 0)) {
	      throw new IllegalStateException("LangResource���changeTrans�����У���ԴID����resIdΪ��");
    }
    
    Map sqlMap = new HashMap();
    sqlMap.put("resId", resId);
    sqlMap.put("langId","C");
    sqlMap.put("resName", resName);

    if (resource.containsKey("C" + resId))
      update(sqlMap);
    else
      insert(sqlMap);
  }

  private void insert(Map sqlMap) throws SQLException {
    BaseDao dao = (BaseDao)ApplusContext.getBean("baseDao");
    dao.insert(INSERT_TRANS, sqlMap);
    resource.put(("C" + (String)sqlMap.get("resId")).toUpperCase(), (String)sqlMap.get("resName"));
  }

  private void update(Map sqlMap) throws SQLException {
    BaseDao dao = (BaseDao)ApplusContext.getBean("baseDao");
    resource.remove("C" + (String)sqlMap.get("resId"));
    dao.update(UPDATE_TRANS,sqlMap);
    resource.put(("C" + (String)sqlMap.get("resId")).toUpperCase(), (String)sqlMap.get("resName"));
  }


  /**
   * 
   */
  public void clear() {
    resource.clear();
    resource = null;
    instance = null;
  }
  
  public void add(String code, String name){
    resource.put(code, name);
  }

  /**
   * ������Դ�����
   * @uml.property  name="resource"
   * @uml.associationEnd  qualifier="toUpperCase:java.lang.String java.lang.String"
   */
  private Map resource = null;
  
  private static final String GET_TRANS_TABLE = "gmap-common.getTransTab";
  
  private static final String INSERT_TRANS = "gmap-common.insertTrans";
  
  private static final String UPDATE_TRANS = "gmap-common.updateTrans";

  /**
   * ������Դ��ʵ��
   */
  private static LangResource instance = null;
}
