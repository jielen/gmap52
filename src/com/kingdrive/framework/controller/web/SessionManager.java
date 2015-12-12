package com.kingdrive.framework.controller.web;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import com.kingdrive.framework.controller.web.util.WebKeys;

public class SessionManager implements Serializable {

  private Hashtable data[];

  private boolean isValid;

  public static final int SDS = 0;

  public static final int TDS = 1;

  public static final int PDS = 2;

  public static final int GDS = 3;

  private HttpSession session;

  private SessionManager(HttpSession session) {
    this.isValid = true;
    this.session = session;
    init();
    session.setAttribute(WebKeys.SESSION_MANAGER, this);
  }

  public static SessionManager getSessionManager(HttpSession session) {
    SessionManager instance = null;
    try {
      instance = (SessionManager) session.getAttribute(WebKeys.SESSION_MANAGER);
      if (instance == null) {
        instance = new SessionManager(session);
      }
    } catch (Exception e) {
      instance = null;
    }
    return instance;
  }

  protected void init() {
    data = new Hashtable[3];
    for (int i = 0; i < 3; i++)
      data[i] = new Hashtable();

  }

  public Object put(int storage, String key, Object value) {
    if (storage == 3) {
      ServletContext ctx = session.getServletContext();
      Object ret;
      synchronized (ctx) {
        ret = ctx.getAttribute(key);
        ctx.setAttribute(key, value);
      }
      return ret;
    }
    Object ret = data[storage].put(key, value);
    session.setAttribute(WebKeys.SESSION_MANAGER, this);
    return ret;
  }

  public Object get(int storage, String key) {
    if (storage == 3) {
      ServletContext ctx = session.getServletContext();
      Object ret;
      synchronized (ctx) {
        ret = ctx.getAttribute(key);
      }
      return ret;
    }
    return data[storage].get(key);
  }

  public Enumeration keys(int storage) {
    if (storage == 3) {
      ServletContext ctx = session.getServletContext();
      Enumeration ret;
      synchronized (ctx) {
        ret = ctx.getAttributeNames();
      }
      return ret;
    }
    return data[storage].keys();
  }

  public Object remove(int storage, String key) {
    if (storage == 3) {
      ServletContext ctx = session.getServletContext();
      Object ret;
      synchronized (ctx) {
        ret = ctx.getAttribute(key);
        ctx.removeAttribute(key);
      }
      return ret;
    }
    Object ret = data[storage].remove(key);
    session.setAttribute(WebKeys.SESSION_MANAGER, this);
    return ret;
  }

  public void clear(int storage) {
    if (storage == 3) {
      ServletContext ctx = session.getServletContext();
      synchronized (ctx) {
        Enumeration e = ctx.getAttributeNames();
        for (; e.hasMoreElements();) {
          ctx.removeAttribute((String) e.nextElement());
        }
      }
      return;
    }
    data[storage].clear();
    session.setAttribute(WebKeys.SESSION_MANAGER, this);
    return;
  }

  public int size(int storage) {
    if (storage == 3)
      throw new UnsupportedOperationException();
    return data[storage].size();
  }

  public void invalidate() {
    isValid = false;
  }

  public boolean isValid() {
    return isValid;
  }

  public String getContent(int storage) {
    if (storage == 3)
      throw new UnsupportedOperationException();

    StringBuffer result = new StringBuffer("");
    Enumeration names = keys(storage);
    for (; names.hasMoreElements();) {
      String name = (String) names.nextElement();
      Object value = get(storage, name);
      if (value == null) {
        value = "null";
      }
      result.append(name).append(" = ").append(value.toString()).append(";\n");
    }

    return result.toString();
  }
}
