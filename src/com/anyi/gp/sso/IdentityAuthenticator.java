package com.anyi.gp.sso;

import javax.servlet.http.HttpServletRequest;

import com.anyi.gp.domain.User;

public interface IdentityAuthenticator {

  /**
   * 根据请求，验证是否是合法用户。若是，返回对应的User,否则返回null.
   * @return 登录用户对应的User对象
   */
  User getLoginUser(HttpServletRequest request)
  throws NoSuchUserException ,AuthenticationFailedException, IpIncorrectException;
  
}
