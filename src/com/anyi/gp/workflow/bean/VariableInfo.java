package com.anyi.gp.workflow.bean;

import java.io.Serializable;


/**
 * @author   leidaohong
 */
public class VariableInfo extends AbstractWFEntity {
  private String name; // /变量的名称

  private int id; // /变量的id号

  private String type; // /变量类型

  private Serializable value; // /变量的值

  private boolean inMandatory;

  private boolean in;

  private boolean out;

  /** 根据用户挂接模板时,定义的绑定条件,它也是一个返回唯一值得sql语句 */
  private String bind_expression;

  private String tab_id;

  private boolean is_filter_by_entitykey = true;/* 不需要业务主键过滤 */

  private String condition;/* 挂接时定义的过滤条件 */

  public static final String TYPE_STRING = "string";

  public static final String TYPE_INT = "int";

  public static final String TYPE_DOUBLE = "double";

  public static final String TYPE_DATE = "date";

  public static final String TYPE_BOOL = "boolean";

  public static final String ALL_TYPES[] = { "string", "integer", "double",
      "date", "boolean" };

  public static final String DEFAULT_STRING = "";

  public static final Long DEFAULT_LONG = new Long(0L);

  public static final Double DEFAULT_DOUBLE = new Double(0.0D);

  public static final Integer DEFAULT_INT = new Integer(0);

  public static final Boolean DEFAULT_BOOL = new Boolean(true);

  /**
 * @return   Returns the id.
 * @uml.property   name="id"
 */
  public int getId() {
    return id;
  }

  /**
 * @param id   The id to set.
 * @uml.property   name="id"
 */
  public void setId(int id) {
    this.id = id;
  }

  public VariableInfo() {

  }

  public VariableInfo(String name, String type, Serializable value) {
    this.name = name;
    this.type = type;
    this.value = value;
  }

  public VariableInfo(String name, String type, Serializable value,
      boolean flag, boolean flag1, boolean flag2) {
    this.name = name;
    this.type = type;
    this.value = value;
    this.inMandatory = flag;
    this.in = flag1;
    this.out = flag2;
  }

  /**
 * @return   Returns the type.
 * @uml.property   name="type"
 */
public String getType() {
	return this.type;
}

  /**
 * @param type   The type to set.
 * @uml.property   name="type"
 */
public void setType(String aType) {
	this.type = aType;
}

  /**
 * @return   Returns the value.
 * @uml.property   name="value"
 */
public Serializable getValue() {
	return this.value;
}

  /**
 * @param value   The value to set.
 * @uml.property   name="value"
 */
public void setValue(Serializable value) {
	this.value = value;
}

  /**
 * @return   Returns the name.
 * @uml.property   name="name"
 */
public String getName() {
	return this.name;
}

  /**
 * @param name   The name to set.
 * @uml.property   name="name"
 */
public void setName(String name) throws IllegalArgumentException {
	this.name = name;
}

  /**
 * @param inMandatory   The inMandatory to set.
 * @uml.property   name="inMandatory"
 */
public void setInMandatory(boolean flag) {
	this.inMandatory = flag;
}

  /**
 * @return   Returns the inMandatory.
 * @uml.property   name="inMandatory"
 */
public boolean getInMandatory() {
	return this.inMandatory;
}

  /**
 * @param in   The in to set.
 * @uml.property   name="in"
 */
public void setIn(boolean flag) {
	this.in = flag;
}

  /**
 * @return   Returns the in.
 * @uml.property   name="in"
 */
public boolean getIn() {
	return this.in;
}

  /**
 * @param out   The out to set.
 * @uml.property   name="out"
 */
public void setOut(boolean flag) {
	this.out = flag;
}

  /**
 * @return   Returns the out.
 * @uml.property   name="out"
 */
public boolean getOut() {
	return this.out;
}

  /**
 * @return   Returns the bind_expression.
 * @uml.property   name="bind_expression"
 */
  public String getBind_expression() {
    return bind_expression;
  }

  /**
 * @param bind_expression   The bind_expression to set.
 * @uml.property   name="bind_expression"
 */
  public void setBind_expression(String bind_expression) {
    this.bind_expression = bind_expression;
  }

  /**
 * @return   Returns the is_filter_by_entitykey.
 * @uml.property   name="is_filter_by_entitykey"
 */
  public boolean isIs_filter_by_entitykey() {
    return is_filter_by_entitykey;
  }

  /**
 * @param is_filter_by_entitykey   The is_filter_by_entitykey to set.
 * @uml.property   name="is_filter_by_entitykey"
 */
  public void setIs_filter_by_entitykey(boolean is_filter_by_entitykey) {
    this.is_filter_by_entitykey = is_filter_by_entitykey;
  }

  /**
 * @return   Returns the condition.
 * @uml.property   name="condition"
 */
  public String getCondition() {
    return condition;
  }

  /**
 * @param condition   The condition to set.
 * @uml.property   name="condition"
 */
  public void setCondition(String condition) {
    this.condition = condition;
  }

  /**
 * @return   Returns the tab_id.
 * @uml.property   name="tab_id"
 */
  public String getTab_id() {
    return tab_id;
  }

  /**
 * @param tab_id   The tab_id to set.
 * @uml.property   name="tab_id"
 */
  public void setTab_id(String tab_id) {
    this.tab_id = tab_id;
  }
}
