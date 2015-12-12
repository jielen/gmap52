package com.anyi.gp.license;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 
 * 注册文件信息以及软加密信息
 * 所有的对象属性都是加密的
 * 
 */
public class License {
    
  private String sn;

  private String companyName;

  private String registeDate;

  private String address;

  private String postCode;

  private String linkMan;

  private String linkTel;

  private String agentName;

  private String expiredDate;

  private String key;
  
  private String companyCount;

  private String accountCount;

  private String allowedProducts;
 
  public String getAccountCount() {
    return accountCount;
  }

  public String getAddress() {
    return address;
  }

  public String getAgentName() {
    return agentName;
  }

  public String getCompanyCount() {
    return companyCount;
  }

  public String getCompanyName() {
    return companyName;
  }

  public String getExpiredDate() {
    return expiredDate;
  }

  public String getLinkMan() {
    return linkMan;
  }

  public String getLinkTel() {
    return linkTel;
  }

  public String getPostCode() {
    return postCode;
  }

  public String getRegisteDate() {
    return registeDate;
  }

  public String getSn() {
    return sn;
  }

  public String getDecodeAddress() {
    if(!isLicenseValidity()) return null;
    return RegisterTools.decodeString(address);
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getDecodeAgentName() {
    if(!isLicenseValidity()) return null;
    return RegisterTools.decodeString(agentName);
  }

  public void setAgentName(String agentName) {
    this.agentName = agentName;
  }

  public String getDecodeCompanyName() {
    if(!isLicenseValidity()) return null;
    return RegisterTools.decodeString(companyName);
  }

  public void setCompanyName(String companyName) {
    this.companyName = companyName;
  }

  public String getDecodeLinkMan() {
    if(!isLicenseValidity()) return null;
    return RegisterTools.decodeString(linkMan);
  }

  public void setLinkMan(String linkMan) {
    this.linkMan = linkMan;
  }

  public String getDecodeLinkTel() {
    if(!isLicenseValidity()) return null;
    return RegisterTools.decodeString(linkTel);
  }

  public void setLinkTel(String linkTel) {
    this.linkTel = linkTel;
  }

  public String getDecodePostCode() {
    if(!isLicenseValidity()) return null;
    return RegisterTools.decodeString(postCode);
  }

  public void setPostCode(String postCode) {
    this.postCode = postCode;
  }

  public String getDecodeSn() {
    if(!isLicenseValidity()) return null;
    return RegisterTools.decodeString(sn);
  }

  public void setSn(String sn) {
    this.sn = sn;
  }

  public List getDecodeAllowedProducts() {
    if(!isLicenseValidity()) return null;
    List result = new ArrayList();
    if(allowedProducts != null){
    	String products = RegisterTools.decodeString(allowedProducts);
    	String[] array = products.split(",");
    	for (int i = 0; i < array.length; i++) {
    		result.add(array[i]);
    	}
    }
    return result;
  }

  public void setAllowedProducts(String products){
	  this.allowedProducts = products;
  }
  
  public String getAllowedProducts() {
	  return allowedProducts;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public int getDecodeAccountCount() {
    if(!isLicenseValidity()) return -1;
    return Integer.parseInt(RegisterTools.decodeString(accountCount));
  }

  public void setAccountCount(String accountCount) {
    this.accountCount = accountCount;
  }

  public int getDecodeCompanyCount() {
    if(!isLicenseValidity()) return -1;
    return Integer.parseInt(RegisterTools.decodeString(companyCount));
  }

  public void setCompanyCount(String companyCount) {
    this.companyCount = companyCount;
  }

  public Date getDecodeExpiredDate() {
    boolean isValidity = isLicenseValidity();
    if(isValidity){
      String expiredString = RegisterTools.decodeString(expiredDate);
      if(expiredString != null && expiredString.length() > 0){
        Date tExpiredDate = null;
        try{
          tExpiredDate = Date.valueOf(expiredString.substring(0, 10));
        }catch(Exception e){
          return null;
        }
        return tExpiredDate;
      }
    }
    return null;
  }

  public void setExpiredDate(String expiredDate) {
    this.expiredDate = expiredDate;
  }

  public Date getDecodeRegisteDate() {
    boolean isValidity = isLicenseValidity();
    if(isValidity){
      String expiredString = RegisterTools.decodeString(registeDate);
      if(expiredString != null && expiredString.length() > 0){
        Date tRegisteDate = null;
        try{
          tRegisteDate = Date.valueOf(expiredString.substring(0, 10));
        }catch(Exception e){
          return null;
        }
        return tRegisteDate;
      }
    }
    return null;    
  }

  public void setRegisteDate(String registeDate) {
    this.registeDate = registeDate;
  }
  
  public int getLeftDays(){
    boolean isValidity = isLicenseValidity();
    if(isValidity){
      Date expiredDate = getDecodeExpiredDate();
      if(expiredDate != null){
        Date today = new Date(System.currentTimeMillis());
        if(expiredDate.getTime() - today.getTime() < 0){
          return -1;
        }
        return (int)((expiredDate.getTime() - today.getTime()) / 1000 / 60 / 60 / 24);
      }
    }
    return -1;
  }
  
  /**
   * 检查license是否有效
   * @param license
   * @return
   */
  public boolean isLicenseValidity(){
    String keyString = RegisterTools.getKeyStringFromDB();
    if(keyString != null && keyString.equals(key))
      return true;
    
    return false;
  }
}
