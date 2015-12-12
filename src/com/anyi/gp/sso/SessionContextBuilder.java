package com.anyi.gp.sso;

import com.anyi.gp.domain.User;

public interface SessionContextBuilder {

  SessionContext getSessionContext(User user);
}
