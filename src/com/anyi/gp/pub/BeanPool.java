package com.anyi.gp.pub;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.anyi.gp.bean.FuncBean;
import com.anyi.gp.bean.OptionBean;
import com.anyi.gp.bean.ValSetBean;
import com.anyi.gp.core.dao.BaseDao;

/**
 * 
 * 管理平台中的常用bean
 * 缓冲池仅仅缓存系统预置的数据，用户定义的数据是可变的不缓存
 * 
 */
public class BeanPool {
  
  private static final Logger logger = Logger.getLogger(BeanPool.class);

  Map options = new HashMap();
  
  Map valsets = new HashMap();
  
  Map functions = new HashMap();
  
  public BeanPool(BaseDao dao) {
    initSysOptions(dao);
    //initSysValsets(dao);
    //initFunctions(dao);
  }
  
  /**
   * 初始化options
   * @param dao
   */
  private void initSysOptions(BaseDao dao){
    try {
      Map param = new HashMap();
      param.put("IS_SYST_OPT", "Y");
      
      List result = dao.queryForList(CommonSqlIdConst.GET_AS_OPTION, param);
      for(int i = 0; i < result.size(); i++){
        OptionBean option = (OptionBean)result.get(i);
        options.put(option.getOptId(), option);
      }
    } catch (SQLException e) {
      logger.error("initOptions(BaseDao) -  : BaseDao dao=" + dao);
      throw new RuntimeException(e);
    }
  }
  
  /**
   * 初始化valsets
   * @param dao
   */
  private void initSysValsets(BaseDao dao){
    try {
      Map param = new HashMap();
      param.put("IS_SYSTEM", "Y");
      
      List result = dao.queryForList(CommonSqlIdConst.GET_AS_VALSET, param);
      for(int i = 0; i < result.size(); i++){
        ValSetBean valset = (ValSetBean)result.get(i);
        initSysVal(dao, valset);
        valsets.put(valset.getValSetId(), valset);//TODO 是否进行ibatis延迟加载配置?
      }
    } catch (SQLException e) {
      logger.error("initValsets(BaseDao) -  : BaseDao dao=" + dao);
      throw new RuntimeException(e);
    }
  }
  
  private void initSysVal(BaseDao dao, ValSetBean valset){
    try {
      String valSql = valset.getValSql();
      List result = new ArrayList();
      if(valSql != null && valSql.length() > 0){
        ////GeneralFunc.sqlSearchVal(valset.getValSetId(), valSql, result);
      }
      else{
        Map param = new HashMap();
        param.put("VALSET_ID", valset.getValSetId());
        param.put("IS_SYSTEM", "Y");
        result = dao.queryForList(CommonSqlIdConst.GET_AS_VAL, param);
      }
      
      valset.setValBeans(result);
    
    } catch (SQLException e) {
      logger.error("initValsets(BaseDao) -  : BaseDao dao=" + dao);
      throw new RuntimeException(e);
    }    
  }
  
  /**
   * 初始化functions
   * @param dao
   */
  private void initFunctions(BaseDao dao){
    try {
      List result = dao.queryForList(CommonSqlIdConst.GET_AS_FUNC);
      for(int i = 0; i < result.size(); i++){
        FuncBean func = (FuncBean)result.get(i);
        functions.put(func.getFuncId(), func);
      }
    } catch (SQLException e) {
      logger.error("initValsets(BaseDao) -  : BaseDao dao=" + dao);
      throw new RuntimeException(e);
    }
  }
  
  public OptionBean getOption(String optionId) {
    OptionBean option = (OptionBean) options.get(optionId);
    if(option == null){
      String optValue = GeneralFunc.getOption(optionId, "*");
      option = new OptionBean();
      option.setOptVal(optValue);
    }
    return option;
  }
  
  public FuncBean getFunction(String funcId){
    FuncBean func = (FuncBean) functions.get(funcId);
    return func;
  }
  
  public ValSetBean getValSet(String ValsetId){
    ValSetBean valset = (ValSetBean) valsets.get(ValsetId);
    return valset;
  }
  
  public void asySysOptions(String optionId, String value) {
	  OptionBean bean = (OptionBean)options.get(optionId);
	  if (bean != null) {
		  bean.setOptVal(value);
	  }
  }
  
}
