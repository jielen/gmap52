/*$Id: ITextElementInfor.java,v 1.1 2008/02/22 09:09:31 liuxiaoyong Exp $*/
package com.anyi.gp.print.template;

/**
 * <p>
 * Title: ITextElementInforҳ��Ԫ�ع�����Ϣ�ӿ�
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002
 * </p>
 * <p>
 * Company:
 * </p>
 *
 * @author leejianwei
 * @version 1.0
 */

public interface ITextElementInfor {
  int STATICTEXT = 1; // ���⣨��̬�ı���

  int TEXTFIELD = 2; // ���ݿ��ֶ�

  int PARAMETER = 3; // ��ͷ����

  String getContent();

  int getX();

  int getY();

  int getWidth();

  int getHeight();

}
