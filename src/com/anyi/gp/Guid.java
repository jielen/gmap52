/** $Id: Guid.java,v 1.1 2008/04/29 06:53:01 liuxiaoyong Exp $ */
package com.anyi.gp;

import java.net.InetAddress;

/**
 * <p>
 * Title: 产生唯一标识
 * </p>
 * <p>
 * Description: 消息部门用户管理接口
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company: 用友政务
 * </p>
 * 
 * @author:majian
 * @version: 1.0
 * @time: 2004/06/11
 */

/**
 * 前4个字节是IP地址，接着8个字节是系统时间，最后4个字节是计数器。 在数据库中存储可以使用 char(32) 类型，保存如下字符串
 * bfa78274000000006661bea400000003，可以由 toString()方法得到。 也可以用getData() 获得
 * byte[16] 进行处理。
 */

public class Guid extends Object {
  static int counter = 0;

  byte[] guts;

  /**
   * 产生一个新的Guid.
   */
  public Guid() {
    guts = nextGuid();
  }

  /**
   * 产生一个唯一标识
   * 
   * @return byte[]
   */
  public synchronized static byte[] nextGuid() {
    try {
      // 前4字节为ip
      byte[] ip = InetAddress.getLocalHost().getAddress();
      counter++;
      byte[] guid = new byte[16];
      for (int i = 0; i < 4; i++) {
        guid[i] = ip[i];
      }
      // 然后8字节为系统时间
      byte[] timeAry = Guid.long2bytes(System.currentTimeMillis());
      for (int i = 4; i < 12; i++) {
        guid[i] = timeAry[i - 4];
      }
      // 然后4字节为计数器
      byte[] counterAry = int2bytes(counter);
      for (int i = 12; i < 16; i++) {
        guid[i] = counterAry[i - 12];
      }

      return guid;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * 产生唯一标识的大写
   * 
   * @return the string
   */
  public String toString() {
    StringBuffer sb = toStringBuffer(); // 32位格式
    return (new String(sb)).toUpperCase();
  }

  /**
   * 将唯一标识转成大写
   * 
   * @return the string，长度32，中间没有分隔符
   */
  private StringBuffer toStringBuffer() {
    StringBuffer str = new StringBuffer();
    String s;
    int ii;

    byte ip[] = new byte[4];
    for (int i = 0; i < 4; i++) {
      ip[i] = guts[i];
    }
    s = Integer.toHexString(bytes2int(ip));
    ii = 8 - s.length();
    for (int i = 0; i < ii; i++) {
      s = "0" + s;
    }
    str.append(s);

    byte time[] = new byte[8];
    for (int i = 4; i < 12; i++) {
      time[i - 4] = guts[i];
    }
    s = Long.toHexString(bytes2long(time));
    ii = 16 - s.length();
    for (int i = 0; i < ii; i++) {
      s = "0" + s;
    }
    str.append(s);

    byte count[] = new byte[4];
    for (int i = 12; i < 16; i++) {
      count[i - 12] = guts[i];
    }
    s = Integer.toHexString(bytes2int(count));
    ii = 8 - s.length();
    for (int i = 0; i < ii; i++) {
      s = "0" + s;
    }
    str.append(s);

    return str;
  }

  /**
   * 获取Guid内容
   * 
   * @return byte[]
   */
  public byte[] getData() {
    return guts;
  }

  private synchronized static byte[] long2bytes(long lParam) {
    byte[] byteAry = new byte[8];
    for (int i = 0; i < 8; i++) {
      byteAry[i] = (byte) (lParam >> ((7 - i) * 8));
    }
    return byteAry;
  }

  private synchronized static byte[] int2bytes(int iParam) {
    byte[] byteAry = new byte[4];
    for (int i = 0; i < 4; i++) {
      byteAry[i] = (byte) (iParam >> ((3 - i) * 8));
    }
    return byteAry;
  }

  private synchronized static long bytes2long(byte[] byteAry) {
    if (byteAry == null || byteAry.length != 8) {
      return 0;
    }
    long l = 0;
    for (int i = 0; i < byteAry.length; i++) {
      l += byteAry[i] << ((7 - i) * 8);
    }
    return l;
  }

  private synchronized static int bytes2int(byte[] byteAry) {
    if (byteAry == null || byteAry.length != 4) {
      return 0;
    }
    int ii = 0;
    for (int i = 0; i < byteAry.length; i++) {
      ii += byteAry[i] << ((3 - i) * 8);
    }
    return ii;

  }

  /* test code */
  public static void main(String args[]) {
  }
} // /:~
