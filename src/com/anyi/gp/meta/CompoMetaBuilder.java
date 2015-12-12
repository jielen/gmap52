package com.anyi.gp.meta;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompoMetaBuilder extends AbstractMetaBuilder{
	
  private static Map compoPool = new HashMap();

	/**
	 * 创建CompoMeta对象
	 * @param id
	 * @param parameterObject
	 * @return
	 */
	public synchronized Object generateMeta(String id, Object parameterObject){
		CompoMeta compo = (CompoMeta)compoPool.get(parameterObject);
		if(compo != null)
			return compo;
		
		try {
			compo = (CompoMeta)dao.queryForObject(id, parameterObject);
      if(compo != null){
        compo.setAutoNumFields(generateAutoNumFields(MetaSqlIdConst.asNoRuleSQLID, compo.getName()));
        compo.setCalls(generateCalls(MetaSqlIdConst.asCompoFuncSQLID, compo.getName()));
        compoPool.put(parameterObject, compo);
      }			
		} catch (SQLException e) {
			// TCJLODO Auto-generated catch block
			e.printStackTrace();
		}
    
		return compo;
	}
	
	/**
	 * 获取compo的自动编号字段
	 * @param id
	 * @param parameterObject
	 * @return
	 */
	private List generateAutoNumFields(String id, Object parameterObject){
		List result = new ArrayList();
		try {
			List list = dao.queryForList(id, parameterObject);
      if(list == null)
        return result;
      
      for(int i = 0; i < list.size(); i++){
        Map map = (Map)list.get(i);
        result.add(map.get("NO_FIELD"));
      }
      
		} catch (SQLException e) {
			// TCJLODO Auto-generated catch block
			e.printStackTrace();
		}
		  
    return result;
//		StringBuffer sb = new StringBuffer();
//		for(int i = 0; i < list.size(); i++){
//      Map map = (Map)list.get(i);
//			sb.append(map.get("NO_FIELD") + ",");
//		}
//		if(sb.length() == 0) 
//      return "";
//		return sb.substring(0, sb.length() - 1);
	}
	
	/**
	 * 创建部件的功能按钮
	 * @param id
	 * @param parameterObject
	 * @return
   * TODO 增加父部件的功能
	 */
	private List generateCalls(String id, Object parameterObject){
		List list = null;
		try {
			list = dao.queryForList(id, parameterObject);
		} catch (SQLException e) {
			// TCJLODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
  
  public static void clearMetaPool(){
    compoPool.clear();
  }
}
