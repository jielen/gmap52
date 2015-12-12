package com.anyi.gp.print.service;

import com.anyi.gp.Delta;

/**
 * 
 * 对打印数据进行二次处理的接口
 * 利用spring的aop机制来进行方法的拦截调用
 */
public interface IPrintModelService {

  /**
   * 传入sqlid和keyCondition，业务系统可以根据此进行二次处理
   * keyCondition的处理可以参考和调用DataGenerator.processKeyCondition方法
   * @param sqlid
   * @param keyCondition
   * @return
   */
  public Delta processPrintModel(String sqlid, String keyCondition);
  
  /**
   * 传入printData，业务系统可以对其进行二次处理
   * @param printData
   * @return
   */
  public Delta processPrintModel(Delta printData);
  
}
