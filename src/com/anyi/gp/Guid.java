/** $Id: Guid.java,v 1.1 2008/04/29 06:53:01 liuxiaoyong Exp $ */
package com.anyi.gp;

import java.net.InetAddress;

/**
 * <p>
 * Title: ����Ψһ��ʶ
 * </p>
 * <p>
 * Description: ��Ϣ�����û�����ӿ�
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company: ��������
 * </p>
 * 
 * @author:majian
 * @version: 1.0
 * @time: 2004/06/11
 */

/**
 * ǰ4���ֽ���IP��ַ������8���ֽ���ϵͳʱ�䣬���4���ֽ��Ǽ������� �����ݿ��д洢����ʹ�� char(32) ���ͣ����������ַ���
 * bfa78274000000006661bea400000003�������� toString()�����õ��� Ҳ������getData() ���
 * byte[16] ���д���
 */

public class Guid extends Object {
  static int counter = 0;

  byte[] guts;

  /**
   * ����һ���µ�Guid.
   */
  public Guid() {
    guts = nextGuid();
  }

  /**
   * ����һ��Ψһ��ʶ
   * 
   * @return byte[]
   */
  public synchronized static byte[] nextGuid() {
    try {
      // ǰ4�ֽ�Ϊip
      byte[] ip = InetAddress.getLocalHost().getAddress();
      counter++;
      byte[] guid = new byte[16];
      for (int i = 0; i < 4; i++) {
        guid[i] = ip[i];
      }
      // Ȼ��8�ֽ�Ϊϵͳʱ��
      byte[] timeAry = Guid.long2bytes(System.currentTimeMillis());
      for (int i = 4; i < 12; i++) {
        guid[i] = timeAry[i - 4];
      }
      // Ȼ��4�ֽ�Ϊ������
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
   * ����Ψһ��ʶ�Ĵ�д
   * 
   * @return the string
   */
  public String toString() {
    StringBuffer sb = toStringBuffer(); // 32λ��ʽ
    return (new String(sb)).toUpperCase();
  }

  /**
   * ��Ψһ��ʶת�ɴ�д
   * 
   * @return the string������32���м�û�зָ���
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
   * ��ȡGuid����
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
