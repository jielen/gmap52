/*
 * Created on 2005-9-15
 *
 * copyright:ufgov
 */
package com.anyi.gp.taglib;

/**
 * @author   leidaohong
 * @date :2005-9-15
 * @description :
 */
public interface IVisibleTag extends ITag {
  public String make();
  public String makeJS();
  /**
 * @return
 * @uml.property   name="id"
 */
public String getId();
  /**
 * @param id
 * @uml.property   name="id"
 */
public void setId(String id);
  /**
 * @return
 * @uml.property   name="isvisible"
 */
public boolean isIsvisible();
  /**
 * @param isvisible
 * @uml.property   name="isvisible"
 */
public void setIsvisible(boolean isvisible);
}
