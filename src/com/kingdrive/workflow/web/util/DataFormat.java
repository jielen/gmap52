package com.kingdrive.workflow.web.util;

public class DataFormat {

  public DataFormat() {
  }

  public static String getIsActive(String value) {
    if (value.equals("0"))
      return "����";
    else if (value.equals("1"))
      return "������";
    else
      return "���ô���";
  }

  public static String getNodeType(String type) {
    if (type.equals("0"))
      return "��ʼ�ڵ�";
    if (type.equals("1"))
      return "�����ڵ�";
    if (type.equals("2"))
      return "����ڵ�";
    if (type.equals("3"))
      return "��֧�ڵ�";
    if (type.equals("4"))
      return "�¼������ڵ�";
    if (type.equals("5"))
      return "��ڵ�";
    if (type.equals("6"))
      return "��ڵ�";
    return "δ֪�ڵ�����";
  }

  public static String getLinkExecutorRelation(String relation) {
    if (relation.equals("0"))
      return "�޹�";
    if (relation.equals("1"))
      return "ֱ���ϼ�";
    if (relation.equals("2"))
      return "�Լ�";
    return "δ֪����ִ���߹�ϵ";
  }

  public static String getExecutorsMethod(String method) {
    if (method.equals("0"))
      return "��ǩ";
    if (method.equals("1"))
      return "��ǩ";
    if (method.equals("2"))
      return "˳��ǩ";
    return "δ֪����ִ�з�ʽ";
  }

  public static String getLinkNumberOrPercent(String flag) {
    if (flag.equals("0"))
      return "����";
    if (flag.equals("1"))
      return "�ٷֱ�";
    return "δ֪����";
  }

  public static String getLinkType(String flag) {
    if (flag.equals("0"))
      return "��ͨ";
    if (flag.equals("1"))
      return "����";
    return "δ֪����";
  }

  public static String getVariableType(String type) {
    if (type.equals("0"))
      return "������";
    if (type.equals("1"))
      return "�ַ���";
    return "δ֪��������";
  }

  public static String getTaskIdentity(int flag) {
    if (flag == 0)
      return "�������������񱻴���";
    else if (flag == -1)
      return "����";
    else if (flag == -2)
      return "�ƽ�";
    else
      return "����";
  }
}
