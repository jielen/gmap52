package com.anyi.gp.license;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;


/**
 * 
 * 当前加密狗对应的许可信息，用于硬件加密
 * 
 */
public class LicenseStatus {
  
  private static final Logger logger = Logger.getLogger(LicenseStatus.class);
  
  private String host;
  
  private int port;
  
  private String sn;

  private String companyCount;

  private String accountCount;

  private List allowedProducts = new ArrayList();

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public String getSn() {
    return sn;
  }

  public void setSn(String sn) {
    this.sn = sn;
  }

  public List getAllowedProducts() {
    return allowedProducts;
  }

  public void setAllowedProducts(List allowedProducts) {
    this.allowedProducts = allowedProducts;
  }

  public void addAllowedProduct(String product){
    this.allowedProducts.add(product);
  }

  public String getAccountCount() {
    return accountCount;
  }

  public void setAccountCount(String accountCount) {
    this.accountCount = accountCount;
  }

  public String getCompanyCount() {
    return companyCount;
  }

  public void setCompanyCount(String companyCount) {
    this.companyCount = companyCount;
  }
  
  public void setSn(){
    SocketCommunication communication = new SocketCommunication();
    try {
      communication.connect(getHost(), getPort(), "APP+");
    } catch (UnknownHostException e) {
      logger.error(e);
    } catch (IOException e) {
      logger.error(e);
    }
    this.sn = SocketCommunication.SN;
  }

  /**
   * 
   * 通过socket通讯设置购买产品
   * @param service
   * 
   */
  public void setAllowedProducts(LicenseService service) {
    List products = service.getAppNames();
    for (int i = 0, j = products.size(); i < j; i++) {
      String pid = (String) products.get(i);
      if (pid.equals("AS") || pid.equals("MA") || pid.equals("WF") 
        || pid.equals("admin") || pid.equals("style")) {
        continue;
      }
      SocketCommunication communication = new SocketCommunication();
      String tmpPid = null;
      if (pid.equalsIgnoreCase("BG") || pid.equalsIgnoreCase("GF")) {
        tmpPid = "GL+";
      } else {
        tmpPid = pid + "+";
      }
      try {
        communication.connect(getHost(), getPort(), tmpPid);
      } catch (UnknownHostException e) {
        logger.error(e);
        break;
      } catch (IOException e) {
        logger.error(e);
      }
      if (communication.getInternalCount() != 0) {
        addAllowedProduct(pid);
      }
    }
  }
  
  /**
   * 
   * 通过socket通讯设置单位数目
   *
   */
  public void setCompanyCount() {
    SocketCommunication communication = new SocketCommunication();
    try {
      communication.connect(getHost(), getPort(), "APP+");
    } catch (UnknownHostException e) {
      logger.error(e);
    } catch (IOException e) {
      logger.error(e);
    }
    setCompanyCount(communication.getInternalCount() + "");
  }
  
  /**
   * 
   * 通过socket通讯设置账套数目
   *
   */  
  public void setAccountCount() {
    SocketCommunication communication = new SocketCommunication();
    try {
      communication.connect(getHost(), getPort(), "APP2+");
    } catch (UnknownHostException e) {
      logger.error(e);
    } catch (IOException e) {
      logger.error(e);
    }
    setAccountCount(communication.getInternalCount() + "");
  }
  
  public String getLincenseStatusInfo(){
    StringBuffer info = new StringBuffer();
    if(host == null || host.length() == 0){
      info.append("加密服务未启用：无服务器地址!");
    }
    else{
      info.append("<p>已经启用加密服务。</p>\n <p>单位最大许可数量：");
      info.append(companyCount);
      info.append("</p>\n <p>账套最大许可数量：");
      info.append(accountCount);
      info.append("</p>\n<p>授权许可的产品：");
      if(allowedProducts != null){
        for(int i = 0; i < allowedProducts.size(); i++){
          if(i != 0)
            info.append(",");
          info.append(allowedProducts.get(i));
        }
      }
    }
    return info.toString();
  }
}
