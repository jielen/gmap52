package com.kingdrive.workflow.web.util;

public class DataFormat {

  public DataFormat() {
  }

  public static String getIsActive(String value) {
    if (value.equals("0"))
      return "可用";
    else if (value.equals("1"))
      return "不可用";
    else
      return "配置错误";
  }

  public static String getNodeType(String type) {
    if (type.equals("0"))
      return "开始节点";
    if (type.equals("1"))
      return "结束节点";
    if (type.equals("2"))
      return "任务节点";
    if (type.equals("3"))
      return "分支节点";
    if (type.equals("4"))
      return "事件触发节点";
    if (type.equals("5"))
      return "与节点";
    if (type.equals("6"))
      return "或节点";
    return "未知节点类型";
  }

  public static String getLinkExecutorRelation(String relation) {
    if (relation.equals("0"))
      return "无关";
    if (relation.equals("1"))
      return "直接上级";
    if (relation.equals("2"))
      return "自己";
    return "未知流向执行者关系";
  }

  public static String getExecutorsMethod(String method) {
    if (method.equals("0"))
      return "独签";
    if (method.equals("1"))
      return "并签";
    if (method.equals("2"))
      return "顺序签";
    return "未知流向执行方式";
  }

  public static String getLinkNumberOrPercent(String flag) {
    if (flag.equals("0"))
      return "数字";
    if (flag.equals("1"))
      return "百分比";
    return "未知类型";
  }

  public static String getLinkType(String flag) {
    if (flag.equals("0"))
      return "普通";
    if (flag.equals("1"))
      return "回退";
    return "未知类型";
  }

  public static String getVariableType(String type) {
    if (type.equals("0"))
      return "数字型";
    if (type.equals("1"))
      return "字符型";
    return "未知变量类型";
  }

  public static String getTaskIdentity(int flag) {
    if (flag == 0)
      return "正常，但该任务被代理";
    else if (flag == -1)
      return "正常";
    else if (flag == -2)
      return "移交";
    else
      return "代理";
  }
}
