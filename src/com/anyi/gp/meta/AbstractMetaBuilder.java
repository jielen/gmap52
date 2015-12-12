package com.anyi.gp.meta;

import com.anyi.gp.core.dao.BaseDao;

public abstract class AbstractMetaBuilder {
	public BaseDao dao = null;
	
	public BaseDao getDao() {
		return dao;
	}

	public void setDao(BaseDao dao) {
		this.dao = dao;
	}

	public abstract Object generateMeta(String id, Object parameterObject);
	
}
