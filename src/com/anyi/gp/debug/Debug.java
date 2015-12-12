//* $Id: Debug.java,v 1.1 2008/02/22 08:32:52 liubo Exp $ */
package com.anyi.gp.debug;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * ���ڶ��ԡ����ԺͲ��ԵĹ��߼� ��ʵ��˵���� ʵ��ʱע�������Ӧ��ֻ�����ڱ�׼ java �࣬�������κ�����ƽ̨�ࡣ �������ڽ�����ֲ��
 */
public class Debug {

  /**
   * ��������ֵΪ��
   * 
   * @param b
   *          Ҫ����ֵ
   * @throws AssertionException
   */
  public static void assertTrue(boolean b) throws AssertionException {
    if (!b) {
      throw new AssertionException("assert ��鷢���쳣!");
    }
  }

  /**
   * ��������ֵΪ��
   * 
   * @param b
   *          Ҫ����ֵ
   * @param message
   *          ���ʧ��ʱ����Ϣ
   * @param argument
   *          ��Ϣ�Ĳ����������Ϊ null����ʹ�� java.text.MessageFormat ��ʽ����Ϣ�������ֻ��һ����������
   *          Object ���ͣ�����ж������������ Object[] ���͡�
   * @throws AssertionException
   */
  public static void assertTrue(boolean b, String message, Object argument)
      throws AssertionException {
    if (!b) {
      if (null != argument) {
        try {
          if (argument instanceof Object[]) {
            message = MessageFormat.format(message, (Object[]) argument);
          } else {
            message = MessageFormat.format(message, new Object[] { argument });
          }
        } catch (IllegalArgumentException e) {
        }
      }
      throw new AssertionException("assert ��鷢���쳣 :" + message);
    }
  }

  private static int MAXSIZE = 1000;

  private static boolean DEBUG_MODE = false;

  private static List logs;

  private static Object objectDebugged;

  /**
   * ����Ҫ���ԵĶ���һ��ֻ�������һ������
   * 
   * @param obj
   *          Ҫ���ԵĶ���
   * @param debugMode
   *          �Ƿ���е���
   */
  public static void setDebugMode(Object obj, boolean debugMode) {
    DEBUG_MODE = debugMode;
    if (DEBUG_MODE) {
      logs = new ArrayList();
      objectDebugged = obj;
    } else {
      objectDebugged = null;
    }
  }

  /**
   * ��¼������Ϣ
   * 
   * @param src
   *          ������Ϣ�Ķ���
   * @param message
   *          ������Ϣ
   */
  public static void log(Object obj, String message) {
    if (!DEBUG_MODE || objectDebugged != obj) {
      return;
    }
    if (logs.size() > MAXSIZE) {
      logs = new ArrayList();
    }
    logs.add(message);
  }

  /**
   * �����ض�����ĵ�����Ϣ
   * 
   * @param obj
   *          Ҫ���ԵĶ���
   * @return ���� \n �ָ����ַ��������û�е��Ըö��󣬷��ؿ��ַ��� ""
   */
  public static String getLog(Object obj) {
    if (objectDebugged != obj) {
      return "";
    }
    return toString(logs);
  }

  /** ���� \n �ָ����ַ��� */
  private static String toString(List list) {
    if (null == list) {
      return "";
    }
    StringBuffer s = new StringBuffer();
    for (Iterator i = list.iterator(); i.hasNext();) {
      s.append(i.next()).append("\n");
    }
    return s.toString();
  }
}
