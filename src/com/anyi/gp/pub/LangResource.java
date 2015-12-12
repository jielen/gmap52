package com.anyi.gp.pub;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.anyi.gp.context.ApplusContext;
import com.anyi.gp.core.dao.BaseDao;

/**
 * <p>
 * 语种资源库
 * </p>
 * <p>
 * 语种资源库记录了所有字符串在不同语种的翻译。 有两种类型的资源，一种是简单资源，如“入库单”等；另一种是模式资源，
 * 如“你没有审核{0}元订单的权利。”，跟多的模式信息参见{@link java.text.MessageFormat MessageFormat}
 * </p>
 * 
 */
public class LangResource {

  /**
   * 返回相应资源代码在特定语言中的翻译字符串
   * 
   * @param lang
   *          语种代码，中文为"C"，英文为"E"
   * @param sourceStr
   *          资源代码
   * @return 翻译之后的资源字符串
   * @throws IllegalStateException
   *           语种资源库中没有相应的资源
   * @deprecated 
   */
  public final String getLang(String lang, String sourceStr) {
    if (lang == null)
      lang = "C";
    
    if (sourceStr == null)
      throw new IllegalStateException(
          "LangResource类的getLang方法中，资源ID参数sourceStr为空");
    
    String result = (String) resource.get((lang + sourceStr).toUpperCase());
    if (result == null) {
      return sourceStr;
    }
    return result;
  }

  public final String getLang(String source){
    if (source == null)
      throw new IllegalStateException(
          "LangResource类的getLang方法中，资源ID参数sourceStr为空");
    
    String result = (String) resource.get(("C" + source).toUpperCase());
    if (result == null) {
      return source;
    }
    return result;
  }
  
  /**
   * 获得对语种资源库的引用 
   * @return 语种资源库
   */
  public static LangResource getInstance() {
    if (instance == null) {
        instance = new LangResource();        
    }
    return instance;
  }
  
  /**
   * 从数据库或文件中取出语种资源，构造语种资源库
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
      throw new RuntimeException("读语言-资源表时，SQL语句执行错误！" + ex.toString());
    } 
  }

  public void changeTrans(String resId, String resName) throws SQLException {	
    if ((resId == null) || (resId.length() == 0)) {
	      throw new IllegalStateException("LangResource类的changeTrans方法中，资源ID参数resId为空");
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
   * 语种资源缓冲池
   * @uml.property  name="resource"
   * @uml.associationEnd  qualifier="toUpperCase:java.lang.String java.lang.String"
   */
  private Map resource = null;
  
  private static final String GET_TRANS_TABLE = "gmap-common.getTransTab";
  
  private static final String INSERT_TRANS = "gmap-common.insertTrans";
  
  private static final String UPDATE_TRANS = "gmap-common.updateTrans";

  /**
   * 语种资源库实例
   */
  private static LangResource instance = null;
}
