//* $Id: Debug.java,v 1.1 2008/02/22 08:32:52 liubo Exp $ */
package com.anyi.gp.debug;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 用于断言、调试和测试的工具集 【实现说明】 实现时注意这个类应该只依赖于标准 java 类，不依赖任何其它平台类。 这样便于进行移植。
 */
public class Debug {

  /**
   * 检查参数的值为真
   * 
   * @param b
   *          要检查的值
   * @throws AssertionException
   */
  public static void assertTrue(boolean b) throws AssertionException {
    if (!b) {
      throw new AssertionException("assert 检查发现异常!");
    }
  }

  /**
   * 检查参数的值为真
   * 
   * @param b
   *          要检查的值
   * @param message
   *          检查失败时的消息
   * @param argument
   *          消息的参数，如果不为 null，将使用 java.text.MessageFormat 格式化消息。如果是只有一个参数，用
   *          Object 类型，如果有多个参数，传入 Object[] 类型。
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
      throw new AssertionException("assert 检查发现异常 :" + message);
    }
  }

  private static int MAXSIZE = 1000;

  private static boolean DEBUG_MODE = false;

  private static List logs;

  private static Object objectDebugged;

  /**
   * 设置要调试的对象，一次只允许调试一个对象
   * 
   * @param obj
   *          要调试的对象
   * @param debugMode
   *          是否进行调试
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
   * 记录调试信息
   * 
   * @param src
   *          发出信息的对象
   * @param message
   *          调试信息
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
   * 返回特定对象的调试信息
   * 
   * @param obj
   *          要调试的对象
   * @return 返回 \n 分隔的字符串，如果没有调试该对象，返回空字符串 ""
   */
  public static String getLog(Object obj) {
    if (objectDebugged != obj) {
      return "";
    }
    return toString(logs);
  }

  /** 返回 \n 分隔的字符串 */
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
