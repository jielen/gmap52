/**
 * @author hmgkevin
 * @date 2008-03-07
 */
package com.anyi.gp.print.service;

import java.util.List;

public interface PrintSetService {
  /**
   * 将打印设置参数保持到数据库中
   * @param setInfo String
   */  
  public void setPrintSetInfo(String setInfo);
  
  /**
   * 从数据库中提取打印设置参数信息
   * @param setInfo
   * @return
   */
  public String getPrintSetInfo(String setInfo);
  
  /**
   * 获取部件代码和部件名称
   * @param condition
   * @param componame
   * @return
   */
  public List getCompoTplInfo(String condition, String componame);
  
  /**
   * 从文件中取得模板的HTML代码
   * @param prn_tpl_code 读取的文件名
   * @return inTextData String 若文件存在，返回模板的HTML代码；若不存在，返回自动布局的标志。
   */
  public String getTextDataFromFile(String prn_tpl_code);
  
  /**
   * 通用往数据库中写入大字段Text的字符串值
   * @param tableName 要写入的大字段表名
   * @param fieldName 要写入的大字段字段名
   * @param condition where查询条件
   * @param textData 要写入的大字段字符串内容
   * @param rowCount 记录行数
   * @return String
   */
  public String setJasperTextData(String tableName, String fieldName, String condition, String textData, String rowCount);
}
