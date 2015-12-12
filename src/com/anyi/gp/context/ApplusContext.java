package com.anyi.gp.context;

import java.util.Properties;

import javax.servlet.ServletContext;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.interceptor.TransactionProxyFactoryBean;
import org.springframework.web.context.WebApplicationContext;

// �����ļ�Ϊ��
// ..\..\..\..\..\defaultroot\WEB-INF\applicationContext.xml

public class ApplusContext {

  private static WebApplicationContext webApplicationContext = null;

  /** ��̬�࣬����������ʵ�� */
  private ApplusContext() {
  }

  public static EnvironmentConfig getEnvironmentConfig() {
    return (EnvironmentConfig) webApplicationContext.getBean("environmentConfig");
  }

  public static ServletContext getServletContext() {
    return getWebApplicationContext().getServletContext();
  }

  public static WebApplicationContext getWebApplicationContext() {
    return webApplicationContext;
  }

  public static void setWebApplicationContext(
    WebApplicationContext webApplicationContext) {
    ApplusContext.webApplicationContext = webApplicationContext;
  }

  public static Object getBean(String beanName) {
    if(webApplicationContext == null) return null;
    return webApplicationContext.getBean(beanName);
  }

  /**
   * ���ɾ�������������Ե�ҵ����ʵ�������ø�ʵ�������з����������������׳��κ��쳣����ع�����
   * @param cls ��Ҫ���ɵ�ҵ���������
   * @return ��������������Ե�ҵ������������Ϊ cls (������)
   */
  public static Object getTransactionBean(Class cls) {
    Object target = null;
    try {
      target = cls.newInstance();
    } catch (InstantiationException e) {
      throw new RuntimeException(e);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
    PlatformTransactionManager tran = null;
    try {
      tran = (PlatformTransactionManager) getBean("currentTransactionManager");
    } catch (RuntimeException e) {
      // do nothing
    }
    if (null == tran) {
      throw new RuntimeException("Error_1005: �Ҳ�����Ϊ " + "currentTransactionManager"
        + " �������������");
    }
    Properties properties = new Properties();
    properties.setProperty("*", "PROPAGATION_REQUIRED,-Exception");

    TransactionProxyFactoryBean proxyFactory = new TransactionProxyFactoryBean();
    proxyFactory.setTarget(target);
    proxyFactory.setTransactionManager(tran);
    proxyFactory.setTransactionAttributes(properties);
    proxyFactory.afterPropertiesSet(); //_vip

    return proxyFactory.getObject();
  }
}
