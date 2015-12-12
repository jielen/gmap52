
package com.anyi.gp;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * 通过AppServer类来初始化环境值
 * 
 */
public class AppServer {

  private static Context getInitialContext() throws NamingException {

    try {
      return new InitialContext();
    } catch (RuntimeException e) {
      throw e;
    }
  }

  public static Object ContextJndiObject(String jndiName)
      throws NamingException {
    Object ref = null;
    Context ctx = null;
    try {
      ctx = getInitialContext();
      ref = ctx.lookup(jndiName);
      return ref;
    } finally {
      if (ctx != null) {
        ctx.close();
      }
    }
  }
}
