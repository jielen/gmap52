package com.anyi.gp.domain;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable{
  
  private static final long serialVersionUID = 1441025983434072642L;

  private String userCode = null;
  
  private String userName = null;

  private String password = null;
  
  private List groupList = null;
  
  private String dsKey = null;
  
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((userCode == null) ? 0 : userCode.hashCode());
    return result;
  }

  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    final User other = (User) obj;
    if (userCode == null) {
      if (other.userCode != null)
        return false;
    } else if (!userCode.equals(other.userCode))
      return false;
    return true;
  }

  public String getUserCode() {
    return userCode;
  }

  public void setUserCode(String userCode) {
    this.userCode = userCode;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public List getGroupList() {
    return groupList;
  }

  public void setGroupList(List groupList) {
    this.groupList = groupList;
  }

  public String getDsKey() {
    return dsKey;
  }

  public void setDsKey(String dsKey) {
    this.dsKey = dsKey;
  }

}
