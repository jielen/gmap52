package com.anyi.gp.print.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * 对象拷贝类
 * <p>
 * Title: ObjectClone
 * </p>
 * <p>
 * Description: 实现对象的深度拷贝，目前应用在模板分割时design对象的拷贝
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company: UFGOV
 * </p>
 * 
 * @author zhanyw
 * @version 1.0
 */
public class ObjectClone {
  public ObjectClone() {
  }

  public Object clone(Object obj) {
    try {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      ObjectOutputStream oout = new ObjectOutputStream(out);
      oout.writeObject(obj);

      ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(out
          .toByteArray()));
      return in.readObject();
    } catch (Exception e) {
      throw new RuntimeException("cannot clone class ["
          + obj.getClass().getName() + "] via serialization: " + e.toString());
    }
  }

}
