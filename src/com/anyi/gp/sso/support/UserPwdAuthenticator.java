package com.anyi.gp.sso.support;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;

import com.anyi.gp.core.dao.BaseDao;
import com.anyi.gp.domain.User;
import com.anyi.gp.pub.GeneralFunc;
import com.anyi.gp.sso.AuthenticationFailedException;
import com.anyi.gp.sso.IdentityAuthenticator;
import com.anyi.gp.sso.IpIncorrectException;
import com.anyi.gp.sso.NoSuchUserException;

public class UserPwdAuthenticator implements IdentityAuthenticator {
  
  private BaseDao dao;
  
  public BaseDao getDao() {
    return dao;
  }

  public void setDao(BaseDao dao) {
    this.dao = dao;
  }

  public User getLoginUser(HttpServletRequest request) throws NoSuchUserException,
    AuthenticationFailedException, IpIncorrectException {
    
    String userId = request.getParameter("username");
//	try {
//		userId = new String(userId.getBytes("ISO-8859-1"), "GBK");
//	} catch (UnsupportedEncodingException e) {
//			// TCJLODO Auto-generated catch block
//		e.printStackTrace();
//	}
    
    /*
    String remoteAddr = request.getRemoteAddr();
    boolean ipValidity = GeneralFunc.checkIPValidity(remoteAddr, userId);
    
    if(!ipValidity)
      throw new IpIncorrectException();
    */
    User loginUser = GeneralFunc.getLoginUser(userId);    
    if (loginUser == null) {
      throw new NoSuchUserException();
    }
    
    String encodedPwd = GeneralFunc.encodePwd(request.getParameter("password"));
    if (encodedPwd.equals(loginUser.getPassword())) {
      String dsKey = request.getParameter("dsKey");
      if(dsKey != null && dsKey.length() > 0){
        loginUser.setDsKey(dsKey);
      }
      return loginUser;
    }
    
    throw new AuthenticationFailedException();
  }

}
