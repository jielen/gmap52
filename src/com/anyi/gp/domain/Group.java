package com.anyi.gp.domain;

import java.io.Serializable;

public class Group implements Serializable {

	private static final long serialVersionUID = -8991329186158962048L;

	private String id = null;

	private String groupName = null;

	private String groupDesc;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getGroupDesc() {
		return groupDesc;
	}

	public void setGroupDesc(String groupDesc) {
		this.groupDesc = groupDesc;
	}

}
