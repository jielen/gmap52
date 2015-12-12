/* $Id: TextElement.java,v 1.1 2008/02/22 09:09:36 liuxiaoyong Exp $*/
package com.anyi.gp.print.template;

/**
 * <p>
 * Title: 实现ITextElementInfor接口
 * </p>
 * <p>
 * Description: 页面元素详细信息描述
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company:
 * </p>
 *
 * @author leejianwei
 * @version 1.0
 */
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author   leidaohong
 */
public class TextElement implements ITextElementInfor {
  private int x;

  private int y;

  private int width;

  private int height;

  private byte align;

  private String name;

  private String content;

  private String classType;

  private boolean print;

  public TextElement() {
  }

  /**
   * 根据XML节点构造一个页面元素信息
   *
   * @param node
   *          Node 从前端脚本传来的XML数据
   */
  public TextElement(Node node) {
    NodeList nodes = node.getChildNodes();
    for (int i = 0, j = nodes.getLength(); i < j; i++) {
      Node ele = nodes.item(i);
      if (ele.getNodeName().equals("x")) {
        setX(Integer.parseInt(ele.getFirstChild().getNodeValue()));
      } else if (ele.getNodeName().equals("y")) {
        setY(Integer.parseInt(ele.getFirstChild().getNodeValue()));
      } else if (ele.getNodeName().equals("width")) {
        setWidth(Integer.parseInt(ele.getFirstChild().getNodeValue()));
      } else if (ele.getNodeName().equals("height")) {
        setHeight(Integer.parseInt(ele.getFirstChild().getNodeValue()));
      } else if (ele.getNodeName().equals("align")) {
        setAlign(ele.getFirstChild().getNodeValue());
      } else if (ele.getNodeName().equals("content")) {
        if (ele.hasChildNodes()) {
          setContent(ele.getFirstChild().getNodeValue());
        } else {
          setContent("");
        }
      } else if (ele.getNodeName().equals("name")) {
        setName(ele.getFirstChild().getNodeValue());
      } else if (ele.getNodeName().equals("classType")) {
        setClassType(ele.getFirstChild().getNodeValue());
      } else if (ele.getNodeName().equals("isPrint")) {
        String value = ele.getFirstChild().getNodeValue();
        if (value.equalsIgnoreCase("y")) {
          setPrint(true);
        }
      }
    }
  }

  /**
 * 取得元素的X坐标
 * @return   int X坐标值
 * @uml.property   name="x"
 */
  public int getX() {
    return x;
  }

  /**
 * 取得元素的align 对齐值 public static final byte TEXT_ALIGN_CENTER 2 public static final byte TEXT_ALIGN_JUSTIFIED 4 public static final byte TEXT_ALIGN_LEFT 1 public static final byte TEXT_ALIGN_RIGHT 3
 * @return   byte 对齐值
 * @uml.property   name="align"
 */
  public byte getAlign() {
    return align;
  }

  /**
 * 设置X坐标值
 * @param x   int 坐标
 * @uml.property   name="x"
 */
  public void setX(int x) {
    this.x = x;
  }

  /**
 * 取得元素Y坐标
 * @return   int
 * @uml.property   name="y"
 */
  public int getY() {
    return y;
  }

  /**
 * 设置元素Y坐标
 * @param y   int Y坐标值
 * @uml.property   name="y"
 */
  public void setY(int y) {
    this.y = y;
  }

  /**
 * 取得元素的宽度
 * @return   int 原素宽
 * @uml.property   name="width"
 */
  public int getWidth() {
    return width;
  }

  /**
 * 设置元素宽度值
 * @param width   int 宽度值
 * @uml.property   name="width"
 */
  public void setWidth(int width) {
    this.width = width;
  }

  /**
 * 取得元素高度值
 * @return   int 高度值
 * @uml.property   name="height"
 */
  public int getHeight() {
    return height;
  }

  /**
 * 设置元素的高度
 * @param height   int 高度值
 * @uml.property   name="height"
 */
  public void setHeight(int height) {
    this.height = height;
  }

  /**
 * 获得元素名
 * @return   String 元素名称
 * @uml.property   name="name"
 */
  public String getName() {
    return name;
  }

  /**
 * 设置元素名称
 * @param name   String 名称
 * @uml.property   name="name"
 */
  public void setName(String name) {
    this.name = name;
  }

  /**
 * 取得元素的内容（字段名）
 * @return   String
 * @uml.property   name="content"
 */
  public String getContent() {
    return content;
  }

  /**
 * 设置元素的内容（字段名）
 * @param content   String
 * @uml.property   name="content"
 */
  public void setContent(String content) {
    this.content = content;
  }

  /**
   * 设置元素的内容水平对齐方式（左，中，右）
   *
   * @param content
   *          String
   */
  public void setAlign(String align) {
    if (align.equals("center")) {
      this.align = 2;
    } else if (align.equals("justify")) {
      this.align = 4;
    } else if (align.equals("left")) {
      this.align = 1;
    } else if (align.equals("right")) {
      this.align = 3;
    } else {
      this.align = 1;
    }
  }

  /**
 * 取字段值类型
 * @return   String 字段值类型
 * @uml.property   name="classType"
 */
  public String getClassType() {
    return classType;
  }

  /**
 * 设置字段值类型
 * @param classType   String 字段值类型
 * @uml.property   name="classType"
 */
  public void setClassType(String classType) {
    this.classType = classType;
  }

  /**
 * @return   Returns the print.
 * @uml.property   name="print"
 */
public boolean isPrint() {
	return print;
}

  /**
 * @param print   The print to set.
 * @uml.property   name="print"
 */
public void setPrint(boolean print) {
	this.print = print;
}

}
