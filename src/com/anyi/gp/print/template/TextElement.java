/* $Id: TextElement.java,v 1.1 2008/02/22 09:09:36 liuxiaoyong Exp $*/
package com.anyi.gp.print.template;

/**
 * <p>
 * Title: ʵ��ITextElementInfor�ӿ�
 * </p>
 * <p>
 * Description: ҳ��Ԫ����ϸ��Ϣ����
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
   * ����XML�ڵ㹹��һ��ҳ��Ԫ����Ϣ
   *
   * @param node
   *          Node ��ǰ�˽ű�������XML����
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
 * ȡ��Ԫ�ص�X����
 * @return   int X����ֵ
 * @uml.property   name="x"
 */
  public int getX() {
    return x;
  }

  /**
 * ȡ��Ԫ�ص�align ����ֵ public static final byte TEXT_ALIGN_CENTER 2 public static final byte TEXT_ALIGN_JUSTIFIED 4 public static final byte TEXT_ALIGN_LEFT 1 public static final byte TEXT_ALIGN_RIGHT 3
 * @return   byte ����ֵ
 * @uml.property   name="align"
 */
  public byte getAlign() {
    return align;
  }

  /**
 * ����X����ֵ
 * @param x   int ����
 * @uml.property   name="x"
 */
  public void setX(int x) {
    this.x = x;
  }

  /**
 * ȡ��Ԫ��Y����
 * @return   int
 * @uml.property   name="y"
 */
  public int getY() {
    return y;
  }

  /**
 * ����Ԫ��Y����
 * @param y   int Y����ֵ
 * @uml.property   name="y"
 */
  public void setY(int y) {
    this.y = y;
  }

  /**
 * ȡ��Ԫ�صĿ��
 * @return   int ԭ�ؿ�
 * @uml.property   name="width"
 */
  public int getWidth() {
    return width;
  }

  /**
 * ����Ԫ�ؿ��ֵ
 * @param width   int ���ֵ
 * @uml.property   name="width"
 */
  public void setWidth(int width) {
    this.width = width;
  }

  /**
 * ȡ��Ԫ�ظ߶�ֵ
 * @return   int �߶�ֵ
 * @uml.property   name="height"
 */
  public int getHeight() {
    return height;
  }

  /**
 * ����Ԫ�صĸ߶�
 * @param height   int �߶�ֵ
 * @uml.property   name="height"
 */
  public void setHeight(int height) {
    this.height = height;
  }

  /**
 * ���Ԫ����
 * @return   String Ԫ������
 * @uml.property   name="name"
 */
  public String getName() {
    return name;
  }

  /**
 * ����Ԫ������
 * @param name   String ����
 * @uml.property   name="name"
 */
  public void setName(String name) {
    this.name = name;
  }

  /**
 * ȡ��Ԫ�ص����ݣ��ֶ�����
 * @return   String
 * @uml.property   name="content"
 */
  public String getContent() {
    return content;
  }

  /**
 * ����Ԫ�ص����ݣ��ֶ�����
 * @param content   String
 * @uml.property   name="content"
 */
  public void setContent(String content) {
    this.content = content;
  }

  /**
   * ����Ԫ�ص�����ˮƽ���뷽ʽ�����У��ң�
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
 * ȡ�ֶ�ֵ����
 * @return   String �ֶ�ֵ����
 * @uml.property   name="classType"
 */
  public String getClassType() {
    return classType;
  }

  /**
 * �����ֶ�ֵ����
 * @param classType   String �ֶ�ֵ����
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
