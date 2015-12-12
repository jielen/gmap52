package com.anyi.gp.context;

import java.util.Properties;

import javax.servlet.ServletContext;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.interceptor.TransactionProxyFactoryBean;
import org.springframework.web.context.WebApplicationContext;

// 配置文件为：
// ..\..\..\..\..\defaultroot\WEB-INF\applicationContext.xml

public class ApplusContext {

  private static WebApplicationContext webApplicationContext = null;

  /** 静态类，不允许生成实例 */
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
   * 生成具有事务控制特性的业务类实例，调用该实例的所有方法都会启动事务，抛出任何异常都会回滚事务
   * @param cls 需要生成的业务类的类型
   * @return 具有事务控制特性的业务代理对象，类型为 cls (的子类)
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
      throw new RuntimeException("Error_1005: 找不到名为 " + "currentTransactionManager"
        + " 的事务管理器！");
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
