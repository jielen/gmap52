/*$Id: ITextElementInfor.java,v 1.1 2008/02/22 09:09:31 liuxiaoyong Exp $*/
package com.anyi.gp.print.template;

/**
 * <p>
 * Title: ITextElementInfor页面元素公共信息接口
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
  int STATICTEXT = 1; // 标题（静态文本）

  int TEXTFIELD = 2; // 数据库字段

  int PARAMETER = 3; // 表头参数

  String getContent();

  int getX();

  int getY();

  int getWidth();

  int getHeight();

}
