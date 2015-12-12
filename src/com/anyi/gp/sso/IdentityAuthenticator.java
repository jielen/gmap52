package com.anyi.gp.sso;

import javax.servlet.http.HttpServletRequest;

import com.anyi.gp.domain.User;

public interface IdentityAuthenticator {

  /**
   * ����������֤�Ƿ��ǺϷ��û������ǣ����ض�Ӧ��User,���򷵻�null.
   * @return ��¼�û���Ӧ��User����
   */
  User getLoginUser(HttpServletRequest request)
  throws NoSuchUserException ,AuthenticationFailedException, IpIncorrectException;
  
}
